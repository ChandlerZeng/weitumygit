package com.libtop.weitu.activity.search.dynamicCardLayout;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.search.ImagePagerActivity2;
import com.libtop.weitu.activity.search.adapter.DynamicCardAdapter;
import com.libtop.weitu.activity.search.dto.DynamicCardBean;
import com.libtop.weitu.activity.search.dto.ImageAlbumBean;
import com.libtop.weitu.activity.search.dto.ImageListBean;
import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.utils.Preference;
import com.libtop.weitu.utils.CheckUtil;
import com.libtop.weitu.utils.JSONUtil;
import com.libtop.weitu.utils.NetworkUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;


public class DynamicCardActivity extends BaseActivity
{

    @Nullable
    @Bind(R.id.title)
    TextView tvTitle;

    private static final int COLUMNCOUNT = 3;

    private Bundle mBundle;
    private String id, type;
    private ImageAlbumBean imageAlbumBean;

    String favorite;

    private RecyclerView mRecyclerView;
    private ArrayList<String> urlLists = new ArrayList<>();
    ArrayList<String> idList = new ArrayList<String>();
    private DynamicCardAdapter adapter;

    public static final int RESULT_UPDATE = 250;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setInjectContentView(R.layout.activity_dynamic_card);

        mRecyclerView = (RecyclerView) this.findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(COLUMNCOUNT, StaggeredGridLayoutManager.VERTICAL));//设置RecyclerView布局管理器为2列垂直排布
        adapter = new DynamicCardAdapter(this, urlLists, COLUMNCOUNT);
        mRecyclerView.setAdapter(adapter);
        adapter.setOnClickListener(new DynamicCardAdapter.OnItemClickListener()
        {
            @Override
            public void ItemClickListener(View view, int postion)
            {
                openImage(postion);
            }
        });


        mPreference = new Preference(this);
        mBundle = getIntent().getExtras();
        id = mBundle.getString("id");
        type = mBundle.getString("type");
        init();
    }


    private void openImage(int position)
    {
        Intent intent = new Intent(DynamicCardActivity.this, ImagePagerActivity2.class);
        intent.putExtra("position", position);
        intent.putExtra("see_pic", 2);
        intent.putExtra(ImagePagerActivity2.DEFAULT_SELECTED_LIST, urlLists);
        intent.putExtra("favorite", favorite);
        intent.putExtra("cover", imageAlbumBean.cover);
        intent.putExtra("uploadUsername", imageAlbumBean.uploadUsername);
        intent.putExtra("imageID", imageAlbumBean.id);
        intent.putExtra(ImagePagerActivity2.ID_LIST, idList);
        startActivityForResult(intent, 1);
    }


    private void init()
    {
        getImageList();
    }


    public void getImageList()
    {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
        params.put("ip", NetworkUtil.getLocalIpAddress2(DynamicCardActivity.this));
        params.put("method", "imageAlbum.get");
        if (!CheckUtil.isNull(mPreference.getString(Preference.uid)))
        {
            params.put("uid", mPreference.getString(Preference.uid));
        }
        showLoding();
        HttpRequest.loadWithMap(params).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {
                dismissLoading();
                Toast.makeText(mContext, "网络不给力，请稍后重试", Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onResponse(String json, int responseId)
            {
                dismissLoading();
                if (!TextUtils.isEmpty(json))
                {
                    DynamicCardBean dynamicCardBean = JSONUtil.readBean(json, DynamicCardBean.class);
                    if (dynamicCardBean == null)
                    {
                        return;
                    }
                    if (dynamicCardBean.code == 1)
                    {
                        imageAlbumBean = dynamicCardBean.imageAlbum;
                        favorite = Integer.toString(dynamicCardBean.favorite);
                        if (!TextUtils.isEmpty(imageAlbumBean.title))
                        {
                            tvTitle.setText(imageAlbumBean.title);
                        }
                        urlLists.clear();
                        idList.clear();
                        for (ImageListBean listBean : dynamicCardBean.imageList)
                        {
                            urlLists.add(listBean.url);
                            idList.add(listBean.id);
                        }
                        adapter.setNewData(urlLists);
                    }
                }
            }
        });

    }


    @Nullable
    @OnClick({R.id.back_btn})
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.back_btn:
                onBackPressed();
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_UPDATE)
        {
            if (data.getBooleanExtra("isCollect", false))
            {
                favorite = "1";
            }
            else
            {
                favorite = "0";
            }
        }
    }

}