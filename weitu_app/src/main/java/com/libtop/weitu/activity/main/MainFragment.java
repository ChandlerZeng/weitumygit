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
import com.libtop.weitu.activity.login.LoginFragment;
import com.libtop.weitu.activity.main.adapter.MoreSubjectAdapter;
import com.libtop.weitu.activity.main.adapter.ResourceFileAdapter;
import com.libtop.weitu.activity.main.dto.DocBean;
import com.libtop.weitu.activity.main.dto.ImageSliderDto;
import com.libtop.weitu.activity.main.rank.RankFragment;
import com.libtop.weitu.activity.main.subsubject.MoreRmdFileFragment;
import com.libtop.weitu.activity.main.subsubject.MoreSubjectFragment;
import com.libtop.weitu.activity.notice.NoticeActivity;
import com.libtop.weitu.activity.search.SearchActivity;
import com.libtop.weitu.activity.search.VideoPlayActivity2;
import com.libtop.weitu.activity.search.dto.SearchResult;
import com.libtop.weitu.activity.search.dynamicCardLayout.DynamicCardActivity;
import com.libtop.weitu.activity.source.AudioPlayActivity2;
import com.libtop.weitu.activity.source.PdfActivity2;
import com.libtop.weitu.base.BaseFragment;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.http.MapUtil;
import com.libtop.weitu.http.WeituNetwork;
import com.libtop.weitu.test.Resource;
import com.libtop.weitu.test.Subject;
import com.libtop.weitu.test.SubjectResource;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.ACache;
import com.libtop.weitu.utils.CheckUtil;
import com.libtop.weitu.utils.ContantsUtil;
import com.libtop.weitu.utils.ContextUtil;
import com.libtop.weitu.utils.PicassoLoader;
import com.libtop.weitu.widget.NetworkLoadingLayout;
import com.libtop.weitu.widget.view.GridViewForScrollView;
import com.libtop.weitu.widget.view.ListViewForScrollView;
import com.zbar.lib.CaptureActivity;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bingoogolapple.badgeview.BGABadgeImageView;
import cn.lightsky.infiniteindicator.InfiniteIndicator;
import cn.lightsky.infiniteindicator.page.OnPageClickListener;
import cn.lightsky.infiniteindicator.page.Page;
import okhttp3.Call;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


/**
 *  MainFragment.java
 */
