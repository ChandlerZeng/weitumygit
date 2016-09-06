package com.libtop.weitu.activity.search.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.base.BaseAdapter;
import com.libtop.weitu.dao.bean.Search;

import java.util.List;


public class SearchAdapter extends BaseAdapter<Search>
{

    private Context mContext;
    private List<Search> mData;
    private OnDeleteImgClickListener mDeleteImgClickListener;


    public SearchAdapter(Context context, List<Search> data)
    {
        super(context, data, R.layout.item_search_layout);
        mContext = context;
        mData = data;
    }


    public SearchAdapter(Context context, List<Search> data, OnDeleteImgClickListener listener)
    {
        super(context, data, R.layout.item_search_layout);
        mContext = context;
        mData = data;
        this.mDeleteImgClickListener = listener;
    }


    @Override
    protected void newView(View convertView)
    {
        ViewHolder holder = new ViewHolder();
        holder.title = (TextView) convertView.findViewById(R.id.title);
        holder.imageDelete = (ImageView) convertView.findViewById(R.id.delete_image);
        convertView.setTag(holder);
    }


    @Override
    protected void holderView(final View convertView, final Search search, final int position)
    {
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.title.setText(search.getName());
        holder.imageDelete.setImageResource(R.drawable.search_delete);
        holder.imageDelete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mDeleteImgClickListener.onDeleteImgTouch(convertView, search, position);
            }
        });
    }


    public interface OnDeleteImgClickListener
    {
        void onDeleteImgTouch(View v, Search search, int position);
    }


    class ViewHolder
    {
        TextView title;
        ImageView imageDelete;

    }

}
