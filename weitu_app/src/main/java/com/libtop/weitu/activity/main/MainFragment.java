package com.libtop.weitu.activity.main;

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
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.login.LoginFragment;
import com.libtop.weitu.activity.main.adapter.MainIconAdapter;
import com.libtop.weitu.activity.main.adapter.MainImageAdapter;
import com.libtop.weitu.activity.main.adapter.NoticeAdapter;
import com.libtop.weitu.activity.main.clazz.ClassmateFragment;
import com.libtop.weitu.activity.main.clickHistory.ClickHistoryActivity;
import com.libtop.weitu.activity.main.dto.DocBean;
import com.libtop.weitu.activity.main.dto.ImageSliderDto;
import com.libtop.weitu.activity.main.dto.NoticeInfo;
import com.libtop.weitu.activity.main.notice.NoticeContentFragment;
import com.libtop.weitu.activity.main.notice.NoticeFragment;
import com.libtop.weitu.activity.main.rmdbooks.MainBooksFragment;
import com.libtop.weitu.activity.search.BookDetailFragment;
import com.libtop.weitu.activity.search.SearchActivity;
import com.libtop.weitu.activity.search.VideoPlayActivity2;
import com.libtop.weitu.activity.search.dto.SearchResult;
import com.libtop.weitu.activity.search.dynamicCardLayout.DynamicCardActivity;
import com.libtop.weitu.activity.source.AudioPlayActivity2;
import com.libtop.weitu.activity.source.PdfActivity2;
import com.libtop.weitu.base.BaseFragment;
import com.libtop.weitu.http.MapUtil;
import com.libtop.weitu.http.WeituNetwork;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.ACache;
import com.libtop.weitu.utils.CheckUtil;
import com.libtop.weitu.utils.DateUtil;
import com.libtop.weitu.utils.PicassoLoader;
import com.libtop.weitu.widget.gridview.FixedGridView;
import com.libtop.weitu.widget.listview.ChangeListView;
import com.zbar.lib.CaptureActivity;

import java.io.Serializable;
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
 * Title: MainFragment.java
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
public class MainFragment extends BaseFragment implements ViewPager.OnPageChangeListener, OnPageClickListener {
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
    private ACache mCache;

