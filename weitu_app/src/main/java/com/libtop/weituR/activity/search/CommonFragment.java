package com.libtop.weituR.activity.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Toast;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.ContentActivity;
import com.libtop.weituR.activity.login.LoginFragment;
import com.libtop.weituR.activity.search.adapter.CommonAdapter;
import com.libtop.weituR.activity.search.dto.CommonDto;
import com.libtop.weituR.base.impl.ImgFragment;
import com.libtop.weituR.http.HttpRequest;
import com.libtop.weituR.tool.Preference;
import com.libtop.weituR.utils.CheckUtil;
import com.libtop.weituR.widget.listview.ScrollRefListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;

public class CommonFragment extends ImgFragment{

	@Bind(R.id.common_list)
	ScrollRefListView listView;

	private String bid;
	private boolean hasData = false;
	private int curentPage = 1;
	private CommonAdapter listAdapter;
	private List<CommonDto> data;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setGuesture(true);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_common_list;
	}

	@Override
	public void onCreation(View root) {
		init();
	}

	private void init() {
		Bundle bundle = ((ContentActivity)mContext).getCurrentExtra();
		bid = bundle.getString("bid");

		data = new ArrayList<CommonDto>();
		listAdapter = new CommonAdapter(mContext, data);
		listView.setXListViewListener(new ScrollRefListView.IXListViewListener() {
			@Override
			public void onLoadMore() {
				if (hasData) {
					getCommons();
				}
			}
		});

		listView.setAdapter(listAdapter);
		listView.setPullLoadEnable(false);
		getCommons();
	}

	private void getCommons() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", Preference.instance(mContext)
				.getString(Preference.uid));
		params.put("method", "comment.list");
		params.put("bid", bid);
		params.put("page", curentPage);
		if (curentPage == 1) {
			showLoding();
		}
		HttpRequest.loadWithMapSec(params, new HttpRequest.CallBackSec() {
			@Override
			public void onError(Call call, Exception e, int id) {

			}

			@Override
			public void onResponse(String json, int id) {
				if (curentPage == 1) {
					dismissLoading();
				}
				if (CheckUtil.isNullTxt(json)) {
					Toast.makeText(mContext, "请求超时，请稍后再试", Toast.LENGTH_SHORT).show();
					return;
				}
				if (!CheckUtil.isNull(json)) {
					try {
						if (curentPage == 1) {
							data.clear();
						}
						JSONArray array = new JSONArray(json);
						if (array.length() < 10) {
							hasData = false;
							listView.setPullLoadEnable(false);
						} else {
							hasData = true;
							listView.setPullLoadEnable(true);
						}
						for (int i = 0; i < array.length(); i++) {
							CommonDto dto = new CommonDto();
							dto.of(array.getJSONObject(i));
							data.add(dto);
						}
						listAdapter.notifyDataSetChanged();
						curentPage++;
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	@Override
	public void onBackPressed() {
		((ContentActivity)mContext).popBack();
	}

	@Nullable
	@OnClick({R.id.back_btn,R.id.new_btn})
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back_btn:
			onBackPressed();
			break;
		case R.id.new_btn:
			if (CheckUtil.isNull(Preference.instance(mContext).getString(Preference.UserName))) {
				Bundle bundle=new Bundle();
				bundle.putString(ContentActivity.FRAG_CLS, LoginFragment.class.getName());
				bundle.putBoolean(ContentActivity.FRAG_ISBACK, true);
				bundle.putBoolean(ContentActivity.FRAG_WITH_ANIM,true);
				mContext.startActivity(bundle, ContentActivity.class);
			} else {
				newCommonFragment();
			}
			break;
		}
	}

	private void newCommonFragment() {
		String tag = "favorate_fragment";
		FragmentManager manager = mContext.getSupportFragmentManager();
		NewComFragment tempFragment = (NewComFragment) mFm.findFragmentByTag(tag);
		if (tempFragment == null) {
			tempFragment = NewComFragment.Instance(bid);
			tempFragment.setCallBack(new NewComFragment.CallBack() {
				@Override
				public void callBack() {
					curentPage = 1;
					getCommons();
				}
			});
		}
		if (!tempFragment.isAdded()) {
			tempFragment.show(manager, tag);
		}
	}

}
