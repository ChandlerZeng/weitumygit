package com.libtop.weitu.activity.main.rank;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentFragment;
import com.libtop.weitu.activity.search.adapter.MainPageAdapter;
import com.libtop.weitu.widget.NoSlideViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnPageChange;


/**
 * Created by LianTu on 2016/7/15.
 */
public class RankFragment extends ContentFragment {
    @Bind(R.id.viewpager)
    NoSlideViewPager mViewPager;
    @Bind(R.id.radioGroup)
    RadioGroup mRadioGroup;
    @Bind(R.id.back_btn)
    ImageView backBtn;
    @Bind(R.id.title)
    TextView title;

    private List<Fragment> mFrags;
    private MainPageAdapter mAdapter;

    private int pageIndex = 0;

    public static final int HOT_SUB = 0, HOT_RES = 1, NEWEST_SUB = 2, NEWEST_RES = 3;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFrags = new ArrayList<Fragment>();
        RankPageFragment f1 = new RankPageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", 5);
        f1.setArguments(bundle);

        RankPageFragment f2 = new RankPageFragment();
        Bundle bundle2 = new Bundle();
        bundle2.putInt("type", 1);
        f2.setArguments(bundle2);

        RankPageFragment f3 = new RankPageFragment();
        Bundle bundle3 = new Bundle();
        bundle3.putInt("type", 2);
        f3.setArguments(bundle3);

        RankPageFragment f4 = new RankPageFragment();
        Bundle bundle4 = new Bundle();
        bundle4.putInt("type", 3);
        f4.setArguments(bundle4);

        mFrags.add(f1);
        mFrags.add(f2);
        mFrags.add(f3);
        mFrags.add(f4);

        mAdapter = new MainPageAdapter(getChildFragmentManager(), mFrags);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_rank_layout;
    }


    @Override
    public void onCreation(View root) {
        initView();
    }


    private void initView() {
        title.setText("排行榜");
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.finish();
            }
        });
        mViewPager.setPagingEnabled(true);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(0);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                onCheckedChanged1(group, checkedId);
            }
        });
    }

    @Nullable
    @OnPageChange(value = R.id.viewpager)
    public void onPageSelected(int position)
    {
        switch (position)
        {
            case HOT_SUB:
                mRadioGroup.check(R.id.hot_subject);
                break;
            case HOT_RES:
                mRadioGroup.check(R.id.hot_resource);
                break;
            case NEWEST_SUB:
                mRadioGroup.check(R.id.newest_subject);
                break;
            case NEWEST_RES:
                mRadioGroup.check(R.id.newest_resource);
                break;
        }
    }


    public void onCheckedChanged1(RadioGroup group, int checkedId) {
        pageIndex = -1;
        switch (checkedId) {
            case R.id.hot_subject: {
                pageIndex = HOT_SUB;
            }
            break;
            case R.id.hot_resource: {
                pageIndex = HOT_RES;
            }
            break;
            case R.id.newest_subject:
                pageIndex = NEWEST_SUB;
                break;
            case R.id.newest_resource:
                pageIndex = NEWEST_RES;
                break;
        }
        mViewPager.setCurrentItem(pageIndex);
    }
}
