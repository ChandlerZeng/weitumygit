package com.libtop.weituR.activity.main.adapter;


import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.libtop.weitu.R;
import com.libtop.weituR.base.BaseAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by LianTu on 2016/4/25.
 */
public class VideoCoverAdapter extends BaseAdapter<String> {

    public VideoCoverAdapter(Context context, List<String> data) {
        super(context, data, R.layout.item_video_cover);
    }

    @Override
    protected void newView(View convertView) {
        Holder holder = new Holder();
        holder.thumbImage = (ImageView)convertView.findViewById(R.id.thumb_image);
        convertView.setTag(holder);
    }

    @Override
    protected void holderView(View convertView, String videoCoverPath, int position) {
        Holder holder = (Holder) convertView.getTag();
        if (!TextUtils.isEmpty(videoCoverPath)){
            Picasso.with(mContext).load(videoCoverPath)
                    .error(R.drawable.default_error)
                    .placeholder(R.drawable.default_image)
                    .fit().into(holder.thumbImage);
        }
    }

    private static class Holder{
        ImageView thumbImage;
    }
}
