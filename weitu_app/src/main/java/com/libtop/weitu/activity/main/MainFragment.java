package com.libtop.weitu.activity.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.classify.ClassifyDetailActivity;
import com.libtop.weitu.activity.classify.ClassifyFragment;
import com.libtop.weitu.activity.classify.bean.ClassifyBean;
import com.libtop.weitu.activity.login.LoginFragment;
import com.libtop.weitu.activity.main.adapter.MainClassifyAdapter;
import com.libtop.weitu.activity.main.adapter.MoreSubjectAdapter;
import com.libtop.weitu.activity.main.adapter.ResourceFileAdapter;
import com.libtop.weitu.activity.main.dto.DocBean;
import com.libtop.weitu.activity.main.dto.ImageSliderDto;
import com.libtop.weitu.activity.main.dto.ResourceBean;
import com.libtop.weitu.activity.main.dto.SubjectBean;
import com.libtop.weitu.activity.main.subsubject.MoreSubjectFragment;
import com.libtop.weitu.activity.notice.NoticeActivity;
import com.libtop.weitu.activity.search.SearchActivity;
import com.libtop.weitu.base.BaseFragment;
import com.libtop.weitu.config.WTConstants;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.http.MapUtil;
import com.libtop.weitu.http.WeituNetwork;
import com.libtop.weitu.test.Category;
import com.libtop.weitu.test.adapter.CategoryAdapter;
import com.libtop.weitu.service.WTStatisticsService;
import com.libtop.weitu.utils.ACache;
import com.libtop.weitu.utils.CacheUtil;
import com.libtop.weitu.utils.CheckUtil;
import com.libtop.weitu.utils.CollectionUtil;
import com.libtop.weitu.utils.ContextUtil;
import com.libtop.weitu.utils.DisplayUtil;
import com.libtop.weitu.utils.JSONUtil;
import com.libtop.weitu.utils.LogUtil;
import com.libtop.weitu.utils.MessageRemindUtil;
import com.libtop.weitu.utils.Preference;
import com.libtop.weitu.widget.NetworkLoadingLayout;
import com.libtop.weitu.widget.view.HorizontalListView;
import com.libtop.weitu.widget.view.PagingListViewForScrollView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.zbar.lib.CaptureActivity;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bingoogolapple.badgeview.BGABadgeImageView;
import cn.lightsky.infiniteindicator.InfiniteIndicator;
import cn.lightsky.infiniteindicator.Loader.ImageLoader;
import cn.lightsky.infiniteindicator.page.OnPageClickListener;
import cn.lightsky.infiniteindicator.page.Page;
import okhttp3.Call;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


public class MainFragment extends BaseFragment implements OnPageClickListener
{
    private static final String CACHE_KEY_IMAGE_SLIDER = "imageSliderList";
    private static final String CACHE_KEY_CLASSIFY = "classifyList";
    private static final String CACHE_KEY_RECOMMEND_SUBJECT = "recommendSubjectList";
    private static final String CACHE_KEY_HOT_SUBJECT = "hotSubjectList";
    private static final String CACHE_KEY_RECOMMEND_RESOURCE = "recommendResourceList";

    @Bind(R.id.fragment_discover_layout_swiperefreshlayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.fragment_discover_layout_scrollview)
    ScrollView scrollView;
    @Bind(R.id.included_main_content_classify_listview)
    HorizontalListView classifyListView;
    @Bind(R.id.included_main_content_recommend_subject_listview)
    HorizontalListView recommendSubjectListView;
    @Bind(R.id.included_main_content_hot_subject_listview)
    HorizontalListView hotSubjectListView;
    @Bind(R.id.included_main_content_infiniteindicator)
    InfiniteIndicator mAnimLineIndicator;
    @Bind(R.id.included_main_content_resource_listview)
    PagingListViewForScrollView resourceListView;
    @Bind(R.id.fragment_discover_layout_notice_imageview)
    BGABadgeImageView noticeIv;
    @Bind(R.id.recommend_subject_networkloadinglayout)
    NetworkLoadingLayout rmdSubNetworkLoadingLayout;
    @Bind(R.id.hot_subject_networkloadinglayout)
    NetworkLoadingLayout hotSubNetworkLoadingLayout;
    @Bind(R.id.recommend_resource_networkloadinglayout)
    NetworkLoadingLayout rmdResNetworkLoadingLayout;
    @Bind(R.id.more_recommend_subject_list)
    ImageView moreRecommentSubjectList;
    @Bind(R.id.more_hot_subject_list)
    ImageView moreHotSubjectList;

