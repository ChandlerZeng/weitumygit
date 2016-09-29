package com.libtop.weitu.activity.notice;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.base.MyBaseFragment;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.test.SchoolNotice;
import com.libtop.weitu.utils.CollectionUtil;
import com.libtop.weitu.utils.ContextUtil;
import com.libtop.weitu.utils.DateUtil;
import com.libtop.weitu.utils.JSONUtil;
import com.libtop.weitu.utils.ListViewUtil;
import com.libtop.weitu.viewadapter.CommonAdapter;
import com.libtop.weitu.viewadapter.ViewHolderHelper;
import com.libtop.weitu.widget.NetworkLoadingLayout;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;


/**
 * @author Sai
 * @ClassName: SchoolNoticeFragment
 * @Description: 校内通知页
 * @date 9/13/16 14:54
 */
public class SchoolNoticeFragment extends MyBaseFragment implements NetworkLoadingLayout.OnRetryClickListner
{
    private ListView schoolNoticeListView;
    private NetworkLoadingLayout networkLoadingLayout;

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
            loadSchoolNotices();
        }

        return rootView;
    }


    private void initChildView(View view)
    {
        schoolNoticeListView = (ListView) view.findViewById(R.id.fragment_notice_school_view_listview);
        ListViewUtil.addPaddingHeader(getActivity(), schoolNoticeListView);

        networkLoadingLayout = (NetworkLoadingLayout) view.findViewById(R.id.networkloadinglayout);
        networkLoadingLayout.setOnRetryClickListner(this);
    }


    private void loadSchoolNotices()
    {
        Map<String, Object> map = new HashMap<>();
        map.put("lid", "10564");
        map.put("method", "notice.list");

        HttpRequest.loadWithMap(map).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {
                networkLoadingLayout.showLoadFailAndRetryPrompt();
            }


            @Override
            public void onResponse(String response, int id)
            {
                List<SchoolNotice> schoolNotices = JSONUtil.readBeanArray(response, SchoolNotice.class);
                List<SchoolNotice> newSchoolNotices = filterSchoolNoticesTitles(schoolNotices);

                if (CollectionUtil.getSize(newSchoolNotices) > 0)
                {
                    networkLoadingLayout.dismiss();

                    schoolNoticeListView.setAdapter(new SchoolNoticeListViewAdapter(getActivity(), newSchoolNotices));
                    schoolNoticeListView.setOnItemClickListener(listViewOnItemClickListener);
                }
                else
                {
                    networkLoadingLayout.showEmptyPrompt();
                }
            }
        });
    }


    private List<SchoolNotice> filterSchoolNoticesTitles(List<SchoolNotice> notices)
    {
        if (CollectionUtil.isEmpty(notices))
        {
            return null;
        }

        List<SchoolNotice> newNotices = new ArrayList<>();
        for (SchoolNotice notice : notices)
        {
            String title = notice.getTitle().replaceAll("\u00A0", "").trim();
            if (!TextUtils.isEmpty(title))
            {
                notice.setTitle(title);
                newNotices.add(notice);
            }
        }

        return newNotices;
    }


    @Override
    public void onRetryClick(View v)
    {
        loadSchoolNotices();
    }


    private AdapterView.OnItemClickListener listViewOnItemClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            SchoolNotice notice = (SchoolNotice) parent.getAdapter().getItem(position);
            ContextUtil.readSchoolNoticeDetail(getActivity(), notice.getId(), notice.getTitle(), notice.getDateLine());
        }
    };


    private class SchoolNoticeListViewAdapter extends CommonAdapter<SchoolNotice>
    {
        public SchoolNoticeListViewAdapter(Context context, List<SchoolNotice> mDatas)
        {
            super(context, R.layout.listview_item_school_notice, mDatas);
        }


        @Override
        public void convert(ViewHolderHelper helper, SchoolNotice schoolNotice, int position)
        {
            helper.setText(R.id.listview_item_content_textview, schoolNotice.getTitle());
            helper.setText(R.id.listview_item_create_time_textview, DateUtil.transformToShow(schoolNotice.getDateLine()));
        }
    }
}
