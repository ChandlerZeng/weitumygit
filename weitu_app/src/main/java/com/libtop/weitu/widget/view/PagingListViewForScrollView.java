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

import java.util.List;


public class PagingListViewForScrollView extends ListView
{
    private boolean isLoading;
    private boolean hasMoreItems;
    private Pagingable pagingableListener;
    private LoadingView loadingView;
    private OnScrollListener onScrollListener;


    public PagingListViewForScrollView(Context context)
    {
        super(context);
        init();
    }


    public PagingListViewForScrollView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }


    public PagingListViewForScrollView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }


    public boolean isLoading()
    {
        return this.isLoading;
    }


    public void setIsLoading(boolean isLoading)
    {
        this.isLoading = isLoading;
    }


    public void setPagingableListener(Pagingable pagingableListener)
    {
        this.pagingableListener = pagingableListener;
    }


    public void setHasMoreItems(boolean hasMoreItems)
    {
        this.hasMoreItems = hasMoreItems;
        if (!this.hasMoreItems)
        {
            removeFooterView(loadingView);
        }
        else if (findViewById(R.id.loading_view) == null)
        {
            // fix bug "crash when click footerview" by Sai.
            addFooterView(loadingView, null, false);

            ListAdapter adapter = ((HeaderViewListAdapter) getAdapter()).getWrappedAdapter();
            setAdapter(adapter);
        }
    }


    public boolean hasMoreItems()
    {
        return this.hasMoreItems;
    }


    public void onFinishLoading(boolean hasMoreItems, List<? extends Object> newItems)
    {
        setHasMoreItems(hasMoreItems);
        setIsLoading(false);
        if (newItems != null && newItems.size() > 0)
        {
            ListAdapter adapter = ((HeaderViewListAdapter) getAdapter()).getWrappedAdapter();
            if (adapter instanceof PagingBaseAdapter)
            {
                ((PagingBaseAdapter) adapter).addMoreItems(newItems);
            }
        }
    }


    private void init()
    {
        isLoading = false;
        loadingView = new LoadingView(getContext());

        // fix bug "crash when click footerview" by Sai.
        addFooterView(loadingView, null, false);

        super.setOnScrollListener(new OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {
                // Dispatch to child OnScrollListener
                if (onScrollListener != null)
                {
                    onScrollListener.onScrollStateChanged(view, scrollState);
                }
            }


            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {

                // Dispatch to child OnScrollListener
                if (onScrollListener != null)
                {
                    onScrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                }

                int lastVisibleItem = firstVisibleItem + visibleItemCount;
                if (!isLoading && hasMoreItems && (lastVisibleItem == totalItemCount))
                {
                    if (pagingableListener != null)
                    {
                        isLoading = true;
                        pagingableListener.onLoadMoreItems();
                    }
                }
            }
        });
    }


    @Override
    public void setOnScrollListener(OnScrollListener listener)
    {
        onScrollListener = listener;
    }


    public interface Pagingable
    {
        void onLoadMoreItems();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
