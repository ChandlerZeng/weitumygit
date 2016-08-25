package com.libtop.weitu.base;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import butterknife.ButterKnife;

public abstract class BaseFragmentDialog extends DialogFragment implements IFragmentStub {

	protected BaseActivity mContext;
	private boolean state = false;
	protected FragmentManager mFm;

	private boolean injected = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_TITLE,
				android.R.style.Theme_Translucent_NoTitleBar);
		mContext = (BaseActivity) getActivity();
		mFm=getChildFragmentManager();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getDialog().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
								 KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getAction() == KeyEvent.ACTION_UP) {
					if (state) {
						onBackPressed();
					}
					state = false;
					return true;
				} else if (keyCode == KeyEvent.KEYCODE_BACK
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					state = true;
				}
				return false;
			}
		});
		return dialog;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		injected = true;
		View view=inflater.inflate(getLayoutId(),container,false);
		if (view==null) {
			throw new NullPointerException("please set fixed layout id!");
		}
//		fieldView(view);
//		View view= x.view().inject(this, inflater, container);
		ButterKnife.bind(this,view);
		onCreation(view);
		return view;
	}

	protected abstract int getLayoutId();

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (!injected) {
//			x.view().inject(this, this.getView());
			ButterKnife.bind(this,this.getView());
			onCreation(this.getView());
		}
	}

	@Override
	public void onCreation(View root) {

	}

	@Override
	public void onBackPressed() {
		dismiss();
	}

	public void showToast(String message) {
		mContext.showToast(message);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mContext=null;
	}
}
