package com.libtop.weitu.activity.search;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.search.dto.CommentNeedDto;
import com.libtop.weitu.activity.search.dto.SearchResult;
import com.libtop.weitu.activity.source.Bean.MediaAlbumBean;
import com.libtop.weitu.activity.source.Bean.MediaListItemBean;
import com.libtop.weitu.activity.source.Bean.MediaResultBean;
import com.libtop.weitu.activity.source.RecyclerSingleChoiseAdapter;
import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.eventbus.MessageEvent;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.http.MapUtil;
import com.libtop.weitu.http.WeituNetwork;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.CheckUtil;
import com.libtop.weitu.utils.ContantsUtil;
import com.libtop.weitu.utils.DisplayUtils;
import com.libtop.weitu.utils.NetworkUtil;
import com.libtop.weitu.utils.ShareSdkUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;
import okhttp3.Call;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by Administrator on 2016/2/3 0003.
 */
public class VideoPlayActivity2 extends BaseActivity implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, AdapterView.OnItemClickListener, RecyclerSingleChoiseAdapter.OnItemClickLister
{
    public static String MEDIA_PATH = "vedio_path";
    public static String MEDIA_NAME = "vedio_name";

    private static final int STATUS_FULLSCREEN = 0;
    private static final int STATUS_SCALE = 1;

    @Bind(R.id.title)
    TextView mTitleText;
    @Bind(R.id.title_inner)
    TextView mInTitleText;

    @Bind(R.id.title_container)
    LinearLayout mTitleCon;

    @Bind(R.id.video_view)
    VideoView mVideo;
    @Bind(R.id.seekbar_small)
    SeekBar mSmallSeek;
    @Bind(R.id.seekbar_big)
    SeekBar mBigSeek;
    @Bind(R.id.progress_current)
    TextView mCurrentPro;
    @Bind(R.id.progress_total)
    TextView mTotalPro;
    @Bind(R.id.tv_play_time)
    TextView tvTag;

    @Bind(R.id.progress_big)
    TextView mBigPro;

    @Bind(R.id.play_pause_small)
    ImageButton mSPlayBtn;
    @Bind(R.id.play_pause_big)
    ImageButton mBPlayBtn;

    @Bind(R.id.media_bottom_full)
    LinearLayout mFBottomView;
    @Bind(R.id.media_bottom_scale)
    LinearLayout mSBottomView;
    @Bind(R.id.media_top)
    LinearLayout mTopView;

    @Bind(R.id.container)
    RelativeLayout mVideoSu;

    @Bind(R.id.rl_pdf_bottom)
    RelativeLayout rlPdfBottom;

    @Bind(R.id.ll_bottom)
    LinearLayout llBottom;


    @Bind(R.id.scl_horizontal)
    RecyclerView mRecyclerView;

    @Bind(R.id.introduction)
    TextView mIntroText;

    @Bind(R.id.img_collect)
    ImageView imgCollect;

    @Bind(R.id.back_btn)
    ImageView backBtn;

    @Bind(R.id.tv_publisher)
    TextView tvPublisher;


    private Animation mBHideAn, mBShowAn, mTHideAn, mTShowAn;
    private boolean isPaused = false;
    private boolean thread = true;

    private boolean isShowing = false;

    private int playingIndex = 0;

    private int status_flag;

    private List<MediaListItemBean> mRes = new ArrayList<MediaListItemBean>();

    /**
     * Seekbar总长度
     */
    private int MaxLength = 1000;

    private int TouchLength;

    private boolean isTouch = false;
    private boolean notShowButtom = false;

    private List<MediaListItemBean> mData = new ArrayList<MediaListItemBean>();

    private SearchResult searchResult;
    private RecyclerSingleChoiseAdapter mRecyclerAdapter;
    private List<String> lists = new ArrayList<String>();
    private MediaAlbumBean mediaAlbumBean;
    private boolean isCollectShow;
    private String titleName = "";

