package com.libtop.weitu.activity.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.comment.CommentDetailActivity;
import com.libtop.weitu.activity.main.clickHistory.ResultBean;
import com.libtop.weitu.activity.search.BookDetailFragment;
import com.libtop.weitu.activity.search.VideoPlayActivity2;
import com.libtop.weitu.activity.search.dto.SearchResult;
import com.libtop.weitu.activity.search.dynamicCardLayout.DynamicCardActivity;
import com.libtop.weitu.activity.source.AudioPlayActivity2;
import com.libtop.weitu.activity.source.PdfActivity2;
import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.eventbus.MessageEvent;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.test.CommentBean;
import com.libtop.weitu.test.Comments;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.ContantsUtil;
import com.libtop.weitu.viewadapter.CommonAdapter;
import com.libtop.weitu.viewadapter.ViewHolderHelper;
import com.squareup.picasso.Picasso;
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


/**
 * Created by LianTu on 2016-9-12.
 */
public class MyLikeActivity2 extends BaseActivity
{

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.lv_my_like)
    ListView lvMyLike;

    public static final int VIDEO = 1, AUDIO = 2, DOC = 3, PHOTO = 4, BOOK = 5;

    private MyLikeAdapter myLikeAdapter;

    private List<ResultBean> mData = new ArrayList<>();

    private List<Comments> commentsList = new ArrayList<>(); //TODO


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setInjectContentView(R.layout.activity_main_my_like);
        EventBus.getDefault().register(this);
        initView();
