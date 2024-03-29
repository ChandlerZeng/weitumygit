package com.libtop.weituR.activity;

import android.os.Bundle;
import android.widget.Toast;

import com.libtop.weituR.base.BaseFragment;
import com.libtop.weituR.eventbus.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Administrator on 2016/1/25 0025.
 */
public abstract class ContentFragment extends BaseFragment{
    protected ContentActivity mActivity;
    protected Bundle mBundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity=(ContentActivity)mContext;
        mBundle=mActivity.getCurrentExtra();
    }

    @Override
    public void onBackPressed() {
        if (mBundle==null){
            mContext.finish();
            return;
        }
        if (mBundle.getBoolean(ContentActivity.FRAG_ISBACK)){
            mActivity.popBack();
        }else {
            mContext.finish();
        }
    }

}
