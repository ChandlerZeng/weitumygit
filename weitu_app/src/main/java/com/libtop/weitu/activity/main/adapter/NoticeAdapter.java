package com.libtop.weitu.activity.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.main.dto.NoticeInfo;

import java.util.List;

/**
 * Created by LianTu on 2016/6/28.
 */
public class NoticeAdapter extends BaseAdapter {

    private List<NoticeInfo> mlist;
    private Context mContext;
    private String[] aaa = {"【沙龙】", "【校园】", "【培训】", "【培训】"};

    private LayoutInflater mInflater;

    public NoticeAdapter(Context context,List<NoticeInfo> list){
        mContext = context;
        mlist = list;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object getItem(int i) {
        return mlist.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        Notice holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.notice_layout, null);
            holder = new Notice();
            holder.noticeTitle = (TextView) convertView.findViewById(R.id.one);
            holder.noticeContent = (TextView) convertView.findViewById(R.id.two);
            convertView.setTag(holder);
        } else {
            holder = (Notice) convertView.getTag();
        }
        if(!mlist.get(position).title.replaceAll(" ", "").trim().equals("")){
            holder.noticeContent.setVisibility(View.VISIBLE);
            holder.noticeContent.setText(mlist.get(position).title);
        }else{
            holder.noticeTitle.setVisibility(View.GONE);
            holder.noticeContent.setVisibility(View.GONE);
        }
        return convertView;
    }

    public void setData(List<NoticeInfo> lists){
        mlist = lists;
        notifyDataSetChanged();
    }

    class Notice {
        public TextView noticeTitle;
        public TextView noticeContent;
    }
}
