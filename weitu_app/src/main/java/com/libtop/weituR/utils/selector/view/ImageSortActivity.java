package com.libtop.weituR.utils.selector.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weituR.http.HttpRequest;
import com.libtop.weituR.widget.dialog.TranLoading;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class ImageSortActivity extends Activity {
    public static final int DESCRIPTION_RETURNQ = 20;
    protected TranLoading mLoading;
    GridView mGridView;
    private VideoSortAdapter mAdapter;
    private ArrayList<VideoSortBean> lists = new ArrayList<VideoSortBean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_sort);
        mLoading = new TranLoading(ImageSortActivity.this);
        mGridView = (GridView) findViewById(R.id.gridview);
        mAdapter = new VideoSortAdapter(lists);
        mGridView.setAdapter(mAdapter);
        getUploadUrl();
        ((TextView) findViewById(R.id.title)).setText("分类");
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int code = lists.get(position).sortId;
                String name = lists.get(position).sortName;
                Intent data = new Intent();
                Bundle b = new Bundle();
                b.putString("name", name);
                b.putInt("code", code);
                data.putExtras(b);
                setResult(DESCRIPTION_RETURNQ, data);
                finish();
            }
        });
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getUploadUrl() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("method", "categories.root");
        showLoding();
        HttpRequest.loadWithMap(params)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String json, int id) {
                        dismissLoading();
                        if (!TextUtils.isEmpty(json)) {
                            //   showToast("没有相关数据");
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
                                Message msg = updataHandler.obtainMessage();
                                msg.what = 1;
                                updataHandler.sendMessage(msg);
                                dismissLoading();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                dismissLoading();
                            }
                            return;
                        }
                        dismissLoading();
                    }
                });
    }

    private void showLoding(){
        if (mLoading!=null&&!mLoading.isShowing())
            mLoading.show();
    }

    private void dismissLoading(){
        if (mLoading!=null&&mLoading.isShowing())
            mLoading.dismiss();
    }

    public class VideoSortBean {
        public String sortName;
        public int sortId;
    }

    class VideoSortAdapter extends BaseAdapter {

        private List<VideoSortBean> mlists;
        LayoutInflater mInflater;

        public VideoSortAdapter(List<VideoSortBean> nlist) {
            this.mlists = nlist;
            mInflater = (LayoutInflater) ImageSortActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setData(List<VideoSortBean> nlist) {
            this.mlists = nlist;
        }

        @Override
        public int getCount() {
            return mlists.size();
        }

        @Override
        public Object getItem(int position) {
            return mlists.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_grid_string, parent, false);
                holder = new ViewHolder();
                holder.image = (TextView) convertView.findViewById(R.id.text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.image.setText(mlists.get(position).sortName);
            return convertView;
        }

        class ViewHolder {
            TextView image;
        }
    }

    private Handler updataHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    mAdapter.setData(lists);
                    mAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };
}