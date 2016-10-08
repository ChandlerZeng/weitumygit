package com.libtop.weitu.activity.main;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.main.dto.SubjectDetailBean;
import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.dao.ResultCodeDto;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.utils.Preference;
import com.libtop.weitu.utils.ImageLoaderUtil;
import com.libtop.weitu.utils.JSONUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;

import static com.libtop.weitu.activity.main.SubjectDetailActivity.isFollow;


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


    private SubjectDetailBean subjectDetailBean;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setInjectContentView(R.layout.activity_main_subject_info);
        initView();
    }


    @Override
    protected void onResume()
    {
        super.onResume();
    }


    @Override
    protected void onPause()
    {
        super.onPause();
    }


    private void initView()
    {
        String bean  = getIntent().getStringExtra("subjectDetailBean");
        subjectDetailBean = new Gson().fromJson(bean, SubjectDetailBean.class);
        if (subjectDetailBean == null){
            Toast.makeText(mContext,"暂无数据，请重试",Toast.LENGTH_SHORT).show();
            return;
        }
        if (isFollow){
            tvSubjectInfoWatch.setText("取消关注");
            tvSubjectInfoWatch.setTextColor(ContextCompat.getColor(mContext,R.color.red));
        }else {
            tvSubjectInfoWatch.setText("关注");
            tvSubjectInfoWatch.setTextColor(ContextCompat.getColor(mContext,R.color.newGreen));
        }
        ImageLoaderUtil.loadImage(mContext,imgSubjectInfo,subjectDetailBean.subject.getCover(),ImageLoaderUtil.RESOURCE_ID_IMAGE_BIG);
        title.setText("主题信息");
        tvSubjectInfoTitle.setText(subjectDetailBean.subject.getTitle());
//        tvSubjectInfoSort.setText(subjectDetailBean.subject.get);
        tvSubjectInfoDesc.setText(subjectDetailBean.subject.getIntroduction());
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
        if (!isFollow){
            requestFollow();
        }else {
            requestUnFollow();
        }
    }

    //    http://weitu.bookus.cn/subject/follow.json?text={"sid":"56f97d8d984e741f1420axx","uid":"56f97d8d984e741f1420awr8","method":"subject.follow"}
    private void requestFollow()
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sid", subjectDetailBean.subject.getId());
        params.put("uid", Preference.instance(mContext).getString(Preference.uid));
        params.put("method", "subject.follow");
        HttpRequest.loadWithMap(params).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {

            }


            @Override
            public void onResponse(String json, int id)
            {
                ResultCodeDto resultCodeDto = JSONUtil.readBean(json, ResultCodeDto.class);
                if (resultCodeDto == null){
                    Toast.makeText(mContext,R.string.netError,Toast.LENGTH_SHORT).show();
                    return;
                }
                if (resultCodeDto.code == 1){
                    isFollow = true;
                    Toast.makeText(mContext,"主题关注成功",Toast.LENGTH_SHORT).show();
                    tvSubjectInfoWatch.setText("取消关注");
                    tvSubjectInfoWatch.setTextColor(ContextCompat.getColor(mContext,R.color.red));

                }else
                {
                    Toast.makeText(mContext,"主题关注失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //    http://weitu.bookus.cn/subject/unfollow.json?text={"sid":"56f97d8d984e741f1420axx","uid":"56f97d8d984e741f1420awr8","method":"subject.unfollow"}
    private void requestUnFollow()
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sid", subjectDetailBean.subject.getId());
        params.put("uid", Preference.instance(mContext).getString(Preference.uid));
        params.put("method", "subject.unfollow");
        HttpRequest.loadWithMap(params).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {

            }


            @Override
            public void onResponse(String json, int id)
            {
                ResultCodeDto resultCodeDto = JSONUtil.readBean(json, ResultCodeDto.class);
                if (resultCodeDto == null){
                    Toast.makeText(mContext,R.string.netError,Toast.LENGTH_SHORT).show();
                    return;
                }
                if (resultCodeDto.code == 1){
                    isFollow = false;
                    Toast.makeText(mContext,"取消关注成功",Toast.LENGTH_SHORT).show();
                    tvSubjectInfoWatch.setText("关注");
                    tvSubjectInfoWatch.setTextColor(ContextCompat.getColor(mContext,R.color.newGreen));
                }else
                {
                    Toast.makeText(mContext,"取消关注失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
