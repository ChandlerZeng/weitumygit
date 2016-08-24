package com.libtop.weituR.base.impl;

import android.os.Bundle;

import com.libtop.weituR.base.BaseFragment;

/**
 * Created by Administrator on 2016/1/11 0011.
 */
public abstract class ImgFragment extends BaseFragment {
//    protected DisplayImageOptions mOptions;
//    protected ImageOptions mOptions;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mOptions=((AppApplication)mContext.getApplicationContext())
//                .getCommenOptions();
//        mOptions = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.content_bg)
//                .showImageForEmptyUri(R.drawable.content_bg)
//                .cacheInMemory(true).cacheOnDisk(true)
//                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
//                .displayer(new RoundedBitmapDisplayer(4)).build();
    }
}
