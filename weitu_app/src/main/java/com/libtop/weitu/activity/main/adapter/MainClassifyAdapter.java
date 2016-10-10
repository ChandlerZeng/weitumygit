package com.libtop.weitu.activity.main.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.classify.bean.ClassifyBean;
import com.libtop.weitu.viewadapter.CommonAdapter;
import com.libtop.weitu.viewadapter.ViewHolderHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by zeng on 2016/10/9.
 */

public class MainClassifyAdapter extends CommonAdapter<ClassifyBean>{

    public MainClassifyAdapter(Context context, List<ClassifyBean> list) {
        super(context, R.layout.item_main_classify,list);
    }

    @Override
    public void convert(ViewHolderHelper helper, ClassifyBean object, int position) {
        ImageView imageView = helper.getView(R.id.main_classify_image);
        if(datas!=null && position==datas.size()-1){
            helper.setText(R.id.main_classify_text,"全部分类");
            imageView.setImageResource(R.drawable.add_pic);
        }else {
            if(object.name!=null){
                helper.setText(R.id.main_classify_text,object.name);
            }
            Picasso.with(context).load(object.id).placeholder(R.drawable.default_error).fit().centerInside().into(imageView);
        }
    }

    public void setData(List<ClassifyBean> data){
        datas = data;
        notifyDataSetChanged();
    }
}
