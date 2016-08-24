package com.libtop.weituR.activity.search.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.search.dto.CommentResult;
import com.libtop.weituR.base.BaseAdapter;
import com.libtop.weituR.utils.ContantsUtil;
import com.libtop.weituR.utils.DateUtil;
import com.libtop.weituR.utils.selector.MultiImageSelectorFragment;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

/**
 * <p>
 * Title: CommentAdapter.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * CreateTime：16/5/31
 * </p>
 *
 * @author 陆
 * @version common v1.0
 */
public class CommentAdapter extends BaseAdapter<CommentResult> {


    private OnReplyClickListener onReplyClickListener;


    public CommentAdapter(Context context, List<CommentResult> data, int resourceId, OnReplyClickListener listenner) {
        super(context, data, R.layout.item_list_2comment);
        this.onReplyClickListener = listenner;
    }

    @Override
    protected void newView(View convertView) {
        Holder holder = new Holder();
        holder.thumbImage = (ImageView) convertView.findViewById(R.id.img_head);
        holder.time = (TextView) convertView.findViewById(R.id.tv_time);
        holder.comment2 = (TextView) convertView.findViewById(R.id.tv_commnet2);
        holder.comment = (TextView) convertView.findViewById(R.id.tv_commnet);
        holder.tvReply = (TextView) convertView.findViewById(R.id.tv_reply);
        convertView.setTag(holder);
    }

    @Override
    protected void holderView(View convertView, CommentResult commentResult, final int position) {
        Holder holder = (Holder) convertView.getTag();
        if(commentResult.quotedComment != null && !TextUtils.isEmpty(commentResult.quotedComment.content)){
            holder.comment2.setVisibility(View.VISIBLE);
            SpannableStringBuilder builder= getOrangeStrBuilder(commentResult.quotedComment.username,commentResult.quotedComment.content);
            holder.comment2.setText(builder);
        }else {
            holder.comment2.setVisibility(View.GONE);
        }
        holder.time.setText(DateUtil.parseToDate(commentResult.timeline));
        SpannableStringBuilder builder2= getOrangeStrBuilder(commentResult.username,commentResult.content);
        holder.comment.setText(builder2);
        bindData(commentResult.uid, holder.thumbImage);
        holder.tvReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onReplyClickListener.onReplyTouch(v,position);
            }
        });
    }

    public interface OnReplyClickListener {
        void onReplyTouch(View v, int position);
    }

    void bindData(String uid, ImageView image) {

        String url = ContantsUtil.getAvatarUrl(uid);
        if (TextUtils.isEmpty(url))
            return;
        Picasso.with(mContext)
                .load(url)
                .transform(new CircleTransform())
                .error(R.drawable.head_image)
                .placeholder(R.drawable.head_image)
                .tag(MultiImageSelectorFragment.TAG)
                .fit()
                .centerCrop()
                .into(image);
    }

    private class Holder {
        ImageView thumbImage;
        TextView time, comment2, comment,tvReply;
    }

    private SpannableStringBuilder getOrangeStrBuilder(String first,String append){
        String builderStr = first + "：";
        SpannableStringBuilder builder = new SpannableStringBuilder(builderStr+append);

        //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
        ForegroundColorSpan orangeSpan = new ForegroundColorSpan(Color.parseColor("#EE5B21"));
        builder.setSpan(orangeSpan, 0, builderStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return builder;
    }

    private class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }

}
