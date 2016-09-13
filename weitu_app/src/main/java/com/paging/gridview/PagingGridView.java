package com.paging.gridview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;

import com.libtop.weitu.R;

import java.util.List;


public class PagingGridView extends HeaderGridView
{
    private boolean isLoading;
    private boolean hasMoreItems;
    private Pagingable pagingableListener;
    private LoadingView loadinView;


    public PagingGridView(Context context)
    {
        super(context);
        init();
    }


    public PagingGridView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }


    public PagingGridView(Context context, AttributeSet attrs, int defStyle)
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
            removeFooterView(loadinView);
        }
        else if (findViewById(R.id.loading_view) == null)
        {
            ((ViewGroup) loadinView.getParent()).removeView(loadinView);
            addFooterView(loadinView, null, false);

            ListAdapter adapter = ((FooterViewGridAdapter) getAdapter()).getWrappedAdapter();
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
            ListAdapter adapter = ((FooterViewGridAdapter) getAdapter()).getWrappedAdapter();
            if (adapter instanceof PagingBaseAdapter)
            {
                ((PagingBaseAdapter) adapter).addMoreItems(newItems);
            }
        }
    }


    private void init()
    {
        isLoading = false;
        loadinView = new LoadingView(getContext());

        addFooterView(loadinView, null, false);

        setOnScrollListener(new OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {
                //DO NOTHING...
            }


            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
                if (totalItemCount > 0)
                {
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
            }
        });
    }


    public interface Pagingable
    {
        void onLoadMoreItems();
    }
}
