package com.libtop.weitu.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.utils.selector.view.ImageSortActivity;

import butterknife.Bind;
import butterknife.OnClick;


public class NewThemeActivity extends BaseActivity
{


    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.img_cover)
    ImageView imgCover;
    @Bind(R.id.et_theme_title)
    EditText etThemeTitle;
    @Bind(R.id.et_theme_desc)
    EditText etThemeDesc;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setInjectContentView(R.layout.activity_main_new_theme);
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
//        Intent intent = new Intent(ImageCoverActivity.this, ImageEditActivity.class);
//        intent.putExtra("aid", mid);
//        intent.putExtra("uid", uid);
//        intent.putExtra("iswhere", 4);
//        startActivityForResult(intent, 1);
    }
}
