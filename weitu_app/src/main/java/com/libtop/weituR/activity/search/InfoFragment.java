package com.libtop.weituR.activity.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.search.dto.DetailDto;
import com.libtop.weituR.base.impl.NotifyFragment;
import com.libtop.weituR.dao.BookBo;
import com.libtop.weituR.dao.bean.Book;
import com.libtop.weituR.utils.ContantsUtil;

import butterknife.Bind;
import butterknife.OnClick;

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

	private BookBo bo;
	private String content = "";
	private BookDetailFragment2 mParent;

	private DetailDto dto;

	public static InfoFragment Instance() {
		InfoFragment fragment = new InfoFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		activity = (BookDetailActivity)mContext;
		mParent=(BookDetailFragment2)getParentFragment();
		bo = new BookBo(mContext);
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
		// WebSettings settings = webview.getSettings();
		// settings.setBlockNetworkImage(true);
		// settings.setDefaultTextEncodingName("utf-8");
		// String meta =
		// "<html><head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0,max-scale=1.0, user-scalable=no\">";
		// meta += meta +
		// "</head><body style=\"color:#444a4d !important;font-size:16px;line-height:140%;"
		// +
		// "letter-spacing: 0.3mm;backgroud-color:#f3f4f6\">";
		// meta +=
		// "<div style=\"margin:3px 3px 3px 3px;border-radius: 4px 4px 4px 4px;backgroud-color:#ffffff;\">"
		// + content + "</div>";
		// meta += "</body></html>";
		// webview.loadDataWithBaseURL(null, meta, "text/html", "utf-8",
		// "about:blank");
		// webview.setOnTouchListener(new OnTouchListener() {
		// private float preY = -1;
		//
		// @Override
		// public boolean onTouch(View arg0, MotionEvent event) {
		// switch (event.getAction()) {
		// case MotionEvent.ACTION_DOWN:
		// preY = event.getRawY();
		// break;
		// case MotionEvent.ACTION_MOVE:
		// if (webview.getScrollY() < 10) {
		// activity.setHeardVisibleHeight((int) ((event.getRawY() - preY)));
		// webview.scrollTo(0, 0);
		// }
		// preY = event.getRawY();
		// break;
		// default:
		// preY = -1;
		// break;
		// }
		// return false;
		// }
		// });
	}

	public void loadInfo(String info) {
		this.content = info;
	}

	public void loadInfo(DetailDto dto) {
		this.dto = dto;
	}

	@Nullable
	@OnClick({R.id.want,R.id.favorate})
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.favorate:
			saveFavorate();
			Toast.makeText(mContext, "收藏成功", Toast.LENGTH_SHORT).show();
			break;
		case R.id.want:
			saveWant();
			Toast.makeText(mContext, "设置成功", Toast.LENGTH_SHORT).show();
			break;
		}
	}

	private void saveFavorate() {
		DetailDto dto = mParent.getDto();
		Book book = new Book();
		book.setIsbn(dto.isbn);
		book.setAuthor(dto.author);
		book.setCover(dto.cover);
		book.setName(dto.title);
		book.setFavorate(true);
		book.setSee_pre(false);
		book.setWant_see(false);
		bo.saveUpdate(book);
		ContantsUtil.FAVORATE = false;
	}

	private void saveWant() {
		DetailDto dto = mParent.getDto();
		Book book = new Book();
		book.setIsbn(dto.isbn);
		book.setAuthor(dto.author);
		book.setCover(dto.cover);
		book.setName(dto.title);
		book.setFavorate(false);
		book.setSee_pre(true);
		book.setWant_see(false);
		bo.saveUpdate(book);
		ContantsUtil.WANT = false;
	}

	private void saveSee() {
		DetailDto dto = mParent.getDto();
		Book book = new Book();
		book.setIsbn(dto.isbn);
		book.setAuthor(dto.author);
		book.setCover(dto.cover);
		book.setName(dto.title);
		book.setFavorate(false);
		book.setSee_pre(false);
		book.setWant_see(true);
		bo.saveUpdate(book);
		ContantsUtil.SEEPRE = false;
	}

	@Override
	public void notify(String data) {
		// TODO Auto-generated method stub

	}


}
