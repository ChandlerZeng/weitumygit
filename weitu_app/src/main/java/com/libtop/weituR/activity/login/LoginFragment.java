package com.libtop.weituR.activity.login;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.ContentActivity;
import com.libtop.weituR.activity.main.MainActivity;
import com.libtop.weituR.base.BaseFragment;
import com.libtop.weituR.http.HttpRequest;
import com.libtop.weituR.tool.Preference;
import com.libtop.weituR.utils.CheckUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by Administrator on 2016/1/8 0008.
 */
public class LoginFragment extends BaseFragment {

    @Bind(R.id.user_name)
    EditText mNameEdit;
    @Bind(R.id.user_passwd)
    EditText mPasswdEdit;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_login;
    }

    @Override
    public void onCreation(View root) {
        setTitle(root);
        String phone =mPreference.getString(Preference.phone);
        if (!TextUtils.isEmpty(phone)) {
            mNameEdit.setText(phone);
        }
    }

    private void setTitle(View root){
        ((TextView)root.findViewById(R.id.title)).setText(R.string.login_fast_str);
    }

    @Nullable
    @OnClick({R.id.back_btn,R.id.title_right_text,R.id.registor_new_user,R.id.forget_password
            ,R.id.login_btn})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_btn:
                onBackPressed();
                break;
            case R.id.title_right_text:
            case R.id.registor_new_user:
                //注册流程
                ((ContentActivity)mContext).changeFragment(RegMobileFragment2.class.getName()
                        ,true,true);
                break;
            case R.id.forget_password:
                //找回密码流程

                ((ContentActivity)mContext).changeFragment(ForgetPasswdFragment2.class.getName()
                        ,true,true);
                break;
            case R.id.login_btn:
                //登陆
                author();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mContext.getIntent().getExtras().getBoolean(ContentActivity.FRAG_ISBACK)){
            ((ContentActivity)mContext).popBack();
        }else {
            mContext.finish();
        }
    }

    /**
     * 自身用户系统登陆
     */
    private void author() {
        final String mobile=mNameEdit.getText().toString();
        String password=mPasswdEdit.getText().toString();
        if (TextUtils.isEmpty(mobile)) {
            showToast("电话号码不能为空");
            return;
        }
        if (!CheckUtil.checkNumber(mobile)) {
            showToast("请输入正确的电话号码");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            showToast("密码不能为空");
            return;
        }
        showLoding();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("method", "user.auth");
        params.put("phone", mobile);
        params.put("password", password);
        HttpRequest.loadWithMapSec(params, new HttpRequest.CallBackSec() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String jsonStr, int id) {
                dismissLoading();
                if (TextUtils.isEmpty(jsonStr)) {
                    showToast("请求超时，请稍后再试");
                    return;
                }
                try {
                    JSONObject json = new JSONObject(jsonStr);
                    if (json.getInt("code") == 1) {
                        // 临时存放
                        mPreference.putString(Preference.UserName,
                                json.getString("username"));
                        mPreference.putString(Preference.uid,
                                json.getString("uid"));
                        mPreference.putString(Preference.sex,
                                json.getString("sex"));
                        mPreference.putString(Preference.phone,
                                mobile+ "");

                        // 图书馆信息
                        JSONObject library = json
                                .getJSONObject("library");
                        mPreference.putString(Preference.SchoolId,
                                library.getString("id"));
                        mPreference.putString(Preference.SchoolName,
                                library.getString("name"));
                        mPreference.putString(Preference.SchoolCode,
                                library.getString("code"));

                        showToast("登录成功");
//                                if (WelcomeActivity.instance != null) {
//                                    WelcomeActivity.instance.finish();
//                                }
                        mContext.startActivity(null, MainActivity.class);
                        //结束欢迎页
                        mContext.setResult(Activity.RESULT_OK);
                        mContext.finish();
                    } else {
                        showToast("登陆失败，请检查手机或密码是否正确");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast("登陆出错");
                }
            }
        });
    }
}
