package com.libtop.weituR.activity.main.adapter;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.main.dto.VideoFolderBean;
import com.libtop.weituR.base.BaseAdapter;
import com.libtop.weituR.utils.selector.MultiImageSelectorFragment;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by LianTu on 2016/4/21.
 */
public class VideoListAdapter extends BaseAdapter<VideoFolderBean> {
    final int mGridWidth;
    private OnOptionImgClickListener mOptionImgClickListener;

    public VideoListAdapter(Context context, List<VideoFolderBean> data, OnOptionImgClickListener listenner) {
        super(context, data, R.layout.item_lv_video_folder);
        this.mOptionImgClickListener = listenner;
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
    protected void newView(View convertView) {
        Holder holder = new Holder();
        holder.title = (TextView) convertView.findViewById(R.id.tv_folder_title);
        holder.videoNum = (TextView) convertView.findViewById(R.id.tv_video_num);
        holder.authority = (TextView) convertView.findViewById(R.id.tv_authority);
        holder.imageButton = (ImageButton) convertView.findViewById(R.id.imgBtn_video_choice);
        holder.imgFolder = (ImageView) convertView.findViewById(R.id.img_folder);
        convertView.setTag(holder);
    }

    @Override
    protected void holderView(final View convertView, VideoFolderBean videoFolderBean, final int position) {
        Holder holder = (Holder) convertView.getTag();
        holder.title.setText(videoFolderBean.title);
        holder.videoNum.setText(videoFolderBean.mediaCount + "个视频");
        holder.authority.setText(videoFolderBean.categoriesName1);
        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOptionImgClickListener.onOptionImgTouch(v, position);

            }
        });
        bindData(videoFolderBean.cover, holder.imgFolder);

    }

    private static class Holder {
        TextView title, videoNum, authority;
        ImageButton imageButton;
        ImageView imgFolder;
    }

    public interface OnOptionImgClickListener {
        void onOptionImgTouch(View v, int position);
    }

    void bindData(String url, ImageView image) {

//        String a = "http://nt1.libtop.com/1/" + url + ".jpg";
//        ImageLoader.getInstance().init(
//                ImageLoaderConfiguration.createDefault(mContext));
//        ImageLoader.getInstance().displayImage(a, image);
        Picasso.with(mContext).invalidate(url);
        if (!TextUtils.isEmpty(url)){
            Picasso.with(mContext)
                    .load(url)
                    .error(R.drawable.video_cover)
                    .placeholder(R.drawable.video_cover)
                    .tag(MultiImageSelectorFragment.TAG)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .resize(mGridWidth + 20, mGridWidth)
                    .centerCrop()
                    .into(image);
        }
    }

}
