package com.libtop.weitu.viewadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * 通用 BaseAdapter
 */
public abstract class CommonAdapter<T> extends BaseAdapter
{
    protected Context context;
    protected LayoutInflater inflater;
    protected int itemLayoutId;
    protected List<T> datas;

    private OnViewInflateListener mOnViewInflateListener;


    public interface OnViewInflateListener
    {
        void onInflate(View inflated);
    }


    public OnViewInflateListener getOnViewInflateListener()
    {
        return mOnViewInflateListener;
    }


    public void setOnViewInflateListener(OnViewInflateListener onViewInflateListener)
    {
        this.mOnViewInflateListener = onViewInflateListener;
    }


    public CommonAdapter(Context context, int itemLayoutId)
    {
        this(context, itemLayoutId, null);
    }


    public CommonAdapter(Context context, int itemLayoutId, List<T> datas)
    {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.itemLayoutId = itemLayoutId;

        this.datas = (datas == null ? new ArrayList<T>() : new ArrayList<T>(datas));
    }


    @Override
    public int getCount()
    {
        return this.datas.size();
    }


    @Override
    public T getItem(int position)
    {
        return this.datas.get(position);
    }


    @Override
    public long getItemId(int position)
    {
        return position;
    }


    public int getItemPosition(T item)
    {
        return this.datas.indexOf(item);
    }


    public void add(T item)
    {
        this.datas.add(item);
        notifyDataSetChanged();
    }


    public void addAll(List<T> list)
    {
        this.datas.addAll(list);
        notifyDataSetChanged();
    }


    public void set(T oldItem, T newItem)
    {
        setItem(this.datas.indexOf(oldItem), newItem);
    }


    public void setItem(int index, T item)
    {
        this.datas.set(index, item);
        notifyDataSetChanged();
    }


    public void removeItem(T item)
    {
        this.datas.remove(item);
        notifyDataSetChanged();
    }


    public void removeItem(int index)
    {
        this.datas.remove(index);
        notifyDataSetChanged();
    }


    public void replaceAll(List<T> list)
    {
        this.datas.clear();

        if (list != null && list.size() > 0)
        {
            this.datas.addAll(list);
        }

        notifyDataSetChanged();
    }


    public boolean containsItem(T item)
    {
        return this.datas.contains(item);
    }


    public List<T> getItems()
    {
        return this.datas;
    }


    public void clear()
    {
        this.datas.clear();
        notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final ViewHolderHelper helper = ViewHolderHelper.get(context, convertView, parent, itemLayoutId, position, mOnViewInflateListener);

        convert(helper, getItem(position), position);

        return helper.getConvertView();
    }


    public abstract void convert(ViewHolderHelper helper, T object, int position);
}
