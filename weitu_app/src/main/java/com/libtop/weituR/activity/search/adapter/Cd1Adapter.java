package com.libtop.weituR.activity.search.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weituR.base.BaseAdapter;

import java.util.List;

public class Cd1Adapter extends BaseAdapter<Integer> {

	public Cd1Adapter(Context context, List<Integer> data) {
		super(context, data, R.layout.item_cd_layout);
	}

	@Override
	protected void newView(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.cdName = (TextView) convertView.findViewById(R.id.name);
		holder.size = (TextView) convertView.findViewById(R.id.size);
		holder.play = (ImageView) convertView.findViewById(R.id.play);
		convertView.setTag(holder);
	}

	@Override
	protected void holderView(View convertView, Integer dto,int position) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		holder.cdName.setText("光盘" + dto);
		holder.size.setVisibility(View.GONE);
		holder.play.setVisibility(View.GONE);
	}

	class ViewHolder {
		TextView cdName;
		TextView size;
		ImageView play;
	}
}
