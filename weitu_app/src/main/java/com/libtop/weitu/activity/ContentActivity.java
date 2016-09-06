package com.libtop.weitu.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.libtop.weitu.R;
import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.base.BaseFragment;
import com.libtop.weitu.base.FragmentFactory;


/**
 * Created by Administrator on 2016/1/8 0008.
 */
public class ContentActivity extends BaseActivity
{
    private String mCurrentTag;

    public static final String FRAG_CLS = "frag_clzz";
    public static final String FRAG_ISBACK = "frag_isback";
    public static final String FRAG_WITH_ANIM = "frag_with_anim";

    private Bundle mBundle;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setInjectContentView(R.layout.activity_container);
        mBundle = getIntent().getExtras();
        if (mBundle != null)
        {
            addFragment(mBundle.getString(FRAG_CLS));
        }

    }


    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        mBundle = intent.getExtras();
        if (mBundle != null)
        {
            changeFragment(mBundle.getString(FRAG_CLS), mBundle.getBoolean(FRAG_ISBACK), mBundle.getBoolean(FRAG_WITH_ANIM));
        }
    }


    public Bundle getCurrentExtra()
    {
        return mBundle;
    }


    /**
     * 添加fragment
     *
     * @param fragCls
     */
    private void addFragment(String fragCls)
    {
        FragmentTransaction ft = mFm.beginTransaction();
        Fragment frag = FragmentFactory.newFragment(fragCls);
        ft.add(R.id.container, frag, fragCls);
        ft.commit();
        mCurrentTag = fragCls;
    }


    /**
     * 切换fragment
     *
     * @param fragCls     fragment的类名
     * @param isBack      是否加入回退栈
     * @param withAnimate 是否显示动画效果
     */
    public void changeFragment(String fragCls, boolean isBack, boolean withAnimate)
    {
        FragmentTransaction ft = mFm.beginTransaction();
        Fragment frag = mFm.findFragmentByTag(fragCls);
        if (frag == null)
        {
            frag = FragmentFactory.newFragment(fragCls);
        }
        ft.replace(R.id.container, frag, fragCls);
        if (isBack)
        {
            ft.addToBackStack(fragCls);
        }
        if (withAnimate)
        {
            ft.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out, R.anim.slide_right_in, R.anim.slide_right_out);
        }
        ft.commit();
        mCurrentTag = fragCls;
    }


    public BaseFragment findFragment(String cls)
    {
        return (BaseFragment) mFm.findFragmentByTag(cls);
    }


    /**
     * 把回退事件传递给fragment
     */
    @Override
    public void onBackPressed()
    {
        BaseFragment frag = (BaseFragment) mFm.findFragmentByTag(mCurrentTag);
        if (frag != null)
        {
            frag.onBackPressed();
            return;
        }
        super.onBackPressed();
    }


    /**
     * 调用fragment回退栈
     */
    public void popBack()
    {
        mFm.popBackStack();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        BaseFragment frag = (BaseFragment) mFm.findFragmentByTag(mCurrentTag);
        if (frag != null)
        {
            frag.onResult(requestCode, resultCode, data);
        }
    }


}
