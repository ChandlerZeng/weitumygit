package com.libtop.weitu.activity.classify.adapter;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.classify.bean.KeyBean;
import com.libtop.weitu.base.BaseAdapter;
import com.libtop.weitu.widget.gridview.FixedGridView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2016/1/13.
 */
public class KeysListAdapter extends BaseAdapter<KeyBean>
{
    private Map<KeyBean, KeysGridAdapter> mItems = new HashMap<KeyBean, KeysGridAdapter>();
    private OnKeySelectedListener mOnkeySelected;


    public KeysListAdapter(Context context, List<KeyBean> data, OnKeySelectedListener onkeySelected)
    {
        super(context, data, R.layout.item_list_search_keys);
        fillData(data);
        mOnkeySelected = onkeySelected;
    }


    private void fillData(List<KeyBean> data)
    {
        for (KeyBean bean : data)
        {
            mItems.put(bean, new KeysGridAdapter(mContext, bean.subCategories));
        }
    }


    public void update(List<KeyBean> data)
    {
        mItems.clear();
        fillData(data);
        notifyDataSetChanged();
    }


    @Override
    protected void newView(View convertView)
    {
        Holder holder = new Holder();
        holder.titleText = (TextView) convertView.findViewById(R.id.title);
        holder.keyGrid = (FixedGridView) convertView.findViewById(R.id.grid_view);
        convertView.setTag(holder);
    }


    @Override
    protected void holderView(View convertView, KeyBean s, final int parentIndex)
    {
        Holder holder = (Holder) convertView.getTag();
        holder.titleText.setText(s.name);
        holder.keyGrid.setAdapter(mItems.get(s));
        final List<KeyBean.Child> keys = s.subCategories;
        holder.keyGrid.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (mOnkeySelected != null)
                {
                    mOnkeySelected.onSelectKey(keys.get(position).name, parentIndex, position);
                }
            }
        });
    }


    public interface OnKeySelectedListener
    {
        void onSelectKey(String key, int parentIndex, int childIndex);
    }


    private class Holder
    {
        TextView titleText;
        FixedGridView keyGrid;
    }
}
