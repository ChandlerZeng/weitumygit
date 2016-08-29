package com.libtop.weitu.activity.main.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.main.dto.DocBean;
import com.libtop.weitu.base.BaseAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/4/25 0025.
 */
public class UploadDocAdapter extends BaseAdapter<DocBean> {
    public Context mContext;
    private OnOptionImgClickListener mOptionImgClickListener;
    public List<ProgressBar> pView = new ArrayList<ProgressBar>();

    public UploadDocAdapter(Context context, List<DocBean> mlist,UploadDocAdapter.OnOptionImgClickListener optionImgClickListener) {
        super(context, mlist,R.layout.item_doc_upload);
        this.mOptionImgClickListener = optionImgClickListener;
    }

    @Override
    protected void newView(View convertView) {
        Holder holder = new Holder();
        holder.uploadProgress = (ProgressBar) convertView.findViewById(R.id.pgsBar);
        holder.view = (View) convertView.findViewById(R.id.go_pro);
        holder.oneView = (TextView) convertView.findViewById(R.id.one);
        holder.twoView = (TextView) convertView.findViewById(R.id.two);
        holder.threeView = (TextView) convertView.findViewById(R.id.three);
        holder.imageButton = (ImageButton) convertView.findViewById(R.id.imgBtn_video_choice);
        holder.imageView = (ImageView) convertView.findViewById(R.id.see);
        convertView.setTag(holder);
    }

    @Override
    protected void holderView(View convertView, DocBean docBean, final int position) {
        Holder holder = (Holder) convertView.getTag();
        pView.add(holder.uploadProgress);

        holder.uploadProgress.setProgress(docBean.progress);
        holder.oneView.setText(docBean.title);
        long duration_temp = docBean.uploadTime;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String str = sdf.format(new Date(duration_temp));
        holder.twoView.setText(str);
        holder.threeView.setText(docBean.stateString);
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
    }

    static class Holder {
        public View view;
        public ProgressBar uploadProgress;
        public TextView oneView, twoView, threeView;
        public ImageButton imageButton;
        public ImageView imageView;
    }

    public interface OnOptionImgClickListener {
        void onOptionImgTouch(View v, int position);

        void onImageTouch(View v, int position);

        void onUpload(View v, int position);
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
}
