package com.libtop.weituR.activity.search;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.ContentActivity;
import com.libtop.weituR.activity.search.adapter.HistoryAdapter;
import com.libtop.weituR.base.BaseFragment;
import com.libtop.weituR.dao.HistoryBo;
import com.libtop.weituR.dao.bean.History;
import com.libtop.weituR.tool.Preference;
import com.libtop.weituR.utils.ContantsUtil;

import java.util.List;

import butterknife.Bind;

public class HistoryFragment extends BaseFragment {

	@Bind(R.id.list)
	GridView mGrid;
	@Bind(R.id.trash)
	Button mTrashBtn;

	private List<History> mDatas;
	private HistoryAdapter mAdapter;
	private HistoryBo mBo;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBo = new HistoryBo(mContext);
		mDatas = mBo.lists(Preference.instance(mContext).getString(
				Preference.SchoolCode));
		ContantsUtil.UPDATE_HISTORY = true;
		mAdapter = new HistoryAdapter(mContext, mDatas);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.fragment_history_list;
	}

	@Override
	public void onCreation(View root) {
		initView();
	}

	public void onResume() {
		super.onResume();
		if (!ContantsUtil.UPDATE_HISTORY) {
			mDatas.clear();
			mDatas.addAll(mBo.lists(Preference.instance(mContext).getString(
					Preference.SchoolCode)));
			ContantsUtil.UPDATE_HISTORY = true;
		}
	}

	private void initView() {
		mGrid.setAdapter(mAdapter);
		mGrid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Bundle bundle = new Bundle();
				History item = mDatas.get(position);
				bundle.putString("isbn", item.getIsbn());
				bundle.putString("name", item.getName());
				bundle.putString("auth", item.getAuth());
				bundle.putString("report", item.getReport());
				bundle.putString("cover", item.getUrl());
				bundle.putString("publisher", item.getPublisher());
				bundle.putString("school", Preference.instance(mContext).getString(
						Preference.SchoolCode));
				bundle.putString(ContentActivity.FRAG_CLS,BookDetailFragment2.class.getName());
				mContext.startActivity(bundle, ContentActivity.class);
			}
		});
		mTrashBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mBo.clear();
				mDatas.clear();
				mAdapter.notifyDataSetChanged();
			}
		});
	}


}
