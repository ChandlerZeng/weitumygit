package com.libtop.weituR.activity.main;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.libtop.weitu.R;
import com.libtop.weituR.activity.ContentActivity;
import com.libtop.weituR.activity.login.LoginFragment;
import com.libtop.weituR.activity.main.adapter.MainIconAdapter;
import com.libtop.weituR.activity.main.adapter.MainImageAdapter;
import com.libtop.weituR.activity.main.adapter.NoticeAdapter;
import com.libtop.weituR.activity.main.clazz.ClassmateFragment;
import com.libtop.weituR.activity.main.clickHistory.ClickHistoryActivity2;
import com.libtop.weituR.activity.main.dto.DocBean;
import com.libtop.weituR.activity.main.dto.ImageSliderDto;
import com.libtop.weituR.activity.main.dto.NoticeInfo;
import com.libtop.weituR.activity.main.lesson.LessonTypeListFragment;
import com.libtop.weituR.activity.main.notice.NoticeContentFragment;
import com.libtop.weituR.activity.main.notice.NoticeFragment;
import com.libtop.weituR.activity.main.notice.NoticeFragment2;
import com.libtop.weituR.activity.main.rmdbooks.MainBooksFragment;
import com.libtop.weituR.activity.main.service.ServiceListFragment;
import com.libtop.weituR.activity.search.BookDetailFragment2;
import com.libtop.weituR.activity.search.SearchActivity;
import com.libtop.weituR.activity.search.VideoPlayActivity4;
import com.libtop.weituR.activity.search.VideoPlayActivity5;
import com.libtop.weituR.activity.search.dto.SearchResult;
import com.libtop.weituR.activity.search.dynamicCardLayout.DynamicCardActivity;
import com.libtop.weituR.activity.source.AudioPlayActivity4;
import com.libtop.weituR.activity.source.PdfActivity3;
import com.libtop.weituR.base.BaseFragment;
import com.libtop.weituR.http.MapUtil;
import com.libtop.weituR.http.WeituNetwork;
import com.libtop.weituR.tool.Preference;
import com.libtop.weituR.utils.CheckUtil;
import com.libtop.weituR.utils.DateUtil;
import com.libtop.weituR.utils.PicassoLoader;
import com.libtop.weituR.widget.gridview.FixedGridView;
import com.libtop.weituR.widget.listview.ChangeListView;
import com.zbar.lib.CaptureActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnItemClick;
import cn.lightsky.infiniteindicator.InfiniteIndicator;
import cn.lightsky.infiniteindicator.page.OnPageClickListener;
import cn.lightsky.infiniteindicator.page.Page;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * <p>
 * Title: MainFragment2.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * CreateTime：16/6/12
 * </p>
 *
 * @author 陆
 * @version common v1.0
 */
public class MainFragment2 extends BaseFragment implements ViewPager.OnPageChangeListener, OnPageClickListener {
    //    @Bind(R.id.title)
//    TextView mLibText;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.scroll)
    ScrollView mScroll;
    private MainIconAdapter mAdapter;
    @Bind(R.id.grid_view)
    FixedGridView mGrid;
    @Bind(R.id.infinite_anim_circle)
    InfiniteIndicator mAnimLineIndicator;
    @Bind(R.id.list_view)
    ChangeListView changeListView;
    @Bind(R.id.tv_school)
    TextView tvSchool;

    @Bind(R.id.news_more_text)
    TextView newsMoreText;
//    @Bind(R.id.books_favorite_more_text)
//    TextView booksFavtMoreText;
//    @Bind(R.id.books_recommend_more_text)
//    TextView booksRmdMoreText;
//    @Bind(R.id.upload_more_text)
//    TextView uploadMoreText;


    @Bind(R.id.grid_view2)
    FixedGridView mGrid2;
    @Bind(R.id.grid_view3)
    FixedGridView mGrid3;
    @Bind(R.id.ll_news)
    LinearLayout llNews;

