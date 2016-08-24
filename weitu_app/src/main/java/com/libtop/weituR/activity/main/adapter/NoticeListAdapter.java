package com.libtop.weituR.activity.main.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.main.dto.NoticeInfo;
import com.libtop.weituR.base.BaseAdapter;
import com.libtop.weituR.utils.DateUtil;

import java.util.List;

/**
 * Created by Administrator on 2016/1/14 0014.
 */
public class NoticeListAdapter extends BaseAdapter<NoticeInfo> {
    public NoticeListAdapter(Context context, List<NoticeInfo> data) {
        super(context, data, R.layout.item_list_notice);
    }

    @Override
    protected void newView(View convertView) {
        Holder holder = new Holder();
        holder.titleText = (TextView) convertView.findViewById(R.id.title);
        holder.hotIcon = (ImageView) convertView.findViewById(R.id.ishot);
        holder.library=(TextView)convertView.findViewById(R.id.library);
        holder.comments=(TextView)convertView.findViewById(R.id.comment);
        holder.timeStr=(TextView)convertView.findViewById(R.id.time);
        convertView.setTag(holder);
    }

    @Override
    protected void holderView(View convertView, NoticeInfo noticeInfo, int position) {
        Holder holder = (Holder) convertView.getTag();
        if (position<=3){
            holder.hotIcon.setVisibility(View.VISIBLE);
        }else {
            holder.hotIcon.setVisibility(View.GONE);
        }
        holder.titleText.setText(noticeInfo.title);
        holder.comments.setText(0+"");
        holder.timeStr.setText(DateUtil.getSendTimeDistance(noticeInfo.dateLine));

    }

    private class Holder{
        ImageView hotIcon;
        TextView titleText,library,comments,timeStr;
    }
}
