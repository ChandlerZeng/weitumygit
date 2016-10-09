package com.libtop.weitu.activity.base;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.view.View;

import com.libtop.weitu.application.AppApplication;
import com.umeng.analytics.MobclickAgent;


/**
 * @author Sai
 * @ClassName: MyBaseActivity
 * @Description: 基础Activity
 * @date 9/12/16 19:46
 */
public class MyBaseActivity extends Activity
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


    protected Activity getThis()
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
