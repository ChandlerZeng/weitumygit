package com.libtop.weitu.activity.classify.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.classify.ClassifyDetailActivity;
import com.libtop.weitu.activity.classify.bean.ClassifyResultBean;
import com.libtop.weitu.test.CategoryResult;
import com.libtop.weitu.test.Resource;
import com.libtop.weitu.test.Subject;
import com.libtop.weitu.test.SubjectResource;
import com.libtop.weitu.utils.ContantsUtil;
import com.libtop.weitu.utils.DateUtil;
import com.libtop.weitu.viewadapter.ViewHolderHelper;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by LianTu on 2016/7/20.
 */
public class ClassifySubDetailAdapter extends BaseAdapter
{
    private LayoutInflater mInflater;
    private Context context;
    private List<CategoryResult> mlist;


    public ClassifySubDetailAdapter(Context context, List<CategoryResult> list)
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


    public void setData(List<CategoryResult> list)
    {
        this.mlist = list;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        CategoryResult result = mlist.get(position);
        if (result instanceof Subject)
        {
            return getSubjectView(position, convertView, parent, (Subject)result);
        }
        else if (result instanceof Resource)
        {
            return getResourceView(position, convertView, parent, (Resource)result);
        }

        return null;
    }


    private View getSubjectView(int position,View convertView, ViewGroup parent, Subject subject) {
        ViewHolderHelper helper = ViewHolderHelper.get(context, convertView, parent, R.layout.item_list_rank_subject, position);

        helper.setText(R.id.subject_file_title, subject.name);
        helper.setText(R.id.subject_file_desc, subject.intro);
        helper.setText(R.id.subject_file_member, "成员：" + subject.count_follow);

        ImageView coverIv = helper.getView(R.id.subject_file_image);
        Picasso.with(context).load(subject.cover).error(R.drawable.default_image).placeholder(R.drawable.default_image).fit().into(coverIv);

        return helper.getConvertView();
    }


    private View getResourceView(int position,View convertView, ViewGroup parent, Resource resource)
    {
        ViewHolderHelper helper = ViewHolderHelper.get(context, convertView, parent, R.layout.item_list_rank_other, position);

        helper.setText(R.id.subject_file_title, resource.name);
        helper.setText(R.id.subject_file_uploader, resource.intro);
        String date = DateUtil.parseToDate(resource.t_upload);
        helper.setText(R.id.subject_file_date, date);


        ImageView coverIv = helper.getView(R.id.subject_file_image);
        Picasso.with(context).load(resource.cover).error(R.drawable.default_image).placeholder(R.drawable.default_image).fit().into(coverIv);

        return helper.getConvertView();
    }


    public void setNewData(List<CategoryResult> list)
    {
        this.mlist = list;
        notifyDataSetChanged();
    }

}
