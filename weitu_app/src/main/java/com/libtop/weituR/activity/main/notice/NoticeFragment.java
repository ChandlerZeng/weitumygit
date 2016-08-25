package com.libtop.weituR.activity.main.notice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.libtop.weitu.R;
import com.libtop.weituR.activity.ContentActivity;
import com.libtop.weituR.activity.main.adapter.NoticeListAdapter;
import com.libtop.weituR.activity.main.dto.NoticeInfo;
import com.libtop.weituR.base.BaseFragment;
import com.libtop.weituR.http.HttpRequest;
import com.libtop.weituR.utils.DateUtil;
import com.libtop.weituR.utils.JsonUtil;
import com.libtop.weituR.widget.listview.RemakeXListView;
import com.libtop.weituR.widget.listview.XListView;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnItemClick;
import okhttp3.Call;

/**
 * Created by Administrator on 2016/1/14 0014.
 */
public class NoticeFragment extends BaseFragment {
    @Bind(R.id.title)
    TextView mTitleText;
    //    @Bind(R.id.footer_Text)
//    TextView mFooterText;
    @Bind(R.id.notice_list2)
    RemakeXListView mListView;

    private List<NoticeInfo> mInfos = new ArrayList<NoticeInfo>();
    private NoticeListAdapter mAdapter;
    private int mCurPage = 1;
    private boolean hasData = true;
    private boolean isInitiated = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new NoticeListAdapter(mContext, mInfos);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_notice_list2;
    }

    @Override
    public void onCreation(View root) {
        initView();
        requestNotics();
    }

    private void initView() {
        mTitleText.setText(R.string.news_list);
        mListView.setAdapter(mAdapter);
        mListView.setPullLoadEnable(false);
//        mListView.setOnScrollListener(this);
        mListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                mCurPage = 1;
                requestNotics();
            }

            @Override
            public void onLoadMore() {
                if (hasData) {
                    requestNotics();
                }
            }
        });
        mCurPage = 1;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        mContext.finish();
    }

    @Nullable
    @OnClick(R.id.back_btn)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                onBackPressed();
                break;
        }
    }

    @Nullable
    @OnItemClick(value = R.id.notice_list2)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //显示新闻详情
        Bundle bundle1 = new Bundle();
        bundle1.putString(ContentActivity.FRAG_CLS, NoticeContentFragment.class.getName());
        bundle1.putBoolean(ContentActivity.FRAG_WITH_ANIM, true);
        bundle1.putBoolean(ContentActivity.FRAG_ISBACK, true);
        bundle1.putString("id", mInfos.get(position - 1).id);
        bundle1.putString("title", mInfos.get(position - 1).title);
        bundle1.putString("date", DateUtil.parseToDate(mInfos.get(position - 1).dateLine + 86400000));
        mContext.startActivity(bundle1, ContentActivity.class);
    }

    private void requestNotics() {
        if (!isInitiated)
            showLoding();
        isInitiated = true;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("page", mCurPage);
        params.put("method", "notice.list");
        params.put("lid", "10564");
        HttpRequest.loadWithMapSec(params, new HttpRequest.CallBackSec() {
            @Override
            public void onError(Call call, Exception e, int id) {
                dismissLoading();
                showToast("无法连接服务器，请检查网络");
                mListView.stopRefresh();
            }

            @Override
            public void onResponse(String json, int id) {
                dismissLoading();
                if (TextUtils.isEmpty(json)) {
                    showToast("没有相关数据");
                    return;
                }
                mListView.stopRefresh();
                mInfos.clear();
                List<NoticeInfo> lists = JsonUtil.fromJson(json, new TypeToken<List<NoticeInfo>>() {
                }.getType());
                List<NoticeInfo> infoLists=new ArrayList<NoticeInfo>();
                /*for (NoticeInfo info : lists) {
                    String info1 = info.title.replaceAll(" ", "").trim();
                    info.title = info1;
                    if (!info.title.equals("")) {
                        infoLists.add(info);
                    }
                }*/
                for(int i=0;i<lists.size();i++){
                    String info1 = lists.get(i).title.replaceAll(" ", "").trim();
                    lists.get(i).title = info1;
                    if (!info1.equals("")) {
                        infoLists.add(lists.get(i));
                    }
                }
                mInfos.addAll(infoLists);
                if (mInfos.size() < 60) {
                    hasData = false;
                    mListView.setPullLoadEnable(false, hasData);
                } else {
                    hasData = true;
                    mListView.setPullLoadEnable(true);
                }
                mCurPage++;
                mAdapter.notifyDataSetChanged();
                if (mInfos.size() == 0) {
                    showToast("没有相关数据");
                }
            }
        });
    }

}
