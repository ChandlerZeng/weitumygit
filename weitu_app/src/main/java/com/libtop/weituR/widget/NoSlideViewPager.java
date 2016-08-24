package com.libtop.weituR.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by LianTu on 2016/7/15.
 */
public class NoSlideViewPager extends ViewPager {

    //设为false的时候不可滑动
    private boolean isPagingEnabled = true;

    public NoSlideViewPager(Context context) {
        super(context);
    }

    public NoSlideViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.isPagingEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.isPagingEnabled && super.onInterceptTouchEvent(event);
    }


    public void setPagingEnabled(boolean b) {
        this.isPagingEnabled = b;
    }}
