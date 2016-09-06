package com.libtop.weitu.activity.search.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.search.dto.SearchResult;
import com.libtop.weitu.base.impl.ImgAdapter;
import com.libtop.weitu.utils.selector.MultiImageSelectorFragment;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * <p>
 * Title: ResultListAdapter2.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * CreateTime：16/5/30
 * </p>
 *
 * @author 陆
 * @version common v1.0
 */
public class ResultListAdapter2 extends ImgAdapter
{
    //0文档 1图片
    private int type = 0;
    private Context context;
    private String urlString;


    public ResultListAdapter2(Context context, List<?> data, int type)
    {
        super(context, data, R.layout.item_list_result3);
        this.type = type;
        this.context = context;
    }


    @Override
    protected void newView(View convertView)
    {
        ViewHolder holder = new ViewHolder();
        holder.iconCover = (ImageView) convertView.findViewById(R.id.see);
        holder.titleText = (TextView) convertView.findViewById(R.id.doc_title);
        holder.tvTag = (TextView) convertView.findViewById(R.id.tv_tag);
        holder.uploaderText = (TextView) convertView.findViewById(R.id.doc_author);
        holder.timeText = (TextView) convertView.findViewById(R.id.doc_time);
        holder.imageText = (TextView) convertView.findViewById(R.id.doc_size);
        convertView.setTag(holder);
    }


    @Override
    protected void holderView(View convertView, Object itemObject, int position)
    {
        ViewHolder holder = (ViewHolder) convertView.getTag();
        SearchResult data = (SearchResult) itemObject;
        holder.titleText.setText(data.title);
        if (!TextUtils.isEmpty(data.categoriesName1))
        {
            holder.tvTag.setText(data.categoriesName1);
        }
        if (!TextUtils.isEmpty(data.categoriesName2))
        {
            holder.tvTag.setText(data.categoriesName1 + "/" + data.categoriesName2);
        }
        holder.uploaderText.setText("上传者:" + data.uploadUsername);
        if (type == 0)
        {
            if (TextUtils.isEmpty(data.cover))
            {
                data.cover = "http://";
            }
            Picasso.with(context).load(data.cover).placeholder(R.drawable.pdf).error(R.drawable.pdf).fit().into(holder.iconCover);
        }
        else
        {
            getImage(holder.iconCover, data.cover);
        }
    }


    protected class ViewHolder
    {
        ImageView iconCover;
        TextView titleText, uploaderText, timeText, imageText, tvTag;
    }


    private void getImage(ImageView image, String url)
    {
        String a = url;
        Picasso.with(context).load(a).placeholder(R.drawable.default_image).tag(MultiImageSelectorFragment.TAG).fit().centerCrop().into(image);
    }
}
