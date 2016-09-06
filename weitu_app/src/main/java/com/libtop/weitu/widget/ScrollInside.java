package com.libtop.weitu.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;


/**
 * Created by LianTu on 2016-8-11.
 */
public class ScrollInside extends ScrollView
{

    private boolean isChildMove = false;


    public ScrollInside(Context context)
    {
        super(context);
    }


    public ScrollInside(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }


    public ScrollInside(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
                Log.w("guanglog", "CHILD ACTION_DOWN");
                requestDisallowParentInterceptTouchEvent(true);
                //Must declare interest to get more events
                return true;
            case MotionEvent.ACTION_UP:
                Log.w("guanglog", "CHILD ACTION_UP");
                requestDisallowParentInterceptTouchEvent(false);
                //没有移动即是点击事件
                if (!isChildMove)
                {
                    this.performClick();
                }
                isChildMove = false;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                Log.w("guanglog", "CHILD ACTION_POINTER_DOWN");
                break;
            case MotionEvent.ACTION_POINTER_UP:
                Log.w("guanglog", "CHILD ACTION_POINTER_UP");
                break;
            case MotionEvent.ACTION_MOVE:
                //调用父控件的滑动事件，不然Scrollview不会滚动
                super.onTouchEvent(event);
                isChildMove = true;
                Log.w("guanglog", "CHILD ACTION_MOVE");
                break;
            case MotionEvent.ACTION_CANCEL:
                requestDisallowParentInterceptTouchEvent(false);
                break;
        }
        return false;
    }


    private void requestDisallowParentInterceptTouchEvent(Boolean __disallowIntercept)
    {
        View __v = this;
        while (__v.getParent() != null && __v.getParent() instanceof View)
        {
            if (__v.getParent() instanceof ScrollView)
            {
                __v.getParent().requestDisallowInterceptTouchEvent(__disallowIntercept);
            }
            __v = (View) __v.getParent();
        }
    }
}
