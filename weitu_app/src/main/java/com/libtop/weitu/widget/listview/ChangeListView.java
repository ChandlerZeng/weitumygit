package com.libtop.weitu.widget.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;


/**
 * <p>
 * Title: ChangeListView.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * CreateTime：16/6/12
 * </p>
 *
 * @author 作者名
 * @version common v1.0
 */
public class ChangeListView extends ListView
{

    public boolean isExpandable;


    public ChangeListView(Context context)
    {
        super(context);
    }


    public ChangeListView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }


    public ChangeListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }


    @Override
    /**
     * 重写该方法，达到使ListView适应ScrollView的效果
     */ protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}