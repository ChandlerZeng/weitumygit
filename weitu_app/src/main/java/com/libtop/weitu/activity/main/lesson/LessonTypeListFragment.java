package com.libtop.weitu.activity.main.lesson;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.main.adapter.LessonTypeListAdapter;
import com.libtop.weitu.activity.main.dto.LessonTypeData;
import com.libtop.weitu.base.BaseFragment;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.CollectionUtils;
import com.libtop.weitu.utils.JsonUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnItemClick;
import okhttp3.Call;

/**
 * Created by Administrator on 2016/1/20 0020.
 */
public class LessonTypeListFragment extends BaseFragment{
    @Bind(R.id.title)
    TextView mTitleText;
    @Bind(R.id.list)
    ListView mListView;

    private List<LessonTypeData> mData=new ArrayList<LessonTypeData>();
    private LessonTypeListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter=new LessonTypeListAdapter(mContext,mData);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_list_type;
    }

    private void setTitle(){
        mTitleText.setText("特色课堂");
    }

    @Override
    public void onCreation(View root) {
        setTitle();
        mListView.setAdapter(mAdapter);
        if (mData.isEmpty())load();
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

    @Override
    public void onBackPressed() {
        mContext.finish();
    }

    private void load(){
        showLoding();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("libraryCode", mPreference.getString(Preference.SchoolId));
        params.put("method", "specialCourse.list");
        HttpRequest.loadWithMap(params)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String json, int id) {
                        dismissLoading();
                        if (TextUtils.isEmpty(json)) {
                            showToast("未找到相关记录");
                            return;
                        }
                        List<LessonTypeData> date = JsonUtil.fromJson(json, new TypeToken<List<LessonTypeData>>() {
                        }.getType());
                        if (CollectionUtils.isEmpty(date)) {
                            showToast("未找到相关记录");
                            return;
                        }
                        mData = date;
                        mAdapter.update(mData);
                        Log.w("md",mData.size()+"");
                    }
                });
    }

    @Nullable @OnItemClick(value = R.id.list)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bd=new Bundle();
        LessonTypeData ld=mData.get(position);
        bd.putString("id",ld.getId());
        bd.putString("title",ld.getTitle());
        bd.putBoolean(ContentActivity.FRAG_WITH_ANIM, true);
        bd.putBoolean(ContentActivity.FRAG_ISBACK,true);
        bd.putString(ContentActivity.FRAG_CLS, LessonListFragment.class.getName());
        mContext.startActivity(bd,ContentActivity.class);
    }
}
