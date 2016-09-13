package com.libtop.weitu.activity.main;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.base.BaseActivity;

import butterknife.Bind;
import butterknife.OnClick;


/**
 * Created by LianTu on 2016-9-8.
 */
public class SubjectInfoActivity extends BaseActivity
{

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.img_subject_info)
    ImageView imgSubjectInfo;
    @Bind(R.id.tv_subject_info_title)
    TextView tvSubjectInfoTitle;
    @Bind(R.id.tv_subject_info_sort)
    TextView tvSubjectInfoSort;
    @Bind(R.id.tv_subject_info_desc)
    TextView tvSubjectInfoDesc;
    @Bind(R.id.tv_subject_info_watch)
    TextView tvSubjectInfoWatch;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setInjectContentView(R.layout.activity_main_subject_info);
        initView();
        reqestData();
    }


    private void reqestData()
    {

    }


    private void initView()
    {

    }


    @OnClick({R.id.back_btn, R.id.tv_subject_info_watch})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.back_btn:
                onBackPressed();
                break;
            case R.id.tv_subject_info_watch:
                break;
        }
    }
}
