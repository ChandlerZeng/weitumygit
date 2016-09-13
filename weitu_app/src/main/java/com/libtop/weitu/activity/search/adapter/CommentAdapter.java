package com.libtop.weitu.activity.search.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.test.Comments;
import com.libtop.weitu.test.ReplyBean;
import com.libtop.weitu.utils.DateUtil;
import com.libtop.weitu.utils.selector.MultiImageSelectorFragment;
import com.libtop.weitu.viewadapter.CommonAdapter;
import com.libtop.weitu.viewadapter.ViewHolderHelper;
import com.libtop.weitu.widget.listview.ChangeListView;
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
public class CommentAdapter extends CommonAdapter<Comments>
{


    private OnReplyClickListener onReplyClickListener;
    private CommentReplyAdapter mAdapter;


    public CommentAdapter(Context context, List<Comments> data, OnReplyClickListener listenner)
    {
        super(context, R.layout.item_list_comment, data);
        this.onReplyClickListener = listenner;
    }

    @Override
    public void convert(ViewHolderHelper helper, final Comments object, final int position) {
        ImageView headImage = helper.getView(R.id.img_head);
        RelativeLayout commentLayout1 = helper.getView(R.id.comment_layout1);
        LinearLayout commentLayout2 = helper.getView(R.id.comment_layout2);
        ChangeListView listView = helper.getView(R.id.list_reply);
        TextView tvUser = helper.getView(R.id.tv_user_name);
        TextView tvTime = helper.getView(R.id.tv_time);
        TextView tvcomment = helper.getView(R.id.tv_commnet1);
        final TextView tvcommentmore = helper.getView(R.id.tv_commnet_more);
        TextView tvLike = helper.getView(R.id.tv_like);
//        tvLike.setVisibility(View.VISIBLE);
        TextView tvReply = helper.getView(R.id.tv_reply);
        if(object!=null){
            commentLayout1.setVisibility(View.VISIBLE);
            if(object.user.logo!=null){
                String url = object.user.logo;
                bindData(url,headImage);
            }
            tvUser.setText(object.user.name);
            tvTime.setText(DateUtil.parseToStringWithoutSS(object.t_create));
            if(object.count_praise!=0){
                tvLike.setText(object.count_praise+"");
//                helper.setText(R.id.tv_like, object.count_praise);
            }else {
                tvLike.setText("点赞");
//                helper.setText(R.id.tv_like,"点赞");
            }
            if(object.count_reply!=0){
                tvReply.setText(object.count_reply+"");
            }else {
                tvReply.setText("回复");
            }
            if (object.content != null && !TextUtils.isEmpty(object.content))
            {
                tvcomment.setVisibility(View.VISIBLE);
                tvcomment.setText(object.content);
                if(object.replys.size()>0){
                    commentLayout2.setVisibility(View.VISIBLE);
                    if(object.replys.size()>5){
                        if(object.isExpanded){
                            mAdapter = new CommentReplyAdapter(context,R.layout.item_comment_reply_list,object.replys);
                            tvcommentmore.setVisibility(View.VISIBLE);
                            tvcommentmore.setText("收起评论");
                        } else {
                            List<ReplyBean> replyBeans = object.replys.subList(0,5);
                            mAdapter = new CommentReplyAdapter(context,R.layout.item_comment_reply_list,replyBeans);
                            tvcommentmore.setVisibility(View.VISIBLE);
                            tvcommentmore.setText("展开更多");
                        }
                    }else{
                        mAdapter = new CommentReplyAdapter(context,R.layout.item_comment_reply_list,object.replys);
                        tvcommentmore.setVisibility(View.GONE);
                    }
                    listView.setAdapter(mAdapter);
                } else {
                    commentLayout2.setVisibility(View.GONE);
                }
            }
            else
            {
                tvcomment.setVisibility(View.GONE);
            }
            tvReply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onReplyClickListener.onReplyTouch(v, position);
                }
            });
            tvcommentmore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!object.isExpanded){
                        object.isExpanded = true;
                        notifyDataSetChanged();
                    } else {
                        object.isExpanded = false;
                        notifyDataSetChanged();
                    }
                }
            });
        }else {
            commentLayout1.setVisibility(View.GONE);
        }

    }

    public interface OnReplyClickListener
    {
        void onReplyTouch(View v, int position);
    }


    void bindData(String url, ImageView image)
    {

//        String url = ContantsUtil.getAvatarUrl(uid);
        if (TextUtils.isEmpty(url))
        {
            return;
        }
        Picasso.with(context).load(url).transform(new CircleTransform()).error(R.drawable.head_image).placeholder(R.drawable.head_image).tag(MultiImageSelectorFragment.TAG).fit().centerCrop().into(image);
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
    public void setData(List<Comments> listResult){
        this.datas = listResult ;
        notifyDataSetChanged();
    }
    class CommentReplyAdapter extends CommonAdapter<ReplyBean>{

        private List<ReplyBean> replyBeans;
        public CommentReplyAdapter(Context context, int itemLayoutId, List<ReplyBean> replyList) {
            super(context, R.layout.item_comment_reply_list, replyList);
            this.replyBeans = replyList;
        }

        @Override
        public void convert(ViewHolderHelper helper, ReplyBean object, int position) {
            if(object.content!=null){
                String user_name = object.user.name;
                String content = "  "+object.content;
                String reply_user_name;
                String reply;
                if(object.reply_user!=null && object.reply_user.name!=null&&!TextUtils.isEmpty(object.reply_user.name)){
                   reply_user_name = object.reply_user.name;
                    reply = " "+"回复"+" ";
                    SpannableString spannableString = getGreenStr(user_name, reply, reply_user_name, content);
                    helper.setText(R.id.sub_comment,spannableString);
                } else {
                    reply_user_name = "";
                    reply = "";
                    SpannableString spannableString = getGreenStr(user_name,reply,reply_user_name,content);
                    helper.setText(R.id.sub_comment,spannableString);
                }
            }
        }

        private SpannableString getGreenStr(String userName,String reply ,String replyUserName, String content)
        {

            SpannableString spannableString = new SpannableString(userName + reply + replyUserName + content);
//            if(reply!=null && !TextUtils.isEmpty(reply) && replyUserName!=null &&  !TextUtils.isEmpty(replyUserName)){
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#47885D")), 0, userName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#47885D")), userName.length() + reply.length(), userName.length() + reply.length() + replyUserName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//            } else {
//                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#47885D")), 0, userName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            }

            return spannableString;
        }
    }

    public void setListViewHeight(ListView listView, int size, boolean expanded,TextView textView)
    {
        if (size == 0)
        {
            listView.setVisibility(View.GONE);
        }
        else
        {
            listView.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams = listView.getLayoutParams();
            //获取ListView每个Item高度
            View item = mAdapter.getView(0, null, listView);
            item.measure(0, 0);
            int height = item.getMeasuredHeight();
            if (size > 0 & size <= 5)
            {
                layoutParams.height = size * height;
                textView.setVisibility(View.GONE);
            }
            else
            {
                if (expanded)
                {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText("收起评论");
                    layoutParams.height = size * height;
                }
                else
                {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText("展开更多");
                    layoutParams.height = 5 * height;
                }
            }
            listView.setLayoutParams(layoutParams);
        }

    }

}
