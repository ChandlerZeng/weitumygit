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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentFragment;
import com.libtop.weitu.activity.main.NewSubjectActivity;
import com.libtop.weitu.activity.main.adapter.SelectSubjectAdapter;
import com.libtop.weitu.activity.user.dto.CollectBean;
import com.libtop.weitu.dao.ResultCodeDto;
import com.libtop.weitu.eventbus.MessageEvent;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.http.MapUtil;
import com.libtop.weitu.http.WeituNetwork;
import com.libtop.weitu.test.Subject;
import com.libtop.weitu.test.SubjectResource;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.ContantsUtil;
import com.libtop.weitu.utils.JsonUtil;
import com.melnykov.fab.FloatingActionButton;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnItemClick;
import okhttp3.Call;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by Zeng on 2016/9/8
 */
public class SelectSubjectFragment extends ContentFragment {

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

    private List<Subject> selectSubDatas = new ArrayList<>();
    private SelectSubjectAdapter mAdapter;
    private int mCurPage = 1;
    private boolean hasData = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new SelectSubjectAdapter(mContext, selectSubDatas);
        EventBus.getDefault().register(this);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_select_subject;
    }


    @Override
    public void onCreation(View root) {
        initView();
        loadCollected();
    }


    private void initView() {
        title.setText("选择主题");
        newTheme.attachToListView(selectSubList);
        selectSubList.setAdapter(mAdapter);
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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

        String[] subIds = mAdapter.selectSubId();
        if(subIds == null || subIds.length == 0){
            onBackPressed();
            return;
        }
        HashMap<String, Object> map = new HashMap<>();
        try
        {
            JSONArray jsonarray = new JSONArray(Arrays.toString(subIds));
            map.put("ids", jsonarray);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        String api = "/subject/resource/include";
        HttpRequest.newLoad(ContantsUtil.API_FAKE_HOST_PUBLIC + api, map).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }


            @Override
            public void onResponse(String json, int id) {
                if (!TextUtils.isEmpty(json)) {
                    dismissLoading();
                    showToast("收录成功");
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isIncluded", true);
                    EventBus.getDefault().post(new MessageEvent(bundle));
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            onBackPressed();
                        }
                    }, 1000);
                }else{
                    showToast("收录失败,请稍后再试");
                }
            }
        });
    }


    @Nullable
    @OnItemClick(value = R.id.select_sub_list)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mAdapter.setCheckStatus(position);
    }


    private void loadCollected() {
        showLoding();
        Map<String, Object> params = new HashMap<String, Object>();
        String api = "/subject/my_all/list";
        HttpRequest.newLoad(ContantsUtil.API_FAKE_HOST_PUBLIC + api, null).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }


            @Override
            public void onResponse(String json, int id) {
                if (!TextUtils.isEmpty(json)) {
                    dismissLoading();
                    try {
                        SubjectResource subjectResource = JsonUtil.fromJson(json, new TypeToken<SubjectResource>() {
                        }.getType());
                        selectSubDatas = subjectResource.subjects;
                        mAdapter.setData(selectSubDatas);
                        if (selectSubDatas.size() == 0) {
                            showToast("没有相关主题");
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
        startActivityForResult(intent, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 100){
//            loadCollected();
        }
    }
}
