package com.libtop.weitu.activity.main.videoUpload;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.main.adapter.VideoListAdapter;
import com.libtop.weitu.activity.main.dto.VideoFolderBean;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.utils.Preference;
import com.libtop.weitu.widget.dialog.TranLoading;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;


public class VideoMoveActivity extends Activity implements View.OnClickListener
{
    private VideoListAdapter mAdapter;
    protected Preference mPreference;
    private List<VideoFolderBean> mInfos = new ArrayList<VideoFolderBean>();
    ListView listView;
    private String fid;
    protected TranLoading mLoading;
    Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_move);
        mLoading = new TranLoading(VideoMoveActivity.this);
        mAdapter = new VideoListAdapter(VideoMoveActivity.this, mInfos, null);
        listView = (ListView) findViewById(R.id.select_type_list);
        listView.setAdapter(mAdapter);
        fid = getIntent().getStringExtra("fid");
        mPreference = new Preference(VideoMoveActivity.this);
        findViewById(R.id.exit).setOnClickListener(this);
        findViewById(R.id.ll_new_folderBtn).setOnClickListener(this);
        requestVideos();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                setFid(position);
            }
        });
        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg)
            {
                super.handleMessage(msg);
                switch (msg.what)
                {
                    case 1:
                        Toast.makeText(VideoMoveActivity.this, "移动成功", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(VideoMoveActivity.this, "移动失败", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        };
    }


    private void requestVideos()
    {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("page", 1);
        params.put("uid", mPreference.getString(Preference.uid));
        params.put("method", "mediaAlbum.query");
        HttpRequest.loadWithMap(params).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {

            }


            @Override
            public void onResponse(String json, int id)
            {
                if (TextUtils.isEmpty(json))
                {
                    return;
                }
                mInfos.clear();
                Type collectionType = new TypeToken<List<VideoFolderBean>>()
                {
                }.getType();
                List<VideoFolderBean> beans = new Gson().fromJson(json, collectionType);
                for (VideoFolderBean bean : beans)
                {
                    mInfos.add(bean);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void onClick(View v)
    {
        int a = v.getId();
        switch (a)
        {
            case R.id.exit:
                finish();
                break;
            case R.id.ll_new_folderBtn:
                Intent myIntent = new Intent();
                Bundle bundle2 = new Bundle();
                bundle2.putString(ContentActivity.FRAG_CLS, VideoNewFolderFragment.class.getName());
                myIntent.putExtras(bundle2);
                myIntent.setClass(VideoMoveActivity.this, ContentActivity.class);
                startActivity(myIntent);
                break;
        }
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        requestVideos();
    }


    private void setFid(final int position)
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", fid);
        params.put("aid", mInfos.get(position).id);
        params.put("method", "media.changeAlbum");
        mLoading.show();
        HttpRequest.loadWithMap(params).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {

            }


            @Override
            public void onResponse(String json, int id)
            {
                if (!TextUtils.isEmpty(json))
                {
                    if (mLoading.isShowing())
                    {
                        mLoading.dismiss();
                    }
                    try
                    {
                        JSONObject mjson = new JSONObject(json);
                        int code = mjson.getInt("code");
                        Message msg = handler.obtainMessage();
                        if (code == 1)
                        {
                            msg.what = 1;
                            handler.sendMessage(msg);
                        }
                        else
                        {
                            msg.what = 2;
                            handler.sendMessage(msg);
                        }
                        finish();
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                        if (mLoading.isShowing())
                        {
                            mLoading.dismiss();
                        }
                    }
                    return;
                }
                if (mLoading.isShowing())
                {
                    mLoading.dismiss();
                }
            }
        });
    }


}