    ResourceFileAdapter resourceFileAdapter;
    MainClassifyAdapter mainClassifyAdapter;
    MoreSubjectAdapter recommendSubjectAdapter;
    MoreSubjectAdapter hotSubjectAdapter;
    private ArrayList<Page> pageViews;
    private List<DocBean> bList = new ArrayList<DocBean>();

    //TODO
    CategoryAdapter categoryAdapter;
    List<Category> categoriesList = new ArrayList<>();

    private List<ClassifyBean> classifyList = new ArrayList<>();
    private List<ResourceBean> reourceList = new ArrayList<>();
    private List<SubjectBean> subjectList = new ArrayList<>();
    private List<SubjectBean> hotsubjectList = new ArrayList<>();
    private List<ImageSliderDto> slideList = new ArrayList<ImageSliderDto>();
    private CompositeSubscription _subscriptions = new CompositeSubscription();

    private ACache mCache;

    private boolean isFirstIn = true;
    private boolean isRefreshed = false;

    private int pageIndex;


    @Override
    protected int getLayoutId()
    {
        return R.layout.fragment_discover_layout;
    }


    @Override
    public void onCreation(View root)
    {
        preInitData();
        initView();
    }


    @Override
    public void onResume()
    {
        super.onResume();
        mAnimLineIndicator.start();
        updateNoticeBadge();
    }


    @Override
    public void onPause()
    {
        super.onPause();
        mAnimLineIndicator.stop();
    }


    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        _subscriptions.clear();
    }


    private void preInitData()
    {
        mCache = ACache.get(mContext);
    }


    private void initView()
    {
        mainClassifyAdapter = new MainClassifyAdapter(mContext,classifyList);
        recommendSubjectAdapter = new MoreSubjectAdapter(mContext, subjectList);
        hotSubjectAdapter = new MoreSubjectAdapter(mContext, hotsubjectList);
        resourceFileAdapter = new ResourceFileAdapter(mContext, reourceList);
        // TODO
        categoryAdapter = new CategoryAdapter(mContext,categoriesList);
        classifyListView.setAdapter(categoryAdapter);

        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW);
        if (isFirstIn)
        {
            isFirstIn = false;
            rmdSubNetworkLoadingLayout.showLoading();
            hotSubNetworkLoadingLayout.showLoading();
            rmdResNetworkLoadingLayout.showLoading();
            reloadAllData();
        }

        swipeRefreshLayout.setRefreshing(false);
        rmdSubNetworkLoadingLayout.setOnRetryClickListner(rmdSubOnRetryClickListener);
        hotSubNetworkLoadingLayout.setOnRetryClickListner(hotSubOnRetryClickListener);
        rmdResNetworkLoadingLayout.setOnRetryClickListner(rmdResOnRetryClickListener);

//        classifyListView.setAdapter(mainClassifyAdapter); // TODO: 2016/10/11  
        recommendSubjectListView.setAdapter(recommendSubjectAdapter);
        hotSubjectListView.setAdapter(hotSubjectAdapter);
        resourceListView.setAdapter(resourceFileAdapter);
