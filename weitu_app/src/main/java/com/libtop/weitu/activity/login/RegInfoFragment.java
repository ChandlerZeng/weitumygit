package com.libtop.weitu.activity.login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.main.LibraryFragment;
import com.libtop.weitu.base.BaseFragment;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.CheckUtil;
import com.libtop.weitu.utils.ContantsUtil;

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
public class RegInfoFragment extends BaseFragment{
    @Bind(R.id.user_name)
    EditText mNameEdit;
    @Bind(R.id.library)
    TextView mLibText;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_registor_info2;
    }

    @Override
    public void onCreation(View root) {
        setTitle(root);
    }

    private void setTitle(View root){
        ((TextView)root.findViewById(R.id.title)).setText("补充信息");
    }

    @Nullable
    @OnClick({R.id.next_step,R.id.back_btn,R.id.sex, R.id.school})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_step:
                nextStep();
                break;
            case R.id.back_btn:
                ((ContentActivity)mContext).popBack();
                break;
            case R.id.school:
                ((ContentActivity)mContext).changeFragment(LibraryFragment.class.getName()
                        ,true,true);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        String schoolId = mPreference.getString(
                Preference.SchoolCode);
        if (!CheckUtil.isNull(schoolId)) {
            mLibText.setText(mPreference.getString(
                    Preference.SchoolName));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        ((ContentActivity)mContext).popBack();
    }

    private void nextStep() {
        String nick=mNameEdit.getText().toString();
        String library=mLibText.getText().toString();
        if (CheckUtil.isNull(nick)) {
            showToast("昵称不能为空");
            return;
        }
        if (!(CheckUtil.checkLength(nick, 4,8))) {
            showToast("请输入4到8个字");
            return;
        }
        if (CheckUtil.isNull(library)) {
            showToast("请选择图书馆");
            return;
        }
        Map<String, Object> params = new LinkedHashMap<String, Object>();
        params.put("method", "user.registerPhone");
        params.put("phone", ContantsUtil.phone);
        params.put("lid",
                Preference.instance(mContext).getString(Preference.SchoolCode));
        params.put("username", nick+ "");
        params.put("password", ContantsUtil.passwd);
        params.put("sex", "0");
        params.put("captcha", ContantsUtil.caption);
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
                            toNext();
                        } else {
                            showToast("注册失败");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        showToast("注册出错");
                    }
                } else {
                    showToast("请求失败，请稍后再试");
                }
            }
        });
    }

    private void toNext() {
        ((ContentActivity)mContext).changeFragment(LibraryFragment.class.getName()
                ,true,true);
    }
}
