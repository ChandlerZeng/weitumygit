package com.libtop.weitu.activity.main.videoUpload;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.main.dto.VideoBean;
import com.libtop.weitu.activity.search.dto.MediaResult;
import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.utils.DisplayUtils;
import com.libtop.weitu.utils.TransformUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;


/**
 * Created by Administrator on 2016/2/3 0003.
 */
public class VideoPlayActivity3 extends BaseActivity implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, AdapterView.OnItemClickListener
{
    public static String MEDIA_PATH = "vedio_path";
    public static String MEDIA_NAME = "vedio_name";

    private static final int STATUS_FULLSCREEN = 0;
    private static final int STATUS_SCALE = 1;

    @Bind(R.id.title)
    TextView mTitleText;
    @Bind(R.id.title_inner)
    TextView mInTitleText;
    @Bind(R.id.tv_name)
    TextView mVideoName;
    @Bind(R.id.tv_time)
    TextView mVideoTime;
    @Bind(R.id.tv_size)
    TextView mVideoSize;

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


    private Animation mBHideAn, mBShowAn, mTHideAn, mTShowAn;
    private boolean isPaused = false;
    private boolean thread = true;

    private boolean isShowing = false;

    private int playingIndex = 0;

    private int status_flag;

    private List<MediaResult> mRes = new ArrayList<MediaResult>();

    /**
     * Seekbar总长度
     */
    private int MaxLength = 1000;

    private int TouchLength;

    private boolean isTouch = false;
    private boolean notShowButtom = false;
    private VideoBean videoBean;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.e("test draw time start", System.currentTimeMillis() + "");
        super.onCreate(savedInstanceState);
        noNetThanExit(mContext);
        setInjectContentView(R.layout.activity_video_play3);
        init();
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


    private void init()
    {
        setupAnimate();
        Vitamio.isInitialized(getApplicationContext());
        playingIndex = getIntent().getExtras().getInt("index");

        videoBean = new Gson().fromJson(getIntent().getExtras().getString("videoBean"), VideoBean.class);
        if (videoBean != null)
        {
            if (!TextUtils.isEmpty(videoBean.title))
            {
                mVideoName.setText(videoBean.title);
            }
            long duration_temp = videoBean.videDduration * 1000;
            String hms;
            long seconds = (duration_temp % (1000 * 60)) / 1000;
            long hours = (duration_temp / (1000 * 60 * 60));
            long minutes = (duration_temp % (1000 * 60 * 60)) / (1000 * 60);
            if (duration_temp < 1000 * 60 * 60)
            {
                hms = String.format("%02d:%02d", minutes, seconds);
            }
            else
            {

                hms = String.format("%02d:%02d", hours, minutes);
            }
            mVideoTime.setText(hms);
            mVideoSize.setText(TransformUtil.bytes2kb(videoBean.videoSize));
        }

        String videoPath = getIntent().getExtras().getString(MEDIA_PATH);
        String name = getIntent().getExtras().getString(MEDIA_NAME);
        notShowButtom = getIntent().getExtras().getBoolean("notShowButtom");
        mTitleText.setText(name);
        mInTitleText.setText(name);
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
            mFBottomView.startAnimation(mBShowAn);
            mTopView.startAnimation(mTShowAn);
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
    @OnClick({R.id.back_btn, R.id.back_btn_inner, R.id.play_pause_big, R.id.play_pause_small, R.id.video_container, R.id.fullscreen, R.id.scale})
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.back_btn:
            case R.id.back_btn_inner:
                finishSimple();
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
                setFullScreen();
                break;
            case R.id.scale:
                setWindowScreen();
                break;
        }
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
        mVideo.setVideoLayout(VideoView.VIDEO_LAYOUT_FIT_PARENT, 0);
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
        TextView tv = (TextView) mPagers.get(0);
        MediaResult mr = mRes.get(position);
        tv.setText(TextUtils.isEmpty(mr.introduction) ? "暂无" : mr.introduction);
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
        String txt;
        try
        {
            txt = mRes.get(playingIndex).introduction;
        }
        catch (Exception e)
        {
            txt = "123";
        }
        textView.setText(TextUtils.isEmpty(txt) ? "暂无" : txt);
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
}
