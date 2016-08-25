package com.libtop.weitu.activity.main.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.main.dto.VideoBean;
import com.libtop.weitu.utils.TransformUtil;
import com.libtop.weitu.utils.selector.MultiImageSelectorFragment;
import com.squareup.picasso.Picasso;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/4/25 0025.
 */
public class UploadAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    public Context mContext;
    public List<VideoBean> mlist;
    private OnOptionImgClickListener mOptionImgClickListener;
    public List<ProgressBar> pView = new ArrayList<ProgressBar>();
    progressView holder;
    final int mGridWidth;

    public UploadAdapter(Context context, List<VideoBean> list, OnOptionImgClickListener listenner) {
        this.mOptionImgClickListener = listenner;
        this.mlist = list;
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        int width = 0;
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            wm.getDefaultDisplay().getSize(size);
            width = size.x;
        } else {
            width = wm.getDefaultDisplay().getWidth();
        }
        mGridWidth = width / 5;
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object getItem(int position) {
        return mlist.get(position);
    }

    public void setData(List<VideoBean> list) {
        this.mlist = list;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.upload_adapter, null);
            holder = new progressView();
            holder.uploadProgress = (ProgressBar) convertView.findViewById(R.id.pgsBar);
            holder.view = (View) convertView.findViewById(R.id.go_pro);
            holder.video = (View) convertView.findViewById(R.id.see);
            holder.oneView = (TextView) convertView.findViewById(R.id.one);
            holder.twoView = (TextView) convertView.findViewById(R.id.two);
            holder.threeView = (TextView) convertView.findViewById(R.id.three);
            holder.video_duration = (TextView) convertView.findViewById(R.id.video_duration);
            holder.video_size = (TextView) convertView.findViewById(R.id.video_size);
            holder.imageButton = (ImageButton) convertView.findViewById(R.id.imgBtn_video_choice);
            holder.imageView = (ImageView) convertView.findViewById(R.id.see);
            convertView.setTag(holder);
        } else {
            holder = (progressView) convertView.getTag();
        }
        pView.add(holder.uploadProgress);

        long duration_temp2 = mlist.get(position).videDduration;
        String hms = cal(duration_temp2);
//        long seconds = (duration_temp2 % (1000 * 60)) / 1000;
//        long hours = (duration_temp2 / (1000 * 60 * 60));
//        long minutes = (duration_temp2 % (1000 * 60 * 60)) / (1000 * 60);
//        if (duration_temp2 < 1000 * 60 * 60) {
//            hms = String.format("%02d:%02d", minutes, seconds);
//        } else {
//            hms = String.format("%02d:%02d", hours, minutes);
//        }
        holder.uploadProgress.setProgress(mlist.get(position).progress);
        holder.video_size.setText(TransformUtil.bytes2kb(mlist.get(position).videoSize));
        holder.oneView.setText(mlist.get(position).titleChange + ".mp4");
        long duration_temp = mlist.get(position).createTime;
        String str;
        if (duration_temp == 0) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
            str = formatter.format(curDate);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            str = sdf.format(new Date(duration_temp));
        }
        holder.twoView.setText(str);
        holder.threeView.setText(mlist.get(position).state);
        holder.video_duration.setText(hms);
//        if (mlist.get(position).thumbPath == null || mlist.get(position).thumbPath.equals(""))
//            holder.imageView.setImageResource(R.drawable.default_image);
//        else {
//            Bitmap bitmap = getLoacalBitmap(mlist.get(position).thumbPath);
//            if (bitmap == null)
//                holder.imageView.setImageResource(R.drawable.default_image);
//            else
//                holder.imageView.setImageBitmap(bitmap);
//        }

        bindData(mlist.get(position).coverUrl, holder.imageView, mlist.get(position).filePath);
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOptionImgClickListener.onOptionImgTouch(v, position);

            }
        });
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOptionImgClickListener.onUpload(v, position);

            }
        });
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOptionImgClickListener.onImageTouch(v, position);

            }
        });
        return convertView;
    }

    class progressView {
        public View view, video;
        public ProgressBar uploadProgress;
        public TextView oneView, twoView, threeView, video_duration, video_size;
        public ImageButton imageButton;
        public ImageView imageView;
    }

    public interface OnOptionImgClickListener {
        void onOptionImgTouch(View v, int position);

        void onImageTouch(View v, int position);

        void onUpload(View v, int position);
    }

    /**
     * 加载本地图片
     * http://bbs.3gstdy.com
     *
     * @param url
     * @return
     */
    public Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String cal(long msecond) {
        int second = (int) msecond;
        int h = 0;
        int d = 0;
        int s = 0;
        int temp = second % 3600;
        if (second > 3600) {
            h = second / 3600;
            if (temp != 0) {
                if (temp > 60) {
                    d = temp / 60;
                    if (temp % 60 != 0) {
                        s = temp % 60;
                    }
                } else {
                    s = temp;
                }
            }
        } else {
            d = second / 60;
            if (second % 60 != 0) {
                s = second % 60;
            }
        }

        return d + ":" + s;
    }

    void bindData(String url, ImageView image, String fileurl) {
        Bitmap bitmap1 = ThumbnailUtils.createVideoThumbnail(fileurl, MediaStore.Video.Thumbnails.MICRO_KIND);
        String a = url;
        if (bitmap1 != null) {
            image.setImageBitmap(bitmap1);

        } else {
            if (a == null || a.length() == 0)
                return;
            Picasso.with(mContext)
                    .load(a)
                    .placeholder(R.drawable.default_image)
                    .tag(MultiImageSelectorFragment.TAG)
                    .resize(mGridWidth + 20, mGridWidth)
                    .centerCrop()
                    .into(image);
        }
    }

}
