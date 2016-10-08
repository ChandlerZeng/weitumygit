package com.libtop.weitu.activity.main;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.libtop.weitu.BuildConfig;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.startup.StartupActivity;
import com.libtop.weitu.activity.user.UserCenterFragment;
import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.service.WTBroadcastReceiver;
import com.libtop.weitu.service.WTBroadcastService;
import com.libtop.weitu.service.WTPushService;
import com.libtop.weitu.utils.ContextUtil;
import com.libtop.weitu.utils.PopupW.MoreWindow;
import com.libtop.weitu.utils.Preference;
import com.libtop.weitu.widget.NoSlideViewPager;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;


/**
 * Created by Administrator on 2016/1/8 0008.
 */
public class MainActivity extends BaseActivity
{
    private long mLastBackPress = 0;
    @Bind(R.id.mViewPager)
    NoSlideViewPager mViewPager;
    private int indicatorWidth;
    @Bind(R.id.tv_home)
    TextView home;
    @Bind(R.id.img_home)
    ImageView imgHome;
    @Bind(R.id.tv_clazz)
    TextView clazz;
    @Bind(R.id.img_clazz)
    ImageView imgClazz;
    @Bind(R.id.tv_personal)
    TextView personal;
    @Bind(R.id.img_personal)
    ImageView imgPersonal;
    MoreWindow mMoreWindow;
    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    TabFragmentPagerAdapter mAdapter;

    private final int TOTAL_FRAGMENT = 3;

    private MyHandler handler = new MyHandler(this);
    private WTBroadcastReceiver noticeBroadcastReceiver;


    private static class MyHandler extends Handler
    {
        WeakReference<MainActivity> reference;


        MyHandler(MainActivity activity)
        {
            reference = new WeakReference<>(activity);
        }


        @Override
        public void handleMessage(Message msg)
        {
            if (msg.obj != null && msg.obj instanceof Intent)
            {
                MainActivity mainActivity = reference.get();

                Intent intent = (Intent) msg.obj;
                mainActivity.handleNoticeBroadcastIntent(intent);
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.e("test draw time start", System.currentTimeMillis() + "");
        super.onCreate(savedInstanceState);
        setInjectContentView(R.layout.activity_main_2);
        if (StartupActivity.instance != null)
        {
            StartupActivity.instance.finish();
            StartupActivity.instance = null;
        }

        String loginUid = mPreference.getString(Preference.uid);
        if (!TextUtils.isEmpty(loginUid))
        {
            WTPushService.initPushConfigure(this, BuildConfig.LOG_DEBUG, BuildConfig.LOG_DEBUG, 1);
            WTPushService.registerPushService(getApplicationContext(), loginUid);
        }

        initFragment();
        init();
        setListener();
        mViewPager.setPagingEnabled(false);
        mAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);

        imgHome.setBackgroundResource(R.drawable.main_tag_checked_home);
        home.setTextColor(getResources().getColor(R.color.newGreen));

        doRegisterReceiverAction();
    }


    private void showMoreWindow(View view)
    {
        if (null == mMoreWindow)
        {
            mMoreWindow = new MoreWindow(this);
            mMoreWindow.init();
        }

        mMoreWindow.showMoreWindow(view, 100);
    }


    private void init()
    {
        WindowManager wm = this.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();

        indicatorWidth = width / 4;
    }


    public void initFragment()
    {
        MainFragment one = new MainFragment();
        SchoolFragment two = new SchoolFragment();
        UserCenterFragment three = new UserCenterFragment();
        fragmentList.add(one);
        fragmentList.add(two);
        fragmentList.add(three);
    }


    @Override
    protected void onPause()
    {
        super.onPause();
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        Log.e("test draw time end", System.currentTimeMillis() + "");
    }


    @Override
    public void onBackPressed()
    {
        long tempTime = System.currentTimeMillis();
        if (tempTime - mLastBackPress < 2000)
        {
            finishSimple();
        }
        else
        {
            Toast.makeText(mContext, "再按一次，退出应用", Toast.LENGTH_SHORT).show();
        }
        mLastBackPress = tempTime;
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        ContextUtil.unregisterReceiver(this, noticeBroadcastReceiver);
    }


    private void doRegisterReceiverAction()
    {
        noticeBroadcastReceiver = new WTBroadcastReceiver(handler);
        IntentFilter filter = new IntentFilter();
        filter.addAction(WTBroadcastService.ACTION_NEW_DYNAMIC_NOTICE); //新动态消息
        registerReceiver(noticeBroadcastReceiver, filter);
    }


    private void handleNoticeBroadcastIntent(Intent intent)
    {
        String action = intent.getAction();
        switch (action)
        {
            case WTBroadcastService.ACTION_NEW_DYNAMIC_NOTICE:  //新动态消息
                Fragment fragment = fragmentList.get(0);
                if (fragment instanceof MainFragment)
                {
                    ((MainFragment) fragment).updateNoticeBadge();
                }
                break;
        }
    }


    @Nullable
    @OnClick({R.id.ll_home, R.id.ll_discover, R.id.ll_personal})
    public void onClick(View v)
    {
        imgHome.setBackgroundResource(R.drawable.main_tag_unchecked_home);
        home.setTextColor(ContextCompat.getColor(mContext, R.color.grey1));
        imgClazz.setBackgroundResource(R.drawable.main_tag_unchecked_discover);
        clazz.setTextColor(ContextCompat.getColor(mContext, R.color.grey1));
        imgPersonal.setBackgroundResource(R.drawable.main_tag_unchecked_personal);
        personal.setTextColor(ContextCompat.getColor(mContext, R.color.grey1));
        switch (v.getId())
        {
            case R.id.ll_home:
                imgHome.setBackgroundResource(R.drawable.main_tag_checked_home);
                home.setTextColor(ContextCompat.getColor(mContext, R.color.newGreen));
                mViewPager.setCurrentItem(0);
                break;
            case R.id.ll_discover:
                imgClazz.setBackgroundResource(R.drawable.main_tag_checked_discover);
                clazz.setTextColor(ContextCompat.getColor(mContext, R.color.newGreen));
                mViewPager.setCurrentItem(1);
                break;
            case R.id.ll_personal:
                imgPersonal.setBackgroundResource(R.drawable.main_tag_checked_personal);
                personal.setTextColor(ContextCompat.getColor(mContext, R.color.newGreen));
                mViewPager.setCurrentItem(2);
                break;
        }
    }


    public class TabFragmentPagerAdapter extends FragmentPagerAdapter
    {

        public TabFragmentPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }


        @Override
        public Fragment getItem(int arg0)
        {
            Fragment ft = null;
            switch (arg0)
            {
                case 0:
                    ft = fragmentList.get(0);
                    break;
                case 1:
                    ft = fragmentList.get(1);
                    break;
                case 2:
                    ft = fragmentList.get(2);
                    break;
                case 3:
                    break;
                default:
                    ft = fragmentList.get(0);
                    break;
            }
            return ft;
        }


        @Override
        public int getCount()
        {

            return TOTAL_FRAGMENT;
        }

    }


    // 互相监听
    private void setListener()
    {

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {

            @Override
            public void onPageSelected(int position)
            {
            }


            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2)
            {

            }


            @Override
            public void onPageScrollStateChanged(int arg0)
            {

            }
        });
    }

}
