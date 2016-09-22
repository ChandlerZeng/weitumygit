package com.libtop.weitu.activity.search;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.comment.CommentDetailActivity;
import com.libtop.weitu.activity.search.adapter.CommentAdapter;
import com.libtop.weitu.activity.search.dto.CommentNeedDto;
import com.libtop.weitu.activity.search.dto.CommentResult;
import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.eventbus.MessageEvent;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.test.CommentBean;
import com.libtop.weitu.test.Comments;
import com.libtop.weitu.test.Reply;
import com.libtop.weitu.test.ReplyBean;
import com.libtop.weitu.utils.ContantsUtil;
import com.libtop.weitu.utils.JsonUtil;
import com.libtop.weitu.utils.ListViewUtil;
import com.libtop.weitu.utils.selector.utils.AlertDialogUtil;
import com.libtop.weitu.utils.selector.view.MyAlertDialog;
import com.libtop.weitu.widget.NetworkLoadingLayout;
import com.libtop.weitu.widget.listview.XListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;


public class CommentActivity extends BaseActivity implements CommentAdapter.OnCommentListener,NetworkLoadingLayout.OnRetryClickListner
{

    @Bind(R.id.edit_comment)
    EditText editText;
    @Bind(R.id.list_comment)
    XListView xListView;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.sub_title)
    TextView subTitle;
    @Bind(R.id.networkloadinglayout)
    NetworkLoadingLayout networkLoadingLayout;

    private CommentAdapter commentAdapter;

    private List<CommentResult> list;

    private CommentNeedDto commentNeedDto;

    private boolean isReply = false;
    private boolean isItemReply = false;
    private String cid;

    private CommentBean commentBean; //TODO
    private List<Comments> commentsList = new ArrayList<>(); //TODO

    private int mCurPage = 1;
    private boolean hasData = true;
    private boolean isFirstIn = true;
    private boolean isRefreshed = false;

    private HashMap<String, Object> map = new HashMap<>();
    private HashMap<String, Object> map2 = new HashMap<>();

    private ReplyBean replyItem;
    private Comments comments;
    private List<ReplyBean> replyItems;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setInjectContentView(R.layout.activity_comment);
        EventBus.getDefault().register(this);
        initView();
    }

    private void initView(){
        String json = getIntent().getStringExtra("CommentNeedDto");
        commentNeedDto = JsonUtil.fromJson(json, new TypeToken<CommentNeedDto>()
        {
        }.getType());

        if (commentNeedDto.title!=null && !TextUtils.isEmpty(commentNeedDto.title))
        {
            subTitle.setText(commentNeedDto.title);
        }
        if (isFirstIn)
        {
            isFirstIn = false;
            networkLoadingLayout.showLoading();
            getCommentList();
        }
        ListViewUtil.addPaddingHeader(mContext,xListView);
        xListView.setPullLoadEnable(false);

        xListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                mCurPage = 1;
                isRefreshed = true;
                getCommentList();
            }

            @Override
            public void onLoadMore() {
                if (hasData) {
                    getCommentList();
                }
            }
        });
        title.setText("评论");
