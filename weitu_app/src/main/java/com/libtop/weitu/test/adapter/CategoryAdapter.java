package com.libtop.weitu.test.adapter;

import android.content.Context;
import android.graphics.Color;
import android.widget.ImageView;

import com.libtop.weitu.R;
import com.libtop.weitu.test.Category;
import com.libtop.weitu.utils.ImageLoaderUtil;
import com.libtop.weitu.viewadapter.CommonAdapter;
import com.libtop.weitu.viewadapter.ViewHolderHelper;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.ColorFilterTransformation;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;


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
        if (position == getCount() - 1)
        {
            helper.setText(R.id.main_classify_text, "全部分类");
            imageView.setImageResource(R.drawable.shape_bg_g2);
        }
        else
        {

            helper.setText(R.id.main_classify_text, object.name);

            List<Transformation> transformations = new ArrayList<>();
            transformations.add(ImageLoaderUtil.getDefaultRoundedCornersTransformation(context));
            transformations.add(new ColorFilterTransformation(Color.argb(160, 0, 0, 0)));

            ImageLoaderUtil.build(context, object.cover).transform(transformations).fit().into(imageView);
        }
    }
}
