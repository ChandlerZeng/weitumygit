package com.libtop.weitu.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


/**
 * Created by LianTu on 2016-9-7.
 */
public class ThemeDetailActivity extends BaseActivity
{

    @Bind(R.id.tv_theme_detail_title)
    TextView tvThemeDetailTitle;
    @Bind(R.id.tv_theme_detail_follow_num)
    TextView tvThemeDetailFollowNum;
    @Bind(R.id.tv_theme_detail_follow)
    TextView tvThemeDetailFollow;
    @Bind(R.id.img_theme_detail_photo)
    ImageView imThemeDetailPhoto;
    @Bind(R.id.lv_theme_detail)
    ListView lvThemeDetail;

    private RankAdapter rankAdapter;
    private List<ResultBean> mData = new ArrayList<>();

    private boolean isFollow = true;

    public static final int VIDEO = 1, AUDIO = 2, DOC = 3, PHOTO = 4, BOOK = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setInjectContentView(R.layout.activity_main_theme_detail);
        rankAdapter = new RankAdapter(mContext, mData);
        initView();
        getData();
    }


    private void initView()
    {
        String cover = getIntent().getStringExtra("cover");
        if (!TextUtils.isEmpty(cover)){
            Picasso.with(mContext).load(cover).fit().into(imThemeDetailPhoto);
        }
        tvThemeDetailTitle.setText("Java测试");
        tvThemeDetailFollowNum.setText("66");


        lvThemeDetail.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                startByType(mData.get(position).type, position);
            }
        });
        lvThemeDetail.setAdapter(rankAdapter);
    }

    private void getData()
    {
        showLoding();
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

            }


            @Override
            public void onNext(List<ResultBean> resultBeen)
            {
                dismissLoading();
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


    @OnClick({R.id.back_btn, R.id.rl_theme_detail_title,R.id.tv_theme_detail_follow})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.back_btn:
                onBackPressed();
                break;
            case R.id.rl_theme_detail_title:
                titleClick();
                break;
            case R.id.tv_theme_detail_follow:
                followClick();
                break;
        }
    }


    private void followClick()
    {
        if (isFollow){
            requestUnFollow();
            tvThemeDetailFollow.setText("已关注");
            tvThemeDetailFollow.setBackgroundResource(R.drawable.shape_bg_follow_press);
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
                tvThemeDetailFollow.setText("关注");
                tvThemeDetailFollow.setBackgroundResource(R.drawable.shape_bg_follow);
            }
        }, null);
    }


    private void titleClick()
    {
        Intent intent = new Intent(mContext,ThemeInfoActivity.class);
        startActivity(intent);
    }
}
