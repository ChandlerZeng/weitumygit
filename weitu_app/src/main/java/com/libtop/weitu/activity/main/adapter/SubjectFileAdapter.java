package com.libtop.weitu.activity.main.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.search.dto.BookDto;
import com.libtop.weitu.base.BaseAdapter;
import com.libtop.weitu.utils.CheckUtil;
import com.libtop.weitu.utils.ContantsUtil;
import com.libtop.weitu.viewadapter.CommonAdapter;
import com.libtop.weitu.viewadapter.ViewHolderHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by LianTu on 2016/6/22.
 */
public class SubjectFileAdapter extends CommonAdapter<BookDto> {

    public SubjectFileAdapter(Context context, List<BookDto> data) {
        super(context, R.layout.item_list_subject_file, data);
    }

    @Override
    public void convert(ViewHolderHelper helper, BookDto object, int position) {
        ImageView fileImage = helper.getView(R.id.subject_file_image);
        Picasso.with(context).load(ContantsUtil.IMG_BASE + object.cover).placeholder(R.drawable.default_image).into(fileImage);
        helper.setText(R.id.subject_file_title,object.title);
        if(!CheckUtil.isNull(object.author)){
            helper.setText(R.id.subject_file_publisher,object.author);
        }
        if(!CheckUtil.isNull(object.publisher)){
            helper.setText(R.id.subject_file_date,object.publisher);
        }
    }

}
