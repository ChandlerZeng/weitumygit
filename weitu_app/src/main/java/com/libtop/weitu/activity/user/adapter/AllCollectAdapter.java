package com.libtop.weitu.activity.user.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.main.clickHistory.ClickHistoryActivity;
import com.libtop.weitu.activity.user.dto.CollectBean;
import com.libtop.weitu.utils.ContantsUtil;
import com.libtop.weitu.utils.DateUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.vov.vitamio.utils.Log;

/**
 * Created by LianTu on 2016/7/18.
 */
public class AllCollectAdapter extends BaseAdapter{
    private List<CollectBean> lists;
    private Context context;
    private LayoutInflater inflater;
    private final int MEDIA = 0,OTHER = 1;

    private boolean visable = false;
    private boolean allCheck = false;

    public AllCollectAdapter(Context context,List<CollectBean> lists){
        this.lists = lists;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemViewType(int position) {
        if (lists != null && position < lists.size()) {
            int  type = lists.get(position).favor.type;
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
        CollectBean collectBean = lists.get(position);
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
                    holder.view = (View) convertView.findViewById(R.id.check_layout);
                    holder.checkMark = (CheckBox) convertView.findViewById(R.id.checkmark);
                    convertView.setTag(holder);
                } else {
                    holder = (viewHolder1) convertView.getTag();
                }
                if (collectBean.favor.type == ClickHistoryActivity.BOOK){
                    Picasso.with(context).load(ContantsUtil.IMG_BASE + collectBean.target.cover)
                            .placeholder(R.drawable.default_image)
                            .error(R.drawable.default_image)
                            .centerInside()
                            .fit()
                            .into(holder.imageView);
                } else{
                    Picasso.with(context).load(collectBean.target.cover)
                            .placeholder(R.drawable.default_image)
                            .error(R.drawable.default_image)
                            .centerInside()
                            .fit()
                            .into(holder.imageView);
                }
                if (visable)
                    holder.view.setVisibility(View.VISIBLE);
                else
                    holder.view.setVisibility(View.GONE);
                holder.tvTitle.setText(collectBean.target.title);
                if (!TextUtils.isEmpty(collectBean.target.categoriesName1)){
                    holder.tvTag.setText(collectBean.target.categoriesName1);
                }else{
                    holder.tvTag.setText("暂无分类");
                }
                if (!TextUtils.isEmpty(collectBean.target.categoriesName2)){
                    holder.tvTag.setText(collectBean.target.categoriesName1+"/"+collectBean.target.categoriesName2);
                }else{
                    holder.tvTag.setText("暂无分类");
                }
//                holder.tvTag.setText("dsfdsafa");
                holder.checkMark.setChecked(allCheck);
                if (!TextUtils.isEmpty(collectBean.target.author))
                    holder.tvAuthor.setText(collectBean.target.author);
                else
                    holder.tvAuthor.setText("");
                if (!TextUtils.isEmpty(collectBean.target.publisher))
                    holder.tvPublisher.setText(collectBean.target.publisher);
                else
                    holder.tvPublisher.setText("");
                if (!TextUtils.isEmpty(collectBean.target.introduction))
                    holder.tvDesc.setText(collectBean.target.introduction);
                else
                    holder.tvDesc.setText("");
//                holder.tvAuthor.setText();
//                holder.tvPublisher.setText();
//                holder.tvDesc.setText();
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
                    holder.view = (View) convertView.findViewById(R.id.check_layout);
                    holder.checkMark = (CheckBox) convertView.findViewById(R.id.checkmark);
                    convertView.setTag(holder);
                } else {
                    holder = (viewHolder2) convertView.getTag();
                }
                if (visable)
                    holder.view.setVisibility(View.VISIBLE);
                else
                    holder.view.setVisibility(View.GONE);
                holder.checkMark.setChecked(allCheck);
                if (collectBean.favor.type == ClickHistoryActivity.DOC){
                    if (TextUtils.isEmpty(collectBean.target.cover))
                        collectBean.target.cover = "http://";
                    Picasso.with(context)
                            .load(collectBean.target.cover)
                            .placeholder(R.drawable.pdf)
                            .error(R.drawable.pdf)
                            .centerInside()
                            .fit()
                            .into(holder.imageView);
                }else{
                    Picasso.with(context)
//                        .load(ContantsUtil.getCoverUrl(collectBean.target.id))
                            .load(collectBean.target.cover)
                            .centerInside()
                            .fit()
                            .into(holder.imageView);
                }
                holder.tvTitle.setText(collectBean.target.title);
                if (!TextUtils.isEmpty(collectBean.target.categoriesName1)){
                    holder.tvTag.setText(collectBean.target.categoriesName1);
                }else{
                    holder.tvTag.setText("暂无分类");
                }
                if (!TextUtils.isEmpty(collectBean.target.categoriesName2)){
                    holder.tvTag.setText(collectBean.target.categoriesName1+"/"+collectBean.target.categoriesName2);
                }else{
                    holder.tvTag.setText("暂无分类");
                }
//                holder.tvTag.setText("dfsdafd");
                holder.tvUploader.setText("上传："+collectBean.target.uploadUsername);
                holder.tvTime.setText("时间："+DateUtil.parseToDate(collectBean.target.timeline));
                break;
            }
        }
        return convertView;
    }

    public void setVisableView() {
        if (visable)
            visable = false;
        else
            visable = true;
        notifyDataSetChanged();
    }

    public void setAllView() {
        if (allCheck)
            allCheck = false;
        else
            allCheck = true;
        for (int i = 0; i < lists.size(); i++)
            lists.get(i).target.ischecked = allCheck;
        notifyDataSetChanged();
    }

    public String[] cleanView() {
        List<CollectBean> list = new ArrayList<CollectBean>();
        for (int i = 0; i < lists.size(); i++) {
            if (lists.get(i).target.ischecked) {
                Log.e("" + lists.get(i).target.ischecked);
                list.add(lists.get(i));
            }
        }
        String[] str = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            str[i] = list.get(i).favor.id;
        }
        allCheck = false;
        return str;
    }

    public void updateList(List<CollectBean> lists){
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
        View view;
        CheckBox checkMark;
    }

    class viewHolder2{
        ImageView imageView;
        TextView tvTitle;
        TextView tvTag;
        TextView tvUploader;
        TextView tvTime;
        View view;
        CheckBox checkMark;
    }

}
