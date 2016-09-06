package com.libtop.weitu.utils.selector.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.main.dto.VideoBean;
import com.libtop.weitu.utils.selector.utils.AsyncImageLoader;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;


/**
 * <p>
 * Title: UploadAdapter2.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * CreateTime：16/5/5
 * </p>
 *
 * @author 作者名
 * @version common v1.0
 */
public class UploadAdapter2 extends BaseAdapter
{
    private LayoutInflater mInflater;
    public Context mContext;
    public List<VideoBean> mlist;
    private OnOptionImgClickListener mOptionImgClickListener;
    private int mGridWidth = 0;
    progressView holder;
    //    public DisplayImageOptions mOptions;
    //    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    private AsyncImageLoader imageLoader;


    public UploadAdapter2(Context context, List<VideoBean> list, OnOptionImgClickListener listenner)
    {
        this.mOptionImgClickListener = listenner;
        this.mlist = list;
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        //  imageLoader = new AsyncImageLoader(context);
        int width = 0;
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
        {
            Point size = new Point();
            wm.getDefaultDisplay().getSize(size);
            width = size.x;
        }
        else
        {
            width = wm.getDefaultDisplay().getWidth();
        }
        mGridWidth = width / 4;
        //        ImageLoader.getInstance().init(
        //                ImageLoaderConfiguration.createDefault(mContext));
        //        mOptions = new DisplayImageOptions.Builder()
        //                .showImageOnLoading(R.drawable.default_image)
        //                .showImageForEmptyUri(R.drawable.default_image)
        //                .showImageOnFail(R.drawable.default_image)
        //                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
        //                .displayer(new RoundedBitmapDisplayer(8)).cacheInMemory(true)
        //                .cacheOnDisc(true).build();
    }


    @Override
    public int getCount()
    {
        return mlist.size();
    }


    @Override
    public Object getItem(int position)
    {
        return mlist.get(position);
    }


    public void setData(List<VideoBean> list)
    {
        this.mlist = list;
    }


    @Override
    public long getItemId(int position)
    {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {

        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.upload_adapter2, null);
            holder = new progressView();
            holder.title = (TextView) convertView.findViewById(R.id.tv_folder_title);
            holder.count = (TextView) convertView.findViewById(R.id.tv_video_num);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imgBtn_video_choice);
            holder.imageFolder = (ImageView) convertView.findViewById(R.id.img_folder);
            holder.tagGroup = (TextView) convertView.findViewById(R.id.et_tag);
            convertView.setTag(holder);
        }
        else
        {
            holder = (progressView) convertView.getTag();
        }

        //        if (mlist.get(position).filePath.length() != 0) {
        final String imgUrl = mlist.get(position).filePath;
        holder.imageFolder.setTag(imgUrl);
        // 预设一个图片
        holder.imageFolder.setImageResource(R.drawable.default_image);
        //
        if (!TextUtils.isEmpty(imgUrl))
        {
            Picasso.with(mContext).invalidate(imgUrl);
            //            Picasso.with(mContext).setIndicatorsEnabled(true);
            Picasso.with(mContext).load(imgUrl)
                    //                    .noFade()
                    //                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .error(R.drawable.default_image).placeholder(R.drawable.default_image).networkPolicy(NetworkPolicy.NO_CACHE).fit().into(holder.imageFolder);
        }
        //        ImageLoader.getInstance().displayImage(imgUrl, holder.imageFolder,
        //                mOptions, null);
        //        if (!TextUtils.isEmpty(imgUrl)) {
        ////            Bitmap bitmap = imageLoader.loadImage(holder.imageFolder, imgUrl);
        ////            if (bitmap != null) {
        ////                holder.imageFolder.setImageBitmap(bitmap);
        ////            }
        //            bindData(holder.imageFolder,)
        //  }

        //    }

        holder.title.setText(mlist.get(position).titleChange);
        holder.tagGroup.setText(mlist.get(position).desc);
        holder.count.setText("" + mlist.get(position).videoSize + "\t张照片");
        holder.imageView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mOptionImgClickListener.onOptionImgTouch(v, position);
            }
        });
        return convertView;
    }


    class progressView
    {
        public View view, video;
        public TextView title, count;
        public ImageView imageView, imageFolder;
        public TextView tagGroup;
    }


    public interface OnOptionImgClickListener
    {
        void onOptionImgTouch(View v, int position);

    }


    /**
     * 加载本地图片
     * http://bbs.3gstdy.com
     *
     * @param url
     * @return
     */
    public Bitmap getLoacalBitmap(String url)
    {
        try
        {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
    }


    //    SimpleImageLoadingListener a = new SimpleImageLoadingListener() {
    //        public void onLoadingStarted(String imageUri, View view) {
    //
    //        }
    //
    //        public void onLoadingFailed(String imageUri, View view
    //        ) {
    //
    //
    //        }
    //
    //        public void onLoadingComplete(String imageUri, View view,
    //                                      Bitmap loadedImage) {
    //            ImageView imageView = (ImageView) view;
    //            if (imageView.getTag() != null && imageView.getTag().equals(imageUri)) {
    //                imageView.setImageBitmap(loadedImage);
    //            }
    //        }
    //    };

    /**
     * 图片加载第一次显示监听器
     *
     * @author Administrator
     */
    //    private static class AnimateFirstDisplayListener extends
    //            SimpleImageLoadingListener {
    //
    //        static final List<String> displayedImages = Collections
    //                .synchronizedList(new LinkedList<String>());
    //
    //        @Override
    //        public void onLoadingComplete(String imageUri, View view,
    //                                      Bitmap loadedImage) {
    //            if (loadedImage != null) {
    //                ImageView imageView = (ImageView) view;
    //                // 是否第一次显示
    //                boolean firstDisplay = !displayedImages.contains(imageUri);
    //                if (firstDisplay) {
    //                    // 图片淡入效果
    //                    FadeInBitmapDisplayer.animate(imageView, 500);
    //                    displayedImages.add(imageUri);
    //                }
    //            }
    //        }
    //    }
}

