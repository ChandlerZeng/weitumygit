package com.libtop.weitu.activity.comment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
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
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.login.LoginFragment;
import com.libtop.weitu.activity.main.dto.CommentDetailDto;
import com.libtop.weitu.activity.main.dto.CommentDto;
import com.libtop.weitu.activity.main.dto.PraisedUsersBean;
import com.libtop.weitu.activity.main.dto.ReplyListDto;
import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.eventbus.MessageEvent;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.service.WTStatisticsService;
import com.libtop.weitu.utils.CheckUtil;
import com.libtop.weitu.utils.ContextUtil;
import com.libtop.weitu.utils.DateUtil;
import com.libtop.weitu.utils.ImageLoaderUtil;
import com.libtop.weitu.utils.Preference;
import com.libtop.weitu.utils.selector.utils.AlertDialogUtil;
import com.libtop.weitu.utils.selector.view.MyAlertDialog;
import com.libtop.weitu.viewadapter.CommonAdapter;
import com.libtop.weitu.viewadapter.ViewHolderHelper;
import com.libtop.weitu.widget.NetworkLoadingLayout;
import com.libtop.weitu.widget.view.ListViewForScrollView;
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
import butterknife.OnTouch;
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
    ListViewForScrollView listReply;
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
    private Map<String,String> mapItemReply = new HashMap<>();

    private List<PraisedUsersBean> praiseUserList = new ArrayList<>();
    private List<ReplyListDto> replyBeanList = new ArrayList<>();
    private PraisedUsersBean user = new PraisedUsersBean();
    private CommentDto commentsData;

    private PraiseHeadAdapter praiseHeadAdapter;
    private ReplyListAdapter replyListAdapter;

    private int MY_PRAISE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_detail);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
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
            getData(cid);
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

                WTStatisticsService.onEvent(mContext, WTStatisticsService.EID_COMMENTDETAIL_REPLY_ITEM_CLI);

                if(isNotLogin()){
                    login();
                }else {
                    if(replyBeanList.get(position).uid.equals(mPreference.getString(Preference.uid))){
                    onReplyItemDeleted(replyBeanList.get(position));   //TODO
                    }else {
                        onReplyItemTouch(replyBeanList.get(position));
                    }
                }
            }
        });
    }

    private void getUser(PraisedUsersBean praiseBean){
        if(praiseBean!=null){
            user=praiseBean;
        }
    }

    @OnClick({R.id.back_btn, R.id.comment_detail_link_layout, R.id.commit, R.id.likeLayout})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.back_btn:
                onBackPressed();
                break;

            case R.id.comment_detail_link_layout:
                WTStatisticsService.onEvent(mContext, WTStatisticsService.EID_COMMENTDETAIL_RESOURCE_CLI);

                if(commentsData!=null){
                    ContextUtil.openResourceByType(mContext, commentsData.type, commentsData.getTid());
                }
                break;

            case R.id.commit:
                WTStatisticsService.onEvent(mContext, WTStatisticsService.EID_COMMENTDETAIL_ADDREPLY_CLI);
                sendComment(view);
                break;

            case R.id.likeLayout:
                WTStatisticsService.onEvent(mContext, WTStatisticsService.EID_COMMENTDETAIL_PRAISE_CLI);
                onLikeTouch(MY_PRAISE);
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
//        http://weitu.bookus.cn/comment/get.json?text={"id":"56f97d8d984e741f1420a19e","uid":"56f97d8d984e741f1420a19e","method":"comment.get"}
        Map<String, Object> map = new HashMap<>();
        map.put("id", cid);
        map.put("method","comment.get");
        map.put("uid",mPreference.getString(Preference.uid));
        HttpRequest.loadWithMap(map).execute(new StringCallback() {

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
                        CommentDetailDto data = gson.fromJson(json, new TypeToken<CommentDetailDto>() {
                        }.getType());
                        handleCommentDetailResult(data);
                        handleCommentPraiseResult(data.praisedUsers);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void handleCommentDetailResult(CommentDetailDto data){
        if(data==null){
            networkLoadingLayout.showEmptyPrompt();
        }else {
            if(data.comment!=null){
                commentsData = new CommentDto();
                commentsData = data.comment;
            } else{
                networkLoadingLayout.showEmptyPrompt();
                return;
            }

            if (data.comment.getContent() != null) {
                tvCommnet1.setText(data.comment.getContent());
            }
            if (data.comment.getUsername() != null) {
                tvUserName.setText(data.comment.getUsername());
            }
            if (data.comment.getTimeline() != 0) {
                tvTime.setText(DateUtil.parseToStringWithoutSS(data.comment.getTimeline()));
            }
            String urlLogo = null;
            if (data.comment.logo != null) {
                urlLogo = data.comment.logo;
            }
            ImageLoaderUtil.loadLogoImage(mContext, imgHead, urlLogo);
            if (data.praised == 0) {
                likeIcon.setImageResource(R.drawable.icon_comment_detail_unpraised);
                MY_PRAISE = 0;
                data.comment.praised = 0;
            } else {
                likeIcon.setImageResource(R.drawable.icon_comment_detail_praised);
                MY_PRAISE = 1;
                data.comment.praised = 1;
            }

            if(data.comment.getTitle()!=null){
//                            Picasso.with(mCoclearntext).load(data.comment.resource.cover).error(R.drawable.default_error).placeholder(R.drawable.default_error).fit().centerCrop().into(resourceFileImage);
                commentFileTitle.setText(data.comment.getTitle());
                commentFileAuthor.setText("上传："+data.comment.getUsername());
            }

            if (data.comment.replyList != null) {
                replyBeanList = data.comment.replyList;
                replyListAdapter.setData(replyBeanList);
            }
        }
    }

    private void handleCommentPraiseResult(List<PraisedUsersBean> data){
        if (data != null) {
//            getUser(data.get(0));
            if (data.size() > 8) {
                praiseUserList = data.subList(0, 8);
            } else {
                praiseUserList = data;
            }
        }
        praiseHeadAdapter.setData(praiseUserList);
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
            String replyUid = mapItemReply.get("reply_rid").toString();
            putItemReply(replyUid,str);
        }
        else
        {
            putReply(str);
        }
    }
    private void putReply(String content) //TODO
    {
//        http://weitu.bookus.cn/reply/save.json?text={"cid":"56f97d8d984e741f1420a19e","uid":"56f97d8d984e741f1420a19e","content":"xxx","method":"reply.save"}
        showLoding();
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
                            Toast.makeText(CommentDetailActivity.this,"回复评论成功",Toast.LENGTH_SHORT).show();
                            try {
                                getData(cid);
                                editComment.setText("");
                                editComment.setHint("发表评论");
                                isReply = false;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(CommentDetailActivity.this,"回复评论失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void putItemReply(String replyId,String content){
        showLoding();
        Map<String,Object> map = new HashMap<>();
        map.put("cid",cid);
        map.put("uid",mPreference.getString(Preference.uid));
        map.put("content",content);
        map.put("method","reply.save");
        map.put("rid",replyId);
        HttpRequest.loadWithMap(map).execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }


                    @Override
                    public void onResponse(String json, int id) {
                        if (!TextUtils.isEmpty(json)) {
                            dismissLoading();
                            Toast.makeText(CommentDetailActivity.this,"回复评论成功",Toast.LENGTH_SHORT).show();
                            try {
                                commentsData.replies=commentsData.replies+1;
                                getData(cid);
                                editComment.setText("");
                                editComment.setHint("发表评论");
                                isReply = false;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(CommentDetailActivity.this,"回复评论失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void deleteReplyComment(final ReplyListDto replyBean){
        showLoding();
        Map<String, Object> map = new HashMap<>();
        map.put("method","reply.delete");
        map.put("id",replyBean.id);
        HttpRequest.loadWithMap(map).execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }


                    @Override
                    public void onResponse(String json, int id) {
                        if (!TextUtils.isEmpty(json)) {
                            Toast.makeText(CommentDetailActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                            commentsData.replies=commentsData.replies-1;
                            replyBeanList.remove(replyBean);
                            replyListAdapter.notifyDataSetChanged();
                            dismissLoading();
                        }
                    }
                });
    }


    public void onReplyItemTouch(ReplyListDto replyBean) {

        String cid =replyBean.id;
        String replyRid =replyBean.id;
        if (replyBean.content != null)
        {
            isReply = true ;
            mapItemReply.put("cid",cid);
            mapItemReply.put("reply_rid",replyRid);
            editComment.requestFocus();
            String first = "回复";
            String userName = "";
            SpannableString spannableString = getGreenStr(userName,first,replyBean.username);
            editComment.setHint(spannableString);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editComment, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void onReplyItemDeleted(final ReplyListDto replyBean) {
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
        getData(cid);
    }


    private void likeClicked(){
        Map<String,Object> map = new HashMap<>();
        map.put("cid",cid);
        map.put("uid",mPreference.getString(Preference.uid));
        map.put("method","comment.praise");
        HttpRequest.loadWithMap(map).execute(new StringCallback(){
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }


                    @Override
                    public void onResponse(String json, int id) {
                        if (!TextUtils.isEmpty(json)) {
                            //   showToast("没有相关数据");
                            dismissLoading();
                            Toast.makeText(mContext, "已赞", Toast.LENGTH_SHORT).show();
                            getData(cid);
                        }
                    }
                });
    }

    private void likeCancelled(){
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
                            Toast.makeText(mContext, "已取消赞", Toast.LENGTH_SHORT).show();
                            getData(cid);
                        }
                    }
                });
    }

    public void onLikeTouch(int mypraise) {
        if(isNotLogin()){
            login();
        }else {
            if(mypraise==0){
                likeClicked();
            }else{
                likeCancelled();
            }
        }
    }

    class PraiseHeadAdapter extends CommonAdapter<PraisedUsersBean>{

        public PraiseHeadAdapter(Context context, int itemLayoutId, List<PraisedUsersBean> datas) {
            super(context, R.layout.praise_item_grid_photo, datas);
        }

        @Override
        public void convert(ViewHolderHelper helper, PraisedUsersBean object, int position) {
            ImageView imageHead = helper.getView(R.id.praise_head_image);
            String logoUrl = null;
            if(object.logo!=null){
                logoUrl = object.logo;
            }
            ImageLoaderUtil.loadLogoImage(mContext, imageHead, logoUrl);
        }
        public void setData(List<PraisedUsersBean> userBeans){
            this.datas = userBeans;
            notifyDataSetChanged();
        }
    }

    class ReplyListAdapter extends CommonAdapter<ReplyListDto>{

        public ReplyListAdapter(Context context, int itemLayoutId, List<ReplyListDto> datas) {
            super(context, R.layout.item_list_comment_detail, datas);
        }

        @Override
        public void convert(ViewHolderHelper helper, ReplyListDto object, int position) {
            ImageView imageHead = helper.getView(R.id.img_head);
            String logoUrl = null;
            if(object.user.avatar!=null){
                logoUrl = object.user.avatar;
            }
            ImageLoaderUtil.loadLogoImage(mContext, imageHead, logoUrl);
            String user_name = object.username;
            String reply_user_name;
            String reply;
            if(object.repliedUser!=null && object.repliedUser.username!=null){
                reply_user_name = object.repliedUser.username;
                reply = " "+"回复"+" ";
                SpannableString spannableString = getGreenStr(user_name, reply, reply_user_name);
                helper.setText(R.id.tv_user_name,spannableString);
            } else {
                reply_user_name = "";
                reply = "";
                SpannableString spannableString = getGreenStr(user_name,reply,reply_user_name);
                helper.setText(R.id.tv_user_name,spannableString);
            }
            helper.setText(R.id.tv_time,DateUtil.transformToShow(object.timeline));
            helper.setText(R.id.tv_content, object.content);
        }
        public void setData(List<ReplyListDto> replyBeans){
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

    public boolean isNotLogin(){
        return CheckUtil.isNull(mPreference.getString(Preference.uid));
    }

    public void login(){
        Bundle bundle = new Bundle();
        bundle.putBoolean("isFromComment",true);
        bundle.putString(ContentActivity.FRAG_CLS, LoginFragment.class.getName());
        mContext.startActivity(bundle, ContentActivity.class);
    }

}
