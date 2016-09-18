package com.libtop.weitu.activity.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.comment.CommentDetailActivity;
import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.test.CommentBean;
import com.libtop.weitu.test.Comments;
import com.libtop.weitu.utils.ContantsUtil;
import com.libtop.weitu.utils.JsonUtil;
import com.libtop.weitu.utils.OpenResUtil;
import com.libtop.weitu.viewadapter.CommonAdapter;
import com.libtop.weitu.viewadapter.ViewHolderHelper;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import okhttp3.Call;


/**
 * Created by LianTu on 2016-9-12.
 */
public class MyLikeActivity extends BaseActivity
{

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.lv_my_like)
    ListView lvMyLike;

    public static final int VIDEO = 1, AUDIO = 2, DOC = 3, PHOTO = 4, BOOK = 5;

    private MyLikeAdapter myLikeAdapter;

    private List<Comments> mData = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setInjectContentView(R.layout.activity_main_my_like);
        initView();
        reqestData();
    }


    private void reqestData()
    {
        showLoding();
        HttpRequest.newLoad(ContantsUtil.USER_COMMENT_LIST).execute(new StringCallback()
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
                    CommentBean commentBean = JsonUtil.fromJson(json, CommentBean.class);
                    myLikeAdapter.addAll(commentBean.comments);
                    mData.addAll(commentBean.comments);
                }
            }
        });
    }


    private void initView()
    {
        boolean isComment = getIntent().getBooleanExtra("isComment",false);
        title.setText("我赞过的");
        if (isComment)
            title.setText("我的评论");
        myLikeAdapter = new MyLikeAdapter(mContext);
        lvMyLike.setAdapter(myLikeAdapter);
        lvMyLike.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent intent = new Intent(mContext,CommentDetailActivity.class);
                intent.putExtra("cid",mData.get(position).cid);
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });
    }


    private class MyLikeAdapter extends CommonAdapter<Comments>{



        public MyLikeAdapter(Context context)
        {
            super(context, R.layout.item_my_like);
        }


        @Override
        public void convert(ViewHolderHelper helper, final Comments comments, final int position)
        {
            ImageView photoCover = helper.getView(R.id.img_item_my_like_photo);
            ImageView headCover = helper.getView(R.id.img_my_like_head);
            LinearLayout insideLayout = helper.getView(R.id.ll_my_like_inside);
            insideLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    OpenResUtil.startByType(mContext,comments.resource.type, comments.resource.rid);
                }
            });
            Picasso.with(context).load(comments.user.logo)
                    .transform(new CropCircleTransformation())
                    .fit().into(headCover);
            Picasso.with(context).load(comments.resource.cover).fit().into(photoCover);

            helper.setText(R.id.tv_my_like_name,comments.user.name);
            helper.setText(R.id.ll_my_like_content,comments.content);
            helper.setText(R.id.tv_item_my_like_title,comments.resource.name);
            helper.setText(R.id.tv_item_my_like_uploader,comments.resource.uploader_name);
            helper.setText(R.id.tv_my_like_like_num,comments.count_praise+"");
            helper.setText(R.id.tv_my_like_comment_num,comments.count_reply+"");
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
