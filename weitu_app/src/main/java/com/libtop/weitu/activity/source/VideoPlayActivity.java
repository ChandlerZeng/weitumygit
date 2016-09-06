package com.libtop.weitu.activity.source;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.utils.DisplayUtils;

import butterknife.Bind;
import butterknife.OnClick;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;


/**
 * Created by Administrator on 2016/1/4 0004.
 */
public class VideoPlayActivity extends BaseActivity implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener
{
    public static String MEDIA_PATH = "vedio_path";
    public static String MEDIA_NAME = "vedio_name";
    @Bind(R.id.title)
    TextView mTitleText;
    @Bind(R.id.video_view)
    VideoView mVideo;
    @Bind(R.id.seekbar)
    SeekBar mSeek;
    @Bind(R.id.progress_current)
    TextView mCurrentPro;
    @Bind(R.id.progress_total)
    TextView mTotalPro;
    @Bind(R.id.play_pause)
    ImageButton mPlayBtn;
    @Bind(R.id.media_bottom)
    LinearLayout mBottomView;
    @Bind(R.id.media_top)
    LinearLayout mTopView;

    @Bind(R.id.container)
    RelativeLayout mVideoSu;

    private Animation mBHideAn, mBShowAn, mTHideAn, mTShowAn;
    private boolean isPaused = false;
    private boolean thread = true;

    private boolean isShowing = true;

    private boolean isVertical = true;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        noNetThanExit(mContext);
        setInjectContentView(R.layout.activity_video_play2);
        init();
    }


    private void setOrientation(boolean isVertical)
    {
        this.isVertical = isVertical;
        if (isVertical)
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
        String videoPath = getIntent().getExtras().getString(MEDIA_PATH);
        String name = getIntent().getExtras().getString(MEDIA_NAME);
        mTitleText.setText(name);
        mVideo.setVideoURI(Uri.parse(videoPath));
        mVideo.requestFocus();
        mPlayBtn.setClickable(false);
        mPlayBtn.setImageResource(R.drawable.btn_play);

        mVideo.setOnCompletionListener(this);
        mVideo.setOnPreparedListener(this);

        new CustomThread().start();
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
                    long curent = mVideo.getCurrentPosition();
                    long total = mVideo.getDuration();
                    long position = 1000L * curent / total;
                    mSeek.setProgress((int) position);
                    setPlayProgress(mCurrentPro, curent);
                    mSeek.setSecondaryProgress(mVideo.getBufferPercentage() * 10);
                    break;
            }
        }
    };


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


    /**
     * 播放暂停
     */
    private void playOrPause()
    {
        if (!mVideo.isPlaying())
        {
            mVideo.start();
            mPlayBtn.setImageResource(R.drawable.btn_pause);
        }
        else
        {
            mVideo.pause();
            mPlayBtn.setImageResource(R.drawable.btn_play);
        }
        isPaused = !isPaused;
    }


    private void show()
    {
        if (!isShowing)
        {
            mBottomView.setVisibility(View.VISIBLE);
            mTopView.setVisibility(View.VISIBLE);
            mBottomView.startAnimation(mBShowAn);
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
            mBottomView.startAnimation(mBHideAn);
            mBottomView.setVisibility(View.GONE);
            mTopView.startAnimation(mTHideAn);
            mTopView.setVisibility(View.GONE);
            isShowing = false;
        }
    }


    @Nullable
    @OnClick({R.id.back_btn, R.id.play_pause, R.id.video_container})
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.back_btn:
                finishSimple();
                break;
            case R.id.play_pause:
                playOrPause();
                break;
            case R.id.video_container:
                show();
                break;
        }
    }


    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        if (!fromUser)
        {
            return;
        }
        long newposition = (mVideo.getDuration() * progress) / 1000L;
        mVideo.seekTo(newposition);
        isPaused = false;
        mPlayBtn.setImageResource(R.drawable.btn_pause);

    }


    @Override
    public void onCompletion(MediaPlayer mp)
    {
        thread = false;
        finish();
        mPlayBtn.setImageResource(R.drawable.btn_play);
    }


    @Override
    public void onPrepared(MediaPlayer mp)
    {
        mp.setPlaybackSpeed(1.0f);
        long duration = mVideo.getDuration();
        long position = mVideo.getCurrentPosition();
        setPlayProgress(mCurrentPro, position); // 设置时间显示
        setPlayProgress(mTotalPro, duration);
        mSeek.setMax(1000);
        mPlayBtn.setClickable(true);
        mPlayBtn.setImageResource(R.drawable.btn_pause);
        hide();
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
                    sleep(100);
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
        setOrientation(false);
        ViewGroup.LayoutParams pm = mVideoSu.getLayoutParams();
        pm.height = ViewGroup.LayoutParams.MATCH_PARENT;
        mVideoSu.setLayoutParams(pm);
        mVideoSu.requestLayout();
    }


    private void setWindowScreen()
    {
        setOrientation(true);
        ViewGroup.LayoutParams pm = mVideoSu.getLayoutParams();
        pm.height = DisplayUtils.dp2px(this, 200);
        mVideoSu.setLayoutParams(pm);
        mVideoSu.requestLayout();
    }
}
