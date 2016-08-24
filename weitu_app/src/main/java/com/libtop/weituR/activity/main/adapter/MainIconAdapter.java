package com.libtop.weituR.activity.main.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weituR.base.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/8 0008.
 */
public class MainIconAdapter extends BaseAdapter<String> {
//    private int [] icons={R.drawable.grid_icon_classroom,R.drawable.grid_icon_lent,R.drawable.grid_icon_collection,R.drawable.grid_icon_upload};
    //屏蔽未使用
    private int [] icons={R.drawable.intelligence_search,R.drawable.read_history
            , R.drawable.class_icon,R.drawable.schedule
            ,R.drawable.video_icon, R.drawable.music_icon
            ,R.drawable.doc_icon, R.drawable.image_icon};


    public MainIconAdapter(Context context) {
        super(context,null, R.layout.item_grid_main);
        String[] texts=context.getResources().getStringArray(R.array.common_services_tag1);
        List<String> source=new ArrayList<String>();
        for (String text:texts){
            source.add(text);
        }
        mData=source;
    }



    @Override
    protected void newView(View convertView) {

    }

    @Override
    protected void holderView(View convertView, String s, int position) {
        convertView.setId(icons[position]);
        ImageView iconImg=(ImageView)convertView.findViewById(R.id.icon);
        TextView text=(TextView)convertView.findViewById(R.id.text);
        iconImg.setImageResource(icons[position]);
        text.setText(s);
    }
}
