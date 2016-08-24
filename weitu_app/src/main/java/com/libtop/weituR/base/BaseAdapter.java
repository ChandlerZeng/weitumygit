package com.libtop.weituR.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.List;

public abstract class BaseAdapter<T> extends android.widget.BaseAdapter {
	
	protected Context mContext;
	protected LayoutInflater mInflater;
	protected List<T> mData;
	private int mResId;
	
	public BaseAdapter(Context context, List<T> data, int resourceId){
		mContext = context;
		mData = data;
		mInflater = LayoutInflater.from(context);
		mResId = resourceId;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}	

	@Override
	public long getItemId(int position) {
		return convertId(position,mData.get(position));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = mInflater.inflate(mResId, null, false);
			newView(convertView);
		}
		holderView(convertView,mData.get(position),position);
		return convertView;
	}
	
	public void setData(List<T> data){
		mData = data;
	}
	
	public List<T> getData(){
		return mData;
	}
	
	/**
	 * 用于覆盖，在各个其他adapter里边返回id,默认返回position
	 * @param position
	 * @return
	 */
	protected long convertId(int position,T t){
		return position;
	}

	/**
	 * 第一次创建的时调用
	 * @param convertView
	 */
	protected abstract void newView(View convertView);
	
	/**
	 * 用于数据赋值等等
	 * @param convertView
	 */
	protected abstract void holderView(View convertView,T t,int position);
}
