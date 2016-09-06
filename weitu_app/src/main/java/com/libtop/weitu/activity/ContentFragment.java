package com.libtop.weitu.activity;

import android.os.Bundle;

import com.libtop.weitu.base.BaseFragment;


/**
 * Created by Administrator on 2016/1/25 0025.
 */
public abstract class ContentFragment extends BaseFragment
{
    protected ContentActivity mActivity;
    protected Bundle mBundle;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mActivity = (ContentActivity) mContext;
        mBundle = mActivity.getCurrentExtra();
    }


    @Override
    public void onBackPressed()
    {
        if (mBundle == null)
        {
            mContext.finish();
            return;
        }
        if (mBundle.getBoolean(ContentActivity.FRAG_ISBACK))
        {
            mActivity.popBack();
        }
        else
        {
            mContext.finish();
        }
    }

}
