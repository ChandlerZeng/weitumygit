package com.libtop.weituR.activity.classify.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.classify.bean.ClassifyBean;
import com.libtop.weituR.base.BaseAdapter;

import java.util.List;

/**
 * Created by LianTu on 2016/7/19.
 */
public class ClassifyAdapter extends BaseAdapter<ClassifyBean>{

    public ClassifyAdapter(Context context, List<ClassifyBean> data) {
        super(context, data, R.layout.item_list_classify);
    }

    @Override
    protected void newView(View convertView) {
        ViewHolder holder = new ViewHolder();
        holder.classifyText = (TextView) convertView.findViewById(R.id.name);
        holder.resText = (TextView) convertView.findViewById(R.id.resCount);
        convertView.setTag(holder);
    }

    @Override
    protected void holderView(View convertView, ClassifyBean classifyBean, int position) {
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.classifyText.setText(classifyBean.name);
        holder.resText.setText("共"+classifyBean.count+"种资源");
    }

    private class ViewHolder {
        TextView classifyText, resText;
    }
}
