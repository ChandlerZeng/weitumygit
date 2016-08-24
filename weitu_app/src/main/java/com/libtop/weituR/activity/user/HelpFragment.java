package com.libtop.weituR.activity.user;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weituR.base.BaseFragmentDialog;
import com.libtop.weituR.utils.FileUtil;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 帮助界面
 * @author Administrator
 *
 */
public class HelpFragment extends BaseFragmentDialog{
	
	@Bind(R.id.back_btn)
	ImageButton mBackBtn;
	@Bind(R.id.web_content)
	WebView mWebview;
	@Bind(R.id.title)
	TextView mTitleText;
	
	private String mContent = "";

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getDialog().getWindow().getAttributes().windowAnimations = R.style.dialog_fragment_fade_anim;		
	}

	@Override
	protected int getLayoutId() {
		return R.layout.fragment_about_layout;
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		mContent = FileUtil.readTxtFromAsset(mContext, "html/help.html");
	}

	@Override
	public void onCreation(View root) {
		init();
	}

	private void init(){
		//网页设置
		WebSettings settings = mWebview.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setBlockNetworkImage(true);
		settings.setDefaultTextEncodingName("utf-8");
		mWebview.setBackgroundColor(0);
		mWebview.getBackground().setAlpha(0);
		mWebview.loadDataWithBaseURL(null, mContent, "text/html", "utf-8",
				"about:blank");
		
		mTitleText.setText("帮助中心");
//		mBackBtn.setOnClickListener(this);
	}

	@Nullable
	@OnClick(R.id.back_btn)
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.back_btn:
			dismiss();
			break;
		}
	}

}
