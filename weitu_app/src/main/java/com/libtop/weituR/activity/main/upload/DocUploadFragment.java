package com.libtop.weituR.activity.main.upload;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.ContentActivity;
import com.libtop.weituR.base.BaseFragment;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/1/25 0025.
 */
public class DocUploadFragment extends BaseFragment {
    @Bind(R.id.title)
    TextView mTitleText;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_doc_upload;
    }

    @Override
    public void onCreation(View root) {
        setTitle();
    }

    private void setTitle(){
        mTitleText.setText("文档上传");
    }

    @Nullable
    @OnClick(R.id.back_btn)
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_btn:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        ((ContentActivity)mContext).popBack();
    }
}
