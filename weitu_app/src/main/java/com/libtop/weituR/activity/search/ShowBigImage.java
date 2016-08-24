package com.libtop.weituR.activity.search;

import android.os.Bundle;
import android.view.View;

import com.libtop.weitu.R;
import com.libtop.weituR.base.impl.ImgActivity;
import com.libtop.weituR.widget.photoview.PhotoView;
import com.libtop.weituR.widget.photoview.PhotoViewAttacher;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


/**
 * 下载显示大图
 * 
 */
public class ShowBigImage extends ImgActivity {

	private PhotoView image;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_show_big_image);
		super.onCreate(savedInstanceState);

		image = (PhotoView) findViewById(R.id.image);
		String url = getIntent().getStringExtra("url");
		Picasso.with(mContext)
				.load(url)
				.into(image, new Callback() {
					@Override
					public void onSuccess() {
						dismissLoading();
					}

					@Override
					public void onError() {
						dismissLoading();
					}
				});
//		x.image().bind(image, url, mOptions, new Callback.ProgressCallback<Drawable>() {
//			@Override
//			public void onWaiting() {
//
//			}
//
//			@Override
//			public void onStarted() {
//				mLoading.show();
//			}
//
//			@Override
//			public void onLoading(long total, long current, boolean isDownloading) {
//
//			}
//
//			@Override
//			public void onSuccess(Drawable result) {
//				if (mLoading.isShowing()){
//					mLoading.dismiss();
//				}
//			}
//
//			@Override
//			public void onError(Throwable ex, boolean isOnCallback) {
//				if (mLoading.isShowing()){
//					mLoading.dismiss();
//				}
//			}
//
//			@Override
//			public void onCancelled(CancelledException cex) {
//				if (mLoading.isShowing()){
//					mLoading.dismiss();
//				}
//			}
//
//			@Override
//			public void onFinished() {
//
//			}
//		});
//		ImageLoader.getInstance().displayImage(url, image,mOptions,new ImageLoadingListener() {
//
//			@Override
//			public void onLoadingStarted(String arg0, View arg1) {
//				mLoading.show();
//			}
//
//			@Override
//			public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
//				mLoading.dismiss();
//			}
//
//			@Override
//			public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
//				mLoading.dismiss();
//			}
//
//			@Override
//			public void onLoadingCancelled(String arg0, View arg1) {
//				mLoading.dismiss();
//			}
//		});

		image.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
			@Override
			public void onViewTap(View view, float x, float y) {
				finish();
				overridePendingTransition(R.anim.alpha_into, R.anim.zoomout);
			}
		});

	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.alpha_into, R.anim.zoomout);
	}
}
