package com.libtop.weitu.activity.main.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.main.dto.DocBean;
import com.libtop.weitu.base.BaseAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * <p>
 * Title: MainImageAdapter.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * CreateTime：16/6/12
 * </p>
 *
 * @author 陆
 * @version common v1.0
 */
public class MainImageAdapter extends BaseAdapter<DocBean> {

    private boolean isNewest;

    public MainImageAdapter(Context context, List<DocBean> data) {
        super(context, data, R.layout.main_image_adapter);
        this.isNewest = false;
    }

    public MainImageAdapter(Context context, List<DocBean> data,boolean isNewest) {
        super(context, data, R.layout.main_image_adapter2);
        this.isNewest = isNewest;
    }

    @Override
    protected void newView(View convertView) {
        Holder holder = new Holder();
        holder.imageView = (ImageView) convertView.findViewById(R.id.image);
        holder.content = (TextView) convertView.findViewById(R.id.content);
        convertView.setTag(holder);
    }

    @Override
    protected void holderView(View convertView, DocBean bean, int position) {
        Holder holder = (Holder) convertView.getTag();
        holder.content.setText(bean.title);
        bindData(bean.cover, holder.imageView, null);
    }

    class Holder {
        public TextView content;
        public ImageView imageView;
    }

    void bindData(String url, ImageView image, String fileurl) {
        String a;
        if (isNewest){
            a = url;
        }else {
            a = "http://cover1.bookday.cn/" + url;
        }
        if (a == null || a.length() == 0)
            a = "http://";
        Picasso.with(mContext)
                .load(a)
                .placeholder(R.drawable.default_image)
                .fit()
                .centerInside()
                .into(image);

    }
}
