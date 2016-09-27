package com.libtop.weitu.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.widget.ImageView;

import com.libtop.weitu.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;


/**
 * @author Sai
 * @ClassName: ImageLoaderUtil
 * @Description: 图片加载工具类
 * @date 9/13/16 19:14
 */
public class ImageLoaderUtil
{
    private static final int USER_LOGO_RESOURCE_ID = R.drawable.user_default_icon;
    private static final int DEFAULT_IMAGE_RESOURCE_ID = R.drawable.default_image;
    public static final int DEFAULT_BIG_IMAGE_RESOURCE_ID = R.drawable.bg_new_subject;
    private static final float ROUND_SIZE = 5;


    public static void loadLogoImage(Context context, ImageView imageView, String url)
    {
        Picasso.with(context).load(StringUtil.getCoverUrl(url)).transform(new CircleTransform()).placeholder(USER_LOGO_RESOURCE_ID).error(USER_LOGO_RESOURCE_ID).into(imageView);
    }


    public static void loadImage(Context context, ImageView imageView, String url)
    {
        loadImage(context, imageView, StringUtil.getCoverUrl(url), DEFAULT_IMAGE_RESOURCE_ID);
    }


    public static void loadImage(Context context, ImageView imageView, String url, int defaultResId)
    {
        Picasso.with(context).load(StringUtil.getCoverUrl(url)).placeholder(defaultResId).error(defaultResId).centerInside().fit().into(imageView);
    }

    public static void loadCropImage(Context context, ImageView imageView, String url, int defaultResId)
    {
        Picasso.with(context).load(StringUtil.getCoverUrl(url)).placeholder(defaultResId).error(defaultResId).centerCrop().fit().into(imageView);
    }

    public static void loadPlaceImage(Context context, ImageView imageView, int resId)
    {
        Picasso.with(context).load(resId).transform(new RoundedCornersTransformation((int) DisplayUtil.dp2px(context,ROUND_SIZE),0)).fit().into(imageView);
    }

    public static void loadRoundImage(Context context, ImageView imageView,String url, int defaultResId)
    {
        Picasso.with(context).load(StringUtil.getCoverUrl(url)).placeholder(defaultResId).transform(new RoundedCornersTransformation((int) DisplayUtil.dp2px(context,ROUND_SIZE),0)).fit().into(imageView);
    }

    private static class CircleTransform implements Transformation
    {
        @Override
        public Bitmap transform(Bitmap source)
        {
            int size = Math.min(source.getWidth(), source.getHeight());
            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source)
            {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();

            return bitmap;
        }


        @Override
        public String key()
        {
            return "circle";
        }
    }
}
