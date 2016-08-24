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
//import com.libtop.weituR.widget.dialog.AlertDialog;
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
//setInjectContentView(R.layout.fragment_registor_mobile)
//public class RegMobileFragment extends BaseFragment{
//    @Bind(R.id.user_name)
//    EditText mMobileEdit;
//    private AlertDialog mAlert;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mAlert = new AlertDialog(mContext, "该用户已经被注册，是否直接登录");
//        mAlert.setCallBack(new AlertDialog.CallBack() {
//            @Override
//            public void cancel() {
//            }
//
//            @Override
//            public void callBack() {
//                Bundle bundle = new Bundle();
//                bundle.putString("phone", mMobileEdit.getText() + "");
//                ((ContentActivity)mContext).changeFragment(LoginFragment.class.getName(),false,true);
//            }
//        });
//    }
//
//    @Override
//    public void onCreation(View root) {
//        setTitle(root);
//    }
//
//    private void setTitle(View root){
//        ((TextView)root.findViewById(R.id.title)).setText(R.string.registor_str);
//    }
//
//    @Nullable @OnClick({R.id.next_step,R.id.back_btn})
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.next_step:
//                nextStep();
//                break;
//            case R.id.back_btn:
//                ((ContentActivity)mContext).popBack();
//                break;
//        }
//    }
//
//    @Override
//    public void onBackPressed() {
//        ((ContentActivity)mContext).popBack();
//    }
//
//    private void nextStep() {
//        final String mobile=mMobileEdit.getText().toString();
//        if (CheckUtil.isNull(mobile)) {
//            showToast("手机号码不能为空");
//            return;
//        }
//        if (!CheckUtil.checkNumber(mobile)) {
//            showToast("请输入正确的手机号码");
//            return;
//        }
//        Map<String, Object> params = new LinkedHashMap<String, Object>();
//        params.put("method", "user.check");
//        params.put("phone", mobile+"");
//        params.put("username", mobile+"");
//        mLoading.show();
//        HttpServiceUtil.requestSec(mContext, true, params,
//                new HttpServiceUtil.CallBack() {
//                    @Override
//                    public void callback(String jsonStr) {
//                        if (mLoading.isShowing()){
//                            mLoading.dismiss();
//                        }
//                        if (CheckUtil.isNullTxt(jsonStr)) {
//                            showToast("请求超时，请稍后再试");
//                            return;
//                        }
//                        if (CheckUtil.isNullTxt(jsonStr)) {
//                            showToast("网络连接超时请稍后再试");
//                            return;
//                        }
//                        try {
//                            if (CheckUtil.isNull(jsonStr)) {
//                                showToast("请求出错");
//                            } else {
//                                JSONObject json = new JSONObject(jsonStr);
//                                if (json.getInt("code") == 0) {
//                                    ContantsUtil.phone = mobile+ "";
//                                    toNext();
//                                } else {
//                                    mAlert.show();
//                                }
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        mAlert.dismiss();
//    }
//
//    private void toNext() {
////        Intent intent = new Intent(context, RegCaptionActivity.class);
////        startActivity(intent);
//        ((ContentActivity)mContext).changeFragment(RegCaptionFragment.class.getName()
//                , true, true);
//    }
//}
