package com.libtop.weitu.activity.search.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


public class MainPageAdapter extends FragmentPagerAdapter
{

    private List<Fragment> data;


    public MainPageAdapter(FragmentManager fm, List<Fragment> data)
    {
        super(fm);
        this.data = data;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        Fragment f = (Fragment) super.instantiateItem(container, position);
        return f;
    }


    @Override
    public int getCount()
    {
        return data.size();
    }


    @Override
    public Fragment getItem(int position)
    {
        return data.get(position);
    }


    public int getItemPosition(Object object)
    {
        return PagerAdapter.POSITION_NONE;
    }


    @Override
    public void setPrimaryItem(View container, int position, Object object)
    {

    }
}
