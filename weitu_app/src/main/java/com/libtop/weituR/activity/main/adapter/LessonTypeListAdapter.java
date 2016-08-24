package com.libtop.weituR.activity.main.adapter;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.main.dto.LessonTypeData;
import com.libtop.weituR.base.BaseAdapter;

import java.util.List;

/**
 * Created by Administrator on 2016/1/20 0020.
 */
public class LessonTypeListAdapter extends BaseAdapter<LessonTypeData> {
    private int[] bgRes={R.color.yellow1,R.color.green4,R.color.bule1
            ,R.color.green5,R.color.purple1,R.color.red1};

    public LessonTypeListAdapter(Context context, List<LessonTypeData> data) {
        super(context, data, R.layout.item_list_lesson_type);
    }

    @Override
    protected void newView(View convertView) {
        Holder holder = new Holder();
        holder.container = (LinearLayout) convertView.findViewById(R.id.container);
        holder.tvTitle=(TextView)convertView.findViewById(R.id.title);
        holder.tvSub=(TextView)convertView.findViewById(R.id.sub);
        convertView.setTag(holder);
    }

    public void update(List<LessonTypeData> data){
        mData=data;
        notifyDataSetChanged();
    }

    private int getBg(int position){
        if (position==0) return bgRes[position];
        int index;
        index=position%(bgRes.length-1);
        if (index==0) return bgRes[bgRes.length-1];
        return bgRes[index];
    }

    @Override
    protected void holderView(View convertView, LessonTypeData lessonTyeData, int position) {
        Holder holder = (Holder) convertView.getTag();
        int res=getBg(position);
        holder.container.setBackgroundResource(res);
        holder.tvTitle.setText(lessonTyeData.getTitle());
        holder.tvSub.setText(lessonTyeData.getSubTitle());
    }

    private class Holder{
        LinearLayout container;
        TextView tvTitle,tvSub;
    }
}
