package com.libtop.weitu.activity.base;

import android.support.v4.app.Fragment;

import com.libtop.weitu.application.AppApplication;


/**
 * @author Sai
 * @ClassName: MyBaseFragment
 * @Description: 基础Fragment
 * @date 9/13/16 11:56
 */
public class MyBaseFragment extends Fragment
{
    protected Fragment getThis()
    {
        return this;
    }


    protected AppApplication getAppApplication()
    {
        return AppApplication.getInstance();
    }
}
