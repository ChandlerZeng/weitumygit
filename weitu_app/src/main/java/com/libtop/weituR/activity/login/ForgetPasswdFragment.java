//package com.libtop.weituR.activity.login;
//
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.libtop.weitu.R;
//import com.libtop.weituR.activity.ContentActivity;
//import com.libtop.weituR.base.BaseFragment;
//import com.libtop.weituR.utils.CheckUtil;
//import com.libtop.weituR.utils.ContantsUtil;
//import com.libtop.weituR.http.old_del.HttpServiceUtil;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.xutils.view.annotation.ContentView;
//import org.xutils.view.annotation.Event;
//import org.xutils.view.annotation.ViewInject;
//
//import java.util.LinkedHashMap;
//import java.util.Map;
//
///**
// * Created by Administrator on 2016/1/8 0008.
// */
//setInjectContentView(R.layout.fragment_forget_passwd)
//public class ForgetPasswdFragment extends BaseFragment {
////    @Bind(id = R.id.back_btn)
////    ImageView mBackBtn; // 回退
////
////    @Bind(id = R.id.send_email)
////    Button mSendBtn; // 发送邮件
//
//
//    @Bind(R.id.email)
//    EditText mMobileEdit;
//
//    private String mMsg = "";
//
//    @Override
//    public void onCreation(View root) {
//        setTitle(root);
//        Bundle bundle =mContext. getIntent().getExtras();
//        mMsg = bundle.getString("email");
//        mMobileEdit.setText(mMsg);
//    }
//
//    private void setTitle(View root){
//        ((TextView)root.findViewById(R.id.title)).setText(R.string.forget_get_passwd);
//    }
//
//    @Nullable @OnClick({R.id.back_btn,R.id.send_email})
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.back_btn:
//                ((ContentActivity)mContext).popBack();
//                break;
//            case R.id.send_email:
//                send();
//                break;
//        }
//    }
//
//    @Override
//    public void onBackPressed() {
//        ((ContentActivity)mContext).popBack();
//    }
//
//    private void send(){
//        final String mobile=mMobileEdit.getText().toString();
//        if (CheckUtil.isNull(mobile)) {
//            showToast("电话号码不能为空");
//            return;
//        }
//        if (!CheckUtil.checkNumber(mobile)) {
//            showToast("请输入正确的手机号码");
//            return;
//        }
//        Map<String, Object> params = new LinkedHashMap<String, Object>();
//        params.put("method", "user.sendCaptcha");
//        params.put("phone", mobile+"");
//        params.put("username", mobile+"");
//        if (!mLoading.isShowing()) mLoading.show();
//        HttpServiceUtil.requestSec(mContext, true, params, new HttpServiceUtil.CallBack() {
//            @Override
//            public void callback(String jsonStr) {
//                if (mLoading.isShowing()){
//                    mLoading.dismiss();
//                }
//                if (CheckUtil.isNullTxt(jsonStr)) {
//                    showToast("请求超时，请稍后再试");
//                    return;
//                }
//                try {
//                    if (CheckUtil.isNull(jsonStr)) {
//                        showToast("请求出错");
//                    } else {
//                        JSONObject json = new JSONObject(jsonStr);
//                        if (json.getInt("code") == 1) {
//                            ContantsUtil.phone = mobile + "";
//                            ((ContentActivity)mContext).changeFragment(ForgetCaptionFragment.class.getName()
//                                ,true,true);
//                        } else {
//                            showToast("获取验证码失败");
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    showToast("注册出错");
//                }
//            }
//        });
//    }
//}
