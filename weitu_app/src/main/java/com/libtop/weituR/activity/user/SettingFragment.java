package com.libtop.weituR.activity.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.ContentActivity;
import com.libtop.weituR.activity.login.LoginFragment;
import com.libtop.weituR.base.BaseFragment;
import com.libtop.weituR.http.HttpRequest;
import com.libtop.weituR.tool.Preference;
import com.libtop.weituR.utils.CacheUtil;
import com.libtop.weituR.utils.CheckUtil;
import com.libtop.weituR.utils.ContantsUtil;
import com.libtop.weituR.utils.FileUtils;
import com.libtop.weituR.widget.dialog.AlertDialog;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/1/11 0011.
 */
public class SettingFragment extends BaseFragment {

//    @Bind( R.id.vesion)
//    TextView mVesionText;
    @Bind( R.id.tv_cache_size)
    TextView tvCacheSize;
    @Bind( R.id.logout)
    TextView tvLogout;

    private AlertDialog mAlert;


    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    String run(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    @Nullable
    @OnClick({R.id.change_password,R.id.back_btn,R.id.check_update,R.id.about_us,R.id.help,R.id.clear_cache,R.id.logout})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_password:
                changePassword();
                break;
            case R.id.back_btn:
                mContext.finish();
                break;
            case R.id.about_us:
                showAboutDialog();
                break;
            case R.id.check_update:
                checkUpdate();
                break;
            case R.id.help:
                showHelpDialog();
                break;
            case R.id.clear_cache:
                clearCache();
//                x.image().clearCacheFiles();
//                Toast.makeText(mContext, "清理缓存成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.logout:
                logout();
                break;
        }
    }

    private void clearCache() {
        CacheUtil.deleteCache(mContext);
        tvCacheSize.setText("0KB");
        showToast("清除缓存成功");
    }

    private void changePassword() {
        if (CheckUtil.isNull(mPreference.getString(Preference.uid))) {
            Bundle bundle1 = new Bundle();
            bundle1.putString(ContentActivity.FRAG_CLS, LoginFragment.class.getName());
            mContext.startActivity(bundle1, ContentActivity.class);
        } else {
            ((ContentActivity)mContext).changeFragment(SetNewPasswdFragment.class.getName(),true,true);
        }
    }

    private void setTitle(View root){
        ((TextView)root.findViewById(R.id.title)).setText(R.string.set);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_setting2;
    }

    @Override
    public void onCreation(View root) {
//        mVesionText.setText(ContantsUtil.vesion);
        setTitle(root);
        int size = (int) CacheUtil.getCacheSize(mContext);
        String sizeText = FileUtils.getReadableFileSize(size);
        tvCacheSize.setText(sizeText);
        if (CheckUtil.isNull(mPreference.getString(Preference.uid))) {
            tvLogout.setVisibility(View.GONE);
        } else {
            tvLogout.setVisibility(View.VISIBLE);
        }

    }

    //退出账号
    private void logout() {
        showLoding();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("method", "keymap.logout");
        HttpRequest.loadWithMapSec(params, new HttpRequest.CallBackSec() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String response, int id) {
                dismissLoading();
                mPreference.remove(Preference.uid);
                mPreference.remove(Preference.UserName);
                mPreference.remove(Preference.SchoolId);
                mPreference.remove(Preference.sex);
                mPreference.remove(Preference.SID);
                mPreference.remove(Preference.AESKEY);
                mPreference.remove(Preference.hid);
                HttpRequest.reset();
                mContext.finish();
            }
        });
    }

    /**
     * 打开关于
     */
    private void showAboutDialog() {
        String tag = "about_dialog";
        FragmentManager manager = getChildFragmentManager();
        AboutFragment2 fragment = (AboutFragment2) manager.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = new AboutFragment2();
        }
        fragment.show(manager, tag);
    }

    /**
     * 打开帮助
     */
    private void showHelpDialog() {
        String tag = "about_dialog";
        FragmentManager manager = getChildFragmentManager();
        HelpFragment fragment = (HelpFragment) manager.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = new HelpFragment();
        }
        fragment.show(manager, tag);
    }

    private void checkUpdate() {

        showLoding();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("version", ContantsUtil.currentVesion);
        params.put("method", "issue.update");
        HttpRequest.loadWithMap(params)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String json, int id) {
                        dismissLoading();
                        if (CheckUtil.isNullTxt(json)) {
                            showToast("请求超时，请稍后再试");
                            return;
                        }
                        if ("".equals(json) || json == null) {
                            showToast("已为最新版本");
                        } else {
                            try {
                                JSONObject data = new JSONObject(json);
                                String link = data.getString("link");
                                if (!"none".equals(link)) {
                                    showUpdateDialog(data.getString("link"));
                                    mPreference.putBoolean(
                                            Preference.UPDATE_STATE, true);
                                    mPreference.putLong(Preference.UPDATE_TIME,
                                            System.currentTimeMillis());
                                } else {
                                    showToast("已是最新版本");
                                }
                            } catch (Exception e) {
                                showToast("数据解析出错");
                            }
                        }
                    }
                });
    }

    private void showUpdateDialog(final String url) {
        mAlert = new AlertDialog(mContext, "有新版本，您确定要更新吗");
        mAlert.setCallBack(new AlertDialog.CallBack() {
            @Override
            public void cancel() {
                mPreference.putBoolean(Preference.UPDATE_STATE, false);
            }

            @Override
            public void callBack() {
                Uri uri = Uri.parse(url);
                Intent downloadIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(downloadIntent);
            }
        });
        mAlert.show();
    }

    @Override
    public void onBackPressed() {
        mContext.finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAlert!=null)mAlert.dismiss();
    }
}
