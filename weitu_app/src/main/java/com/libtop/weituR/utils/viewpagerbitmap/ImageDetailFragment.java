package com.libtop.weituR.utils.viewpagerbitmap;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.libtop.weitu.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import io.vov.vitamio.utils.Log;

public class ImageDetailFragment extends Fragment {
    private String mImageUrl;
    ImageView mImageView;
    private ProgressBar progressBar;
    private PhotoViewAttacher mAttacher;
    private Bitmap mBitmap;
    private static int mwidth, mheight;
    static ImageDetailFragment imageDetailFragment;

    public static ImageDetailFragment newInstance(String imageUrl, int width, int height) {
        if(imageDetailFragment==null)
             imageDetailFragment = new ImageDetailFragment();
      //  final ImageDetailFragment f = new ImageDetailFragment();
        final Bundle args = new Bundle();
        args.putString("url", imageUrl);
        imageDetailFragment.setArguments(args);
        mwidth = width;
        mheight = height;
        return imageDetailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageUrl = getArguments() != null ? getArguments().getString("url")
                : null;
        int a = ImageDetailFragment.this.hashCode();
        Log.e("" + a, a);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.image_detail_fragment,
                container, false);
        mImageView = (ImageView) v.findViewById(R.id.image);
        mAttacher = new PhotoViewAttacher(mImageView);
        mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {

            @Override
            public void onPhotoTap(View arg0, float arg1, float arg2) {
                //getActivity().finish();
            }
        });

        progressBar = (ProgressBar) v.findViewById(R.id.loading);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String a = "http://nt1.libtop.com/f/" + mImageUrl + ".jpg";
        File imageFile = new File(mImageUrl);
        if (!imageFile.exists()) {
            progressBar.setVisibility(View.VISIBLE);
            Picasso.with(getActivity()).load(a).fit().into(mImageView, new Callback() {
                @Override
                public void onSuccess() {
                    progressBar.setVisibility(View.GONE);
                    mAttacher.update();
                }

                @Override
                public void onError() {
                    progressBar.setVisibility(View.GONE);
                }
            });
//            ImageLoader.getInstance().init(
//                    ImageLoaderConfiguration.createDefault(getActivity()));
//            ImageLoader.getInstance().displayImage(a, mImageView,
//                    new SimpleImageLoadingListener() {
//                        public void onLoadingStarted(String imageUri, View view) {
//                            progressBar.setVisibility(View.VISIBLE);
//                        }
//
//                        public void onLoadingFailed(String imageUri, View view
//                        ) {
//
//                            progressBar.setVisibility(View.GONE);
//                        }
//
//                        public void onLoadingComplete(String imageUri, View view,
//                                                      Bitmap loadedImage) {
//                            progressBar.setVisibility(View.GONE);
//                            mAttacher.update();
//                        }
//                    });
        } else {
            try {
                Bitmap bitmap1 = ((BitmapDrawable) mImageView.getBackground()).getBitmap();
                if (bitmap1 != null) {
                    bitmap1.recycle();
                    bitmap1 = null;
                }
            } catch (Exception e) {

            }
            if (mBitmap != null) {
                mBitmap.recycle();
                mBitmap = null;
            }
            mBitmap = getLoacalBitmap(mImageUrl);
            mImageView.setImageBitmap(mBitmap);
            mAttacher.update();
        }
    }

    public Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onDestroy() {
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
            System.gc();
        }
        super.onDestroy();
    }
}
