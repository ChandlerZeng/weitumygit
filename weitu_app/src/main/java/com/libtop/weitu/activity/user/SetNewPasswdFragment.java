package com.libtop.weitu.activity.user;

import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.login.LoginFragment;
import com.libtop.weitu.base.BaseFragment;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.CheckUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;


/**
 * Created by LianTu on 2016/7/5.
 */
public class SetNewPasswdFragment extends BaseFragment
{
    @Bind(R.id.password)
    EditText mPasswdEdit; // 新密码
    @Bind(R.id.confir_password)
    EditText confirPassword; //  确认新密码
    @Bind(R.id.et_origin)
    EditText etOrigin; // 旧密码数据
    @Bind(R.id.img_origin)
    ImageView imgOrigin; // 旧密码图标
    @Bind(R.id.img_confirm)
    ImageView imgConfirPassword; // 修改密码图标
    private boolean isOldPasswdCorrect = false;
    private boolean isNewPasswdCorrect = false;
    private static String mobile;


    private TextWatcher watcher = new TextWatcher()
    {
        private String temp;


        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            temp = s.toString();
        }


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
        }


        @Override
        public void afterTextChanged(Editable s)
        {

            String newPasswd = mPasswdEdit.getText().toString();
            imgConfirPassword.setVisibility(View.VISIBLE);
            if (!temp.equals(newPasswd))
            {
                imgConfirPassword.setBackgroundResource(R.drawable.wrong);
                isNewPasswdCorrect = false;
            }
            else
            {
                isNewPasswdCorrect = true;
                imgConfirPassword.setBackgroundResource(R.drawable.correct);
            }
        }
    };


    @Override
    protected int getLayoutId()
    {
        return R.layout.fragment_registor_passwd;
    }


    @Override
    public void onCreation(View root)
    {
        setTitle(root);
        etOrigin.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (hasFocus)
                {

                }
                else
                {
                    checkPasswork();
                }
            }
        });
        confirPassword.addTextChangedListener(watcher);
    }


    private void setTitle(View root)
    {
        ((TextView) root.findViewById(R.id.title)).setText("修改密码");
    }


    @Nullable
    @OnClick({R.id.back_btn, R.id.next_step})
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.next_step:
                nextStep();
                break;
            case R.id.back_btn:
                ((ContentActivity) mContext).popBack();
                break;
        }
    }


    /**
     * 自身用户系统登陆
     */
    private void checkPasswork()
    {
        mobile = mPreference.getString(Preference.phone);
        String password = etOrigin.getText().toString();
        if (TextUtils.isEmpty(mobile))
        {
            return;
        }
        if (TextUtils.isEmpty(password))
        {
            imgOrigin.setVisibility(View.VISIBLE);
            imgOrigin.setBackgroundResource(R.drawable.wrong);
            return;
        }
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
                        isOldPasswdCorrect = true;
                    }
                    else
                    {
                        isOldPasswdCorrect = false;
                    }
                    imgOrigin.setVisibility(View.VISIBLE);
                    if (isOldPasswdCorrect)
                    {
                        imgOrigin.setBackgroundResource(R.drawable.correct);
                    }
                    else
                    {
                        imgOrigin.setBackgroundResource(R.drawable.wrong);
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                    isOldPasswdCorrect = false;
                }
            }
        });
    }


    @Override
    public void onBackPressed()
    {
        ((ContentActivity) mContext).popBack();
    }


    private void nextStep()
    {
        String confirmPasswd = confirPassword.getText().toString();
        String newPasswd = mPasswdEdit.getText().toString();
        String oldPasswd = etOrigin.getText().toString();
        String uid = mPreference.getString(Preference.uid);
        if (CheckUtil.isNull(newPasswd))
        {
            showToast("新密码不能为空");
            return;
        }
        if (!(CheckUtil.checkLength(newPasswd, 6, 16)))
        {
            showToast("新密码长度为6至16位");
            return;
        }
        if (!isOldPasswdCorrect)
        {
            showToast("原密码错误");
            return;
        }
        if (!isNewPasswdCorrect)
        {
            showToast("新密码错误");
            return;
        }
        showLoding();
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("method", "user.updatePassword");
        params.put("uid", uid);
        params.put("newPassword", newPasswd + "");
        params.put("oldPassword", oldPasswd + "");
        showLoding();
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
                if (CheckUtil.isNullTxt(jsonStr))
                {
                    showToast("请求超时，请稍后再试");
                    return;
                }
                if (!CheckUtil.isNull(jsonStr))
                {
                    try
                    {
                        JSONObject json = new JSONObject(jsonStr);
                        if (json.getInt("code") == 1)
                        {
                            ((ContentActivity) mContext).changeFragment(LoginFragment.class.getName(), false, true);
                            showToast("密码重置成功");
                        }
                        else
                        {
                            showToast("密码重置失败");
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                        showToast("密码重置出错");
                    }
                }
                else
                {
                    showToast("密码重置失败");
                }
            }
        });
    }
}
