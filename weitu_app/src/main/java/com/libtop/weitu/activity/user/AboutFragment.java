package com.libtop.weitu.activity.user;

/**
 * Created by LianTu on 2016/7/5.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.base.BaseFragmentDialog;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * 关于微图界面
 *
 * @author Administrator
 *
 */
public class AboutFragment extends BaseFragmentDialog {

    @Bind( R.id.back_btn)
    ImageButton mBackBtn;
    @Nullable
    @Bind(R.id.web_content)
    WebView mWebview;
    @Nullable
    @Bind(R.id.title)
    TextView mTitleText;

    private String mContent = "";

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.dialog_fragment_fade_anim;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_about_layout;
    }

    @Override
    public void onCreation(View root) {
    }


    @Nullable
    @OnClick(R.id.back_btn)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                dismiss();
                break;
        }
    }

}
