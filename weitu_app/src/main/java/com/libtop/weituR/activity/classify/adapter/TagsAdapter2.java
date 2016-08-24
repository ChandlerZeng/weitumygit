package com.libtop.weituR.activity.classify.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.classify.bean.TabBean;

import java.util.List;

import io.vov.vitamio.utils.Log;

/**
 * Created by Administrator on 2016/3/16 0016.
 */
public class TagsAdapter2 extends BaseAdapter {
    private List<TabBean> mdata;
    private Context mcontext;

    public TagsAdapter2(Context context, List<TabBean> data) {
        Log.e("fuck","+mdata.get(position)");
        this.mcontext = context;
        this.mdata = data;
        setFirst();
    }

    @Override
    public int getCount() {
        return mdata.size();
    }

    @Override
    public Object getItem(int position) {
        return mdata.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder ;
        if (convertView == null) {
            holder = new Holder();
            convertView = LayoutInflater.from(mcontext).inflate(R.layout.item_list_search_tab2, null); //mContext指的是调用的Activtty
            holder.text = (TextView) convertView.findViewById(R.id.radio);
            holder.textvertical = (TextView) convertView.findViewById(R.id.vertical);
            holder.texthorizontal = (TextView) convertView.findViewById(R.id.horizontal);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        holder.text.setText(mdata.get(position).name);
        if (mdata.get(position).getIscheck()) {
            holder.textvertical.setVisibility(View.GONE);

        } else {
            holder.textvertical.setVisibility(View.VISIBLE);

        }

        return convertView;
    }

    private void setFirst() {
        for (int i = 0; i < mdata.size(); i++) {
            if (i == 0)
                mdata.get(i).setIscheck(true);
            else
                mdata.get(i).setIscheck(false);
        }
    }

    public void setData(List<TabBean> data) {
        this.mdata = data;
        setFirst();
        notifyDataSetChanged();
    }

    public void setData(int count) {
        for (int i = 0; i < mdata.size(); i++) {
            if (i == count)
                mdata.get(i).setIscheck(true);
            else
                mdata.get(i).setIscheck(false);
        }
        notifyDataSetChanged();
    }

    class Holder {
        public TextView text, textvertical, texthorizontal;
    }

}
