package com.libtop.weitu.activity.login;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.main.MainActivity;
import com.libtop.weitu.application.AppApplication;
import com.libtop.weitu.base.BaseFragment;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.service.WTPushService;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.CheckUtil;

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
public class LoginFragment extends BaseFragment
{

    @Bind(R.id.user_name)
    EditText mNameEdit;
    @Bind(R.id.user_passwd)
    EditText mPasswdEdit;

    private boolean isFromComment;


    @Override
    protected int getLayoutId()
    {
        return R.layout.fragment_login;
    }


    @Override
    public void onCreation(View root)
    {
        setTitle(root);
        String phone = mPreference.getString(Preference.phone);
        if (!TextUtils.isEmpty(phone))
        {
            mNameEdit.setText(phone);
        }

        Bundle bundle = ((ContentActivity) mContext).getCurrentExtra();
        isFromComment = bundle.getBoolean("isFromComment",false);
    }


    private void setTitle(View root)
    {
        ((TextView) root.findViewById(R.id.title)).setText(R.string.login_fast_str);
    }


    @Nullable
    @OnClick({R.id.back_btn, R.id.registor_new_user, R.id.forget_password, R.id.login_btn})
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.back_btn:
                onBackPressed();
                break;
            case R.id.registor_new_user:
                ((ContentActivity) mContext).changeFragment(RegMobileFragment.class.getName(), true, true);
                break;
            case R.id.forget_password:
                ((ContentActivity) mContext).changeFragment(ForgetPasswdFragment.class.getName(), true, true);
                break;
            case R.id.login_btn:
                author();
                break;
        }
    }


    @Override
    public void onBackPressed()
    {
        if (mContext.getIntent().getExtras().getBoolean(ContentActivity.FRAG_ISBACK))
        {
            ((ContentActivity) mContext).popBack();
        }
        else
        {
            mContext.finish();
        }
    }


    /**
     * 自身用户系统登陆
     */
    private void author()
    {
        final String mobile = mNameEdit.getText().toString();
        String password = mPasswdEdit.getText().toString();
        if (TextUtils.isEmpty(mobile))
        {
            showToast("电话号码不能为空");
            return;
        }
        if (!CheckUtil.checkNumber(mobile))
        {
            showToast("请输入正确的电话号码");
            return;
        }
        if (TextUtils.isEmpty(password))
        {
            showToast("密码不能为空");
            return;
        }
        showLoding();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("method", "user.auth");
        params.put("phone", mobile);
        params.put("password", password);
        HttpRequest.loadWithMapSec(params, new HttpRequest.CallBackSec()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {

            }


            @Override
            public void onResponse(String jsonStr, int id)
            {
                dismissLoading();
                if (TextUtils.isEmpty(jsonStr))
                {
                    showToast("请求超时，请稍后再试");
                    return;
                }
                try
                {
                    JSONObject json = new JSONObject(jsonStr);
                    if (json.getInt("code") == 1)
                    {
                        String uid = json.getString("uid");

                        // 临时存放
                        mPreference.putString(Preference.UserName, json.getString("username"));
                        mPreference.putString(Preference.uid, uid);
                        mPreference.putString(Preference.sex, json.getString("sex"));
                        mPreference.putString(Preference.phone, mobile + "");

                        // 图书馆信息
                        JSONObject library = json.getJSONObject("library");
                        mPreference.putString(Preference.SchoolId, library.getString("id"));
                        mPreference.putString(Preference.SchoolName, library.getString("name"));
                        mPreference.putString(Preference.SchoolCode, library.getString("code"));

                        showToast("登录成功");
                        if(isFromComment){
                            mContext.finish();
                        }else {
                            mContext.startActivity(null, MainActivity.class);
                            //结束欢迎页
                            mContext.setResult(Activity.RESULT_OK);
                            mContext.finish();
                        }

                        // 注册推送别名
                        WTPushService.registerPushService(AppApplication.getInstance(), uid);
                    }
                    else
                    {
                        showToast("登陆失败，请检查手机或密码是否正确");
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    showToast("登陆出错");
                }
            }
        });
    }
}
