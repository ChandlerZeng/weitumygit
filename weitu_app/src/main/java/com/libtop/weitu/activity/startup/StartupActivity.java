package com.libtop.weitu.activity.startup;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.login.LoginFragment;
import com.libtop.weitu.activity.main.LibraryFragment;
import com.libtop.weitu.activity.main.MainActivity;
import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.CheckUtil;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/1/8 0008.
 */
public class StartupActivity extends BaseActivity  {
    @Bind(R.id.view_bottom)
    LinearLayout mBottomView;
    @Bind(R.id.text)
    TextView mInfoText;

    public static Activity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setInjectContentView(R.layout.activity_startup);
        startHandler();

        instance=this;
    }

    private void showBottom() {
        mInfoText.setText(R.string.welcome_content);
        mBottomView.setVisibility(View.VISIBLE);
        mBottomView.startAnimation(AnimationUtils.loadAnimation(mContext,
                R.anim.push_bottom_in));
    }

    private void startHandler() {
        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (CheckUtil.isNull(mPreference
                        .getString(Preference.SchoolName))) {
                    showBottom();
                } else {
                    startActivity(null, MainActivity.class);
                    finish();
                }

            }
        }, 2000);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Nullable
    @OnClick({R.id.login_btn,R.id.choose_library})
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        switch (v.getId()) {
            case R.id.login_btn:
                //登陆页面
                bundle.putString(ContentActivity.FRAG_CLS, LoginFragment.class.getName());
                break;
            case R.id.choose_library:
                //图书馆选择页面
                bundle.putInt("from", 1);
                bundle.putString(ContentActivity.FRAG_CLS, LibraryFragment.class.getName());
                break;
        }
        startActivity(bundle, ContentActivity.class);
    }

}
