package com.libtop.weitu.activity.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.main.dto.SchoolDto;
import com.libtop.weitu.utils.CheckUtil;

import java.util.List;

public class SortAdapter extends BaseAdapter implements SectionIndexer {
	private List<SchoolDto> list = null;
	private Context mContext;

	public SortAdapter(Context mContext, List<SchoolDto> list) {
		this.mContext = mContext;
		this.list = list;
	}

	/**
	 * @param list
	 */
	public void updateListView(List<SchoolDto> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final SchoolDto mContent = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(R.layout.item_school,
					null);
			viewHolder.tvTitle = (TextView) view.findViewById(R.id.title);
			viewHolder.tvLetter = (TextView) view.findViewById(R.id.catalog);
			viewHolder.line = view.findViewById(R.id.line);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		String pre = "";
		if(position != 0){
			pre = list.get(position - 1).character;
		}
		if (!CheckUtil.checkEquels(pre, list.get(position).character)) {
			viewHolder.tvLetter.setVisibility(View.VISIBLE);
			viewHolder.tvLetter.setText(mContent.character.toUpperCase());
			viewHolder.line.setVisibility(View.VISIBLE);
		} else {
			viewHolder.tvLetter.setVisibility(View.GONE);
			viewHolder.line.setVisibility(View.GONE);
		}
		viewHolder.tvTitle.setText(this.list.get(position).name);
		return view;

	}

	final static class ViewHolder {
		TextView tvLetter;
		TextView tvTitle;
		View line;
	}

	/**
	 */
	public int getSectionForPosition(int position) {
		return list.get(position).character.charAt(0);
	}

	/**
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).character;
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		String sortStr = str.trim().substring(0, 1).toUpperCase();
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}
}