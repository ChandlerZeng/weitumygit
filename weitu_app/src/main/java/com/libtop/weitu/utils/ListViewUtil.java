package com.libtop.weitu.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import com.libtop.weitu.R;


/**
 * Created by LianTu on 2016-9-19.
 */
public class ListViewUtil
{
    public static void addPaddingHeader(Context context, ListView listView)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.include_listview_padding_header,null);
        listView.addHeaderView(view,null,false);
    }
}
