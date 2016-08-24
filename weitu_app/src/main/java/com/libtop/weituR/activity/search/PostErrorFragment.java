package com.libtop.weituR.activity.search;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.ContentActivity;
import com.libtop.weituR.base.BaseFragment;
import com.libtop.weituR.http.HttpRequest;
import com.libtop.weituR.tool.Preference;
import com.libtop.weituR.utils.CheckUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;

public class PostErrorFragment extends BaseFragment{
	
	@Bind(R.id.id1)
	CheckBox id1;
	@Bind(R.id.id2)
	CheckBox id2;
	@Bind(R.id.id3)
	CheckBox id3;
	@Bind(R.id.id4)
	CheckBox id4;
	@Bind(R.id.id5)
	CheckBox id5;
	@Bind(R.id.id6)
	CheckBox id6;
	@Bind(R.id.id7)
	CheckBox id7;
	@Bind(R.id.id8)
	CheckBox id8;
	@Bind(R.id.id9)
	CheckBox id9;


	@Bind(R.id.content)
	EditText content;
	private String mid;

	private void init() {
		mid = mContext.getIntent().getExtras().getString("mid");
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_post_error;
	}

	@Override
	public void onCreation(View root) {
		init();
	}

	@Nullable
	@OnClick({R.id.back_btn,R.id.send_info})
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back_btn:
			onBackPressed();
			break;
		case R.id.send_info:
			if(CheckUtil.isNull(content.getText())){
				showToast("写点什么吧");
				return;
			}
			sendBack(content.getText() + "");
			break;
		}
	}

	@Override
	public void onBackPressed() {
		((ContentActivity)mContext).popBack();
	}

	private void sendBack(String content) {
		showLoding();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("method", "defect.save");
		params.put("uid", Preference.instance(mContext).getString(Preference.uid));
		params.put("mid", mid);
		params.put("type", getCheck());
		params.put("content", content);
		HttpRequest.loadWithMapSec(params, new HttpRequest.CallBackSec() {
			@Override
			public void onError(Call call, Exception e, int id) {

			}

			@Override
			public void onResponse(String json, int id) {
				dismissLoading();
				if (CheckUtil.isNull(json)) {
					showToast("网络连接超时，请稍后再试");
				} else {
					showToast("感谢您提的宝贵意见");
					onBackPressed();
				}
			}
		});
	}

	private String getCheck(){
		String types = "";
		if(id1.isChecked()){
			types += "1,";
		}
		if(id2.isChecked()){
			types += "2,";
		}
		if(id3.isChecked()){
			types += "3,";
		}
		if(id4.isChecked()){
			types += "4,";
		}
		if(id5.isChecked()){
			types += "5,";
		}
		if(id6.isChecked()){
			types += "6,";
		}
		if(id7.isChecked()){
			types += "7,";
		}
		if(id8.isChecked()){
			types += "8,";
		}
		if(id9.isChecked()){
			types += "9,";
		}
		if(!CheckUtil.isNull(types)){
			types.substring(0, types.length()-2);
		}
		return types;
	}
}
