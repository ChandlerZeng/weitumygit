package com.libtop.weituR.activity.main.clickHistory;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weituR.base.BaseActivity;
import com.libtop.weituR.base.impl.NotifyFragment;
import com.libtop.weituR.dao.ResultCodeDto;
import com.libtop.weituR.eventbus.MessageEvent;
import com.libtop.weituR.http.MapUtil;
import com.libtop.weituR.http.WeituNetwork;
import com.libtop.weituR.tool.Preference;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by LianTu on 2016/7/15.
 */
public class ClickHistoryActivity2 extends BaseActivity {

    @Bind(R.id.title)
    TextView tvTitle;
    @Bind(R.id.commit)
    TextView tvCommit;

    private NotifyFragment mFragment;
    private String mCurentTag ="";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setInjectContentView(R.layout.activity_click_history);
        init();
    }


    private void init() {
        tvTitle.setText("我的历史");
        tvCommit.setText("清空历史");
        FragmentTransaction tran =mFm.beginTransaction();
        tran.replace(R.id.content_fragment,new ClickHistoryFragment());
        tran.addToBackStack(null);
        tran.commit();
    }

    @Nullable
    @OnClick({R.id.back_btn,R.id.delete,R.id.commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                onBackPressed();
                break;
            case R.id.commit:
                //清空历史
                clearHistory();
                break;

        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    //清空历史
    private void clearHistory() {
        HashMap<String,Object> map = new HashMap<>();
        map.put("uid",mPreference.getString(Preference.uid));
        map.put("method","footprint.clear");
        String[] arrays = MapUtil.map2Parameter(map);
        subscription = WeituNetwork.getWeituApi()
                .getResultCode(arrays[0],arrays[1],arrays[2])
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResultCodeDto>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(ResultCodeDto resultCodeDto) {
                        if (resultCodeDto.code==1){
                            showToast("清除成功");
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("isClean",true);
                            EventBus.getDefault().post(new MessageEvent(bundle));
                        }else {
                            showToast("清除失败");
                        }
                    }
                });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFragment!=null){
            mFragment=null;
        }
    }
}
