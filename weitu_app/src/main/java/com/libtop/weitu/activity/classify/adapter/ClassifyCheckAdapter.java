package com.libtop.weitu.activity.classify.adapter;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.classify.bean.ClassifyBean;
import com.libtop.weitu.base.BaseAdapter;

import java.util.List;

/**
 * Created by LianTu on 2016/7/21.
 */
public class ClassifyCheckAdapter extends BaseAdapter<ClassifyBean>{

    private SparseBooleanArray sBarray = new SparseBooleanArray();
    private boolean isFilter;
    private List<ClassifyBean> mdata;

    public ClassifyCheckAdapter(Context context, List<ClassifyBean> data,boolean isFilter) {
        super(context, data, R.layout.item_list_check_string);
        mdata = data;
        sBarray.put(0,true);
        this.isFilter = isFilter;
    }

    @Override
    protected void newView(View convertView) {
        Holder holder = new Holder();
        holder.textView = (TextView) convertView.findViewById(R.id.textView1);
        holder.subText = (TextView) convertView.findViewById(R.id.sub_text);
        holder.checkView =(ImageView) convertView.findViewById(R.id.img_check);
        convertView.setTag(holder);
    }

    @Override
    protected void holderView(View convertView, ClassifyBean s, final int position) {
        Holder holder = (Holder) convertView.getTag();
        holder.textView.setText(s.name);
        if (isFilter){
            holder.subText.setVisibility(View.INVISIBLE);
        }else {
            holder.subText.setVisibility(View.VISIBLE);
            holder.subText.setText(s.countString);
        }
        if (sBarray.get(position)){
            holder.checkView.setVisibility(View.VISIBLE);
            holder.textView.setTextColor(mContext.getResources().getColor(R.color.newGreen));
        }else {
            holder.checkView.setVisibility(View.INVISIBLE);
            holder.textView.setTextColor(mContext.getResources().getColor(R.color.black));
        }
    }

    public void upDateData(List<ClassifyBean> data){
        mdata = data;
        notifyDataSetChanged();
    }

    public void setCheck(int position){
        sBarray.clear();
        sBarray.put(position,true);
        notifyDataSetChanged();
    }

    class Holder{
        TextView textView,subText;
        ImageView checkView;
    }
}
