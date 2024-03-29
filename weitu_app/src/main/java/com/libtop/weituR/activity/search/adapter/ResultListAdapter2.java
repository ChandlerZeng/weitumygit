package com.libtop.weituR.activity.search.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.search.dto.SearchResult;
import com.libtop.weituR.base.impl.ImgAdapter;
import com.libtop.weituR.utils.selector.MultiImageSelectorFragment;
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
public class ResultListAdapter2 extends ImgAdapter {
//    private ImageOptions xOptions;
    //0文档 1图片
    private int type = 0;
    private Context context;
    private String urlString;

    public ResultListAdapter2(Context context, List<?> data, int type) {
        super(context, data, R.layout.item_list_result3);
        this.type = type;
        this.context = context;
//        xOptions = new ImageOptions.Builder()
//                .setFailureDrawableId(R.drawable.content_bg)
//                .setLoadingDrawableId(R.drawable.content_bg)
//                .setUseMemCache(true)
//                .build();
    }

    @Override
    protected void newView(View convertView) {
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
    protected void holderView(View convertView, Object itemObject, int position) {
        ViewHolder holder = (ViewHolder) convertView.getTag();
        SearchResult data = (SearchResult) itemObject;
        holder.titleText.setText(data.title);
        if (!TextUtils.isEmpty(data.categoriesName1)){
            holder.tvTag.setText(data.categoriesName1);
        }
        if (!TextUtils.isEmpty(data.categoriesName2)){
            holder.tvTag.setText(data.categoriesName1+"/"+data.categoriesName2);
        }
        holder.uploaderText.setText("上传者:" + data.uploadUsername);
        if (type == 0){
            if (TextUtils.isEmpty(data.cover))
                data.cover = "http://";
            Picasso.with(context)
                    .load(data.cover)
                    .placeholder(R.drawable.pdf)
                    .error(R.drawable.pdf)
                    .fit()
                    .into(holder.iconCover);
        } else{
            getImage(holder.iconCover, data.cover);
        }
        // holder.iconCover.setBackgroundResource(DocType.getDocType(data.title));
        // x.image().bind(holder.iconCover, ContantsUtil.getCoverUrl(data.id), xOptions);
//        ImageLoader.getInstance().displayImage(
//                ContantsUtil.getCoverUrl(data.id), holder.iconCover, mOptions);

//        holder.iconCover.setImageURI(Uri.parse(ContantsUtil.getCoverUrl(data.id)));
    }

    protected class ViewHolder {
        //        SimpleDraweeView iconCover;
        ImageView iconCover;
        TextView titleText, uploaderText, timeText, imageText,tvTag;
    }


    private void getImage(ImageView image, String url) {
        String a = url;
        Picasso.with(context)
                .load(a)
                .placeholder(R.drawable.default_image)
                .tag(MultiImageSelectorFragment.TAG)
                .fit()
                .centerCrop()
                .into(image);
    }
}
