package com.libtop.weitu.activity.notice;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.base.MyBaseFragment;
import com.libtop.weitu.config.SystemNoticeConfig;
import com.libtop.weitu.config.WTConstants;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.test.SystemNotice;
import com.libtop.weitu.test.User;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.CollectionUtil;
import com.libtop.weitu.utils.ContantsUtil;
import com.libtop.weitu.utils.ContextUtil;
import com.libtop.weitu.utils.DateUtil;
import com.libtop.weitu.utils.ImageLoaderUtil;
import com.libtop.weitu.utils.JsonUtil;
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
 * @ClassName: SystemNoticeFragment
 * @Description: 系统通知页
 * @date 9/13/16 14:47
 */
public class SystemNoticeFragment extends MyBaseFragment implements NetworkLoadingLayout.OnRetryClickListner
{
    private PagingListView systemNoticeListView;
    private NetworkLoadingLayout networkLoadingLayout;

    private SystemNoticeListViewAdapter systemNoticeListViewAdapter;
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
            rootView = inflater.inflate(R.layout.fragment_notice_system_view, container, false);
            initChildView(rootView);
        }

        if (isFirstIn)
        {
            isFirstIn = false;
            networkLoadingLayout.showLoading();
            loadSystemNotices(1);
        }

        return rootView;
    }


    private void initChildView(View view)
    {
        systemNoticeListViewAdapter = new SystemNoticeListViewAdapter(getActivity(), new ArrayList<SystemNotice>());

        systemNoticeListView = (PagingListView) view.findViewById(R.id.fragment_notice_system_view_paginglistview);
        ListViewUtil.addPaddingHeader(getActivity(), systemNoticeListView);
        systemNoticeListView.setAdapter(systemNoticeListViewAdapter);
        systemNoticeListView.setHasMoreItems(false);
        systemNoticeListView.setOnItemClickListener(listViewOnItemClickListener);
        systemNoticeListView.setPagingableListener(pagingableListener);

        networkLoadingLayout = (NetworkLoadingLayout) view.findViewById(R.id.networkloadinglayout);
        networkLoadingLayout.setOnRetryClickListner(this);
    }


    private void loadSystemNotices(final int page)
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
                    systemNoticeListView.onFinishLoading(true, null);
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

                ArrayList<SystemNotice> notices = JsonUtil.fromJson(response, new TypeToken<ArrayList<SystemNotice>>(){});
                int size = CollectionUtil.getSize(notices);
                boolean hasMore = (size > WTConstants.LIMIT_PAGE_SIZE_DEFAULT);
                if (page > 1)
                {
                    systemNoticeListView.onFinishLoading(hasMore, notices);
                }
                else
                {
                    if (size > 0)
                    {
                        networkLoadingLayout.dismiss();
                        systemNoticeListView.onFinishLoading(hasMore, notices);
                    }
                    else
                    {
                        networkLoadingLayout.showEmptyPrompt();
                    }
                }
            }
        });
    }


    private void setSystemNoticeMarkRead(String id, final int position)
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
                Integer code = JsonUtil.getInt(response, "code");
                if (code != null && code == 1)
                {
                    SystemNotice notice = (SystemNotice) systemNoticeListViewAdapter.getItem(position);
                    notice.setHasRead(1);

                    systemNoticeListViewAdapter.setItem(position, notice);
                }
            }
        });
    }


    @Override
    public void onRetryClick(View v)
    {
        loadSystemNotices(1);
    }


    private PagingListView.Pagingable pagingableListener = new PagingListView.Pagingable()
    {
        @Override
        public void onLoadMoreItems()
        {
            loadSystemNotices(nextPageIndex);
        }
    };


    private AdapterView.OnItemClickListener listViewOnItemClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            SystemNotice notice = (SystemNotice) parent.getAdapter().getItem(position);
            if (notice.getHasRead() == 0)
            {
                setSystemNoticeMarkRead(notice.getId(), position - 1);  //需减少头部的位置
            }

            switch (notice.getType())
            {
                case SystemNoticeConfig.NOTICE_TYPE_FOLLOW_SUBJECT:
                    ContextUtil.openSubjectDetail(getActivity(), notice.getExtraId());
                    break;

                case SystemNoticeConfig.NOTICE_TYPE_RESOURCE_COMMENT:
                case SystemNoticeConfig.NOTICE_TYPE_COMMENT_REPLY:
                    ContextUtil.readCommentDetail(getActivity(), notice.getExtraId());
                    break;

                default:
                    break;
            }
        }
    };


    private class SystemNoticeListViewAdapter extends PagingBaseAdapter<SystemNotice>
    {
        private Context context;


        public SystemNoticeListViewAdapter(Context context, List<SystemNotice> mDatas)
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
            SystemNotice notice = (SystemNotice) getItem(position);
            User user = notice.getFromUser();

            ViewHolderHelper helper = ViewHolderHelper.get(context, convertView, null, R.layout.listview_item_system_notice, position);

            View layoutView = helper.getView(R.id.listview_item_layout_ll);
            ImageView logoView = helper.getView(R.id.listview_item_logo_imageview);
            TextView userNameTv = helper.getView(R.id.listview_item_name_textview);
            TextView promptTv = helper.getView(R.id.listview_item_prompt_textview);
            TextView createTimeTv = helper.getView(R.id.listview_item_create_time_textview);
            TextView contentTv = helper.getView(R.id.listview_item_content_textview);

            layoutView.setSelected(notice.getHasRead() == 0);

            if (user != null)
            {
                ImageLoaderUtil.loadLogoImage(context, logoView, ContantsUtil.getAvatarUrl(user.getId()));
                userNameTv.setText(user.getUsername());
            }

            promptTv.setText("");
            switch (notice.getType())
            {
                case SystemNoticeConfig.NOTICE_TYPE_FOLLOW_SUBJECT:
                    break;

                case SystemNoticeConfig.NOTICE_TYPE_RESOURCE_COMMENT:
                    promptTv.setText("评论了你");
                    break;

                case SystemNoticeConfig.NOTICE_TYPE_COMMENT_REPLY:
                    promptTv.setText("回复了你");
                    break;
            }

            createTimeTv.setText(DateUtil.transformToShow(notice.getTimeline()));
            contentTv.setText(notice.getContent());

            return helper.getConvertView();
        }
    }
}
