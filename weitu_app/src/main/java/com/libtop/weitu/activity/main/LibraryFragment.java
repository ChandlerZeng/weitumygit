package com.libtop.weitu.activity.main;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentFragment;
import com.libtop.weitu.activity.main.adapter.SortAdapter;
import com.libtop.weitu.activity.main.dto.SchoolDto;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.CheckUtil;
import com.libtop.weitu.widget.slidebar.SideBar;

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
 * Created by Administrator on 2016/1/8 0008.
 */
public class LibraryFragment extends ContentFragment{
    @Bind(R.id.list)
    ListView mLibListView;
    @Bind(R.id.sidebar)
    SideBar mSideBar;
    @Bind(R.id.dialog)
    TextView mDialog;
    @Bind(R.id.title)
    TextView mTitleText;


    private List<SchoolDto> mDatas=new ArrayList<SchoolDto>();
    private SortAdapter mAdapter;
    private int from = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = mContext.getIntent().getExtras();
        if (bundle != null) {
            from = bundle.getInt("from", 0);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_library_list;
    }

    @Override
    public void onCreation(View root) {
        super.onCreation(root);
        mTitleText.setText(R.string.choose_library);
        mAdapter = new SortAdapter(mContext, mDatas);
        mLibListView.setAdapter(mAdapter);
        mSideBar.setTextView(mDialog);
        mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                // 该字母首次出现的位置
                int position = mAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mLibListView.setSelection(position);
                }
            }
        });
//        mSideBar.setOnTouchingLetterChangedListener(this);
//        mLibListView.setOnItemClickListener(this);
        loadData();
    }



    private void loadData() {
        showLoding();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("method", "library.list");
        HttpRequest.loadWithMapSec(params, new HttpRequest.CallBackSec() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String json, int id) {
                dismissLoading();
                if (!CheckUtil.isNull(json)) {
                    try {
                        mDatas.clear();
                        JSONArray array = new JSONArray(json);
                        for (int i = 0; i < array.length(); i++) {
                            SchoolDto dto = new SchoolDto();
                            dto.of(array.getJSONObject(i));
                            mDatas.add(dto);
                        }
                        mAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showToast("数据解析出错");
                    }
                }
            }
        });
    }

    @Nullable
    @OnItemClick(value = R.id.list)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SchoolDto dto = mDatas.get(position);
        mPreference.putString(Preference.SchoolName, dto.name);
        mPreference.putString(Preference.SchoolCode, dto.code);
        mPreference.putString(Preference.SchoolId, dto.id);
        if (from == 0) {
            mActivity.popBack();
        } else {
            //打开主页面
            mContext.setResult(Activity.RESULT_OK);
//            if (WelcomeActivity.instance != null) {
//                WelcomeActivity.instance.finish();
//            }
            mContext.finish();
            mContext.startActivity(null, MainActivity.class);
            //关闭起始页
//            mContext.setResult(Activity.RESULT_OK);
//            mContext.finish();
        }
    }

//    @Nullable @OnClick(value = R.id.sidebar,type = SideBar.OnTouchingLetterChangedListener.class)
//    private void onTouchingLetterChanged(String s) {
//        // 该字母首次出现的位置
//        int position = mAdapter.getPositionForSection(s.charAt(0));
//        if (position != -1) {
//            mLibListView.setSelection(position);
//        }
//    }

    @Nullable @OnClick(R.id.back_btn)
    public void viewClick(View v){
        switch (v.getId()){
            case R.id.back_btn:
                onBackPressed();
                break;
        }
    }
}
