/**
 * @file XListViewHeader.java
 * @create Apr 18, 2012 5:22:27 PM
 * @author Maxwin
 * @description XListView's header
 */
package com.libtop.weitu.widget.listview;


import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.libtop.weitu.R;


/**
 * 顶部有 加载进度条的
 *
 * @author Administrator
 */
public class DHeader extends LinearLayout
{
    LinearLayout mContainer;
    private ProgressBar mProgressBar;
    private int mState = STATE_NORMAL;

    private Animation mRotateUpAnim;
    private Animation mRotateDownAnim;

    private final int ROTATE_ANIM_DURATION = 180;

    public final static int STATE_NORMAL = 0;
    public final static int STATE_READY = 1;
    public final static int STATE_REFRESHING = 2;


    public DHeader(Context context)
    {
        super(context);
        initView(context);
    }


    /**
     * @param context
     * @param attrs
     */
    public DHeader(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initView(context);
    }


    @SuppressWarnings("deprecation")
    private void initView(Context context)
    {
        // 初始情况，设置下拉刷新view高度为0
        LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT, 0);
        mContainer = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.widget_dtop_rbottom, null);
        addView(mContainer, lp);
        setGravity(Gravity.BOTTOM);

        mProgressBar = (ProgressBar) findViewById(R.id.xlistview_header_progressbar);

        mRotateUpAnim = new RotateAnimation(0.0f, -180.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateUpAnim.setFillAfter(true);
        mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
        mRotateDownAnim.setFillAfter(true);
    }


    public void setState(int state)
    {
        if (state == mState)
        {
            return;
        }

        if (state == STATE_REFRESHING)
        { // 显示进度
            mProgressBar.setVisibility(View.VISIBLE);
        }
        else
        { // 显示箭头图片
            mProgressBar.setVisibility(View.GONE);
        }

        switch (state)
        {
            case STATE_NORMAL:

                break;
            case STATE_READY:
                if (mState != STATE_READY)
                {

                }
                break;
            case STATE_REFRESHING:
                break;
            default:
        }

        mState = state;
    }


    public void setVisiableHeight(int height)
    {
        if (height < 0)
        {
            height = 0;
        }
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        lp.height = height;
        mContainer.setLayoutParams(lp);
    }


    public int getVisiableHeight()
    {
        return mContainer.getHeight();
    }


    /**
     * hide footer when disable pull load more
     */
    public void hide()
    {
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        lp.height = 0;
        mContainer.setLayoutParams(lp);
    }


    /**
     * show footer
     */
    public void show()
    {
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        lp.height = LayoutParams.WRAP_CONTENT;
        mContainer.setLayoutParams(lp);
    }


    public void setBottomMargin(int height)
    {
        if (height < 0)
        {
            return;
        }
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        lp.bottomMargin = height;
        mContainer.setLayoutParams(lp);
    }


    public int getBottomMargin()
    {
        LayoutParams lp = (LayoutParams) mContainer.getLayoutParams();
        return lp.bottomMargin;
    }


}
