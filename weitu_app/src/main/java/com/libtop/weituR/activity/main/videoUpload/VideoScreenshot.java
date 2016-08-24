package com.libtop.weituR.activity.main.videoUpload;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Title: VideoScreenshot.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * CreateTime：16/4/25
 * </p>
 *
 * @author 作者名
 * @version common v1.0
 */
public class VideoScreenshot {
    private static final String TAG = "MainActivity";

    public VideoScreenshot(String url, Context context) {
        this.mcontext = context;
        this.filePath = url;
    }

    private Context mcontext;

    List<Bitmap> mlist;

    private String filePath = "";

    long videoTime = 0;

    public Bitmap getVideoThumbnail(long length) throws MalformedURLException {

        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            Uri url = Uri.parse(filePath);
            retriever.setDataSource(mcontext, url);
            bitmap = retriever.getFrameAtTime(length, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }


    public long getVideoTime() {

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        long a = 0;
        try {
            mmr.setDataSource(filePath);
            String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE); // api level 10, 即从GB2.3.3开始有此功能
            Log.d(TAG, "title:" + title);
            String album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            Log.d(TAG, "album:" + album);
            String mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            Log.d(TAG, "mime:" + mime);
            String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            Log.d(TAG, "artist:" + artist);
            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION); // 播放时长单位为毫秒
            a = Long.parseLong(duration);

            Log.e(TAG, "duration:" + duration);
            String bitrate = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE); // 从api level 14才有，即从ICS4.0才有此功能
            Log.d(TAG, "bitrate:" + bitrate);
            String date = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);
            Log.d(TAG, "date:" + date);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return a;
    }

    public List<Bitmap> getTenBitmap(long length) throws MalformedURLException {
        List<Bitmap> a = new ArrayList<Bitmap>();
        for (int i = 1; i < 11; i++) {
            long time = length / 10 * i;
            a.add(getVideoThumbnail(time));
        }

        return a;
    }


}
