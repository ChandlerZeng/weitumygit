package com.libtop.weitu.activity.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Toast;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.login.LoginFragment;
import com.libtop.weitu.base.BaseFragmentDialog;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.CheckUtil;
import com.libtop.weitu.utils.ContantsUtil;
import com.libtop.weitu.widget.dialog.TranLoading;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 创建评论
 * 
 * @author longbh
 * 
 */
public class NewComFragment extends BaseFragmentDialog{

	@Bind(R.id.back_btn)
	ImageButton backBtn;
	@Bind(R.id.level)
	RatingBar level;
	@Bind(R.id.content)
	EditText content;
	@Bind(R.id.submit)
	Button submit;

	private CallBack callback;
	private String bid;

	private Preference prefrence;
	private TranLoading dialog;

	public static NewComFragment Instance(String bid) {
		NewComFragment fragment = new NewComFragment();
		Bundle bundle = new Bundle();
		bundle.putString("bid", bid);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bid = getArguments().getString("bid");
		dialog = new TranLoading(mContext);
		prefrence = Preference.instance(mContext);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.fragment_new_common;
	}


	@Nullable
	@OnClick({R.id.back_btn,R.id.submit})
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.back_btn:
			onBackPressed();
			break;
		case R.id.submit:
			if (CheckUtil.isNull(prefrence.getString(Preference.UserName))) {
				Bundle b=new Bundle();
				b.putString(ContentActivity.FRAG_CLS, LoginFragment.class.getName());
				mContext.startActivity(b,ContentActivity.class);
			} else {
				submit();
			}
			break;
		}
	}

	private void submit() {
		if (CheckUtil.isNull(content.getText())) {
			Toast.makeText(mContext, "写点评论吧", Toast.LENGTH_SHORT).show();
			return;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("method", "comment.save");
		params.put("bid", bid);
		params.put("uid",
				Preference.instance(mContext).getString(Preference.uid));
		params.put("content", content.getText());
		params.put("score", level.getProgress());
		dialog.show();
		HttpRequest.loadWithMapSec(params, new HttpRequest.CallBackSec() {
			@Override
			public void onError(Call call, Exception e, int id) {

			}

			@Override
			public void onResponse(String json, int id) {
				dialog.dismiss();
				if (CheckUtil.isNullTxt(json)) {
					Toast.makeText(mContext,"请求超时，请稍后再试",Toast.LENGTH_SHORT).show();
					return;
				}
				if (CheckUtil.isNull(json)) {
					Toast.makeText(mContext,"网络连接超时，请稍后再试",Toast.LENGTH_SHORT).show();
				} else {
					try {
						JSONObject object = new JSONObject(json);
						if (object.getInt("code") == 1) {
							ContantsUtil.COMMON = true;
							Toast.makeText(mContext,"评论成功，感谢您的支持",Toast.LENGTH_SHORT).show();
							if (callback != null) {
								callback.callBack();
							}
							dismiss();
						} else {
							Toast.makeText(mContext,"评论失败，请稍后再试",Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						e.printStackTrace();
						Toast.makeText(mContext,"数据解析出错",Toast.LENGTH_SHORT).show();
					}
				}
			}
		});
	}

	public void setCallBack(CallBack callBack) {
		this.callback = callBack;
	}

	public interface CallBack {
		void callBack();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		dialog.dismiss();
	}

	@Override
	public void onBackPressed() {
		dismiss();
	}
}
