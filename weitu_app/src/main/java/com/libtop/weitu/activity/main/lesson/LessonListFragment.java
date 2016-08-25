package com.libtop.weitu.activity.main.lesson;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.main.adapter.LessonListAdapter;
import com.libtop.weitu.activity.main.dto.LessonData;
import com.libtop.weitu.activity.search.MediaDetailFragment;
import com.libtop.weitu.base.BaseFragment;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.utils.CollectionUtils;
import com.libtop.weitu.utils.JsonUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnItemClick;
import okhttp3.Call;

/**
 * Created by Administrator on 2016/1/20 0020.
 */
public class LessonListFragment extends BaseFragment{
    @Bind(R.id.title)
    TextView mTitleText;
    @Bind(R.id.list)
    ListView mListView;

    private List<LessonData.Item> mData=new ArrayList<LessonData.Item>();
    private LessonListAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter=new LessonListAdapter(mContext,mData);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_list_type;
    }

    @Override
    public void onCreation(View root) {
        setTitle();
        mListView.setAdapter(mAdapter);
        if (mData.isEmpty()) load();
    }

    private void setTitle(){
        mTitleText.setText(((ContentActivity)mContext).getCurrentExtra().getString("title"));
    }

    private void load(){
        showLoding();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", ((ContentActivity)mContext).getCurrentExtra().getString("id"));
        params.put("method", "specialCourse.get");
        HttpRequest.loadWithMap(params)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String json, int id) {
                        dismissLoading();
                        if (TextUtils.isEmpty(json)) {
                            showToast("未找到相关记录");
                            return;
                        }
                        LessonData date = JsonUtil.fromJson(json, new TypeToken<LessonData>() {
                        }.getType());
                        if (date==null||CollectionUtils.isEmpty(date.getMediaAlbums())) {
                            showToast("未找到相关记录");
                            return;
                        }
                        mData = date.getMediaAlbums();
                        mAdapter.update(mData);
                    }
                });
    }

    @Nullable
    @OnClick(R.id.back_btn)
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_btn:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        ((ContentActivity)mContext).popBack();
    }

    @Nullable @OnItemClick(value = R.id.list)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //播放页面
        Bundle bundle = new Bundle();
        LessonData.Item result=mData.get(position);
        bundle.putString("type", "video");
        bundle.putString("artist",result.getArtist());
        bundle.putString("id",result.getId());
        bundle.putString("title", result.getTitle());
        bundle.putInt("favorite", result.getFavorite());
        bundle.putInt("hot",result.getHot());
        bundle.putInt("views", result.getView());
        bundle.putString("cover", result.getCover());
        bundle.putBoolean(ContentActivity.FRAG_ISBACK,true);
        bundle.putString(ContentActivity.FRAG_CLS,MediaDetailFragment.class.getName());
        mContext.startActivity(bundle,ContentActivity.class);
    }
}
