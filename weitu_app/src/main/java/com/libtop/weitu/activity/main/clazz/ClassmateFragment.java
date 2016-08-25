package com.libtop.weitu.activity.main.clazz;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.main.adapter.MateSortAdapter;
import com.libtop.weitu.activity.main.dto.ClassmateBean;
import com.libtop.weitu.base.BaseFragment;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.CollectionUtils;
import com.libtop.weitu.utils.DisplayUtils;
import com.libtop.weitu.utils.JsonUtil;
import com.libtop.weitu.widget.slidebar.SideBar;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnItemClick;
import okhttp3.Call;

/**
 * Created by Administrator on 2016/1/19 0019.
 */
public class ClassmateFragment extends BaseFragment {
    @Bind(R.id.title)
    TextView mTitleText;
    @Bind(R.id.list)
    ListView mListView;
    @Bind(R.id.sidebar)
    SideBar mSideBar;
    @Bind(R.id.dialog)
    TextView mDialog;

    @Bind(R.id.root)
    LinearLayout mRoot;

    private MemberPop mPop;

    private List<ClassmateBean> mDatas=new ArrayList<ClassmateBean>();
    private MateSortAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPop=new MemberPop(mContext);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_classmate_list;
    }

    @Override
    public void onCreation(View root) {
        setTitle();
        mAdapter = new MateSortAdapter(mContext, mDatas);
        mListView.setAdapter(mAdapter);
        mSideBar.setTextView(mDialog);
        mSideBar.setLetterSize(DisplayUtils.dp2px(mContext, 14));
        mSideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {
            @Override
            public void onTouchingLetterChanged(String s) {
                int position = mAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    mListView.setSelection(position);
                }
            }
        });
//        mSideBar.setOnTouchingLetterChangedListener(this);
//        mListView.setOnItemClickListener(this);
        load();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setTitle(){
//        mBackBtn.setOnClickListener(this);
        mTitleText.setText("班级成员");
//        mTitleText.setTextSize();
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

    private void load(){
        showLoding();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("uid", mPreference.getString(Preference.uid));
//        params.put("uid", "569cb656984e0c6b0cf1c35f");
        params.put("method", "user.classmate");
        HttpRequest.loadWithMap(params)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String json, int id) {
                        dismissLoading();
                        if (TextUtils.isEmpty(json)){
                            showToast("未找到相关记录");
                            return;
                        }
                        List<ClassmateBean> date= JsonUtil.fromJson(json,new TypeToken<List<ClassmateBean>>(){}.getType());
                        if (CollectionUtils.isEmpty(date)){
                            showToast("未找到相关记录");
                            return;
                        }
                        sortList(date);
                        mDatas=date;
                        mAdapter.update(mDatas);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        mContext.finish();
    }


    @Nullable @OnItemClick(value = R.id.list)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        new MemberPop(mContext,mDatas.get(position)).showAtLocation(mRoot, Gravity.RIGHT,0,0);
        mPop.setData(mDatas.get(position));
        mPop.showOnParent(mRoot);
    }

//    @Nullable @OnClick(value = R.id.sidebar,type = SideBar.OnTouchingLetterChangedListener.class)
//    private void onTouchingLetterChanged(String s) {
//        int position = mAdapter.getPositionForSection(s.charAt(0));
//        if (position != -1) {
//            mListView.setSelection(position);
//        }
//    }

    private void sortList(List<ClassmateBean> date){
        Collections.sort(date, new Comparator<ClassmateBean>() {
            @Override
            public int compare(ClassmateBean lhs, ClassmateBean rhs) {
                char mine=lhs.getCharacter().toUpperCase().charAt(0);
                char other=rhs.getCharacter().toUpperCase().charAt(0);
                return mine-other;
            }
        });
    }

    @Override
    public void onDestroy() {
        mPop.dismiss();
        super.onDestroy();
    }
}
