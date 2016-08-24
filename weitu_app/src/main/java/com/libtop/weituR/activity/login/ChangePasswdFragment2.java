package com.libtop.weituR.activity.login;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.ContentActivity;
import com.libtop.weituR.base.BaseFragment;
import com.libtop.weituR.http.HttpRequest;
import com.libtop.weituR.utils.CheckUtil;
import com.libtop.weituR.utils.ContantsUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by Administrator on 2016/1/11 0011.
 */
public class ChangePasswdFragment2 extends BaseFragment {
    @Bind(R.id.password)
    EditText mPasswdEdit; // 密码

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_registor_passwd2;
    }

    @Override
    public void onCreation(View root) {
        setTitle(root);
    }

    private void setTitle(View root){
        ((TextView)root.findViewById(R.id.title)).setText(R.string.set_passwd);
    }

    @Nullable
    @OnClick({R.id.back_btn,R.id.next_step})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_step:
                nextStep();
                break;
            case R.id.back_btn:
                ((ContentActivity)mContext).popBack();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        ((ContentActivity)mContext).popBack();
    }

    private void nextStep() {
        String newPasswd=mPasswdEdit.getText().toString();
        if (CheckUtil.isNull(newPasswd)) {
            showToast("密码不能为空");
            return;
        }
        if (!(CheckUtil.checkLength(newPasswd, 6, 16))) {
            showToast("密码长度为6至16位");
            return;
        }
        ContantsUtil.passwd = newPasswd+"";
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("method", "user.resetPassword");
        params.put("phone", ContantsUtil.phone);
        params.put("captcha", ContantsUtil.caption);
        params.put("password", newPasswd + "");
        showLoding();
        HttpRequest.loadWithMapSec(params, new HttpRequest.CallBackSec() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String jsonStr, int id) {
                dismissLoading();
                if (CheckUtil.isNullTxt(jsonStr)) {
                    showToast("请求超时，请稍后再试");
                    return;
                }
                if (!CheckUtil.isNull(jsonStr)) {
                    try {
                        JSONObject json = new JSONObject(jsonStr);
                        if (json.getInt("code") == 1) {
                            ((ContentActivity)mContext).changeFragment(LoginFragment.class.getName()
                                    , false, true);
//                                    Intent intent = new Intent(context,
//                                            LoginActivity.class);
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                                    startActivity(intent);
                        } else {
                            showToast("密码重置失败");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showToast("密码重置出错");
                    }
                } else {
                    showToast("密码重置失败");
                }
            }
        });
    }
}
