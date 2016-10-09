package com.libtop.weitu.activity.main.subsubject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.ContentFragment;
import com.libtop.weitu.activity.main.NewSubjectActivity;
import com.libtop.weitu.activity.main.adapter.SelectSubjectAdapterNew;
import com.libtop.weitu.activity.main.dto.SubjectBean;
import com.libtop.weitu.dao.ResultCodeDto;
import com.libtop.weitu.eventbus.MessageEvent;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.test.Subject;
import com.libtop.weitu.utils.Preference;
import com.libtop.weitu.utils.JSONUtil;
import com.libtop.weitu.utils.ListViewUtil;
import com.libtop.weitu.widget.NetworkLoadingLayout;
import com.melnykov.fab.FloatingActionButton;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnItemClick;
import okhttp3.Call;


/**
 * Created by Zeng on 2016/9/8
 */
public class SelectSubjectFragment extends ContentFragment implements NetworkLoadingLayout.OnRetryClickListner{

    @Bind(R.id.back_btn)
    ImageView backBtn;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.commit)
    TextView commit;
    @Bind(R.id.select_sub_list)
    ListView selectSubList;
    @Bind(R.id.select_sub_new_theme)
    FloatingActionButton newTheme;
    @Bind(R.id.networkloadinglayout)
    NetworkLoadingLayout networkLoadingLayout;

    private List<SubjectBean> selectSubDatas = new ArrayList<>();
    private SelectSubjectAdapterNew mAdapter;
    private Subject subject;

    private boolean isFirstIn = true;
    private Bundle budleState;
    private int result = 0;

    private String tid;
    private int type;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = ((ContentActivity) mContext).getCurrentExtra();
        tid = bundle.getString("tid");
        type = bundle.getInt("type");
        mAdapter = new SelectSubjectAdapterNew(mContext, selectSubDatas);
        EventBus.getDefault().register(this);
        budleState = new Bundle();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_select_subject;
    }


    @Override
    public void onCreation(View root) {
        initView();
    }


    private void initView() {
        if (isFirstIn) {
            isFirstIn = false;
            networkLoadingLayout.showLoading();
            loadCollected();
        }
//        mAdapter.setData(selectSubDatas);
        title.setText("选择主题");
        ListViewUtil.addPaddingHeader(mContext,selectSubList);
        newTheme.setVisibility(View.GONE);
        newTheme.attachToListView(selectSubList);
        selectSubList.setAdapter(mAdapter);
        networkLoadingLayout.setOnRetryClickListner(this);

    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        budleState.putSerializable("subjectlist", (Serializable) selectSubDatas);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        budleState.clear();
    }

    @Nullable
    @OnClick({R.id.back_btn, R.id.commit, R.id.select_sub_new_theme})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                onBackPressed();
                break;
            case R.id.commit:
                subjectInclude();
                break;
            case R.id.select_sub_new_theme:
                createNewTheme();
                break;
        }
    }

    private void subjectInclude(){
//        http://weitu.bookus.cn/subject/include.json?text={"sids":["56f97d8d984e741f1420axx","56f97d8d984e741f1420ayy"],"tid":"56f97d8d984e741f1420awr8","method":"subject.include"}
        String[] subIds = mAdapter.selectSubId();
        if(subIds == null || subIds.length == 0){
            onBackPressed();
            return;
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("method","subject.include");
        map.put("tid",tid);
        map.put("type",type);
        try
        {
            JSONArray jsonarray = new JSONArray(Arrays.toString(subIds));
            map.put("sids", jsonarray);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        HttpRequest.loadWithMap(map).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }


            @Override
            public void onResponse(String json, int id) {
                if (json!=null && !TextUtils.isEmpty(json)) {
                    dismissLoading();
                    ResultCodeDto resultCodeDto = JSONUtil.readBean(json, ResultCodeDto.class);
                    if(resultCodeDto!=null && resultCodeDto.code==1){
                        showToast("收录成功");
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("isIncluded", true);
                        EventBus.getDefault().post(new MessageEvent(bundle));
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                onBackPressed();
                            }
                        }, 1000);
                    }else {
                        showToast("收录失败,请稍后再试");
                    }
                } else {
                    showToast("收录失败,请稍后再试");
                }
            }
        });
    }


    @Nullable
    @OnItemClick(value = R.id.select_sub_list)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mAdapter.setCheckStatus(position-1);
    }

    private void loadCollected() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("uid",mPreference.getString(Preference.uid));
        params.put("method","subject.my");
        params.put("page",1);
        HttpRequest.loadWithMap(params).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                networkLoadingLayout.showLoadFailAndRetryPrompt();
            }


            @Override
            public void onResponse(String json, int id) {
                if (!TextUtils.isEmpty(json)) {
                    networkLoadingLayout.dismiss();
                    newTheme.setVisibility(View.VISIBLE);
                    try {
                        ArrayList<SubjectBean> subjects = JSONUtil.readBeanArray(json, SubjectBean.class);
                        subjects.removeAll(Collections.singleton(null));

                        selectSubDatas = subjects;
                        if(result == NewSubjectActivity.RESULT_SUCCESSS){
                            List<SubjectBean> subList = new ArrayList<>();
                            subList = (List<SubjectBean>) budleState.getSerializable("subjectlist");
                            for(int i = 1;i<selectSubDatas.size();i++){
                                boolean checked = subList.get(i-1).ischecked();
                                selectSubDatas.get(i).setIschecked(checked);
                            }
                        }
                        mAdapter.setData(selectSubDatas);
                        if (selectSubDatas.size() == 0) {
                            networkLoadingLayout.showEmptyPrompt();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(MessageEvent event)
    {
        Bundle bundle = event.message;
        Boolean isIncluded = bundle.getBoolean("isIncluded",false);
        if (isIncluded)
        {
//            loadCollected();
        }
    }

    private void createNewTheme(){
        Intent intent = new Intent();
        intent.setClass(getActivity(), NewSubjectActivity.class);
        intent.putExtra("fromSelect",true);
        startActivityForResult(intent, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 100 & resultCode==NewSubjectActivity.RESULT_SUCCESSS){
//            loadCollected();
            result = resultCode;
            loadCollected();
        }
    }


    @Override
    public void onRetryClick(View v) {
        loadCollected();
    }
}
