package com.libtop.weituR.activity.startup;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.ContentActivity;
import com.libtop.weituR.activity.login.LoginFragment;
import com.libtop.weituR.activity.main.LibraryFragment;
import com.libtop.weituR.activity.main.MainActivity;
import com.libtop.weituR.base.BaseActivity;
import com.libtop.weituR.tool.Preference;
import com.libtop.weituR.utils.CheckUtil;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/1/8 0008.
 */
public class StartupActivity extends BaseActivity  {
    @Bind(R.id.view_bottom)
    LinearLayout mBottomView;
//    @Bind(R.id.login_btn)
//    Button mLoginBtn;
//    @Bind(R.id.choose_library)
//    Button mLibBtn;
    @Bind(R.id.text)
    TextView mInfoText;

    public static Activity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setInjectContentView(R.layout.activity_startup);
//        setContentView(R.layout.activity_startup);
        startHandler();

//        mLoginBtn.setOnClickListener(this);
//        mLibBtn.setOnClickListener(this);

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
                    //展示底部页面
                    showBottom();
//                    startActivity(null, WelcomeActivity.class);
                } else {
                    //到主页面   // MainActivity //VideoScreenActivity  //GudieActivity //CameraActivity //UploadFileActivity
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
