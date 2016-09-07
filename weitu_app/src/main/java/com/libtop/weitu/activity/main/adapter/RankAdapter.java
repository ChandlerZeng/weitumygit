package com.libtop.weitu.activity.main.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.main.clickHistory.ResultBean;
import com.libtop.weitu.activity.search.dto.BookDto;
import com.libtop.weitu.utils.CheckUtil;
import com.libtop.weitu.utils.ContantsUtil;
import com.libtop.weitu.utils.DateUtil;
import com.libtop.weitu.viewadapter.CommonAdapter;
import com.libtop.weitu.viewadapter.ViewHolderHelper;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Zeng on 2016/9/7.
 */
public class RankAdapter extends CommonAdapter<ResultBean> {

    public RankAdapter(Context context, List<ResultBean> data) {
        super(context, R.layout.item_list_subject_file, data);
    }

    @Override
    public void convert(ViewHolderHelper helper, ResultBean object, int position) {
        ImageView fileImage = helper.getView(R.id.subject_file_image);
        Picasso.with(context).load(ContantsUtil.IMG_BASE + object.target.cover).placeholder(R.drawable.default_image).into(fileImage);
        helper.setText(R.id.subject_file_title,object.target.title);
        if(!CheckUtil.isNull(object.target.publisher)){
            helper.setText(R.id.subject_file_publisher,object.target.publisher);
        }
        if(!CheckUtil.isNull(object.target.timeline)){
            String date = DateUtil.parseToDate(object.target.timeline);
            helper.setText(R.id.subject_file_date,date);
        }
    }
    public void setData(List<ResultBean> data){
        datas = data;
        notifyDataSetChanged();
    }

}
