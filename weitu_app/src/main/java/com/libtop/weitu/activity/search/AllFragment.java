package com.libtop.weitu.activity.search;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.search.adapter.AllListAdapter;
import com.libtop.weitu.activity.search.dto.AllDto;
import com.libtop.weitu.activity.search.dto.SearchResult;
import com.libtop.weitu.activity.search.dynamicCardLayout.DynamicCardActivity;
import com.libtop.weitu.activity.source.AudioPlayActivity2;
import com.libtop.weitu.activity.source.PdfActivity2;
import com.libtop.weitu.base.impl.NotifyFragment;
import com.libtop.weitu.eventbus.MessageEvent;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.CheckUtil;
import com.libtop.weitu.utils.CollectionUtils;
import com.libtop.weitu.utils.JsonUtil;
import com.libtop.weitu.widget.listview.ScrollRefListView;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import okhttp3.Call;

/**
 * Created by LianTu on 2016/6/6.
 */
public class AllFragment extends NotifyFragment {
    @Bind(R.id.list)
    ScrollRefListView mListview;
    @Bind(R.id.null_txt)
    TextView mNullTxt;

    private AllListAdapter mAdapter;
    private List<AllDto> mData=new ArrayList<AllDto>();
    private boolean isCreate = false, hasData = true,isFirstCreate = false;
    private int curPage = 1;

    private String sortType = "view";
    public static final String VIDEO="video-album",AUDIO="audio-album",DOC="document",PHOTO="image-album",BOOK="book";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mAdapter = new AllListAdapter(mContext,mData);
        curPage = 1;
        isFirstCreate = true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search_all;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(MessageEvent event) {
        Bundle bm = event.message;
        int pageIndex = bm.getInt("pageIndex");
        if (this.isVisible()&&pageIndex==ResultFragment.ALL&&!isFirstCreate){
            sortType = bm.getString("sortType");
            curPage = 1;
            loadPage();
        }
    }

    @Override
    public void onCreation(View root) {
        initView();
    }

    private void initView(){
        mListview.setAdapter(mAdapter);
        mListview.setPullLoadEnable(false);
        mListview.setXListViewListener(new ScrollRefListView.IXListViewListener() {
            @Override
            public void onLoadMore() {
                if (hasData) {
                    loadPage();
                }
            }
        });
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //跳转至详细页面
                AllDto result = mData.get(position - 1);
                startByType(result.entityType,position-1);
            }
        });
    }

    @Override
    public void load(){
        if (!isCreate) {
            curPage = 1;
            mData.clear();
            mAdapter.notifyDataSetChanged();
            isCreate = true;
            loadPage();
            return;
        }
        if (CollectionUtils.isEmpty(mData)){
            mNullTxt.setText("未搜索到相关记录");
            mNullTxt.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    private void handleResult(String json){
        Log.w("json", json);
        if (curPage == 1) {
            dismissLoading();
        }
        if (CheckUtil.isNullTxt(json)) {
//                    showToast("请求超时，请稍后再试");
            mNullTxt.setText("未搜索到相关记录");
            mNullTxt.setVisibility(View.VISIBLE);
            return;
        }
        isFirstCreate = false;
        if (!CheckUtil.isNull(json)) {
            try {
                if (curPage == 1) {
                    mData.clear();
                }
                JSONArray array = new JSONArray(json);
                if (array.length() < 10) {
                    hasData = false;
                    mListview.setPullLoadEnable(false);
                } else {
                    hasData = true;
                    mListview.setPullLoadEnable(true);
                }
                List<AllDto> data = JsonUtil.fromJson(json,new TypeToken<List<AllDto>>(){}.getType());
                mData.addAll(data);
                mAdapter.updateList(mData);
                if (mData.size() == 0 && curPage == 1) {
                    mNullTxt.setText("未搜索到相关记录");
                    mNullTxt.setVisibility(View.VISIBLE);
                } else {
                    mNullTxt.setVisibility(View.GONE);
                }
                curPage++;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            if (curPage == 1) {
                showToast("未搜索到相关记录");
            }
        }
    }

    private void startByType(String type, int position) {
        if (!TextUtils.isEmpty(type)){
            switch (type){
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
                case BOOK:
                    openBook(position);
                    break;
            }
        }

    }

    private void openAudio(int position) {
        SearchResult result = new SearchResult();
        result.id = mData.get(position).id;
        result.cover = mData.get(position).cover;
        Intent intent = new Intent(mContext, AudioPlayActivity2.class);
        intent.putExtra("resultBean", new Gson().toJson(result));
        mContext.startActivity(intent);
    }

    private void openVideo(int position) {
        SearchResult result = new SearchResult();
        result.id = mData.get(position).id;
        Intent intent = new Intent(mContext, VideoPlayActivity2.class);
        intent.putExtra("resultBean", new Gson().toJson(result));
        mContext.startActivity(intent);
    }

    private void openBook(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("name", mData.get(position).title);
        bundle.putString("cover", mData.get(position).cover);
//        bundle.putString("auth", mData.get(position).author);
        bundle.putString("isbn", mData.get(position).isbn);
//        bundle.putString("publisher", mData.get(position).publisher);
        bundle.putString("school", Preference.instance(mContext)
                .getString(Preference.SchoolCode));
        bundle.putBoolean(ContentActivity.FRAG_ISBACK, false);
        bundle.putBoolean(BookDetailFragment.ISFROMMAINPAGE, true);
        bundle.putString(ContentActivity.FRAG_CLS, BookDetailFragment.class.getName());
        mContext.startActivity(bundle, ContentActivity.class);
    }

    private void openPhoto(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("type", "img");
        bundle.putString("id", mData.get(position).id);
        mContext.startActivity(bundle, DynamicCardActivity.class);
    }

//    private void openPhoto(int position) {
//        Intent intent = new Intent();
//        intent.putExtra("position", 0);
//        intent.putExtra("see_pic", 2);
//        intent.setClass(mContext, ImagePagerActivity2.class);
//        startActivity(intent);
//    }

    private void openDoc(int position) {
        Intent intent = new Intent();
        intent.putExtra("url", "");
        intent.putExtra("doc_id", mData.get(position).id);
        intent.setClass(mContext, PdfActivity2.class);
        mContext.startActivity(intent);
        mContext.overridePendingTransition(R.anim.zoomin,
                R.anim.alpha_outto);
    }


    private void requestSearch(){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("method", "search.api");
        params.put("sort", sortType);
        params.put("keyword",mPreference.getString(Preference.KEYWORD_SEARCH));
//        params.put("lid",
//                Preference.instance(mActivity).getString(Preference.SchoolCode));
        params.put("page", curPage);
        if (curPage == 1) {
            showLoding();
        }
        mNullTxt.setVisibility(View.GONE);
        HttpRequest.loadWithMap(params)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dismissLoading();
                    }

                    @Override
                    public void onResponse(String json, int id) {
                        handleResult(json);
                    }
                });
    }

    private void loadPage(){
            requestSearch();
    }

    @Override
    public void reSet() {
        hasData=true;
        curPage=1;
        isCreate=false;
    }

    @Override
    public void notify(String data) {

    }
    private void hideAndSeek(){
        if (mData.size() == 0 && curPage == 1) {
            mNullTxt.setText("未搜索到相关记录");
            mNullTxt.setVisibility(View.VISIBLE);
        } else {
            mNullTxt.setVisibility(View.GONE);
        }
    }
}

