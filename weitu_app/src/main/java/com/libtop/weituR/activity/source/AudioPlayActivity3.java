//package com.libtop.weituR.activity.source;
//
//import android.content.Intent;
//import android.media.AudioManager;
//import android.media.MediaPlayer;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.annotation.Nullable;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.View;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ScrollView;
//import android.widget.SeekBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.gson.Gson;
//import com.libtop.weitu.R;
//import com.libtop.weituR.activity.search.CommentActivity;
//import com.libtop.weituR.activity.search.dto.CommentNeedDto;
//import com.libtop.weituR.activity.search.dto.SearchResult;
//import com.libtop.weituR.activity.source.Bean.MediaAlbumBean;
//import com.libtop.weituR.activity.source.Bean.MediaListItemBean;
//import com.libtop.weituR.activity.source.Bean.MediaResultBean;
//import com.libtop.weituR.base.BaseActivity;
//import com.libtop.weituR.http.HttpRequest;
//import com.libtop.weituR.http.MapUtil;
//import com.libtop.weituR.http.WeituNetwork;
//import com.libtop.weituR.tool.Preference;
//import com.libtop.weituR.utils.CheckUtil;
//import com.libtop.weituR.utils.ContantsUtil;
//import com.libtop.weituR.utils.NetworkUtil;
//import com.libtop.weituR.utils.ShareSdkUtil;
//import com.libtop.weituR.widget.ScrollInside;
//import com.squareup.picasso.Picasso;
//import com.zhy.http.okhttp.callback.StringCallback;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import butterknife.Bind;
//import butterknife.OnClick;
//import okhttp3.Call;
//import rx.Observer;
//import rx.android.schedulers.AndroidSchedulers;
//import rx.schedulers.Schedulers;
//
///**
// * Created by LianTu on 2016-8-9.
// */
//public class AudioPlayActivity3 extends BaseActivity implements MediaPlayer.OnPreparedListener
//        ,MediaPlayer.OnCompletionListener,RecyclerSingleChoiseAdapter.OnItemClickLister{
//    public static final String MEDIA_PATH = "media_path";
//    public static final String MEDIA_NAME = "media_name";
//    @Bind(R.id.title)
//    TextView mTitleText;
//    @Nullable
//    @Bind(R.id.top_right)
//    ImageButton mListsBtn;
//    //	@Bind(R.id.seekbar)
////	SeekBar seekbar;
//    @Bind(R.id.progress_current)
//    TextView mCurrentProc;
//    @Bind(R.id.progress_total)
//    TextView mTotalProc;
//    @Bind(R.id.play_pause)
//    ImageButton mPlayBtn;
//
//    //	@Bind(R.id.uploader)
////	TextView mUploaderText;
////	@Bind(R.id.viewers)
////	TextView mViewersText;
//
//    @Bind(R.id.introduction)
//    TextView mIntroText;
//
//    @Bind(R.id.tv_uploader)
//    TextView mUploader;
//
//    @Bind(R.id.img_audio_photo)
//    ImageView imgAudio;
//
//    @Bind(R.id.scl_horizontal)
//    RecyclerView mRecyclerView;
//
//    @Bind(R.id.img_collect)
//    ImageView imgCollect;
//
//    @Bind(R.id.seekbar)
//    SeekBar seekbar;
//
//    @Bind(R.id.scl_child)
//    ScrollInside sclChild;
//
//    @Bind(R.id.scl_parent)
//    ScrollView sclParent;
//
//    @Bind(R.id.ll_board_scroll)
//    LinearLayout sclBoard;
//
//    private boolean isPaused = false;
//    private MediaPlayer mPlayer;
//    private boolean thread = true;
//
//    private List<MediaListItemBean> mAudios=new ArrayList<MediaListItemBean>();
//    private int mCurrentIndex;
//    private MediaListPopup mPop;
//
//    private List<MediaListItemBean> mData=new ArrayList<MediaListItemBean>();
//
//    private SearchResult searchResult;
//    private RecyclerSingleChoiseAdapter mAdapter;
//    private List<String> lists = new ArrayList<String>();
//    private MediaAlbumBean mediaAlbumBean ;
//
//    private boolean isCollectShow = false;
//    private String titleName1 = "";
//
//    private boolean isOpen = true;
//
//
//    private Handler mHandler = new Handler() {
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case 1:
//                    if(mPlayer != null){
//                        long curent = mPlayer.getCurrentPosition();
//                        long total=mPlayer.getDuration();
//                        long twoMin = 1000L * 60 * 2;
//                        if ((curent > twoMin)&& !isOpen){
//                            if (mPlayer!=null){
//                                mPlayer.stop();
//                                return;
//                            }
//                        }
//                        long position=total==0?0:1000L*curent/total;
//                        seekbar.setProgress((int) position);
//                        setPlayProgress(mCurrentProc, curent); // 设置时间显示
//                    }
//                    break;
//            }
//        }
//    };
//
//    /**
//     * 设置显示进度
//     *
//     * @param position
//     */
//    private void setPlayProgress(TextView textView, long position) {
//        int value =(int) position / 1000;
//        int minute = value / 60;
//        int hour = minute / 60;
//        int second = value % 60;
//        minute %= 60;
//        textView.setText(String.format("%02d:%02d:%02d", hour, minute, second));
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setInjectContentView(R.layout.activity_audio_play4);
////		init();
//        mCurrentIndex=0;
////        mAdapter = new SingleSelectAdapter(mContext,lists);
////        mRecyclerView.setAdapter(mAdapter);
//        mAdapter = new RecyclerSingleChoiseAdapter(mContext,lists,this);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
//        mRecyclerView.setLayoutManager(linearLayoutManager);//这里用线性显示 类似于listview
////        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));//这里用线性宫格显示 类似于grid view
////        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL));//这里用线性宫格显示 类似于瀑布流
//        mRecyclerView.setAdapter(mAdapter);
////        mRecyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
////            @Override
////            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                String imgPath = (String) parent.getItemAtPosition(position);
////                mAdapter.setSingleSelect(position);
////                mCurrentIndex = position;
////                stopAndRelase();
////                loadIndex();
//////                bundle.putInt("media_list_position", position);
//////
//////                mCurrentIndex =
//////
//////                bundle.putParcelableArrayList("media_list", (ArrayList<? extends Parcelable>) mData);
//////                mContext.startActivity(bundle, AudioPlayActivity.class);
////            }
////        });
//        String result = getIntent().getExtras().getString("resultBean");
//        searchResult = new Gson().fromJson(result,SearchResult.class);
//        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (!fromUser || mPlayer == null) return;
//                long newposition = (mPlayer.getDuration() * progress) / 1000L;
//                mPlayer.seekTo((int)newposition);
//                isPaused=false;
//                mPlayBtn.setImageResource(R.drawable.audio_pause);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
////        Picasso.with(mContext).load(ContantsUtil.getCoverUrl(searchResult.id)).into(imgAudio);
////        sclChild.setOnTouchListener(new View.OnTouchListener() {
////            @Override
////            public boolean onTouch(View v, MotionEvent event) {
////                v.getParent().requestDisallowInterceptTouchEvent(true);
////                return false;
////            }
////        });
////        sclParent.setOnTouchListener(new View.OnTouchListener() {
////            @Override
////            public boolean onTouch(View v, MotionEvent event) {
////                sclChild.requestDisallowInterceptTouchEvent(false);
////                return false;
////            }
////        });
//        loadIndex();
//
//    }
//
//    private void init(){
////		Vitamio.isInitialized(getApplicationContext());
//
//        mAudios= mData;
//
//        if (mAudios.isEmpty()){
//            Toast.makeText(mContext,"没有音频列表",Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//
////		mPath = getIntent().getExtras().getString(MEDIA_PATH);
////		String name = getIntent().getExtras().getString(MEDIA_NAME);
////		mTitleText.setText(name);
//        mPlayer=new MediaPlayer();
//        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//
//        mPlayer.setOnCompletionListener(this);
//        mPlayer.setOnPreparedListener(this);
//
////		mBackBtn.setOnClickListener(this);
////		mListsBtn.setOnClickListener(this);
////		mPlayBtn.setOnClickListener(this);
////		mPrevBtn.setOnClickListener(this);
////		mNextBtn.setOnClickListener(this);
////		seekbar.setOnSeekBarChangeListener(this);
//
//        mPop=new MediaListPopup(this, new MediaListPopup.OnItemClickListener() {
//            @Override
//            public void onItemClick(int position) {
//                mPlayer.stop();
//                mPlayer.reset();
//                try {
//                    setAudio(mAudios.get(position));
//                } catch (IOException e) {
//                    showToast("音频解析失败");
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        try {
//
//            setAudio(mAudios.get(mCurrentIndex));
////			mPlayer.start();
//            new CustomThread().start();
//        } catch (IOException e) {
//            showToast("音频解析失败");
//            e.printStackTrace();
//        }
//
//    }
//
//
//    @Nullable
//    @OnClick({R.id.img_collect,R.id.img_comment,R.id.img_share,R.id.back_btn,
//            R.id.top_right,R.id.play_pause,R.id.prev,R.id.next,R.id.scl_child,R.id.img_audio_photo})
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.img_collect:
//                collectClick();
//                break;
//            case R.id.img_comment:
//                commentClick();
//                break;
//            case R.id.img_share:
//                shareClick();
//                break;
//            case R.id.back_btn:
//                finishSimple();
//                break;
//            case R.id.top_right:
//                showListPop();
//                break;
//            case R.id.play_pause:
//                playOrPause();
//                break;
//            case R.id.prev:
//                playPrev();
//                break;
//            case R.id.next:
//                playNext();
//                break;
//            case R.id.scl_child:
//                sclChildClick();
//                break;
//            case R.id.img_audio_photo:
//                imgAudioPhotoClick();
//                break;
//        }
//    }
//
//    private void imgAudioPhotoClick() {
//        sclBoard.setVisibility(View.VISIBLE);
//    }
//
//    private void sclChildClick() {
//        sclBoard.setVisibility(View.INVISIBLE);
//    }
//
//    private void shareClick() {
////		Toast.makeText(mContext,"share click",Toast.LENGTH_SHORT).show();
////		UemgShare a = new UemgShare(mContext);
////		String str = "www.baidu.com";
////		a.setImage(str).setTitle("321").setText("123").share();
//        String title = "微图分享";
//        String content = "“【视频】"+titleName1+"”"+ ContantsUtil.shareContent;
//        String imageUrl = "drawable://" + R.drawable.wbshare;
//        ShareSdkUtil.showShareWithLocalImg(mContext,title,content,imageUrl);
//    }
//
//    //音频的type为2
//    private void commentClick() {
//        Intent intent = new Intent(mContext, CommentActivity.class);
//        CommentNeedDto commentNeedDto = new CommentNeedDto();
//        commentNeedDto.title = mediaAlbumBean.uploadUsername;
//        commentNeedDto.author = mediaAlbumBean.artist;
//        commentNeedDto.publisher = mediaAlbumBean.uploadUsername;
//        commentNeedDto.photoAddress = mediaAlbumBean.cover;
//        commentNeedDto.tid = searchResult.id;
//        commentNeedDto.type = 2;
//        intent.putExtra("CommentNeedDto",new Gson().toJson(commentNeedDto));
//        startActivity(intent);
////		Intent intent = new Intent(mContext, CommentActivity.class);
////		intent.putExtra("comment_tid",searchResult.id);
////		intent.putExtra("comment_type", "mediaAlbum");
////		startActivity(intent);
//    }
//
//    private void collectClick() {
////		Toast.makeText(mContext,"collect click",Toast.LENGTH_SHORT).show();
//        if (isCollectShow){
//            requestCancelCollect();
//        }else {
//            requestCollect();
//        }
//    }
//
//    private void requestCancelCollect() {
//        if (searchResult==null){
//            return;
//        }
//        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("uid"
//                , mPreference.getString(Preference.uid));
//        params.put("tid",searchResult.id);
//        params.put("method", "favorite.delete");
//        mLoading.show();
//        HttpRequest.loadWithMap(params)
//                .execute(new StringCallback() {
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//
//                    }
//
//                    @Override
//                    public void onResponse(String json, int id) {
//                        Log.w("json", json);
//                        mLoading.hide();
//                        if (CheckUtil.isNullTxt(json)) {
//                            return;
//                        }
//                        if (!CheckUtil.isNull(json)) {
//                            JSONObject mjson = null;
//                            try {
//                                mjson = new JSONObject(json);
//                                if (mjson.getInt("code") == 1) {
//                                    Toast.makeText(mContext, "取消收藏成功", Toast.LENGTH_SHORT).show();
//                                    isCollectShow = false;
//                                    imgCollect.setBackgroundResource(R.drawable.collect_no);
//                                } else {
//                                    Toast.makeText(mContext, "取消收藏失败", Toast.LENGTH_SHORT).show();
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        } else {
//                            showToast("未搜索到相关记录");
//                        }
//                    }
//                });
//    }
//
//    private void requestCollect() {
//        if (searchResult==null){
//            return;
//        }
//        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("uid"
//                , mPreference.getString(Preference.uid));
//        params.put("tid",searchResult.id);
//        params.put("type",2);
//        params.put("method","favorite.save");
//        mLoading.show();
//        HttpRequest.loadWithMap(params)
//                .execute(new StringCallback() {
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//
//                    }
//
//                    @Override
//                    public void onResponse(String json, int id) {
//                        Log.w("json", json);
//                        mLoading.hide();
//                        if (CheckUtil.isNullTxt(json)) {
//                            return;
//                        }
//                        if (!CheckUtil.isNull(json)) {
//                            JSONObject mjson = null;
//                            try {
//                                mjson = new JSONObject(json);
//                                if (mjson.getInt("code")==1){
//                                    Toast.makeText(mContext, "收藏成功", Toast.LENGTH_SHORT).show();
//                                    isCollectShow = true;
//                                    imgCollect.setBackgroundResource(R.drawable.collect);
//                                }else {
//                                    Toast.makeText(mContext, "收藏失败", Toast.LENGTH_SHORT).show();
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                        } else {
//                            showToast("未搜索到相关记录");
//                        }
//                    }
//                });
//    }
//
//    private void setAudio(MediaListItemBean data) throws IOException {
//        if (!TextUtils.isEmpty(mediaAlbumBean.cover)){
//            Picasso.with(mContext).load(mediaAlbumBean.cover).into(imgAudio);
//        }
//        String title=data.title;
//        titleName1 = title;
//        String uploader=data.uploadUsername;
//        if (data.view!=null){
//            int views=data.view;
//        }
//        String intro=data.introduction;
//        String url=data.url;
//        String uploader1 = data.uploadUsername;
//        isCollectShow = (mediaAlbumBean.favorite == 1);
//
//        mPlayer.setDataSource(mContext, Uri.parse(url));
//        mPlayBtn.setImageResource(R.drawable.audio_pause);
//        mPlayBtn.setClickable(false);
//        isPaused=false;
//        mPlayer.prepareAsync();
//        mTitleText.setText(TextUtils.isEmpty(title) ? "未知标题" : title);
////		mUploaderText.setText("上传者:" + (TextUtils.isEmpty(uploader) ? "未知" : uploader));
////		mViewersText.setText("收听数:" + views);
//        mIntroText.setText("简介:"+(TextUtils.isEmpty(intro)?"暂无":intro));
//        mUploader.setText(uploader1);
//        if (isCollectShow){
//            imgCollect.setBackgroundResource(R.drawable.collect);
//        }else {
//            imgCollect.setBackgroundResource(R.drawable.collect_no);
//        }
//    }
//
//    private void playNext(){
//        mPlayer.stop();
//        mPlayer.reset();
//        int next=mCurrentIndex+1;
//        mCurrentIndex=next>mAudios.size()-1?0:next;
//        try {
//            setAudio(mAudios.get(mCurrentIndex));
//        } catch (IOException e) {
//            e.printStackTrace();
//            showToast("音频解析失败");
//        }
//    }
//
//    private void playPrev(){
//        mPlayer.stop();
//        mPlayer.reset();
//        int prev=mCurrentIndex-1;
//        mCurrentIndex=prev<0?mAudios.size()-1:prev;
//        try {
//            setAudio(mAudios.get(mCurrentIndex));
//        } catch (IOException e) {
//            e.printStackTrace();
//            showToast("音频解析失败");
//        }
//    }
//
//    private void showListPop(){
//        List<String> list=new ArrayList<String>();
//        for (MediaListItemBean bean:mAudios){
//            String title=bean.title;
//            if (TextUtils.isEmpty(title)){
//                title="未知标题";
//            }
//            list.add(title);
//        }
//        mPop.show(mListsBtn,list);
//    }
//
//    @Override
//    public void onDestroy(){
//        super.onDestroy();
//        stopAndRelase();
//    }
//
//    private void stopAndRelase(){
//        if (mPop!=null && mPop.isShowing()){
//            mPop.dismiss();
//        }
//
//        thread = false;
//        if (mPlayer!=null){
//            mPlayer.stop();
//            mPlayer.release();
//            mPlayer = null;
//        }
//    }
//
//    /**
//     * 播放暂停
//     */
//    private void playOrPause() {
//        if (!mPlayer.isPlaying()) {
//            mPlayer.start();
//            mPlayBtn.setImageResource(R.drawable.audio_pause);
//        } else {
//            mPlayer.pause();
//            mPlayBtn.setImageResource(R.drawable.audio_play);
//        }
//        isPaused = !isPaused;
//    }
//
//    @Override
//    public void onCompletion(MediaPlayer mp) {
////		mPlayBtn.setImageResource(R.drawable.btn_play);
//        //播放下一首
////		if (mCurrentIndex==mAudios.size()-1){
////			finish();
////		}else {
////			playNext();
////		}
//    }
//
//    @Override
//    public void onPrepared(MediaPlayer mp) {
//        long duration = mPlayer.getDuration();
//        long position = mPlayer.getCurrentPosition();
//        setPlayProgress(mCurrentProc, position); // 设置时间显示
//        setPlayProgress(mTotalProc, duration);
//        seekbar.setMax(1000);
//        mPlayBtn.setClickable(true);
//        mPlayBtn.setImageResource(R.drawable.audio_pause);
//        mPlayer.start();
//    }
//
//    @Override
//    public void onItemClick(View v, int position) {
////        String imgPath = (String) lists.get(position);
//        mAdapter.setSingleSelect(position);
//        mCurrentIndex = position;
//        stopAndRelase();
//        loadIndex();
//    }
//
////	@Nullable @OnClick(value = R.id.seekbar,type = SeekBar.OnSeekBarChangeListener.class
////			,method = "onProgressChanged")
////	private void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
////		if (!fromUser || mPlayer == null) return;
////		long newposition = (mPlayer.getDuration() * progress) / 1000L;
////		mPlayer.seekTo((int)newposition);
////		isPaused=false;
////		mPlayBtn.setImageResource(R.drawable.audio_pause);
////	}
//
//
//    class CustomThread extends Thread {
//        @Override
//        public void run() {
//            while(thread){
//                if(!isPaused){
//                    mHandler.sendEmptyMessage(1);
//                }
//                try {
//                    sleep(500);
//                } catch (InterruptedException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//
//                }
//            }
//        }
//    }
//
//
//    private void loadIndex(){
//        if (searchResult==null){
//            return;
//        }
//        mLoading.show();
//        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("id", searchResult.id);
//        if(!TextUtils.isEmpty(mPreference.getString(Preference.uid)))
//            params.put("uid", mPreference.getString(Preference.uid));
//        params.put("ip", NetworkUtil.getLocalIpAddress2(mContext));
//        params.put("method", "mediaAlbum.get");
//        String[] arrays = MapUtil.map2Parameter(params);
//        subscription = WeituNetwork.getWeituApi()
//                .getMedia(arrays[0],arrays[1],arrays[2])
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<MediaResultBean>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        if (mLoading!=null&&mLoading.isShowing()){
//                            mLoading.dismiss();
//                        }
//                        Log.w("guanglog","error + " + e);
//                    }
//
//                    @Override
//                    public void onNext(MediaResultBean mediaResultBean) {
//                        if (mLoading!=null&&mLoading.isShowing()){
//                            mLoading.dismiss();
//                        }
//                        if (mediaResultBean.code==1){
//                            mediaAlbumBean = mediaResultBean.mediaAlbum;
//                            isOpen = (mediaAlbumBean.open == 1);
//                            lists.clear();
//                            mData.clear();
//                            mData = mediaResultBean.mediaList;
//                            for (MediaListItemBean bean:mData) {
//                                lists.add(bean.title);
//                            }
//                            resetCache();
//                            mAdapter.setNewData(lists);
//                            init();
//                        }
//                    }
//                });
//    }
//
//    private void resetCache(){
////		DbManager dao= x.getDb(((AppApplication)mContext.getApplicationContext()).getDaoConfig());
////		try {
////			dao.delete(MediaResult.class);
////			for (MediaListItemBean result:mData){
////				dao.save(result);
////			}
////		} catch (DbException e) {
////			e.printStackTrace();
////		}
//    }
//
//
//}
