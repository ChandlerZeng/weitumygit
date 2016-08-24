package com.libtop.weituR.activity.search;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.search.adapter.SearchAdapter;
import com.libtop.weituR.base.BaseFragment;
import com.libtop.weituR.dao.SearchBo;
import com.libtop.weituR.dao.bean.Search;
import com.libtop.weituR.utils.ContantsUtil;

import java.util.List;

import butterknife.Bind;

public class SearchListFragment extends BaseFragment {

	@Bind(R.id.list)
	ListView mList;
	@Bind(R.id.trash)
	Button mTrashBtn;

	private List<Search> mData;
	private SearchAdapter mAdapter;
	private SearchBo mBo;
	private SearchActivity mActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBo = new SearchBo(mContext);
		mData = mBo.lists();
		ContantsUtil.UPDATE_SEARCH = true;
		mAdapter = new SearchAdapter(mContext, mData);
		mActivity = (SearchActivity)mContext;
	}

	@Override
	protected int getLayoutId() {
		return R.layout.fragment_search_list;
	}


	@Override
	public void onResume() {
		super.onResume();
		if (!ContantsUtil.UPDATE_SEARCH) {
			mData.clear();
			mData.addAll(mBo.lists());
			ContantsUtil.UPDATE_SEARCH = true;
		}
	}

	@Override
	public void onCreation(View root) {
		initView();
	}

	private void initView() {
		mList.setAdapter(mAdapter);
		mList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
									int position, long arg3) {
				mActivity.search(mData.get(position).getName());
			}
		});
		mTrashBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mData.clear();
				mBo.clear();
				mAdapter.notifyDataSetChanged();
			}
		});
	}
}
