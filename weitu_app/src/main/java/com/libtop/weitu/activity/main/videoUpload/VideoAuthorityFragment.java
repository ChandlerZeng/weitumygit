package com.libtop.weitu.activity.main.videoUpload;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentFragment;

import butterknife.Bind;
import butterknife.OnClick;


/**
 * Created by LianTu on 2016/4/25.
 */
public class VideoAuthorityFragment extends ContentFragment
{
    @Bind(R.id.title)
    TextView mTitleText;


    @Override
    protected int getLayoutId()
    {
        return R.layout.fragment_video_authority_set;
    }


    @Override
    public void onCreation(View root)
    {
        setTitle();
    }


    @Nullable
    @OnClick({R.id.back_btn})
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.back_btn:
                onBackPressed();
                break;
        }
    }


    private void setTitle()
    {
        mTitleText.setText("隐私设置");
    }
}