    private boolean isOpen = true;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.e("test draw time start", System.currentTimeMillis() + "");
        super.onCreate(savedInstanceState);
        setInjectContentView(R.layout.activity_video_play5);
        noNetThanExit(mContext);
        mRecyclerAdapter = new RecyclerSingleChoiseAdapter(mContext, lists, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);//这里用线性显示 类似于listview
        mRecyclerView.setAdapter(mRecyclerAdapter);
        String result = getIntent().getExtras().getString("resultBean");
        searchResult = new Gson().fromJson(result, SearchResult.class);
        loadIndex(0);
    }


    private void setOrientation(int flag)
    {
        status_flag = flag;
        if (status_flag == STATUS_SCALE)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }


    private void init(int position)
    {
        setupAnimate();
        Vitamio.isInitialized(getApplicationContext());
        playingIndex = getIntent().getExtras().getInt("index");

        String videoPath = "http://www.null.mp4";
        String name = "";
        if (mData.size() > 0)
        {
            if (!TextUtils.isEmpty(mData.get(position).url))
            {
                videoPath = mData.get(position).url;
            }
            name = mediaAlbumBean.title;
            titleName = name;
        }

        notShowButtom = getIntent().getExtras().getBoolean("notShowButtom");
        mTitleText.setText(name);
        mInTitleText.setText(name);
        tvPublisher.setText(mediaAlbumBean.uploadUsername);
        mBPlayBtn.setClickable(false);
        mBPlayBtn.setImageResource(R.drawable.media_icon_play_big);

        mSPlayBtn.setClickable(false);
        mSPlayBtn.setImageResource(R.drawable.media_icon_play_small);

        mVideo.setVideoURI(Uri.parse(videoPath));

        setWindowScreen();

        mVideo.setOnCompletionListener(this);
        mVideo.setOnPreparedListener(this);
        mVideo.setOnErrorListener(this);

        initInfo();


        new CustomThread().start();
        mSmallSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if (isTouch)
                {
                    TouchLength = progress;
                }
                if (!fromUser)
                {
                    return;
                }
                long newposition = (mVideo.getDuration() * progress) / 1000L;
                mVideo.seekTo(newposition);
                isPaused = false;
                mSPlayBtn.setImageResource(R.drawable.media_icon_pause_small);
                mBPlayBtn.setImageResource(R.drawable.media_icon_pause_big);
            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
                isTouch = true;
            }


            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                isTouch = false;
                setmProgress(TouchLength);
            }
        });
        mBigSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if (isTouch)
                {
                    setmProgress(progress);
                }
                if (!fromUser)
                {
                    return;
                }
                long newposition = (mVideo.getDuration() * progress) / 1000L;
                mVideo.seekTo(newposition);
                isPaused = false;
                mSPlayBtn.setImageResource(R.drawable.media_icon_pause_small);
                mBPlayBtn.setImageResource(R.drawable.media_icon_pause_big);
            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {
                isTouch = true;
            }


            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                isTouch = false;
                setmProgress(TouchLength);
            }
        });
    }


    /**
     * 设置视频进度
     *
     * @param lenth
     */
    private void setmProgress(int lenth)
    {
        long value = (long) (mVideo.getDuration() / MaxLength * lenth);
        mVideo.seekTo(value);

    }


    /**
     * 滑动改变视频进度
     *
     * @param lenth
     */
    private void touchProgress(int lenth)
    {
        long go = 0;
        lenth = lenth * 100;
        long duration = mVideo.getDuration();
        long position = mVideo.getCurrentPosition();
        if (lenth > 0)
        {
            go = position + (long) lenth;

            mVideo.seekTo(go);
        }
        else
        {
            go = position + (long) lenth;

            mVideo.seekTo(go);

        }

    }


    private void setupAnimate()
    {
        mBHideAn = AnimationUtils.loadAnimation(mContext, R.anim.push_bottom_out);
        mBShowAn = AnimationUtils.loadAnimation(mContext, R.anim.push_bottom_in);

        //top
        mTHideAn = AnimationUtils.loadAnimation(mContext, R.anim.push_top_out);
        mTShowAn = AnimationUtils.loadAnimation(mContext, R.anim.push_top_in);

    }


    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 1:
                    long current = mVideo.getCurrentPosition();
                    long total = mVideo.getDuration();
                    long position = 1000L * current / total;
                    long twoMin = 1000L * 60 * 2;
                    if ((current > twoMin) && !isOpen)
                    {
                        if (mVideo.isPlaying())
                        {
                            mVideo.stopPlayback();
                            return;
                        }
                    }
                    if (status_flag == STATUS_SCALE)
                    {
                        mSmallSeek.setProgress((int) position);
                        mCurrentPro.setText(getPlayProgress(current));
                        mTotalPro.setText(getPlayProgress(total));
                        mSmallSeek.setSecondaryProgress(mVideo.getBufferPercentage() * 10);
                    }
                    else
                    {
                        mBigSeek.setProgress((int) position);
                        mBigPro.setText(getPlayProgress(current) + "/" + getPlayProgress(total));
                        mBigSeek.setSecondaryProgress(mVideo.getBufferPercentage() * 10);
                    }
                    break;
            }
        }
    };


    /**
     * 设置显示进度
     *
     * @param position
     */
    private String getPlayProgress(long position)
    {
        int value = (int) position / MaxLength;
        int minute = value / 60;
        int second = value % 60;
        minute %= 60;
        return String.format("%02d:%02d", minute, second);
    }


    /**
     * 播放暂停
     */
    private void playOrPause()
    {
        if (!mVideo.isPlaying())
        {
            mVideo.start();
            mSPlayBtn.setImageResource(R.drawable.media_icon_pause_small);
            mBPlayBtn.setImageResource(R.drawable.media_icon_pause_big);

        }
        else
        {
            mVideo.pause();
            mSPlayBtn.setImageResource(R.drawable.media_icon_play_small);
            mBPlayBtn.setImageResource(R.drawable.media_icon_play_big);
        }
        isPaused = !isPaused;
    }


    private void show()
    {
        if (!isShowing)
        {
            mFBottomView.setVisibility(View.VISIBLE);
            mTopView.setVisibility(View.VISIBLE);
            if (mFBottomView != null && mBShowAn != null)
            {
                mFBottomView.startAnimation(mBShowAn);
            }
            if (mTopView != null && mTShowAn != null)
            {
                mTopView.startAnimation(mTShowAn);
            }
            isShowing = true;
            mHandler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    hide();
                }
            }, 5000);
        }

    }


    private void hide()
    {
        if (isShowing)
        {
            mFBottomView.startAnimation(mBHideAn);
            mFBottomView.setVisibility(View.GONE);
            mTopView.startAnimation(mTHideAn);
            mTopView.setVisibility(View.GONE);
            isShowing = false;
        }
    }


    @Nullable
    @OnClick({R.id.img_collect, R.id.img_comment, R.id.img_share, R.id.back_btn, R.id.back_btn_inner, R.id.play_pause_big, R.id.play_pause_small, R.id.video_container, R.id.fullscreen, R.id.scale})
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.img_collect:
                collectClick();
                break;
            case R.id.img_comment:
                commentClick();
                break;
            case R.id.img_share:
                shareClick();
                break;
            case R.id.back_btn:
                finishSimple();
                break;
            case R.id.back_btn_inner:
                setWindowScreen();
                break;
            case R.id.play_pause_small:
            case R.id.play_pause_big:
                playOrPause();
                break;
            case R.id.video_container:
                if (status_flag == STATUS_FULLSCREEN)
                {
                    show();
                }
                break;
            case R.id.fullscreen:
                status_flag = STATUS_FULLSCREEN;
                setFullScreen();
                break;
            case R.id.scale:
                status_flag = STATUS_SCALE;
                setWindowScreen();
                break;
        }
    }


    @Override
    public void onBackPressed()
    {
        if (status_flag == STATUS_FULLSCREEN)
        {
            setWindowScreen();
        }
        else
        {
            finishSimple();
        }
    }


    private void shareClick()
    {
        String title = "微图分享";
        String content = "“【视频】" + titleName + "”" + ContantsUtil.shareContent;
        String imageUrl = "drawable://" + R.drawable.wbshare;
        ShareSdkUtil.showShareWithLocalImg(mContext, title, content, imageUrl);
    }


    //视频的type为2
    private void commentClick()
    {
        Intent intent = new Intent(mContext, CommentActivity.class);
        CommentNeedDto commentNeedDto = new CommentNeedDto();
        commentNeedDto.title = mediaAlbumBean.title;
        commentNeedDto.author = mediaAlbumBean.artist;
        commentNeedDto.publisher = mediaAlbumBean.uploadUsername;
        commentNeedDto.photoAddress = mediaAlbumBean.cover;
        commentNeedDto.tid = searchResult.id;
        commentNeedDto.type = 2;
        intent.putExtra("CommentNeedDto", new Gson().toJson(commentNeedDto));
        startActivity(intent);
    }


    private void collectClick()
    {
        if (isCollectShow)
        {
            requestCancelCollect();
        }
        else
        {
            requestCollect();
        }
    }


    private void requestCancelCollect()
    {
        if (searchResult == null)
        {
            return;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("uid", mPreference.getString(Preference.uid));
        params.put("tid", searchResult.id);
        params.put("method", "favorite.delete");
        showLoding();
        HttpRequest.loadWithMap(params).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {

            }


            @Override
            public void onResponse(String json, int id)
            {
                Log.w("json", json);
                dismissLoading();
                if (CheckUtil.isNullTxt(json))
                {
                    return;
                }
                if (!CheckUtil.isNull(json))
                {
                    JSONObject mjson = null;
                    try
                    {
                        mjson = new JSONObject(json);
                        if (mjson.getInt("code") == 1)
                        {
                            Toast.makeText(mContext, "取消收藏成功", Toast.LENGTH_SHORT).show();
                            isCollectShow = false;
                            imgCollect.setBackgroundResource(R.drawable.collect_no);
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("isDelete", true);
                            EventBus.getDefault().post(new MessageEvent(bundle));
                        }
                        else
                        {
                            Toast.makeText(mContext, "取消收藏失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    showToast("未搜索到相关记录");
                }
            }
        });
    }


    private void requestCollect()
    {
        if (searchResult == null)
        {
            return;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("uid", mPreference.getString(Preference.uid));
        params.put("tid", searchResult.id);
        params.put("type", 1);
        params.put("method", "favorite.save");
        showLoding();
        HttpRequest.loadWithMap(params).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {

            }


            @Override
            public void onResponse(String json, int id)
            {
                Log.w("json", json);
                dismissLoading();
                if (CheckUtil.isNullTxt(json))
                {
                    return;
                }
                if (!CheckUtil.isNull(json))
                {
                    JSONObject mjson = null;
                    try
                    {
                        mjson = new JSONObject(json);
                        if (mjson.getInt("code") == 1)
                        {
                            Toast.makeText(mContext, "收藏成功", Toast.LENGTH_SHORT).show();
                            isCollectShow = true;
                            imgCollect.setBackgroundResource(R.drawable.collect);
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("isDelete", true);
                            EventBus.getDefault().post(new MessageEvent(bundle));
                        }
                        else
                        {
                            Toast.makeText(mContext, "收藏失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                }
                else
                {
                    showToast("未搜索到相关记录");
                }
            }
        });
    }


    @Override
    public void onCompletion(MediaPlayer mp)
    {
        thread = false;
        mSPlayBtn.setImageResource(R.drawable.media_icon_play_small);
        mBPlayBtn.setImageResource(R.drawable.media_icon_play_big);
    }


    @Override
    public void onPrepared(MediaPlayer mp)
    {
        mp.setPlaybackSpeed(1.0f);
        long duration = mVideo.getDuration();
        long position = mVideo.getCurrentPosition();
        mBigSeek.setMax(MaxLength);
        mSmallSeek.setMax(MaxLength);
        mSPlayBtn.setClickable(true);
        mBPlayBtn.setClickable(true);
        mSPlayBtn.setImageResource(R.drawable.media_icon_pause_small);
        mBPlayBtn.setImageResource(R.drawable.media_icon_pause_big);
        if (status_flag == STATUS_SCALE)
        {
            mCurrentPro.setText(getPlayProgress(position));
            mTotalPro.setText(getPlayProgress(duration));
        }
        else
        {
            mBigPro.setText(getPlayProgress(position) + "/" + getPlayProgress(duration));
            hide();
        }
    }


    @Override
    public boolean onError(MediaPlayer mp, int what, int extra)
    {
        showToast("该视频不存在");
        return true;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        playingIndex = position;
        mListAdapter.notifyDataSetChanged();
        MediaListItemBean mr = mRes.get(position);
        mTitleText.setText(mr.title);
        mInTitleText.setText(mr.title);

        if (mVideo.isPlaying())
        {
            mVideo.stopPlayback();

        }
        mBPlayBtn.setClickable(false);
        mBPlayBtn.setImageResource(R.drawable.media_icon_play_big);

        mSPlayBtn.setClickable(false);
        mSPlayBtn.setImageResource(R.drawable.media_icon_play_small);

        mVideo.setVideoURI(Uri.parse(mr.url));
    }


    @Override
    public void onItemClick(View v, int position)
    {
        mRecyclerAdapter.setSingleSelect(position);
        loadIndex(position);
    }


    class CustomThread extends Thread
    {
        @Override
        public void run()
        {
            while (thread)
            {
                if (!isPaused)
                {
                    mHandler.sendEmptyMessage(1);
                }
                try
                {
                    sleep(1000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }


    private void setFullScreen()
    {
        setOrientation(STATUS_FULLSCREEN);
        ViewGroup.LayoutParams pm = mVideoSu.getLayoutParams();
        pm.height = ViewGroup.LayoutParams.MATCH_PARENT;
        mVideoSu.setLayoutParams(pm);
        mVideoSu.requestLayout();
        mVideo.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);

        mTitleCon.setVisibility(View.GONE);
        mSBottomView.setVisibility(View.GONE);
    }


    private void setWindowScreen()
    {
        hide();
        setOrientation(STATUS_SCALE);
        ViewGroup.LayoutParams pm = mVideoSu.getLayoutParams();
        pm.height = DisplayUtils.dp2px(mContext, 220);
        mVideoSu.setLayoutParams(pm);
        mTitleCon.setVisibility(View.VISIBLE);
        mSBottomView.setVisibility(View.VISIBLE);
        mVideo.setVideoLayout(VideoView.VIDEO_LAYOUT_ORIGIN, 0);
    }


    private List<View> mPagers = new ArrayList<View>();


    private void initInfo()
    {
        TextView textView = new TextView(mContext);
        ViewGroup.LayoutParams p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        int horPadding = DisplayUtils.dp2px(mContext, 12);
        int topPadding = DisplayUtils.dp2px(mContext, 16);
        textView.setLayoutParams(p);
        textView.setPadding(horPadding, topPadding, horPadding, 0);
        try
        {
            if (!TextUtils.isEmpty(mediaAlbumBean.introduction))
            {
                mIntroText.setText(mediaAlbumBean.introduction);
            }
        }
        catch (Exception e)
        {
            mIntroText.setText("暂无");
        }
        textView.setTextSize(14);
        textView.setVerticalScrollBarEnabled(true);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        textView.setTextColor(mContext.getResources().getColor(R.color.grey3));
        mPagers.add(textView);

        ListView list = new ListView(mContext);
        list.setLayoutParams(p);
        list.setCacheColorHint(mContext.getResources().getColor(R.color.transparent));
        list.setDivider(new ColorDrawable(mContext.getResources().getColor(R.color.grey7)));
        list.setDividerHeight(DisplayUtils.dp2px(this, 0.5f));
        list.setSelector(R.color.transparent);
        list.setAdapter(mListAdapter);
        list.setOnItemClickListener(this);
        mPagers.add(list);

    }


    private android.widget.BaseAdapter mListAdapter = new android.widget.BaseAdapter()
    {

        @Override
        public int getCount()
        {
            return mRes.size();
        }


        @Override
        public Object getItem(int position)
        {
            return mRes.get(position);
        }


        @Override
        public long getItemId(int position)
        {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null)
            {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_string2, null);
            }
            TextView tv = (TextView) convertView.findViewById(R.id.text);
            String s = mRes.get(position).title;
            tv.setText(s);
            tv.setTextSize(16);
            tv.setSingleLine(true);
            tv.setGravity(Gravity.CENTER);
            if (position == playingIndex)
            {
                tv.setTextColor(mContext.getResources().getColor(R.color.green2));
            }
            else
            {
                tv.setTextColor(mContext.getResources().getColor(R.color.grey3));
            }
            return convertView;
        }
    };

    private PagerAdapter mPageAdapter = new PagerAdapter()
    {
        @Override
        public int getCount()
        {
            return mPagers.size();
        }


        @Override
        public boolean isViewFromObject(View view, Object object)
        {
            return view == object;
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            View v = mPagers.get(position);
            container.addView(v);
            return v;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            container.removeView(mPagers.get(position));
        }
    };


    @Override
    protected void onResume()
    {
        super.onResume();
        Log.e("test draw time end", System.currentTimeMillis() + "");
    }


    private void loadIndex(final int position)
    {
        if (searchResult == null)
        {
            return;
        }
        showLoding();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", searchResult.id);
        if (!TextUtils.isEmpty(mPreference.getString(Preference.uid)))
        {
            params.put("uid", mPreference.getString(Preference.uid));
        }
        params.put("ip", NetworkUtil.getLocalIpAddress2(mContext));
        params.put("method", "mediaAlbum.get");
        String[] arrays = MapUtil.map2Parameter(params);
        subscription = WeituNetwork.getWeituApi().getMedia(arrays[0], arrays[1], arrays[2]).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<MediaResultBean>()
        {
            @Override
            public void onCompleted()
            {

            }


            @Override
            public void onError(Throwable e)
            {
                dismissLoading();
                Log.w("guanglog", "error + " + e);
            }


            @Override
            public void onNext(MediaResultBean mediaResultBean)
            {
                dismissLoading();
                if (mediaResultBean.code == 1)
                {
                    mediaAlbumBean = mediaResultBean.mediaAlbum;
                    isCollectShow = (mediaResultBean.favorite == 1);
                    if (isCollectShow)
                    {
                        imgCollect.setBackgroundResource(R.drawable.collect);
                    }
                    else
                    {
                        imgCollect.setBackgroundResource(R.drawable.collect_no);
                    }
                    if (!TextUtils.isEmpty(mediaAlbumBean.introduction))
                    {
                        mIntroText.setText(mediaAlbumBean.introduction);
                    }
                    if (mediaAlbumBean.categoriesName1 != null || mediaAlbumBean.categoriesName2 != null)
                    {
                        tvTag.setText(mediaAlbumBean.categoriesName1 + "/" + mediaAlbumBean.categoriesName2);
                    }
                    else
                    {
                        tvTag.setText("暂无分类");
                    }
                    isOpen = (mediaAlbumBean.open == 1);
                    lists.clear();
                    mData.clear();
                    mData = mediaResultBean.mediaList;
                    if (mData.isEmpty())
                    {
                        Toast.makeText(mContext, "没有视频列表", Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    if (mData.size() < 2)
                    {
                        mRecyclerView.setVisibility(View.GONE);
                    }
                    else
                    {
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }
                    for (MediaListItemBean bean : mData)
                    {
                        lists.add(bean.title);
                    }
                    mRecyclerAdapter.setNewData(lists);
                    init(position);
                }
            }
        });
    }
}
