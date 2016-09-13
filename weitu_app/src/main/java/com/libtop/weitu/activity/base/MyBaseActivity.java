package com.libtop.weitu.activity.base;

import android.app.Activity;

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
}
