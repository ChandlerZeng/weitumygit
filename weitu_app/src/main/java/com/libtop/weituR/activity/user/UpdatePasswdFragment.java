//package com.libtop.weituR.activity.user;
//
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.libtop.weitu.R;
//import com.libtop.weituR.activity.ContentActivity;
//import com.libtop.weituR.base.BaseFragment;
//import com.libtop.weituR.utils.CheckUtil;
//import com.libtop.weituR.http.old_del.HttpServiceUtil;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.xutils.view.annotation.ContentView;
//import org.xutils.view.annotation.Event;
//import org.xutils.view.annotation.ViewInject;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by Administrator on 2016/1/11 0011.
// */
//setInjectContentView(R.layout.fragment_update_passwd)
//public class UpdatePasswdFragment extends BaseFragment{
//    @Bind(R.id.old_user_passwd)
//    EditText mOldPassEdit;
//    @Bind(R.id.new_user_passwd)
//    EditText mNewPassEdit;
//
//    @Override
//    public void onCreation(View root) {
//        setTitle(root);
//    }
//
//    private void setTitle(View root){
//        ((TextView)root.findViewById(R.id.title)).setText(R.string.update_passwd);
//    }
//
//    @Nullable @OnClick({R.id.submit,R.id.back_btn})
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.submit:
//                updatePasswd();
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
//    private void updatePasswd() {
//        String oldPasswd=mOldPassEdit.getText().toString();
//        String newPasswd=mNewPassEdit.getText().toString();
//        if (CheckUtil.isNull(oldPasswd+ "")) {
//            Toast.makeText(mContext, "旧密码不能为空", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if (CheckUtil.isNull(newPasswd+"")) {
//            Toast.makeText(mContext, "新密码不能为空", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if (!CheckUtil.checkLength(newPasswd+"", 5, 15)) {
//            Toast.makeText(mContext, "密码长度为6至16位", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if (!CheckUtil.checkLength(oldPasswd+"", 5, 15)) {
//            Toast.makeText(mContext, "密码长度为6至16位", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("method", "user.updatePassword");
//        params.put("oldPassword", oldPasswd+"");
//        params.put("newPassword", newPasswd+"");
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
//                        if (!CheckUtil.isNull(jsonStr)) {
//                            try {
//                                JSONObject json = new JSONObject(jsonStr);
//                                if (json.getInt("code") == 1) {
//                                    Toast.makeText(mContext, "修改密码成功", Toast.LENGTH_SHORT).show();
//                                    ((ContentActivity)mContext).popBack();
//                                } else {
//                                    showToast("修改密码失败");
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                                showToast("修改密码出错");
//                            }
//                        } else {
//                            showToast("请求出错，请稍后再试");
//                        }
//                    }
//                });
//    }
//}
