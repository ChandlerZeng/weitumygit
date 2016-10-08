package com.libtop.weitu.base.impl;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.libtop.weitu.base.BaseActivity;


/**
 * Created by Administrator on 2016/1/7 0007.
 */
public class GestureActivity extends BaseActivity implements GestureDetector.OnGestureListener
{
    private GestureDetector mDetector;
    private boolean isGesture = false;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mDetector = new GestureDetector(this);
    }


    @Override
    protected void onResume()
    {
        super.onResume();
    }


    @Override
    protected void onPause()
    {
        super.onPause();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        if (mDetector.onTouchEvent(event))
        {
            event.setAction(MotionEvent.ACTION_CANCEL);
        }
        return super.dispatchTouchEvent(event);
    }


    @Override
    public boolean onDown(MotionEvent e)
    {
        return false;
    }


    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
    {
        if (isGesture)
        {
            float subX = e2.getX() - e1.getX();
            float subY = e2.getY() - e1.getY();
            if (subX > 150 && Math.abs(subY) < 170)
            {
                scrollXBack();
            }
        }
        return false;
    }


    private void scrollXBack()
    {
        finish();
    }


    /**
     * 设置为可以侧滑关闭
     *
     * @param state
     */
    public void setGuesture(boolean state)
    {
        isGesture = state;
    }


    @Override
    public void onLongPress(MotionEvent e)
    {

    }


    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
    {
        return false;
    }


    @Override
    public void onShowPress(MotionEvent e)
    {

    }


    @Override
    public boolean onSingleTapUp(MotionEvent e)
    {
        return false;
    }

}