//        classifyListView.setOnItemClickListener(classifyListViewOnItemClickListener); TODO
        recommendSubjectListView.setOnItemClickListener(recommendSubjectListViewOnItemClickListener);
        hotSubjectListView.setOnItemClickListener(hotSubjectListViewOnItemClickListener);
        resourceListView.setOnItemClickListener(resourceListViewOnItemClickListener);
        resourceListView.setHasMoreItems(false);
        resourceListView.setPagingableListener(new PagingListViewForScrollView.Pagingable() {
            @Override
            public void onLoadMoreItems() {
                loadResourceFile(pageIndex);
            }
        });

        scrollView.smoothScrollTo(0, 0);
        swipeRefreshLayout.setOnRefreshListener(swipeOnRefreshListener);
    }


    private void reloadAllData()
    {
        requestImageSlider();
//        requestClassifyInfo();
        requestClassifyFakeInfo();
        requestSubject();
        requestHotSubject();
        loadResourceFile(1);
        swipeRefreshLayout.setRefreshing(false);
    }


    public void updateNoticeBadge()
    {
        boolean hasNewDynamic = MessageRemindUtil.hasNewDynamicMessage(getActivity());
        if (hasNewDynamic)
        {
            noticeIv.showCirclePointBadge();
        }
        else
        {
            noticeIv.hiddenBadge();
        }
    }


    private void requestImageSlider()
    {
        List<ImageSliderDto> imageSliderDtos = (List<ImageSliderDto>) mCache.getAsObject(CacheUtil.getKeyWithVersionCode(CACHE_KEY_IMAGE_SLIDER));
        handleImageSlideResult(imageSliderDtos);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("method", "focus.list");
        String[] arrays = MapUtil.map2Parameter(params);

        _subscriptions.add(WeituNetwork.getWeituApi().getImageSlider(arrays[0], arrays[1], arrays[2]).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<List<ImageSliderDto>>()
        {
            @Override
            public void onCompleted()
            {
            }


            @Override
            public void onError(Throwable e)
            {
                Log.w("guanglog", "error + " + e);
            }


            @Override
            public void onNext(List<ImageSliderDto> imageSliderDtos)
            {
                mCache.put(CacheUtil.getKeyWithVersionCode(CACHE_KEY_IMAGE_SLIDER), (Serializable) imageSliderDtos);
                handleImageSlideResult(imageSliderDtos);
            }
        }));
    }


    private void handleImageSlideResult(List<ImageSliderDto> imageSliderDtos)
    {
        if (CollectionUtil.getSize(imageSliderDtos) > 0)
        {
            mAnimLineIndicator.setVisibility(View.VISIBLE);
            slideList.clear();
            slideList = imageSliderDtos;
            initImageSlide();
        }else {
            mAnimLineIndicator.setVisibility(View.GONE);
        }
    }


    private void initImageSlide()
    {
        initPageViews();
        testAnimLineIndicator();
    }


    private void initPageViews()
    {
        if (CollectionUtil.getSize(slideList) > 0)
        {
            pageViews = new ArrayList<>();
            pageViews.add(new Page("A ", slideList.get(0).poster, this));
            pageViews.add(new Page("B ", slideList.get(1).poster, this));
        }
    }


    @Override
    public void onPageClick(int position, Page page)
    {
        HashMap<String, String> map = WTStatisticsService.createMap(WTStatisticsService.KEY_INDEX, String.valueOf(position));
        WTStatisticsService.onEvent(getActivity(), WTStatisticsService.EID_HOMEPAGE_BANNER_CLI, map);

        ImageSliderDto imageSliderDto = slideList.get(position);
        if (TextUtils.isEmpty(imageSliderDto.domain))
        {
            return;
        }

        int type = imageSliderDto.type;
        if (type == 1)
        {
            ContextUtil.openResourceByType(mContext,ContextUtil.getResourceType(imageSliderDto.domain),imageSliderDto.id);
        }
        else if (type == 2)
        {
            if (!TextUtils.isEmpty(imageSliderDto.url))
            {
                Uri uri = Uri.parse(imageSliderDto.url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        }
    }


    private void testAnimLineIndicator()
    {
        int windowWidth = mPreference.getInt("windowWidth", -1);
        int width;
        if (windowWidth == -1)
        {
            width = DisplayUtil.getDeviceWidthPixels(mContext);
            mPreference.putInt("windowWidth", width);
        }
        else
        {
            width = mPreference.getInt("windowWidth");
        }

        mAnimLineIndicator.getLayoutParams().height = width * 22 / 75;
        mAnimLineIndicator.requestLayout();
        mAnimLineIndicator.setImageLoader(new PicassoLoader());
        mAnimLineIndicator.addPages(pageViews);
        mAnimLineIndicator.setPosition(InfiniteIndicator.IndicatorPosition.Center_Bottom);
    }

    //TODO
    private void requestClassifyFakeInfo()
    {
        final List<Category> classifyBeanList = (List<Category>) mCache.getAsObject(CacheUtil.getKeyWithVersionCode(CACHE_KEY_CLASSIFY));

        if (CollectionUtil.getSize(classifyBeanList) > 0)
        {
            handleCategoriesResult(classifyBeanList);
        }
        String api = "http://115.28.189.104/find/category/recommend/list";
        HttpRequest.newLoad(api).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {
            }


            @Override
            public void onResponse(String json, int id)
            {
                try
                {
                    List<Category> categories = JSONUtil.readBeanArray(new JSONObject(json), "categories", Category.class);
                    mCache.put(CacheUtil.getKeyWithVersionCode(CACHE_KEY_CLASSIFY), (Serializable) categories);
                    handleCategoriesResult(categories);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    LogUtil.e(getTag(), e.toString());
                }
            }
        });
    }
    //TODO
    private void handleCategoriesResult(List<Category> categories){
        categoriesList.clear();
        categoriesList.addAll(categories);
        if(categoriesList.isEmpty()){
            return;
        }
        categoryAdapter.replaceAll(categoriesList);
    }

    private void requestClassifyInfo()
    {
        final List<ClassifyBean> classifyBeanList = (List<ClassifyBean>) mCache.getAsObject(CacheUtil.getKeyWithVersionCode(CACHE_KEY_CLASSIFY));
        if (CollectionUtil.getSize(classifyBeanList) > 0)
        {
            handleClassifyResult(classifyBeanList);
        }
        Map<String,Object> map = new HashMap<>();
        map.put("method", "categories.root");
        HttpRequest.loadWithMap(map).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {
            }


            @Override
            public void onResponse(String json, int id)
            {
                try
                {
                    List<ClassifyBean> classifyBeen = JSONUtil.readBeanArray(json, ClassifyBean.class);
                    mCache.put(CacheUtil.getKeyWithVersionCode(CACHE_KEY_CLASSIFY), (Serializable) classifyBeen);
                    handleClassifyResult(classifyBeen);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    LogUtil.e(getTag(), e.toString());
                }
            }
        });
    }


    private void requestSubject()
    {
        final List<SubjectBean> subjectLists = (List<SubjectBean>) mCache.getAsObject(CacheUtil.getKeyWithVersionCode(CACHE_KEY_RECOMMEND_SUBJECT));
        if (CollectionUtil.getSize(subjectLists) > 0)
        {
            handleSubjectResult(subjectLists);
        }
        Map<String,Object> map = new HashMap<>();
        map.put("page",1);
//        map.put("pageSize",4);
        map.put("method","subject.recommend");
        HttpRequest.loadWithMap(map).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {
                if (CollectionUtil.getSize(subjectLists) > 0)
                {
                    //TODO
                }
                else
                {
                    rmdSubNetworkLoadingLayout.showLoadFailAndRetryPrompt();
                }
            }


            @Override
            public void onResponse(String json, int id)
            {
                try
                {
                    List<SubjectBean> subjectBeanList = JSONUtil.readBeanArray(json, SubjectBean.class);

                    if (subjectBeanList.size() == 0 && subjectLists.size() == 0)
                    {
                        rmdSubNetworkLoadingLayout.showEmptyPrompt();
                        return;
                    }

                    mCache.put(CacheUtil.getKeyWithVersionCode(CACHE_KEY_RECOMMEND_SUBJECT), (Serializable) subjectBeanList);
                    handleSubjectResult(subjectBeanList);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    LogUtil.e(getTag(), e.toString());
                }
            }
        });
    }

    private void requestHotSubject()
    {
        final List<SubjectBean> subjectLists = (List<SubjectBean>) mCache.getAsObject(CacheUtil.getKeyWithVersionCode(CACHE_KEY_HOT_SUBJECT));
        if (CollectionUtil.getSize(subjectLists) > 0)
        {
            handleHotSubjectResult(subjectLists);
        }
        Map<String,Object> map = new HashMap<>();
        map.put("page",1);
        map.put("method","subject.popular");
        HttpRequest.loadWithMap(map).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {
                if (CollectionUtil.getSize(subjectLists) > 0)
                {
                    //TODO
                }
                else
                {
                    hotSubNetworkLoadingLayout.showLoadFailAndRetryPrompt();
                }
            }


            @Override
            public void onResponse(String json, int id)
            {
                try
                {
                    List<SubjectBean> subjectBeanList = JSONUtil.readBeanArray(json, SubjectBean.class);
                    if (subjectBeanList.size() == 0 && subjectLists.size() == 0)
                    {
                        hotSubNetworkLoadingLayout.showEmptyPrompt();
                        return;
                    }

                    mCache.put(CacheUtil.getKeyWithVersionCode(CACHE_KEY_HOT_SUBJECT), (Serializable) subjectBeanList);
                    handleHotSubjectResult(subjectBeanList);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    LogUtil.e(getTag(), e.toString());
                }
            }
        });
    }

    private void loadResourceFile(final int page)
    {
        final List<ResourceBean> resourceList = (List<ResourceBean>) mCache.getAsObject(CacheUtil.getKeyWithVersionCode(CACHE_KEY_RECOMMEND_RESOURCE));
        if (CollectionUtil.getSize(resourceList) > 0)
        {
            handleResourceFile(resourceList,page);

        }
        Map<String,Object> map = new HashMap<>();
        map.put("page",page);
        map.put("method","resources.recommend");
        HttpRequest.loadWithMap(map).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {
                if (CollectionUtil.getSize(resourceList) > 0)
                {
                    //TODO
                }
                else
                {
                    rmdResNetworkLoadingLayout.showLoadFailAndRetryPrompt();
                    resourceListView.setVisibility(View.GONE);
                }
            }


            @Override
            public void onResponse(String json, int id)
            {
                if(!json.isEmpty() && !json.equals("null")){
                    try
                    {
                        pageIndex = page + 1 ;
                        List<ResourceBean> resourceBeenLists = JSONUtil.readBeanArray(json, ResourceBean.class);
                        if (resourceBeenLists.size() > 0)
                        {
                        }
                        else if (resourceList.size() == 0 && resourceBeenLists.size() == 0)
                        {
                            hotSubNetworkLoadingLayout.showEmptyPrompt();
                            return;
                        }
                        if(page==1){
                            mCache.put(CacheUtil.getKeyWithVersionCode(CACHE_KEY_RECOMMEND_RESOURCE), (Serializable) resourceBeenLists);
                        }
                        int size = CollectionUtil.getSize(resourceBeenLists);
                        boolean hasMore = (size == WTConstants.LIMIT_PAGE_SIZE_DEFAULT);
                        if (page > 1)
                        {
                            resourceListView.onFinishLoading(hasMore, resourceBeenLists);
                        }
                        else
                        {
                            if (size > 0)
                            {
                                resourceListView.onFinishLoading(hasMore, resourceBeenLists);
                            }
                        }
                        handleResourceFile(resourceBeenLists,page);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        LogUtil.e(getTag(), e.toString());
                    }
                }
            }
        });
    }


    private void handleSubjectResult(List<SubjectBean> subList)
    {
        subjectList.clear();
        subjectList.addAll(subList);
        if (subjectList.isEmpty())
        {
            return;
        }
        rmdSubNetworkLoadingLayout.dismiss();
        recommendSubjectAdapter.replaceAll(subjectList);
    }

    private void handleHotSubjectResult(List<SubjectBean> subList)
    {
        hotsubjectList.clear();
        hotsubjectList.addAll(subList);
        if (hotsubjectList.isEmpty())
        {
            return;
        }
        hotSubNetworkLoadingLayout.dismiss();
        hotSubjectAdapter.replaceAll(hotsubjectList);
    }


    private void handleResourceFile(List<ResourceBean> resources, int page)
    {
        if(page == 1){
            reourceList.clear();
        }
        reourceList.addAll(resources);
        if (reourceList.isEmpty())
        {
            return;
        }
        resourceListView.setVisibility(View.VISIBLE);
        rmdResNetworkLoadingLayout.dismiss();
        resourceFileAdapter.setData(reourceList);
    }

    private void handleClassifyResult(List<ClassifyBean> classifyBeen)
    {
        classifyList.clear();
        classifyList.addAll(classifyBeen);
        if (classifyList.isEmpty())
        {
            return;
        }
        mainClassifyAdapter.replaceAll(classifyList);
    }


    private SwipeRefreshLayout.OnRefreshListener swipeOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener()
    {
        @Override
        public void onRefresh()
        {
            isRefreshed = true;
            reloadAllData();
        }
    };

    //TODO
    private AdapterView.OnItemClickListener classifyListViewOnItemClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            if(position==classifyList.size()-1){
                Bundle bundle = new Bundle();
                bundle.putString(ContentActivity.FRAG_CLS, ClassifyFragment.class.getName());
                mContext.startActivity(bundle, ContentActivity.class);
            }else {
                ClassifyBean classifyBean = classifyList.get(position);

                HashMap<String, String> map = WTStatisticsService.createMap(WTStatisticsService.KEY_CATEGORY, classifyBean.name);
                WTStatisticsService.onEvent(getActivity(), WTStatisticsService.EID_HOMEPAGE_CATEGORY_ITEM_CLI, map);

                mainClassifyAdapter.setItem(position,classifyBean);
                Intent intent = new Intent(mContext, ClassifyDetailActivity.class);
                intent.putExtra("code", classifyBean.code);
                intent.putExtra("name", classifyBean.name);
                startActivity(intent);
            }
        }
    };


    private AdapterView.OnItemClickListener recommendSubjectListViewOnItemClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            HashMap<String, String> map = WTStatisticsService.createMap(WTStatisticsService.KEY_INDEX, String.valueOf(position));
            WTStatisticsService.onEvent(getActivity(), WTStatisticsService.EID_HOMEPAGE_SUBJECTRECOMMEND_ITEM_CLI, map);

            SubjectBean subjectBean = subjectList.get(position);
            subjectBean.setResourceUpdateCount(0);
            recommendSubjectAdapter.setItem(position,subjectBean);
            ContextUtil.openSubjectDetail(mContext,subjectBean.getId());
        }
    };

    private AdapterView.OnItemClickListener hotSubjectListViewOnItemClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            HashMap<String, String> map = WTStatisticsService.createMap(WTStatisticsService.KEY_INDEX, String.valueOf(position));
            WTStatisticsService.onEvent(getActivity(), WTStatisticsService.EID_HOMEPAGE_SUBJECTPOPULAR_ITEM_CLI, map);

            SubjectBean subjectBean = hotsubjectList.get(position);
            subjectBean.setResourceUpdateCount(0);
            hotSubjectAdapter.setItem(position,subjectBean);
            ContextUtil.openSubjectDetail(mContext,subjectBean.getId());
        }
    };


    private AdapterView.OnItemClickListener resourceListViewOnItemClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            if (position < 20)  //只统计前20个
            {
                HashMap<String, String> map = WTStatisticsService.createMap(WTStatisticsService.KEY_INDEX, String.valueOf(position));
                WTStatisticsService.onEvent(getActivity(), WTStatisticsService.EID_HOMEPAGE_RESOURCERECOMMEND_ITEM_CLI, map);
            }

            ResourceBean resource = reourceList.get(position);
            ContextUtil.openResource(mContext, resource, false);
        }
    };


    private NetworkLoadingLayout.OnRetryClickListner rmdSubOnRetryClickListener = new NetworkLoadingLayout.OnRetryClickListner()
    {
        @Override
        public void onRetryClick(View v)
        {
            requestSubject();
        }
    };

    private NetworkLoadingLayout.OnRetryClickListner hotSubOnRetryClickListener = new NetworkLoadingLayout.OnRetryClickListner()
    {
        @Override
        public void onRetryClick(View v)
        {
            requestHotSubject();
        }
    };

    private NetworkLoadingLayout.OnRetryClickListner rmdResOnRetryClickListener = new NetworkLoadingLayout.OnRetryClickListner()
    {
        @Override
        public void onRetryClick(View v)
        {
            loadResourceFile(1);
        }
    };


    @Nullable
    @OnClick({
            R.id.fragment_discover_layout_notice_imageview,
            R.id.fragment_discover_layout_scan_imageview,
            R.id.fragment_discover_layout_search_edittext,
            R.id.more_recommend_subject_list,
            R.id.more_hot_subject_list
    })
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.fragment_discover_layout_notice_imageview:
                WTStatisticsService.onEvent(getActivity(), WTStatisticsService.EID_HOMEPAGE_NOTICE_CLI);

                MessageRemindUtil.clearDynamicRemind(getActivity());  //点击通知菜单时, 清除新消息提醒
                updateNoticeBadge();
                if (CheckUtil.isNull(mPreference.getString(Preference.uid)))
                {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString(ContentActivity.FRAG_CLS, LoginFragment.class.getName());
                    mContext.startActivity(bundle1, ContentActivity.class);
                }
                else
                {
                    Intent intent = new Intent(getActivity(), NoticeActivity.class);
                    startActivity(intent);
                }
                break;

            case R.id.fragment_discover_layout_scan_imageview:
                WTStatisticsService.onEvent(getActivity(), WTStatisticsService.EID_HOMEPAGE_QRCODE_CLI);

                Bundle bundle = new Bundle();
                bundle.putInt("from", 1);
                bundle.putString(ContentActivity.FRAG_CLS, LibraryFragment.class.getName());
                mContext.startActivity(bundle, CaptureActivity.class);
                break;

            case R.id.fragment_discover_layout_search_edittext:
                WTStatisticsService.onEvent(getActivity(), WTStatisticsService.EID_HOMEPAGE_SEARCH_CLI);

                mContext.startActivity(null, SearchActivity.class);
                break;

            case R.id.more_recommend_subject_list:
                WTStatisticsService.onEvent(getActivity(), WTStatisticsService.EID_HOMEPAGE_SUBJECTRECOMMEND_MORE_CLI);

                Bundle bundle4 = new Bundle();
                bundle4.putString(ContentActivity.FRAG_CLS, MoreSubjectFragment.class.getName());
                bundle4.putString("title","推荐主题");
                bundle4.putString("method","subject.recommend");
                mContext.startActivity(bundle4, ContentActivity.class);
                break;
            case R.id.more_hot_subject_list:
                WTStatisticsService.onEvent(getActivity(), WTStatisticsService.EID_HOMEPAGE_SUBJECTPOPULAR_MORE_CLI);

                Bundle bundle5 = new Bundle();
                bundle5.putString(ContentActivity.FRAG_CLS, MoreSubjectFragment.class.getName());
                bundle5.putString("title","热门主题");
                bundle5.putString("method","subject.popular");
                mContext.startActivity(bundle5, ContentActivity.class);
                break;

        }
    }


    private class PicassoLoader implements ImageLoader
    {
        @Override
        public void initLoader(Context context)
        {
        }


        @Override
        public void load(Context context, ImageView targetView, Object res)
        {
            if (res == null)
            {
                return;
            }

            Picasso picasso = Picasso.with(context);
            RequestCreator requestCreator = null;

            if (res instanceof String)
            {
                requestCreator = picasso.load((String) res);
            }
            else if (res instanceof File)
            {
                requestCreator = picasso.load((File) res);
            }
            else if (res instanceof Integer)
            {
                requestCreator = picasso.load((Integer) res);
            }

            requestCreator.fit().tag(context).into(targetView);
        }
    }
}
