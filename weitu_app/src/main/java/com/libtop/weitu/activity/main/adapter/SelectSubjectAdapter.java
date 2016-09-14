package com.libtop.weitu.activity.main.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.main.dto.SelectSubBean;
import com.libtop.weitu.activity.search.dto.BookDto;
import com.libtop.weitu.activity.user.dto.CollectBean;
import com.libtop.weitu.test.Subject;
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
public class SelectSubjectAdapter extends CommonAdapter<Subject> {

    public SelectSubjectAdapter(Context context, List<Subject> data) {
        super(context, R.layout.item_list_select_subject, data);
    }

    @Override
    public void convert(ViewHolderHelper helper, Subject object, int position) {
        ImageView fileImage = helper.getView(R.id.subject_image);
        Picasso.with(context).load(object.cover).placeholder(R.drawable.default_image).into(fileImage);
        helper.setText(R.id.subject_title, object.name);
        helper.setChecked(R.id.checkBox, datas.get(position).ischecked);
    }
    public void setData(List<Subject> data){
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
        List<Subject> list = new ArrayList<>();
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
            subIds[i] = list.get(i).sid;
        }
        return subIds;
    }

}
