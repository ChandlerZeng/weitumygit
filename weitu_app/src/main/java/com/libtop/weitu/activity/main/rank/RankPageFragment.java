package com.libtop.weitu.activity.main.rank;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.main.adapter.RankPageAdapter;
import com.libtop.weitu.activity.main.dto.ResourceBean;
import com.libtop.weitu.activity.main.dto.SubjectBean;
import com.libtop.weitu.base.BaseFragment;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.test.CategoryResult;
import com.libtop.weitu.utils.ContextUtil;
import com.libtop.weitu.utils.JSONUtil;
import com.libtop.weitu.utils.ListViewUtil;
import com.libtop.weitu.widget.NetworkLoadingLayout;
import com.libtop.weitu.widget.view.XListView;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import okhttp3.Call;


/**
 * Created by Zeng on 2016/9/7.
 */
public class RankPageFragment extends BaseFragment implements NetworkLoadingLayout.OnRetryClickListner
{
    @Bind(R.id.networkloadinglayout)
    NetworkLoadingLayout networkLoadingLayout;
    @Bind(R.id.xlist)
    XListView xListView;


    private RankPageAdapter mAdapter;
    private List<CategoryResult> categoryResultList = new ArrayList<>();
    private List<SubjectBean> subjectBeanList = new ArrayList<>();
    private List<ResourceBean> resourceBeanList = new ArrayList<>();

    public static final int VIDEO = 1, AUDIO = 2, DOC = 3, PHOTO = 4, BOOK = 5;
    private String method;
    private boolean isSubject;
    private int nextPageIndex;
    private boolean hasData = false;
    private boolean isFirstIn = true;
    private boolean isRefreshed = false;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        method = bundle.getString("method","subject.popular");
        if(method.equals("subject.popular")||method.equals("subject.latest")){
            isSubject = true;
        }else {
            isSubject = false;
        }
        mAdapter = new RankPageAdapter(mContext, categoryResultList);
    }


    @Override
    protected int getLayoutId()
    {
        return R.layout.fragment_rank_page;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
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
            getData(1);
        }
        ListViewUtil.addPaddingHeader(mContext,xListView);
        xListView.setAdapter(mAdapter);
        xListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (isSubject) {
                    SubjectBean subjectBean = subjectBeanList.get(position-2);
                    ContextUtil.openSubjectDetail(mContext,subjectBean.getId());
                } else {
                    ResourceBean resource = resourceBeanList.get(position-2);
                    if(resource.getEntityType().equals("book")){
                        ContextUtil.openResourceByType(mContext, getResourceType(resource), resource.getIsbn(), true);
                    }else {
                        ContextUtil.openResourceByType(mContext, getResourceType(resource), resource.getId(), true);
                    }
                }
            }
        });


        xListView.setPullLoadEnable(false);
        xListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                isRefreshed=true;
                getData(1);
            }

            @Override
            public void onLoadMore() {
                if (hasData) {
                    getData(nextPageIndex);
                }
            }
        });
        networkLoadingLayout.setOnRetryClickListner(this);
    }

    private int getResourceType(ResourceBean resourceBean){
        if(resourceBean.getEntityType().equals("audio-album")){
            return ContextUtil.AUDIO;
        }else if(resourceBean.getEntityType().equals("video-album")){
            return ContextUtil.VIDEO;
        }else if(resourceBean.getEntityType().equals("document")){
            return ContextUtil.DOC;
        }else if(resourceBean.getEntityType().equals("book")){
            return ContextUtil.BOOK;
        }else {
            return ContextUtil.PHOTO;
        }
    }

    private void getData(final int page)
    {
        HashMap<String, Object> map = new HashMap<>();
        map.put("method", method);
        map.put("page",page);
        HttpRequest.loadWithMap(map).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if(page>1){

                }else if(!isRefreshed) {
                    networkLoadingLayout.showLoadFailAndRetryPrompt();
                }
            }


            @Override
            public void onResponse(String json, int id) {
                if (!TextUtils.isEmpty(json)) {
                    xListView.stopRefresh();
                    nextPageIndex = page+1 ;
                    if(page==1){
                        networkLoadingLayout.dismiss();
                        categoryResultList.clear();
                    }
                    if(isSubject){
                        handleSubjectResult(json,page);
                    }else {
                        handleResourceResult(json,page);
                    }
                }
            }
        });
    }

    private void handleSubjectResult(String json, int page) {
        List<SubjectBean> subjectList = JSONUtil.readBeanArray(json, SubjectBean.class);
        if(page==1){
            subjectBeanList.clear();
        }
        subjectBeanList.addAll(subjectList);
        categoryResultList.addAll(subjectList);
        if (subjectList.size() < 20) {
            hasData = false;
            xListView.setPullLoadEnable(false);
        } else {
            hasData = true;
            xListView.setPullLoadEnable(true);
        }
        if (categoryResultList.size() == 0 && page == 1) {
            networkLoadingLayout.showEmptyPrompt();
        }
        mAdapter.setData(categoryResultList);
    }

    private void handleResourceResult(String json, int page) {
        List<ResourceBean> resourceList = JSONUtil.readBeanArray(json, ResourceBean.class);
        if(page==1){
            resourceBeanList.clear();
        }
        resourceBeanList.addAll(resourceList);
        categoryResultList.addAll(resourceList);
        if (resourceList.size() < 20) {
            hasData = false;
            xListView.setPullLoadEnable(false);
        } else {
            hasData = true;
            xListView.setPullLoadEnable(true);
        }
        if (categoryResultList.size() == 0 && page == 1) {
            networkLoadingLayout.showEmptyPrompt();
        }
        mAdapter.setData(categoryResultList);
    }


    @Override
    public void onRetryClick(View v) {
        getData(1);
    }
}
