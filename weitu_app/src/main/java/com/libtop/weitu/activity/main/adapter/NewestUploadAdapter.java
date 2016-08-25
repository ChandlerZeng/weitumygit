package com.libtop.weitu.activity.main.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.main.dto.DocBean;
import com.libtop.weitu.utils.DateUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Zeng on 2016/8/11.
 */
public class NewestUploadAdapter extends com.libtop.weitu.base.BaseAdapter<DocBean> {
    private List<DocBean> lists;
    private Context context;
    private LayoutInflater inflater;

    public NewestUploadAdapter(Context context, List<DocBean> lists) {
        super(context, lists, R.layout.item_list_other);
        this.lists = lists;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    protected void newView(View convertView) {
        ViewHolder holder = new ViewHolder();
        holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
        holder.tvTitle = (TextView) convertView.findViewById(R.id.title);
        holder.tvTag = (TextView) convertView.findViewById(R.id.tv_tag);
        holder.tvUploader = (TextView) convertView.findViewById(R.id.tv_uploader);
        holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
        convertView.setTag(holder);
    }

    @Override
    protected void holderView(View convertView, DocBean docBean, int position) {
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.tvTitle.setText(docBean.title);
        bindData(docBean.cover, holder.imageView, null);
        if (!TextUtils.isEmpty(docBean.categoriesName1)) {
            holder.tvTag.setText(docBean.categoriesName1);
        }
        if (!TextUtils.isEmpty(docBean.categoriesName2)) {
            holder.tvTag.setText(docBean.categoriesName1 + "/" + docBean.categoriesName2);
        }
        holder.tvUploader.setText("上传：" + docBean.uploadUsername);
        if (docBean.uploadTime != null) {
            holder.tvTime.setText(DateUtil.parseToDate(docBean.uploadTime));
        }
    }

    void bindData(String url, ImageView image, String fileurl) {
        Picasso.with(mContext)
                .load(url)
                .placeholder(R.drawable.default_image)
                .fit()
                .centerInside()
                .into(image);
    }

    public void updateList(List<DocBean> lists) {
        this.lists = lists;
        notifyDataSetChanged();
    }

    class ViewHolder {
        ImageView imageView;
        TextView tvTitle;
        TextView tvTag;
        TextView tvUploader;
        TextView tvTime;
    }

}
