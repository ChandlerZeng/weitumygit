package com.libtop.weitu.activity.user.adapter;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.search.dto.CommentResult;
import com.libtop.weitu.base.BaseAdapter;
import com.libtop.weitu.utils.ContantsUtil;
import com.libtop.weitu.utils.DateUtil;
import com.libtop.weitu.utils.selector.MultiImageSelectorFragment;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

import io.vov.vitamio.utils.Log;


/**
 * Created by LianTu on 2016/7/12.
 */
public class MyCommentAdapter extends BaseAdapter<CommentResult>
{


    private boolean visable = false;
    private boolean allCheck = false;


    public MyCommentAdapter(Context context, List<CommentResult> data)
    {
        super(context, data, R.layout.item_list_2mycomment);
    }


    @Override
    protected void newView(View convertView)
    {
        Holder holder = new Holder();
        holder.view = (View) convertView.findViewById(R.id.check_layout);
        holder.thumbImage = (ImageView) convertView.findViewById(R.id.img_head);
        holder.time = (TextView) convertView.findViewById(R.id.tv_time);
        holder.comment2 = (TextView) convertView.findViewById(R.id.tv_commnet2);
        holder.comment = (TextView) convertView.findViewById(R.id.tv_commnet);
        holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
        holder.checkMark = (CheckBox) convertView.findViewById(R.id.checkmark);
        convertView.setTag(holder);
    }


    @Override
    protected void holderView(View convertView, CommentResult commentResult, final int position)
    {
        Holder holder = (Holder) convertView.getTag();
        if (commentResult.quotedComment != null && !TextUtils.isEmpty(commentResult.quotedComment.content))
        {
            holder.comment2.setVisibility(View.VISIBLE);
            SpannableStringBuilder builder = getBlueStrBuilder(commentResult.quotedComment.username, commentResult.quotedComment.content);
            holder.comment2.setText(builder);
        }
        else
        {
            holder.comment2.setVisibility(View.GONE);
        }
        holder.checkMark.setChecked(allCheck);
        holder.time.setText(DateUtil.parseToDate(commentResult.timeline));
        SpannableStringBuilder builder2 = getBlueStrBuilder(commentResult.username, commentResult.content);
        holder.comment.setText(builder2);
        SpannableStringBuilder builder3 = getGreenStrBuilder(commentResult.typeName, commentResult.title);
        holder.tvTitle.setText(builder3);
        bindData(commentResult.uid, holder.thumbImage);
        if (visable)
        {
            holder.view.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.view.setVisibility(View.GONE);
        }
        holder.checkMark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                mData.get(position).ischecked = isChecked;
            }
        });
    }


    void bindData(String uid, ImageView image)
    {

        String url = ContantsUtil.getAvatarUrl(uid);
        if (TextUtils.isEmpty(url))
        {
            return;
        }
        Picasso.with(mContext).load(url).transform(new CircleTransform()).error(R.drawable.head_image).placeholder(R.drawable.head_image).tag(MultiImageSelectorFragment.TAG).fit().centerCrop().into(image);
    }


    public void setVisableView()
    {
        if (visable)
        {
            visable = false;
        }
        else
        {
            visable = true;
        }
        notifyDataSetChanged();
    }


    public void setAllView()
    {
        if (allCheck)
        {
            allCheck = false;
        }
        else
        {
            allCheck = true;
        }
        for (int i = 0; i < mData.size(); i++)
        {
            mData.get(i).ischecked = allCheck;
        }
        notifyDataSetChanged();
    }


    public String[] cleanView()
    {
        List<CommentResult> list = new ArrayList<CommentResult>();
        for (int i = 0; i < mData.size(); i++)
        {
            if (mData.get(i).ischecked)
            {
                Log.e("" + mData.get(i).ischecked);
                list.add(mData.get(i));
            }
        }
        String[] str = new String[list.size()];
        for (int i = 0; i < list.size(); i++)
        {
            str[i] = list.get(i).id;
        }
        allCheck = false;
        return str;
    }


    private class Holder
    {
        CheckBox checkMark;
        View view;
        ImageView thumbImage;
        TextView time, comment2, comment, tvTitle;
    }


    private SpannableStringBuilder getBlueStrBuilder(String first, String append)
    {
        String builderStr = first + "：";
        SpannableStringBuilder builder = new SpannableStringBuilder(builderStr + append);

        //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
        ForegroundColorSpan orangeSpan = new ForegroundColorSpan(Color.parseColor("#1185E7"));
        builder.setSpan(orangeSpan, 0, builderStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return builder;
    }


    private SpannableStringBuilder getGreenStrBuilder(String first, String append)
    {
        String builderStr = "【" + first + "】";
        SpannableStringBuilder builder = new SpannableStringBuilder(builderStr + append);

        //ForegroundColorSpan 为文字前景色，BackgroundColorSpan为文字背景色
        ForegroundColorSpan orangeSpan = new ForegroundColorSpan(Color.parseColor("#2DBE60"));
        builder.setSpan(orangeSpan, 0, builderStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return builder;
    }


    private class CircleTransform implements Transformation
    {
        @Override
        public Bitmap transform(Bitmap source)
        {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source)
            {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }


        @Override
        public String key()
        {
            return "circle";
        }
    }

}
