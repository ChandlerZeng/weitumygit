package com.libtop.weitu.activity.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.comment.CommentDetailActivity;
import com.libtop.weitu.activity.main.dto.CommentDto;
import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.ContextUtil;
import com.libtop.weitu.utils.DateUtil;
import com.libtop.weitu.utils.ImageLoaderUtil;
import com.libtop.weitu.utils.JSONUtil;
import com.libtop.weitu.viewadapter.CommonAdapter;
import com.libtop.weitu.viewadapter.ViewHolderHelper;
import com.libtop.weitu.widget.view.XListView;
import com.zhy.http.okhttp.callback.StringCallback;

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
public class MyLikeActivity extends BaseActivity
{

    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.lv_my_like)
    XListView lvMyLike;

    public static final int VIDEO = 1, AUDIO = 2, DOC = 3, PHOTO = 4, BOOK = 5;

    private MyLikeAdapter myLikeAdapter;

    private List<CommentDto> mData = new ArrayList<>();

    private boolean isFromMyPraised = false;
    private int mCurPage = 1;

    private boolean hasData = true;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setInjectContentView(R.layout.activity_main_my_like);
        isFromMyPraised = getIntent().getBooleanExtra("isFromMyPraised",false);
        initView();
        reqestData(true);
    }


    private void reqestData(final boolean clean)
    {
        if (clean){
            mCurPage = 1;
            showLoding();
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("uid", Preference.instance(mContext).getString(Preference.uid));
        params.put("page", mCurPage);
        if (isFromMyPraised){
            params.put("method", "comment.myPraised");
        }else {
            params.put("method", "comment.myPost");
        }
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
                    if (clean){
                        mData.clear();
                    }
                    ArrayList<CommentDto> lists = JSONUtil.readBeanArray(json, CommentDto.class);
                    if (lists == null){
                        return;
                    }
                    lvMyLike.stopRefresh();
                    if (lists.size() < 10)
                    {
                        hasData = false;
                        lvMyLike.setPullLoadEnable(false);
                    }
                    else
                    {
                        hasData = true;
                        lvMyLike.setPullLoadEnable(true);
                    }
                    myLikeAdapter.addAll(lists);
                    mData.addAll(lists);
                    mCurPage++;
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
                position = position - 1;
                Intent intent = new Intent(mContext,CommentDetailActivity.class);
                intent.putExtra("cid",mData.get(position).getId());
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });
        lvMyLike.setPullLoadEnable(false);
        lvMyLike.setXListViewListener(new XListView.IXListViewListener()
        {
            @Override
            public void onRefresh()
            {
                reqestData(true);
            }


            @Override
            public void onLoadMore()
            {
                if (hasData)
                {
                    reqestData(false);
                }
            }
        });
        mCurPage = 1;
    }


    private class MyLikeAdapter extends CommonAdapter<CommentDto>{



        public MyLikeAdapter(Context context)
        {
            super(context, R.layout.item_my_like);
        }


        @Override
        public void convert(ViewHolderHelper helper, final CommentDto commentDto, final int position)
        {
            ImageView photoCover = helper.getView(R.id.img_item_my_like_photo);
            ImageView headCover = helper.getView(R.id.img_my_like_head);
            LinearLayout insideLayout = helper.getView(R.id.ll_my_like_inside);
            insideLayout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    ContextUtil.openResourceByType(mContext,commentDto.type, commentDto.getTid());
                }
            });
            ImageLoaderUtil.loadLogoImage(mContext, headCover, commentDto.getLogo());
            ImageLoaderUtil.loadImage(context,photoCover,commentDto.getLogo());

            helper.setText(R.id.tv_my_like_name,commentDto.getUsername());
            helper.setText(R.id.ll_my_like_content,commentDto.getContent());
            helper.setText(R.id.tv_item_my_like_title,commentDto.getTitle());
            helper.setText(R.id.tv_my_like_time, DateUtil.transformToShow(commentDto.getTimeline()));
            helper.setText(R.id.tv_item_my_like_uploader,commentDto.getUsername());
            helper.setText(R.id.tv_my_like_like_num,commentDto.praises+"");
            helper.setText(R.id.tv_my_like_comment_num,commentDto.replies+"");
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
