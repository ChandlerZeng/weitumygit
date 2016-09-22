package com.libtop.weitu.activity.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.libtop.weitu.R;
import com.libtop.weitu.test.Resource;
import com.libtop.weitu.utils.DateUtil;
import com.libtop.weitu.viewadapter.ViewHolderHelper;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by Zeng on 2016/9/10.
 */
public class ResourceFileAdapter extends BaseAdapter
{
    private LayoutInflater mInflater;
    private Context context;
    private List<Resource> mlist;


    public ResourceFileAdapter(Context context, List<Resource> list)
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


    public void setData(List<Resource> list)
    {
        this.mlist = list;
        notifyDataSetChanged();
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        return getResourceView(position,convertView,parent,mlist.get(position));
    }

    private View getResourceView(int position,View convertView, ViewGroup parent, Resource resource)
    {
        ViewHolderHelper helper = null;
        if(resource.type==3){
            helper = ViewHolderHelper.get(context, convertView, parent, R.layout.item_list_rank_book, position);
            helper.setText(R.id.subject_file_desc, resource.intro);
            helper.setText(R.id.subject_file_author, "作者：" + resource.uploader_name);
            String date = DateUtil.parseToDate(resource.t_upload);
            helper.setText(R.id.subject_file_publisher, "出版社：" + "新华出版社");

        }else {
            helper = ViewHolderHelper.get(context, convertView, parent, R.layout.item_list_rank_other, position);
            helper.setText(R.id.subject_file_uploader,"上传：" + resource.uploader_name);
            String date = DateUtil.parseToDate(resource.t_upload);
            helper.setText(R.id.subject_file_date,"时间："+ date);
        }
        helper.setText(R.id.subject_file_title, resource.name);
        ImageView coverIv = helper.getView(R.id.subject_file_image);
        Picasso.with(context).load(resource.cover).error(R.drawable.default_image).placeholder(R.drawable.default_image).fit().centerInside().into(coverIv);

        return helper.getConvertView();
    }
}
