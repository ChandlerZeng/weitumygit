package com.libtop.weitu.activity.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.startup.StartupActivity;
import com.libtop.weitu.activity.user.UserCenterFragment;
import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.utils.PopupW.MoreWindow;
import com.libtop.weitu.widget.NoSlideViewPager;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;


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
        JPushInterface.init(this);
        JPushInterface.resumePush(getApplicationContext());
        initFragment();
        init();
        setListener();
        mViewPager.setPagingEnabled(false);
        mAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);

        imgHome.setBackgroundResource(R.drawable.main_tag_checked_home);
        home.setTextColor(getResources().getColor(R.color.newGreen));

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
        MobclickAgent.onPause(this);
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        MobclickAgent.onResume(this);
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