    NoticeAdapter noticeAdapter;
    MainImageAdapter mainImageAdapter, mainImageAdapter2;
    private ArrayList<Page> pageViews;
    private List<DocBean> uploadList = new ArrayList<DocBean>();
    private List<DocBean> bList = new ArrayList<DocBean>();

    private List<NoticeInfo> mInfos=new ArrayList<NoticeInfo>();
    private List<ImageSliderDto> slideList =new ArrayList<ImageSliderDto>();

    private CompositeSubscription _subscriptions = new CompositeSubscription();

    private final int VIDEO=1,AUDIO=2,DOC=3,PHOTO=4;

    private int newestIndex;

    private boolean isUpdateSchoolNews  = true;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_layout3;
    }

    @Override
    public void onCreation(View root) {
        initView();
//        initData();
//        testAnimLineIndicator();
//          getDetailData();
        initLoad();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                _subscriptions.clear();
                uploadList.clear();
                newestIndex = 0;
                initLoad();
            }
        });
    }

    private void initLoad() {
        loadBookRecommand();
        requestNotics();
        requestImageSlider();
        loadNewestUpload();
    }

    private void initView() {
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW);
//        swipeRefreshLayout.setEnabled(false);
        if (mAdapter == null) mAdapter = new MainIconAdapter(mContext);
        mGrid.setAdapter(mAdapter);
        mainImageAdapter = new MainImageAdapter(mContext, bList);
        mainImageAdapter2 = new MainImageAdapter(mContext, uploadList, true);
        mGrid2.setAdapter(mainImageAdapter);
        mGrid3.setAdapter(mainImageAdapter2);
        mGrid2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DocBean bean = bList.get(position);
                openBook(bean.title,bean.cover,bean.author,bean.isbn,bean.publisher);
            }
        });
        mGrid3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DocBean bean = uploadList.get(position);
                switch (bean.type){
                    case VIDEO:
                        openVideo(bean.id);
                        break;
                    case AUDIO:
                        openAudio(bean.id,bean.cover);
                        break;
                    case DOC:
                        openDoc(bean.id);
                        break;
                    case PHOTO:
                        openPhoto(bean.id);
                        break;
                }
            }
        });
        noticeAdapter = new NoticeAdapter(mContext,mInfos);
        changeListView.setAdapter(noticeAdapter);
        changeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle1 = new Bundle();
                bundle1.putString(ContentActivity.FRAG_CLS, NoticeContentFragment.class.getName());
                bundle1.putBoolean(ContentActivity.FRAG_WITH_ANIM, true);
                bundle1.putBoolean(ContentActivity.FRAG_ISBACK, false);
                bundle1.putString("id", mInfos.get(position).id);
                bundle1.putString("title", mInfos.get(position).title);
                bundle1.putString("date", DateUtil.parseToDate(mInfos.get(position).dateLine));
                mContext.startActivity(bundle1, ContentActivity.class);
            }
        });
        mScroll.smoothScrollTo(0, 0);
    }

    private void loadNewestUpload() {
//        unsubscribe();
        swipeRefreshLayout.setRefreshing(true);
        Observable<List<DocBean>> newestVideoObservable = getNewestVideoObservable();
        Observable<List<DocBean>> newestAudioObservable = getNewestAudioObservable();
        Observable<List<DocBean>> newestDocObservable = getNewestDocObservable();
        Observable<List<DocBean>> newestPhotoObservable = getNewestPhotoObservable();
        _subscriptions.add(
        Observable.concat(newestVideoObservable,newestAudioObservable,newestDocObservable,newestPhotoObservable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<DocBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        newestIndex++;
                        if(newestIndex > 3)
                            newestIndex = 0;
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onNext(List<DocBean> docBeens) {
                        newestIndex++;
                        swipeRefreshLayout.setRefreshing(false);
                        if (docBeens.isEmpty()||docBeens.size()<3){
                            return;
                        }
                        for (DocBean docBean:docBeens){
                            docBean.type = newestIndex;
                        }
                        if(newestIndex > 3)
                            newestIndex = 0;
                        uploadList.addAll(docBeens.subList(0,3));
                        mainImageAdapter2.setData(uploadList);
                        mainImageAdapter2.notifyDataSetChanged();
                    }
                })
        );

    }

    //获取最新视频
    private Observable<List<DocBean>> getNewestVideoObservable(){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("method", "mediaAlbum.latest");
        params.put("type", 1);
        String[] arrays = MapUtil.map2Parameter(params);
        return WeituNetwork.getWeituApi().getNewest(arrays[0],arrays[1],arrays[2]);
    }

    //获取最新音频
    private Observable<List<DocBean>> getNewestAudioObservable() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("method", "mediaAlbum.latest");
        params.put("type", 2);
        String[] arrays = MapUtil.map2Parameter(params);
        return WeituNetwork.getWeituApi().getNewest(arrays[0], arrays[1], arrays[2]);
    }

    //获取最新文档
    private Observable<List<DocBean>> getNewestDocObservable(){
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("method", "document.latest");
        String[] arrays = MapUtil.map2Parameter(params);
        return WeituNetwork.getWeituApi().getNewest(arrays[0],arrays[1],arrays[2]);
    }

    //获取最新图片
    private Observable<List<DocBean>> getNewestPhotoObservable(){
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("method", "imageAlbum.latest");
        String[] arrays = MapUtil.map2Parameter(params);
        return WeituNetwork.getWeituApi().getNewest(arrays[0],arrays[1],arrays[2]);
    }

    private void requestImageSlider() {
//        mLoading.show();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("method", "focus.list");
        String[] arrays = MapUtil.map2Parameter(params);
        _subscriptions.add(
                WeituNetwork.getWeituApi()
                        .getImageSlider(arrays[0],arrays[1],arrays[2])
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<List<ImageSliderDto>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.w("guanglog","error + " + e);
                            }

                            @Override
                            public void onNext(List<ImageSliderDto> imageSliderDtos) {
                                if (imageSliderDtos.isEmpty())
                                    return;
                                slideList.clear();
                                slideList = imageSliderDtos;
                                initImageSlide();
                            }
                        })
        );
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _subscriptions.clear();
    }

    private void initImageSlide(){
        initData();
        testAnimLineIndicator();
    }

    private void requestNotics(){
//        mLoading.show();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("method", "notice.list");
//        params.put("lid",mPreference.getString(Preference.SchoolCode));//lid
        params.put("lid","10564");
        String[] arrays = MapUtil.map2Parameter(params);
        _subscriptions.add(
                WeituNetwork.getWeituApi()
                        .getNoticeInfo(arrays[0],arrays[1],arrays[2])
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<List<NoticeInfo>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                setNewsGone();
                                newsMoreText.setVisibility(View.GONE);
                            }

                            @Override
                            public void onNext(List<NoticeInfo> noticeInfos) {
                                if (noticeInfos.isEmpty()) {
                                    setNewsGone();
                                    newsMoreText.setVisibility(View.GONE);
                                    return;
                                }
                                setNewsVisible();
                                mInfos.clear();
                                mInfos = noticeInfos;
                                if (mInfos.size()>3){
                                    mInfos = mInfos.subList(0,3);
                                }
                                noticeAdapter.setData(mInfos);
                            }
                        })
        );
    }

    private void setNewsVisible(){
        llNews.setVisibility(View.VISIBLE);
        newsMoreText.setVisibility(View.VISIBLE);
        changeListView.setVisibility(View.VISIBLE);
    }

    private void setNewsGone(){
        llNews.setVisibility(View.GONE);
        newsMoreText.setVisibility(View.GONE);
        changeListView.setVisibility(View.GONE);
    }

    private void openAudio(String id,String cover) {
        SearchResult result = new SearchResult();
        result.id = id;
        result.cover = cover;
        Intent intent = new Intent(mContext, AudioPlayActivity4.class);
        intent.putExtra("resultBean", new Gson().toJson(result));
        mContext.startActivity(intent);
    }

    private void openVideo(String id) {
        SearchResult result = new SearchResult();
        result.id = id;
        Intent intent = new Intent(mContext, VideoPlayActivity5.class);
        intent.putExtra("resultBean", new Gson().toJson(result));
        mContext.startActivity(intent);
    }

    private void openBook(String bookName,String cover,String author,String isbn,String publisher) {
        Bundle bundle = new Bundle();
        bundle.putString("name", bookName);
        bundle.putString("cover", cover);
        bundle.putString("auth", author);
        bundle.putString("isbn", isbn);
        bundle.putString("publisher", publisher);
        bundle.putString("school", Preference.instance(mContext)
                .getString(Preference.SchoolCode));
        bundle.putBoolean("isFromMainPage", true);
        bundle.putBoolean(ContentActivity.FRAG_ISBACK, false);
        bundle.putString(ContentActivity.FRAG_CLS, BookDetailFragment2.class.getName());
        mContext.startActivity(bundle, ContentActivity.class);
    }

    private void openPhoto(String id) {
        Bundle bundle = new Bundle();
        bundle.putString("type", "img");
        bundle.putString("id", id);
        mContext.startActivity(bundle, DynamicCardActivity.class);
    }

    private void openDoc(String id) {
        Intent intent = new Intent();
        intent.putExtra("url", "");
        intent.putExtra("doc_id", id);
        intent.setClass(mContext, PdfActivity3.class);
        mContext.startActivity(intent);
        mContext.overridePendingTransition(R.anim.zoomin,
                R.anim.alpha_outto);
    }

    private void initData() {
        if (slideList.isEmpty()){
            return;
        }
        pageViews = new ArrayList<>();
        pageViews.add(new Page("A ", slideList.get(0).poster, this));
        pageViews.add(new Page("B ", slideList.get(1).poster, this));
//        pageViews.add(new Page("C ", "https://raw.githubusercontent.com/lightSky/InfiniteIndicator/master/res/c.jpg", this));
//        pageViews.add(new Page("D ", "https://raw.githubusercontent.com/lightSky/InfiniteIndicator/master/res/d.jpg", this));

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPreference.getString(Preference.SchoolCode).equals("10564")){
            setNewsVisible();
            requestNotics();
        }else {
            setNewsGone();
        }
        mAnimLineIndicator.start();
