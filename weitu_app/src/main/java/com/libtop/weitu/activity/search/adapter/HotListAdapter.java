package com.libtop.weitu.activity.search.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.search.dto.HotSearchDto;
import com.libtop.weitu.base.BaseAdapter;

import java.util.List;

/**
 * Created by LianTu on 2016/6/28.
 */
public class HotListAdapter extends BaseAdapter<HotSearchDto>{
    public HotListAdapter(Context context, List<HotSearchDto> data) {
        super(context, data, R.layout.item_hot_layout);
    }

    @Override
    protected void newView(View convertView) {
        ViewHolder holder = new ViewHolder();
        holder.tvNum = (TextView) convertView.findViewById(R.id.tv_num);
        holder.title = (TextView) convertView.findViewById(R.id.title);
        convertView.setTag(holder);
    }

    @Override
    protected void holderView(View convertView, HotSearchDto hotSearchDto,int position) {
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.title.setText(hotSearchDto.title);
        holder.tvNum.setText((position+1)+"");
        String numText = holder.tvNum.getText().toString();
        holder.tvNum.setBackgroundResource(R.color.white);
        switch (position){
            case (0):
                if (numText.equals("1"))
                    holder.tvNum.setBackgroundResource(R.color.num1);
                break;
            case 1:
                if (numText.equals("2"))
                    holder.tvNum.setBackgroundResource(R.color.num2);
                break;
            case 2:
                if (numText.equals("3"))
                    holder.tvNum.setBackgroundResource(R.color.num3);
                break;
        }
    }

    class ViewHolder{
        TextView tvNum;
        TextView title;
    }

}
