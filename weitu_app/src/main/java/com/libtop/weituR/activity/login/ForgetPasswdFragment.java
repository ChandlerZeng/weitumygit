package com.libtop.weituR.activity.login;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
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
 * Created by Administrator on 2016/1/8 0008.
 */
public class ForgetPasswdFragment extends BaseFragment {
//    @Bind(id = R.id.back_btn)
//    ImageView mBackBtn; // 回退
//
//    @Bind(id = R.id.send_email)
//    Button mSendBtn; // 发送邮件

    @Bind(R.id.capton)
    EditText mVerifyEdit;
    @Bind(R.id.get_capton)
    Button mVerifyBtn;


    @Bind(R.id.email)
    EditText mMobileEdit;

    private String mMsg = "";

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_forget_passwd2;
    }

    @Override
    public void onCreation(View root) {
        setTitle(root);
        Bundle bundle =mContext. getIntent().getExtras();
        mMsg = bundle.getString("email");
        mMobileEdit.setText(mMsg);
    }

    private void setTitle(View root){
        ((TextView)root.findViewById(R.id.title)).setText(R.string.forget_get_passwd);
    }

    @Nullable
    @OnClick({R.id.back_btn,R.id.send_email,R.id.get_capton,R.id.tv_back_login})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                ((ContentActivity)mContext).popBack();
                break;
            case R.id.send_email:
//                send();
                nextStep2();
                break;
            case R.id.get_capton:
                getCapton2();
                break;
            case R.id.tv_back_login:
                back2Login();
                break;
        }
    }

    private void back2Login() {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        ((ContentActivity)mContext).popBack();
    }

    private void send(){
        final String mobile=mMobileEdit.getText().toString();
        if (CheckUtil.isNull(mobile)) {
            showToast("电话号码不能为空");
            return;
        }
        if (!CheckUtil.checkNumber(mobile)) {
            showToast("请输入正确的手机号码");
            return;
        }
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("method", "user.sendCaptcha");
        params.put("phone", mobile+"");
        params.put("username", mobile+"");
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
                try {
                    if (CheckUtil.isNull(jsonStr)) {
                        showToast("请求出错");
                    } else {
                        JSONObject json = new JSONObject(jsonStr);
                        if (json.getInt("code") == 1) {
                            ContantsUtil.phone = mobile + "";
                            ((ContentActivity)mContext).changeFragment(ChangePasswdFragment.class.getName()
                                    ,true,true);
                        } else {
                            showToast("获取验证码失败");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast("注册出错");
                }
            }
        });
    }


    //输入验证码后检矫
    private void nextStep2() {
        final String mobile=mMobileEdit.getText().toString();
        if (CheckUtil.isNull(mobile)) {
            showToast("手机号码不能为空");
            return;
        }
        if (!CheckUtil.checkNumber(mobile)) {
            showToast("请输入正确的手机号码");
            return;
        }
        ContantsUtil.phone = mobile;
        final String verify=mVerifyEdit.getText().toString();
        if (CheckUtil.isNull(verify)) {
            showToast("请输入手机验证码");
            return;
        }

        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("phone", mobile);
        params.put("method", "user.validateCaptcha");
        params.put("captcha",verify+"");
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
                try {
                    if (CheckUtil.isNull(jsonStr)) {
                        showToast("请求出错");
                    } else {
                        JSONObject json = new JSONObject(jsonStr);
                        if (json.getInt("code") == 1) {
                            ContantsUtil.caption = verify + "";
                            toNext();
                        } else {
                            showToast("验证验证码失败");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast("验证出错");
                }
            }
        });
    }

    //获取验证码的按钮点击
    private void getCapton2() {
        final String mobile=mMobileEdit.getText().toString();
        if (CheckUtil.isNull(mobile)) {
            showToast("手机号码不能为空");
            return;
        }
        if (!CheckUtil.checkNumber(mobile)) {
            showToast("请输入正确的手机号码");
            return;
        }
        ContantsUtil.phone = mobile;
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("phone", mobile);
        params.put("method", "user.sendCaptcha");
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
                try {
                    if (CheckUtil.isNull(jsonStr)) {
                        showToast("请求出错");
                        mVerifyBtn.setText("获取验证码");
                        mVerifyBtn.setEnabled(true);
                    } else {
                        JSONObject json = new JSONObject(jsonStr);
                        if (json.getInt("code") == 1) {
                            mVerifyBtn.setText("重发(60)");
                            seconds = 0;
                            mHandler.postDelayed(mTimer, 1000);
                            mVerifyBtn.setEnabled(false);
                            showToast("验证码已发送到你手机，请注意查收。");
                        } else {
                            mVerifyBtn.setText("获取验证码");
                            mVerifyBtn.setEnabled(true);
                            showToast("获取验证码失败");
                        }
                    }
                } catch (JSONException e) {
                    mVerifyBtn.setText("获取验证码");
                    mVerifyBtn.setEnabled(true);
                    e.printStackTrace();
                    showToast("获取验证码出错");
                }
            }
        });
    }

    private void toNext() {
        ((ContentActivity)mContext).changeFragment(ChangePasswdFragment.class.getName(),true,true);
    }

    // 定时器控制
    private int seconds = 0;
    private Handler mHandler = new Handler();
    private boolean isEnd = false;
    private Runnable mTimer = new Runnable() {
        @Override
        public void run() {
            if (!isEnd) {
                seconds++;
                mVerifyBtn.setText("重发(" + (60 - seconds) + ")");
                if (seconds < 60) {
                    mHandler.postDelayed(this, 1000);
                } else {
                    mVerifyBtn.setText("获取验证码");
                    mVerifyBtn.setEnabled(true);
                }
            }
        }
    };
}
