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

    private static final int RESOURCE_ID_USER_LOGO = R.drawable.user_default_icon;  // logo默认占位图资源ID
    private static final int RESOURCE_ID_IMAGE_DEFAULT = R.drawable.default_image;  // 普通图片默认占位图资源ID
    private static final float ROUND_SIZE_DEFAULT = 5;


    // 加载logo图片
    public static void loadLogoImage(Context context, ImageView imageView, String url)
    {
        Transformation transformation = new CropCircleTransformation();
        loadImage(context, imageView, url, RESOURCE_ID_USER_LOGO, transformation, false);
    }


    // 加载本地资源图片(圆角效果)
    public static void loadRoundImage(Context context, ImageView imageView, int resId)
    {
        Transformation transformation = getDefaultRoundedCornersTransformation(context);
        loadImage(context, imageView, resId, transformation);
    }


    // 加载图片(圆角效果)
    public static void loadRoundImage(Context context, ImageView imageView, String url, int defaultResId)
    {
        Transformation transformation = getDefaultRoundedCornersTransformation(context);
        loadImage(context, imageView, url, defaultResId, transformation, true);
    }


    // 加载图片(默认效果)
    public static void loadImage(Context context, ImageView imageView, String url)
    {
        loadImage(context, imageView, url, RESOURCE_ID_IMAGE_DEFAULT, null, true);
    }


    // 加载图片(默认效果)
    public static void loadImage(Context context, ImageView imageView, String url, int defaultResId)
    {
        loadImage(context, imageView, url, defaultResId, null, true);
    }


    // 加载本地资源图片
    private static void loadImage(Context context, ImageView imageView, int resId, Transformation transformation)
    {
        RequestCreator requestCreator = Picasso.with(context).load(resId).fit();

        if (transformation != null)
        {
            requestCreator.transform(transformation);
        }

        requestCreator.into(imageView);
    }


    // 加载图片
    private static void loadImage(Context context, ImageView imageView, String url, int defaultResId, Transformation transformation, boolean shouldCenterInside)
    {
        String notEmptyUrl = StringUtil.getCoverUrl(url);
        RequestCreator requestCreator = Picasso.with(context).load(notEmptyUrl).placeholder(defaultResId).error(defaultResId).fit();

        if (transformation != null)
        {
            requestCreator.transform(transformation);
        }
        if (shouldCenterInside)
        {
            requestCreator.centerInside();
        }

        requestCreator.into(imageView);
    }


    private static Transformation getDefaultRoundedCornersTransformation(Context context)
    {
        int roundSize = DisplayUtil.dp2px(context, ROUND_SIZE_DEFAULT);
        return new RoundedCornersTransformation(roundSize, 0);
    }
}
