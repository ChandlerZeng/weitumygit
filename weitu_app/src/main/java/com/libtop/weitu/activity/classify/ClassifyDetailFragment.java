package com.libtop.weitu.activity.classify;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.classify.adapter.ClassifyDetailAdapter;
import com.libtop.weitu.activity.classify.adapter.ClassifySubDetailAdapter;
import com.libtop.weitu.activity.classify.bean.ClassifyDetailBean;
import com.libtop.weitu.activity.classify.bean.ClassifyResultBean;
import com.libtop.weitu.activity.user.dto.CollectBean;
import com.libtop.weitu.base.BaseFragment;
import com.libtop.weitu.eventbus.MessageEvent;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.test.Resource;
import com.libtop.weitu.test.Subject;
import com.libtop.weitu.utils.ContextUtil;
import com.libtop.weitu.utils.ListViewUtil;
import com.libtop.weitu.widget.NetworkLoadingLayout;
import com.libtop.weitu.widget.view.XListView;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import okhttp3.Call;


/**
 * Created by Zeng on 2016/9/7.
 */
public class ClassifyDetailFragment extends BaseFragment implements NetworkLoadingLayout.OnRetryClickListner
{
    @Bind(R.id.networkloadinglayout)
    NetworkLoadingLayout networkLoadingLayout;
    @Bind(R.id.xlist)
    XListView xListView;


    private ClassifySubDetailAdapter subresAdapter;
    private ClassifyDetailAdapter mAdapter;
    private List<CollectBean> categoryResultList = new ArrayList<>();
    private List<Subject> subjectList = new ArrayList<>();
    private List<Resource> resourceList = new ArrayList<>();

    private String group;
    private String method;
    private int mCurPage = 1;
    private boolean hasData = false;
    private boolean isFirstIn = true;
    private boolean isRefreshed = false;
    private long code, subCode;
    private String filterString = "view";
    private List<ClassifyResultBean> mData = new ArrayList<>();



    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        mCurPage = bundle.getInt("page");
        method = bundle.getString("method");
        code = bundle.getLong("code");
        subCode = bundle.getLong("subCode");
        group = bundle.getString("group");
        EventBus.getDefault().register(this);
    }


    @Override
    protected int getLayoutId()
    {
        return R.layout.fragment_classify_detail_page;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onCreation(View root)
    {
        initView();
    }


    private void initView()
    {
        if (isFirstIn)
        {
            isFirstIn = false;
            networkLoadingLayout.showLoading();
            getData();
        }
        subresAdapter = new ClassifySubDetailAdapter(mContext, categoryResultList);
        mAdapter = new ClassifyDetailAdapter(mContext,mData);
        ListViewUtil.addPaddingHeader(mContext,xListView);
        xListView.setAdapter(mAdapter);
        xListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (group.equals("subject")) {
                    ContextUtil.openSubjectDetail(mContext,"57ea290104122049539a365b");
                //TODO ContextUtil.openSubjectDetail(mContext,mData.get(position-2).id);

                } else if (group.equals("resources")) {
                    if(mData.get(position-2).entityType.equals("document")){
                        ContextUtil.openResourceByType(mContext,ContextUtil.ENTITY_TYPE_DOC,mData.get(position-2).id);
                    }else if(mData.get(position-2).entityType.equals("image-album")){
                        ContextUtil.openResourceByType(mContext,ContextUtil.ENTITY_TYPE_AUDIO,mData.get(position-2).id);
                    }
                }
            }
        });

        xListView.setPullLoadEnable(false);
        xListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                isRefreshed = true;
                mCurPage = 1;
                getData();
            }

            @Override
            public void onLoadMore() {
                if (hasData) {
                    getData();
                }
            }
        });
        networkLoadingLayout.setOnRetryClickListner(this);
    }

    private void getData()
    {
        //        http://weitu.bookus.cn/search/categories.json?text={"label1":100000,"label2":0,"sort":"favorite","page":1,"method":"search.categories"}
        Map<String, Object> map = new HashMap<>();
        map.put("label1", code);
        map.put("label2", subCode);
        map.put("page", mCurPage);
        map.put("method", "search.categories");
        map.put("group", group);
        HttpRequest.loadWithMap(map).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (mData.isEmpty() && !isRefreshed)
                {
                    networkLoadingLayout.showLoadFailAndRetryPrompt();
//                    mListView.setVisibility(View.GONE);
                }
            }
            @Override
            public void onResponse(String json, int id) {
                if(!TextUtils.isEmpty(json) && !json.equals("null")){
                    dismissLoading();
                    xListView.stopRefresh();
                    networkLoadingLayout.dismiss();
                    if (mCurPage == 1)
                    {
                        mData.clear();
                    }
                    ClassifyDetailBean classifyDetailBean = new Gson().fromJson(json,new TypeToken<ClassifyDetailBean>(){}.getType());
                    if(classifyDetailBean.result!=null){
                        mData.addAll(classifyDetailBean.result);
                    }
                    if (classifyDetailBean.result.size() < 20)
                    {
                        hasData = false;
                        xListView.setPullLoadEnable(false);
                    }
                    else
                    {
                        hasData = true;
                        xListView.setPullLoadEnable(true);
                    }
                    mCurPage++;
                    if (mData.isEmpty()) {
                        networkLoadingLayout.showEmptyPrompt();
                    }
                    else
                    {
                        mAdapter.setNewData(mData);
                    }
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(MessageEvent event)
    {
        showLoding();
        Bundle bm = event.message;
        if(bm.getString("filterString")!=null){
            filterString = bm.getString("filterString");
        }
        if(!String.valueOf(bm.getLong("subCode")).equals("")){
            subCode = bm.getLong("subCode");
        }
        if(bm.getInt("page")!=0){
            mCurPage = bm.getInt("page");
        }
        getData();
    }

    @Override
    public void onRetryClick(View v) {
        mCurPage = 1;
        getData();
    }

}
