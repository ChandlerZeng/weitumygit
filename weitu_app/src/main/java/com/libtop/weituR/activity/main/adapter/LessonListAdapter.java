package com.libtop.weituR.activity.main.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.main.dto.LessonData;
import com.libtop.weituR.base.impl.ImgAdapter;
import com.libtop.weituR.utils.ContantsUtil;
import com.squareup.picasso.Picasso;


import java.util.List;

/**
 * Created by Administrator on 2016/1/20 0020.
 */
public class LessonListAdapter extends ImgAdapter {
    public LessonListAdapter(Context context, List<LessonData.Item> data) {
        super(context, data, R.layout.item_list_lesson);
    }

    public void update(List<LessonData.Item> data){
        mData=data;
        notifyDataSetChanged();
    }

    @Override
    protected void newView(View convertView) {
        Holder holder = new Holder();
        holder.icon = (ImageView) convertView.findViewById(R.id.icon);
        holder.tvTitle=(TextView)convertView.findViewById(R.id.title);
        holder.tvUpload=(TextView)convertView.findViewById(R.id.uploader);
        convertView.setTag(holder);
    }

    @Override
    protected void holderView(View convertView, Object o, int position) {
        LessonData.Item item=(LessonData.Item)o;
        Holder holder = (Holder) convertView.getTag();
        holder.tvTitle.setText(item.getTitle());
        holder.tvUpload.setText("上传者:" + item.getUploadUsername());
//        ImageLoader.getInstance().displayImage(ContantsUtil.getCoverUrl(item.getId()),holder.icon,mOptions);
        Picasso.with(mContext).load(ContantsUtil.getCoverUrl(item.getId())).into(holder.icon);
//        x.image().bind(holder.icon,ContantsUtil.getCoverUrl(item.getId()),mOptions);
    }

    private class Holder{
        ImageView icon;
        TextView tvTitle,tvUpload;
    }
}
