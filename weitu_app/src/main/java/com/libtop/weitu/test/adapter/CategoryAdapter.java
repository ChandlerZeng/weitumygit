package com.libtop.weitu.test.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.libtop.weitu.R;
import com.libtop.weitu.test.Category;
import com.libtop.weitu.viewadapter.CommonAdapter;
import com.libtop.weitu.viewadapter.ViewHolderHelper;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by zeng on 2016/10/10.
 */

public class CategoryAdapter extends CommonAdapter<Category>
{
    public CategoryAdapter(Context context, List<Category> datas)
    {
        super(context, R.layout.item_main_classify, datas);
    }


    @Override
    public int getCount()
    {
        return super.getCount() + 1;
    }


    @Override
    public Category getItem(int position)
    {
        if (position == getCount() - 1)
        {
            return null;
        }
        return super.getItem(position);
    }


    @Override
    public void convert(ViewHolderHelper helper, Category object, int position)
    {
        ImageView imageView = helper.getView(R.id.main_classify_image);
        if (datas != null && position == getCount() - 1)
        {
            helper.setText(R.id.main_classify_text, "全部分类");
            imageView.setImageResource(R.drawable.shape_bg_g2);
        }
        else
        {
            if (object.name != null)
            {
                helper.setText(R.id.main_classify_text, object.name);
            }
            Picasso.with(context).load(object.cover).placeholder(R.drawable.default_error).resize(100, 100).centerCrop().into(imageView);
        }
    }
}
