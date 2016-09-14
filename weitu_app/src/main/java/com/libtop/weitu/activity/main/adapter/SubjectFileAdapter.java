package com.libtop.weitu.activity.main.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.search.dto.BookDto;
import com.libtop.weitu.base.BaseAdapter;
import com.libtop.weitu.test.Resource;
import com.libtop.weitu.utils.CheckUtil;
import com.libtop.weitu.utils.ContantsUtil;
import com.libtop.weitu.utils.DateUtil;
import com.libtop.weitu.viewadapter.CommonAdapter;
import com.libtop.weitu.viewadapter.ViewHolderHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Zeng on 2016/9/6.
 */
public class SubjectFileAdapter extends CommonAdapter<Resource> {

    public SubjectFileAdapter(Context context, List<Resource> data) {
        super(context, R.layout.item_list_subject_file, data);
    }

    @Override
    public void convert(ViewHolderHelper helper, Resource object, int position) {
        ImageView fileImage = helper.getView(R.id.subject_file_image);
        Picasso.with(context).load(object.cover).placeholder(R.drawable.default_image).into(fileImage);
        helper.setText(R.id.subject_file_title,object.name);
        if(!CheckUtil.isNull(object.uploader_name)){
            helper.setText(R.id.subject_file_publisher,"上传："+object.uploader_name);
        }
        if(!CheckUtil.isNull(object.t_upload)){
            helper.setText(R.id.subject_file_date, DateUtil.parseToStringWithoutSS(object.t_upload));
        }
    }
    public void setData(List<Resource> data){
        datas = data;
        notifyDataSetChanged();
    }

}
