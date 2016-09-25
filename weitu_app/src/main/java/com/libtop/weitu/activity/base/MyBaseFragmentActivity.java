package com.libtop.weitu.activity.base;

import android.support.annotation.IdRes;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.libtop.weitu.application.AppApplication;


/**
 * @author Sai
 * @ClassName: MyBaseFragmentActivity
 * @Description: 基础FragmentActivity
 * @date 9/13/16 15:12
 */
public class MyBaseFragmentActivity extends FragmentActivity
{
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
