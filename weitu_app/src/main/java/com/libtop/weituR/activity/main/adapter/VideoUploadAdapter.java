package com.libtop.weituR.activity.main.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.main.dto.VideoBean;
import com.libtop.weituR.activity.main.dto.VideoFolderBean;
import com.libtop.weituR.base.BaseAdapter;
import com.libtop.weituR.utils.DisplayUtils;
import com.libtop.weituR.utils.TransformUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by LianTu on 2016/4/25.
 */
public class VideoUploadAdapter extends BaseAdapter<VideoBean> {

    private HashMap<Integer,Boolean> map = new HashMap<Integer, Boolean>();

    public VideoUploadAdapter(Context context, List<VideoBean> data) {
        super(context, data, R.layout.item_video_upload);
    }

    @Override
    protected void newView(View convertView) {
        Holder holder = new Holder();
        holder.thumbImage = (ImageView)convertView.findViewById(R.id.thumb_image);
        holder.videoSize = (TextView)convertView.findViewById(R.id.video_size);
        holder.videoDuration = (TextView)convertView.findViewById(R.id.video_duration);
        convertView.setTag(holder);
    }

    @Override
    protected void holderView(View convertView, final VideoBean videoBean, final int position) {
        Holder holder = (Holder) convertView.getTag();
        long duration_temp = videoBean.videDduration;
        String hms;
        long seconds = (duration_temp % (1000 * 60)) / 1000;
        long hours = (duration_temp / (1000 * 60 * 60));
        long minutes = (duration_temp % (1000 * 60 * 60)) / (1000 * 60);
        if (duration_temp <1000*60*60){
            hms = String.format("%02d:%02d",minutes,seconds);
        }else {

            hms = String.format("%02d:%02d",hours,minutes);
        }
        holder.videoDuration.setText(hms);
        holder.videoSize.setText(TransformUtil.bytes2kb(videoBean.videoSize));
//        if(videoBean.thumbPath != null){
//            holder.thumbImage.setImageURI(Uri.parse(videoBean.thumbPath));
//        }
        if (videoBean.filePath != null){
            Bitmap bitmap = null;
            bitmap = BitmapCache.getBitmapFromMemCache(position);

            if (bitmap != null) {
                holder.thumbImage.setImageBitmap(bitmap);
            } else {
                holder.thumbImage.setImageResource(R.drawable.default_image);
//                final int CORE_POOL_SIZE = 5;
//                final int MAXIMUM_POOL_SIZE = 128;
//                final int KEEP_ALIVE = 10;
//
//                final BlockingQueue<Runnable> sWorkQueue =
//                        new LinkedBlockingQueue<Runnable>(10);
//
//                ThreadFactory sThreadFactory = Executors.defaultThreadFactory();
//
//                final ThreadPoolExecutor sExecutor =
//                        new ThreadPoolExecutor(CORE_POOL_SIZE,
//                                MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, sWorkQueue, sThreadFactory);

                try {
                    if (map.get(position)){
                        return;
                    }
                }catch (NullPointerException e){

                }
                map.put(position,true);
                new AsyncTask<Integer,String,Bitmap>(){

                    @Override
                    protected Bitmap doInBackground(Integer... params) {
                        Bitmap bitmap1 = ThumbnailUtils.createVideoThumbnail(videoBean.filePath, MediaStore.Video.Thumbnails.MICRO_KIND);
                        return bitmap1;
                    }

                    //set photoView and holder
                    protected void onPostExecute(Bitmap bitmap1) {
                        if (bitmap1 != null) {
                            BitmapCache.addBitmapToMemoryCache(position, bitmap1);
                            notifyDataSetChanged();
                        }
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Integer[]) null);
            }
//            Bitmap b = ThumbnailUtils.createVideoThumbnail(videoBean.filePath, MediaStore.Video.Thumbnails.MICRO_KIND);
//            holder.thumbImage.setImageBitmap(b);
        }
    }

    private static class Holder{
        ImageView thumbImage;
        TextView videoSize;
        TextView videoDuration;
    }
}
