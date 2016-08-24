package com.libtop.weituR.base.impl;

import android.content.Context;

import com.libtop.weituR.base.BaseAdapter;

import java.util.List;

/**
 * Created by Administrator on 2016/1/8 0008.
 */
public abstract class ImgAdapter extends BaseAdapter {

//    protected DisplayImageOptions mOptions;

//    protected ImageOptions mOptions;

    public ImgAdapter(Context context, List<?> data, int resourceId) {
        super(context, data, resourceId);
//        mOptions = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.content_bg)
//                .showImageOnFail(R.drawable.content_bg)
//                .showImageForEmptyUri(R.drawable.content_bg)
//                .cacheInMemory(true).cacheOnDisk(true)
//                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
//                .displayer(new RoundedBitmapDisplayer(4)).build();
//        mOptions=((AppApplication)mContext.getApplicationContext())
//                .getCommenOptions();
    }


}
