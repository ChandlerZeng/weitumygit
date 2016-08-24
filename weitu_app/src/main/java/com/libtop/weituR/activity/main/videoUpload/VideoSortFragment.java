package com.libtop.weituR.activity.main.videoUpload;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.ContentActivity;
import com.libtop.weituR.activity.ContentFragment;
import com.libtop.weituR.activity.main.adapter.VideoSortAdapter;
import com.libtop.weituR.eventbus.MessageEvent;
import com.libtop.weituR.http.HttpRequest;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by LianTu on 2016/4/25.
 */
public class VideoSortFragment extends ContentFragment{

    @Bind(R.id.title)
    TextView mTitleText;

    @Bind(R.id.gridview)
    GridView mGridView;

    private VideoSortAdapter mAdapter;

    private ArrayList<VideoSortBean> lists = new ArrayList<VideoSortBean>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ContentActivity)getActivity()).getCurrentExtra();
        requestVideSort();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_video_sort;
    }


    @Override
    public void onCreation(View root) {
        setTitle();
        if (mAdapter==null) mAdapter=new VideoSortAdapter(mContext,lists);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                VideoSortBean item = (VideoSortBean) parent.getItemAtPosition(position);
                Bundle bm = new Bundle();
                bm.putString("target",VideoNewFolderFragment.class.getName());
                bm.putString("target1", VideoCompleteFolderFragment.class.getName());
                bm.putString("sort", item.sortName);
                bm.putInt("sortId",item.sortId );
                EventBus.getDefault().post(new MessageEvent(bm));
                onBackPressed();
            }
        });
    }


    @Nullable
    @OnClick({R.id.back_btn})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_btn:
                onBackPressed();
                break;
        }
    }
    private void setTitle(){
        mTitleText.setText("分类");
    }

    private void requestVideSort() {
        showLoding();
        //7.上传视频的文件夹的分类，选择分类后，上传的是分类的code字段
        //http://weitu.bookus.cn/categories/root.json?text={"method":"categories.root"}
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("method","categories.root");
        HttpRequest.loadWithMap(params)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String json, int id) {
                        dismissLoading();
                        Log.w("guanglog",json);
                        try {
                            lists.clear();
                            JSONArray array = new JSONArray(json);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject jsonObject = array.getJSONObject(i);
                                VideoSortBean bean = new VideoSortBean();
                                bean.sortName = jsonObject.getString("name");
                                bean.sortId = jsonObject.getInt("code");
                                lists.add(bean);
                            }
                            mAdapter.notifyDataSetChanged();
                            if (lists.size() == 0) {
                                showToast("没有相关数据");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (TextUtils.isEmpty(json)) {
                            if (mContext!=null){
                                showToast("没有相关数据");
                            }
                            return;
                        }
                    }
                });
    }

    public class VideoSortBean{
        public String sortName;
        public int sortId;
    }
}
