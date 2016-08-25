package com.libtop.weitu.activity.search.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.search.dto.CdDto;
import com.libtop.weitu.base.BaseAdapter;

import java.util.List;

public class CdAdapter extends BaseAdapter<CdDto>{

	public CdAdapter(Context context, List<CdDto> data) {
		super(context, data, R.layout.item_cd_layout);
	}

	@Override
	protected void newView(View convertView) {
		ViewHolder holder = new ViewHolder();
		holder.cdName = (TextView) convertView.findViewById(R.id.name);
		holder.size = (TextView) convertView.findViewById(R.id.size);
		holder.play = (ImageView) convertView.findViewById(R.id.play);
		holder.playState = (TextView) convertView.findViewById(R.id.play_state);
		convertView.setTag(holder);
	}

	@Override
	protected void holderView(View convertView, CdDto dto,int position) {
		ViewHolder holder = (ViewHolder) convertView.getTag();
		holder.cdName.setText(dto.name);
		if ("folder".equals(dto.type)) {
			holder.size.setText("目录");
		} else {
			holder.size.setText(dto.size);
		}
		if (dto.playState == -2) {
			holder.play.setVisibility(View.GONE);
			holder.playState.setVisibility(View.VISIBLE);
			holder.playState.setText("加载失败");
			holder.play.setVisibility(View.GONE);
		} else if (dto.playState == -1) {
			holder.play.setVisibility(View.GONE);
			holder.playState.setVisibility(View.GONE);
			holder.play.setVisibility(View.GONE);
		} else if (dto.playState == 1) {
			holder.play.setVisibility(View.GONE);
			holder.playState.setVisibility(View.VISIBLE);
			holder.playState.setText("加载中请稍后");
			holder.play.setVisibility(View.VISIBLE);
		} else if (dto.playState == 2) {
			holder.play.setVisibility(View.GONE);
			holder.playState.setVisibility(View.VISIBLE);
			holder.playState.setText("加载完成");
			holder.play.setVisibility(View.VISIBLE);
		}else if(dto.playState == 3){
			holder.play.setVisibility(View.GONE);
			holder.playState.setVisibility(View.VISIBLE);
			holder.playState.setText("正在播放");
			holder.play.setVisibility(View.VISIBLE);
		}
	}

	class ViewHolder {
		TextView cdName;
		TextView size;
		TextView playState;
		ImageView play;
	}
}
