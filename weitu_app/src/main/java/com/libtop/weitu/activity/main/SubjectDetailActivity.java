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

import com.libtop.weitu.R;
import com.libtop.weitu.activity.classify.adapter.ClassifySubDetailAdapter;
import com.libtop.weitu.activity.main.dto.SubjectDetailBean;
import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.test.CategoryResult;
import com.libtop.weitu.test.Resource;
import com.libtop.weitu.test.SubjectResource;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.ContextUtil;
import com.libtop.weitu.utils.ImageLoaderUtil;
import com.libtop.weitu.utils.JsonUtil;
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
    private List<CategoryResult> mData = new ArrayList<>();

    private boolean isFollow = true;

    public static final int VIDEO = 1, AUDIO = 2, DOC = 3, PHOTO = 4, BOOK = 5;

    private String data[];

    private HeaderViewHolder headerViewHolder;

    private String idString;

    private String titleString = "";


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

//    http://weitu.bookus.cn/subject/resources.json?text={"sid":"56f97d8d984e741f1420ayy","method":"subject.resources"}
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
                SubjectResource subjectResource = JsonUtil.fromJson(json, SubjectResource.class);
                if (subjectResource==null){
                    Toast.makeText(mContext,R.string.netError,Toast.LENGTH_SHORT).show();
                    return;
                }
                mData.clear();
                mData.addAll(subjectResource.resources);
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
                    Resource resource = (Resource) arg0.getAdapter().getItem(position);
                    ContextUtil.openResourceByType(mContext,resource.type, resource.rid);
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
                Toast.makeText(mContext,R.string.netError,Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onResponse(String json, int id)
            {
                SubjectDetailBean subjectDetailBean = JsonUtil.fromJson(json, SubjectDetailBean.class);
                if (subjectDetailBean==null){
                    return;
                }
                changeView(subjectDetailBean);
            }
        });
    }


    private void changeView(SubjectDetailBean subjectDetailBean)
    {
        ImageLoaderUtil.loadImage(mContext,pullZoomListView.getHeaderImageView(),subjectDetailBean.subject.cover,ImageLoaderUtil.DEFAULT_BIG_IMAGE_RESOURCE_ID);
        title.setText(subjectDetailBean.subject.title);
        titleString = subjectDetailBean.subject.title;
        headerViewHolder.tvThemeDetailTitle.setText(titleString);
        headerViewHolder.tvThemeDetailFollowNum.setText(subjectDetailBean.subject.follows+"");
        if (Preference.instance(mContext).getString(Preference.uid).equals(subjectDetailBean.subject.uid)){
            headerViewHolder.tvThemeDetailFollow.setBackgroundResource(R.drawable.shape_bg_edit);
            headerViewHolder.tvThemeDetailFollow.setText("编辑");
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

        // 通过构造函数来初始化ButterKnife
        public HeaderViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        // 在Activity/Fragment的onDestroy里一定要调用unBind方法
        public void unBind() {
            ButterKnife.unbind(this);
        }

        @OnClick({ R.id.rl_theme_detail_title,R.id.tv_theme_detail_follow})
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
            }
        }
    }


    private void followClick()
    {
        if (isFollow){
            requestUnFollow();
            headerViewHolder.tvThemeDetailFollow.setText("已关注");
            headerViewHolder.tvThemeDetailFollow.setBackgroundResource(R.drawable.shape_bg_follow_press);
        }else {
            showPopWindow();
            requestFollow();
        }
        isFollow = !isFollow;

    }


    private void requestFollow()
    {
//        Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(),
//                R.drawable.bg_new_subject);
//        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("uid", Preference.instance(mContext).getString(Preference.uid));
//        params.put("title", etSubjectTitle.getText().toString());
//        params.put("introduction", etSubjectDesc.getText().toString());
//        params.put("label1", label1);
//        params.put("cover", ClippingPicture.bitmapToBase64(icon));
//        params.put("method", "subject.save");
//        HttpRequest.loadWithMap(params).execute(new StringCallback()
    }


    private void requestUnFollow()
    {
    }


    private void showPopWindow(){
        String title = "您确定要取消关注？";
        final AlertDialogUtil dialog = new AlertDialogUtil();
        dialog.showDialog(mContext, title, "确定", "取消", new MyAlertDialog.MyAlertDialogOnClickCallBack()
        {
            @Override
            public void onClick()
            {
                headerViewHolder.tvThemeDetailFollow.setText("关注");
                headerViewHolder.tvThemeDetailFollow.setBackgroundResource(R.drawable.shape_bg_edit);
            }
        }, null);
    }


    private void titleClick()
    {
        Intent intent = new Intent(mContext,SubjectInfoActivity.class);
        intent.putExtra("cover","");
        startActivity(intent);
    }
}
