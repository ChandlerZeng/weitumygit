package com.libtop.weituR.activity.main.notice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.ContentActivity;
import com.libtop.weituR.activity.main.adapter.NoticeListAdapter;
import com.libtop.weituR.activity.main.dto.NoticeInfo;
import com.libtop.weituR.base.BaseFragment;
import com.libtop.weituR.http.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;

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
    @Bind(R.id.list)
    ListView mListView;

    private List<NoticeInfo> mInfos=new ArrayList<NoticeInfo>();
    private NoticeListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter=new NoticeListAdapter(mContext,mInfos);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_notice_list;
    }

    @Override
    public void onCreation(View root) {
        setTitle();
        mListView.setAdapter(mAdapter);
        requestNotics();
    }

    private void setTitle(){
        mTitleText.setText(R.string.news_broadcast);
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
        switch (v.getId()){
            case R.id.back_btn:
                onBackPressed();
                break;
        }
    }

    @Nullable @OnItemClick(value = R.id.list)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //显示公告详情
        Bundle bundle1=new Bundle();
        bundle1.putString(ContentActivity.FRAG_CLS, NoticeContentFragment.class.getName());
        bundle1.putBoolean(ContentActivity.FRAG_WITH_ANIM, true);
        bundle1.putBoolean(ContentActivity.FRAG_ISBACK,true);
        bundle1.putString("id",mInfos.get(position).id);
        mContext.startActivity(bundle1,ContentActivity.class);
    }

    private void requestNotics(){
        if (!mInfos.isEmpty()) return;
        showLoding();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("method", "notice.list");
//        params.put("lid",mPreference.getString(Preference.SchoolCode));//lid
        params.put("lid","10564");
        HttpRequest.loadWithMapSec(params, new HttpRequest.CallBackSec() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String json, int id) {
                dismissLoading();
                if (TextUtils.isEmpty(json)) {
                    showToast("没有相关数据");
                    return;
                }
                try {
                    mInfos.clear();
                    JSONArray array = new JSONArray(json);
                    for (int i = 0; i < array.length(); i++) {
                        NoticeInfo bean = new NoticeInfo();
                        bean.form(array.getJSONObject(i));
                        mInfos.add(bean);
                    }
                    mAdapter.notifyDataSetChanged();
                    if (mInfos.size() == 0) {
                        showToast("没有相关数据");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