//        mLibText.setText(Preference.instance(mContext).getString(
//                Preference.SchoolName));
    }

    @Override
    public void onPause() {
        super.onPause();
        mAnimLineIndicator.stop();
    }

    @Nullable
    @OnClick({R.id.search, R.id.container, R.id.open_clazz, R.id.spec_lesson, R.id.news, R.id.service, R.id.banner, R.id.search_top
    ,R.id.news_more_text})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open_clazz:
                Bundle bundle = new Bundle();
                bundle.putInt("from", 1);
                bundle.putString(ContentActivity.FRAG_CLS, LibraryFragment.class.getName());
                mContext.startActivity(bundle, CaptureActivity.class);
                // CaptureActivity ContentActivity
                break;
            case R.id.search_top:
                if (CheckUtil.isNull(mPreference.getString(Preference.uid))) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString(ContentActivity.FRAG_CLS, LoginFragment.class.getName());
                    mContext.startActivity(bundle1, ContentActivity.class);
                } else {
                    Intent intent = new Intent(mContext, ClickHistoryActivity2.class);
                    startActivity(intent);
                }
                break;
            case R.id.container:
                mContext.startActivity(null, SearchActivity.class);
                break;
            case R.id.spec_lesson:
                Bundle bundle5 = new Bundle();
                bundle5.putString(ContentActivity.FRAG_CLS, LessonTypeListFragment.class.getName());
                mContext.startActivity(bundle5, ContentActivity.class);
                break;
            case R.id.news:
                Bundle bundle2 = new Bundle();
                bundle2.putString(ContentActivity.FRAG_CLS, NoticeFragment.class.getName());
                mContext.startActivity(bundle2, ContentActivity.class);
                break;
            case R.id.service:
                Bundle bundle3 = new Bundle();
                bundle3.putString(ContentActivity.FRAG_CLS, ServiceListFragment.class.getName());
                mContext.startActivity(bundle3, ContentActivity.class);
                break;
            case R.id.banner:
