package com.libtop.weitu.activity.search;

import android.os.Bundle;
import android.view.View;

import com.libtop.weitu.R;
import com.libtop.weitu.base.impl.ImgActivity;
import com.libtop.weitu.widget.photoview.PhotoView;
import com.libtop.weitu.widget.photoview.PhotoViewAttacher;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


/**
 * 下载显示大图
 */
public class ShowBigImage extends ImgActivity
{

    private PhotoView image;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_show_big_image);
        super.onCreate(savedInstanceState);

        image = (PhotoView) findViewById(R.id.image);
        String url = getIntent().getStringExtra("url");
        Picasso.with(mContext).load(url).into(image, new Callback()
        {
            @Override
            public void onSuccess()
            {
                dismissLoading();
            }


            @Override
            public void onError()
            {
                dismissLoading();
            }
        });

        image.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener()
        {
            @Override
            public void onViewTap(View view, float x, float y)
            {
                finish();
                overridePendingTransition(R.anim.alpha_into, R.anim.zoomout);
            }
        });
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


    @Override
    public void onBackPressed()
    {
        finish();
        overridePendingTransition(R.anim.alpha_into, R.anim.zoomout);
    }
}
