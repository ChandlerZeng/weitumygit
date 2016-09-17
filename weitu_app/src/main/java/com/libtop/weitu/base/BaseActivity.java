package com.libtop.weitu.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.libtop.weitu.R;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.DisplayUtils;
import com.libtop.weitu.utils.NetworkUtil;
import com.libtop.weitu.widget.dialog.TranLoading;
import com.libtop.weitu.widget.msg.AppMsg;

import butterknife.ButterKnife;
import rx.Subscription;


@SuppressWarnings("deprecation")
public class BaseActivity extends FragmentActivity
{

    protected Subscription subscription;
    protected BaseActivity mContext;
    public int mScreenWidth = 0;
    public int mScreenHeight = 0;

    protected Preference mPreference;
    protected TranLoading mLoading;
    protected FragmentManager mFm;

    protected InputMethodManager imm;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mContext = this;
        DisplayMetrics dm = DisplayUtils.getDisplayMetrics(mContext);
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;

        mPreference = new Preference(mContext);
        mLoading = new TranLoading(mContext);

        mFm = getSupportFragmentManager();
    }


    protected void unsubscribe()
    {
        if (subscription != null && !subscription.isUnsubscribed())
        {
            subscription.unsubscribe();
        }
    }


    protected void setInjectContentView(int layoutId)
    {
        setContentView(layoutId);
        ButterKnife.bind(this);
    }


    /**
     * 覆写finish方法，覆盖默认方法，加入切换动画
     */
    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_right_out);
    }


    public void finishResult(int resultCode)
    {
        setResult(resultCode);
        this.finish();
    }


    public void finishSimple()
    {
        super.finish();
    }


    /**
     * 覆写startactivity方法，加入切换动�?
     */
    public void startActivity(Bundle bundle, Class<?> target)
    {
        Intent intent = new Intent(this, target);
        if (bundle != null)
        {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
    }

    /**
     * 覆写startactivity方法，加入切换动�?
     */
    public void startActivityWithFlag(Intent intent, int flag)
    {
        if (flag!=0){
            intent.setFlags(flag);
        }
        startActivity(intent);
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
    }


    @Override
    protected void onDestroy()
    {
        release();
        unsubscribe();
        super.onDestroy();
    }


    protected void release()
    {
        if (mLoading != null)
        {
            mLoading.dismiss();
        }
        mLoading = null;
        mContext = null;
        mPreference = null;
        mFm = null;
    }


    protected void showLoding()
    {
        if (mLoading != null && !mLoading.isShowing())
        {
            mLoading.show();
        }
    }


    protected void dismissLoading()
    {
        if (mLoading != null && mLoading.isShowing())
        {
            mLoading.dismiss();
        }
    }


    /**
     * 带回调的跳转
     *
     * @param bundle
     * @param requestCode
     * @param target
     */
    public void startForResult(Bundle bundle, int requestCode, Class<?> target)
    {
        Intent intent = new Intent(this, target);
        if (bundle != null)
        {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_left_out);
    }


    public void showToast(String message)
    {
        AppMsg appmsg = AppMsg.makeText(mContext, message, AppMsg.STYLE_INFO);
        appmsg.setLayoutGravity(android.view.Gravity.BOTTOM);
        appmsg.show();
    }


    protected void noNetThanExit(Context context)
    {
        if (!NetworkUtil.checkConnection(context))
        {
            finish();
            Toast.makeText(context, "请检查网络后重试", Toast.LENGTH_SHORT).show();
        }
    }

    protected void hideKeyBoard(View v){
        imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive()){
            imm.hideSoftInputFromWindow(v.getWindowToken(),0);
        }
    }
}
