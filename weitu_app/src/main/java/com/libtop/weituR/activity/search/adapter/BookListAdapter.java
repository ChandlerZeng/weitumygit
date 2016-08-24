package com.libtop.weituR.activity.search.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.search.dto.BookDto;
import com.libtop.weituR.base.impl.ImgAdapter;
import com.libtop.weituR.utils.CheckUtil;
import com.libtop.weituR.utils.ContantsUtil;
import com.squareup.picasso.Picasso;


import java.util.List;

public class BookListAdapter extends ImgAdapter {

	public BookListAdapter(Context context, List<?> data) {
		super(context, data, R.layout.item_list_video);
	}

	@Override
	protected void newView(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.icon = (ImageView) convertView.findViewById(R.id.icon);
		holder.name = (TextView) convertView.findViewById(R.id.title);
		holder.tvTag = (TextView) convertView.findViewById(R.id.tv_tag);
		holder.author = (TextView) convertView.findViewById(R.id.author);
		holder.content = (TextView) convertView.findViewById(R.id.publisher);
		holder.desc = (TextView) convertView.findViewById(R.id.tv_desc);
		convertView.setTag(holder);
	}

	@Override
	protected void holderView(View convertView, Object itemObject,int position) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		BookDto dto = (BookDto) itemObject;
		holder.name.setText(dto.title);
		holder.author.setText(dto.author);
		if (!TextUtils.isEmpty(dto.introduction)){
			holder.desc.setText(dto.introduction);
		}
		if(!CheckUtil.isNull(dto.publisher)){
			holder.content.setText(dto.publisher);
		}else{
			holder.content.setText("");
		}
		if (!TextUtils.isEmpty(dto.categoriesName1)){
			holder.tvTag.setText(dto.categoriesName2);
		}
		if (!TextUtils.isEmpty(dto.categoriesName1)){
			holder.tvTag.setText(dto.categoriesName1+"/"+dto.categoriesName2);
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
		TextView tvTag;
		TextView desc;
	}

}
