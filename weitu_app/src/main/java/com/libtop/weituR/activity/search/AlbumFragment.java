package com.libtop.weituR.activity.search;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.ContentActivity;
import com.libtop.weituR.activity.search.dto.ImageResult;
import com.libtop.weituR.base.impl.ImgFragment;
import com.libtop.weituR.http.HttpRequest;
import com.libtop.weituR.utils.CheckUtil;
import com.libtop.weituR.utils.DisplayUtils;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import okhttp3.Call;

/**
 * Created by Administrator on 2015/12/24 0024.
 */
public class AlbumFragment extends ImgFragment {
    @Bind(R.id.back_btn)
    ImageView mBackBtn;
    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.grid_view)
    GridView mGrid;
    @Bind( R.id.null_txt)
    TextView mNullTxt;
    @Bind(R.id.root_view)
    View mRoot;


    private List<ImageResult> mData=new ArrayList<ImageResult>();
    private Bundle mCached;

    private AlbumPopWindow mAlbumPop;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_album_imgs;
    }

    @Override
    public void onCreation(View root) {
        mCached=((ContentActivity)mContext).getCurrentExtra();
        mTitle.setText(mCached.getString("title"));
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mAlbumPop=new AlbumPopWindow(mContext,getUrls());

        mGrid.setAdapter(mAdapter);
        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mAlbumPop.show(mRoot,position);
            }
        });
        load();
    }

    @Override
    public void onBackPressed() {
        if(mCached.getBoolean(ContentActivity.FRAG_ISBACK)){
            ((ContentActivity)mContext).popBack();
        }else{
            mContext.finish();
        }
    }

    //    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_album_imgs);
//        mLoading = new TranLoading(context);
//        mCached=getIntent().getExtras();
//
//        mTitle.setText(mCached.getString("title"));
//        mBackBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
//
//        mAlbumPop=new AlbumPopWindow(context,getUrls());
//
//        mGrid.setAdapter(mAdapter);
//        mGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                mAlbumPop.show(mRoot,position);
//            }
//        });
//        load();
//    }

    private void load(){
        if (mCached==null){
            return;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("aid", mCached.getString("id"));
        showLoding();
        HttpRequest.loadWithMap(params)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String json, int id) {
                        Log.w("json", json);
                        dismissLoading();
                        if (CheckUtil.isNullTxt(json)) {
                            mNullTxt.setText("未搜索到相关记录");
                            mNullTxt.setVisibility(View.VISIBLE);
                            return;
                        }
                        if (!CheckUtil.isNull(json)) {
                            try {
                                JSONArray array = new JSONArray(json);
                                for (int i = 0; i < array.length(); i++) {
                                    ImageResult result = new ImageResult();
                                    result.form(array.getJSONObject(i));
                                    mData.add(result);
                                }
                                mAdapter.notifyDataSetChanged();
                                if (array.length() == 0) {
                                    mNullTxt.setText("未搜索到相关记录");
                                    mNullTxt.setVisibility(View.VISIBLE);
                                } else {
                                    mNullTxt.setVisibility(View.GONE);
                                }
                            } catch (JSONException e) {
                                mNullTxt.setText("未搜索到相关记录");
                                mNullTxt.setVisibility(View.VISIBLE);
                                e.printStackTrace();
                            }
                        } else {
                            showToast("未搜索到相关记录");
                        }
                    }
                });
    }

    private BaseAdapter mAdapter=new BaseAdapter() {

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView==null){
                convertView= LayoutInflater.from(mContext).inflate(R.layout.item_grid_result,null,false);
            }
            ImageView img=(ImageView)convertView.findViewById(R.id.icon);
            TextView name=(TextView)convertView.findViewById(R.id.title);

            int imgsize=( DisplayUtils.getDisplayWith(mContext)
                    -DisplayUtils.dp2px(mContext,46))/4;
            ViewGroup.LayoutParams params=img.getLayoutParams();
            params.width=imgsize;
            params.height=imgsize;
            img.setLayoutParams(params);

            ImageResult content=mData.get(position);

            name.setText(content.title);
//            ImageLoader.getInstance().displayImage(content.url, img,mOptions);
            if (!TextUtils.isEmpty(content.url))
                Picasso.with(mContext).load(content.url).into(img);
//            x.image().bind(img,content.url,mOptions);
            return convertView;
        }

        @Override
        public void notifyDataSetChanged() {
            mAlbumPop.resetData(getUrls());
            super.notifyDataSetChanged();
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAlbumPop!=null) mAlbumPop.dismiss();
    }

    private List<String> getUrls(){
        List<String> urls=new ArrayList<String>();
        for (ImageResult result:mData){
            urls.add(result.url);
        }
        return urls;
    }

}
