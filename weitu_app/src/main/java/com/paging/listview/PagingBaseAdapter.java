package com.paging.listview;

import java.util.List;

import android.widget.BaseAdapter;


public abstract class PagingBaseAdapter<T> extends BaseAdapter
{
    protected List<T> items;


    public PagingBaseAdapter()
    {
        this(null);
    }


    public PagingBaseAdapter(List<T> items)
    {
        this.items = items;
    }


    public void addMoreItems(List<T> newItems)
    {
        this.items.addAll(newItems);
        notifyDataSetChanged();
    }


    public void addMoreItems(int location, List<T> newItems)
    {
        this.items.addAll(location, newItems);
        notifyDataSetChanged();
    }


    public void addMoreItem(T newItem)
    {
        this.items.add(newItem);
        notifyDataSetChanged();
    }


    public void addMoreItem(int location, T newItem)
    {
        this.items.add(location, newItem);
        notifyDataSetChanged();
    }


    public void removeAllItems()
    {
        this.items.clear();
        notifyDataSetChanged();
    }


    public void removeItem(int location)
    {
        this.items.remove(location);
        notifyDataSetChanged();
    }


    public void setItem(int location, T newItem)
    {
        this.items.set(location, newItem);
        notifyDataSetChanged();
    }
}
