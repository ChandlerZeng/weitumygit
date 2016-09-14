package com.libtop.weitu.activity.classify;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.ListPopupWindow;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.classify.adapter.ClassifyCheckAdapter;
import com.libtop.weitu.activity.classify.adapter.ClassifyDetailAdapter;
import com.libtop.weitu.activity.classify.adapter.ClassifySubDetailAdapter;
import com.libtop.weitu.activity.classify.bean.ClassifyBean;
import com.libtop.weitu.activity.classify.bean.ClassifyDetailBean;
import com.libtop.weitu.activity.classify.bean.ClassifyResultBean;
import com.libtop.weitu.activity.main.dto.DisplayDto;
import com.libtop.weitu.activity.search.BookDetailFragment;
import com.libtop.weitu.activity.search.SearchActivity;
import com.libtop.weitu.activity.search.VideoPlayActivity2;
import com.libtop.weitu.activity.search.dto.SearchResult;
import com.libtop.weitu.activity.search.dynamicCardLayout.DynamicCardActivity;
import com.libtop.weitu.activity.source.AudioPlayActivity2;
import com.libtop.weitu.activity.source.PdfActivity2;
import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.http.MapUtil;
import com.libtop.weitu.http.WeituNetwork;
import com.libtop.weitu.test.CategoryResult;
import com.libtop.weitu.test.HttpRequestTest;
import com.libtop.weitu.test.SubjectResource;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.ContantsUtil;
import com.libtop.weitu.utils.JsonUtil;
import com.libtop.weitu.widget.listview.XListView;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by LianTu on 2016/7/20.
 */
public class ClassifyDetailActivity extends BaseActivity
{

    @Bind(R.id.title)
    TextView mTitleText;
    @Bind(R.id.tv_sort)
    TextView mSubTitleText;
    @Bind(R.id.list)
    XListView mListView;
    @Bind(R.id.nullCo)
    View noView;
    @Bind(R.id.titlebar)
    View titleBar;
    @Bind(R.id.img_arrow)
    ImageView imgArrow;
    @Bind(R.id.radioGroup)
    RadioGroup radioGroup;

    private String type = "subject";
    private String api = "/category/subject/list";
    private String mAction;
    private int mCurentPage = 1;
    private ClassifyDetailAdapter mAdapter;
    private List<ClassifyResultBean> mData;
    private ListPopupWindow mListPop, mListFilterPop;
    private boolean hasData = false;

    private long code, subCode;
    private String filterString = "view";

    private List<ClassifyBean> lists = new ArrayList<ClassifyBean>();
    private List<CategoryResult> categoryResultList = new ArrayList<>();
    private ClassifyCheckAdapter filterCheckAdapter, classifyCheckAdapter;
    private ClassifySubDetailAdapter subresAdapter;


