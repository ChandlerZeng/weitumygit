package com.libtop.weitu.utils;


import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.libtop.weitu.R;


/**
 * @author Sai
 * @ClassName: DisplayTool
 * @Description: 显示尺寸单位转换工具类
 * @date 2016-09-17 17:41
 */
public class DisplayUtil
{
    /**
     * 获取设备的度量对象
     *
     * @param   context
     * @return  DisplayMetrics
     */
    public static DisplayMetrics getDisplayMetrics(Context context)
    {
        return context.getResources().getDisplayMetrics();
    }


    /**
     * 获取设备的宽度分辨率[px单位]
     *
     * @param   context
     * @return  int 设备宽度分辨率,单位px
     */
    public static int getDeviceWidthPixels(Context context)
    {
        return getDisplayMetrics(context).widthPixels;
    }


    /**
     * 获取设备的高度分辨率[px单位]
     *
     * @param   context
     * @return  int 设备高度分辨率,单位px
     */
    public static int getDeviceHeightPixels(Context context)
    {
        return getDisplayMetrics(context).heightPixels;
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param context 上下文
     * @param dpValue dp值
     * @return int px值
     */
    public static int dp2px(Context context, float dpValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     *
     * @param context 上下文
     * @param pxValue px值
     * @return int dp值
     */
    public static int px2dp(Context context, float pxValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * 根据手机的分辨率从 sp 的单位 转成为 px(像素)
     *
     * @param context 上下文
     * @param spValue sp值
     * @return int px值
     */
    public static int sp2px(Context context, float spValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (spValue * scale + 0.5f);
    }


    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 sp
     *
     * @param context 上下文
     * @param pxValue px值
     * @return int sp值
     */
    public static int px2sp(Context context, float pxValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * 获取设备的状态栏高度[px单位]
     *
     * @param activity
     * @return int 设备的状态栏高度,单位px
     */
    public static int getStatusBarHeight(Activity activity)
    {
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        return frame.top;
    }


    /**
     * 获取视图的高度（去除状态瘭高度）
     *
     * @param activity
     * @return int
     */
    public static int getViewHeightWithoutStatusBarPixels(Activity activity)
    {
        return getDeviceHeightPixels(activity) - getStatusBarHeight(activity);
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
