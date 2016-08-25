package com.libtop.weitu.activity.search.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.libtop.weitu.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by LianTu on 2016/6/1.
 */
public class SingleSelectAdapter extends BaseAdapter {

    private List<String> lists;
    private Context context;
    //用于记录每个RadioButton的状态，并保证只可选一个
    private HashMap<String,Boolean> states=new HashMap<String,Boolean>();

    public SingleSelectAdapter(Context context, List<String> lists){
        this.lists = lists;
        this.context = context;
        states.put(String.valueOf(0),true);
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_string_select, null);
            holder = new ViewHolder();
            holder.llOut = (LinearLayout) convertView.findViewById(R.id.ll_out);
            holder.tvIn = (TextView) convertView.findViewById(R.id.tv_in);
            convertView.setTag(holder);
        }else{
            holder=(ViewHolder) convertView.getTag();
        }

        if(states.get(String.valueOf(position)) == null || states.get(String.valueOf(position))== false){
            holder.llOut.setBackgroundColor(Color.WHITE);
        }else{
            holder.llOut.setBackgroundResource(R.drawable.green_board);
        }
        holder.tvIn.setText(lists.get(position));
        return convertView;
    }

    public void setSingleSelect(int position){
        for(String key:states.keySet()){
            states.put(key, false);

        }
        states.put(String.valueOf(position),true);
        notifyDataSetChanged();
    }

    static class ViewHolder {
        LinearLayout llOut;
        TextView tvIn;
    }
}
