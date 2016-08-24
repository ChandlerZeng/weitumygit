package com.libtop.weituR.activity.search.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.search.AllFragment;
import com.libtop.weituR.activity.search.dto.AllDto;
import com.libtop.weituR.utils.ContantsUtil;
import com.libtop.weituR.utils.DateUtil;
import com.libtop.weituR.utils.selector.MultiImageSelectorFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by LianTu on 2016/6/27.
 */
public class AllListAdapter extends BaseAdapter {

    private List<AllDto> lists;
    private Context context;
    private LayoutInflater inflater;
    private final int MEDIA = 0,OTHER = 1;

    public AllListAdapter(Context context,List<AllDto> lists){
        this.lists = lists;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        if (lists != null && position < lists.size()) {
            String type = lists.get(position).entityType;
            if (!TextUtils.isEmpty(type) ) {
                if (type.equals(AllFragment.VIDEO)||type.equals(AllFragment.AUDIO)) {
                    return MEDIA;
                } else {
                    return OTHER;

                }
            }
        }
        return super.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        AllDto allDto = lists.get(position);
        switch (type) {
            case OTHER: {
                viewHolder1 holder1 = null;
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.item_list_book, null);
                    holder1 = new viewHolder1();
                    holder1.imageView = (ImageView) convertView.findViewById(R.id.icon);
                    holder1.tvTitle = (TextView) convertView.findViewById(R.id.title);
                    holder1.tvTag = (TextView) convertView.findViewById(R.id.tv_tag);
                    holder1.tvAuthor = (TextView) convertView.findViewById(R.id.author);
                    holder1.tvPublisher = (TextView) convertView.findViewById(R.id.publisher);
                    holder1.tvDesc = (TextView) convertView.findViewById(R.id.tv_desc);
                    convertView.setTag(holder1);
                } else {
                    holder1 = (viewHolder1) convertView.getTag();
                }
                if (allDto.entityType.equals(AllFragment.DOC)){
                    if (TextUtils.isEmpty(allDto.cover))
                        allDto.cover = "http://";
                    Picasso.with(context)
                            .load(allDto.cover)
                            .placeholder(R.drawable.pdf)
                            .error(R.drawable.pdf)
                            .centerInside()
                            .fit()
                            .into(holder1.imageView);
                }else if (allDto.entityType.equals(AllFragment.BOOK)){
                    Picasso.with(context).load(ContantsUtil.IMG_BASE + allDto.cover)
                            .fit()
                            .centerInside()
                            .error(R.drawable.default_image)
                            .into(holder1.imageView);
                } else{
                    Picasso.with(context).load(ContantsUtil.getCoverUrl(allDto.id))
                            .fit()
                            .centerInside()
                            .error(R.drawable.default_image)
                            .into(holder1.imageView);
                }
                holder1.tvTitle.setText(allDto.title);
                if (!TextUtils.isEmpty(allDto.categoriesName1)){
                    holder1.tvTag.setText(allDto.categoriesName1);
                }
                if (!TextUtils.isEmpty(allDto.categoriesName2)){
                    holder1.tvTag.setText(allDto.categoriesName1+"/"+allDto.categoriesName2);
                }
//                holder1.tvTag.setText("dsfdsafa");
                if (!TextUtils.isEmpty(allDto.author))
                    holder1.tvAuthor.setText(allDto.author);
                if (!TextUtils.isEmpty(allDto.publisher))
                    holder1.tvPublisher.setText(allDto.publisher);
                if (!TextUtils.isEmpty(allDto.introduction))
                    holder1.tvDesc.setText(allDto.introduction);
                break;
            }
            case MEDIA:{
                viewHolder2 holder2 = null;
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.item_list_other, null);
                    holder2 = new viewHolder2();
                    holder2.imageView = (ImageView) convertView.findViewById(R.id.icon);
                    holder2.tvTitle = (TextView) convertView.findViewById(R.id.title);
                    holder2.tvTag = (TextView) convertView.findViewById(R.id.tv_tag);
                    holder2.tvUploader = (TextView) convertView.findViewById(R.id.tv_uploader);
                    holder2.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
                    convertView.setTag(holder2);
                } else {
                    holder2 = (viewHolder2) convertView.getTag();
                }
                Picasso.with(context)
                        .load(ContantsUtil.getCoverUrl(allDto.id))
                        .placeholder(R.drawable.default_image)
                        .error(R.drawable.default_image)
                        .centerInside()
                        .fit()
                        .into(holder2.imageView);
                holder2.tvTitle.setText(allDto.title);
                if (!TextUtils.isEmpty(allDto.categoriesName1)){
                    holder2.tvTag.setText(allDto.categoriesName1);
                }
                if (!TextUtils.isEmpty(allDto.categoriesName2)){
                    holder2.tvTag.setText(allDto.categoriesName1+"/"+allDto.categoriesName2);
                }
//                holder2.tvTag.setText("dfsdafd");
                holder2.tvUploader.setText("上传："+allDto.uploadUsername);
                holder2.tvTime.setText("时间："+DateUtil.parseToDate(allDto.timeline));
                break;
            }
        }
        return convertView;
    }

    private void getImage(ImageView image, String url) {
        String a = "http://nt1.libtop.com/f"+url;
        Picasso.with(context)
                .load(a)
                .placeholder(R.drawable.default_image)
                .tag(MultiImageSelectorFragment.TAG)
                .fit()
                .centerInside()
                .into(image);
    }

    public void updateList(List<AllDto> lists){
        this.lists = lists;
        notifyDataSetChanged();
    }

    //各个布局的控件资源
    class viewHolder1{
        ImageView imageView;
        TextView tvTitle;
        TextView tvTag;
        TextView tvAuthor;
        TextView tvPublisher;
        TextView tvDesc;
        }

    class viewHolder2{
        ImageView imageView;
        TextView tvTitle;
        TextView tvTag;
        TextView tvUploader;
        TextView tvTime;
        }

}
