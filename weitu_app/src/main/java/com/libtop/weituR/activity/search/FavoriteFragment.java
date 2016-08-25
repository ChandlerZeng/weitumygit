package com.libtop.weituR.activity.search;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.search.adapter.BookFavorateAdapter;
import com.libtop.weituR.activity.search.dto.BookItem;
import com.libtop.weituR.base.impl.NotifyFragment;
import com.libtop.weituR.http.HttpRequest;
import com.libtop.weituR.utils.CheckUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import okhttp3.Call;

public class FavoriteFragment extends NotifyFragment {

	@Bind( R.id.list)
	ListView listview;

	private List<BookItem> data;
	private BookFavorateAdapter adapter;
	private boolean isCreate = false;
	private BookDetailFragment mParent;

	public static FavoriteFragment Instance() {
		FavoriteFragment fragment = new FavoriteFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (data == null) {
			data = new ArrayList<BookItem>();
		}
		mParent = (BookDetailFragment)getParentFragment();
		adapter = new BookFavorateAdapter(mContext, data);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.fragment_book_layout;
	}

	@Override
	public void onCreation(View root) {
		initView();
	}

	private void initView() {
		listview.setAdapter(adapter);
	}

	public void loadInfo(List<BookItem> temps) {
		if (data == null) {
			data = temps;
		} else {
			data.clear();
			data.addAll(temps);
		}
	}

	@Override
	public void notify(String data) {
		if (!isCreate) {
			loadData();
			isCreate = true;
		}
	}

	private void loadData() {
		data.clear();
		adapter.notifyDataSetChanged();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("lid",mParent.getSchool());
		params.put("bid", mParent.getDto().mid);
		params.put("method", "book.getIndex");
		showLoding();
		HttpRequest.loadWithMapSec(params, new HttpRequest.CallBackSec() {
			@Override
			public void onError(Call call, Exception e, int id) {

			}

			@Override
			public void onResponse(String json, int id) {
				dismissLoading();
				if (CheckUtil.isNullTxt(json)) {
					Toast.makeText(mContext, "请求超时，请稍后再试", Toast.LENGTH_SHORT).show();
					return;
				}
				if (!CheckUtil.isNull(json)) {
					try {
						JSONArray array = new JSONArray(json);
						for (int i = 0; i < array.length(); i++) {
							JSONObject item = array.getJSONObject(i);
							BookItem book = new BookItem();
							book.of(item);
							data.add(book);
						}
						adapter.notifyDataSetChanged();
					} catch (JSONException e) {
						e.printStackTrace();
						Toast.makeText(mContext, "数据解析失败", Toast.LENGTH_SHORT)
								.show();
					}
				}
			}
		});
	}


}
