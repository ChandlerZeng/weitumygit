package com.libtop.weitu.activity.main.adapter;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.main.dto.NoticeInfo;
import com.libtop.weitu.base.BaseAdapter;
import com.libtop.weitu.utils.DateUtil;
import com.libtop.weitu.widget.slidebar.ScheduleView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/14 0014.
 */
public class NoticeListAdapter extends BaseAdapter<NoticeInfo> {
    private Context context;
    private StringBuffer stringBuffer = new StringBuffer();
    private String dateStr;
    private String noticeId;
    private List<String> idList = new ArrayList<String>();
    private List<NoticeInfo> lists;

    public NoticeListAdapter(Context context, List<NoticeInfo> data) {
        super(context, data, R.layout.item_list_notice2);
        this.context = context;
        this.lists = data;
    }

    @Override
    protected void newView(View convertView) {
        Holder holder = new Holder();
        holder.titleText = (TextView) convertView.findViewById(R.id.news_title_txt);
        holder.timeStr = (TextView) convertView.findViewById(R.id.news_date_txt);
        holder.timeline = (ScheduleView) convertView.findViewById(R.id.timeline);
        holder.timeline.setTopLineColor(context.getResources().getColor(R.color.green1));
        holder.timeline.setBottomLineColor(context.getResources().getColor(R.color.green1));
        holder.timeline.setNodeColor(context.getResources().getColor(R.color.green1));
        convertView.setTag(holder);
    }

    @Override
    protected void holderView(View convertView, NoticeInfo noticeInfo, int position) {
        Holder holder = (Holder) convertView.getTag();
        String titleStr = noticeInfo.title.trim();

        if (position == 0)
            holder.timeline.setHiddenTopLine(true);
        else if (position == (lists.size()-1))
            holder.timeline.setHiddenBottomLine(true);
        else
            holder.timeline.setHiddenTopLine(false);
            holder.timeline.setHiddenBottomLine(false);

        if (!titleStr.equals("")) {
            holder.titleText.setVisibility(View.VISIBLE);
            holder.titleText.setText(titleStr);
        } else {
            holder.titleText.setVisibility(View.GONE);
        }

        dateStr = DateUtil.getSendTimeDistance(noticeInfo.dateLine);
        noticeId = noticeInfo.id;
        if (stringBuffer.indexOf(dateStr) == -1) {
            stringBuffer.append(dateStr);
            idList.add(noticeId);
        }

        if (idList.contains(noticeId) & !titleStr.equals("")) {
            holder.timeline.setShouldBiggerNode(true);
            holder.timeStr.setVisibility(View.VISIBLE);
            holder.timeStr.setText(dateStr);
        } else {
            holder.timeline.setShouldBiggerNode(false);
            holder.timeStr.setVisibility(View.GONE);
        }
    }

    private class Holder {
        TextView titleText, timeStr;
        ScheduleView timeline;
    }
}
