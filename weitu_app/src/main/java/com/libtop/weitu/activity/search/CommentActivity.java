package com.libtop.weitu.activity.search;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.comment.CommentDetailActivity;
import com.libtop.weitu.activity.search.adapter.CommentAdapter;
import com.libtop.weitu.activity.search.adapter.CommentAdapter.OnReplyItemClickListener;
import com.libtop.weitu.activity.search.dto.CommentNeedDto;
import com.libtop.weitu.activity.search.dto.CommentResult;
import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.http.MapUtil;
import com.libtop.weitu.http.WeituNetwork;
import com.libtop.weitu.test.CommentBean;
import com.libtop.weitu.test.Comments;
import com.libtop.weitu.test.HttpRequestTest;
import com.libtop.weitu.test.ReplyBean;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.ContantsUtil;
import com.libtop.weitu.utils.JsonUtil;
import com.libtop.weitu.utils.selector.utils.AlertDialogUtil;
import com.libtop.weitu.utils.selector.view.MyAlertDialog;
import com.libtop.weitu.widget.listview.RemakeXListView;
import com.libtop.weitu.widget.listview.XListView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class CommentActivity extends BaseActivity implements CommentAdapter.OnReplyClickListener,OnReplyItemClickListener,CommentAdapter.OnLikeClickListener,CommentAdapter.OnCommentClickListener
{

    @Bind(R.id.edit_comment)
    EditText editText;
    @Bind(R.id.list_comment)
    RemakeXListView xListView;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.sub_title)
    TextView subTitle;
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
    private HashMap<String, Object> map = new HashMap<>();
    private HashMap<String, Object> map2 = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setInjectContentView(R.layout.activity_comment);
        initView();
    }

    private void initView(){
        String json = getIntent().getStringExtra("CommentNeedDto");
        commentNeedDto = JsonUtil.fromJson(json, new TypeToken<CommentNeedDto>()
        {
        }.getType());

        if (!TextUtils.isEmpty(commentNeedDto.title))
        {
            subTitle.setText(commentNeedDto.title);
        }
        xListView.setPullLoadEnable(false);
        xListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
//                    onBackPressed();
                }
            }
        });

        xListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if(commentsList.get(position-1).uid.equals("1")){
                    String title = "您确定要删除？";
                    final AlertDialogUtil dialog = new AlertDialogUtil();
                    dialog.showDialog(CommentActivity.this, title, "确定", "取消", new MyAlertDialog.MyAlertDialogOnClickCallBack()
                    {
                        @Override
                        public void onClick()
                        {
                            deleteComment(cid,commentsList.get(position));
                        }
                    }, null);
                }
                return false;
            }
        });

        xListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                mCurPage = 1;
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
        getCommentList();
        commentAdapter = new CommentAdapter(this, commentsList, this,this,this,this);
        xListView.setAdapter(commentAdapter);
    }


    //    影音，书本，文档，图库的评论列表接口
    //    http://weitu.bookus.cn/comment/list.json?text={"uid":"565bea2c984ec06f56befda3","tid":"563c69b4984e338019914a66","page":1,"method":"comment.list"}
    private void getData()
    {
        showLoding();
        HashMap<String, Object> map = new HashMap<>();
        map.put("tid", commentNeedDto.tid);
        map.put("page", 1);
        map.put("method", "comment.list");
        map.put("uid", mPreference.getString(Preference.uid));
        String[] arrays = MapUtil.map2Parameter(map);
        subscription = WeituNetwork.getWeituApi().getComment(arrays[0], arrays[1], arrays[2]).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<CommentResult>>()
        {
            @Override
            public void onCompleted()
            {
                Log.w("guanglog", "completed");
            }


            @Override
            public void onError(Throwable e)
            {
                Log.w("guanglog", "error + " + e);
            }


            @Override
            public void onNext(List<CommentResult> commentResults)
            {
                dismissLoading();
                list.clear();
                list.addAll(commentResults);
                commentAdapter.setData(commentsList);
                commentAdapter.notifyDataSetChanged();
                editText.setText("");
                isReply=false;
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
        if(isItemReply && isReply)
        {
            String cid = map2.get("cid").toString();
            String replyUid = map2.get("reply_uid").toString();
            putItemReply(cid, replyUid,str);
        }
        else if (isReply)
        {
            String cid = map.get("cid").toString();
            putReply(str,cid);
        }
        else
        {
            putComment(str);
        }
        xListView.setSelection(1);
    }
    private void putReply(String content,String cid) //TODO
    {
        showLoding();
        String api = "resource/comment/reply";
        String uid = 1+"";
        String rid = 1+"";
        OkHttpUtils.get().url(ContantsUtil.API_FAKE_HOST_PUBLIC + "/" + api)
                .addParams("uid",uid)
                .addParams("rid", rid)
                .addParams("cid", cid)
                .addParams("content", content)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }


                    @Override
                    public void onResponse(String json, int id) {
                        if (!TextUtils.isEmpty(json)) {
                            //   showToast("没有相关数据");
                            getCommentList();
                            return;
                        } else {
                            dismissLoading();
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
                            showToast("评论成功");
                            try {
                                Gson gson = new Gson();
                                CommentBean data = gson.fromJson(json, new TypeToken<CommentBean>() {
                                }.getType());
                                if (data.comment != null) {
                                    commentsList.add(0,data.comment);
                                }
                                commentAdapter.setData(commentsList);
                                editText.setText("");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                });
    }

    @Override
    public void onReplyTouch(View v, int position) //TODO
    {
        Comments commentResult = commentsList.get(position);
        isReply = true;
        if (commentResult.content != null && !TextUtils.isEmpty(commentResult.content))
        {
            String cid = commentResult.cid;
            map.put("cid",cid);
            editText.requestFocus();
            String first = "回复";
            SpannableStringBuilder spannableString = getGreenStrBuilder(first,commentResult.user.name);
            editText.setText(spannableString);
            editText.setSelection(spannableString.length());
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
        else
        {
//            cid = commentResult.id;
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
    private void getCommentList()
    {
        //  http://192.168.0.9/resource/comment/list private
        //  http://115.28.189.104/resource/comment/list public
        showLoding();
        Map<String, Object> map = new HashMap<>();
        String api = "/resource/comment/list";
        map.put("page", mCurPage);
        HttpRequest.newLoad(ContantsUtil.API_FAKE_HOST_PUBLIC + api, map).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e("CommentActivity", e.getMessage());
            }


            @Override
            public void onResponse(String json, int id) {
                if (!TextUtils.isEmpty(json)) {
                    xListView.stopRefresh();
                    dismissLoading();
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
                        mCurPage++;
                        commentAdapter.setData(commentsList);
                        editText.setText("");
                        isReply = false;
                        isItemReply = false;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void deleteReplyComment(String cid, final ReplyBean replyBean){
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
                            showToast("删除成功");
                            commentAdapter.removeSubItem(replyBean);
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
                            showToast("删除成功");
                            commentAdapter.removeItem(comments);
                            dismissLoading();
                        }
                    }
                });
    }

    private void putItemReply(String cid,String replyUid,String content){
        showLoding();
        String api = "resource/comment/reply";
        OkHttpUtils.get().url(ContantsUtil.API_FAKE_HOST_PUBLIC + "/" + api)
                .addParams("reply_uid",replyUid)
                .addParams("content", content)
                .build()
                .execute(new StringCallback(){
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }


                    @Override
                    public void onResponse(String json, int id) {
                        if (!TextUtils.isEmpty(json)) {
                            showToast("回复评论成功");
                            getCommentList();
                            return;
                        } else {
                            dismissLoading();
                        }
                    }
                });
    }

    @Override
    public void onReplyItemTouch(View v, final int position, final ReplyBean replyBean) {
        String userUid = String.valueOf(replyBean.user.uid);
        final String cid =String.valueOf(replyBean.cid);
        String replyUid =String.valueOf(replyBean.reply_uid);
        if(userUid.equals("1")){
            String title = "您确定要删除？";
            final AlertDialogUtil dialog = new AlertDialogUtil();
            dialog.showDialog(CommentActivity.this, title, "确定", "取消", new MyAlertDialog.MyAlertDialogOnClickCallBack()
            {
                @Override
                public void onClick()
                {
                    deleteReplyComment(cid, replyBean);
                }
            }, null);
        }else{
            if (replyBean.content != null)
            {
                isReply = true ;
                isItemReply = true ;
                map2.put("cid",cid);
                map2.put("reply_uid",replyUid);
                editText.requestFocus();
                String first = "回复";
                SpannableStringBuilder spannableString = getGreenStrBuilder(first,replyBean.user.name);
                editText.setText(spannableString);
                editText.setSelection(spannableString.length());
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
            else
            {
//            cid = commentResult.id;
                editText.requestFocus();
            }
        }
    }
    private void likeClicked(String cid){
        String api = "resource/comment/praise";
        OkHttpUtils.get().url(ContantsUtil.API_FAKE_HOST_PUBLIC + "/" + api)
                .addParams("uid", mPreference.getString(mPreference.uid))
                .addParams("rid", 1 + "")
                .addParams("cid",cid)
                .build()
                .execute(new StringCallback(){
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }


                    @Override
                    public void onResponse(String json, int id) {
                        if (!TextUtils.isEmpty(json)) {
                            //   showToast("没有相关数据");
                            commentAdapter.notifyDataSetChanged();
                            return;
                        } else {
                            dismissLoading();
                        }
                    }
                });
    }

    private void likeCancelled(String cid){
        String api = "resource/comment/unpraise";
        OkHttpUtils.get().url(ContantsUtil.API_FAKE_HOST_PUBLIC + "/" + api)
                .addParams("uid", mPreference.getString(mPreference.uid))
                .addParams("rid", 1 + "")
                .addParams("cid",cid)
                .build()
                .execute(new StringCallback(){
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }


                    @Override
                    public void onResponse(String json, int id) {
                        if (!TextUtils.isEmpty(json)) {
                            //   showToast("没有相关数据");
                            commentAdapter.notifyDataSetChanged();
                            return;
                        } else {
                            dismissLoading();
                        }
                    }
                });
    }

    @Override
    public void onLikeTouch(View v, int position, Comments comment) {
        String cid = String.valueOf(comment.cid);
        if(comment.my_praise==0){
            likeClicked(cid);
        }else{
            likeCancelled(cid);
        }
    }

    @Override
    public void onCommentTouch(View v, int position, Comments comment) {
        Bundle bundle = new Bundle();
        bundle.putString("cid", comment.cid);
        startActivity(bundle, CommentDetailActivity.class);

    }

}
