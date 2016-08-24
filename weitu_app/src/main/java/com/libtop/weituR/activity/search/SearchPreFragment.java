package com.libtop.weituR.activity.search;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.search.adapter.MainPageAdapter;
import com.libtop.weituR.base.impl.NotifyFragment;
import com.libtop.weituR.utils.ContantsUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class SearchPreFragment extends NotifyFragment {

	@Bind(R.id.viewpager)
	ViewPager mViewpager;
	@Bind(R.id.radioGroup)
	RadioGroup mGroup;

	private List<Fragment> mFragments;
	private MainPageAdapter mAdapter;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ContantsUtil.UPDATE_SEARCH = true;
		mFragments = new ArrayList<Fragment>();
		mFragments.add(new SearchListFragment());
		mFragments.add(new HistoryFragment());
		mAdapter = new MainPageAdapter(getChildFragmentManager(), mFragments);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.fragment_search_pre;
	}

	@Override
	public void onCreation(View root) {
		initView();
	}

	private void initView() {
		mViewpager.setAdapter(mAdapter);
		mViewpager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				switch (position) {
				case 0:
					mGroup.check(R.id.search_history);
					break;
				default:
					mGroup.check(R.id.click_history);
					break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		mGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int id) {
				switch (id) {
				case R.id.search_history:
					mViewpager.setCurrentItem(0);
					break;
				case R.id.click_history:
					mViewpager.setCurrentItem(1);
					break;
				}
			}
		});
	}

	@Override
	public void notify(String data) {
		// TODO Auto-generated method stub
		
	}
}
