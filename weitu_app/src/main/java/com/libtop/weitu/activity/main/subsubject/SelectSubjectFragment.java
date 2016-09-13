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
import com.libtop.weitu.tool.Preference;
import com.melnykov.fab.FloatingActionButton;

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

    private List<CollectBean> selectSubs = new ArrayList<CollectBean>();
    private SelectSubjectAdapter mAdapter;
    private int mCurPage = 1;
    private boolean hasData = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new SelectSubjectAdapter(mContext, selectSubs);
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
        map.put("method", "favorite.deleteBatch");
        String[] arrays = MapUtil.map2Parameter(map);
        subscription = WeituNetwork.getWeituApi().getResultCode(arrays[0], arrays[1], arrays[2]).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<ResultCodeDto>()
        {
            @Override
            public void onCompleted()
            {

            }


            @Override
            public void onError(Throwable e)
            {
                showToast("收录失败,请稍后再试");
            }


            @Override
            public void onNext(ResultCodeDto resultCodeDto)
            {
                if (resultCodeDto.code == 1)
                {
                    showToast("收录成功");
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isDelete", true);
                    EventBus.getDefault().post(new MessageEvent(bundle));
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            onBackPressed();
                        }
                    }, 1000);
                }
                else
                {
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
        params.put("method", "favorite.query");
        params.put("uid", mPreference.getString(Preference.uid));
        HttpRequest.loadWithMapSec(params, new HttpRequest.CallBackSec() {
            @Override
            public void onError(Call call, Exception e, int id) {
                dismissLoading();
            }


            @Override
            public void onResponse(String json, int id) {
                dismissLoading();
                if (TextUtils.isEmpty(json)) {
                    showToast("没有相关数据");
                    return;
                }
                selectSubs.clear();
                Gson gson = new Gson();
                List<CollectBean> collectBeanList = gson.fromJson(json, new TypeToken<List<CollectBean>>() {
                }.getType());
                for (int i = 0; i < collectBeanList.size(); i++) {
                    CollectBean bean = new CollectBean();
                    bean = collectBeanList.get(i);
                    bean.target.title.replaceAll("　　", "").trim();
                    selectSubs.add(bean);
                }
                mAdapter.setData(selectSubs);
                mAdapter.notifyDataSetChanged();
                if (selectSubs.size() == 0) {
                    showToast("没有相关数据");
                }
            }
        });
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(MessageEvent event)
    {
        Bundle bundle = event.message;
        Boolean isDelete = bundle.getBoolean("isDelete");
        if (isDelete)
        {
            loadCollected();
        }
    }

    private void createNewTheme(){
        Intent intent = new Intent();
        intent.setClass(getActivity(), NewSubjectActivity.class);
        startActivityForResult(intent,100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 100){
            loadCollected();
        }
    }
}
