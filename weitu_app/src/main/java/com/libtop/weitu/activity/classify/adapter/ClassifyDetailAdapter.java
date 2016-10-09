package com.libtop.weitu.activity.classify.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.classify.bean.ClassifyResultBean;
import com.libtop.weitu.utils.ContextUtil;
import com.libtop.weitu.utils.DateUtil;
import com.libtop.weitu.utils.ImageLoaderUtil;
import com.libtop.weitu.viewadapter.ViewHolderHelper;

import java.util.List;


/**
 * Created by Zeng on 2016/9/10.
 */
public class ClassifyDetailAdapter extends BaseAdapter
{
    private LayoutInflater mInflater;
    private Context context;
    private List<ClassifyResultBean> mlist;


    public ClassifyDetailAdapter(Context context, List<ClassifyResultBean> list)
    {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mlist = list;
    }


    @Override
    public int getCount()
    {
        return mlist.size();
    }


    @Override
    public Object getItem(int position)
    {
        return mlist.get(position);
    }


    @Override
    public long getItemId(int position)
    {
        return 0;
    }


    public void setData(List<ClassifyResultBean> list)
    {
        this.mlist = list;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ClassifyResultBean result = mlist.get(position);

        if (result.entityType == String.valueOf(ContextUtil.SUBJECT)){
            return getSubjectView(position, convertView, parent, result);
        }else {
            return getResourceView(position, convertView, parent, result);
        }
    }


    private View getSubjectView(int position,View convertView, ViewGroup parent, ClassifyResultBean classifyResultBean) {
        ViewHolderHelper helper = ViewHolderHelper.get(context, convertView, parent, R.layout.item_list_rank_subject, position);

        helper.setText(R.id.subject_file_title, classifyResultBean.title);
        helper.setText(R.id.subject_file_desc, classifyResultBean.introduction);
//        helper.setText(R.id.subject_file_member, "关注：" + collectBean.target.get.count_follow);

        ImageView coverIv = helper.getView(R.id.subject_file_image);
        ImageLoaderUtil.loadImage(context, coverIv, classifyResultBean.cover);

        return helper.getConvertView();
    }


    private View getResourceView(int position,View convertView, ViewGroup parent, ClassifyResultBean classifyResultBean)
    {
        ViewHolderHelper helper = null;
        if(classifyResultBean.entityType == String.valueOf(ContextUtil.BOOK)){
            helper = ViewHolderHelper.get(context, convertView, parent, R.layout.item_list_rank_book, position);
            helper.setText(R.id.subject_file_desc, classifyResultBean.introduction);
            helper.setText(R.id.subject_file_author, "作者：" + classifyResultBean.uploadUsername);
            String date = DateUtil.parseToDate(classifyResultBean.timeline);
            helper.setText(R.id.subject_file_publisher, date);

        }else {
            helper = ViewHolderHelper.get(context, convertView, parent, R.layout.item_list_rank_other, position);
            helper.setText(R.id.subject_file_uploader,"上传：" + classifyResultBean.uploadUsername);
            String date = DateUtil.parseToDate(classifyResultBean.timeline);
            helper.setText(R.id.subject_file_date,"时间："+ date);
        }
        helper.setText(R.id.subject_file_title, classifyResultBean.title);
        ImageView coverIv = helper.getView(R.id.subject_file_image);
        ImageLoaderUtil.build(context, classifyResultBean.cover).centerInside().into(coverIv);

        return helper.getConvertView();
    }


    public void setNewData(List<ClassifyResultBean> list)
    {
        this.mlist = list;
        notifyDataSetChanged();
    }

}
