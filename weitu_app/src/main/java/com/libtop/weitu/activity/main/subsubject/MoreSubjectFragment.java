package com.libtop.weitu.activity.main.subsubject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.ContentFragment;
import com.libtop.weitu.activity.main.adapter.MoreSubjectAdapter;
import com.libtop.weitu.activity.main.dto.SubjectBean;
import com.libtop.weitu.config.WTConstants;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.service.WTStatisticsService;
import com.libtop.weitu.utils.CollectionUtil;
import com.libtop.weitu.utils.ContextUtil;
import com.libtop.weitu.utils.JSONUtil;
import com.libtop.weitu.widget.NetworkLoadingLayout;
import com.paging.gridview.PagingGridView;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import okhttp3.Call;


/**
 * Created by Zeng on 2016/9/7.
 */
public class MoreSubjectFragment extends ContentFragment implements NetworkLoadingLayout.OnRetryClickListner
{

    @Bind(R.id.back_btn)
    ImageView backBtn;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.sub_grid_view)
    PagingGridView subGridView;
    @Bind(R.id.networkloadinglayout)
    NetworkLoadingLayout networkLoadingLayout;

    private MoreSubjectAdapter moreSubjectAdapter;
    private List<SubjectBean> subjectList = new ArrayList<>();

    private boolean isFirstIn = true;
    private int pageIndex;
    private String titleText;
    private String method;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = ((ContentActivity)mContext).getCurrentExtra();
        titleText = bundle.getString("title");
        method = bundle.getString("method");
    }


    @Override
    protected int getLayoutId()
    {
        return R.layout.fragment_more_sub_layout;
    }


    @Override
    public void onCreation(View root)
    {
        initView();
    }


    private void initView() {
        if (isFirstIn)
        {
            isFirstIn = false;
            networkLoadingLayout.showLoading();
            requestSubject(1);
        }
        title.setText(titleText);
        backBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                mContext.finish();
            }
        });
        subGridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                handleEventStatistics();

                SubjectBean subjectBean = subjectList.get(position);
                subjectBean.setResourceUpdateCount(0);
                moreSubjectAdapter.setItem(position,subjectBean);
                ContextUtil.openSubjectDetail(mContext,subjectBean.getId());
            }
        });
        moreSubjectAdapter = new MoreSubjectAdapter(mContext,subjectList,subGridView);
        subGridView.setAdapter(moreSubjectAdapter);
        subGridView.setHasMoreItems(false);
        subGridView.setPagingableListener(new PagingGridView.Pagingable()
        {
            @Override
            public void onLoadMoreItems()
            {
                requestSubject(pageIndex);
            }
        });
        networkLoadingLayout.setOnRetryClickListner(this);
    }

    private void requestSubject(final int page)
    {
        Map<String,Object> map = new HashMap<>();
        map.put("page",page);
        map.put("pageSize",20);
        map.put("method",method);
        HttpRequest.loadWithMap(map).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {
                if(page==1){
                    networkLoadingLayout.showLoadFailAndRetryPrompt();
                }
            }


            @Override
            public void onResponse(String json, int id)
            {
                if (!TextUtils.isEmpty(json))
                {
                    networkLoadingLayout.dismiss();
                    pageIndex=page+1;
                    try
                    {
                        List<SubjectBean> subjectBeanList = JSONUtil.readBeanArray(json, SubjectBean.class);
                        int size = CollectionUtil.getSize(subjectBeanList);
                        boolean hasMore = (size == WTConstants.LIMIT_PAGE_SIZE_DEFAULT);
                        if (page > 1)
                        {
                            subGridView.onFinishLoading(hasMore, subjectBeanList);
                        }
                        else
                        {
                            if (size > 0)
                            {
                                networkLoadingLayout.dismiss();
                                subGridView.onFinishLoading(hasMore, subjectBeanList);
                            }
                            else
                            {
                                networkLoadingLayout.showEmptyPrompt();
                            }
                        }
                        handleSubjectResult(subjectBeanList);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void handleSubjectResult(List<SubjectBean> subList)
    {
        subjectList.addAll(subList);
        if (subjectList.isEmpty())
            return;
        moreSubjectAdapter.setData(subjectList);
    }


    private void handleEventStatistics()
    {
        switch (method)
        {
            case "subject.recommend":
                WTStatisticsService.onEvent(getActivity(), WTStatisticsService.EID_SUBJECTRECOMMEND_ITEM_CLI);
                break;

            case "subject.popular":
                WTStatisticsService.onEvent(getActivity(), WTStatisticsService.EID_SUBJECTPOPULAR_ITEM_CLI);
                break;
        }
    }

    @Override
    public void onRetryClick(View v)
    {
        requestSubject(1);
    }
}
