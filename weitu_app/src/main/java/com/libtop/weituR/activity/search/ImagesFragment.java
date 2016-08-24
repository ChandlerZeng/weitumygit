package com.libtop.weituR.activity.search;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.libtop.weitu.R;
import com.libtop.weituR.activity.search.adapter.ResultListAdapter2;
import com.libtop.weituR.activity.search.dto.SearchResult;
import com.libtop.weituR.activity.search.dynamicCardLayout.DynamicCardActivity;
import com.libtop.weituR.base.impl.NotifyFragment;
import com.libtop.weituR.eventbus.MessageEvent;
import com.libtop.weituR.http.HttpRequest;
import com.libtop.weituR.tool.Preference;
import com.libtop.weituR.utils.CheckUtil;
import com.libtop.weituR.utils.CollectionUtils;
import com.libtop.weituR.utils.JsonUtil;
import com.libtop.weituR.widget.listview.ScrollRefListView;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import okhttp3.Call;

/**
 * Created by Administrator on 2015/12/23 0023.
 */
public class ImagesFragment extends NotifyFragment {
    @Bind(R.id.grid_view)
    ScrollRefListView mListview;
    //  @Bind(R.id.grid_view)
//    StaggeredGridView mGridview;
    //  GridView mGridview;
    @Bind(R.id.null_txt)
    TextView mNullTxt;

    // private ResultGridAdapter mAdapter;
    private ResultListAdapter2 mAdapter;
    private List<SearchResult> mData;
    private boolean isCreate = false, hasData = true;
    private int curPage = 1;

    private String sortType = "timeline";


    @Override
    public void notify(String data) {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mData = new ArrayList<SearchResult>();
        mAdapter = new ResultListAdapter2(mContext, mData, 1);
//        bo = new HistoryBo(context);
        curPage = 1;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_result3_layout;
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
        if (this.isVisible()&&pageIndex==ResultFragment.IMAGE){
            sortType = bm.getString("sortType");
            curPage = 1;
            loadPage();
        }
    }

    @Override
    public void onCreation(View root) {
        initView();
    }

    private void initView() {
        mListview.setAdapter(mAdapter);
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mData.size() < position)
                    return;
                Bundle bundle = new Bundle();
                bundle.putString("type", "img");
                bundle.putString("id", mData.get(position-1).id);
                mContext.startActivity(bundle, DynamicCardActivity.class);
            }
        });
//        mGridview.setAdapter(mAdapter);
//        mGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> arg0, View arg1,
//                                    int position, long id) {
//                SearchResult da = mData.get(position);
//                // 保存点击记录
//                Bundle bundle = new Bundle();
//                bundle.putString("type", "img");
//                bundle.putString("artist", da.artist);
//                bundle.putString("id", da.id);
//                bundle.putString("title", da.title);
//                bundle.putInt("favorite", da.favorite);
//                bundle.putInt("hot", da.hot);
//                bundle.putInt("views", da.view);
//                bundle.putString("cover", da.cover);
//                if (mContext instanceof ContentActivity) {
//                    bundle.putBoolean(ContentActivity.FRAG_ISBACK, true);
//                    bundle.putBoolean(ContentActivity.FRAG_WITH_ANIM, true);
//                }
        //bundle.putString(ContentActivity.FRAG_CLS, AlbumFragment.class.getName());
