package com.libtop.weitu.activity.search;

import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.search.dto.MediaResult;
import com.libtop.weitu.activity.source.AudioPlayActivity;
import com.libtop.weitu.base.impl.ImgFragment;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.utils.CheckUtil;
import com.libtop.weitu.utils.ContantsUtil;
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
public class MediaDetailFragment extends ImgFragment {
    @Bind(R.id.back_btn)
    ImageView mBackBtn;
    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.icon)
    ImageView mIcon;
    @Bind(R.id.author)
    TextView mAuthor;
    @Bind(R.id.type)
    TextView mType;
    @Bind(R.id.heat)
    TextView mHeat;
    @Bind(R.id.collected)
    TextView mCollected;
    @Bind(R.id.browsed)
    TextView mBrowsed;
    @Bind(R.id.pages)
    TextView mPages;
    @Bind(R.id.list)
    ListView mPageList;

    private Bundle mCached;

    private List<MediaResult> mData=new ArrayList<MediaResult>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_media_detail;
    }

    @Override
    public void onCreation(View root) {
        mCached=((ContentActivity)mContext).getCurrentExtra();
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mPageList.setAdapter(mAdapter);
        mPageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    MediaResult result = mData.get(position);
                    if (result != null) {
                        Bundle bundle = new Bundle();
                        if ("video".equals(mCached.getString("type"))) {
                            bundle.putString(VideoPlayActivity2.MEDIA_NAME, result.title);
                            bundle.putString(VideoPlayActivity2.MEDIA_PATH, result.url);
                            bundle.putInt("index",position);
                            mContext.startActivity(bundle, VideoPlayActivity2.class);
                        } else if ("audio".equals(mCached.getString("type"))) {
                            bundle.putInt("media_list_position", position);
                            bundle.putParcelableArrayList("media_list", (ArrayList<? extends Parcelable>) mData);
//                            bundle.putString(AudioPlayActivity.MEDIA_NAME, result.title);
//                            bundle.putString(AudioPlayActivity.MEDIA_PATH, result.url);
                            mContext.startActivity(bundle, AudioPlayActivity.class);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        setDetail();
        loadIndex();

    }


    private void setDetail(){
        mTitle.setText(mCached.getString("title"));
        String artist=mCached.getString("artist");
        String type=mCached.getString("type");
        mAuthor.setText(Html.fromHtml("<font color=\"#999999\">艺术家: </font><font color=\"#666666\">"
                + (TextUtils.isEmpty(artist) ? "未知" : artist)
                + "</font>"));
        mType.setText(Html.fromHtml("<font color=\"#999999\">类型: </font><font color=\"#666666\">"
                + (TextUtils.equals(type, "video") ? "视频" : (TextUtils.equals(type, "audio") ? "音频" : "未知"))
                + "</font>"));
        mHeat.setText(Html.fromHtml("<font color=\"#999999\">热度: </font><font color=\"#666666\">"
                + mCached.getInt("hot") + "</font>"));
        mCollected.setText(Html.fromHtml("<font color=\"#999999\">收藏: </font><font color=\"#666666\">"
                +mCached.getInt("favorite")+"</font>"));
        mBrowsed.setText(Html.fromHtml("<font color=\"#999999\">浏览: </font><font color=\"#666666\">"
                + mCached.getInt("views") + "</font>"));
        mPages.setText(Html.fromHtml("<font color=\"#999999\">集数: </font>"));
//        ImageLoader.getInstance().displayImage(
//                ContantsUtil.getCoverUrl(mCached.getString("id")), mIcon,
//                mOptions);
        Picasso.with(mContext).load(ContantsUtil.getCoverUrl(mCached.getString("id"))).into(mIcon);
//        x.image().bind(mIcon,ContantsUtil.getCoverUrl(mCached.getString("id")),mOptions);
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        mData.clear();
//        mAdapter.notifyDataSetChanged();
//        mCached=intent.getExtras();
//        mTitle.setText(mCached.getString("title"));
//        setDetail();
//        loadIndex();
//    }

    private BaseAdapter mAdapter=new BaseAdapter() {
        @Override
        public int getCount() {
            return mData.size()<3?3:mData.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView==null){
                convertView= LayoutInflater.from(mContext).inflate(R.layout.item_list_indexs,null,false);
            }
            TextView titleText=(TextView)convertView.findViewById(R.id.title);
            TextView additionText=(TextView)convertView.findViewById(R.id.addition);
            additionText.setText("");
            try {
                MediaResult result=mData.get(position);
                if (result==null){
                    titleText.setText("");
                }else {
//                    additionText.setText(position+"");
                    titleText.setText(result.title);
                }
            } catch (Exception e) {
//                e.printStackTrace();
                titleText.setText("");
            }
            return convertView;
        }
    };

    private void loadIndex(){
        if (mCached==null){
            return;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("type"
                , mCached.getString("type").equals("video")?1:2);
        params.put("aid",mCached.getString("id"));
        params.put("method", "media.list");
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
                            return;
                        }
                        if (!CheckUtil.isNull(json)) {
                            try {
                                JSONArray array = new JSONArray(json);
                                for (int i = 0; i < array.length(); i++) {
                                    MediaResult result = new MediaResult();
                                    result.form(array.getJSONObject(i));
                                    mData.add(result);
                                }
                                resetCache();
                                mAdapter.notifyDataSetChanged();
                                mPages.setText(Html.fromHtml("<font color=\"#999999\">集数: </font><font color=\"#666666\">"
                                        + mData.size() + "</font>"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            showToast("未搜索到相关记录");
                        }
                    }
                });
    }

    private void resetCache(){
//        DbManager dao=x.getDb(((AppApplication)mContext.getApplicationContext()).getDaoConfig());
//        try {
//            dao.delete(MediaResult.class);
//            for (MediaResult result:mData){
//                dao.save(result);
//            }
//        } catch (DbException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onBackPressed() {
        if(mCached.getBoolean(ContentActivity.FRAG_ISBACK)){
            ((ContentActivity)mContext).popBack();
        }else{
            mContext.finish();
        }
    }
}
