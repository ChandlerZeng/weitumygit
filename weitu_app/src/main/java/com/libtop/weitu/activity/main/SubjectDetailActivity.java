package com.libtop.weitu.activity.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.classify.adapter.ClassifySubDetailAdapter;
import com.libtop.weitu.activity.main.dto.SubjectDetailBean;
import com.libtop.weitu.activity.user.dto.CollectBean;
import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.dao.ResultCodeDto;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.utils.Preference;
import com.libtop.weitu.utils.ContextUtil;
import com.libtop.weitu.utils.ImageLoaderUtil;
import com.libtop.weitu.utils.JSONUtil;
import com.libtop.weitu.utils.selector.utils.AlertDialogUtil;
import com.libtop.weitu.utils.selector.view.MyAlertDialog;
import com.libtop.weitu.widget.PullZoomListView;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;


/**
 * Created by LianTu on 2016-9-7.
 */
public class SubjectDetailActivity extends BaseActivity
{

    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.plv_subject_detail)
    PullZoomListView pullZoomListView;
    @Bind(R.id.title_container)
    LinearLayout titleContainer;
    @Bind(R.id.title)
    TextView title;


    private ClassifySubDetailAdapter classifySubDetailAdapter;
    private List<CollectBean> mData = new ArrayList<>();

    public static boolean isFollow = false;

    public static final int VIDEO = 1, AUDIO = 2, DOC = 3, PHOTO = 4, BOOK = 5;

    private String data[];

    private HeaderViewHolder headerViewHolder;

    private String idString;

    private String titleString = "";

    private SubjectDetailBean subjectDetailBean;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setInjectContentView(R.layout.activity_main_subject_detail);
        classifySubDetailAdapter = new ClassifySubDetailAdapter(mContext, mData);
        initView();
        requestData();
        requestListData();

    }

