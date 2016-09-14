package com.libtop.weitu.activity.main;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.base.BaseActivity;
import com.squareup.picasso.Picasso;

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

    private boolean isWatching = false;


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
        String coverString = getIntent().getStringExtra("cover");
        if (!TextUtils.isEmpty(coverString)){
            Picasso.with(mContext).load(coverString).fit().into(imgSubjectInfo);
        }
        title.setText("主题信息");
        tvSubjectInfoTitle.setText("Java开源");
        tvSubjectInfoSort.setText("资源考试");
        tvSubjectInfoDesc.setText("编写本规范的目的是为了进一步规范Java软件编程风格，提高软件源可读性、可靠性和可重用性。");
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
                watchClick();
                break;
        }
    }


    private void watchClick()
    {
        if (isWatching){
            tvSubjectInfoWatch.setText("关注");
            tvSubjectInfoWatch.setTextColor(ContextCompat.getColor(mContext,R.color.newGreen));
        }else {
            tvSubjectInfoWatch.setText("取消关注");
            tvSubjectInfoWatch.setTextColor(ContextCompat.getColor(mContext,R.color.red));
        }
        isWatching = !isWatching;
    }
}
