package com.libtop.weitu.activity.classify.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.user.dto.CollectBean;
import com.libtop.weitu.utils.ContextUtil;
import com.libtop.weitu.utils.DateUtil;
import com.libtop.weitu.utils.ImageLoaderUtil;
import com.libtop.weitu.viewadapter.ViewHolderHelper;

import java.util.List;


/**
 * Created by Zeng on 2016/9/10.
 */
public class ClassifySubDetailAdapter extends BaseAdapter
{
    private LayoutInflater mInflater;
    private Context context;
    private List<CollectBean> mlist;


    public ClassifySubDetailAdapter(Context context, List<CollectBean> list)
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


    public void setData(List<CollectBean> list)
    {
        this.mlist = list;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        CollectBean result = mlist.get(position);

        if (result.type == ContextUtil.ENTITY_TYPE_SUBJECT){
            return getSubjectView(position, convertView, parent, result);
        }else {
            return getResourceView(position, convertView, parent, result);
        }
    }


    private View getSubjectView(int position,View convertView, ViewGroup parent, CollectBean collectBean) {
        ViewHolderHelper helper = ViewHolderHelper.get(context, convertView, parent, R.layout.item_list_rank_subject, position);

        helper.setText(R.id.subject_file_title, collectBean.target.getTitle());
        helper.setText(R.id.subject_file_desc, collectBean.target.getIntroduction());
//        helper.setText(R.id.subject_file_member, "关注：" + collectBean.target.get.count_follow);

        ImageView coverIv = helper.getView(R.id.subject_file_image);
        ImageLoaderUtil.loadImage(context, coverIv, collectBean.target.getCover());

        return helper.getConvertView();
    }


    private View getResourceView(int position,View convertView, ViewGroup parent, CollectBean collectBean)
    {
        ViewHolderHelper helper = null;
        if(collectBean.type == ContextUtil.ENTITY_TYPE_BOOK){
            helper = ViewHolderHelper.get(context, convertView, parent, R.layout.item_list_rank_book, position);
            helper.setText(R.id.subject_file_desc, collectBean.target.getIntroduction());
            helper.setText(R.id.subject_file_author, "作者：" + collectBean.target.getUploadUsername());
            String date = DateUtil.parseToDate(collectBean.target.getTimeline());
            helper.setText(R.id.subject_file_publisher, collectBean.target.getPublisher());

        }else {
            helper = ViewHolderHelper.get(context, convertView, parent, R.layout.item_list_rank_other, position);
            helper.setText(R.id.subject_file_uploader,"上传：" + collectBean.target.getUploadUsername());
            String date = DateUtil.parseToDate(collectBean.target.getTimeline());
            helper.setText(R.id.subject_file_date,"时间："+ date);
        }
        helper.setText(R.id.subject_file_title, collectBean.target.getTitle());
        ImageView coverIv = helper.getView(R.id.subject_file_image);
        ImageLoaderUtil.build(context, collectBean.target.getCover()).centerInside().into(coverIv);

        return helper.getConvertView();
    }


    public void setNewData(List<CollectBean> list)
    {
        this.mlist = list;
        notifyDataSetChanged();
    }

}