//        reqestData();
        getCommentList();
    }

    private void getCommentList()
    {
        //  http://192.168.0.9/resource/comment/list private
        //  http://115.28.189.104/resource/comment/list public
        showLoding();
        Map<String, Object> map = new HashMap<>();
        String api = "/resource/comment/list";
//        map.put("page", mCurPage);
        HttpRequest.newLoad(ContantsUtil.API_FAKE_HOST_PUBLIC + api, map).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e("CommentActivity", e.getMessage());
            }


            @Override
            public void onResponse(String json, int id) {
                if (!TextUtils.isEmpty(json)) {
//                    xListView.stopRefresh();
                    dismissLoading();
                    try {
                        Gson gson = new Gson();
                        CommentBean data = gson.fromJson(json, new TypeToken<CommentBean>() {
                        }.getType());
                        commentsList.clear();
                        if (data.comments != null) {
                            commentsList.addAll(data.comments);
                            myLikeAdapter.setData(commentsList);
                        }
//                        if (commentsList.size() < 20) {
//                            hasData = false;
//                            xListView.setPullLoadEnable(false);
//                        } else {
//                            hasData = true;
//                            xListView.setPullLoadEnable(true);
//                        }
//                        mCurPage++;
//                        commentAdapter.setData(commentsList);
//                        editText.setText("");
//                        editText.setHint("发表评论");
//                        isReply = false;
//                        isItemReply = false;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    /*private void reqestData()
    {
        showLoding();
        HashMap<String, Object> params = new HashMap<>();
        params.put("uid", mPreference.getString(Preference.uid));
        params.put("method", "footprint.query");
        JSONObject jsonObject = new JSONObject();
        HttpRequest.loadWithMap(params).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {
            }


            @Override
            public void onResponse(String json, int id)
            {
                dismissLoading();
                if (!TextUtils.isEmpty(json))
                {
                    List<ResultBean> mlist = JSONUtil.fromJson(json, new TypeToken<List<ResultBean>>()
                    {
                    }.getType());
                    myLikeAdapter.addAll(mlist);
                    mData.addAll(mlist);
                }
            }
        });
    }*/


    private void initView()
    {
        boolean isComment = getIntent().getBooleanExtra("isComment",false);
        title.setText("我赞过的");
        if (isComment)
            title.setText("我的评论");
        myLikeAdapter = new MyLikeAdapter(this,commentsList);
        lvMyLike.setAdapter(myLikeAdapter);
        lvMyLike.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                //TODO 打开具体评论内容
                Bundle bundle = new Bundle();
                bundle.putString("cid", commentsList.get(position).cid);
                bundle.putInt("position", position);
                startForResult(bundle, 200, CommentDetailActivity.class);
            }
        });
    }

    public void replaceComment(int position,Comments comments){
        commentsList.remove(position);
        commentsList.add(position, comments);
        myLikeAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(MessageEvent event)
    {
        Bundle bundle = event.message;
        Comments comments = (Comments) bundle.getSerializable("comments");
        int position = bundle.getInt("position");
        Boolean isCommentUpdate = bundle.getBoolean("isCommentUpdate",false);
        if (isCommentUpdate)
        {
            replaceComment(position, comments);
        }
    }


    @Override
    public void onDestroy()
    {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void startByType(int type, int position)
    {
        switch (type)
        {
            case BOOK:
                openBook(position);
                break;
            case VIDEO:
                openVideo(position);
                break;
            case AUDIO:
                openAudio(position);
                break;
            case DOC:
                openDoc(position);
                break;
            case PHOTO:
                openPhoto(position);
                break;
        }
    }

    private void openAudio(int position)
    {
        SearchResult result = new SearchResult();
        result.id = mData.get(position).target.getId();
        result.cover = mData.get(position).target.getCover();
        Intent intent = new Intent(mContext, AudioPlayActivity2.class);
        intent.putExtra("resultBean", new Gson().toJson(result));
        mContext.startActivity(intent);
    }


    private void openVideo(int position)
    {
        SearchResult result = new SearchResult();
        result.id = mData.get(position).target.getId();
        Intent intent = new Intent(mContext, VideoPlayActivity2.class);
        intent.putExtra("resultBean", new Gson().toJson(result));
        mContext.startActivity(intent);
    }


    private void openBook(int position)
    {
        Bundle bundle = new Bundle();
        bundle.putString("name", mData.get(position).target.getTitle());
        bundle.putString("cover", mData.get(position).target.getCover());
        bundle.putString("auth", mData.get(position).target.getAuthor());
        bundle.putString("isbn", mData.get(position).target.getIsbn());
        bundle.putString("publisher", mData.get(position).target.getPublisher());
        bundle.putString("school", Preference.instance(mContext).getString(Preference.SchoolCode));
        bundle.putBoolean(BookDetailFragment.ISFROMMAINPAGE, true);
        bundle.putBoolean(ContentActivity.FRAG_ISBACK, false);
        bundle.putString(ContentActivity.FRAG_CLS, BookDetailFragment.class.getName());
        mContext.startActivity(bundle, ContentActivity.class);
    }


    private void openPhoto(int position)
    {
        Bundle bundle = new Bundle();
        bundle.putString("type", "img");
        bundle.putString("id", mData.get(position).target.getId());
        mContext.startActivity(bundle, DynamicCardActivity.class);
    }


    private void openDoc(int position)
    {
        Intent intent = new Intent();
        intent.putExtra("url", "");
        intent.putExtra("doc_id", mData.get(position).target.getId());
        intent.setClass(mContext, PdfActivity2.class);
        mContext.startActivity(intent);
        mContext.overridePendingTransition(R.anim.zoomin, R.anim.alpha_outto);
    }

    private class MyLikeAdapter extends CommonAdapter<Comments>{


        public MyLikeAdapter(Context context, List<Comments> data)
        {
            super(context, R.layout.item_my_like, data);
        }
        @Override
        public void convert(ViewHolderHelper helper,  Comments resultBean, final int position)
        {
            ImageView photoCover = helper.getView(R.id.img_item_my_like_photo);
            ImageView headCover = helper.getView(R.id.img_my_like_head);
            LinearLayout insideLayout = helper.getView(R.id.ll_my_like_inside);
            insideLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
//                    startByType(resultBean.resource.type, position);
                }
            });
            Picasso.with(context).load(resultBean.user.logo).fit().into(headCover);
            Picasso.with(context).load(resultBean.resource.cover).fit().into(photoCover);

            helper.setText(R.id.tv_my_like_name, "user name");
            helper.setText(R.id.ll_my_like_content,"This is content.....................");
            helper.setText(R.id.tv_item_my_like_title,resultBean.resource.name);
            helper.setText(R.id.tv_item_my_like_uploader,resultBean.resource.uploader_name);
            helper.setText(R.id.tv_my_like_like_num,resultBean.count_praise+"");
            helper.setText(R.id.tv_my_like_comment_num,resultBean.count_reply+"");
        }
        public void setData(List<Comments> listResult){
            this.datas = listResult ;
            notifyDataSetChanged();
        }
    }


    @OnClick({R.id.back_btn})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.back_btn:
                onBackPressed();
                break;
        }
    }
}
