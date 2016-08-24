package com.libtop.weituR.activity.user.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weituR.base.impl.ImgAdapter;
import com.libtop.weituR.dao.bean.Book;
import com.libtop.weituR.utils.ContantsUtil;
import com.squareup.picasso.Picasso;


import java.util.List;

public class MainBookAdapter extends ImgAdapter{

	public MainBookAdapter(Context context, List<?> data) {
		super(context, data, R.layout.item_search_result);
	}

	@Override
	protected void newView(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.icon = (ImageView) convertView.findViewById(R.id.icon);
		holder.name = (TextView) convertView.findViewById(R.id.title);
		holder.content = (TextView) convertView.findViewById(R.id.content);
		convertView.setTag(holder);
	}

	@Override
	protected void holderView(View convertView, Object itemObject,int position) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		Book dto = (Book) itemObject;
		holder.name.setText(dto.getName());
		holder.content.setText(dto.getAuthor());
		Picasso.with(mContext).load(ContantsUtil.IMG_BASE + dto.getCover()).into(holder.icon);
//		x.image().bind(holder.icon,ContantsUtil.IMG_BASE + dto.getCover(),mOptions);
//		ImageLoader.getInstance().displayImage(
//				ContantsUtil.IMG_BASE + dto.getCover(), holder.icon, mOptions);
	}

	class ViewHolder {
		ImageView icon;
		TextView name;
		TextView content;
	}

}