public class MainFragment extends BaseFragment implements ViewPager.OnPageChangeListener, OnPageClickListener, NetworkLoadingLayout.OnRetryClickListner
{
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.scroll)
    ScrollView mScroll;
    @Bind(R.id.included_main_content_subject_gridview)
    GridViewForScrollView subjectGridView;
    @Bind(R.id.included_main_content_infiniteindicator)
    InfiniteIndicator mAnimLineIndicator;
    @Bind(R.id.included_main_content_resource_listview)
    ListViewForScrollView resourceListView;
    @Bind(R.id.open_alarm)
    BGABadgeImageView noticeIv;
    @Bind(R.id.edit)
    EditText editText;
    @Bind(R.id.included_main_content_category_textview)
    TextView classigyText;
    @Bind(R.id.included_main_content_ranklist_textview)
    TextView rankText;
    @Bind(R.id.included_main_content_more_subject_textview)
    TextView subjectMore;
    @Bind(R.id.included_main_content_more_resource_textview)
    TextView fileMore;
    @Bind(R.id.ll_news)
    LinearLayout llNews;
    @Bind(R.id.networkloadinglayout)
    NetworkLoadingLayout networkLoadingLayout;

    ResourceFileAdapter resourceFileAdapter;
    MoreSubjectAdapter moreSubjectAdapter;
    private ArrayList<Page> pageViews;
    private List<DocBean> bList = new ArrayList<DocBean>();

    private List<Resource> reourceList = new ArrayList<>();
    private List<Subject> subjectList = new ArrayList<>();
    private List<ImageSliderDto> slideList = new ArrayList<ImageSliderDto>();

    private CompositeSubscription _subscriptions = new CompositeSubscription();

    private ACache mCache;

    private boolean isFirstIn = true;
    private boolean isRefreshed = false;


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


    private void preInitData()
    {
        mCache = ACache.get(mContext);
    }


    private void initView()
    {
        moreSubjectAdapter = new MoreSubjectAdapter(mContext, subjectList);
        resourceFileAdapter = new ResourceFileAdapter(mContext, reourceList);
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW);
        if (isFirstIn)
        {
            isFirstIn = false;
            networkLoadingLayout.showLoading();
            reloadAllData();
        }
        swipeRefreshLayout.setRefreshing(false);
        networkLoadingLayout.setOnRetryClickListner(this);
        subjectGridView.setAdapter(moreSubjectAdapter);
        resourceListView.setAdapter(resourceFileAdapter);
        subjectGridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Subject subject = subjectList.get(position);
                Intent intent = new Intent(mContext, SubjectDetailActivity.class);
                intent.putExtra("cover", subject.cover);
                startActivity(intent);
            }
        });
        resourceListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Resource resource = reourceList.get(position);
                //                openBook(resource.name, resource.cover, resource.uploader_name, "9787504444622", "中国商业出版社,2001");//TODO
                ContextUtil.openResourceByType(mContext, resource.type, resource.rid);
            }
        });
        mScroll.smoothScrollTo(0, 0);
        swipeRefreshLayout.setOnRefreshListener(swipeOnRefreshListener);
    }


    private void reloadAllData()
    {
        requestImageSlider();
        requestSubject();
        loadResourceFile();
        swipeRefreshLayout.setRefreshing(false);
    }


    private void updateNoticeBadge(int newCount)
    {
        if (newCount > 1)
        {
            noticeIv.showTextBadge("13");
        }
        else
        {
            noticeIv.hiddenBadge();
        }
    }


    private void requestImageSlider()
    {
        List<ImageSliderDto> imageSliderDtos = (List<ImageSliderDto>) mCache.getAsObject("imageSliderDtos");
        if (imageSliderDtos != null && !imageSliderDtos.isEmpty())
        {
            handleImageSlideResult(imageSliderDtos);
        }
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
                mCache.put("imageSliderDtos", (Serializable) imageSliderDtos);
                handleImageSlideResult(imageSliderDtos);
            }
        }));
    }


    private void handleImageSlideResult(List<ImageSliderDto> imageSliderDtos)
    {
        if (imageSliderDtos.isEmpty())
        {
            return;
        }
        slideList.clear();
        slideList = imageSliderDtos;
        initImageSlide();
    }


    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        _subscriptions.clear();
    }


    private void initImageSlide()
    {
        initData();
        testAnimLineIndicator();
    }


    private void initData()
    {
        if (slideList.isEmpty())
        {
            return;
        }
        pageViews = new ArrayList<>();
        pageViews.add(new Page("A ", slideList.get(0).poster, this));
        pageViews.add(new Page("B ", slideList.get(1).poster, this));

    }


    @Override
    public void onResume()
    {
        super.onResume();
        mAnimLineIndicator.start();
    }


    @Override
    public void onPause()
    {
        super.onPause();
        mAnimLineIndicator.stop();
    }


    @Nullable
    @OnClick({R.id.open_alarm, R.id.open_clazz, R.id.edit, R.id.included_main_content_category_textview, R.id.included_main_content_ranklist_textview, R.id.included_main_content_more_subject_textview, R.id.included_main_content_more_resource_textview})
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.open_alarm:
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

            case R.id.open_clazz:
                Bundle bundle = new Bundle();
                bundle.putInt("from", 1);
                bundle.putString(ContentActivity.FRAG_CLS, LibraryFragment.class.getName());
                mContext.startActivity(bundle, CaptureActivity.class);
                break;

            case R.id.edit:
                mContext.startActivity(null, SearchActivity.class);
                break;

            case R.id.included_main_content_category_textview:
                Bundle bundle2 = new Bundle();
                bundle2.putString(ContentActivity.FRAG_CLS, ClassifyFragment.class.getName());
                mContext.startActivity(bundle2, ContentActivity.class);
                break;

            case R.id.included_main_content_ranklist_textview:
                Bundle bundle3 = new Bundle();
                bundle3.putString(ContentActivity.FRAG_CLS, RankFragment.class.getName());
                mContext.startActivity(bundle3, ContentActivity.class);
                break;

            case R.id.included_main_content_more_subject_textview:
                Bundle bundle4 = new Bundle();
                bundle4.putString(ContentActivity.FRAG_CLS, MoreSubjectFragment.class.getName());
                mContext.startActivity(bundle4, ContentActivity.class);
                break;

            case R.id.included_main_content_more_resource_textview:
                Bundle bundle5 = new Bundle();
                bundle5.putString(ContentActivity.FRAG_CLS, MoreRmdFileFragment.class.getName());
                mContext.startActivity(bundle5, ContentActivity.class);
                break;
        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
    {

    }


    @Override
    public void onPageSelected(int position)
    {

    }


    @Override
    public void onPageScrollStateChanged(int state)
    {

    }


    @Override
    public void onPageClick(int position, Page page)
    {
        ImageSliderDto imageSliderDto = slideList.get(position);
        if (TextUtils.isEmpty(imageSliderDto.domain))
        {
            return;
        }
        if (imageSliderDto.type == 1)
        {
            switch (imageSliderDto.domain)
            {
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
        }
        else if (imageSliderDto.type == 2)
        {
            if (TextUtils.isEmpty(imageSliderDto.url))
            {
                return;
            }
            Uri uri = Uri.parse(imageSliderDto.url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);

        }
    }


    private void openAudio(int position)
    {
        SearchResult result = new SearchResult();
        result.id = slideList.get(position).id;
        Intent intent = new Intent(mContext, AudioPlayActivity2.class);
        intent.putExtra("resultBean", new Gson().toJson(result));
        mContext.startActivity(intent);
    }


    private void openVideo(int position)
    {
        SearchResult result = new SearchResult();
        result.id = slideList.get(position).tid;
        Intent intent = new Intent(mContext, VideoPlayActivity2.class);
        intent.putExtra("resultBean", new Gson().toJson(result));
        mContext.startActivity(intent);
    }


    private void openPhoto(int position)
    {
        Bundle bundle = new Bundle();
        bundle.putString("type", "img");
        bundle.putString("id", slideList.get(position).id);
        mContext.startActivity(bundle, DynamicCardActivity.class);
    }


    private void openDoc(int position)
    {
        Intent intent = new Intent();
        intent.putExtra("url", "");
        intent.putExtra("doc_id", slideList.get(position).id);
        intent.setClass(mContext, PdfActivity2.class);
        mContext.startActivity(intent);
        mContext.overridePendingTransition(R.anim.zoomin, R.anim.alpha_outto);
    }


    private void testAnimLineIndicator()
    {
        int windowWidth = mPreference.getInt("windowWidth", -1);
        int width;
        if (windowWidth == -1)
        {
            width = getWinWidth();
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


    //等到屏幕宽度
    private int getWinWidth()
    {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenWidth = displaymetrics.widthPixels;
        return screenWidth;
    }


    private void requestSubject()
    {
        final List<Subject> subjectLists = (List<Subject>) mCache.getAsObject("subjectList");
        if (subjectLists != null && !subjectLists.isEmpty())
        {
            handleSubjectResult(subjectLists);
            networkLoadingLayout.dismiss();
        }
        String api = "/find/subject/recommend/top";
        HttpRequest.newLoad(ContantsUtil.API_FAKE_HOST_PUBLIC + api, null).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {
                if (subjectLists != null && !subjectLists.isEmpty())
                {

                }
                else
                {
                    networkLoadingLayout.showLoadFailAndRetryPrompt();
                }
            }


            @Override
            public void onResponse(String json, int id)
            {
                if (!TextUtils.isEmpty(json))
                {
                    try
                    {
                        Gson gson = new Gson();
                        SubjectResource subjectResource = gson.fromJson(json, new TypeToken<SubjectResource>()
                        {
                        }.getType());
                        List<Subject> listSub = new ArrayList<>();
                        listSub = subjectResource.subjects;
                        if (listSub.size() > 0)
                        {
                            networkLoadingLayout.dismiss();
                        }
                        else if (listSub.size() == 0 && subjectLists.size() == 0)
                        {
                            networkLoadingLayout.showEmptyPrompt();
                            return;
                        }
                        mCache.put("subjectList", (Serializable) listSub);
                        handleSubjectResult(listSub);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private void handleSubjectResult(List<Subject> subList)
    {
        subjectList.clear();
        subjectList = subList;
        if (subjectList.isEmpty())
        {
            return;
        }
        if (subjectList.size() > 4)
        {
            subjectList = subjectList.subList(0, 4);
        }
        moreSubjectAdapter.setData(subjectList);
    }


    private void loadResourceFile()
    {
        List<Resource> resourceList = (List<Resource>) mCache.getAsObject("resourceList");
        if (resourceList != null && !resourceList.isEmpty())
        {
            handleResourceFile(resourceList);
        }
        String api = "/find/resource/recommend/top";
        HttpRequest.newLoad(ContantsUtil.API_FAKE_HOST_PUBLIC + api, null).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {
            }


            @Override
            public void onResponse(String json, int id)
            {
                if (!TextUtils.isEmpty(json))
                {
                    try
                    {
                        Gson gson = new Gson();
                        SubjectResource subjectResource = gson.fromJson(json, new TypeToken<SubjectResource>()
                        {
                        }.getType());
                        List<Resource> list = new ArrayList<>();
                        list = subjectResource.resources;
                        mCache.put("resourceList", (Serializable) list);
                        handleResourceFile(list);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private void handleResourceFile(List<Resource> resources)
    {
        reourceList.clear();
        reourceList = resources;
        if (reourceList.isEmpty())
        {
            return;
        }
        if (reourceList.size() > 4)
        {
            reourceList = reourceList.subList(0, 4);
        }
        resourceFileAdapter.setData(reourceList);
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


    @Override
    public void onRetryClick(View v)
    {
        reloadAllData();
    }
}
