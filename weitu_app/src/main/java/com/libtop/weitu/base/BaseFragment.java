package com.libtop.weitu.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.NetworkUtil;
import com.libtop.weitu.widget.dialog.TranLoading;

import butterknife.ButterKnife;
import rx.Subscription;

public abstract class BaseFragment extends Fragment implements IFragmentStub{

	protected Subscription subscription;
	protected BaseActivity mContext;
	protected Preference mPreference;
	protected TranLoading mLoading;
	protected FragmentManager mFm;

	private boolean injected = false;
	View rootView;//缓存Fragment view
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = (BaseActivity) getActivity();
		if (mPreference==null)
			mPreference = new Preference(mContext);
		mLoading=mContext.mLoading;
		if (mLoading==null)
			mLoading=new TranLoading(mContext);
		mFm=getChildFragmentManager();
	}

	protected void unsubscribe() {
		if (subscription != null && !subscription.isUnsubscribed()) {
			subscription.unsubscribe();
		}
	}


	public void showTip(final String str){
		mContext.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		injected = true;
		if(rootView==null){
			View view=inflater.inflate(getLayoutId(),container,false);
			rootView = view;
			if (view==null) {
				throw new NullPointerException("please set fixed layout id!");
			}
			ButterKnife.bind(this,view);
			onCreation(rootView);
		}


		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}
		return rootView;
	}

	protected abstract int getLayoutId();

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (!injected) {
			ButterKnife.bind(this,this.getView());
			onCreation(this.getView());
		}
	}

	@Override
	public void onCreation(View root) {

	}

	@Override
	public void onBackPressed() {

	}

	public void onResult(int request,int result,Intent data){

	}


	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unsubscribe();
	}

	protected void showLoding(){
		if (mLoading!=null&&!mLoading.isShowing())
			mLoading.show();
	}

	protected void dismissLoading(){
		if (mLoading!=null&&mLoading.isShowing())
			mLoading.dismiss();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mContext=null;
		mFm=null;
		if(mLoading!=null){
			mLoading.dismiss();
			mLoading = null;
		}
	}

	public void showToast(String message){
		if (mContext!=null){
			mContext.showToast(message);
		}
	}

	protected void noNetThanExit(Context context){
		if (!NetworkUtil.checkConnection(context)){
			onBackPressed();
			Toast.makeText(context,"请检查网络后重试",Toast.LENGTH_SHORT).show();
		}
	}

}
