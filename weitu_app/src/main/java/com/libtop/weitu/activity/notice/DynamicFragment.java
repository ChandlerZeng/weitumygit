package com.libtop.weitu.activity.notice;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.base.MyBaseFragment;
import com.libtop.weitu.config.DynamicConfig;
import com.libtop.weitu.config.WTConstants;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.test.Dynamic;
import com.libtop.weitu.test.User;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.CollectionUtil;
import com.libtop.weitu.utils.ContantsUtil;
import com.libtop.weitu.utils.ContextUtil;
import com.libtop.weitu.utils.DateUtil;
import com.libtop.weitu.utils.ImageLoaderUtil;
import com.libtop.weitu.utils.JSONUtil;
import com.libtop.weitu.utils.ListViewUtil;
import com.libtop.weitu.utils.LogUtil;
import com.libtop.weitu.viewadapter.ViewHolderHelper;
import com.libtop.weitu.widget.NetworkLoadingLayout;
import com.paging.listview.PagingBaseAdapter;
import com.paging.listview.PagingListView;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;


/**
 * @author Sai
 * @ClassName: DynamicFragment
 * @Description: 动态页(系统通知)
 * @date 9/13/16 14:47
 */
public class DynamicFragment extends MyBaseFragment implements NetworkLoadingLayout.OnRetryClickListner
{
    private PagingListView dynamicListView;
    private NetworkLoadingLayout networkLoadingLayout;

    private DynamicListViewAdapter dynamicListViewAdapter;
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
            rootView = inflater.inflate(R.layout.fragment_notice_dynamic_view, container, false);
            initChildView(rootView);
        }

        if (isFirstIn)
        {
            isFirstIn = false;
            networkLoadingLayout.showLoading();
            loadDynamics(1);
        }

        return rootView;
    }


    private void initChildView(View view)
    {
        dynamicListViewAdapter = new DynamicListViewAdapter(getActivity(), new ArrayList<Dynamic>());

        dynamicListView = (PagingListView) view.findViewById(R.id.fragment_notice_dynamic_view_paginglistview);
        ListViewUtil.addPaddingHeader(getActivity(), dynamicListView);
        dynamicListView.setAdapter(dynamicListViewAdapter);
        dynamicListView.setHasMoreItems(false);
        dynamicListView.setOnItemClickListener(listViewOnItemClickListener);
        dynamicListView.setPagingableListener(pagingableListener);

        networkLoadingLayout = (NetworkLoadingLayout) view.findViewById(R.id.networkloadinglayout);
        networkLoadingLayout.setOnRetryClickListner(this);
    }


    private void loadDynamics(final int page)
    {
        Map<String, Object> map = new HashMap<>();
        map.put("uid", Preference.instance(getActivity()).getString(Preference.uid));
        map.put("page", page);
        map.put("method", "dynamic.list");

        HttpRequest.loadWithMap(map).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {
                if (page > 1)
                {
                    dynamicListView.onFinishLoading(true, null);
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

                ArrayList<Dynamic> notices = JSONUtil.readBeanArray(response, Dynamic.class);
                int size = CollectionUtil.getSize(notices);
                boolean hasMore = (size >= WTConstants.LIMIT_PAGE_SIZE_DEFAULT);
                if (page > 1)
                {
                    dynamicListView.onFinishLoading(hasMore, notices);
                }
                else
                {
                    if (size > 0)
                    {
                        networkLoadingLayout.dismiss();
                        dynamicListView.onFinishLoading(hasMore, notices);
                    }
                    else
                    {
                        networkLoadingLayout.showEmptyPrompt();
                    }
                }
            }
        });
    }


    private void setDynamicMarkRead(String id, final int position)
    {
        Map<String, Object> map = new HashMap<>();
        map.put("uid", Preference.instance(getActivity()).getString(Preference.uid));
        map.put("id", id);
        map.put("method", "dynamic.read");

        HttpRequest.loadWithMap(map).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {
                LogUtil.e(getThis(), e.toString());
            }


            @Override
            public void onResponse(String response, int id)
            {
                Integer code = JSONUtil.getInt(response, "code");
                if (code != null && code == 1)
                {
                    Dynamic notice = (Dynamic) dynamicListViewAdapter.getItem(position);
                    notice.setHasRead(1);

                    dynamicListViewAdapter.setItem(position, notice);
                }
            }
        });
    }


    @Override
    public void onRetryClick(View v)
    {
        loadDynamics(1);
    }


    private PagingListView.Pagingable pagingableListener = new PagingListView.Pagingable()
    {
        @Override
        public void onLoadMoreItems()
        {
            loadDynamics(nextPageIndex);
        }
    };


    private AdapterView.OnItemClickListener listViewOnItemClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            Dynamic notice = (Dynamic) parent.getAdapter().getItem(position);
            if (notice.getHasRead() == 0)
            {
                setDynamicMarkRead(notice.getId(), position - 1);  //需减去头部的位置
            }

            switch (notice.getType())
            {
                case DynamicConfig.NOTICE_TYPE_FOLLOW_SUBJECT:
                    ContextUtil.openSubjectDetail(getActivity(), notice.getExtraId());
                    break;

                case DynamicConfig.NOTICE_TYPE_RESOURCE_COMMENT:
                case DynamicConfig.NOTICE_TYPE_COMMENT_REPLY:
                    ContextUtil.readCommentDetail(getActivity(), notice.getExtraId());
                    break;

                default:
                    break;
            }
        }
    };


    private class DynamicListViewAdapter extends PagingBaseAdapter<Dynamic>
    {
        private Context context;


        public DynamicListViewAdapter(Context context, List<Dynamic> mDatas)
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
            Dynamic dynamic = (Dynamic) getItem(position);
            User fromUser = dynamic.getFromUser();

            ViewHolderHelper helper = ViewHolderHelper.get(context, convertView, null, R.layout.listview_item_system_notice, position);

            View layoutView = helper.getView(R.id.listview_item_layout_ll);
            ImageView logoView = helper.getView(R.id.listview_item_logo_imageview);
            TextView userNameTv = helper.getView(R.id.listview_item_name_textview);
            TextView promptTv = helper.getView(R.id.listview_item_prompt_textview);
            TextView createTimeTv = helper.getView(R.id.listview_item_create_time_textview);
            TextView contentTv = helper.getView(R.id.listview_item_content_textview);

            layoutView.setSelected(dynamic.getHasRead() == 0);

            if (fromUser != null)
            {
                ImageLoaderUtil.loadLogoImage(context, logoView, ContantsUtil.getAvatarUrl(fromUser.getId()));
                userNameTv.setText(fromUser.getUsername());
            }

            promptTv.setText("");
            switch (dynamic.getType())
            {
                case DynamicConfig.NOTICE_TYPE_FOLLOW_SUBJECT:
                    break;

                case DynamicConfig.NOTICE_TYPE_RESOURCE_COMMENT:
                    promptTv.setText("评论了你");
                    break;

                case DynamicConfig.NOTICE_TYPE_COMMENT_REPLY:
                    promptTv.setText("回复了你");
                    break;
            }

            createTimeTv.setText(DateUtil.transformToShow(dynamic.getTimeline()));
            contentTv.setText(dynamic.getContent());

            return helper.getConvertView();
        }
    }
}
