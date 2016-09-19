package com.libtop.weitu.activity.main.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.libtop.weitu.R;
import com.libtop.weitu.test.Subject;
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
        super(context, R.layout.item_fragment_subject, datas);
    }

    @Override
    public void convert(ViewHolderHelper helper, Subject object, int position) {
        ImageView imageView = helper.getView(R.id.img_item_subject);
        ImageView imageNew = helper.getView(R.id.img_item_subject_new);
        Picasso.with(context).load(object.cover).placeholder(R.drawable.default_image).error(R.drawable.default_image).centerInside().fit().into(imageView);
        helper.setText(R.id.tv_item_subject, object.name);
        if(object.has_new==0){
            imageNew.setVisibility(View.GONE);
        }else if(object.has_new == 1) {
            imageNew.setVisibility(View.VISIBLE);
        }
    }
    public void setData(List<Subject> list){
        this.datas = list ;
        notifyDataSetChanged();
    }
}
