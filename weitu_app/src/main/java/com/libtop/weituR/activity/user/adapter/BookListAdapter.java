package com.libtop.weituR.activity.user.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.user.dto.FBookDto;
import com.libtop.weituR.base.impl.ImgAdapter;
import com.libtop.weituR.utils.CheckUtil;
import com.libtop.weituR.utils.ContantsUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BookListAdapter extends ImgAdapter {

	public BookListAdapter(Context context, List<?> data) {
		super(context, data, R.layout.item_list_result);
	}

	@Override
	protected void newView(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.icon = (ImageView) convertView.findViewById(R.id.icon);
		holder.name = (TextView) convertView.findViewById(R.id.title);
		holder.author = (TextView) convertView.findViewById(R.id.author);
		holder.content = (TextView) convertView.findViewById(R.id.publisher);
		convertView.setTag(holder);
	}

	@Override
	protected void holderView(View convertView, Object itemObject,int position) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		FBookDto dto = (FBookDto) itemObject;
		holder.name.setText(dto.title);
		holder.author.setText(dto.author);
		if(!CheckUtil.isNull(dto.publisher)){
			holder.content.setText(dto.publisher);
		}else{
			holder.content.setText("");
		}
		Picasso.with(mContext).load(ContantsUtil.IMG_BASE + dto.cover).into(holder.icon);
//		x.image().bind(holder.icon,ContantsUtil.IMG_BASE + dto.cover,mOptions);
//		ImageLoader.getInstance().displayImage(
//				ContantsUtil.IMG_BASE + dto.cover, holder.icon, mOptions);
	}

	class ViewHolder {
		ImageView icon;
		TextView name;
		TextView author;
		TextView content;
	}

}
