package com.libtop.weitu.activity.main.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.main.clickHistory.ClickHistoryActivity;
import com.libtop.weitu.activity.main.clickHistory.ResultBean;
import com.libtop.weitu.utils.ContantsUtil;
import com.libtop.weitu.utils.DateUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by LianTu on 2016/7/15.
 */
public class AllHistoryAdapter extends BaseAdapter{
    private List<ResultBean> lists;
    private Context context;
    private LayoutInflater inflater;
    private final int MEDIA = 0,OTHER = 1;

    public AllHistoryAdapter(Context context,List<ResultBean> lists){
        this.lists = lists;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        if (lists != null && position < lists.size()) {
            int  type = lists.get(position).type;
            if (type == ClickHistoryActivity.VIDEO||type == ClickHistoryActivity.AUDIO||type==ClickHistoryActivity.DOC) {
                return MEDIA;
            } else {
                return OTHER;

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
        ResultBean resultBean = lists.get(position);
        switch (type) {
            case OTHER: {
                viewHolder1 holder = null;
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.item_list_book, null);
                    holder = new viewHolder1();
                    holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
                    holder.tvTitle = (TextView) convertView.findViewById(R.id.title);
                    holder.tvTag = (TextView) convertView.findViewById(R.id.tv_tag);
                    holder.tvAuthor = (TextView) convertView.findViewById(R.id.author);
                    holder.tvPublisher = (TextView) convertView.findViewById(R.id.publisher);
                    holder.tvDesc = (TextView) convertView.findViewById(R.id.tv_desc);
                    convertView.setTag(holder);
                } else {
                    holder = (viewHolder1) convertView.getTag();
                }
                if (resultBean.type == ClickHistoryActivity.BOOK){
                    Picasso.with(context)
                            .load(ContantsUtil.IMG_BASE + resultBean.target.cover)
                            .placeholder(R.drawable.default_image)
                            .error(R.drawable.default_image)
                            .centerInside()
                            .fit()
                            .into(holder.imageView);
                } else{
                    Picasso.with(context)
                            .load(resultBean.target.cover)
                            .placeholder(R.drawable.default_image)
                            .error(R.drawable.default_image)
                            .centerInside()
                            .fit()
                            .into(holder.imageView);
                }
                holder.tvTitle.setText(resultBean.target.title);
                if (!TextUtils.isEmpty(resultBean.target.categoriesName1)){
                    holder.tvTag.setText(resultBean.target.categoriesName1);
                }else{
                    holder.tvTag.setText("暂无分类");
                }
                if (!TextUtils.isEmpty(resultBean.target.categoriesName2)){
                    holder.tvTag.setText(resultBean.target.categoriesName1+"/"+resultBean.target.categoriesName2);
                }else{
                    holder.tvTag.setText("暂无分类");
                }
                if (!TextUtils.isEmpty(resultBean.target.author))
                    holder.tvAuthor.setText(resultBean.target.author);
                else
                    holder.tvAuthor.setText("");
                if (!TextUtils.isEmpty(resultBean.target.publisher))
                    holder.tvPublisher.setText(resultBean.target.publisher);
                else
                    holder.tvPublisher.setText("");
                if (!TextUtils.isEmpty(resultBean.target.introduction))
                    holder.tvDesc.setText(resultBean.target.introduction);
                else
                    holder.tvDesc.setText("");
                break;
            }
            case MEDIA:{
                viewHolder2 holder = null;
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.item_list_other, null);
                    holder = new viewHolder2();
                    holder.imageView = (ImageView) convertView.findViewById(R.id.icon);
                    holder.tvTitle = (TextView) convertView.findViewById(R.id.title);
                    holder.tvTag = (TextView) convertView.findViewById(R.id.tv_tag);
                    holder.tvUploader = (TextView) convertView.findViewById(R.id.tv_uploader);
                    holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
                    convertView.setTag(holder);
                } else {
                    holder = (viewHolder2) convertView.getTag();
                }
                if (resultBean.type == ClickHistoryActivity.DOC){
                    Picasso.with(context)
                            .load(resultBean.target.cover)
                            .placeholder(R.drawable.pdf)
                            .error(R.drawable.pdf)
                            .centerInside()
                            .fit()
                            .into(holder.imageView);
                }else {
                    Picasso.with(context)
                            .load(resultBean.target.cover)
                            .centerInside()
                            .fit()
                            .into(holder.imageView);
                }

                holder.tvTitle.setText(resultBean.target.title);
                if (!TextUtils.isEmpty(resultBean.target.categoriesName1)){
                    holder.tvTag.setText(resultBean.target.categoriesName1);
                }else{
                    holder.tvTag.setText("暂无分类");
                }
                if (!TextUtils.isEmpty(resultBean.target.categoriesName2)){
                    holder.tvTag.setText(resultBean.target.categoriesName1+"/"+resultBean.target.categoriesName2);
                }else {
                    holder.tvTag.setText("暂无分类");
                }
                holder.tvUploader.setText("上传："+resultBean.target.uploadUsername);
                holder.tvTime.setText("时间："+DateUtil.parseToDate(resultBean.target.timeline));
                break;
            }
        }
        return convertView;
    }

    public void updateList(List<ResultBean> lists){
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
