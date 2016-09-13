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

import com.google.gson.Gson;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.main.adapter.RankAdapter;
import com.libtop.weitu.activity.main.clickHistory.ResultBean;
import com.libtop.weitu.activity.search.BookDetailFragment;
import com.libtop.weitu.activity.search.VideoPlayActivity2;
import com.libtop.weitu.activity.search.dto.SearchResult;
import com.libtop.weitu.activity.search.dynamicCardLayout.DynamicCardActivity;
import com.libtop.weitu.activity.source.AudioPlayActivity2;
import com.libtop.weitu.activity.source.PdfActivity2;
import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.http.MapUtil;
import com.libtop.weitu.http.WeituNetwork;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.selector.utils.AlertDialogUtil;
import com.libtop.weitu.utils.selector.view.MyAlertDialog;
import com.libtop.weitu.widget.PullZoomListView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


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


    private RankAdapter rankAdapter;
    private List<ResultBean> mData = new ArrayList<>();

    private boolean isFollow = true;

    public static final int VIDEO = 1, AUDIO = 2, DOC = 3, PHOTO = 4, BOOK = 5;

    private String data[];

    private HeaderViewHolder headerViewHolder;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setInjectContentView(R.layout.activity_main_subject_detail);
        rankAdapter = new RankAdapter(mContext, mData);
        initView();
        getData();

    }


    private void initView()
    {
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW);
        swipeRefreshLayout.setProgressViewOffset(true,50,100);
        swipeRefreshLayout.setEnabled(false);

        String cover = getIntent().getStringExtra("cover");
        if (!TextUtils.isEmpty(cover)){
            Picasso.with(mContext).load(cover).fit().into(pullZoomListView.getHeaderImageView());
        }

        pullZoomListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                startByType(mData.get(position).type, position);
            }
        });
        pullZoomListView.setAdapter(rankAdapter);

        View view = LayoutInflater.from(mContext).inflate(R.layout.header_subject_detail,null);
        headerViewHolder = new HeaderViewHolder(view);
        pullZoomListView.addHeaderView(view);
        headerViewHolder.tvThemeDetailTitle.setText("Java测试");
        headerViewHolder.tvThemeDetailFollowNum.setText("66");
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
                }else {
                    titleContainer.setAlpha(1);
                }
            }
        });
        pullZoomListView.setOnRefreshListener(new PullZoomListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
    }

    private void getData()
    {
        swipeRefreshLayout.setRefreshing(true);
        HashMap<String, Object> map = new HashMap<>();
        map.put("uid", mPreference.getString(Preference.uid));
        map.put("method", "footprint.query");
        String[] arrays = MapUtil.map2Parameter(map);
        subscription = WeituNetwork.getWeituApi().getHistory(arrays[0], arrays[1], arrays[2]).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<ResultBean>>()
        {
            @Override
            public void onCompleted()
            {

            }


            @Override
            public void onError(Throwable e)
            {
                swipeRefreshLayout.setRefreshing(false);
            }


            @Override
            public void onNext(List<ResultBean> resultBeen)
            {
                swipeRefreshLayout.setRefreshing(false);
                mData.clear();
                mData = resultBeen;
                rankAdapter.setData(mData);
            }
        });


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
        result.id = mData.get(position).target.id;
        result.cover = mData.get(position).target.cover;
        Intent intent = new Intent(mContext, AudioPlayActivity2.class);
        intent.putExtra("resultBean", new Gson().toJson(result));
        mContext.startActivity(intent);
    }


    private void openVideo(int position)
    {
        SearchResult result = new SearchResult();
        result.id = mData.get(position).target.id;
        Intent intent = new Intent(mContext, VideoPlayActivity2.class);
        intent.putExtra("resultBean", new Gson().toJson(result));
        mContext.startActivity(intent);
    }


    private void openBook(int position)
    {
        Bundle bundle = new Bundle();
        bundle.putString("name", mData.get(position).target.title);
        bundle.putString("cover", mData.get(position).target.cover);
        bundle.putString("auth", mData.get(position).target.author);
        bundle.putString("isbn", mData.get(position).target.isbn);
        bundle.putString("publisher", mData.get(position).target.publisher);
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
        bundle.putString("id", mData.get(position).target.id);
        mContext.startActivity(bundle, DynamicCardActivity.class);
    }


    private void openDoc(int position)
    {
        Intent intent = new Intent();
        intent.putExtra("url", "");
        intent.putExtra("doc_id", mData.get(position).target.id);
        intent.setClass(mContext, PdfActivity2.class);
        mContext.startActivity(intent);
        mContext.overridePendingTransition(R.anim.zoomin, R.anim.alpha_outto);
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
                headerViewHolder.tvThemeDetailFollow.setBackgroundResource(R.drawable.shape_bg_follow);
            }
        }, null);
    }


    private void titleClick()
    {
        Intent intent = new Intent(mContext,SubjectInfoActivity.class);
        startActivity(intent);
    }
}
