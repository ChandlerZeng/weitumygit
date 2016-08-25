package com.libtop.weitu.activity.source;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.search.dto.MediaResult;
import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.utils.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

public class AudioPlayActivity extends BaseActivity implements MediaPlayer.OnPreparedListener
		,MediaPlayer.OnCompletionListener{
	public static final String MEDIA_PATH = "media_path";
	public static final String MEDIA_NAME = "media_name";
	@Bind(R.id.title)
	TextView mTitleText;
	@Bind(R.id.top_right)
	ImageButton mListsBtn;
	@Bind(R.id.seekbar)
	SeekBar mSeek;
	@Bind(R.id.progress_current)
	TextView mCurrentProc;
	@Bind(R.id.progress_total)
	TextView mTotalProc;
	@Bind(R.id.play_pause)
	ImageButton mPlayBtn;

	@Bind(R.id.uploader)
	TextView mUploaderText;
	@Bind(R.id.viewers)
	TextView mViewersText;
	@Bind(R.id.introduction)
	TextView mIntroText;

	private boolean isPaused = false;
	private MediaPlayer mPlayer;
	private boolean thread = true;

	private List<MediaResult> mAudios=new ArrayList<MediaResult>();
	private int mCurrentIndex;
	private MediaListPopup mPop;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					if(mPlayer != null){
						long curent = mPlayer.getCurrentPosition();
						long total=mPlayer.getDuration();

						long position=total==0?0:1000L*curent/total;
						mSeek.setProgress((int) position);
						setPlayProgress(mCurrentProc, curent); // 设置时间显示
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
	private void setPlayProgress(TextView textView, long position) {
		int value =(int) position / 1000;
		int minute = value / 60;
		int hour = minute / 60;
		int second = value % 60;
		minute %= 60;
		textView.setText(String.format("%02d:%02d:%02d", hour, minute, second));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setInjectContentView(R.layout.activity_audio_play2);
		init();
	}

	private void init(){
//		Vitamio.isInitialized(getApplicationContext());
		mAudios=getIntent().getExtras().getParcelableArrayList("media_list");
		mCurrentIndex=getIntent().getExtras().getInt("media_list_position");

		if (CollectionUtils.isEmpty(mAudios)){
			showToast("解析失败");
			finish();
			return;
		}

//		mPath = getIntent().getExtras().getString(MEDIA_PATH);
//		String name = getIntent().getExtras().getString(MEDIA_NAME);
//		mTitleText.setText(name);
		mPlayer=new MediaPlayer();
		mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

		mPlayer.setOnCompletionListener(this);
		mPlayer.setOnPreparedListener(this);

//		mBackBtn.setOnClickListener(this);
//		mListsBtn.setOnClickListener(this);
//		mPlayBtn.setOnClickListener(this);
//		mPrevBtn.setOnClickListener(this);
//		mNextBtn.setOnClickListener(this);
//		mSeek.setOnSeekBarChangeListener(this);
		mSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (!fromUser) return;
				long newposition = (mPlayer.getDuration() * progress) / 1000L;
				mPlayer.seekTo((int)newposition);
				isPaused=false;
				mPlayBtn.setImageResource(R.drawable.btn_pause);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});

		mPop=new MediaListPopup(this, new MediaListPopup.OnItemClickListener() {
			@Override
			public void onItemClick(int position) {
				mPlayer.stop();
				mPlayer.reset();
				try {
					setAudio(mAudios.get(position));
				} catch (IOException e) {
					showToast("音频解析失败");
					e.printStackTrace();
				}
			}
		});

		try {

			setAudio(mAudios.get(mCurrentIndex));
//			mPlayer.start();
			new CustomThread().start();
		} catch (IOException e) {
			showToast("音频解析失败");
			e.printStackTrace();
		}

	}

	@Nullable
	@OnClick({R.id.back_btn,R.id.top_right,R.id.play_pause,R.id.prev,R.id.next})
	public void onClick(View v) {
		switch (v.getId()) {
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

	private void setAudio(MediaResult data) throws IOException {
		String title=data.title;
		String uploader=data.uploadUsername;
		int views=data.view;
		String intro=data.introduction;
		String url=data.url;

		mPlayer.setDataSource(mContext, Uri.parse(url));
		mPlayBtn.setImageResource(R.drawable.btn_pause);
		mPlayBtn.setClickable(false);
		isPaused=false;
		mPlayer.prepareAsync();
		mTitleText.setText(TextUtils.isEmpty(title) ? "未知标题" : title);
		mUploaderText.setText("上传者:" + (TextUtils.isEmpty(uploader) ? "未知" : uploader));
		mViewersText.setText("收听数:" + views);
		mIntroText.setText("简介:"+(TextUtils.isEmpty(intro)?"暂无":intro));
	}

	private void playNext(){
		mPlayer.stop();
		mPlayer.reset();
		int next=mCurrentIndex+1;
		mCurrentIndex=next>mAudios.size()-1?0:next;
		try {
			setAudio(mAudios.get(mCurrentIndex));
		} catch (IOException e) {
			e.printStackTrace();
			showToast("音频解析失败");
		}
	}

	private void playPrev(){
		mPlayer.stop();
		mPlayer.reset();
		int prev=mCurrentIndex-1;
		mCurrentIndex=prev<0?mAudios.size()-1:prev;
		try {
			setAudio(mAudios.get(mCurrentIndex));
		} catch (IOException e) {
			e.printStackTrace();
			showToast("音频解析失败");
		}
	}

	private void showListPop(){
		List<String> list=new ArrayList<String>();
		for (MediaResult bean:mAudios){
			String title=bean.title;
			if (TextUtils.isEmpty(title)){
				title="未知标题";
			}
			list.add(title);
		}
		mPop.show(mListsBtn,list);
	}

	@Override
	public void onDestroy(){
		super.onDestroy();

		mPop.dismiss();

		thread = false;
		if (mPlayer!=null){
			mPlayer.stop();
			mPlayer.release();
			mPlayer = null;
		}
	}

	/**
	 * 播放暂停
	 */
	private void playOrPause() {
		if (!mPlayer.isPlaying()) {
			mPlayer.start();
			mPlayBtn.setImageResource(R.drawable.btn_pause);
		} else {
			mPlayer.pause();
			mPlayBtn.setImageResource(R.drawable.btn_play);
		}
		isPaused = !isPaused;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
//		mPlayBtn.setImageResource(R.drawable.btn_play);
		//播放下一首
//		if (mCurrentIndex==mAudios.size()-1){
//			finish();
//		}else {
//			playNext();
//		}
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		long duration = mPlayer.getDuration();
		long position = mPlayer.getCurrentPosition();
		setPlayProgress(mCurrentProc, position); // 设置时间显示
		setPlayProgress(mTotalProc, duration);
		mSeek.setMax(1000);
		mPlayBtn.setClickable(true);
		mPlayBtn.setImageResource(R.drawable.btn_pause);
		mPlayer.start();
	}

//	@Nullable @OnClick(value = R.id.seekbar,type = SeekBar.OnSeekBarChangeListener.class
//			,method = "onProgressChanged")
//	private void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//		if (!fromUser) return;
//		long newposition = (mPlayer.getDuration() * progress) / 1000L;
//		mPlayer.seekTo((int)newposition);
//		isPaused=false;
//		mPlayBtn.setImageResource(R.drawable.btn_pause);
//	}

//	@Override
//	public void onStartTrackingTouch(SeekBar seekBar) {
//
//	}
//
//	@Override
//	public void onStopTrackingTouch(SeekBar seekBar) {
//
//	}

	class CustomThread extends Thread {
		@Override
		public void run() {
			while(thread){
				if(!isPaused){
					mHandler.sendEmptyMessage(1);
				}
				try {
					sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

				}
			}
		}
	}


}
