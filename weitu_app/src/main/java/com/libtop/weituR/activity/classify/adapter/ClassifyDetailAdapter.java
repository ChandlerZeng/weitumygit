package com.libtop.weituR.activity.classify.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.classify.ClassifyDetailActivity;
import com.libtop.weituR.activity.classify.bean.ClassifyResultBean;
import com.libtop.weituR.utils.ContantsUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by LianTu on 2016/7/20.
 */
public class ClassifyDetailAdapter extends BaseAdapter{
    private LayoutInflater mInflater;
    private Context context;
    private List<ClassifyResultBean> mlist;

    public ClassifyDetailAdapter(Context context, List<ClassifyResultBean> list) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mlist = list;
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void setData(List<ClassifyResultBean> list) {
        this.mlist = list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder1 = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_list_result3, null);
            holder1 = new ViewHolder();
            holder1.iconCover = (ImageView) convertView.findViewById(R.id.see);
            holder1.iconCover2 = (ImageView) convertView.findViewById(R.id.see2);
            holder1.titleText = (TextView) convertView.findViewById(R.id.doc_title);
            holder1.uploaderText = (TextView) convertView.findViewById(R.id.doc_author);
            holder1.timeText = (TextView) convertView.findViewById(R.id.doc_time);
            holder1.imageText = (TextView) convertView.findViewById(R.id.doc_size);
            holder1.fraOne = (View) convertView.findViewById(R.id.fra_one);
            holder1.fraTwo = (View) convertView.findViewById(R.id.fra_two);
            holder1.introductionText = (TextView) convertView.findViewById(R.id.doc_introduction);
            holder1.tvTag = (TextView) convertView.findViewById(R.id.tv_tag);
            convertView.setTag(holder1);
        } else {
            holder1 = (ViewHolder) convertView.getTag();
        }

        String typeF = mlist.get(position).entityType;
        if (typeF.equals(ClassifyDetailActivity.BOOK )) {
            holder1.fraOne.setVisibility(View.GONE);
            holder1.fraTwo.setVisibility(View.VISIBLE);
            holder1.introductionText.setVisibility(View.VISIBLE);
        } else {
            holder1.fraOne.setVisibility(View.VISIBLE);
            holder1.fraTwo.setVisibility(View.GONE);
            holder1.introductionText.setVisibility(View.GONE);
        }

        ClassifyResultBean classifyResultBean = mlist.get(position);
        if (!TextUtils.isEmpty(classifyResultBean.categoriesName1)){
            holder1.tvTag.setText(classifyResultBean.categoriesName1);
        }
        if (!TextUtils.isEmpty(classifyResultBean.categoriesName2)){
            holder1.tvTag.setText(classifyResultBean.categoriesName1+"/"+classifyResultBean.categoriesName2);
        }

        holder1.titleText.setText(classifyResultBean.title);
        holder1.uploaderText.setText("上传者:" + classifyResultBean.uploadUsername);
        holder1.introductionText.setText(classifyResultBean.introduction);
        //  checkView(holder1.checkMark, classifyResultBean.ischecked);
        if (typeF.equals(ClassifyDetailActivity.DOC)) {
//            holder1.iconCover.setImageResource(DocType.getDocType(classifyResultBean.title));
            String a = ContantsUtil.IMGHOST2+"/" + classifyResultBean.cover;
            Picasso.with(context).load(a)
                    .error(R.drawable.default_image)
                    .placeholder(R.drawable.default_image)
                    .fit()
                    .into(holder1.iconCover);
        } else {
            String a = ContantsUtil.getCoverUrlLittle(classifyResultBean.id);
            Picasso.with(context)
                    .load(a)
                    .error(R.drawable.default_image)
                    .placeholder(R.drawable.default_image)
                    .fit()
                    .into(holder1.iconCover);
        }

        return convertView;
    }

    protected class ViewHolder {
        ImageView iconCover, iconCover2;
        TextView titleText,tvTag, uploaderText, timeText, imageText, introductionText;
        View  fraOne, fraTwo;
    }

    public void setNewData(List<ClassifyResultBean> list) {
        this.mlist = list;
        notifyDataSetChanged();
    }

}
