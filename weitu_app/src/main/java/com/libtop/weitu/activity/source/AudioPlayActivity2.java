package com.libtop.weitu.activity.source;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.main.subsubject.SelectSubjectFragment;
import com.libtop.weitu.activity.search.CommentActivity;
import com.libtop.weitu.activity.search.dto.CommentNeedDto;
import com.libtop.weitu.activity.search.dto.SearchResult;
import com.libtop.weitu.activity.source.Bean.MediaAlbumBean;
import com.libtop.weitu.activity.source.Bean.MediaListItemBean;
import com.libtop.weitu.activity.source.Bean.MediaResultBean;
import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.eventbus.MessageEvent;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.http.MapUtil;
import com.libtop.weitu.http.WeituNetwork;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.CheckUtil;
import com.libtop.weitu.utils.ContantsUtil;
import com.libtop.weitu.utils.NetworkUtil;
import com.libtop.weitu.utils.ShareSdkUtil;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class AudioPlayActivity2 extends BaseActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, RecyclerSingleChoiseAdapter.OnItemClickLister
{
    public static final String MEDIA_PATH = "media_path";
    public static final String MEDIA_NAME = "media_name";
    @Bind(R.id.title)
    TextView mTitleText;
    @Nullable
    @Bind(R.id.top_right)
    ImageButton mListsBtn;
    @Bind(R.id.progress_current)
    TextView mCurrentProc;
    @Bind(R.id.progress_total)
    TextView mTotalProc;
    @Bind(R.id.play_pause)
    ImageButton mPlayBtn;

    @Bind(R.id.introduction)
    TextView mIntroText;

    @Bind(R.id.img_audio_photo)
    ImageView imgAudio;

    @Bind(R.id.scl_horizontal)
    RecyclerView mRecyclerView;

    @Bind(R.id.img_collect)
    ImageView imgCollect;

    @Bind(R.id.seekbar)
    SeekBar seekbar;

    @Bind(R.id.scl_parent)
    ScrollView sclParent;

    @Bind(R.id.radioGroup)
    RadioGroup radioGroup;

    @Bind(R.id.info)
    RadioButton radioButton;

    private boolean isPaused = false;
    private MediaPlayer mPlayer;
    private boolean thread = true;

    private List<MediaListItemBean> mAudios = new ArrayList<MediaListItemBean>();
    private int mCurrentIndex;
    private MediaListPopup mPop;

    private List<MediaListItemBean> mData = new ArrayList<MediaListItemBean>();

    private SearchResult searchResult;
    private RecyclerSingleChoiseAdapter mAdapter;
    private List<String> lists = new ArrayList<String>();
    private MediaAlbumBean mediaAlbumBean;

    private boolean isCollectShow = false;
    private String titleName1 = "";

    private boolean isOpen = true;
    private String introdution;
    private CustomThread customThread;

    private long currentPlayIndex;
    private long TW0_MIN = 120000;


    private Handler mHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 1:
                    if (mPlayer != null)
                    {
                        currentPlayIndex = mPlayer.getCurrentPosition();
                        long total = mPlayer.getDuration();
                        if ((currentPlayIndex > TW0_MIN) && !isOpen)
                        {
                            if (mPlayer != null)
                            {
                                mPlayer.seekTo(0);
                                Toast.makeText(mContext, "私有资源只能播放两分钟", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        long position = total == 0 ? 0 : 1000L * currentPlayIndex / total;
                        seekbar.setProgress((int) position);
                        mCurrentProc.setText(getPlayProgress(currentPlayIndex));
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
        int value = (int) position / 1000;
        int minute = value / 60;
        int second = value % 60;
        minute %= 60;
        return String.format("%02d:%02d", minute, second);
    }


    /**
     * 设置显示进度
     *
     * @param position
     */
    private void setPlayProgress(TextView textView, long position)
    {
        int value = (int) position / 1000;
        int minute = value / 60;
        int hour = minute / 60;
        int second = value % 60;
        minute %= 60;
        textView.setText(String.format("%02d:%02d:%02d", hour, minute, second));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setInjectContentView(R.layout.activity_audio_play5);
        noNetThanExit(mContext);
        initView();
        mCurrentIndex = 0;
        String result = getIntent().getExtras().getString("resultBean");
        searchResult = new Gson().fromJson(result, SearchResult.class);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser || mPlayer == null) {
                    return;
                }
                long newposition = (mPlayer.getDuration() * progress) / 1000L;
                mPlayer.seekTo((int) newposition);
                isPaused = false;
                mPlayBtn.setBackgroundResource(R.drawable.audio_pause);
            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }


            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        loadIndex();

    }


    private void initView()
    {
        mCurrentIndex = 0;
        mAdapter = new RecyclerSingleChoiseAdapter(mContext, lists, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);//这里用线性显示 类似于listview
        mRecyclerView.setAdapter(mAdapter);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                switch (checkedId)
                {
                    case R.id.info:
                        mIntroText.setText("简介:" + (TextUtils.isEmpty(introdution) ? "暂无" : introdution));
                        break;
                    case R.id.lrc:
                        String lrc = mData.get(mCurrentIndex).lyrics;
                        if (!TextUtils.isEmpty(lrc))
                        {
                            lrc = lrc.replaceAll("<br />", "");
                            mIntroText.setText(lrc);
                        }
                        else
                        {
                            mIntroText.setText("暂无歌词文本");
                        }
                        break;
                }
            }
        });
    }


    private void init()
    {
        mAudios = mData;

        if (mAudios.isEmpty())
        {
            Toast.makeText(mContext, "没有音频列表", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnPreparedListener(this);

        mPop = new MediaListPopup(this, new MediaListPopup.OnItemClickListener()
        {
            @Override
            public void onItemClick(int position)
            {
                mPlayer.stop();
                mPlayer.reset();
                try
                {
                    setAudio(mAudios.get(position));
                }
                catch (IOException e)
                {
                    showToast("音频解析失败");
                    e.printStackTrace();
                }
            }
        });

        seekbar.setProgress(0);
        thread = true;

        try
        {
            setAudio(mAudios.get(mCurrentIndex));
            customThread = null;
            customThread = new CustomThread();
            customThread.start();
        }
        catch (IOException e)
        {
            showToast("音频解析失败");
            e.printStackTrace();
        }

    }


    @Nullable
    @OnClick({R.id.ll_tool_include,R.id.ll_tool_collect, R.id.ll_tool_comment, R.id.ll_tool_share, R.id.back_btn, R.id.top_right, R.id.play_pause, R.id.prev, R.id.next})
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.ll_tool_include:
                includeClick();
                break;
            case R.id.ll_tool_collect:
                collectClick();
                break;
            case R.id.ll_tool_comment:
                commentClick();
                break;
            case R.id.ll_tool_share:
                shareClick();
                break;
            case R.id.back_btn:
                finishSimple();
                break;
            case R.id.top_right:
                showListPop();
                break;
            case R.id.play_pause:
                playOrPause();
                break;
            case R.id.prev:
                playPrev();
                break;
            case R.id.next:
                playNext();
                break;
        }
    }


    private void includeClick()
    {
        Bundle bundle = new Bundle();
        bundle.putString(ContentActivity.FRAG_CLS, SelectSubjectFragment.class.getName());
        mContext.startActivity(bundle,ContentActivity.class);
    }


    @Override
    public void onBackPressed() {
        finishSimple();
    }

    private void shareClick()
    {
        String title = "微图分享";
        String content = "“【视频】" + titleName1 + "”" + ContantsUtil.shareContent;
        String imageUrl = "drawable://" + R.drawable.wbshare;
        ShareSdkUtil.showShareWithLocalImg(mContext, title, content, imageUrl);
    }


    //音频的type为2
    private void commentClick()
    {
        Intent intent = new Intent(mContext, CommentActivity.class);
        CommentNeedDto commentNeedDto = new CommentNeedDto();
        commentNeedDto.title = mediaAlbumBean.uploadUsername;
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
        params.put("type", 2);
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


    private void setAudio(MediaListItemBean data) throws IOException
    {
        if (!TextUtils.isEmpty(mediaAlbumBean.cover))
        {
            Picasso.with(mContext).load(mediaAlbumBean.cover).into(imgAudio);
        }
        String title = mediaAlbumBean.title;
        titleName1 = title;
        if (data.view != null)
        {
            int views = data.view;
        }
        String intro = data.introduction;
        introdution = intro;
        String url = data.url;
        String uploader1 = data.uploadUsername;

        mPlayer.setDataSource(mContext, Uri.parse(url));
        mPlayBtn.setBackgroundResource(R.drawable.audio_pause);
        mPlayBtn.setClickable(false);
        isPaused = false;
        mPlayer.prepareAsync();
        mTitleText.setText(TextUtils.isEmpty(title) ? "未知标题" : title);
        mIntroText.setText("简介:" + (TextUtils.isEmpty(intro) ? "暂无" : intro));
    }


    private void playNext()
    {
        if (mCurrentIndex + 1 >= lists.size())
        {
            Toast.makeText(mContext, "没有下一首", Toast.LENGTH_SHORT).show();
        }
        else
        {
            onItemClick(null, mCurrentIndex + 1);
            mRecyclerView.smoothScrollToPosition(mCurrentIndex + 1);
        }
    }


    private void playPrev()
    {
        if (mCurrentIndex - 1 < 0)
        {
            Toast.makeText(mContext, "没有上一首", Toast.LENGTH_SHORT).show();
        }
        else
        {
            onItemClick(null, mCurrentIndex - 1);
            if (mCurrentIndex - 1 != 0)
            {
                mRecyclerView.scrollToPosition(mCurrentIndex - 1);
            }
        }
    }


    private void showListPop()
    {
        List<String> list = new ArrayList<String>();
        for (MediaListItemBean bean : mAudios)
        {
            String title = bean.title;
            if (TextUtils.isEmpty(title))
            {
                title = "未知标题";
            }
            list.add(title);
        }
        mPop.show(mListsBtn, list);
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
        stopAndRelase();
    }


    private void stopAndRelase()
    {
        if (mPop != null && mPop.isShowing())
        {
            mPop.dismiss();
        }

        thread = false;
        if (mPlayer != null)
        {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }


    /**
     * 播放暂停
     */
    private void playOrPause()
    {
        if (!mPlayer.isPlaying())
        {
            mPlayer.start();
            mPlayBtn.setBackgroundResource(R.drawable.audio_pause);
        }
        else
        {
            mPlayer.pause();
            mPlayBtn.setBackgroundResource(R.drawable.audio_play);
        }
        isPaused = !isPaused;
    }


    @Override
    public void onCompletion(MediaPlayer mp)
    {
    }


    @Override
    public void onPrepared(MediaPlayer mp)
    {
        long duration = mPlayer.getDuration();
        long position = mPlayer.getCurrentPosition();
        mCurrentProc.setText(getPlayProgress(position));
        mTotalProc.setText(getPlayProgress(duration));
        seekbar.setMax(1000);
        mPlayBtn.setClickable(true);
        mPlayBtn.setBackgroundResource(R.drawable.audio_pause);
        mPlayer.start();
    }


    @Override
    public void onItemClick(View v, int position)
    {
        mAdapter.setSingleSelect(position);
        mCurrentIndex = position;
        stopAndRelase();
        loadIndex();
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
                    sleep(500);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();

                }
            }
        }
    }


    private void loadIndex()
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
                    isOpen = (mediaAlbumBean.open == 1);
                    isCollectShow = (mediaResultBean.favorite == 1);
                    if (isCollectShow)
                    {
                        imgCollect.setBackgroundResource(R.drawable.collect);
                    }
                    else
                    {
                        imgCollect.setBackgroundResource(R.drawable.collect_no);
                    }
                    lists.clear();
                    mData.clear();
                    mData = mediaResultBean.mediaList;
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
                    mAdapter.notifyDataSetChanged();
                    init();
                }
            }
        });
    }
}
