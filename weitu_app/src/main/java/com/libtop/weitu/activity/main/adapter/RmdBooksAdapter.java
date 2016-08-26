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
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by LianTu on 2016/6/22.
 */
public class RmdBooksAdapter extends BaseAdapter<BookDto> {

    public RmdBooksAdapter(Context context, List<BookDto> data) {
        super(context, data, R.layout.item_list_rmd_books);
    }

    @Override
    protected void newView(View convertView) {
        Holder holder = new Holder();
        holder.bookCover=(ImageView)convertView.findViewById(R.id.rmd_book_cover_img);
        holder.titleText = (TextView) convertView.findViewById(R.id.rmd_book_title);
        holder.authorText = (TextView) convertView.findViewById(R.id.rmd_book_author);
        holder.publisherText = (TextView) convertView.findViewById(R.id.rmd_book_publisher);
        holder.introduceText = (TextView) convertView.findViewById(R.id.rmd_book_introduction);
        convertView.setTag(holder);
    }

    @Override
    protected void holderView(View convertView, BookDto book, int position) {
        Holder holder = (Holder) convertView.getTag();
        Picasso.with(mContext).load(ContantsUtil.IMG_BASE + book.cover).placeholder(R.drawable.default_image).into(holder.bookCover);
        holder.titleText.setText(book.title);
        if(!CheckUtil.isNull(book.categoriesName1)||!CheckUtil.isNull(book.categoriesName2))
        holder.bookLabel.setText(book.categoriesName1+"/"+book.categoriesName2);
        if(!CheckUtil.isNull(book.author))
        holder.authorText.setText(book.author);
        if(!CheckUtil.isNull(book.publisher))
        holder.publisherText.setText(book.publisher);
        if(!CheckUtil.isNull(book.introduction))
        holder.introduceText.setText("简介："+book.introduction);
    }

    private class Holder{
        ImageView bookCover;
        TextView titleText,bookLabel,authorText,publisherText,introduceText;
    }

}
