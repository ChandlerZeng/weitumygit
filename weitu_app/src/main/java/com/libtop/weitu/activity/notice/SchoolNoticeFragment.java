package com.libtop.weitu.activity.notice;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.gson.reflect.TypeToken;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.base.MyBaseFragment;
import com.libtop.weitu.config.WTConstants;
import com.libtop.weitu.config.network.APIAddress;
import com.libtop.weitu.test.News;
import com.libtop.weitu.utils.CollectionUtil;
import com.libtop.weitu.utils.ContextUtil;
import com.libtop.weitu.utils.DateUtil;
import com.libtop.weitu.utils.JsonUtil;
import com.libtop.weitu.viewadapter.ViewHolderHelper;
import com.libtop.weitu.widget.NetworkLoadingLayout;
import com.paging.listview.PagingBaseAdapter;
import com.paging.listview.PagingListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;


/**
 * @author Sai
 * @ClassName: SchoolNoticeFragment
 * @Description: 校内通知页
 * @date 9/13/16 14:54
 */
public class SchoolNoticeFragment extends MyBaseFragment implements NetworkLoadingLayout.OnRetryClickListner
{
    private PagingListView schoolNoticeListView;
    private NetworkLoadingLayout networkLoadingLayout;

    private int nextPageIndex;
    private boolean isFirstIn = true;
    private View rootView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        if (rootView != null)
        {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent == null)
            {
                parent.removeView(rootView);
            }
        }
        else
        {
            rootView = inflater.inflate(R.layout.fragment_notice_school_view, container, false);
            initChildView(rootView);
        }

        if (isFirstIn)
        {
            isFirstIn = false;
            networkLoadingLayout.showLoading();
            loadSchoolNotices(1);
        }

        return rootView;
    }


    private void initChildView(View view)
    {
        SchoolNoticeListViewAdapter adapter = new SchoolNoticeListViewAdapter(getActivity(), new ArrayList<News>());

        schoolNoticeListView = (PagingListView) view.findViewById(R.id.fragment_notice_school_view_paginglistview);
        schoolNoticeListView.setAdapter(adapter);
        schoolNoticeListView.setHasMoreItems(false);
        schoolNoticeListView.setOnItemClickListener(listViewOnItemClickListener);
        schoolNoticeListView.setPagingableListener(pagingableListener);

        networkLoadingLayout = (NetworkLoadingLayout) view.findViewById(R.id.networkloadinglayout);
        networkLoadingLayout.setOnRetryClickListner(this);
    }


    private void loadSchoolNotices(final int page)
    {
        OkHttpUtils.get().url(APIAddress.NOTICE_SCHOOL_LIST_URL).build().execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {
                if (page > 1)
                {
                    schoolNoticeListView.onFinishLoading(true, null);
                }
                else
                {
                    networkLoadingLayout.showLoadFailAndRetryPrompt();
                }
            }


            @Override
            public void onResponse(String response, int id)
            {
                nextPageIndex = page + 1;

                Object newsJson = JsonUtil.findObject(response, "news");
                ArrayList<News> newsList = JsonUtil.fromJson(newsJson.toString(), new TypeToken<ArrayList<News>>(){});

                int size = CollectionUtil.getSize(newsList);
                boolean hasMore = (size > WTConstants.LIMIT_PAGE_SIZE_DEFAULT);
                if (page > 1)
                {
                    schoolNoticeListView.onFinishLoading(hasMore, newsList);
                }
                else
                {
                    if (size > 0)
                    {
                        networkLoadingLayout.dismiss();
                        schoolNoticeListView.onFinishLoading(hasMore, newsList);
                    }
                    else
                    {
                        networkLoadingLayout.showEmptyPrompt();
                    }
                }
            }
        });
    }


    @Override
    public void onRetryClick(View v)
    {
        loadSchoolNotices(1);
    }


    private PagingListView.Pagingable pagingableListener = new PagingListView.Pagingable()
    {
        @Override
        public void onLoadMoreItems()
        {
            loadSchoolNotices(nextPageIndex);
        }
    };


    private AdapterView.OnItemClickListener listViewOnItemClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            News news = (News) parent.getAdapter().getItem(position);
            ContextUtil.readSchoolNoticeDetail(getActivity(), news.getId(), news.getTitle(), news.getT_create());
        }
    };


    private class SchoolNoticeListViewAdapter extends PagingBaseAdapter<News>
    {
        private Context context;


        public SchoolNoticeListViewAdapter(Context context, List<News> mDatas)
        {
            super(mDatas);
            this.context = context;
        }


        @Override
        public int getCount()
        {
            return CollectionUtil.getSize(items);
        }


        @Override
        public Object getItem(int position)
        {
            return items.get(position);
        }


        @Override
        public long getItemId(int position)
        {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            News news = (News) getItem(position);

            ViewHolderHelper helper = ViewHolderHelper.get(context, convertView, null, R.layout.listview_item_school_notice, position);

            helper.setText(R.id.listview_item_content_textview, news.getTitle());
            helper.setText(R.id.listview_item_create_time_textview, DateUtil.transformToShow(news.getT_create()));

            return helper.getConvertView();
        }
    }
}
