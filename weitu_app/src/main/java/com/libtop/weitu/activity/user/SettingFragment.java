package com.libtop.weitu.activity.user;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import com.libtop.weitu.BuildConfig;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.login.LoginFragment;
import com.libtop.weitu.application.AppApplication;
import com.libtop.weitu.base.BaseFragment;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.service.WTPushService;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.CacheUtil;
import com.libtop.weitu.utils.CheckUtil;
import com.libtop.weitu.utils.FileUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;


/**
 * Created by Administrator on 2016/1/11 0011.
 */
public class SettingFragment extends BaseFragment
{

    @Bind(R.id.tv_cache_size)
    TextView tvCacheSize;
    @Bind(R.id.logout)
    TextView tvLogout;
    @Bind(R.id.tv_version)
    TextView tvVersion;


    @Nullable
    @OnClick({R.id.change_password, R.id.back_btn, R.id.about_us, R.id.clear_cache, R.id.logout})
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.change_password:
                changePassword();
                break;
            case R.id.back_btn:
                mContext.finish();
                break;
            case R.id.about_us:
                showAboutDialog();
                break;
            case R.id.clear_cache:
                clearCache();
                break;
            case R.id.logout:
                logout();
                break;
        }
    }


    private void clearCache()
    {
        CacheUtil.deleteCache(mContext);
        tvCacheSize.setText("0KB");
        showToast("清除缓存成功");
    }


    private void changePassword()
    {
        if (CheckUtil.isNull(mPreference.getString(Preference.uid)))
        {
            Bundle bundle1 = new Bundle();
            bundle1.putString(ContentActivity.FRAG_CLS, LoginFragment.class.getName());
            mContext.startActivity(bundle1, ContentActivity.class);
        }
        else
        {
            ((ContentActivity) mContext).changeFragment(SetNewPasswdFragment.class.getName(), true, true);
        }
    }


    private void setTitle(View root)
    {
        ((TextView) root.findViewById(R.id.title)).setText(R.string.set);

    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected int getLayoutId()
    {
        return R.layout.fragment_setting;
    }


    @Override
    public void onCreation(View root)
    {
        setTitle(root);
        int size = (int) CacheUtil.getCacheSize(mContext);
        String sizeText = FileUtils.getReadableFileSize(size);
        tvCacheSize.setText(sizeText);
        if (CheckUtil.isNull(mPreference.getString(Preference.uid)))
        {
            tvLogout.setVisibility(View.GONE);
        }
        else
        {
            tvLogout.setVisibility(View.VISIBLE);
        }

        tvVersion.setText("当前版本:" + BuildConfig.VERSION_NAME);
    }


    //退出账号
    private void logout()
    {
        showLoding();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("method", "keymap.logout");
        HttpRequest.loadWithMapSec(params, new HttpRequest.CallBackSec()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {

            }


            @Override
            public void onResponse(String response, int id)
            {
                // 反注册推送别名
                WTPushService.unregisterPushService(AppApplication.getInstance());

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


    private void showAboutDialog()
    {
        String tag = "about_dialog";
        FragmentManager manager = getChildFragmentManager();
        AboutFragment fragment = (AboutFragment) manager.findFragmentByTag(tag);
        if (fragment == null)
        {
            fragment = new AboutFragment();
        }
        fragment.show(manager, tag);
    }


    @Override
    public void onBackPressed()
    {
        mContext.finish();
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }
}
