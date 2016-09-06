package com.libtop.weitu.widget.slidebar;

/**
 * Created by zeng on 2016/8/29.
 */

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

import com.libtop.weitu.R;


public class ScheduleView extends View
{
    private int nodeColor;
    private int topLineColor;
    private int bottomLineColor;
    private int lineWidth;
    private int nodeNormalRadius;
    private int nodeBiggerRadius;
    private boolean shouldBiggerNode = false;
    private boolean hiddenTopLine = false;
    private boolean hiddenBottomLine = false;

    private Paint nodePaint;
    private Paint topLinePaint;
    private Paint bottomLinePaint;

    private int topLineHeight;


    public ScheduleView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    public ScheduleView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }


    public ScheduleView(Context context)
    {
        this(context, null);
    }


    private void init(Context context)
    {
        Resources res = context.getResources();

        nodeColor = res.getColor(R.color.blue);
        topLineColor = res.getColor(R.color.blue);
        bottomLineColor = res.getColor(R.color.blue);
        lineWidth = res.getDimensionPixelSize(R.dimen.scheduleview_line_width);
        nodeNormalRadius = res.getDimensionPixelSize(R.dimen.scheduleview_node_normal_radius);
        nodeBiggerRadius = res.getDimensionPixelSize(R.dimen.scheduleview_node_bigger_radius);

        nodePaint = new Paint();
        nodePaint.setAntiAlias(true);
        nodePaint.setColor(nodeColor);
        nodePaint.setStyle(Style.FILL);

        topLinePaint = new Paint();
        topLinePaint.setAntiAlias(true);
        topLinePaint.setStrokeWidth(lineWidth);

        bottomLinePaint = new Paint();
        bottomLinePaint.setAntiAlias(true);
        bottomLinePaint.setStrokeWidth(lineWidth);

        topLineHeight = 55;  //TODO
    }


    public void setTopLineColor(int topLineColor)
    {
        this.topLineColor = topLineColor;
        invalidate();
    }


    public void setNodeColor(int nodeColor)
    {
        this.nodeColor = nodeColor;
        invalidate();
    }


    public void setBottomLineColor(int bottomLineColor)
    {
        this.bottomLineColor = bottomLineColor;
        invalidate();
    }


    public void setShouldBiggerNode(boolean shouldBiggerNode)
    {
        this.shouldBiggerNode = shouldBiggerNode;
        invalidate();
    }


    public void setHiddenTopLine(boolean hiddenTopLine)
    {
        this.hiddenTopLine = hiddenTopLine;
        invalidate();
    }


    public void setHiddenBottomLine(boolean hiddenBottomLine)
    {
        this.hiddenBottomLine = hiddenBottomLine;
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas)
    {
        nodePaint.setColor(nodeColor);
        topLinePaint.setColor(topLineColor);
        bottomLinePaint.setColor(bottomLineColor);

        int cx = getWidth() / 2;
        int cy = topLineHeight;
        int radius = shouldBiggerNode ? nodeBiggerRadius : nodeNormalRadius;
        canvas.drawCircle(cx, cy, radius, nodePaint);

        if (!hiddenTopLine)
        {
            topLinePaint.setStyle(Style.FILL);
            canvas.drawLine(cx, 0, cx, cy, topLinePaint);
        }

        if (!hiddenBottomLine)
        {
            bottomLinePaint.setStyle(Style.FILL);
            canvas.drawLine(cx, cy, cx, getHeight(), bottomLinePaint);
        }
    }
}

