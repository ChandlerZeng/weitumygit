package com.libtop.weitu.activity.main.videoUpload;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.ContentFragment;
import com.libtop.weitu.activity.main.adapter.VideoCoverAdapter;
import com.libtop.weitu.activity.main.dto.VideoBean;
import com.libtop.weitu.eventbus.MessageEvent;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.widget.listview.HorizontalListView;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;


/**
 * Created by LianTu on 2016/5/4.
 */
public class VideoCoverFragment extends ContentFragment
{
    @Bind(R.id.img_video_cover)
    ImageView mImgVideoCover;

    @Bind(R.id.title)
    TextView mTitleText;


    @Bind(R.id.hlv_cover)
    HorizontalListView mHListView;

    private VideoCoverAdapter mAdapter;

    private List<String> mInfos = new ArrayList<String>();
    private Bundle bundle;
    private VideoBean videoBean;
    private int choseSequence = 0;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mAdapter = new VideoCoverAdapter(mContext, mInfos);
        bundle = ((ContentActivity) getActivity()).getCurrentExtra();
        videoBean = new Gson().fromJson(bundle.getString("videoBean"), VideoBean.class);
        getVideoCover();
    }


    @Override
    protected int getLayoutId()
    {
        return R.layout.fragment_video_cover;
    }


    private void getVideoCover()
    {
        //weitu.bookus.cn/media/snapshot.json?text={"id":"5742ac54984e88f24d858640","method":"media.snapshot"}
        //然后取他的snapshotServer字段，snapshotServer再拼接ID-1.jpg 到ID-10.jpg
        //        http://nt1.libtop.com/snapshot/571727c6ce76e3ded34a4637-1.jpg
        //        这个是获取视频截图的，其中ID+-1到-10，一共获取10张
        showLoding();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", videoBean.videoId);
        params.put("method", "media.snapshot");
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
                try
                {
                    JSONObject jsonObject = new JSONObject(json);
                    String serverUrl = jsonObject.getString("snapshotServer");
                    if (TextUtils.isEmpty(serverUrl))
                    {
                        return;
                    }
                    mInfos.clear();
                    for (int i = 1; i < 11; i++)
                    {
                        String imgUrl = serverUrl + videoBean.videoId + "-" + i + ".jpg";
                        mInfos.add(imgUrl);
                    }
                    mAdapter.notifyDataSetChanged();

                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void onCreation(View root)
    {
        setTitle();
        mHListView.setAdapter(mAdapter);
        mHListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                String imgPath = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(imgPath))
                {
                    Picasso.with(mContext).load(imgPath).error(R.drawable.default_error).placeholder(R.drawable.default_image).fit().into(mImgVideoCover);
                }
                choseSequence = position + 1;
            }
        });

    }


    @Nullable
    @OnClick({R.id.back_btn, R.id.btn_finish})
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.back_btn:
                onBackPressed();
                break;
            case R.id.btn_finish:
                requestCover();
                break;
        }
    }


    private void requestCover()
    {
        if (choseSequence != 0)
        {
            videoBean.coverUrl = mInfos.get(choseSequence - 1);
            videoBean.coverSequence = choseSequence;
            Bundle bd = new Bundle();
            bd.putString("target", VideoCompleteFolderFragment.class.getName());
            bd.putString("videoBean", new Gson().toJson(videoBean));
            EventBus.getDefault().post(new MessageEvent(bd));
        }
        onBackPressed();
    }


    private void setTitle()
    {
        mTitleText.setText("编辑封面");
    }
}
