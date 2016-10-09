package com.libtop.weitu.activity.search;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.comment.CommentDetailActivity;
import com.libtop.weitu.activity.login.LoginFragment;
import com.libtop.weitu.activity.main.dto.CommentDto;
import com.libtop.weitu.activity.main.dto.ReplyListDto;
import com.libtop.weitu.activity.search.adapter.CommentAdapter;
import com.libtop.weitu.activity.search.dto.CommentNeedDto;
import com.libtop.weitu.activity.search.dto.CommentResult;
import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.dao.ResultCodeDto;
import com.libtop.weitu.eventbus.MessageEvent;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.utils.Preference;
import com.libtop.weitu.utils.CheckUtil;
import com.libtop.weitu.utils.ContantsUtil;
import com.libtop.weitu.utils.JSONUtil;
import com.libtop.weitu.utils.ListViewUtil;
import com.libtop.weitu.utils.selector.utils.AlertDialogUtil;
import com.libtop.weitu.utils.selector.view.MyAlertDialog;
import com.libtop.weitu.widget.NetworkLoadingLayout;
import com.libtop.weitu.widget.view.XListView;
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
import butterknife.OnTouch;
import okhttp3.Call;


public class CommentActivity extends BaseActivity implements CommentAdapter.OnCommentListener,NetworkLoadingLayout.OnRetryClickListner
{

