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
import com.libtop.weitu.activity.main.dto.CommentDto;
import com.libtop.weitu.activity.main.dto.ReplyListDto;
import com.libtop.weitu.activity.search.CommentActivity;
import com.libtop.weitu.service.WTStatisticsService;
import com.libtop.weitu.utils.ContantsUtil;
import com.libtop.weitu.utils.DateUtil;
import com.libtop.weitu.utils.ImageLoaderUtil;
import com.libtop.weitu.viewadapter.CommonAdapter;
import com.libtop.weitu.viewadapter.ViewHolderHelper;
import com.libtop.weitu.widget.view.ListViewForScrollView;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 * Title: CommentAdapter.java
 * Created by Zeng 2016/9/16
 *
 * @version common v2.0
 */
public class CommentAdapter extends CommonAdapter<CommentDto>
{
    private ImageView headImage;
    private ImageView praiseIcon;
    private RelativeLayout commentLayout1;
    private LinearLayout commentLinearLayout;
    private LinearLayout commentLayout2;
    private LinearLayout likeLayout;
    private LinearLayout replyLayout;
    private ListViewForScrollView listView;
    private TextView tvUser;
    private TextView tvTime;
    private TextView tvcomment;
    private TextView tvcommentmore;
    private TextView tvLike;
    private TextView tvReply;

    private OnCommentListener onCommentListener;
    private CommentReplyAdapter mAdapter;

    public List<ReplyListDto> replyListDtos = new ArrayList<>();


    public CommentAdapter(Context context, List<CommentDto> data, OnCommentListener onCommentListener)
    {
        super(context, R.layout.item_list_comment, data);
        this.onCommentListener = onCommentListener;
    }


    @Override
    public void convert(ViewHolderHelper helper, final CommentDto object, final int position)
    {
        initCommentView(helper, object);
        initReplyView(helper, object);
        clickListener(object, position);

    }


    public void initCommentView(ViewHolderHelper helper, CommentDto object)
    {
        headImage = helper.getView(R.id.img_head);
        praiseIcon = helper.getView(R.id.icon_praise);
        commentLayout1 = helper.getView(R.id.comment_layout1);
        commentLinearLayout = helper.getView(R.id.comment_father_layout);
        likeLayout = helper.getView(R.id.likeLayout);
        replyLayout = helper.getView(R.id.replyLayout);
        tvUser = helper.getView(R.id.tv_user_name);
        tvTime = helper.getView(R.id.tv_time);
        tvcomment = helper.getView(R.id.tv_commnet1);
        tvLike = helper.getView(R.id.tv_like);
        tvReply = helper.getView(R.id.tv_reply);
        if (object != null)
        {
            commentLayout1.setVisibility(View.VISIBLE);
            bindData(ContantsUtil.getAvatarUrl(object.getUid()), headImage);
            tvUser.setText(object.getUsername());
            tvTime.setText(DateUtil.transformToShow(object.getTimeline()));
            if (object.praised == 0)
            {
                praiseIcon.setImageResource(R.drawable.icon_comment_unpraised);
            }
            else
            {
                praiseIcon.setImageResource(R.drawable.icon_comment_praised);
            }
            if (object.praises != 0)
            {
                tvLike.setText(object.praises + "");
            }
            else
            {
                tvLike.setText("点赞");
            }
            if (object.replies != 0)
            {
                tvReply.setText(object.replies + "");
            }
            else
            {
                tvReply.setText("回复");
            }
            if (object.getContent() != null && !TextUtils.isEmpty(object.getContent()))
            {
                tvcomment.setVisibility(View.VISIBLE);
                tvcomment.setText(object.getContent());

            }
            else
            {
                tvcomment.setVisibility(View.GONE);
            }

        }
        else
        {
            commentLayout1.setVisibility(View.GONE);
        }

    }


    public void initReplyView(ViewHolderHelper helper, CommentDto object)
    {
        commentLayout2 = helper.getView(R.id.comment_layout2);
        listView = helper.getView(R.id.list_reply);
        tvcommentmore = helper.getView(R.id.tv_commnet_more);

        if (object != null)
        {
            replyListDtos = object.replyList;
            if (replyListDtos.size() > 0)
            {
                commentLayout2.setVisibility(View.VISIBLE);
                if (replyListDtos.size() > 5)
                {
                    if (object.isExpanded)
                    {
                        mAdapter = new CommentReplyAdapter(context, R.layout.item_comment_reply_list, replyListDtos);
                        tvcommentmore.setVisibility(View.VISIBLE);
                        tvcommentmore.setText("收起评论");
                    }
                    else
                    {
                        List<ReplyListDto> replyBeans = replyListDtos.subList(0, 5);
                        mAdapter = new CommentReplyAdapter(context, R.layout.item_comment_reply_list, replyBeans);
                        tvcommentmore.setVisibility(View.VISIBLE);
                        tvcommentmore.setText("展开更多");
                    }
                }
                else
                {
                    mAdapter = new CommentReplyAdapter(context, R.layout.item_comment_reply_list, replyListDtos);
                    tvcommentmore.setVisibility(View.GONE);
                }
                listView.setAdapter(mAdapter);
            }
            else
            {
                commentLayout2.setVisibility(View.GONE);
            }
        }
    }


