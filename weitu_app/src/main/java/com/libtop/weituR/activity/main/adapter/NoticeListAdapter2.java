package com.libtop.weituR.activity.main.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.main.dto.NoticeInfo;
import com.libtop.weituR.base.BaseAdapter;
import com.libtop.weituR.utils.DateUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/14 0014.
 */
public class NoticeListAdapter2 extends BaseAdapter<NoticeInfo> {
    private StringBuffer stringBuffer=new StringBuffer();
    private String dateStr;
    private String noticeId;
    private List<String> idList=new ArrayList<String>();
    public NoticeListAdapter2(Context context, List<NoticeInfo> data) {
        super(context, data, R.layout.item_list_notice2);
    }

    @Override
    protected void newView(View convertView) {
        Holder holder = new Holder();
        holder.titleText = (TextView) convertView.findViewById(R.id.news_title_txt);
        holder.timeStr=(TextView)convertView.findViewById(R.id.news_date_txt);
        convertView.setTag(holder);
    }

    @Override
    protected void holderView(View convertView, NoticeInfo noticeInfo, int position) {
        Holder holder = (Holder) convertView.getTag();
        String titleStr= noticeInfo.title.trim();
        if(!titleStr.equals("")){
            holder.titleText.setVisibility(View.VISIBLE);
            holder.titleText.setText(titleStr);
        }else {
            holder.titleText.setVisibility(View.GONE);
        }
        dateStr = DateUtil.getSendTimeDistance(noticeInfo.dateLine+ 86400000);
        noticeId=noticeInfo.id;
        if(stringBuffer.indexOf(dateStr)==-1) {
            stringBuffer.append(dateStr);
            idList.add(noticeId);
        }
        if(idList.contains(noticeId)&!titleStr.equals("")){
            holder.timeStr.setVisibility(View.VISIBLE);
            holder.timeStr.setText(dateStr);
        }else {
            holder.timeStr.setVisibility(View.INVISIBLE);
        }
    }

    private class Holder{
        TextView titleText,timeStr;
    }
}
