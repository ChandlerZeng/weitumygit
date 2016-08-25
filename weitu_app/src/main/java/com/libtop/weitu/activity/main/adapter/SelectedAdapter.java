package com.libtop.weitu.activity.main.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.main.dto.DisplayDto;
import com.libtop.weitu.base.BaseAdapter;
import com.libtop.weitu.utils.DateUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * <p>
 * Title: SelectedAdapter.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * CreateTime：16/6/12
 * </p>
 *
 * @author 作者名
 * @version common v1.0
 */
public class SelectedAdapter extends BaseAdapter<DisplayDto> {

    private int type;
    private String mMethod;

    public SelectedAdapter(Context context, List<DisplayDto> data, int resourceId, String method) {
        super(context, data, R.layout.selected_layout);
        this.mMethod = method;
        if (method.equals("mediaAlbum.list"))
            type = 1;
        else if (method.equals("imageAlbum.list"))
            type = 2;
    }

    @Override
    protected void newView(View convertView) {
        Holder holder = new Holder();
        holder.title = (TextView) convertView.findViewById(R.id.title);
        holder.time = (TextView) convertView.findViewById(R.id.time);
//        holder.countLook = (TextView) convertView.findViewById(R.id.count_look);
        holder.imageText = (TextView) convertView.findViewById(R.id.image_text);
        holder.label = (TextView) convertView.findViewById(R.id.label);
        holder.Content = (TextView) convertView.findViewById(R.id.content);
        holder.imageView = (ImageView) convertView.findViewById(R.id.image_title);
        holder.textImage = (ImageView) convertView.findViewById(R.id.text_image);
        convertView.setTag(holder);
    }

    @Override
    protected void holderView(View convertView, DisplayDto displayDto, int position) {
        Holder holder = (Holder) convertView.getTag();
        holder.title.setText(displayDto.title);
//        holder.countLook.setText(displayDto.categoriesName1 + "万人观看");
        //holder.imageText.setText(DisplayDto.stateString);
        if (!TextUtils.isEmpty(displayDto.categoriesName1)){
            holder.label.setText(displayDto.categoriesName1);
        }
        if (!TextUtils.isEmpty(displayDto.categoriesName2)){
            holder.label.setText(displayDto.categoriesName1+"/"+displayDto.categoriesName2);
        }
//        if (!TextUtils.isEmpty(displayDto.categoriesName1)||!TextUtils.isEmpty(displayDto.categoriesName2))

        holder.Content.setText(displayDto.introduction);

        if(!TextUtils.isEmpty(displayDto.cover)){
            Picasso.with(mContext)
                    .load(displayDto.cover)
                    .placeholder(R.drawable.default_image)
                    .error(R.drawable.default_image)
                    .fit()
                    .into(holder.imageView);
        }

        if (mMethod.equals("mediaAlbum.list")) {
            if (type == 1)
                holder.textImage.setImageResource(R.drawable.delicate_video);
            else
                holder.textImage.setImageResource(R.drawable.delicate_music);
        } else if (mMethod.equals("document.list")) {
            holder.textImage.setImageResource(R.drawable.delicate_doc);
        } else if (mMethod.equals("imageAlbum.list")) {
            holder.textImage.setImageResource(R.drawable.delicate_img);
        }
//        long duration_temp = displayDto.timeline;
//        String str;
//        if (duration_temp == 0) {
//            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
//            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
//            str = formatter.format(curDate);
//        } else {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
//            str = sdf.format(new Date(duration_temp));
//        }
        holder.time.setText(DateUtil.parseToDate(displayDto.timeline));
    }

    class Holder {
        public TextView title, time, imageText, label, Content;
        public ImageView imageView, textImage;
    }

}