//                mContext.startActivity(bundle, ContentActivity.class);
////                context.startActivity(bundle, AlbumFragment.class);
//            }
//        });
    }

    @Override
    public void load() {
        if (!isCreate) {
            curPage = 1;
            mData.clear();
            mAdapter.notifyDataSetChanged();
            loadPage();
            isCreate = true;
            return;
        }
        if (CollectionUtils.isEmpty(mData)) {
            mNullTxt.setText("未搜索到相关记录");
            mNullTxt.setVisibility(View.VISIBLE);
        }
    }


    private void loadPage() {
//        if (mContext instanceof ContentActivity) {
//            requestCate();
//        } else {
            requestSearch();
//        }
    }

    private void handleResult(String json) {
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
        if (!CheckUtil.isNull(json)) {
//            try {
                if (curPage == 1) {
                    mData.clear();
                }
                List<SearchResult> lists = JsonUtil.fromJson(json,new TypeToken<List<SearchResult>>(){}.getType());
                if (lists.size() < 10) {
                    hasData = false;
                    mListview.setPullLoadEnable(false);
                } else {
                    hasData = true;
                    mListview.setPullLoadEnable(true);
                }
                mData.addAll(lists);
                mAdapter.setData(mData);
                mAdapter.notifyDataSetChanged();
//                JSONArray array = new JSONArray(json);
//                if (array.length() < 10) {
//                    hasData = false;
//                } else {
//                    hasData = true;
//                }
//                for (int i = 0; i < array.length(); i++) {
//                    SearchResult result = new SearchResult();
//                    result.of(array.getJSONObject(i));
//                    mData.add(result);
//                }
//                mAdapter.notifyDataSetChanged();
                if (mData.size() == 0 && curPage == 1) {
                    mNullTxt.setText("未搜索到相关记录");
                    mNullTxt.setVisibility(View.VISIBLE);
                } else {
                    mNullTxt.setVisibility(View.GONE);
                }
                curPage++;
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        } else {
            if (curPage == 1) {
                showToast("未搜索到相关记录");
            }
        }
    }


    private void requestSearch() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("method", "imageAlbum.search");
        params.put("sort", "timeline");
        params.put("keyword", mPreference.getString(Preference.KEYWORD_SEARCH));
        params.put("page", curPage);
        if (curPage == 1) {
           showLoding();
        }
        mNullTxt.setVisibility(View.GONE);
        HttpRequest.loadWithMap(params)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String json, int id) {
                        handleResult(json);
                    }
                });
    }

    private void requestCate() {
        Map<String, Object> params = new HashMap<>();
        params.put("filterType", "img");
        params.put("libraryCode", mPreference.getString(Preference.SchoolId));
        params.put("cateCode", mPreference.getInt(Preference.KEYWORD_CATECODE) + "");
        params.put("page", curPage + "");
        params.put("method","search.listByCate");
        if (curPage == 1) {
            showLoding();
        }
        mNullTxt.setVisibility(View.GONE);
        HttpRequest.loadWithMap(params)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dismissLoading();
                        Log.e("request", "erro", e);
                        mNullTxt.setText("未搜索到相关记录");
                        mNullTxt.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onResponse(String result, int id) {
                        dismissLoading();
                        if (curPage == 1) {
                            mData.clear();
                        }
                        List<SearchResult> s = JsonUtil.fromJson(result, new TypeToken<List<SearchResult>>() {
                        }.getType());
                        if (CollectionUtils.isEmpty(s)) {
                            mNullTxt.setText("未搜索到相关记录");
                            mNullTxt.setVisibility(View.VISIBLE);
                            return;
                        } else {
                            mNullTxt.setVisibility(View.GONE);
                        }
                        mData.addAll(s);
                        mAdapter.notifyDataSetChanged();
                        curPage++;
                    }
                });
//        RequestProxy.getInstance().post(ContantsUtil.HOST_ADDON + "/search/listByCate.json"
//                , params, new Callback.Base() {
//            @Override
//            public void onSuccess(String result) {
//                if (mLoading.isShowing()) {
//                    mLoading.dismiss();
//                }
//                if (curPage == 1) {
//                    mData.clear();
//                }
//                List<SearchResult> s = JsonUtil.fromJson(result, new TypeToken<List<SearchResult>>() {
//                }.getType());
//                if (CollectionUtils.isEmpty(s)) {
//                    mNullTxt.setText("未搜索到相关记录");
//                    mNullTxt.setVisibility(View.VISIBLE);
//                    return;
//                } else {
//                    mNullTxt.setVisibility(View.GONE);
//                }
//                mData.addAll(s);
//                mAdapter.notifyDataSetChanged();
//                curPage++;
//            }
//
//            @Override
//            public void onError(Throwable ex) {
//                if (mLoading.isShowing()) {
//                    mLoading.dismiss();
//                }
//                Log.e("request", "erro", ex);
//                mNullTxt.setText("未搜索到相关记录");
//                mNullTxt.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onFail(int code, String msg) {
//                if (mLoading.isShowing()) {
//                    mLoading.dismiss();
//                }
//                Log.w("request", "fail code:" + code + " msg:" + msg);
//                mNullTxt.setText("未搜索到相关记录");
//                mNullTxt.setVisibility(View.VISIBLE);
//            }
//        });
//        HttpServiceUtil.request(ContantsUtil.HOST_ADDON+"/search/listByCate.json", "post", params, new HttpServiceUtil.CallBack() {
//            @Override
//            public void callback(String json) {
//                try {
//                    JSONObject object=new JSONObject(json);
//                    handleResult(object.getJSONArray("data").toString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    mLoading.dismiss();
//                    mNullTxt.setText("未搜索到相关记录");
//                    mNullTxt.setVisibility(View.VISIBLE);
//                }
//            }
//        });
    }

    private void hideAndSeek() {
        if (mData.size() == 0 && curPage == 1) {
            mNullTxt.setText("未搜索到相关记录");
            mNullTxt.setVisibility(View.VISIBLE);
        } else {
            mNullTxt.setVisibility(View.GONE);
        }
    }


    @Override
    public void reSet() {
        hasData = true;
        curPage = 1;
        isCreate = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }
}