//    http://weitu.bookus.cn/subject/CollectBeans.json?text={"sid":"56f97d8d984e741f1420ayy","method":"subject.resources"}
    private void requestListData()
    {
        swipeRefreshLayout.setRefreshing(true);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sid", idString);
        params.put("method", "subject.resources");
        HttpRequest.loadWithMap(params).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {
                swipeRefreshLayout.setRefreshing(false);
            }


            @Override
            public void onResponse(String json, int id)
            {
                swipeRefreshLayout.setRefreshing(false);
                ArrayList<CollectBean> lists = JSONUtil.readBeanArray(json, CollectBean.class);
                if (lists == null){
                    Toast.makeText(mContext,R.string.netError,Toast.LENGTH_SHORT).show();
                    return;
                }
                mData.clear();
                mData.addAll(lists);
                classifySubDetailAdapter.setData(mData);
            }
        });
    }


    private void initView()
    {
        idString = getIntent().getStringExtra("id");
        if (!TextUtils.isEmpty(idString)){
        }

        pullZoomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (position>1){
                    CollectBean collectBean = (CollectBean) arg0.getAdapter().getItem(position);
                    if (collectBean.type == ContextUtil.BOOK){
                        ContextUtil.openResourceByType(mContext,collectBean.type, collectBean.target.getIsbn());
                    }else {
                        ContextUtil.openResourceByType(mContext,collectBean.type, collectBean.target.getId());
                    }
                }
            }
        });
        pullZoomListView.setAdapter(classifySubDetailAdapter);

        View view = LayoutInflater.from(mContext).inflate(R.layout.header_subject_detail,null);
        headerViewHolder = new HeaderViewHolder(view);
        pullZoomListView.addHeaderView(view,null,false);

        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW);
        swipeRefreshLayout.setProgressViewOffset(true,50,100);
        swipeRefreshLayout.setEnabled(false);

        pullZoomListView.setOnScrollListener(new AbsListView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState)
            {
            }


            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
            {
                int scrollY = pullZoomListView.getScrolledY();
                if (scrollY<315){
                    titleContainer.setAlpha(scrollY/(float)315);
                    title.setText("");
                }else {
                    titleContainer.setAlpha(1);
                    title.setText(titleString);
                }
            }
        });
        pullZoomListView.setOnRefreshListener(new PullZoomListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData();
            }
        });
    }

    //http://weitu.bookus.cn/subject/get.json?text={"id":"56f97d8d984e741f1420awr8","method":"subject.get"}
    private void requestData()
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", idString);
        params.put("method", "subject.get");
        HttpRequest.loadWithMap(params).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {
            }


            @Override
            public void onResponse(String json, int id)
            {
                SubjectDetailBean bean = JSONUtil.readBean(json, SubjectDetailBean.class);
                if (bean ==null){
                    return;
                }
                subjectDetailBean = bean;
                changeView(subjectDetailBean);
            }
        });
    }


    private void changeView(SubjectDetailBean subjectDetailBean)
    {
        ImageLoaderUtil.loadImage(mContext,pullZoomListView.getHeaderImageView(),subjectDetailBean.subject.getCover(),ImageLoaderUtil.RESOURCE_ID_IMAGE_BIG);
        titleString = subjectDetailBean.subject.getTitle();
        title.setText(titleString);
        headerViewHolder.tvThemeDetailTitle.setText(titleString);
        headerViewHolder.tvThemeDetailFollowNum.setText(subjectDetailBean.subject.getFollows()+"");
        if (subjectDetailBean.followed == 1){
            isFollow = true;
            headerViewHolder.tvThemeDetailFollow.setText("已关注");
            headerViewHolder.tvThemeDetailFollow.setBackgroundResource(R.drawable.shape_bg_follow_press);
        }else {
            isFollow = false;
            headerViewHolder.tvThemeDetailFollow.setText("关注");
            headerViewHolder.tvThemeDetailFollow.setBackgroundResource(R.drawable.shape_bg_follow);
        }
//        if (Preference.instance(mContext).getString(Preference.uid).equals(subjectDetailBean.subject.uid)){
//            headerViewHolder.tvThemeDetailEdit.setVisibility(View.VISIBLE);
//            headerViewHolder.tvThemeDetailFollow.setVisibility(View.GONE);
//        }
    }


    @Override
    protected void onStart()
    {
        super.onStart();
        if (isFollow){
            headerViewHolder.tvThemeDetailFollow.setText("已关注");
            headerViewHolder.tvThemeDetailFollow.setBackgroundResource(R.drawable.shape_bg_follow_press);
        }else {
            headerViewHolder.tvThemeDetailFollow.setText("关注");
            headerViewHolder.tvThemeDetailFollow.setBackgroundResource(R.drawable.shape_bg_follow);
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

    @Override
    public void onDestroy() {
        headerViewHolder.unBind();
        super.onDestroy();
    }

    class HeaderViewHolder {

        @Bind(R.id.tv_theme_detail_title)
        TextView tvThemeDetailTitle;
        @Bind(R.id.tv_theme_detail_follow_num)
        TextView tvThemeDetailFollowNum;
        @Bind(R.id.tv_theme_detail_follow)
        TextView tvThemeDetailFollow;
        @Bind(R.id.tv_theme_detail_edit)
        TextView tvThemeDetailEdit;

        // 通过构造函数来初始化ButterKnife
        public HeaderViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        // 在Activity/Fragment的onDestroy里一定要调用unBind方法
        public void unBind() {
            ButterKnife.unbind(this);
        }

        @OnClick({ R.id.rl_theme_detail_title,R.id.tv_theme_detail_follow,R.id.tv_theme_detail_edit})
        public void onClick(View view)
        {
            switch (view.getId())
            {
                case R.id.rl_theme_detail_title:
                    titleClick();
                    break;
                case R.id.tv_theme_detail_follow:
                    followClick();
                    break;
                case R.id.tv_theme_detail_edit:
                    editClick();
                    break;
            }
        }
    }


    private void editClick()
    {
        Intent intent = new Intent(mContext, NewSubjectActivity.class);
        intent.putExtra("isEdit",true);
        intent.putExtra("id",idString);
        mContext.startActivity(intent);
    }


    private void followClick()
    {
        if (!isFollow){
            requestFollow();
        }else {
            showPopWindow();
        }
    }

//    http://weitu.bookus.cn/subject/follow.json?text={"sid":"56f97d8d984e741f1420axx","uid":"56f97d8d984e741f1420awr8","method":"subject.follow"}
    private void requestFollow()
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sid", idString);
        params.put("uid", Preference.instance(mContext).getString(Preference.uid));
        params.put("method", "subject.follow");
        HttpRequest.loadWithMap(params).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {

            }


            @Override
            public void onResponse(String json, int id)
            {
                ResultCodeDto resultCodeDto = JSONUtil.readBean(json, ResultCodeDto.class);
                if (resultCodeDto == null){
                    Toast.makeText(mContext,R.string.netError,Toast.LENGTH_SHORT).show();
                    return;
                }
                if (resultCodeDto.code == 1){
                    isFollow = true;
                    Toast.makeText(mContext,"主题关注成功",Toast.LENGTH_SHORT).show();
                    headerViewHolder.tvThemeDetailFollow.setText("已关注");
                    headerViewHolder.tvThemeDetailFollow.setBackgroundResource(R.drawable.shape_bg_follow_press);

                }else
                {
                    Toast.makeText(mContext,"主题关注失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

//    http://weitu.bookus.cn/subject/unfollow.json?text={"sid":"56f97d8d984e741f1420axx","uid":"56f97d8d984e741f1420awr8","method":"subject.unfollow"}
    private void requestUnFollow()
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("sid", idString);
        params.put("uid", Preference.instance(mContext).getString(Preference.uid));
        params.put("method", "subject.unfollow");
        HttpRequest.loadWithMap(params).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {

            }


            @Override
            public void onResponse(String json, int id)
            {
                ResultCodeDto resultCodeDto = JSONUtil.readBean(json, ResultCodeDto.class);
                if (resultCodeDto == null){
                    Toast.makeText(mContext,R.string.netError,Toast.LENGTH_SHORT).show();
                    return;
                }
                if (resultCodeDto.code == 1){
                    isFollow = false;
                    Toast.makeText(mContext,"取消关注成功",Toast.LENGTH_SHORT).show();
                    headerViewHolder.tvThemeDetailFollow.setText("关注");
                    headerViewHolder.tvThemeDetailFollow.setBackgroundResource(R.drawable.shape_bg_follow);
                }else
                {
                    Toast.makeText(mContext,"取消关注失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void showPopWindow(){
        String title = "您确定要取消关注？";
        final AlertDialogUtil dialog = new AlertDialogUtil();
        dialog.showDialog(mContext, title, "确定", "取消", new MyAlertDialog.MyAlertDialogOnClickCallBack()
        {
            @Override
            public void onClick()
            {
                requestUnFollow();
            }
        }, null);
    }


    private void titleClick()
    {
        Intent intent = new Intent(mContext,SubjectInfoActivity.class);
        intent.putExtra("subjectDetailBean",new Gson().toJson(subjectDetailBean));
        startActivity(intent);
    }
}
