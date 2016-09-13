package com.libtop.weitu.activity.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.libtop.weitu.R;
import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.utils.selector.MultiImageSelectorActivity;
import com.libtop.weitu.utils.selector.view.ImageSortActivity;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.OnClick;


public class NewSubjectActivity extends BaseActivity
{


    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.img_cover)
    ImageView imgCover;
    @Bind(R.id.et_subject_title)
    EditText etSubjectTitle;
    @Bind(R.id.et_subject_desc)
    EditText etSubjectDesc;
    @Bind(R.id.tv_subject_sort)
    TextView tvSubjectSort;

    public static final int REQUEST_IMAGE = 123;
    private String name;
    private int label1;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setInjectContentView(R.layout.activity_main_new_subject);
        initView();
    }


    private void initView()
    {
        title.setText("新建主题");
    }


    @OnClick({R.id.back_btn, R.id.img_cover, R.id.ll_theme_sort,R.id.commit})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.back_btn:
                onBackPressed();
                break;
            case R.id.img_cover:
                imgCoverClick();
                break;
            case R.id.ll_theme_sort:
                themeSortClick();
                break;
            case R.id.commit:
                completeClick();
                break;
        }
    }


    private void completeClick()
    {
        finish();
    }


    private void themeSortClick()
    {
        Intent i = new Intent(mContext, ImageSortActivity.class);
        startActivityForResult(i, 1);
    }


    private void imgCoverClick()
    {
        Intent intent = new Intent(mContext, MultiImageSelectorActivity.class);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, 0);
        mContext.startActivityForResult(intent, REQUEST_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE
                && resultCode == Activity.RESULT_OK){
            String a = "file:///" + data.getStringExtra("lamge");
            Picasso.with(mContext).load(a).fit().into(imgCover);
            Toast.makeText(mContext,"Good image ",Toast.LENGTH_SHORT).show();
        }
        switch (resultCode){
            case ImageSortActivity.DESCRIPTION_RETURNQ:
                Bundle ab = data.getExtras();
                name = ab.getString("name");
                label1 = ab.getInt("code");
                tvSubjectSort.setText(name);
                break;

        }
    }
}
