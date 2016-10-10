package com.libtop.weitu.test.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.libtop.weitu.R;
import com.libtop.weitu.test.CategoryBean;
import com.libtop.weitu.viewadapter.CommonAdapter;
import com.libtop.weitu.viewadapter.ViewHolderHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by zeng on 2016/10/10.
 */

public class CategoryAdapter extends CommonAdapter<CategoryBean.Categories> {
    public CategoryAdapter(Context context, List<CategoryBean.Categories> datas) {
        super(context, R.layout.item_main_classify, datas);
    }

    @Override
    public void convert(ViewHolderHelper helper, CategoryBean.Categories object, int position) {
        ImageView imageView = helper.getView(R.id.main_classify_image);
        if(datas!=null && position==datas.size()-1){
            helper.setText(R.id.main_classify_text,"全部分类");
            imageView.setImageResource(R.drawable.add_pic);
        }else {
            if(object.name!=null){
                helper.setText(R.id.main_classify_text,object.name);
            }
            Picasso.with(context).load(object.cover).placeholder(R.drawable.default_error).fit().centerInside().into(imageView);
        }
    }

    public void setData(List<CategoryBean.Categories> data){
        datas = data;
        notifyDataSetChanged();
    }
}
