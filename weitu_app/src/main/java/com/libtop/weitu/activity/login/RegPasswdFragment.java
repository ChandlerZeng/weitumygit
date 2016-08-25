package com.libtop.weitu.activity.login;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.base.BaseFragment;
import com.libtop.weitu.utils.CheckUtil;
import com.libtop.weitu.utils.ContantsUtil;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/1/8 0008.
 */
public class RegPasswdFragment extends BaseFragment{
    @Bind(R.id.password)
    EditText mPasswdEdit; // 密码
    @Bind(R.id.confir_password)
    EditText mConEdit;

    private void setTitle(View root){
        ((TextView)root.findViewById(R.id.title)).setText(R.string.set_passwd);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_registor_passwd;
    }

    @Override
    public void onCreation(View root) {
        setTitle(root);
    }

    @Nullable
    @OnClick({R.id.next_step,R.id.back_btn})
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

    private void nextStep() {
        String password=mPasswdEdit.getText().toString();
        if (CheckUtil.isNull(password)) {
            showToast("密码不能为空");
            return;
        }
        if (!(CheckUtil.checkLength(password, 6, 16))) {
            showToast("密码长度为5至15位");
            return;
        }
        if(!CheckUtil.checkEquels(password, mConEdit.getText())){
            showToast("前后密码不一致");
            return;
        }
        ContantsUtil.passwd = password+ "";
        toNext();
    }

    private void toNext() {
//        ((ContentActivity)mContext).changeFragment(RegInfoFragment.class.getName()
//                , true, true);
    }
}
