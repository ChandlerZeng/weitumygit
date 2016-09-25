package com.libtop.weitu.activity.base;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.view.View;

import com.libtop.weitu.application.AppApplication;


/**
 * @author Sai
 * @ClassName: MyBaseActivity
 * @Description: 基础Activity
 * @date 9/12/16 19:46
 */
public class MyBaseActivity extends Activity
{
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
