package com.libtop.weituR.activity.main.upload;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.search.SearchActivity;
import com.libtop.weituR.activity.search.adapter.MainPageAdapter;
import com.libtop.weituR.base.impl.NotifyFragment;
import com.libtop.weituR.widget.NoSlideViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Zeng on 2016/8/11.
 */
public class NewestUploadFragment extends NotifyFragment{
    @Bind(R.id.newest_upload_viewpager)
    NoSlideViewPager mViewPager;
    @Bind(R.id.radioGroup)
    RadioGroup mRadioGroup;
    @Bind(R.id.newest_upload_btn_back)
    ImageView mBtnBack;
    @Bind(R.id.newest_upload_search_top)
    ImageView mSearch;

    private List<Fragment> mFrags;
    private MainPageAdapter mAdapter;

    private int pageIndex = 0;

    public static final int ALL=0,BOOK=1,VIDEO=2,AUDIO=3,DOC=4,IMAGE=5;

    private ArrayAdapter<CharSequence> adapter = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFrags=new ArrayList<Fragment>();
        NewestUploadContentFragment f1 = new NewestUploadContentFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", 0);
        f1.setArguments(bundle);

        NewestUploadContentFragment f2 = new NewestUploadContentFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putInt("type", 5);
        f2.setArguments(bundle2);

        NewestUploadContentFragment f3 = new NewestUploadContentFragment();
        Bundle bundle3 = new Bundle();
        bundle3.putInt("type", 1);
        f3.setArguments(bundle3);

        NewestUploadContentFragment f4 = new NewestUploadContentFragment();
        Bundle bundle4 = new Bundle();
        bundle4.putInt("type", 2);
        f4.setArguments(bundle4);

        NewestUploadContentFragment f5 = new NewestUploadContentFragment();
        Bundle bundle5 = new Bundle();
        bundle5.putInt("type", 3);
        f5.setArguments(bundle5);

        NewestUploadContentFragment f6 = new NewestUploadContentFragment();
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
    public void onBackPressed() {
        mContext.finish();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_newest_upload_layout;
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
            case R.id.newest_upload_all:{
                pageIndex = ALL;
            }
            break;
            case R.id.newest_upload_books:{
                pageIndex=BOOK;
            }
            break;
            case R.id.newest_upload_video:
                pageIndex=VIDEO;
                break;
            case R.id.newest_upload_audio:
                pageIndex=AUDIO;
                break;
            case R.id.newest_upload_document:
                pageIndex=DOC;
                break;
            case R.id.newest_upload_images:
                pageIndex=IMAGE;
                break;
        }
        mViewPager.setCurrentItem(pageIndex);
    }

    @Nullable
    @OnClick({R.id.newest_upload_btn_back,R.id.newest_upload_search_top})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.newest_upload_btn_back:
                onBackPressed();
                break;
            case R.id.newest_upload_search_top:
                mContext.startActivity(null, SearchActivity.class);
                break;
        }
    }
}
