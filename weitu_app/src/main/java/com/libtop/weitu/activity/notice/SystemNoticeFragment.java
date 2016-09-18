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
import com.libtop.weitu.config.network.APIAddress;
import com.libtop.weitu.test.SystemNotice;
import com.libtop.weitu.test.User;
import com.libtop.weitu.utils.CollectionUtil;
import com.libtop.weitu.utils.ContextUtil;
import com.libtop.weitu.utils.DateUtil;
import com.libtop.weitu.utils.ImageLoaderUtil;
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
 * @ClassName: SystemNoticeFragment
 * @Description: 系统通知页
 * @date 9/13/16 14:47
 */
public class SystemNoticeFragment extends MyBaseFragment implements NetworkLoadingLayout.OnRetryClickListner
{
    private PagingListView systemNoticeListView;
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
        SystemNoticeListViewAdapter adapter = new SystemNoticeListViewAdapter(getActivity(), new ArrayList<SystemNotice>());

        systemNoticeListView = (PagingListView) view.findViewById(R.id.fragment_notice_system_view_paginglistview);
        systemNoticeListView.setAdapter(adapter);
        systemNoticeListView.setHasMoreItems(false);
        systemNoticeListView.setOnItemClickListener(listViewOnItemClickListener);
        systemNoticeListView.setPagingableListener(pagingableListener);

        networkLoadingLayout = (NetworkLoadingLayout) view.findViewById(R.id.networkloadinglayout);
        networkLoadingLayout.setOnRetryClickListner(this);
    }


    private void loadSystemNotices(final int page)
    {
        OkHttpUtils.get().url(APIAddress.NOTICE_MY_LIST_URL).build().execute(new StringCallback()
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

                Object noticesJson = JsonUtil.findObject(response, "notices");
                ArrayList<SystemNotice> notices = JsonUtil.fromJson(noticesJson.toString(), new TypeToken<ArrayList<SystemNotice>>(){});

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

            switch (notice.getType())
            {
                case SystemNoticeConfig.NOTICE_TYPE_FOLLOW_SUBJECT:
                    ContextUtil.readSubjectDetail(getActivity());
                    break;

                case SystemNoticeConfig.NOTICE_TYPE_RESOURCE_COMMENT:
                case SystemNoticeConfig.NOTICE_TYPE_COMMENT_REPLY:
                    ContextUtil.readCommentDetail(getActivity(), notice.getExtra_id());
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
            User user = notice.getFrom_user();

            ViewHolderHelper helper = ViewHolderHelper.get(context, convertView, null, R.layout.listview_item_system_notice, position);

            View layoutView = helper.getView(R.id.listview_item_layout_ll);
            ImageView logoView = helper.getView(R.id.listview_item_logo_imageview);
            TextView userNameTv = helper.getView(R.id.listview_item_name_textview);
            TextView promptTv = helper.getView(R.id.listview_item_prompt_textview);
            TextView createTimeTv = helper.getView(R.id.listview_item_create_time_textview);
            TextView contentTv = helper.getView(R.id.listview_item_content_textview);

            layoutView.setSelected(notice.getHas_read() == 0);

            if (user != null)
            {
                ImageLoaderUtil.loadLogoImage(context, logoView, user.getLogo());
                userNameTv.setText(user.getName());
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

            createTimeTv.setText(DateUtil.transformToShow(notice.getT_create()));
            contentTv.setText(notice.getContent());

            return helper.getConvertView();
        }
    }
}
