package com.libtop.weitu.activity.notice;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.base.MyBaseFragmentActivity;
import com.libtop.weitu.utils.CollectionUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Sai
 * @ClassName: NoticeActivity
 * @Description: 通知视图
 * @date 9/12/16 19:47
 */
public class NoticeActivity extends MyBaseFragmentActivity implements View.OnClickListener
{
    private static final int TABBAR_INDEX_SYSTEM_NOTICE = 0;
    private static final int TABBAR_INDEX_SCHOOL_NOTICE = 1;

    private ImageView backIv;
    private Button systemNoticeBtn;
    private Button schoolNoticeBtn;
    private ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_view);

        initView();
        initData();
    }


    private void initView()
    {
        backIv = getViewById(R.id.activity_notice_view_back_imageview);
        backIv.setOnClickListener(this);

        systemNoticeBtn = getViewById(R.id.activity_notice_view_system_notice_button);
        systemNoticeBtn.setOnClickListener(this);
        schoolNoticeBtn = getViewById(R.id.activity_notice_view_school_notice_button);
        schoolNoticeBtn.setOnClickListener(this);

        viewPager = getViewById(R.id.activity_notice_view_viewpager);
        viewPager.addOnPageChangeListener(viewPagerOnPageChangeListener);
    }


    private void initData()
    {
        List<Fragment> fragments = generateFragmentList();
        NoticeViewPagerAdapter adapter = new NoticeViewPagerAdapter(getSupportFragmentManager(), fragments);

        viewPager.setAdapter(adapter);
    }


    private void updateTabbarStatus(int index)
    {
        switch (index)
        {
            case TABBAR_INDEX_SYSTEM_NOTICE:
                systemNoticeBtn.setBackgroundResource(R.drawable.blackunder);
                systemNoticeBtn.setTextColor(ContextCompat.getColor(getThis(), R.color.grey3));
                schoolNoticeBtn.setBackgroundColor(ContextCompat.getColor(getThis(), R.color.transparent));
                schoolNoticeBtn.setTextColor(ContextCompat.getColor(getThis(), R.color.grey4));
                break;

            case TABBAR_INDEX_SCHOOL_NOTICE:
                systemNoticeBtn.setBackgroundColor(ContextCompat.getColor(getThis(), R.color.transparent));
                systemNoticeBtn.setTextColor(ContextCompat.getColor(getThis(), R.color.grey4));
                schoolNoticeBtn.setBackgroundResource(R.drawable.blackunder);
                schoolNoticeBtn.setTextColor(ContextCompat.getColor(getThis(), R.color.grey3));
                break;
        }

        viewPager.setCurrentItem(index);
    }


    private List<Fragment> generateFragmentList()
    {
        List<Fragment> fragments = new ArrayList<>(2);
        fragments.add(new DynamicFragment());
        fragments.add(new SchoolNoticeFragment());

        return fragments;
    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.activity_notice_view_back_imageview:
                finish();
                break;

            case R.id.activity_notice_view_system_notice_button:
                updateTabbarStatus(TABBAR_INDEX_SYSTEM_NOTICE);
                break;

            case R.id.activity_notice_view_school_notice_button:
                updateTabbarStatus(TABBAR_INDEX_SCHOOL_NOTICE);
                break;
        }
    }


    private ViewPager.OnPageChangeListener viewPagerOnPageChangeListener = new ViewPager.OnPageChangeListener()
    {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {
        }


        @Override
        public void onPageSelected(int position)
        {
            updateTabbarStatus(position);
        }


        @Override
        public void onPageScrollStateChanged(int state)
        {
        }
    };


    private class NoticeViewPagerAdapter extends FragmentPagerAdapter
    {
        private List<Fragment> fragments;


        public NoticeViewPagerAdapter(FragmentManager fm, List<Fragment> fragments)
        {
            super(fm);
            this.fragments = fragments;
        }


        @Override
        public Fragment getItem(int position)
        {
            return this.fragments.get(position);
        }


        @Override
        public int getCount()
        {
            return CollectionUtil.getSize(this.fragments);
        }
    }
}