//                UemgShare a = new UemgShare(mContext);
//                a.setImage("http://www.umeng.com/images/pic/social/integrated_3.png").setText("123").share();
                break;
            case R.id.news_more_text:
                Bundle bundle6 = new Bundle();
                bundle6.putString("method", "notice.list");
                bundle6.putString("lid", "10564");
                bundle6.putString(ContentActivity.FRAG_CLS, NoticeFragment2.class.getName());
                mContext.startActivity(bundle6, ContentActivity.class);
                break;
//            case R.id.books_recommend_more_text:
//                Bundle bundle7 = new Bundle();
//                bundle7.putString("method", "book.listRecommend");
//                bundle7.putString(ContentActivity.FRAG_CLS, MainBooksFragment.class.getName());
//                mContext.startActivity(bundle7, ContentActivity.class);
//                break;
//            case R.id.upload_more_text:
//                Bundle bundle8 = new Bundle();
//                bundle8.putString("method", "book.listRecommend");
//                bundle8.putString(ContentActivity.FRAG_CLS, NewestUploadFragment.class.getName());
//                mContext.startActivity(bundle8, ContentActivity.class);
//                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageClick(int position, Page page) {
        ImageSliderDto imageSliderDto = slideList.get(position);
        if (TextUtils.isEmpty(imageSliderDto.domain))
            return;
        if (imageSliderDto.type==1){
            switch (imageSliderDto.domain){
                case "audio-album":
                    openAudio(position);
                    break;
                case "video-album":
                    openVideo(position);
                    break;
                case "document":
                    openDoc(position);
                    break;
                case "image-album":
                    openPhoto(position);
                    break;
            }
        }else if (imageSliderDto.type == 2){
            if (TextUtils.isEmpty(imageSliderDto.url))
                return;
            Uri uri = Uri.parse(imageSliderDto.url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);

        }
    }

    private void openAudio(int position) {
        SearchResult result = new SearchResult();
        result.id = slideList.get(position).id;
//         result.cover = slideList.get(position).cover;
        Intent intent = new Intent(mContext, AudioPlayActivity4.class);
        intent.putExtra("resultBean", new Gson().toJson(result));
        mContext.startActivity(intent);
    }

    private void openVideo(int position) {
        SearchResult result = new SearchResult();
        result.id = slideList.get(position).id;
        Intent intent = new Intent(mContext, VideoPlayActivity5.class);
        intent.putExtra("resultBean", new Gson().toJson(result));
        mContext.startActivity(intent);
    }


    private void openPhoto(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("type", "img");
        bundle.putString("id", slideList.get(position).id);
        mContext.startActivity(bundle, DynamicCardActivity.class);
    }

    private void openDoc(int position) {
        Intent intent = new Intent();
        intent.putExtra("url", "");
        intent.putExtra("doc_id", slideList.get(position).id);
        intent.setClass(mContext, PdfActivity3.class);
        mContext.startActivity(intent);
        mContext.overridePendingTransition(R.anim.zoomin,
                R.anim.alpha_outto);
    }




    private void testAnimLineIndicator() {
        int windowWidth =mPreference.getInt("windowWidth",-1);
        int width ;
        if (windowWidth==-1){
            width = getWinWidth();
            mPreference.putInt("windowWidth",width);
        }else {
            width = mPreference.getInt("windowWidth");
        }
        mAnimLineIndicator.getLayoutParams().height = width * 22 / 75;
        mAnimLineIndicator.requestLayout();
//        mAnimLineIndicator.invalidate();
        mAnimLineIndicator.setImageLoader(new PicassoLoader());
        mAnimLineIndicator.addPages(pageViews);
        mAnimLineIndicator.setPosition(InfiniteIndicator.IndicatorPosition.Center_Bottom);
//        mAnimLineIndicator.setOnPageChangeListener(this);
    }

    //等到屏幕宽度
    private int getWinWidth(){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenWidth = displaymetrics.widthPixels;
//        int screenHeight = displaymetrics.heightPixels;
        return screenWidth;
    }

    @Nullable @OnItemClick(value = R.id.grid_view)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (view.getId()) {
            case R.drawable.intelligence_search://智能检索
                mContext.startActivity(null, SearchActivity.class);
                break;
            case R.drawable.read_history://历史
//                Toast.makeText(getActivity(), ContantsUtil.IS_DEVELOPING, Toast.LENGTH_SHORT).show();
//                Bundle bundle5 = new Bundle();
//                bundle5.putString(ContentActivity.FRAG_CLS, LessonTypeListFragment.class.getName());
//                mContext.startActivity(bundle5, ContentActivity.class);
                if (CheckUtil.isNull(mPreference.getString(Preference.uid))) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString(ContentActivity.FRAG_CLS, LoginFragment.class.getName());
                    mContext.startActivity(bundle1, ContentActivity.class);
                } else {
                    Bundle bundle2 = new Bundle();
                    bundle2.putString("method", "book.listRecommend");
                    bundle2.putString(ContentActivity.FRAG_CLS, MainBooksFragment.class.getName());
                    mContext.startActivity(bundle2, ContentActivity.class);
                }
                break;
