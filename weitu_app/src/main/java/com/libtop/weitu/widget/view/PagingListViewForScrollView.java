package com.libtop.weitu.widget.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.libtop.weitu.R;
import com.paging.listview.LoadingView;
import com.paging.listview.PagingBaseAdapter;
import com.paging.listview.PagingListView;

import java.util.List;


public class PagingListViewForScrollView extends PagingListView
{

    public PagingListViewForScrollView(Context context) {
        super(context);
    }

    public PagingListViewForScrollView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }


    public PagingListViewForScrollView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
