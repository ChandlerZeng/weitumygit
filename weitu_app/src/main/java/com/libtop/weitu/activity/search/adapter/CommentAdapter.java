package com.libtop.weitu.activity.search.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.test.CommentBean;
import com.libtop.weitu.test.Comments;
import com.libtop.weitu.test.ReplyBean;
import com.libtop.weitu.utils.DateUtil;
import com.libtop.weitu.utils.selector.MultiImageSelectorFragment;
import com.libtop.weitu.viewadapter.CommonAdapter;
import com.libtop.weitu.viewadapter.ViewHolderHelper;
import com.libtop.weitu.widget.listview.ChangeListView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.w3c.dom.Comment;

import java.util.ArrayList;
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


    private OnCommentClickListener onCommentClickListener;
    private OnReplyClickListener onReplyClickListener;
    private OnReplyItemClickListener onReplyItemClickListener;
    private OnLikeClickListener onLikeClickListener;
    private CommentReplyAdapter mAdapter;

    private List<ReplyBean> replyList = new ArrayList<>();


    public CommentAdapter(Context context, List<Comments> data, OnReplyClickListener listenner,OnReplyItemClickListener itemClickListener,OnLikeClickListener likeClickListener
            ,OnCommentClickListener commentClickListener)
    {
        super(context, R.layout.item_list_comment, data);
        this.onCommentClickListener = commentClickListener;
        this.onReplyClickListener = listenner;
        this.onReplyItemClickListener = itemClickListener;
        this.onLikeClickListener = likeClickListener;
    }

    @Override
    public void convert(ViewHolderHelper helper, final Comments object, final int position) {
        ImageView headImage = helper.getView(R.id.img_head);
        RelativeLayout commentLayout1 = helper.getView(R.id.comment_layout1);
        LinearLayout commentLayout2 = helper.getView(R.id.comment_layout2);
        LinearLayout likeLayout = helper.getView(R.id.likeLayout);
        LinearLayout replyLayout = helper.getView(R.id.replyLayout);
        ChangeListView listView = helper.getView(R.id.list_reply);
        TextView tvUser = helper.getView(R.id.tv_user_name);
        TextView tvTime = helper.getView(R.id.tv_time);
        TextView tvcomment = helper.getView(R.id.tv_commnet1);
        final TextView tvcommentmore = helper.getView(R.id.tv_commnet_more);
        TextView tvLike = helper.getView(R.id.tv_like);
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
            }else {
                tvLike.setText("点赞");
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
                replyList = object.replys;
                if(replyList.size()>0){
                    commentLayout2.setVisibility(View.VISIBLE);
                    if(replyList.size()>5){
                        if(object.isExpanded){
                            mAdapter = new CommentReplyAdapter(context,R.layout.item_comment_reply_list,replyList);
                            tvcommentmore.setVisibility(View.VISIBLE);
                            tvcommentmore.setText("收起评论");
                        } else {
                            List<ReplyBean> replyBeans = replyList.subList(0,5);
                            mAdapter = new CommentReplyAdapter(context,R.layout.item_comment_reply_list,replyBeans);
                            tvcommentmore.setVisibility(View.VISIBLE);
                            tvcommentmore.setText("展开更多");
                        }
                    }else{
                        mAdapter = new CommentReplyAdapter(context,R.layout.item_comment_reply_list,replyList);
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
            replyLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onReplyClickListener.onReplyTouch(v, position);
                }
            });

            tvcomment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCommentClickListener.onCommentTouch(v,position,object);
                }
            });


            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if(object.user.uid.equals("1")){
                        onReplyItemClickListener.onReplyItemDeleted(view,position,object.replys.get(position));
                    }else {
                        onReplyItemClickListener.onReplyItemTouch(view, position, object.replys.get(position));
                    }
                }
            });

            likeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLikeClickListener.onLikeTouch(v,position, object);
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

    public interface OnReplyItemClickListener
    {
        void onReplyItemTouch(View v, int position,ReplyBean replyBean);
        void onReplyItemDeleted(View v,int position,ReplyBean replyBean);
    }

    public interface OnLikeClickListener
    {
        void onLikeTouch(View v, int position,Comments comment);
    }

    public interface OnCommentClickListener
    {
        void onCommentTouch(View v,int position,Comments comment);
    }


    void bindData(String url, ImageView image)
    {

//        String url = ContantsUtil.getAvatarUrl(uid);
        if (TextUtils.isEmpty(url))
        {
            return;
        }
        Picasso.with(context).load(url).transform(new CircleTransform()).error(R.drawable.head_image).placeholder(R.drawable.head_image).fit().centerCrop().into(image);
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
        private Context context;
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
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#47885D")), 0, userName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#47885D")), userName.length() + reply.length(), userName.length() + reply.length() + replyUserName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        }
    }

    public void removeSubItem(ReplyBean replyBean){
        replyList.remove(replyBean);
        notifyDataSetChanged();
    }

}
