package com.libtop.weitu.utils;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.libtop.weitu.R;


/**
 * 屏幕相关
 * Created by zy on 2015/12/23 0023.
 */
public class DisplayUtils
{
    /**
     * 获得屏幕数据对象
     *
     * @param context
     * @return
     */
    public static DisplayMetrics getDisplayMetrics(Context context)
    {
        return context.getResources().getDisplayMetrics();
    }


    /**
     * 屏幕宽度px
     *
     * @param context
     * @return
     */
    public static int getDisplayWith(Context context)
    {
        return getDisplayMetrics(context).widthPixels;
    }


    /**
     * 屏幕高度px
     *
     * @param context
     * @return
     */
    public static int getDisplayHeight(Context context)
    {
        return getDisplayMetrics(context).heightPixels;
    }


    public static int getDiaplayeDensity(Context context)
    {
        return getDisplayMetrics(context).densityDpi;
    }


    public static int dp2px(Context context, float value)
    {
        return (int) (value * (getDiaplayeDensity(context) / 160) + 0.5f);
    }

    public static float dip2px(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }


    /**
     * 弹出底部对话框，达到背景背景透明效果
     * <p/>
     * 实现原理：弹出一个全屏popupWindow,将Gravity属性设置bottom,根背景设置为一个半透明的颜色，
     * 弹出时popupWindow的半透明效果背景覆盖了在Activity之上 给人感觉上是popupWindow弹出后，背景变成半透明
     */
    public static PopupWindow openPopChoice(Context context, int popResId)
    {
        View popView = LayoutInflater.from(context).inflate(popResId, null);
        View rootView = (((ViewGroup) ((Activity) context).findViewById(android.R.id.content))).getChildAt(0);// 當前頁面的根佈局
        final PopupWindow popupWindow = new PopupWindow(popView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        popView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                popupWindow.dismiss();
            }
        });

        // 设置弹出动画
        popupWindow.setAnimationStyle(R.anim.push_bottom_in);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        popupWindow.setFocusable(true);
        // 顯示在根佈局的底部
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM | Gravity.LEFT, 0, 0);
        return popupWindow;
    }
}
