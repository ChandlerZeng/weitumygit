package com.libtop.weitu.activity.search;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.search.dto.DetailDto;
import com.libtop.weitu.base.impl.NotifyFragment;

import butterknife.Bind;

public class InfoFragment extends NotifyFragment{

	@Bind(R.id.webview)
	TextView webview;
	@Bind(R.id.tv_catalog)
	TextView tvCatalog;

	@Bind(R.id.auther)
	TextView auther;
	@Bind(R.id.tv_publisher)
	TextView tvPublisher;
	@Bind(R.id.tv_date)
	TextView tvDate;
	@Bind(R.id.tv_isbn)
	TextView tvIsbn;

	private String content = "";
	private BookDetailFragment mParent;

	private DetailDto dto;

	public static InfoFragment Instance() {
		InfoFragment fragment = new InfoFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mParent=(BookDetailFragment)getParentFragment();
	}

	@Override
	protected int getLayoutId() {
		return R.layout.fragment_book_info;
	}

	@Override
	public void onCreation(View root) {
		initView();
	}

	private void initView() {
		if (!TextUtils.isEmpty(content)) {
			content = content.replaceAll("\\r", "\r\n");
			webview.setText(content);
		}
		if (dto!=null){
			webview.setText(dto.introduction);
			if (!TextUtils.isEmpty(dto.catalog)){
				dto.catalog = dto.catalog.replaceAll("\\r", "\r\n");
				tvCatalog.setText(dto.catalog);
			}
		}

		auther.setText("作    者  "+dto.author);
		if (dto.publisher!=null){
			String[] strings = dto.publisher.split(",");
			if (strings!=null && strings.length >0){
				String publisher = strings[0];
				String date = strings[(strings.length-1)];
				tvPublisher.setText("出版社  "+publisher);
				tvDate.setText("日    期  "+date);
			}
		}
		tvIsbn.setText("I S B N  "+dto.isbn);
	}

	public void loadInfo(String info) {
		this.content = info;
	}

	public void loadInfo(DetailDto dto) {
		this.dto = dto;
	}


	@Override
	public void notify(String data) {

	}


}
