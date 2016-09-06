package com.libtop.weitu.activity.search.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.search.dto.CommonDto;
import com.libtop.weitu.base.impl.ImgAdapter;
import com.libtop.weitu.utils.ContantsUtil;
import com.libtop.weitu.utils.DateUtil;
import com.squareup.picasso.Picasso;

import java.util.List;


public class CommonAdapter extends ImgAdapter
{

    public CommonAdapter(Context context, List<?> data)
    {
        super(context, data, R.layout.item_common_layout);
    }


    @Override
    protected void newView(View convertView)
    {
        ViewHolder holder = new ViewHolder();
        holder.photo = (ImageView) convertView.findViewById(R.id.icon);
        holder.name = (TextView) convertView.findViewById(R.id.name);
        holder.time = (TextView) convertView.findViewById(R.id.time);
        holder.content = (TextView) convertView.findViewById(R.id.content);
        convertView.setTag(holder);
    }


    @Override
    protected void holderView(View convertView, Object itemObject, int position)
    {
        ViewHolder holder = (ViewHolder) convertView.getTag();
        CommonDto dto = (CommonDto) itemObject;
        String folder = dto.uid.substring(0, 8);
        String avatar = ContantsUtil.AVATARHOST + "/" + Integer.parseInt(folder, 16) % 1024 + "/" + Integer.parseInt(folder, 16) % 2048 + "/" + dto.uid + "-150.jpg";
        Picasso.with(mContext).load(avatar).into(holder.photo);
        holder.content.setText(dto.content);
        holder.name.setText(dto.username);
        holder.time.setText(DateUtil.parseToDate(dto.timeline));
    }


    class ViewHolder
    {
        ImageView photo;
        TextView name;
        TextView time;
        TextView content;
    }

}
