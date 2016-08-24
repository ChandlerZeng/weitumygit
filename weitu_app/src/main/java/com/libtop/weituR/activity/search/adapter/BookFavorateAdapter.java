package com.libtop.weituR.activity.search.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.search.dto.BookItem;
import com.libtop.weituR.base.BaseAdapter;

import java.util.List;

public class BookFavorateAdapter extends BaseAdapter<BookItem> {

	public BookFavorateAdapter(Context context, List<BookItem> data) {
		super(context, data, R.layout.item_favorate_book);
	}

	@Override
	protected void newView(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.bookNo = (TextView) convertView.findViewById(R.id.book_no);
		holder.no = (TextView) convertView.findViewById(R.id.no);
		holder.address = (TextView) convertView.findViewById(R.id.address);
		holder.state = (TextView) convertView.findViewById(R.id.state);
		convertView.setTag(holder);
	}

	@Override
	protected void holderView(View convertView, BookItem item,int position) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		holder.bookNo.setText(item.number);
		holder.no.setText(item.code);
		holder.address.setText(item.location);
		if ("可借".equals(item.status)) {
			holder.state.setText(item.status);
			holder.state.setTextColor(mContext.getResources().getColor(
					R.color.main_color));
		} else {
			holder.state.setText(item.status);
			holder.state.setTextColor(mContext.getResources().getColor(
					R.color.main_text));
		}
	}

	class ViewHolder {
		TextView bookNo;
		TextView no;
		TextView address;
		TextView state;
	}

}
