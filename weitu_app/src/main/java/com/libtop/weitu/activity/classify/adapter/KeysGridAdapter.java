package com.libtop.weitu.activity.classify.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.classify.bean.KeyBean;
import com.libtop.weitu.base.BaseAdapter;

import java.util.List;

/**
 * Created by Administrator on 2016/1/13.
 */
public class KeysGridAdapter extends BaseAdapter<KeyBean.Child> {

    public KeysGridAdapter(Context context, List<KeyBean.Child> data) {
        super(context, data, R.layout.item_grid_search_key);
    }

    @Override
    public int getCount() {
        return mData.size()>9?9:mData.size();
    }

    @Override
    protected void newView(View convertView) {

    }

    @Override
    protected void holderView(View convertView, KeyBean.Child s, int position) {
        if (position>mData.size()) return;
        TextView tv=(TextView)convertView.findViewById(R.id.text);
        tv.setText(s.name);
        if (position==9){
            tv.setBackgroundResource(R.drawable.shape_search_bg);
        }else{
            tv.setBackgroundResource(R.drawable.shape_search_bg2);
        }
    }
}
