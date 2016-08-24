package com.libtop.weituR.activity.search.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.search.dto.SearchResult;
import com.libtop.weituR.base.impl.ImgAdapter;
import com.libtop.weituR.utils.ContantsUtil;
import com.libtop.weituR.utils.DisplayUtils;
import com.squareup.picasso.Picasso;


import java.util.List;

/**
 * Created by Administrator on 2015/12/23 0023.
 */
public class ResultGridAdapter extends ImgAdapter{
    private int imgSize;
    public ResultGridAdapter(Context context, List<?> data) {
        super(context, data, R.layout.item_grid_result);
        imgSize= DisplayUtils.getDisplayWith(context)/2;
    }

    @Override
    protected void newView(View convertView) {
        ViewHolder holder = new ViewHolder();
        holder.icon = (ImageView) convertView.findViewById(R.id.icon);
        holder.name = (TextView) convertView.findViewById(R.id.title);

        ViewGroup.LayoutParams params=holder.icon.getLayoutParams();
        params.width=imgSize;
        params.height=imgSize;
        holder.icon.setLayoutParams(params);

        convertView.setTag(holder);
    }

    @Override
    protected void holderView(View convertView, Object itemObject,int position) {
        ViewHolder holder = (ViewHolder) convertView.getTag();
        SearchResult data=(SearchResult)itemObject;
        holder.name.setText(data.title);

        Picasso.with(mContext).load(ContantsUtil.getCoverUrl(data.id)).into(holder.icon);
//        x.image().bind(holder.icon,ContantsUtil.getCoverUrl(data.id),mOptions);

//        ImageLoader.getInstance().displayImage(
//                ContantsUtil.getCoverUrl(data.id), holder.icon, mOptions);

        //测试用
//        ImageLoader.getInstance().displayImage(
//                ContantsUtil.getCoverUrl("54d082a4e4b028a441d70922"), holder.icon, options);
    }

    protected class ViewHolder {
        ImageView icon;
        TextView name;
    }
}
