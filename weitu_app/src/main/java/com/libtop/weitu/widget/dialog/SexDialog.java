package com.libtop.weitu.widget.dialog;

import android.content.Context;

import com.libtop.weitu.widget.dialog.dto.MapModel;

import java.util.List;

public class SexDialog extends BaseListDialog {

	public SexDialog(Context context) {
		super(context);
	}

	@Override
	protected void initData(List<MapModel> data) {
		MapModel model = new MapModel("0","男");
		data.add(model);
		model = new MapModel("1","女");
		data.add(model);
	}

}
