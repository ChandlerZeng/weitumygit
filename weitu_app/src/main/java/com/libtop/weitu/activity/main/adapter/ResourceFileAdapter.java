package com.libtop.weitu.activity.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.main.dto.ResourceBean;
import com.libtop.weitu.test.Resource;
import com.libtop.weitu.utils.ContantsUtil;
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
    private List<ResourceBean> mlist;


    public ResourceFileAdapter(Context context, List<ResourceBean> list)
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


    public void setData(List<ResourceBean> list)
    {
        this.mlist = list;
        notifyDataSetChanged();
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        return getResourceView(position,convertView,parent,mlist.get(position));
    }

    private View getResourceView(int position,View convertView, ViewGroup parent, ResourceBean resource)
    {
        ViewHolderHelper helper = null;
        if(resource.getEntityType().equals("book")){
            helper = ViewHolderHelper.get(context, convertView, parent, R.layout.item_list_rank_book, position);
            helper.setText(R.id.subject_file_desc, resource.getIntroduction());
            helper.setText(R.id.subject_file_author, "作者：" + resource.getAuthor());
            helper.setText(R.id.subject_file_publisher, resource.getPublisher());

        }else {
            helper = ViewHolderHelper.get(context, convertView, parent, R.layout.item_list_rank_other, position);
            helper.setText(R.id.subject_file_uploader,"上传：" + resource.getUploadUsername());
            String date = DateUtil.parseToDate(resource.timeline);
            helper.setText(R.id.subject_file_date,"时间："+ date);
        }
        helper.setText(R.id.subject_file_title, resource.getTitle());
        ImageView coverIv = helper.getView(R.id.subject_file_image);
        Picasso.with(context).load(ContantsUtil.IMGHOST2+resource.getCover()).error(R.drawable.default_image).placeholder(R.drawable.default_image).fit().centerInside().into(coverIv);

        return helper.getConvertView();
    }
}
