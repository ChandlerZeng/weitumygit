package com.libtop.weitu.activity.search.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.libtop.weitu.R;
import com.libtop.weitu.base.BaseAdapter;
import com.libtop.weitu.dao.bean.History;
import com.libtop.weitu.utils.ContantsUtil;
import com.squareup.picasso.Picasso;


import java.util.List;

public class HistoryAdapter extends BaseAdapter<History> {

//	protected ImageOptions options;

	public HistoryAdapter(Context context, List<History> data) {
		super(context, data, R.layout.item_history_layout);
//		options = new DisplayImageOptions.Builder()
//				.showStubImage(R.drawable.noimage)
//				.showImageForEmptyUri(R.drawable.noimage)
//				.showImageOnFail(R.drawable.noimage)
//				.cacheInMemory(true).cacheOnDisc(true)
//				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
//				.displayer(new RoundedBitmapDisplayer(4)).build();
//		options=new ImageOptions.Builder()
//				.setLoadingDrawableId(R.drawable.content_bg)
//				.setFailureDrawableId(R.drawable.content_bg)
//				.setUseMemCache(true)
//				.setRadius(4).build();
	}

	@Override
	protected void newView(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.title = (ImageView) convertView.findViewById(R.id.icon);
		convertView.setTag(holder);
	}

	@Override
	protected void holderView(View convertView, History history,int position) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
//		ImageLoader.getInstance()
//				.displayImage(ContantsUtil.IMG_BASE + history.getUrl(),
//						holder.title, options);
		Picasso.with(mContext).load(ContantsUtil.IMG_BASE + history.getUrl()).into(holder.title);
//		x.image().bind(holder.title,ContantsUtil.IMG_BASE + history.getUrl(),options);
	}

	class ViewHolder {
		ImageView title;
	}

}