//            case R.drawable.grid_icon_lent://转借
            case R.drawable.class_icon:
                Bundle bundle3 = new Bundle();
                bundle3.putString(ContentActivity.FRAG_CLS, ClassmateFragment.class.getName());
                mContext.startActivity(bundle3, ContentActivity.class);
                break;
//                Bundle bundle2 = new Bundle();
//                bundle2.putString(ContentActivity.FRAG_CLS, LentFragment.class.getName());
//                mContext.startActivity(bundle2, ContentActivity.class);
//                break;
//            case R.drawable.grid_icon_comment://点评
//                Toast.makeText(getActivity(), ContantsUtil.IS_DEVELOPING, Toast.LENGTH_SHORT).show();
//                break;
            case R.drawable.video_icon://视频
                Bundle bundle4 = new Bundle();
                bundle4.putString("method", "mediaAlbum.list");
                bundle4.putInt("type", 1);
                bundle4.putString(ContentActivity.FRAG_CLS, DelicateFragment.class.getName());
                mContext.startActivity(bundle4, ContentActivity.class);
                break;
            case R.drawable.music_icon://音频
                Bundle bundle2 = new Bundle();
                bundle2.putString("method", "mediaAlbum.list");
                bundle2.putInt("type", 2);
                bundle2.putString(ContentActivity.FRAG_CLS, DelicateFragment.class.getName());
                mContext.startActivity(bundle2, ContentActivity.class);
                break;
            case R.drawable.doc_icon://文档
                Bundle bundle = new Bundle();
                bundle.putString("method", "document.list");
                bundle.putString(ContentActivity.FRAG_CLS, DelicateFragment.class.getName());
                mContext.startActivity(bundle, ContentActivity.class);
                break;
            case R.drawable.image_icon://图库
                Bundle bundle1 = new Bundle();
                bundle1.putString("method", "imageAlbum.list");
                bundle1.putString(ContentActivity.FRAG_CLS, DelicateFragment.class.getName());
                mContext.startActivity(bundle1, ContentActivity.class);
                break;
        }
    }

    private void loadBookRecommand() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("method", "book.listRecommend");
        String[] arrays = MapUtil.map2Parameter(params);
        _subscriptions.add(
        WeituNetwork.getWeituApi()
                .getNewest(arrays[0],arrays[1],arrays[2])
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<DocBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<DocBean> docBeens) {
                        bList.clear();
                        bList = docBeens;
                        if (bList.isEmpty())
                            return;
                        if (bList.size()>6){
                            bList = bList.subList(0,6);
                        }
                        mainImageAdapter.setData(bList);
                        mainImageAdapter.notifyDataSetChanged();
                    }
                })
        );
    }

}
