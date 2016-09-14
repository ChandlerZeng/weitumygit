package com.libtop.weitu.activity.main.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.main.dto.DisplayDto;
import com.libtop.weitu.activity.main.dto.DocBean;
import com.libtop.weitu.test.Subject;
import com.libtop.weitu.test.SubjectResource;
import com.libtop.weitu.utils.ContantsUtil;
import com.libtop.weitu.viewadapter.CommonAdapter;
import com.libtop.weitu.viewadapter.ViewHolderHelper;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by Zeng on 2016/9/7.
 */
public class MoreSubjectAdapter extends CommonAdapter<Subject>
{

    public MoreSubjectAdapter(Context context, List<Subject> datas) {
        super(context, R.layout.more_subject_adapter, datas);
    }

    @Override
    public void convert(ViewHolderHelper helper, Subject object, int position) {
        ImageView imageView = helper.getView(R.id.image);
        Picasso.with(context).load(object.cover).placeholder(R.drawable.default_image).error(R.drawable.default_image).centerInside().fit().into(imageView);
        helper.setText(R.id.title, object.name);
    }
    public void setData(List<Subject> list){
        this.datas = list ;
        notifyDataSetChanged();
    }
}
