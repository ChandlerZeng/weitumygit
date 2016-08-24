package com.libtop.weituR.activity.login;

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
public class RegCaptionFragment extends BaseFragment{
    @Bind(R.id.capton)
    EditText mVerifyEdit;
    @Bind(R.id.get_capton)
    Button mVerifyBtn;
    @Bind(R.id.next_step)
    TextView mHintText;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_registor_capton;
    }

    @Override
    public void onCreation(View root) {
        setTitle(root);
//        mHintText.setText("验证码已经发送到您的手机：" + ContantsUtil.phone);
        mHintText.setText("验证码已经发送到您的手机：");
    }

    private void setTitle(View root){
        ((TextView)root.findViewById(R.id.title)).setText(R.string.input_verify);
    }

    @Nullable
    @OnClick({R.id.back_btn,R.id.next_step,R.id.get_capton})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                ((ContentActivity)mContext).popBack();
                break;
            case R.id.next_step:
                nextStep();
                break;
            case R.id.get_capton:
                getCapton();
                break;
        }
    }

    private void nextStep() {
        final String verify=mVerifyEdit.getText().toString();
        if (CheckUtil.isNull(verify)) {
            showToast("请输入手机验证码");
            return;
        }
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("method", "user.validateCaptcha");
        params.put("phone", ContantsUtil.phone);
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
                            ((ContentActivity)mContext).changeFragment(RegPasswdFragment.class.getName()
                                    , true, true);
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

    private void getCapton() {
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("method", "user.sendCaptcha");
        params.put("phone", ContantsUtil.phone);
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
    @Override
    public void onDestroy() {
        super.onDestroy();
        isEnd = true;
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