    private boolean isUpdateSchoolNews  = true;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_layout3;
    }

    @Override
    public void onCreation(View root) {
        mCache = ACache.get(mContext);
        initView();
//        initData();
//        testAnimLineIndicator();
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
                switch (bean.type) {
                    case VIDEO:
                        openVideo(bean.id);
                        break;
                    case AUDIO:
                        openAudio(bean.id, bean.cover);
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
        List<DocBean> docBeens = (List<DocBean>) mCache.getAsObject("newestLists");
        if(docBeens != null && !docBeens.isEmpty()){
            mainImageAdapter2.setData(docBeens);
            mainImageAdapter2.notifyDataSetChanged();
            uploadList = docBeens;
        }
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
                                handleNewestResult(docBeens);
                            }
                        })
        );

    }

    private void handleNewestResult(List<DocBean> docBeens) {
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
        if (uploadList.size()>=12)
            uploadList.clear();
        uploadList.addAll(docBeens.subList(0,3));
        mCache.put("newestLists", (Serializable) uploadList);
        mainImageAdapter2.setData(uploadList);
        mainImageAdapter2.notifyDataSetChanged();
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
        List<ImageSliderDto> imageSliderDtos= (List<ImageSliderDto>) mCache.getAsObject("imageSliderDtos");
        if(imageSliderDtos!=null&&!imageSliderDtos.isEmpty()){
            handleImageSlideResult(imageSliderDtos);
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("method", "focus.list");
        String[] arrays = MapUtil.map2Parameter(params);
        _subscriptions.add(
                WeituNetwork.getWeituApi()
                        .getImageSlider(arrays[0], arrays[1], arrays[2])
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<List<ImageSliderDto>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.w("guanglog", "error + " + e);
                            }

                            @Override
                            public void onNext(List<ImageSliderDto> imageSliderDtos) {
                                mCache.put("imageSliderDtos", (Serializable) imageSliderDtos);
                                handleImageSlideResult(imageSliderDtos);
                            }
                        })
        );
    }

    private void handleImageSlideResult(List<ImageSliderDto> imageSliderDtos) {
        if (imageSliderDtos.isEmpty())
            return;
        slideList.clear();
        slideList = imageSliderDtos;
        initImageSlide();
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
        List<NoticeInfo> noticeInfos= (List<NoticeInfo>) mCache.getAsObject("noticeInfos");
        if(noticeInfos!=null&&!noticeInfos.isEmpty()){
            handleNoticeResult(noticeInfos);
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("method", "notice.list");
        params.put("lid", mPreference.getString(Preference.SchoolCode));
        String[] arrays = MapUtil.map2Parameter(params);
        _subscriptions.add(
                WeituNetwork.getWeituApi()
                        .getNoticeInfo(arrays[0], arrays[1], arrays[2])
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<List<NoticeInfo>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                List<NoticeInfo> noticeInfos = (List<NoticeInfo>) mCache.getAsObject("noticeInfos");
                                if (noticeInfos == null) {
                                    setNewsGone();
                                }
                            }

                            @Override
                            public void onNext(List<NoticeInfo> noticeInfos) {
                                mCache.put("noticeInfos", (Serializable) noticeInfos);
                                handleNoticeResult(noticeInfos);
                            }
                        })
        );
    }

    private void handleNoticeResult(List<NoticeInfo> noticeInfos) {
        if (noticeInfos.isEmpty()) {
            setNewsGone();
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
        Intent intent = new Intent(mContext, AudioPlayActivity2.class);
        intent.putExtra("resultBean", new Gson().toJson(result));
        mContext.startActivity(intent);
    }

    private void openVideo(String id) {
        SearchResult result = new SearchResult();
        result.id = id;
        Intent intent = new Intent(mContext, VideoPlayActivity2.class);
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
        bundle.putString(ContentActivity.FRAG_CLS, BookDetailFragment.class.getName());
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
        intent.setClass(mContext, PdfActivity2.class);
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
    }

    @Override
    public void onPause() {
        super.onPause();
        mAnimLineIndicator.stop();
    }

    @Nullable
    @OnClick({R.id.search, R.id.container, R.id.open_clazz, R.id.banner, R.id.search_top
            ,R.id.news_more_text})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open_clazz:
                Bundle bundle = new Bundle();
                bundle.putInt("from", 1);
                bundle.putString(ContentActivity.FRAG_CLS, LibraryFragment.class.getName());
                mContext.startActivity(bundle, CaptureActivity.class);
                break;
            case R.id.search_top:
                if (CheckUtil.isNull(mPreference.getString(Preference.uid))) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString(ContentActivity.FRAG_CLS, LoginFragment.class.getName());
                    mContext.startActivity(bundle1, ContentActivity.class);
                } else {
                    Intent intent = new Intent(mContext, ClickHistoryActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.container:
                mContext.startActivity(null, SearchActivity.class);
                break;
            case R.id.banner:
                break;
            case R.id.news_more_text:
                Bundle bundle6 = new Bundle();
                bundle6.putString("method", "notice.list");
                bundle6.putString("lid", "10564");
                bundle6.putString(ContentActivity.FRAG_CLS, NoticeFragment.class.getName());
                mContext.startActivity(bundle6, ContentActivity.class);
                break;
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
        Intent intent = new Intent(mContext, AudioPlayActivity2.class);
        intent.putExtra("resultBean", new Gson().toJson(result));
        mContext.startActivity(intent);
    }

    private void openVideo(int position) {
        SearchResult result = new SearchResult();
        result.id = slideList.get(position).tid;
        Intent intent = new Intent(mContext, VideoPlayActivity2.class);
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
        intent.setClass(mContext, PdfActivity2.class);
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
        mAnimLineIndicator.setImageLoader(new PicassoLoader());
        mAnimLineIndicator.addPages(pageViews);
        mAnimLineIndicator.setPosition(InfiniteIndicator.IndicatorPosition.Center_Bottom);
    }

    //等到屏幕宽度
    private int getWinWidth(){
        DisplayMetrics displaymetrics = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenWidth = displaymetrics.widthPixels;
        return screenWidth;
    }

    @Nullable @OnItemClick(value = R.id.grid_view)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (view.getId()) {
            case R.drawable.intelligence_search://智能检索
                mContext.startActivity(null, SearchActivity.class);
                break;
            case R.drawable.book_icon://图书
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
            case R.drawable.class_icon:
                Bundle bundle3 = new Bundle();
                bundle3.putString(ContentActivity.FRAG_CLS, ClassmateFragment.class.getName());
                mContext.startActivity(bundle3, ContentActivity.class);
                break;
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
        List<DocBean> docBeans= (List<DocBean>) mCache.getAsObject("bookLists");
        if(docBeans!=null&&!docBeans.isEmpty()){
            handleBookResult(docBeans);
        }
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
                                mCache.put("bookLists",(Serializable)docBeens);
                                handleBookResult(docBeens);
                            }
                        })
        );
    }

    private void handleBookResult(List<DocBean> docBeens) {
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

}
