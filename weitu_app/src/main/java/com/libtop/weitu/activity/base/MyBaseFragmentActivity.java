package com.libtop.weitu.activity.base;

import android.support.annotation.IdRes;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.libtop.weitu.application.AppApplication;
import com.umeng.analytics.MobclickAgent;


/**
 * @author Sai
 * @ClassName: MyBaseFragmentActivity
 * @Description: 基础FragmentActivity
 * @date 9/13/16 15:12
 */
public class MyBaseFragmentActivity extends FragmentActivity
{
    @Override
    protected void onResume()
    {
        super.onResume();
        MobclickAgent.onResume(this); // 用于umeng统计分析
    }


    @Override
    protected void onPause()
    {
        super.onPause();
        MobclickAgent.onPause(this); // 用于umeng统计分析
    }


    protected FragmentActivity getThis()
    {
        return this;
    }


    protected AppApplication getAppApplication()
    {
        return AppApplication.getInstance();
    }


    protected <T extends View> T getViewById(@IdRes int id)
    {
        return (T) findViewById(id);
    }
}