//        getData(); TODO
        commentAdapter = new CommentAdapter(this, commentsList, this);
        xListView.setAdapter(commentAdapter);
        networkLoadingLayout.setOnRetryClickListner(this);
    }


    private void getCommentList()
    {
        //  http://192.168.0.9/resource/comment/list private
        //  http://115.28.189.104/resource/comment/list public
        Map<String, Object> map = new HashMap<>();
        String api = "/resource/comment/list";
        map.put("page", mCurPage);
        HttpRequest.newLoad(ContantsUtil.API_FAKE_HOST_PUBLIC + api, map).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (mCurPage > 1) {

                } else if (!isRefreshed) {
                    networkLoadingLayout.showLoadFailAndRetryPrompt();
                }
            }


            @Override
            public void onResponse(String json, int id) {
                if (!TextUtils.isEmpty(json)) {
                    xListView.stopRefresh();
                    if (mCurPage == 1) {
                        networkLoadingLayout.dismiss();
                    }
                    try {
                        Gson gson = new Gson();
                        CommentBean data = gson.fromJson(json, new TypeToken<CommentBean>() {
                        }.getType());
                        commentsList.clear();
                        if (data.comments != null) {
                            commentsList.addAll(data.comments);
                        }
                        if (commentsList.size() < 20) {
                            hasData = false;
                            xListView.setPullLoadEnable(false);
                        } else {
                            hasData = true;
                            xListView.setPullLoadEnable(true);
                        }
                        if (commentsList.size() == 0 && mCurPage == 1) {
                            networkLoadingLayout.showEmptyPrompt();
                        }
                        mCurPage++;
                        commentAdapter.setData(commentsList);
                        editText.setText("");
                        editText.setHint("发表评论");
                        isReply = false;
                        isItemReply = false;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    @Nullable
    @OnClick({R.id.back_btn, R.id.commit})
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.back_btn:
                onBackPressed();
                break;
            case R.id.commit:
                sendComment(v);
                break;
        }
    }


    private void sendComment(View v)
    {
        String str = editText.getText().toString().trim();
        if (str == null || str.length() == 0)
        {
            Toast.makeText(CommentActivity.this, "评论不能为空!", Toast.LENGTH_SHORT).show();
            return;
        }
        hideKeyBoard(v);
        if(isReply && isItemReply)
        {
            String cid = map2.get("cid").toString();
            String replyUid = map2.get("reply_uid").toString();
            putItemReply(cid, replyUid,str);
        }
        else if (isReply)
        {
            putReply(str);
        }
        else
        {
            putComment(str);
        }
    }
    private void putReply(String content) //TODO
    {
        showLoding();
        String api = "resource/comment/reply";
        OkHttpUtils.get().url(ContantsUtil.API_FAKE_HOST_PUBLIC + "/" + api)
//                .addParams("reply_uid", reply_uid)
                .addParams("content", content)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }


                    @Override
                    public void onResponse(String json, int id) {
                        if (!TextUtils.isEmpty(json)) {
                            dismissLoading();
                            Toast.makeText(CommentActivity.this,"回复评论成功",Toast.LENGTH_SHORT).show();
                            try {
                                Gson gson = new Gson();
                                Reply data = gson.fromJson(json, new TypeToken<Reply>() {
                                }.getType());
                                if (data.reply != null) {
                                    commentAdapter.replySubItem(data.reply, replyItems, comments);
                                }
                                editText.setText("");
                                editText.setHint("发表评论");
                                isReply = false;
                                isItemReply = false;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(CommentActivity.this,"回复评论失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void putComment(String content)
    {
        //  http://115.28.189.104/resource/comment/add
        showLoding();
        String api = "resource/comment/add";
        Map<String, Object> map = new HashMap<>();
        map.put("content",content);
        OkHttpUtils.get().url(ContantsUtil.API_FAKE_HOST_PUBLIC + "/" + api)
                .addParams("content", content)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }


                    @Override
                    public void onResponse(String json, int id) {
                        if (!TextUtils.isEmpty(json)) {
                            dismissLoading();
                            Toast.makeText(CommentActivity.this,"评论成功",Toast.LENGTH_SHORT).show();;
                            try {
                                Gson gson = new Gson();
                                CommentBean data = gson.fromJson(json, new TypeToken<CommentBean>() {
                                }.getType());
                                if (data.comment != null) {
                                    commentsList.add(0, data.comment);
                                }
                                commentAdapter.setData(commentsList);
                                editText.setText("");
                                editText.setHint("发表评论");
                                xListView.setSelection(1);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                });
    }

    @Override
    public void onReplyTouch(View v, int position,List<ReplyBean> replyBeans,Comments object) //TODO
    {
        Comments commentResult = commentsList.get(position);
//        replyItem = replyBean;
        replyItems = replyBeans;
        comments = object;
        isReply = true;
        if (commentResult.content != null && !TextUtils.isEmpty(commentResult.content))
        {
            String cid = commentResult.cid;
            String uid = commentResult.uid;
            map.put("reply_uid",uid);
            editText.requestFocus();
            String first = "回复";
            SpannableStringBuilder spannableString = getGreenStrBuilder(first,commentResult.user.name);
            editText.setHint(spannableString);
//            editText.setSelection(spannableString.length());
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
        else
        {
            editText.requestFocus();
        }
    }

    private SpannableStringBuilder getGreenStrBuilder(String first, String append)
    {
        String builderAppend = append + " ";
        SpannableStringBuilder builder = new SpannableStringBuilder(first + builderAppend);

        ForegroundColorSpan greenSpan = new ForegroundColorSpan(Color.parseColor("#47885D"));
        builder.setSpan(greenSpan, first.length(), first.length() + builderAppend.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }


    private void deleteReplyComment(String cid, final ReplyBean replyBean, final List<ReplyBean> replyBeans,final Comments object){
        showLoding();
        String api = "resource/comment/del";
        OkHttpUtils.get().url(ContantsUtil.API_FAKE_HOST_PUBLIC + "/" + api)
                .addParams("cid", cid)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }


                    @Override
                    public void onResponse(String json, int id) {
                        if (!TextUtils.isEmpty(json)) {
                            Toast.makeText(CommentActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                            commentAdapter.removeSubItem(replyBean, replyBeans, object);
                            dismissLoading();
                        }
                    }
                });
    }

    private void deleteComment(String cid, final Comments comments){
        showLoding();
        String api = "resource/comment/del";
        OkHttpUtils.get().url(ContantsUtil.API_FAKE_HOST_PUBLIC + "/" + api)
                .addParams("cid", cid)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }


                    @Override
                    public void onResponse(String json, int id) {
                        if (!TextUtils.isEmpty(json)) {
                            Toast.makeText(CommentActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                            commentsList.remove(comments);
                            commentAdapter.notifyDataSetChanged();
                            dismissLoading();
                        }
                    }
                });
    }

    private void putItemReply(String cid,String replyUid,String content){
        showLoding();
        String api = "resource/comment/reply";
        OkHttpUtils.get().url(ContantsUtil.API_FAKE_HOST_PUBLIC + "/" + api)
                .addParams("reply_uid", replyUid)
                .addParams("content", content)
                .build()
                .execute(new StringCallback(){
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }


                    @Override
                    public void onResponse(String json, int id) {
                        if (!TextUtils.isEmpty(json)) {
                            dismissLoading();
                            Toast.makeText(CommentActivity.this,"回复评论成功",Toast.LENGTH_SHORT).show();
                            try {
                                Gson gson = new Gson();
                                Reply data = gson.fromJson(json, new TypeToken<Reply>() {
                                }.getType());
                                if (data.reply != null) {
//                                    replyItems.add(0,data);
                                    commentAdapter.replySubItem(data.reply, replyItems,comments);
                                }
                                editText.setText("");
                                editText.setHint("发表评论");
                                isReply = false;
                                isItemReply = false;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(CommentActivity.this,"回复评论失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onReplyItemTouch(View v, final int position, final ReplyBean replyBean,final List<ReplyBean> replyBeans,Comments object) {

        String cid =replyBean.cid;
        String replyUid =String.valueOf(replyBean.user.uid);
        replyItem = replyBean;
        replyItems = replyBeans;
        comments = object;
//        String replyUid =String.valueOf(replyBean.reply_uid);
            if (replyBean.content != null)
            {
                isReply = true ;
                isItemReply = true ;
                map2.put("cid",cid);
                map2.put("reply_uid",replyUid);
                editText.requestFocus();
                String first = "回复";
                SpannableStringBuilder spannableString = getGreenStrBuilder(first,replyBean.user.name);
                editText.setHint(spannableString);
//                editText.setSelection(spannableString.length());
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
            else
            {
                editText.requestFocus();
        }
    }

    @Override
    public void onReplyItemDeleted(View v, int position, final ReplyBean replyBean,final List<ReplyBean> replyBeans, final Comments object) {
        String title = "您确定要删除？";
        final AlertDialogUtil dialog = new AlertDialogUtil();
        dialog.showDialog(CommentActivity.this, title, "确定", "取消", new MyAlertDialog.MyAlertDialogOnClickCallBack() {
            @Override
            public void onClick() {
                deleteReplyComment(replyBean.cid, replyBean, replyBeans, object);
            }
        }, null);
    }

    private void likeClicked(String cid, final Comments comments){
        String api = "resource/comment/praise";
        OkHttpUtils.get().url(ContantsUtil.API_FAKE_HOST_PUBLIC + "/" + api)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }


                    @Override
                    public void onResponse(String json, int id) {
                        if (!TextUtils.isEmpty(json)) {
                            //   showToast("没有相关数据");
                            dismissLoading();
                            comments.count_praise = comments.count_praise + 1;
                            comments.my_praise = 1;
                            Toast.makeText(mContext, "已赞", Toast.LENGTH_SHORT).show();
                            commentAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void likeCancelled(String cid,final Comments comments){
        String api = "resource/comment/unpraise";
        OkHttpUtils.get().url(ContantsUtil.API_FAKE_HOST_PUBLIC + "/" + api)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }


                    @Override
                    public void onResponse(String json, int id) {
                        if (!TextUtils.isEmpty(json)) {
                            //   showToast("没有相关数据");
                            dismissLoading();
                            comments.count_praise = comments.count_praise - 1;
                            comments.my_praise = 0;
                            Toast.makeText(mContext, "已取消赞", Toast.LENGTH_SHORT).show();
                            commentAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    public void onLikeTouch(View v, int position, Comments comment) {
        String cid = String.valueOf(comment.cid);
        if(comment.my_praise==0){
            likeClicked(cid,comment);
        }else{
            likeCancelled(cid,comment);
        }
    }

    @Override
    public void onCommentContentClick(View v, int position, Comments comment) {
        Bundle bundle = new Bundle();
        bundle.putString("cid", comment.cid);
        bundle.putInt("position", position);
        startForResult(bundle, 200, CommentDetailActivity.class);
//        startActivity(bundle, CommentDetailActivity.class);
    }

    @Override
    public void onCommentContentLongClick(View v, final int position, Comments comment) {
        if (commentsList.get(position ).uid.equals("1")) {
            String title = "您确定要删除？";
            final AlertDialogUtil dialog = new AlertDialogUtil();
            dialog.showDialog(CommentActivity.this, title, "确定", "取消", new MyAlertDialog.MyAlertDialogOnClickCallBack() {
                @Override
                public void onClick() {
                    deleteComment(cid, commentsList.get(position));
                }
            }, null);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 200 && resultCode ==300){

        }
    }

    public void replaceComment(int position,Comments comments){
        commentsList.remove(position);
        commentsList.add(position, comments);
        commentAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(MessageEvent event)
    {
        if(event.message.getString("isFromComment")!=null && event.message.getString("isFromComment").equals("true")){
            Bundle bundle = event.message;
            Comments comments = (Comments) bundle.getSerializable("comments");
            int position = bundle.getInt("position");
            Boolean isCommentUpdate = bundle.getBoolean("isCommentUpdate",false);
            if (isCommentUpdate)
            {
                replaceComment(position, comments);
            }
        }
    }


    @Override
    public void onDestroy()
    {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onRetryClick(View v) {
        mCurPage = 1;
        getCommentList();
    }
}
