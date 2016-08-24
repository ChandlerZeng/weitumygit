package com.libtop.weituR.activity.search;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;

import com.libtop.weitu.R;
import com.libtop.weituR.base.impl.NotifyFragment;

import butterknife.Bind;

public class CatelogFragment extends NotifyFragment {

	@Bind(R.id.webview)
	WebView webview;

	private String content = "";
	private boolean isCreate = false;

	public static CatelogFragment Instance() {
		CatelogFragment fragment = new CatelogFragment();
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isCreate = false;
	}

	@Override
	protected int getLayoutId() {
		return R.layout.fragment_book_catlog;
	}

	@Override
	public void onCreation(View root) {
		initView();
	}

	private void initView() {
		WebSettings settings = webview.getSettings();
		settings.setBlockNetworkImage(true);
		settings.setLayoutAlgorithm(LayoutAlgorithm.NORMAL);
		//int pxSize = getResources().getDimensionPixelSize(R.dimen.text_size_15);
		settings.setDefaultTextEncodingName("utf-8");
		String meta = "<html><head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0,max-scale=1.0, user-scalable=no\">";
		meta += meta
				+ "</head><body style=\"color:#444a4d !important;font-size:15"
				+ "px;line-height:140%;word-break: break-all;word-wrap: break-word;"
				+ "letter-spacing: 0.1mm;backgroud-color:#f3f4f6\">";
		meta += "<div style=\"margin:3px 3px 3px 3px;border-radius: 4px 4px 4px 4px;backgroud-color:#ffffff;\"><pre width=\"100%\">"
				+ content + "</pre></div>";
		content = meta + "</body></html>";
		isCreate = true;
		webview.loadDataWithBaseURL(null, content, "text/html", "utf-8",
				"about:blank");
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
		// if (webview.getScrollY() == 0) {
		// activity.setHeardVisibleHeight((int) ((event
		// .getRawY() - preY)));
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

	@Override
	public void notify(String data) {
		if (!isCreate) {
			// String meta =
			// "<html><head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0,max-scale=1.0, user-scalable=no\">";
			// meta += meta
			// +
			// "</head><body style=\"color:#444a4d !important;font-size:16px;line-height:140%;word-break: break-all;word-wrap: break-word;"
			// + "letter-spacing: 0.3mm;backgroud-color:#f3f4f6\">";
			// meta +=
			// "<div style=\"margin:3px 3px 3px 3px;border-radius: 4px 4px 4px 4px;backgroud-color:#ffffff;\"><pre width=\"100%\">"
			// + content + "</pre></div>";
			// content += "</body></html>";
			webview.loadDataWithBaseURL(null, content, "text/html", "utf-8",
					"about:blank");
			isCreate = true;
		}
	}
}
