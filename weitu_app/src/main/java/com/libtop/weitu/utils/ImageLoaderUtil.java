package com.libtop.weitu.utils;

import android.content.Context;
import android.widget.ImageView;

import com.libtop.weitu.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;


/**
 * @author Sai
 * @ClassName: ImageLoaderUtil
 * @Description: 图片加载工具类
 * @date 9/13/16 19:14
 */
public class ImageLoaderUtil
{
    /**
     * 默认大图占位图资源ID
     */
    public static final int RESOURCE_ID_IMAGE_BIG = R.drawable.bg_new_subject;

    private static final int RESOURCE_ID_USER_LOGO = R.drawable.head_image;  // logo默认占位图资源ID
    private static final int RESOURCE_ID_IMAGE_DEFAULT = R.drawable.default_image;  // 普通图片默认占位图资源ID
    private static final float ROUND_SIZE_DEFAULT = 5;


    // 加载logo图片
    public static void loadLogoImage(Context context, ImageView imageView, String url)
    {
        Transformation transformation = new CropCircleTransformation();
        build(context, url, RESOURCE_ID_USER_LOGO).transform(transformation).centerCrop().fit().into(imageView);
    }


    // 加载本地资源图片(圆角效果)
    public static void loadRoundImage(Context context, ImageView imageView, int resId)
    {
        Transformation transformation = getDefaultRoundedCornersTransformation(context);
        Picasso.with(context).load(resId).transform(transformation).fit().into(imageView);
    }


    // 加载图片(圆角效果, centerInside)
    public static void loadRoundImage(Context context, ImageView imageView, String url, int defaultResId)
    {
        Transformation transformation = getDefaultRoundedCornersTransformation(context);
        build(context, url, defaultResId).transform(transformation).centerInside().fit().into(imageView);
    }


    // 加载图片(centerInside)
    public static void loadImage(Context context, ImageView imageView, String url)
    {
        loadImage(context, imageView, url, RESOURCE_ID_IMAGE_DEFAULT);
    }


    // 加载图片(centerInside)
    public static void loadImage(Context context, ImageView imageView, String url, int defaultResId)
    {
        build(context, url, defaultResId).centerInside().fit().into(imageView);
    }


    public static RequestCreator build(Context context, String url)
    {
        return build(context, url, RESOURCE_ID_IMAGE_DEFAULT);
    }


    public static RequestCreator build(Context context, String url, int defaultResId)
    {
        String notEmptyUrl = StringUtil.getNotEmptyUrl(url);
        return Picasso.with(context).load(notEmptyUrl).placeholder(defaultResId).error(defaultResId).fit();
    }


    public static Transformation getDefaultRoundedCornersTransformation(Context context)
    {
        int roundSize = DisplayUtil.dp2px(context, ROUND_SIZE_DEFAULT);
        return new RoundedCornersTransformation(roundSize, 0);
    }
}
