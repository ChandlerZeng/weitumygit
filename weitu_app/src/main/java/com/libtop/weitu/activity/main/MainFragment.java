package com.libtop.weitu.activity.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.search.SearchActivity;
import com.libtop.weitu.activity.search.adapter.MainPageAdapter;
import com.libtop.weitu.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;


/**
 * Created by LianTu on 2016-9-6.
 */
public class MainFragment extends BaseFragment
{
    @Bind(R.id.open_alarm)
    ImageView openAlarm;
    @Bind(R.id.btn_main_theme)
    Button btnMainTheme;
    @Bind(R.id.btn_main_resource)
    Button btnMainResource;
    @Bind(R.id.viewpager)
    ViewPager viewpager;


    private List<Fragment> datas;
    private MainPageAdapter adapter;


    @Override
    protected int getLayoutId()
    {
        return R.layout.activity_main_layout;
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        datas = new ArrayList<Fragment>();
        adapter = new MainPageAdapter(mFm, datas);
    }


    private void initView()
    {
        initViewPager();

    }


    private void initViewPager()
    {
        viewpager.setAdapter(adapter);
        ThemeFragment themeFragment =new  ThemeFragment();
        datas.add(themeFragment);
        ResourceFragment resourceFragment =new  ResourceFragment();
        datas.add(resourceFragment);
        adapter.notifyDataSetChanged();


        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {

            }


            @Override
            public void onPageSelected(int position)
            {
                switch (position)
                {
                    case 0:
                        themeClick();
                        break;
                    case 1:
                        resourceClick();
                        break;
                }
            }


            @Override
            public void onPageScrollStateChanged(int state)
            {

            }
        });
    }


    @Override
    public void onCreation(View root)
    {
        super.onCreation(root);
        initView();
    }

    private void searchClick()
    {
        mContext.startActivity(null, SearchActivity.class);
    }


    private void resourceClick()
    {
        btnMainResource.setBackgroundResource(R.drawable.blackunder);
        btnMainResource.setTextColor(ContextCompat.getColor(mContext, R.color.grey3));
        btnMainTheme.setBackgroundColor(ContextCompat.getColor(mContext, R.color.transparent));
        btnMainTheme.setTextColor(ContextCompat.getColor(mContext, R.color.grey4));
        viewpager.setCurrentItem(1);

    }


    private void themeClick()
    {
        btnMainResource.setBackgroundColor(ContextCompat.getColor(mContext, R.color.transparent));
        btnMainResource.setTextColor(ContextCompat.getColor(mContext, R.color.grey4));
        btnMainTheme.setBackgroundResource(R.drawable.blackunder);
        btnMainTheme.setTextColor(ContextCompat.getColor(mContext, R.color.grey3));
        viewpager.setCurrentItem(0);
    }


    private void alarmClick()
    {

    }

    @OnClick({R.id.open_alarm, R.id.btn_main_theme, R.id.btn_main_resource, R.id.search_top})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.open_alarm:
                alarmClick();
                break;
            case R.id.btn_main_theme:
                themeClick();
                break;
            case R.id.btn_main_resource:
                resourceClick();
                break;
            case R.id.search_top:
                searchClick();
                break;
        }
    }

}