    public void clickListener(final CommentDto object, final int position)
    {
        replyLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (object.replyList != null && object.replyList.size() != 0)
                {
                    onCommentListener.onReplyTouch(v, position, object.replyList, object);
                }
                else
                {
                    ReplyListDto replyBean = new ReplyListDto();
                    List<ReplyListDto> replyBeans = new ArrayList<>();
                    onCommentListener.onReplyTouch(v, position, replyBeans, object);
                }
            }
        });

        commentLinearLayout.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                onCommentListener.onCommentContentLongClick(v, position, object);
                return true;
            }
        });

        commentLinearLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onCommentListener.onCommentContentClick(v, position, object);
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (CommentActivity.UID != null && object.replyList.get(position).uid.equals(CommentActivity.UID))
                {
                    onCommentListener.onReplyItemDeleted(view, position, object.replyList.get(position), object.replyList, object);   //TODO
                }
                else
                {
                    onCommentListener.onReplyItemTouch(view, position, object.replyList.get(position), object.replyList, object);
                }
            }
        });

        likeLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                onCommentListener.onLikeTouch(v, position, object);
            }
        });

        tvcommentmore.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!object.isExpanded)
                {

                    WTStatisticsService.onEvent(context, WTStatisticsService.EID_COMMENT_EXPAND_CLI);
                    object.isExpanded = true;
                    notifyDataSetChanged();
                }
                else
                {
                    WTStatisticsService.onEvent(context, WTStatisticsService.EID_COMMENT_COLLAPSE_CLI);
                    object.isExpanded = false;
                    notifyDataSetChanged();
                }
            }
        });

    }


    public interface OnCommentListener
    {
        void onReplyTouch(View v, int position, List<ReplyListDto> replyBeans, CommentDto object);

        void onReplyItemTouch(View v, int position, ReplyListDto replyBean, List<ReplyListDto> replyBeans, CommentDto object);

        void onReplyItemDeleted(View v, int position, ReplyListDto replyBean, List<ReplyListDto> replyBeans, CommentDto object);

        void onLikeTouch(View v, int position, CommentDto comment);

        void onCommentContentClick(View v, int position, CommentDto comment);

        void onCommentContentLongClick(View v, int position, CommentDto comment);

    }


    void bindData(String url, ImageView image)
    {

        if (TextUtils.isEmpty(url))
        {
            return;
        }
        ImageLoaderUtil.loadLogoImage(context, image, url);
    }


    public void setData(List<CommentDto> listResult)
    {
        this.datas = listResult;
        notifyDataSetChanged();
    }


    class CommentReplyAdapter extends CommonAdapter<ReplyListDto>
    {

        private List<ReplyListDto> replyBeans;
        private Context context;


        public CommentReplyAdapter(Context context, int itemLayoutId, List<ReplyListDto> replyLists)
        {
            super(context, R.layout.item_comment_reply_list, replyLists);
            replyListDtos = replyLists;
        }


        @Override
        public void convert(ViewHolderHelper helper, ReplyListDto object, int position)
        {
            if (object.content != null)
            {
                String user_name = object.username;
                String content = "  " + object.content;
                String reply_user_name;
                String reply;
                if (object.repliedUser != null && object.repliedUser.username != null)
                {
                    reply_user_name = object.repliedUser.username;
                    reply = " " + "回复" + " ";
                    SpannableString spannableString = getGreenStr(user_name, reply, reply_user_name, content);
                    helper.setText(R.id.sub_comment, spannableString);
                }
                else
                {
                    reply_user_name = "";
                    reply = "";
                    SpannableString spannableString = getGreenStr(user_name, reply, reply_user_name, content);
                    helper.setText(R.id.sub_comment, spannableString);
                }
            }
        }


        private SpannableString getGreenStr(String userName, String reply, String replyUserName, String content)
        {
            SpannableString spannableString = new SpannableString(userName + reply + replyUserName + content);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#47885D")), 0, userName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#47885D")), userName.length() + reply.length(), userName.length() + reply.length() + replyUserName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        }
    }


    public void removeSubItem(ReplyListDto replyBean, List<ReplyListDto> replyBeans, CommentDto object)
    {
        replyBeans.remove(replyBean);
        object.replies = object.replies - 1;
        notifyDataSetChanged();
    }


    public void replySubItem(ReplyListDto replyBean, List<ReplyListDto> replyBeans, CommentDto object)
    {
        replyBeans.add(replyBean);
        object.replies = object.replies + 1;
        if (replyBeans.size() == 1)
        {
            object.replyList.add(replyBean);
            mAdapter.notifyDataSetChanged();
        }
        notifyDataSetChanged();
    }
}
