package com.libtop.weitu.activity.login;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.base.BaseFragment;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.utils.CheckUtil;
import com.libtop.weitu.utils.ContantsUtil;
import com.libtop.weitu.widget.dialog.AlertDialog;

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
public class RegMobileFragment extends BaseFragment{
    @Bind(R.id.user_name)
    EditText mMobileEdit;
    private AlertDialog mAlert;
    @Bind(R.id.capton)
    EditText mVerifyEdit;
    @Bind(R.id.get_capton)
    Button mVerifyBtn;
    @Bind(R.id.password)
    EditText mPasswdEdit; // 密码

    private String verify;
    private String mobile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAlert = new AlertDialog(mContext, "该用户已经被注册，是否直接登录");
        mAlert.setCallBack(new AlertDialog.CallBack() {
            @Override
            public void cancel() {
            }

            @Override
            public void callBack() {
                Bundle bundle = new Bundle();
                bundle.putString("phone", mMobileEdit.getText() + "");
                ((ContentActivity)mContext).changeFragment(LoginFragment.class.getName(),false,true);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_registor_mobile2;
    }

    @Override
    public void onCreation(View root) {
        setTitle(root);
    }

    private void setTitle(View root){
        ((TextView)root.findViewById(R.id.title)).setText(R.string.registor_str);
    }

    @Nullable
    @OnClick({R.id.next_step,R.id.back_btn,R.id.get_capton})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_step:
                nextStep();
                break;
            case R.id.back_btn:
                ((ContentActivity)mContext).popBack();
                break;
            case R.id.get_capton:
                mobile=mMobileEdit.getText().toString();
                if (CheckUtil.isNull(mobile)) {
                    showToast("手机号码不能为空");
                    return;
                }
                if (!CheckUtil.checkNumber(mobile)) {
                    showToast("请输入正确的手机号码");
                    return;
                }
                nextStep2();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        ((ContentActivity)mContext).popBack();
    }

    private void nextStep2() {
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("method", "user.check");
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
                if (CheckUtil.isNullTxt(jsonStr)) {
                    showToast("网络连接超时请稍后再试");
                    return;
                }
                try {
                    if (CheckUtil.isNull(jsonStr)) {
                        showToast("请求出错");
                    } else {
                        JSONObject json = new JSONObject(jsonStr);
                        if (json.getInt("code") == 0) {
                            ContantsUtil.phone = mobile+ "";
                            getCapton();
                        } else {
                            mAlert.show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void nextStep() {
        mobile=mMobileEdit.getText().toString();
        if (CheckUtil.isNull(mobile)) {
            showToast("手机号码不能为空");
            return;
        }
        if (!CheckUtil.checkNumber(mobile)) {
            showToast("请输入正确的手机号码");
            return;
        }
        String password=mPasswdEdit.getText().toString();
        if (CheckUtil.isNull(password)) {
            showToast("密码不能为空");
            return;
        }
        if (!(CheckUtil.checkLength(password, 6, 16))) {
            showToast("密码长度为5至15位");
            return;
        }
        ContantsUtil.passwd = password+ "";
        verify=mVerifyEdit.getText().toString();
        if (CheckUtil.isNull(verify)) {
            showToast("请输入手机验证码");
            return;
        }
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("method", "user.validateCaptcha");
        params.put("phone", mobile);
        params.put("captcha", verify+"");
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
                            ContantsUtil.caption = verify+ "";
                            toNext();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAlert.dismiss();
    }

    private void toNext() {
//        Intent intent = new Intent(context, RegCaptionActivity.class);
//        startActivity(intent);
        ((ContentActivity)mContext).changeFragment(RegInfoFragment.class.getName()
                , true, true);
    }

    private void getCapton() {
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("method", "user.sendCaptcha");
        params.put("phone", mobile);
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
                            // showToast("已发送验证码到你的手机，请查收."+
                            // json.getString("uid"),
                            // Toast.LENGTH_LONG).show();
                            mVerifyBtn.setText("重发60S");
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
                    showToast("注册出错");
                }
            }
        });
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
