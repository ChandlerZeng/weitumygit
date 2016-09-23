package com.libtop.weitu.activity.main.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.main.dto.SubjectBean;
import com.libtop.weitu.test.Subject;
import com.libtop.weitu.viewadapter.CommonAdapter;
import com.libtop.weitu.viewadapter.ViewHolderHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zeng on 2016/9/8.
 */
public class SelectSubjectAdapterNew extends CommonAdapter<SubjectBean> {

    public SelectSubjectAdapterNew(Context context, List<SubjectBean> data) {
        super(context, R.layout.item_list_select_subject, data);
    }

    @Override
    public void convert(ViewHolderHelper helper, SubjectBean object, int position) {
        ImageView fileImage = helper.getView(R.id.subject_image);
        Picasso.with(context).load(object.cover).placeholder(R.drawable.default_image).into(fileImage);
        helper.setText(R.id.subject_title, object.title);
        helper.setChecked(R.id.checkBox, datas.get(position).ischecked);
    }
    public void setData(List<SubjectBean> data){
        datas = data;
        notifyDataSetChanged();
    }
    public void setCheckStatus(int position )
    {
            if (datas.get(position).ischecked == true)
            {
                datas.get(position).ischecked = false;
            }
            else
            {
                datas.get(position).ischecked = true;
            }
        notifyDataSetChanged();
    }
    public String[] selectSubId()
    {
        List<SubjectBean> list = new ArrayList<>();
        for (int i = 0; i < datas.size(); i++)
        {
            if (datas.get(i).ischecked)
            {
                list.add(datas.get(i));
            }
        }
        String[] subIds = new String[list.size()];
        for (int i = 0; i < list.size(); i++)
        {
            subIds[i] = list.get(i).id;
        }
        return subIds;
    }

}
