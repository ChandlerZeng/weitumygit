package com.libtop.weitu.activity.main;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.base.BaseActivity;

import butterknife.Bind;
import butterknife.OnClick;


/**
 * Created by LianTu on 2016-9-7.
 */
public class ThemeDetailActivity extends BaseActivity
{

    @Bind(R.id.tv_theme_detail_title)
    TextView tvThemeDetailTitle;
    @Bind(R.id.tv_theme_detail_follow_num)
    TextView tvThemeDetailFollowNum;
    @Bind(R.id.tv_theme_detail_follow)
    TextView tvThemeDetailFollow;
    @Bind(R.id.lv_theme_detail)
    ListView lvThemeDetail;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setInjectContentView(R.layout.activity_main_theme_detail);
    }


    @OnClick({R.id.back_btn, R.id.rl_theme_detail_title})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.back_btn:
                break;
            case R.id.rl_theme_detail_title:
                break;
        }
    }
}
