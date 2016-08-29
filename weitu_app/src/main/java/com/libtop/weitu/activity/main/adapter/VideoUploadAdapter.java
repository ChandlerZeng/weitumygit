package com.libtop.weitu.activity.main.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.main.dto.VideoBean;
import com.libtop.weitu.base.BaseAdapter;
import com.libtop.weitu.utils.TransformUtil;

import java.util.HashMap;
import java.util.List;

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
        if (videoBean.filePath != null){
            Bitmap bitmap = null;
            bitmap = BitmapCache.getBitmapFromMemCache(position);

            if (bitmap != null) {
                holder.thumbImage.setImageBitmap(bitmap);
            } else {
                holder.thumbImage.setImageResource(R.drawable.default_image);

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
        }
    }

    private static class Holder{
        ImageView thumbImage;
        TextView videoSize;
        TextView videoDuration;
    }
}
