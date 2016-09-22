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
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentFragment;
import com.libtop.weitu.activity.main.NewSubjectActivity;
import com.libtop.weitu.activity.main.adapter.SelectSubjectAdapter;
import com.libtop.weitu.eventbus.MessageEvent;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.test.Subject;
import com.libtop.weitu.test.SubjectResource;
import com.libtop.weitu.utils.ContantsUtil;
import com.libtop.weitu.utils.JsonUtil;
import com.libtop.weitu.utils.ListViewUtil;
import com.libtop.weitu.widget.NetworkLoadingLayout;
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

    private List<Subject> selectSubDatas = new ArrayList<>();
    private SelectSubjectAdapter mAdapter;
    private Subject subject;

    private boolean isFirstIn = true;


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
    }


    private void initView() {
        if (isFirstIn) {
            isFirstIn = false;
            networkLoadingLayout.showLoading();
            loadCollected();
        }
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
        String api = "/subject/my_all/list";
        HttpRequest.newLoad(ContantsUtil.API_FAKE_HOST_PUBLIC + api, null).execute(new StringCallback() {
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
                        SubjectResource subjectResource = JsonUtil.fromJson(json, new TypeToken<SubjectResource>() {
                        }.getType());
                        selectSubDatas = subjectResource.subjects;
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
        startActivityForResult(intent, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 100){
//            loadCollected();
            createNewSubjectFinished();
        }
    }

    private void createNewSubjectFinished(){
        showLoding();
        Map<String, Object> params = new HashMap<String, Object>();
        String api = "/subject/create";
        params.put("name","新建主题");
        params.put("cover","http://imgsize.ph.126.net/?enlarge=true&imgurl=http://vimg3.ws.126.net/image/snapshot_movie/2013/11/V/3/M9BM227V3.jpg_280x158x1x95.jpg");
        HttpRequest.newLoad(ContantsUtil.API_FAKE_HOST_PUBLIC + api, params).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }


            @Override
            public void onResponse(String json, int id) {
                if (!TextUtils.isEmpty(json)) {
                    dismissLoading();
                    Toast.makeText(mContext,"新建主题成功",Toast.LENGTH_SHORT).show();
                    try {
                        SubjectResource subjectResource = JsonUtil.fromJson(json, new TypeToken<SubjectResource>() {
                        }.getType());
                        subject = subjectResource.subject;
                        if(subject!=null){
                            selectSubDatas.add(0,subject);
                            mAdapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onRetryClick(View v) {
        loadCollected();
    }
}
