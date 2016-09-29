package com.libtop.weitu.activity.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.main.dto.SubjectBean;
import com.libtop.weitu.base.BaseFragment;
import com.libtop.weitu.http.MapUtil;
import com.libtop.weitu.http.WeituNetwork;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.ContextUtil;
import com.libtop.weitu.utils.ImageLoaderUtil;
import com.libtop.weitu.utils.ShowHideOnScroll;
import com.libtop.weitu.viewadapter.CommonAdapter;
import com.libtop.weitu.viewadapter.ViewHolderHelper;
import com.libtop.weitu.widget.NetworkLoadingLayout;
import com.squareup.picasso.Picasso;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;


/**
 * Created by LianTu on 2016-9-6.
 */
public class SubjectFragment extends BaseFragment implements NetworkLoadingLayout.OnRetryClickListner
{

    @Bind(R.id.gv_main_theme)
    GridView gvMainTheme;
    @Bind(R.id.fab_main_new_subject)
    RapidFloatingActionButton fabMainNewTheme;
    @Bind(R.id.networkloadinglayout)
    NetworkLoadingLayout networkLoadingLayout;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private ThemeAdapter themeAdapter;

    private int mPage = 1;

    private boolean hasMoreItems , isBottom;

    @Override
    protected int getLayoutId()
    {
        return R.layout.fragment_main_subject;
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        themeAdapter = new ThemeAdapter(mContext);
    }


    @Override
    public void onCreation(View root)
    {
        super.onCreation(root);
        initView();
        getSubjectData(true);
    }


    private void initView()
    {
        networkLoadingLayout.setOnRetryClickListner(this);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                getSubjectData(true);
            }
        });
        gvMainTheme.setAdapter(themeAdapter);
        gvMainTheme.setOnTouchListener(new ShowHideOnScroll(fabMainNewTheme));
        gvMainTheme.setOnScrollListener(scrollListener);
        gvMainTheme.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                SubjectBean subjectBean = (SubjectBean) parent.getItemAtPosition(position);
                subjectBean.setResourceUpdateCount(0);
                themeAdapter.setItem(position,subjectBean);
                ContextUtil.openSubjectDetail(mContext,subjectBean.getId());
            }
        });
    }


    private void getSubjectData(final boolean clean)
    {
        if (clean){
            networkLoadingLayout.showLoading();
            mPage = 1;
        }
        swipeRefreshLayout.setRefreshing(true);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("uid", Preference.instance(mContext).getString(Preference.uid));
        params.put("page", mPage);
        params.put("method", "subject.my");
        String[] arrays = MapUtil.map2Parameter(params);
        subscription = WeituNetwork.getWeituApi()
                .getSubjectDto(arrays[0], arrays[1], arrays[2])
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterTerminate(new Action0()
                {
                    @Override
                    public void call()
                    {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                })
                .subscribe(new Observer<List<SubjectBean>>()
                {
                    @Override
                    public void onCompleted()
                    {

                    }


                    @Override
                    public void onError(Throwable e)
                    {
                        networkLoadingLayout.showLoadFailAndRetryPrompt();
                    }


                    @Override
                    public void onNext(List<SubjectBean> subjectBeens)
                    {
                        subjectBeens.removeAll(Collections.singleton(null));
                        if (clean){
                            themeAdapter.clear();
                            networkLoadingLayout.dismiss();
                        }
                        hasMoreItems = (subjectBeens.size()>10);
                        themeAdapter.addAll(subjectBeens);
                    }
                });
    }


    @Override
    public void onRetryClick(View v)
    {
        getSubjectData(true);
    }


    private class ThemeAdapter extends CommonAdapter<SubjectBean>
    {


        public ThemeAdapter(Context context)
        {
            super(context, R.layout.item_fragment_subject);
        }


        @Override
        public void convert(ViewHolderHelper helper, SubjectBean subjectBean, int position)
        {
            ImageView themeCover = helper.getView(R.id.img_item_subject);
            ImageView newCover = helper.getView(R.id.img_item_subject_new);
            if(subjectBean!=null){
                Picasso.with(mContext).load(subjectBean.getCover())
                        .placeholder(ImageLoaderUtil.RESOURCE_ID_IMAGE_BIG)
                        .error(ImageLoaderUtil.RESOURCE_ID_IMAGE_BIG)
                        .fit()
                        .into(themeCover);

                helper.setText(R.id.tv_item_subject, subjectBean.getTitle());
            }
            if (subjectBean.getResourceUpdateCount() == 1){
                newCover.setVisibility(View.VISIBLE);
            }else {
                newCover.setVisibility(View.GONE);
            }
            helper.setText(R.id.tv_item_subject, subjectBean.getTitle());
        }
    }


    @OnClick({R.id.fab_main_new_subject})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.fab_main_new_subject:
                newSubjectClick();
                break;
        }
    }

    private AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener()
    {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState)
        {
            if (scrollState == SCROLL_STATE_IDLE && !hasMoreItems && isBottom)
                Toast.makeText(mContext,"没有更多内容",Toast.LENGTH_SHORT).show();
        }


        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
        {
            if (totalItemCount > 0)
            {
                int lastVisibleItem = firstVisibleItem + visibleItemCount;
                isBottom = (lastVisibleItem == totalItemCount);
                if (!swipeRefreshLayout.isRefreshing()&& hasMoreItems && isBottom)
                {
                    swipeRefreshLayout.setRefreshing(true);
                    mPage++;
                    getSubjectData(false);
                }
            }
        }
    };


    private void newSubjectClick()
    {
        Intent intent = new Intent(mContext, NewSubjectActivity.class);
        mContext.startActivity(intent);
    }

}
