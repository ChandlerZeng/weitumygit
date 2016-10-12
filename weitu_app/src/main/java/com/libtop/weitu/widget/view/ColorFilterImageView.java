package com.libtop.weitu.widget.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;


public class ColorFilterImageView extends ImageView
{
    private boolean enableColorFilter = true;


    public ColorFilterImageView(Context context)
    {
        super(context);
    }


    public ColorFilterImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (enableColorFilter)
        {
            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                    Drawable drawable = getDrawable();
                    if (drawable != null)
                    {
                        drawable.mutate().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    break;

                case MotionEvent.ACTION_CANCEL:
                    break;

                case MotionEvent.ACTION_UP:
                    Drawable drawableUp = getDrawable();
                    if (drawableUp != null)
                    {
                        drawableUp.mutate().clearColorFilter();
                    }
                    break;

            }

            return super.onTouchEvent(event);
        }

        return false;
    }


    public void setEnableColorFilter(boolean enableColorFilter)
    {
        this.enableColorFilter = enableColorFilter;
    }
}
