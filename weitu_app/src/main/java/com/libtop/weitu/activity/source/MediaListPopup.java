package com.libtop.weitu.activity.source;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.utils.DisplayUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2016/1/6 0006.
 */
public class MediaListPopup extends PopupWindow implements AdapterView.OnItemClickListener
{
    private Context mContext;
    private OnItemClickListener mItemClickListener;
    private List<String> mDatas = new ArrayList<String>();
    private MediaListAdapter mAdapter;


    public MediaListPopup(Context context, OnItemClickListener itemClickListener)
    {
        super(context);
        mContext = context;
        mItemClickListener = itemClickListener;
        setUp();

    }


    private void setUp()
    {
        View root = LayoutInflater.from(mContext).inflate(R.layout.pop_string_lists, null);
        setContentView(root);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(DisplayUtils.dp2px(mContext, 80));
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(0x000000));
        ListView listView = (ListView) root.findViewById(R.id.list);
        mAdapter = new MediaListAdapter();
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);
    }


    public void show(View parent, List<String> data)
    {
        mDatas = data;
        mAdapter.notifyDataSetChanged();
        showAsDropDown(parent);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        if (mItemClickListener != null)
        {
            mItemClickListener.onItemClick(position);
        }
        dismiss();
    }


    public interface OnItemClickListener
    {
        void onItemClick(int position);
    }


    private class MediaListAdapter extends BaseAdapter
    {

        @Override
        public int getCount()
        {
            return mDatas.size();
        }


        @Override
        public Object getItem(int position)
        {
            return mDatas.get(position);
        }


        @Override
        public long getItemId(int position)
        {
            return position;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null)
            {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_string2, null);
            }
            TextView tv = (TextView) convertView.findViewById(R.id.text);
            String info = mDatas.get(position);
            tv.setText(TextUtils.isEmpty(info) ? ("未知标题" + position) : info);
            return convertView;
        }
    }
}
