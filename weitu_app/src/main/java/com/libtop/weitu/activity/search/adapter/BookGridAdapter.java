package com.libtop.weitu.activity.search.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.search.dto.BookDto;
import com.libtop.weitu.base.impl.ImgAdapter;
import com.libtop.weitu.utils.ContantsUtil;
import com.squareup.picasso.Picasso;


import java.util.List;

public class BookGridAdapter extends ImgAdapter{

	public BookGridAdapter(Context context, List<?> data) {
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
		BookDto dto = (BookDto) itemObject;
		holder.name.setText(dto.title);
		holder.content.setText(dto.author);
//		ImageLoader.getInstance().displayImage(ContantsUtil.IMG_BASE + dto.cover, holder.icon,mOptions);
		Picasso.with(mContext).load(ContantsUtil.IMG_BASE + dto.cover).into(holder.icon);
//		x.image().bind(holder.icon,ContantsUtil.IMG_BASE + dto.cover,mOptions);
	}
	
	class ViewHolder{
		ImageView icon;
		TextView name;
		TextView content;
	}

}