    public static final String VIDEO = "video-album", AUDIO = "audio-album", DOC = "document", PHOTO = "image-album", BOOK = "book";


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setInjectContentView(R.layout.activity_classify_detail);
        code = mContext.getIntent().getExtras().getLong("code");
        String name = getIntent().getExtras().getString("name");
        mTitleText.setText(name);
        mData = new ArrayList<ClassifyResultBean>();
        mAdapter = new ClassifyDetailAdapter(mContext, mData);
        subresAdapter = new ClassifySubDetailAdapter(mContext,categoryResultList);
        initPopView();
        initListView();
//        getData();
        getFakeData();
    }


    private void initListView()
    {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                radioButtonClicked(checkedId);
            }
        });
        mListView.setAdapter(mAdapter);
        mListView.setPullLoadEnable(false);
        mListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                mCurentPage = 1;
                getData();
            }


            @Override
            public void onLoadMore() {
                if (hasData) {
                    getData();
                }
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                startByType(mData.get(position - 1).entityType, position - 1);
            }
        });
        mCurentPage = 1;
    }


    private void initPopView()
    {
        classifyCheckAdapter = new ClassifyCheckAdapter(mContext, lists, false);
        mListPop = new ListPopupWindow(this);
        mListPop.setAdapter(classifyCheckAdapter);
        mListPop.setWidth(ListPopupWindow.MATCH_PARENT);
        mListPop.setHeight(ListPopupWindow.WRAP_CONTENT);
        mListPop.setAnchorView(titleBar);//设置ListPopupWindow的锚点，即关联PopupWindow的显示位置和这个锚点
        mListPop.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        mListPop.setModal(true);//设置是否是模式
        mListPop.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                mSubTitleText.setText(lists.get(position).name);
                subCode = lists.get(position).code;
                classifyCheckAdapter.setCheck(position);
                mCurentPage = 1;
                getData();
                mListPop.dismiss();
            }
        });
        mListPop.setOnDismissListener(new PopupWindow.OnDismissListener()
        {
            @Override
            public void onDismiss()
            {
                imgArrow.setImageResource(R.drawable.arrow_down);
            }
        });


        String[] filters = new String[]{"综合", "浏览数最多", "评论数最多", "收藏数最多", "最新上传"};
        List<ClassifyBean> filterList = new ArrayList<>();
        for (int i = 0; i < filters.length; i++)
        {
            ClassifyBean classifyBean = new ClassifyBean();
            classifyBean.name = filters[i];
            filterList.add(classifyBean);
        }
        filterCheckAdapter = new ClassifyCheckAdapter(mContext, filterList, true);
        mListFilterPop = new ListPopupWindow(this);
        mListFilterPop.setAdapter(filterCheckAdapter);
        mListFilterPop.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        mListFilterPop.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        mListFilterPop.setAnchorView(titleBar);//设置ListPopupWindow的锚点，即关联PopupWindow的显示位置和这个锚点
        mListFilterPop.setBackgroundDrawable(new ColorDrawable(0x99000000));
        mListFilterPop.setModal(true);//设置是否是模式
        mListFilterPop.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                setFilter(position);
                filterCheckAdapter.setCheck(position);
                mCurentPage = 1;
                getData();
                mListFilterPop.dismiss();
            }
        });
    }


    private void setFilter(int position)
    {
        switch (position)
        {
            //综合
            case 0:
                filterString = "view";
                break;
            //浏览数最多
            case 1:
                filterString = "view";
                break;
            //评论数最多
            case 2:
                filterString = "comment";
                break;
            //收藏数最多 TODO bug?
            case 3:
                filterString = "comment";
                break;
            //最新上传
            case 4:
                filterString = "timeline";
                break;
        }
    }


    private void getData()
    {
        //        http://weitu.bookus.cn/search/categories.json?text={"label1":100000,"label2":0,"sort":"favorite","page":1,"method":"search.categories"}
        unsubscribe();
        Map<String, Object> map = new HashMap<>();
        map.put("label1", code);
        map.put("label2", subCode);
        map.put("sort", filterString);
        map.put("page", mCurentPage);
        map.put("method", "search.categories");
        String[] arrays = MapUtil.map2Parameter(map);
        subscription = WeituNetwork.getWeituApi().getClassifyDetail(arrays[0], arrays[1], arrays[2]).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<ClassifyDetailBean>()
        {
            @Override
            public void onCompleted()
            {

            }


            @Override
            public void onError(Throwable e)
            {
                if (mData.isEmpty())
                {
                    noView.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                }
            }


            @Override
            public void onNext(ClassifyDetailBean classifyDetailBean)
            {
                mListView.stopRefresh();
                lists.clear();
                int size = classifyDetailBean.categories.subCategories.size();
                ClassifyBean classifyBean = new ClassifyBean();
                classifyBean.name = "全部";
                classifyBean.code = 0;
                classifyBean.countString = classifyDetailBean.categories.subCategories.get(0).name + "等" + size + "类";
                lists.add(classifyBean);
                for (ClassifyBean classifyBean1 : classifyDetailBean.categories.subCategories)
                {
                    classifyBean1.countString = "共" + classifyBean1.count + "种资源";
                }
                lists.addAll(classifyDetailBean.categories.subCategories);
                classifyCheckAdapter.setData(lists);
                classifyCheckAdapter.notifyDataSetChanged();
                if (mCurentPage == 1)
                {
                    mData.clear();
                }
                mData.addAll(classifyDetailBean.result);
                if (classifyDetailBean.result.size() < 10)
                {
                    hasData = false;
                    mListView.setPullLoadEnable(false);
                }
                else
                {
                    hasData = true;
                    mListView.setPullLoadEnable(true);
                }
                mCurentPage++;
                if (mData.isEmpty())
                {
                    noView.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                }
                else
                {
                    noView.setVisibility(View.GONE);
                    mListView.setVisibility(View.VISIBLE);
                    mAdapter.setNewData(mData);
                }
            }
        });
    }

    private void getFakeData()
    {
        //  http://192.168.0.9/category/resource/list
        Map<String, Object> map = new HashMap<>();
        map.put("type", type);
        HttpRequest.newLoad(ContantsUtil.API_FAKE_HOST_PUBLIC+api,null).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }


            @Override
            public void onResponse(String json, int id) {
                if (!TextUtils.isEmpty(json)) {
                    try {
                        Gson gson = new Gson();
                        SubjectResource data = gson.fromJson(json, new TypeToken<SubjectResource>() {
                        }.getType());
                        categoryResultList.clear();

                        if (api.equals("/category/resource/list")) {
                            categoryResultList.addAll(data.resources);
                        } else {
                            categoryResultList.addAll(data.subjects);
                        }
                        subresAdapter.setNewData(categoryResultList);
                        mListView.setAdapter(subresAdapter);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    @Nullable
    @OnClick({R.id.back_btn, R.id.commit, R.id.img_search, R.id.search_filter, R.id.title})
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.back_btn:
                mContext.finish();
                break;
            case R.id.img_search:
                searchClick();
                break;
            case R.id.search_filter:
                searchFilterClick();
                break;
            case R.id.title:
                titleClick(v);
                break;
        }
    }


    //标题点击更改分类
    private void titleClick(View v)
    {
        imgArrow.setImageResource(R.drawable.arrow_up);
        mListPop.show();
    }


    //更改筛选
    private void searchFilterClick()
    {
        mListFilterPop.show();
    }


    //点击搜索
    private void searchClick()
    {
        mContext.startActivity(null, SearchActivity.class);
    }

    private void radioButtonClicked(int checkedId){
        switch (checkedId){
            case R.id.subject:
                api = "/category/subject/list";
                getFakeData();
                break;
            case R.id.resource:
                api = "/category/resource/list";
                getFakeData();
                break;
        }
    }


    @Override
    public void onBackPressed()
    {
        mContext.finish();
    }


    private void startByType(String type, int position)
    {
        if (!TextUtils.isEmpty(type))
        {
            switch (type)
            {
                case VIDEO:
                    openVideo(position);
                    break;
                case AUDIO:
                    openAudio(position);
                case DOC:
                    openDoc(position);
                    break;
                case PHOTO:
                    openPhoto(position);
                    break;
                case BOOK:
                    openBook(position);
                    break;
            }
        }

    }


    private void openAudio(int position)
    {
        SearchResult result = new SearchResult();
        result.id = mData.get(position).id;
        result.cover = mData.get(position).cover;
        Intent intent = new Intent(mContext, AudioPlayActivity2.class);
        intent.putExtra("ClassifyResultBean", new Gson().toJson(result));
        mContext.startActivity(intent);
    }


    private void openVideo(int position)
    {
        SearchResult result = new SearchResult();
        result.id = mData.get(position).id;
        Intent intent = new Intent(mContext, VideoPlayActivity2.class);
        intent.putExtra("resultBean", new Gson().toJson(result));
        mContext.startActivity(intent);
    }


    private void openBook(int position)
    {
        Bundle bundle = new Bundle();
        bundle.putString("name", mData.get(position).title);
        bundle.putString("cover", mData.get(position).cover);
        bundle.putString("school", Preference.instance(mContext).getString(Preference.SchoolCode));
        bundle.putBoolean(ContentActivity.FRAG_ISBACK, true);
        bundle.putString(ContentActivity.FRAG_CLS, BookDetailFragment.class.getName());
        mContext.startActivity(bundle, ContentActivity.class);
    }


    private void openPhoto(int position)
    {
        Bundle bundle = new Bundle();
        bundle.putString("type", "img");
        bundle.putString("id", mData.get(position).id);
        mContext.startActivity(bundle, DynamicCardActivity.class);
    }


    private void openDoc(int position)
    {
        Intent intent = new Intent();
        intent.putExtra("url", "");
        intent.putExtra("doc_id", mData.get(position).id);
        intent.setClass(mContext, PdfActivity2.class);
        mContext.startActivity(intent);
        mContext.overridePendingTransition(R.anim.zoomin, R.anim.alpha_outto);
    }

}
