package com.libtop.weitu.base.impl;

import android.os.Bundle;

import com.libtop.weitu.base.BaseActivity;


/**
 * Created by Administrator on 2016/1/8 0008.
 */
public class ImgActivity extends BaseActivity {
//    protected DisplayImageOptions mOptions;

//    protected ImageOptions mOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mOptions=((AppApplication)mContext.getApplicationContext())
//                .getCommenOptions();
//        mOptions = new DisplayImageOptions.Builder()
//                .showStubImage(R.drawable.content_bg)
//                .showImageForEmptyUri(R.drawable.content_bg)
//                .cacheInMemory(true).cacheOnDisc(true)
//                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
//                .displayer(new RoundedBitmapDisplayer(4)).build();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mOptions=null;
    }
}
