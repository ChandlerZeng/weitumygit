package com.libtop.weitu.activity.comment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.search.CommentActivity;
import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.eventbus.MessageEvent;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.test.CommentBean;
import com.libtop.weitu.test.Comments;
import com.libtop.weitu.test.PraiseBean;
import com.libtop.weitu.test.Reply;
import com.libtop.weitu.test.ReplyBean;
import com.libtop.weitu.test.UserBean;
import com.libtop.weitu.utils.ContantsUtil;
import com.libtop.weitu.utils.ContextUtil;
import com.libtop.weitu.utils.DateUtil;
import com.libtop.weitu.utils.selector.utils.AlertDialogUtil;
import com.libtop.weitu.utils.selector.view.MyAlertDialog;
import com.libtop.weitu.viewadapter.CommonAdapter;
import com.libtop.weitu.viewadapter.ViewHolderHelper;
import com.libtop.weitu.widget.NetworkLoadingLayout;
import com.libtop.weitu.widget.listview.ChangeListView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
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
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

public class CommentDetailActivity extends BaseActivity implements NetworkLoadingLayout.OnRetryClickListner{

    @Bind(R.id.back_btn)
    ImageView backBtn;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.title_container)
    LinearLayout titleContainer;
    @Bind(R.id.img_head)
    ImageView imgHead;
    @Bind(R.id.tv_user_name)
    TextView tvUserName;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.tv_commnet1)
    TextView tvCommnet1;
    @Bind(R.id.comment_layout1)
    RelativeLayout commentLayout1;
    @Bind(R.id.resource_file_image)
    ImageView resourceFileImage;
    @Bind(R.id.comment_file_title)
    TextView commentFileTitle;
    @Bind(R.id.comment_file_author)
    TextView commentFileAuthor;
    @Bind(R.id.subject_file_ll)
    LinearLayout subjectFileLl;
    @Bind(R.id.comment_detail_link_layout)
    LinearLayout commentDetailLinkLayout;
    @Bind(R.id.likeLayout)
    LinearLayout likeLayout;
    @Bind(R.id.like_icon)
    ImageView likeIcon;
    @Bind(R.id.comment_detail_grid_view)
    GridView commentDetailGridView;
    @Bind(R.id.list_reply)
    ChangeListView listReply;
    @Bind(R.id.edit_comment)
    EditText editComment;
    @Bind(R.id.commit)
    Button commit;
    @Bind(R.id.commentDetailScrollView)
    ScrollView commentDetailScrollView;
    @Bind(R.id.networkloadinglayout)
    NetworkLoadingLayout networkLoadingLayout;

    private String cid;
    private int position;
    private boolean isReply = false;
    private boolean isFirstIn = true;
    private Map<String,String> map = new HashMap<>();

    private List<UserBean> praiseUserList = new ArrayList<>();
    private List<ReplyBean> replyBeanList = new ArrayList<>();
    private UserBean user = new UserBean();
    private Comments commentsData;

    private PraiseHeadAdapter praiseHeadAdapter;
    private ReplyListAdapter replyListAdapter;

    private int MYPRAISE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_detail);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void onDestroy()
    {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(MessageEvent event) {
    }

    private void initView(){
        title.setText("评论详情");
        Bundle bundle = getIntent().getExtras();
        cid = bundle.getString("cid");
        position = bundle.getInt("position");
        if (isFirstIn)
        {
            isFirstIn = false;
            networkLoadingLayout.showLoading();
            getData(String.valueOf(cid));
            getPraiseData();
        }
        networkLoadingLayout.setOnRetryClickListner(this);
        praiseHeadAdapter = new PraiseHeadAdapter(mContext,R.layout.praise_item_grid_photo,praiseUserList);
        replyListAdapter = new ReplyListAdapter(mContext,R.layout.item_list_comment_detail,replyBeanList);
        commentDetailGridView.setAdapter(praiseHeadAdapter);
        commentDetailGridView.requestDisallowInterceptTouchEvent(false);
        listReply.setAdapter(replyListAdapter);
        listReply.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(replyBeanList.get(position).user.uid.equals("1")){
                    onReplyItemDeleted(replyBeanList.get(position));
                }else {
                    onReplyItemTouch(replyBeanList.get(position));
                }
            }
        });
    }

    private void getUser(PraiseBean praiseBean){
        if(praiseBean.praise_users.size()!=0){
            user=praiseBean.praise_users.get(0);
        }
    }

    @OnClick({R.id.back_btn, R.id.comment_detail_link_layout, R.id.commit, R.id.likeLayout})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                onBackPressed();
                break;
            case R.id.comment_detail_link_layout:
                if(commentsData!=null){
                    ContextUtil.openResourceByType(mContext, commentsData.resource.type, commentsData.resource.rid);
//                    openBook(commentsData.resource.name, commentsData.resource.cover, commentsData.resource.uploader_name, "9787504444622", "中国商业出版社,2001");//TODO
                }
                break;
            case R.id.commit:
                sendComment(view);
                break;
            case R.id.likeLayout:
                onLikeTouch(MYPRAISE);
                break;
        }
    }

    @Override
    public void onBackPressed(){ //TODO
        mContext.finish();
        if(commentsData!=null){
            Bundle bundle = new Bundle();
            bundle.putString("isFromComment","true");
            bundle.putSerializable("comments", commentsData);
            bundle.putBoolean("isCommentUpdate", true);
            bundle.putInt("position", position);
            EventBus.getDefault().post(new MessageEvent(bundle));
        }
    }


    private void getData(String cid)
    {
//        http://115.28.189.104/resource/comment/info?cid=4
        Map<String, Object> map = new HashMap<>();
        String method = "/resource/comment/info";
        String api = "/resource/comment/info";
        map.put("cid", cid);
        HttpRequest.newLoad(ContantsUtil.API_FAKE_HOST_PUBLIC + method, map).execute(new StringCallback() {

            @Override
            public void onError(Call call, Exception e, int id) {
                networkLoadingLayout.showLoadFailAndRetryPrompt();
            }


            @Override
            public void onResponse(String json, int id) {
                if (!TextUtils.isEmpty(json)) {
                    networkLoadingLayout.dismiss();
                    try {
                        Gson gson = new Gson();
                        CommentBean data = gson.fromJson(json, new TypeToken<CommentBean>() {
                        }.getType());
                        if(data.comment!=null){
                            commentsData = new Comments();
                            commentsData = data.comment;
                        } else{
                            networkLoadingLayout.showEmptyPrompt();
                            return;
                        }

                        if (data.comment.content != null) {
                            tvCommnet1.setText(data.comment.content);
                        }
                        if (data.comment.user.name != null) {
                            tvUserName.setText(data.comment.user.name);
                        }
                        if (data.comment.t_create != 0) {
                            tvTime.setText(DateUtil.parseToStringWithoutSS(data.comment.t_create));
                        }
                        if (data.comment.user.logo != null) {
                            Picasso.with(mContext).load(data.comment.user.logo).transform(new CircleTransform()).error(R.drawable.head_image).placeholder(R.drawable.head_image).fit().centerCrop().into(imgHead);
                        }
                        if (data.comment.my_praise == 0) {
                            likeIcon.setImageResource(R.drawable.icon_comment_detail_unpraised);
                            MYPRAISE = 0;
                        } else {
                            likeIcon.setImageResource(R.drawable.icon_comment_detail_praised);
                            MYPRAISE = 1;
                        }

                        if(data.comment.resource!=null){
                            Picasso.with(mContext).load(data.comment.resource.cover).error(R.drawable.default_error).placeholder(R.drawable.default_error).fit().centerCrop().into(resourceFileImage);
                            commentFileTitle.setText(data.comment.resource.name);
                            commentFileAuthor.setText("上传："+data.comment.resource.uploader_name);
                        }

                        if (data.comment.replys != null) {
                            replyBeanList = data.comment.replys;
                            replyListAdapter.setData(replyBeanList);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void getPraiseData()
    {
//        http://115.28.189.104/resource/comment/praise_user/list
        Map<String, Object> map = new HashMap<>();
        String api = "/resource/comment/praise_user/list";
//        map.put("cid", cid);
        HttpRequest.newLoad(ContantsUtil.API_FAKE_HOST_PUBLIC + api, null).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }


            @Override
            public void onResponse(String json, int id) {
                if (!TextUtils.isEmpty(json)) {
                    try {
                        Gson gson = new Gson();
                        PraiseBean data = gson.fromJson(json, new TypeToken<PraiseBean>() {
                        }.getType());
                        if (data.praise_users != null) {
                            getUser(data);
                            if (data.praise_users.size() > 8) {
                                praiseUserList = data.praise_users.subList(0, 8);
                            } else {
                                praiseUserList = data.praise_users;
                            }
                            praiseHeadAdapter.setData(praiseUserList);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void sendComment(View v)
    {
        String str = editComment.getText().toString().trim();
        if (str == null || str.length() == 0)
        {
            Toast.makeText(CommentDetailActivity.this, "评论不能为空!", Toast.LENGTH_SHORT).show();
            return;
        }
        hideKeyBoard(v);
        if (isReply)
        {
            String replyUid = map.get("reply_uid").toString();
            putItemReply(replyUid,str);
        }
        else
        {
            putReply(str);
        }
    }
    private void putReply(String content) //TODO
    {
        showLoding();
        String api = "resource/comment/reply";
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
                            Toast.makeText(CommentDetailActivity.this,"回复评论成功",Toast.LENGTH_SHORT).show();
                            try {
                                Gson gson = new Gson();
                                Reply data = gson.fromJson(json, new TypeToken<Reply>() {
                                }.getType());
                                if (data.reply != null) {
                                    replyBeanList.add(data.reply);
                                    replyListAdapter.notifyDataSetChanged();
                                }
                                commentsData.count_reply=commentsData.count_reply+1;
                                editComment.setText("");
                                editComment.setHint("发表评论");
                                isReply = false;
//                                listReply.setSelection(0);
//                                listReply.smoothScrollToPosition(0);
//                                commentDetailScrollView.fullScroll(ScrollView.FOCUS_UP);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(CommentDetailActivity.this,"回复评论失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void putItemReply(String replyUid,String content){
        showLoding();
        String api = "resource/comment/reply";
        OkHttpUtils.get().url(ContantsUtil.API_FAKE_HOST_PUBLIC + "/" + api)
                .addParams("reply_uid", replyUid)
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
                            Toast.makeText(CommentDetailActivity.this,"回复评论成功",Toast.LENGTH_SHORT).show();
                            try {
                                Gson gson = new Gson();
                                Reply data = gson.fromJson(json, new TypeToken<Reply>() {
                                }.getType());
                                if (data.reply != null) {
                                    replyBeanList.add(data.reply);
                                    replyListAdapter.notifyDataSetChanged();
                                }
                                commentsData.count_reply=commentsData.count_reply+1;
                                editComment.setText("");
                                editComment.setHint("发表评论");
                                isReply = false;
//                                listReply.setSelection(0);
//                                listReply.smoothScrollToPosition(0);
//                                commentDetailScrollView.fullScroll(ScrollView.FOCUS_UP);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(CommentDetailActivity.this,"回复评论失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void deleteReplyComment(final ReplyBean replyBean){
        showLoding();
        String api = "resource/comment/del";
        OkHttpUtils.get().url(ContantsUtil.API_FAKE_HOST_PUBLIC + "/" + api)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }


                    @Override
                    public void onResponse(String json, int id) {
                        if (!TextUtils.isEmpty(json)) {
                            Toast.makeText(CommentDetailActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                            commentsData.count_reply=commentsData.count_reply-1;
                            replyBeanList.remove(replyBean);
                            replyListAdapter.notifyDataSetChanged();
                            dismissLoading();
                        }
                    }
                });
    }


    public void onReplyItemTouch(ReplyBean replyBean) {

        String cid =replyBean.cid;
        String replyUid =replyBean.user.uid;
        if (replyBean.content != null)
        {
            isReply = true ;
            map.put("cid",cid);
            map.put("reply_uid",replyUid);
            editComment.requestFocus();
            String first = "回复";
            String userName = "";
            SpannableString spannableString = getGreenStr(userName,first,replyBean.user.name);
            editComment.setHint(spannableString);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editComment, InputMethodManager.SHOW_IMPLICIT);
        }
        else
        {
            editComment.requestFocus();
        }
    }

    public void onReplyItemDeleted(final ReplyBean replyBean) {
        String title = "您确定要删除？";
        final AlertDialogUtil dialog = new AlertDialogUtil();
        dialog.showDialog(CommentDetailActivity.this, title, "确定", "取消", new MyAlertDialog.MyAlertDialogOnClickCallBack() {
            @Override
            public void onClick() {
                deleteReplyComment(replyBean);
            }
        }, null);
    }

    @Override
    public void onRetryClick(View v) {
        getData(String.valueOf(cid));
        getPraiseData();
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
            BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
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

    private void likeClicked(){
        String api = "resource/comment/praise";
        OkHttpUtils.get().url(ContantsUtil.API_FAKE_HOST_PUBLIC + "/" + api)
                .build()
                .execute(new StringCallback(){
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }


                    @Override
                    public void onResponse(String json, int id) {
                        if (!TextUtils.isEmpty(json)) {
                            //   showToast("没有相关数据");
                            dismissLoading();
                            commentsData.count_praise=commentsData.count_praise+1;
                            commentsData.my_praise=1;
                            MYPRAISE = 1;
                            likeIcon.setImageResource(R.drawable.icon_comment_detail_praised);
                            Toast.makeText(mContext, "已赞", Toast.LENGTH_SHORT).show();
                            praiseUserList.add(0,user);
                            praiseHeadAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void likeCancelled(){
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
                            commentsData.count_praise = commentsData.count_praise - 1;
                            commentsData.my_praise = 0;
                            MYPRAISE = 0;
                            likeIcon.setImageResource(R.drawable.icon_comment_detail_unpraised);
                            Toast.makeText(mContext, "已取消赞", Toast.LENGTH_SHORT).show();
                            praiseUserList.remove(user);
                            praiseHeadAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    public void onLikeTouch(int mypraise) {
        if(mypraise==0){
            likeClicked();
        }else{
            likeCancelled();
        }
    }

    class PraiseHeadAdapter extends CommonAdapter<UserBean>{

        public PraiseHeadAdapter(Context context, int itemLayoutId, List<UserBean> datas) {
            super(context, R.layout.praise_item_grid_photo, datas);
        }

        @Override
        public void convert(ViewHolderHelper helper, UserBean object, int position) {
            ImageView imageHead = helper.getView(R.id.praise_head_image);
            Picasso.with(mContext).load(object.logo).transform(new CircleTransform()).error(R.drawable.head_image).placeholder(R.drawable.head_image).fit().centerCrop().into(imageHead);
        }
        public void setData(List<UserBean> userBeans){
            this.datas = userBeans;
            notifyDataSetChanged();
        }
    }

    class ReplyListAdapter extends CommonAdapter<ReplyBean>{

        public ReplyListAdapter(Context context, int itemLayoutId, List<ReplyBean> datas) {
            super(context, R.layout.item_list_comment_detail, datas);
        }

        @Override
        public void convert(ViewHolderHelper helper, ReplyBean object, int position) {
            ImageView imageHead = helper.getView(R.id.img_head);
            Picasso.with(mContext).load(object.user.logo).transform(new CircleTransform()).error(R.drawable.head_image).placeholder(R.drawable.head_image).fit().centerCrop().into(imageHead);
            String user_name = object.user.name;
            String reply_user_name;
            String reply;
            if(object.reply_user!=null && object.reply_user.name!=null&&!TextUtils.isEmpty(object.reply_user.name)){
                reply_user_name = object.reply_user.name;
                reply = " "+"回复"+" ";
                SpannableString spannableString = getGreenStr(user_name, reply, reply_user_name);
                helper.setText(R.id.tv_user_name,spannableString);
            } else {
                reply_user_name = "";
                reply = "";
                SpannableString spannableString = getGreenStr(user_name,reply,reply_user_name);
                helper.setText(R.id.tv_user_name,spannableString);
            }
            helper.setText(R.id.tv_time,DateUtil.transformToShow(object.t_create));
            helper.setText(R.id.tv_content, object.content);
        }
        public void setData(List<ReplyBean> replyBeans){
            this.datas = replyBeans;
            notifyDataSetChanged();
        }
    }
    private SpannableString getGreenStr(String userName,String reply ,String replyUserName)
    {
        SpannableString spannableString = new SpannableString(userName + reply + replyUserName);
        if(userName!=null){
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#47885D")), 0, userName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        if(replyUserName!=null){
            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#47885D")), userName.length() + reply.length(), userName.length() + reply.length() + replyUserName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }
}
