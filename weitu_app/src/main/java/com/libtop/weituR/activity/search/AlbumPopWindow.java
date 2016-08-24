package com.libtop.weituR.activity.search;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weituR.utils.DisplayUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/12/25 0025.
 */
public class AlbumPopWindow extends PopupWindow {
    ViewPager mPager;
    TextView mPageText;
    private Context mContext;
    View mRootView;
    private GalleryAdapter mAdapter;

    private List<String> mUrls;
    public AlbumPopWindow(Context context,List<String> data) {
        super(context);
        mContext=context;
        mUrls=data;
        setupView();
    }

    public void resetData(List<String> data){
        mUrls=data;
        mAdapter.notifyDataSetChanged();
    }

    private void setupView(){
        mRootView= LayoutInflater.from(mContext).inflate(R.layout.pop_gallary_hor,null);
        setContentView(mRootView);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mPager=(ViewPager)mRootView.findViewById(R.id.viewpager);
        mPageText=(TextView)mRootView.findViewById(R.id.textView);

        ViewGroup.LayoutParams params=mPager.getLayoutParams();
        params.height= DisplayUtils.getDisplayWith(mContext)*3/4;
        mPager.setLayoutParams(params);

        mAdapter=new GalleryAdapter(mUrls);
        mPager.setAdapter(mAdapter);
        mPager.setCurrentItem(0);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(0x000000));
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1.0f);
            }
        });

        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mPageText.setText((position+1)+"/"+mUrls.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setBackgroundAlpha(float value){
        Activity activity=(Activity)mContext;
        WindowManager.LayoutParams lp=activity.getWindow().getAttributes();
        lp.alpha = value;
        activity.getWindow().setAttributes(lp);
    }

    public void show(View parent,int position){
        mPager.setCurrentItem(position);
        mPageText.setText((position+1)+"/"+mUrls.size());
        showAtLocation(parent, Gravity.CENTER, 0, 0);
        setBackgroundAlpha(0.5f);
    }

//    private ImageOptions mImgOptions=new ImageOptions.Builder()
//            .setLoadingDrawableId(R.drawable.content_bg)
//            .setFailureDrawableId(R.drawable.content_bg)
//            .setUseMemCache(true)
//            .setRadius(4).build();

//    private DisplayImageOptions mImgOptions = new DisplayImageOptions.Builder()
//            .showStubImage(R.drawable.content_bg)
//            .showImageForEmptyUri(R.drawable.content_bg)
//            .cacheInMemory(true).cacheOnDisc(true)
//            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
//            .displayer(new RoundedBitmapDisplayer(4)).build();

    protected class GalleryAdapter extends PagerAdapter{
        private List<ImageView> mImageViews=new ArrayList<ImageView>();
        ViewGroup.LayoutParams imgParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                   ,ViewGroup.LayoutParams.MATCH_PARENT);
        public GalleryAdapter(List<String> urls){
//            int height= DisplayUtils.getDisplayWith(mContext)*3/4;
//            imgParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
//                    , height);
            initImage(urls);
        }

        @Override
        public void notifyDataSetChanged() {
            initImage(mUrls);
            super.notifyDataSetChanged();
        }

        private void initImage(List<String> urls){
            if (urls==null||urls.isEmpty()){
                mImageViews.clear();
                return;
            }
            mImageViews.clear();
            for (String url:urls){
                ImageView imageView=new ImageView(mContext);
                imageView.setLayoutParams(imgParams);
                imageView.setTag(url);
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                mImageViews.add(imageView);
            }

        }

        @Override
        public int getCount() {
            return mImageViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imgView=mImageViews.get(position);
            container.addView(mImageViews.get(position), 0);
//            ImageLoader.getInstance().displayImage((String) imgView.getTag(), imgView, mImgOptions);
//            x.image().bind(imgView,(String) imgView.getTag(),mImgOptions);
            Picasso.with(mContext).load((String) imgView.getTag()).into(imgView);
            return imgView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ImageView imgView=mImageViews.get(position);
            container.removeView(imgView);
//            ImageLoader.getInstance().cancelDisplayTask(imgView);
        }
    }


}
