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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.classify.ClassifyFragment;
import com.libtop.weitu.activity.main.adapter.MoreSubjectAdapter;
import com.libtop.weitu.activity.main.adapter.SubjectFileAdapter;
import com.libtop.weitu.activity.main.dto.DisplayDto;
import com.libtop.weitu.activity.main.dto.DocBean;
import com.libtop.weitu.activity.main.dto.ImageSliderDto;
import com.libtop.weitu.activity.main.rank.RankFragment;
import com.libtop.weitu.activity.main.subsubject.MoreRmdFileFragment;
import com.libtop.weitu.activity.main.subsubject.MoreSubjectFragment;
import com.libtop.weitu.activity.search.BookDetailFragment;
import com.libtop.weitu.activity.search.SearchActivity;
import com.libtop.weitu.activity.search.VideoPlayActivity2;
import com.libtop.weitu.activity.search.dto.BookDto;
import com.libtop.weitu.activity.search.dto.SearchResult;
import com.libtop.weitu.activity.search.dynamicCardLayout.DynamicCardActivity;
import com.libtop.weitu.activity.source.AudioPlayActivity2;
import com.libtop.weitu.activity.source.PdfActivity2;
import com.libtop.weitu.base.BaseFragment;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.http.MapUtil;
import com.libtop.weitu.http.WeituNetwork;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.ACache;
import com.libtop.weitu.utils.JsonUtil;
import com.libtop.weitu.utils.PicassoLoader;
import com.libtop.weitu.widget.gridview.FixedGridView;
import com.libtop.weitu.widget.listview.ChangeListView;
import com.zbar.lib.CaptureActivity;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import cn.lightsky.infiniteindicator.InfiniteIndicator;
import cn.lightsky.infiniteindicator.page.OnPageClickListener;
import cn.lightsky.infiniteindicator.page.Page;
import okhttp3.Call;
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
    @Bind(R.id.grid_view)
    FixedGridView mGrid;
    @Bind(R.id.infinite_anim_circle)
    InfiniteIndicator mAnimLineIndicator;
    @Bind(R.id.list_view)
    ChangeListView changeListView;
    @Bind(R.id.edit)
    EditText editText;
    @Bind(R.id.classify)
    TextView classigyText;
    @Bind(R.id.rank)
    TextView rankText;
    @Bind(R.id.subject_more)
    TextView subjectMore;
    @Bind(R.id.file_more)
    TextView fileMore;
    @Bind(R.id.ll_news)
    LinearLayout llNews;

    SubjectFileAdapter subjectFileAdapter;
    MoreSubjectAdapter moreSubjectAdapter;
    private ArrayList<Page> pageViews;
    private List<DocBean> bList = new ArrayList<DocBean>();

    private List<BookDto> bookDtos=new ArrayList<BookDto>();
    private List<DisplayDto> displayDtoList=new ArrayList<DisplayDto>();
    private List<ImageSliderDto> slideList =new ArrayList<ImageSliderDto>();

    private CompositeSubscription _subscriptions = new CompositeSubscription();

    private ACache mCache;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_layout3;
    }

    @Override
    public void onCreation(View root) {
        mCache = ACache.get(mContext);
        initView();
        initLoad();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initLoad();
            }
        });
    }

    private void initLoad() {
        loadSubjectRecommand();
        loadSubjectFile();
        requestImageSlider();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void initView() {
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW);
        moreSubjectAdapter = new MoreSubjectAdapter(mContext, displayDtoList);
        mGrid.setAdapter(moreSubjectAdapter);
        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                DocBean bean = bList.get(position);
//                openBook(bean.title,bean.cover,bean.author,bean.isbn,bean.publisher);
                DisplayDto dto = displayDtoList.get(position);
                openPhoto(dto.id);
            }
        });
        subjectFileAdapter = new SubjectFileAdapter(mContext,bookDtos);
        changeListView.setAdapter(subjectFileAdapter);
        changeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BookDto bookDto = bookDtos.get(position);
                openBook(bookDto.title, bookDto.cover, bookDto.author, bookDto.isbn, bookDto.publisher);
            }
        });
        mScroll.smoothScrollTo(0, 0);
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


    private void setNewsVisible(){
        llNews.setVisibility(View.VISIBLE);
        changeListView.setVisibility(View.VISIBLE);
    }

    private void setNewsGone(){
        llNews.setVisibility(View.GONE);
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
        mAnimLineIndicator.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        mAnimLineIndicator.stop();
    }

    @Nullable
    @OnClick({R.id.open_clazz, R.id.edit, R.id.banner, R.id.classify, R.id.rank, R.id.subject_more, R.id.file_more})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open_clazz:
                Bundle bundle = new Bundle();
                bundle.putInt("from", 1);
                bundle.putString(ContentActivity.FRAG_CLS, LibraryFragment.class.getName());
                mContext.startActivity(bundle, CaptureActivity.class);
                break;
            case R.id.edit:
                mContext.startActivity(null, SearchActivity.class);
                break;
            case R.id.banner:
                break;
            case R.id.classify:
                Bundle bundle2 = new Bundle();
                bundle2.putString(ContentActivity.FRAG_CLS, ClassifyFragment.class.getName());
                mContext.startActivity(bundle2, ContentActivity.class);
                break;
            case R.id.rank:
                Bundle bundle3 = new Bundle();
                bundle3.putString(ContentActivity.FRAG_CLS, RankFragment.class.getName());
                mContext.startActivity(bundle3, ContentActivity.class);
                break;
            case R.id.subject_more:
                Bundle bundle4 = new Bundle();
                bundle4.putString(ContentActivity.FRAG_CLS, MoreSubjectFragment.class.getName());
                mContext.startActivity(bundle4, ContentActivity.class);
                break;
            case R.id.file_more:
                Bundle bundle5 = new Bundle();
                bundle5.putString(ContentActivity.FRAG_CLS, MoreRmdFileFragment.class.getName());
                mContext.startActivity(bundle5, ContentActivity.class);
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


    private void loadSubjectRecommand() {
        requestImages();
//        requestBooks();
    }

    private void requestBooks(){
        List<DocBean> docBeans= (List<DocBean>) mCache.getAsObject("bookLists");
        if(docBeans!=null&&!docBeans.isEmpty()){
            handleBookResult(docBeans);
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("method", "book.listRecommend");
        String[] arrays = MapUtil.map2Parameter(params);
        _subscriptions.add(
                WeituNetwork.getWeituApi()
                        .getNewest(arrays[0], arrays[1], arrays[2])
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
                                mCache.put("bookLists", (Serializable) docBeens);
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
        if (bList.size()>4){
            bList = bList.subList(0,4);
        }
//        moreSubjectAdapter.setData(bList);
    }
    private void requestImages()
    {
        List<DisplayDto> displayDtoList1= (List<DisplayDto>) mCache.getAsObject("displayDtos");
        if(displayDtoList1!=null&&!displayDtoList1.isEmpty()){
            handleImageResult(displayDtoList1);
        }
        int page = 1;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("method", "imageAlbum.list");
        params.put("page", page);
        HttpRequest.loadWithMap(params).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }


            @Override
            public void onResponse(String json, int id) {
                if (!TextUtils.isEmpty(json)) {
                    try {
                        List<DisplayDto> listDisplayDtos = JsonUtil.fromJson(json, new TypeToken<List<DisplayDto>>() {
                        }.getType());
                        mCache.put("displayDtos", (Serializable) listDisplayDtos);
                        handleImageResult(listDisplayDtos);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void handleImageResult(List<DisplayDto> displayDtos) {
        displayDtoList.clear();
        displayDtoList = displayDtos;
        if (displayDtoList.isEmpty())
            return;
        if (displayDtoList.size()>4){
            displayDtoList = displayDtoList.subList(0,4);
        }
        moreSubjectAdapter.setData(displayDtoList);
    }

    private void loadSubjectFile(){
        final List<BookDto> bookDtos= (List<BookDto>) mCache.getAsObject("bookDtos");
        if(bookDtos!=null&&!bookDtos.isEmpty()){
            handleSubjectFile(bookDtos);
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("method", "book.listRecommend");
        params.put("lid", mPreference.getString(Preference.SchoolCode));
        String[] arrays = MapUtil.map2Parameter(params);
        _subscriptions.add(
                WeituNetwork.getWeituApi()
                        .getBookDto(arrays[0], arrays[1], arrays[2])
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<List<BookDto>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(List<BookDto> bookDtos) {
                                mCache.put("bookDtos", (Serializable) bookDtos);
                                handleSubjectFile(bookDtos);
                            }
                        })
        );
    }

    private void handleSubjectFile(List<BookDto> booklists) {
        if (booklists.isEmpty()) {
            setNewsGone();
            return;
        }
        setNewsVisible();
        bookDtos.clear();
        bookDtos = booklists;
        if (bookDtos.size()>2){
            bookDtos = bookDtos.subList(0,2);
        }
        subjectFileAdapter.setData(bookDtos);
    }

}
