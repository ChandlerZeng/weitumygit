package com.libtop.weitu.widget.dialog.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.base.BaseAdapter;
import com.libtop.weitu.widget.dialog.dto.MapModel;

import java.util.List;


public class DialogListAdapter extends BaseAdapter<MapModel>
{
    public DialogListAdapter(Context context, List<MapModel> data)
    {
        super(context, data, R.layout.item_list_dialog);
    }


    @Override
    protected void newView(View convertView)
    {
        ViewHolder holder = new ViewHolder();
        holder.modelName = (TextView) convertView.findViewById(R.id.textView);
        convertView.setTag(holder);
    }


    @Override
    protected void holderView(View convertView, MapModel model, int position)
    {
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.modelName.setText(model.value);
    }


    //	public DialogListAdapter(Context context, List<?> data) {
    //		super(context, data, R.layout.item_list_dialog);
    //	}
    //
    //	@Override
    //	protected void newView(View convertView) {
    //		ViewHolder holder = new ViewHolder();
    //		holder.modelName = (TextView) convertView.findViewById(R.id.textView);
    //		convertView.setTag(holder);
    //	}
    //
    //	@Override
    //	protected void holderView(View convertView, Object itemObject) {
    //		MapModel model = (MapModel) itemObject;
    //		ViewHolder holder = (ViewHolder) convertView.getTag();
    //		holder.modelName.setText(model.value);
    //	}
    //
    class ViewHolder
    {
        TextView modelName;
    }

}
