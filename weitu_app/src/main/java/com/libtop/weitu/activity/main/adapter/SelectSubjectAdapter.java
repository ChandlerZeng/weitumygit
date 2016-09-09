package com.libtop.weitu.activity.main.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.main.dto.SelectSubBean;
import com.libtop.weitu.activity.search.dto.BookDto;
import com.libtop.weitu.activity.user.dto.CollectBean;
import com.libtop.weitu.utils.CheckUtil;
import com.libtop.weitu.utils.ContantsUtil;
import com.libtop.weitu.viewadapter.CommonAdapter;
import com.libtop.weitu.viewadapter.ViewHolderHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import io.vov.vitamio.utils.Log;

/**
 * Created by Zeng on 2016/9/8.
 */
public class SelectSubjectAdapter extends CommonAdapter<CollectBean> {

    public SelectSubjectAdapter(Context context, List<CollectBean> data) {
        super(context, R.layout.item_list_select_subject, data);
    }

    @Override
    public void convert(ViewHolderHelper helper, CollectBean object, int position) {
        ImageView fileImage = helper.getView(R.id.subject_image);
        if(object.favor.type==5){
            Picasso.with(context).load(ContantsUtil.IMG_BASE + object.target.cover).placeholder(R.drawable.default_image).into(fileImage);
        } else {
            Picasso.with(context).load(object.target.cover).placeholder(R.drawable.default_image).into(fileImage);
        }
        helper.setText(R.id.subject_title, object.target.title);
        helper.setChecked(R.id.checkBox, datas.get(position).target.ischecked);
    }
    public void setData(List<CollectBean> data){
        datas = data;
        notifyDataSetChanged();
    }
    public void setCheckStatus(int position )
    {
            if (datas.get(position).target.ischecked == true)
            {
                datas.get(position).target.ischecked = false;
            }
            else
            {
                datas.get(position).target.ischecked = true;
            }
        notifyDataSetChanged();
    }
    public String[] selectSubId()
    {
        List<CollectBean> list = new ArrayList<CollectBean>();
        for (int i = 0; i < datas.size(); i++)
        {
            if (datas.get(i).target.ischecked)
            {
                list.add(datas.get(i));
            }
        }
        String[] subIds = new String[list.size()];
        for (int i = 0; i < list.size(); i++)
        {
            subIds[i] = list.get(i).favor.id;
        }
        return subIds;
    }

}
