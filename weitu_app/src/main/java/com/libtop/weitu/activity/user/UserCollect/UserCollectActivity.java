package com.libtop.weitu.activity.user.UserCollect;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.base.impl.NotifyFragment;
import com.libtop.weitu.eventbus.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by LianTu on 2016/7/18.
 */
public class UserCollectActivity extends BaseActivity{
    @Bind(R.id.title)
    TextView tvTitle;
    @Bind(R.id.commit)
    TextView tvCommit;
    @Bind(R.id.delete_view)
    View deleteView;

    private NotifyFragment mFragment;
    private String mCurentTag ="";
    private int pageIndex = 0;

    private Map<Integer,Boolean> map = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setInjectContentView(R.layout.activity_click_history);
        EventBus.getDefault().register(this);
        init();
    }


    private void init() {
        tvTitle.setText("我的收藏");
        tvCommit.setText("编辑");
        FragmentTransaction tran =mFm.beginTransaction();
        tran.replace(R.id.content_fragment,new UserCollectFragment());
        tran.addToBackStack(null);
        tran.commit();
    }

    @Nullable
    @OnClick({R.id.back_btn,R.id.delete,R.id.commit,R.id.have_see,R.id.going_down})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.commit:
                //编辑
                collectEdit();
                break;
            case R.id.have_see:
                //全选
                allSelect();
                break;
            case R.id.going_down:
                //删除
                deleteClick();
                break;

        }
    }

    //删除
    private void deleteClick() {
        Bundle bundle = new Bundle();
        bundle.putString("from",UserCollectActivity.class.getName());
        bundle.putBoolean("delete",true);
        bundle.putInt("pageIndex2",pageIndex);
        EventBus.getDefault().post(new MessageEvent(bundle));
    }

    //全选
    private void allSelect() {
        Bundle bundle = new Bundle();
        bundle.putString("from",UserCollectActivity.class.getName());
        bundle.putBoolean("all",true);
        bundle.putInt("pageIndex2",pageIndex);
        EventBus.getDefault().post(new MessageEvent(bundle));
    }

    //编辑
    private void collectEdit() {
        Bundle bundle = new Bundle();
        bundle.putString("from",UserCollectActivity.class.getName());
        bundle.putInt("pageIndex2",pageIndex);
        EventBus.getDefault().post(new MessageEvent(bundle));
        if (map.get(pageIndex)!=null && map.get(pageIndex)){
            map.put(pageIndex,false);
        }else {
            map.put(pageIndex,true);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(MessageEvent event) {
        Bundle bundle = event.message;
        String from = bundle.getString("from");
        if (from !=null && from.equals(UserCollectFragment.class.getName())){
            pageIndex = bundle.getInt("pageIndex");
        }
        if (map.get(pageIndex)!=null && map.get(pageIndex)){
            tvCommit.setText("取消");
            deleteView.setVisibility(View.VISIBLE);
        }else {
            tvCommit.setText("编辑");
            deleteView.setVisibility(View.GONE);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mFragment!=null){
            mFragment=null;
        }
    }

    @Override
    public void onBackPressed() {
        mContext.finish();
    }
}
