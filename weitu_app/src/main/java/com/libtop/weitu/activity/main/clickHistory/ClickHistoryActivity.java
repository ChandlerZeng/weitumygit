package com.libtop.weitu.activity.main.clickHistory;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.base.impl.NotifyFragment;
import com.libtop.weitu.dao.ResultCodeDto;
import com.libtop.weitu.eventbus.MessageEvent;
import com.libtop.weitu.http.MapUtil;
import com.libtop.weitu.http.WeituNetwork;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.selector.utils.AlertDialogUtil;
import com.libtop.weitu.utils.selector.view.MyAlertDialog;

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
public class ClickHistoryActivity extends BaseActivity {

    @Bind(R.id.title)
    TextView tvTitle;
    @Bind(R.id.commit)
    TextView tvCommit;

    private NotifyFragment mFragment;

    public static final int VIDEO=1,AUDIO=2,DOC=3,PHOTO=4,BOOK=5;

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
                String title = "您确定要删除？";
                final AlertDialogUtil dialog = new AlertDialogUtil();
                dialog.showDialog(ClickHistoryActivity.this, title, "确定", "取消", new MyAlertDialog.MyAlertDialogOnClickCallBack() {
                    @Override
                    public void onClick() {
                        clearHistory();
                    }
                }, null);
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