    @Bind(R.id.edit_comment)
    EditText editText;
    @Bind(R.id.list_comment)
    XListView xListView;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.textview_empty_comment)
    TextView textViewEmptyComment;
    @Bind(R.id.sub_title)
    TextView subTitle;
    @Bind(R.id.networkloadinglayout)
    NetworkLoadingLayout networkLoadingLayout;

    private CommentAdapter commentAdapter;

    private List<CommentResult> list;

    private CommentNeedDto commentNeedDto;

    private boolean isReply = false;
    private boolean isItemReply = false;

    private List<CommentDto> commentsList = new ArrayList<>(); //TODO

    private int mCurPage = 1;
    private boolean hasData = true;
    private boolean isFirstIn = true;
    private boolean isRefreshed = false;

    private HashMap<String, Object> replyMap = new HashMap<>();
    private HashMap<String, Object> replyItemMap = new HashMap<>();

    private ReplyListDto replyItem;
    private CommentDto comments;
    private List<ReplyListDto> replyItems;

    public static String UID ;
    private final int REQUEST_CODE = 300;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setInjectContentView(R.layout.activity_comment);
        UID = mPreference.getString(Preference.uid);
        EventBus.getDefault().register(this);
        initView();
    }


    @Override
    protected void onResume()
    {
        super.onResume();
    }


    @Override
    protected void onPause()
    {
        super.onPause();
    }


    private void initView(){
        String json = getIntent().getStringExtra("CommentNeedDto");
        commentNeedDto = JSONUtil.readBean(json, CommentNeedDto.class);

        if (commentNeedDto.title!=null && !TextUtils.isEmpty(commentNeedDto.title))
        {
            subTitle.setVisibility(View.VISIBLE);
            subTitle.setText(commentNeedDto.title);
        }else {
            subTitle.setVisibility(View.GONE);
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
        commentAdapter = new CommentAdapter(this, commentsList, this);
        xListView.setAdapter(commentAdapter);
        networkLoadingLayout.setOnRetryClickListner(this);
    }


    private void getCommentList()
    {
//        http://weitu.bookus.cn/comment/list.json?text={"tid":"56f97d8d984e741f1420a19e","page":1,"uid":"56f97d8d984e741f1420a19e","method":"comment.list"}
        Map<String, Object> map = new HashMap<>();
        map.put("page", mCurPage);
        if(!isNotLogin()){
            map.put("uid",mPreference.getString(Preference.uid));
        }
        map.put("method","comment.list");
        map.put("tid",commentNeedDto.tid);
        HttpRequest.loadWithMap(map).execute(new StringCallback() {
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
                        commentsList.clear();
                    }
                    List<CommentDto> data = JSONUtil.readBeanArray(json, CommentDto.class);
                    if (data != null) {
                        commentsList.addAll(data);
                    }
                    if (data.size() < 20) {
                        hasData = false;
                        xListView.setPullLoadEnable(false);
                    } else {
                        hasData = true;
                        xListView.setPullLoadEnable(true);
                    }
                    if (commentsList.size() == 0 && mCurPage == 1) {
//                            networkLoadingLayout.showEmptyPrompt();
                        textViewEmptyComment.setVisibility(View.VISIBLE);
                    }else {
                        textViewEmptyComment.setVisibility(View.GONE);
                    }
                    mCurPage++;
                    commentAdapter.setData(commentsList);
                    editText.setText("");
                    editText.setHint("发表评论");
                    isReply = false;
                    isItemReply = false;
                }
            }
        });
    }


    @Nullable
    @OnClick({R.id.back_btn, R.id.commit, R.id.edit_comment})
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


    @OnTouch({R.id.edit_comment})
    public boolean onTouch(View view, MotionEvent event) {
        if (view.getId() == R.id.edit_comment) {
            if(isNotLogin()){
                login();
            }
        }
        return false;
    }




    private void sendComment(View v)
    {
        mCurPage=1;
        String str = editText.getText().toString().trim();
        if (str == null || str.length() == 0)
        {
            Toast.makeText(CommentActivity.this, "评论不能为空!", Toast.LENGTH_SHORT).show();
            return;
        }
        hideKeyBoard(v);
        if(isReply && isItemReply)
        {
            String cid = replyItemMap.get("cid").toString();
            String replyId = replyItemMap.get("reply_id").toString();
            putItemReply(cid, replyId,str);
        }
        else if (isReply)
        {
            String cid = replyMap.get("cid").toString();
            putReply(cid,str);
        }
        else
        {
            putComment(str);
        }
    }
    private void putReply(String cid,String content) //TODO
    {
        showLoding();
//        http://weitu.bookus.cn/reply/save.json?text={"cid":"56f97d8d984e741f1420a19e","uid":"56f97d8d984e741f1420a19e","content":"xxx","method":"reply.save"}
        Map<String,Object> map = new HashMap<>();
        map.put("cid",cid);
        map.put("uid",mPreference.getString(Preference.uid));
        map.put("content",content);
        map.put("method","reply.save");
        HttpRequest.loadWithMap(map).execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }


                    @Override
                    public void onResponse(String json, int id) {
                        if (!TextUtils.isEmpty(json)) {
                            dismissLoading();
                            Toast.makeText(CommentActivity.this,"回复评论成功",Toast.LENGTH_SHORT).show();
                            try {
                                getCommentList();
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
        // http://weitu.bookus.cn/comment/save.json?text={"tid":"56f97d8d984e741f1420a19e","uid":"56f97d8d984e741f1420a19e","method":"comment.save"}
        showLoding();
        Map<String, Object> map = new HashMap<>();
        map.put("method","comment.save");
        map.put("content",content);
        map.put("tid",commentNeedDto.tid);
        map.put("uid",mPreference.getString(Preference.uid));
        map.put("type",commentNeedDto.type);
        HttpRequest.loadWithMap(map).execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }


                    @Override
                    public void onResponse(String json, int id) {
                        if (!TextUtils.isEmpty(json) && json!=null) {
                            dismissLoading();
                            try {
                                Gson gson = new Gson();
                                ResultCodeDto data = gson.fromJson(json, new TypeToken<ResultCodeDto>() {
                                }.getType());
                                if (data != null && data.code==1) {
                                    xListView.requestFocus();
                                    xListView.setSelection(1);
                                    getCommentList();// TODO
                                    Toast.makeText(CommentActivity.this,"评论成功",Toast.LENGTH_SHORT).show();
                                }else {
                                    Toast.makeText(CommentActivity.this,"评论失败",Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else {
                            Toast.makeText(CommentActivity.this,"评论失败",Toast.LENGTH_SHORT).show();
                        }
                    }

                });
    }

    @Override
    public void onReplyTouch(View v, int position,List<ReplyListDto> replyBeans,CommentDto object) //TODO
    {
        if(isNotLogin()){
            login();
        }else {
            CommentDto commentResult = commentsList.get(position);
            replyItems = replyBeans;
            comments = object;
            isReply = true;
            if (commentResult.getContent() != null && !TextUtils.isEmpty(commentResult.getContent()))
            {
                String cid = commentResult.getId();
                replyMap.put("cid",cid);
                editText.requestFocus();
                String first = "回复";
                SpannableStringBuilder spannableString = getGreenStrBuilder(first,commentResult.getUsername());
                editText.setHint(spannableString);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
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


    private void deleteReplyComment(String id, final ReplyListDto replyBean, final List<ReplyListDto> replyBeans,final CommentDto object){
        showLoding();
        Map<String, Object> map = new HashMap<>();
        map.put("method","reply.delete");
        map.put("id",id);
        HttpRequest.loadWithMap(map).execute(new StringCallback() {
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

    private void deleteComment(String cid,final CommentDto comments){
//        http://weitu.bookus.cn/comment/delete.json?text={"id":"56f97d8d984e741f1420a19e","method":"comment.delete"}
        showLoding();
        Map<String,Object> map = new HashMap<>();
        map.put("method","comment.delete");
        map.put("id",cid);
        HttpRequest.loadWithMap(map).execute(new StringCallback() {
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

    private void putItemReply(String cid,String replyId,String content){
        showLoding();
        Map<String,Object> map = new HashMap<>();
        map.put("cid",cid);
        map.put("uid",mPreference.getString(Preference.uid));
        map.put("content",content);
        map.put("method","reply.save");
        map.put("rid",replyId);
        HttpRequest.loadWithMap(map).execute(new StringCallback(){
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }


                    @Override
                    public void onResponse(String json, int id) {
                        if (!TextUtils.isEmpty(json) && json.equals("null")) {
                            dismissLoading();
                            Toast.makeText(CommentActivity.this,"回复评论成功",Toast.LENGTH_SHORT).show();
                            try {
                                getCommentList();
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
    public void onReplyItemTouch(View v, final int position, final ReplyListDto replyBean,final List<ReplyListDto> replyBeans,CommentDto object) {

        if(isNotLogin()){
            login();
        }else {
            String cid =object.getId();
            String replyId =replyBean.id;
            replyItem = replyBean;
            replyItems = replyBeans;
            comments = object;
            if (replyBean.content != null)
            {
                isReply = true ;
                isItemReply = true ;
                replyItemMap.put("cid",cid);
                replyItemMap.put("reply_id",replyId);
                editText.requestFocus();
                String first = "回复";
                SpannableStringBuilder spannableString = getGreenStrBuilder(first,replyBean.username);
                editText.setHint(spannableString);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }

    @Override
    public void onReplyItemDeleted(View v, int position, final ReplyListDto replyBean,final List<ReplyListDto> replyBeans, final CommentDto object) {
        String title = "您确定要删除？";
        final AlertDialogUtil dialog = new AlertDialogUtil();
        dialog.showDialog(CommentActivity.this, title, "确定", "取消", new MyAlertDialog.MyAlertDialogOnClickCallBack() {
            @Override
            public void onClick() {
                deleteReplyComment(replyBean.id, replyBean, replyBeans, object);
            }
        }, null);
    }

    private void likeClicked(String cid, final CommentDto comments){
//        http://weitu.bookus.cn/comment/praise.json?text={"cid":"56f97d8d984e741f1420a19e","uid":"56f97d8d984e741f1420a19e","method":"comment.praise"}
        Map<String,Object> map = new HashMap<>();
        map.put("cid",cid);
        map.put("uid",mPreference.getString(Preference.uid));
        map.put("method","comment.praise");
        HttpRequest.loadWithMap(map).execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }


                    @Override
                    public void onResponse(String json, int id) {
                        if (!TextUtils.isEmpty(json)) {
                            //   showToast("没有相关数据");
                            dismissLoading();
                            comments.praises = comments.praises + 1;
                            comments.praised = 1;
                            Toast.makeText(mContext, "已赞", Toast.LENGTH_SHORT).show();
                            commentAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void likeCancelled(String cid,final CommentDto comments){
//        http://weitu.bookus.cn/comment/unpraise.json?text={"cid":"56f97d8d984e741f1420a19e","uid":"56f97d8d984e741f1420a19e","method":"comment.unpraise"}
        Map<String,Object> map = new HashMap<>();
        map.put("cid",cid);
        map.put("uid",mPreference.getString(Preference.uid));
        map.put("method","comment.unpraise");
        HttpRequest.loadWithMap(map).execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }


                    @Override
                    public void onResponse(String json, int id) {
                        if (!TextUtils.isEmpty(json)) {
                            //   showToast("没有相关数据");
                            dismissLoading();
                            comments.praises = comments.praises - 1;
                            comments.praised = 0;
                            Toast.makeText(mContext, "已取消赞", Toast.LENGTH_SHORT).show();
                            commentAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    public void onLikeTouch(View v, int position, CommentDto comment) {
        String cid = String.valueOf(comment.getId());
        if(isNotLogin()){
            login();
        }else {
            if(comment.praised==0){
                likeClicked(cid,comment);
            }else{
                likeCancelled(cid,comment);
            }
        }
    }

    @Override
    public void onCommentContentClick(View v, int position, CommentDto comment) {
        Bundle bundle = new Bundle();
        bundle.putString("cid", comment.getId());
        bundle.putInt("position", position);
        startForResult(bundle, 200, CommentDetailActivity.class);
    }

    @Override
    public void onCommentContentLongClick(View v, final int position, final CommentDto comment) {
        if (comment.getUid().equals(mPreference.getString(Preference.uid))) {
            String title = "您确定要删除？";
            final AlertDialogUtil dialog = new AlertDialogUtil();
            dialog.showDialog(CommentActivity.this, title, "确定", "取消", new MyAlertDialog.MyAlertDialogOnClickCallBack() {
                @Override
                public void onClick() {
                    if(isNotLogin()){
                        login();
                    }else {
                        deleteComment(comment.getId(),comment);
                    }
                }
            }, null);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 200 && resultCode ==300){

        }else if(requestCode==REQUEST_CODE && resultCode== Activity.RESULT_OK){
            mCurPage=1;
            getCommentList();
            UID = mPreference.getString(Preference.uid);
        }
    }

    public void replaceComment(int position,CommentDto comments){
        commentsList.remove(position);
        commentsList.add(position, comments);
        commentAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(MessageEvent event)
    {
        if(event.message.getString("isFromComment")!=null && event.message.getString("isFromComment").equals("true")){
            Bundle bundle = event.message;
            CommentDto comments = (CommentDto) bundle.getSerializable("comments");
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

    public boolean isNotLogin(){
        return CheckUtil.isNull(mPreference.getString(Preference.uid));
    }

    public void login(){
        Bundle bundle = new Bundle();
        bundle.putBoolean("isFromComment",true);
        bundle.putString(ContentActivity.FRAG_CLS, LoginFragment.class.getName());
        Intent intent = new Intent(mContext,ContentActivity.class);
        intent.putExtras(bundle);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        mContext.startForResultWithFlag(intent,REQUEST_CODE);
    }
}
