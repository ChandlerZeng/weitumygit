package com.libtop.weitu.activity.main.clickHistory;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.RadioGroup;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.search.adapter.MainPageAdapter;
import com.libtop.weitu.base.impl.NotifyFragment;
import com.libtop.weitu.widget.NoSlideViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by LianTu on 2016/7/15.
 */
public class ClickHistoryFragment extends NotifyFragment {
    @Bind(R.id.viewpager)
    NoSlideViewPager mViewPager;
    @Bind(R.id.radioGroup)
    RadioGroup mRadioGroup;

    private List<Fragment> mFrags;
    private MainPageAdapter mAdapter;

    private int pageIndex = 0;

    public static final int ALL=0,BOOK=1,VIDEO=2,AUDIO=3,DOC=4,IMAGE=5;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFrags=new ArrayList<Fragment>();
        AllHistoryFragment f1 = new AllHistoryFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", 0);
        f1.setArguments(bundle);

        AllHistoryFragment f2 = new AllHistoryFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putInt("type", 5);
        f2.setArguments(bundle2);

        AllHistoryFragment f3 = new AllHistoryFragment();
        Bundle bundle3 = new Bundle();
        bundle3.putInt("type", 1);
        f3.setArguments(bundle3);

        AllHistoryFragment f4 = new AllHistoryFragment();
        Bundle bundle4 = new Bundle();
        bundle4.putInt("type", 2);
        f4.setArguments(bundle4);

        AllHistoryFragment f5 = new AllHistoryFragment();
        Bundle bundle5 = new Bundle();
        bundle5.putInt("type", 3);
        f5.setArguments(bundle5);

        AllHistoryFragment f6 = new AllHistoryFragment();
        Bundle bundle6 = new Bundle();
        bundle6.putInt("type", 4);
        f6.setArguments(bundle6);

        mFrags.add(f1);
        mFrags.add(f2);
        mFrags.add(f3);
        mFrags.add(f4);
        mFrags.add(f5);
        mFrags.add(f6);

        mAdapter=new MainPageAdapter(getChildFragmentManager(),mFrags);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_history_layout;
    }


    @Override
    public void onCreation(View root) {
        initView();
    }

    private void initView(){
        mViewPager.setPagingEnabled(false);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(0);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                onCheckedChanged1(group,checkedId);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void notify(String data) {
        int current=mViewPager.getCurrentItem();
        int max=mAdapter.getCount();
        for (int i=0;i<max;i++){
            NotifyFragment frag=(NotifyFragment)mAdapter.getItem(i);
            frag.reSet();
            if (current==i){
                frag.load();
            }
        }
    }

    public void onCheckedChanged1(RadioGroup group, int checkedId) {
        pageIndex=-1;
        switch (checkedId){
            case R.id.search_all:{
                pageIndex = ALL;
            }
            break;
            case R.id.search_books:{
                pageIndex=BOOK;
            }
            break;
            case R.id.search_video:
                pageIndex=VIDEO;
                break;
            case R.id.search_audio:
                pageIndex=AUDIO;
                break;
            case R.id.search_document:
                pageIndex=DOC;
                break;
            case R.id.search_images:
                pageIndex=IMAGE;
                break;
        }
        mViewPager.setCurrentItem(pageIndex);
    }

}
