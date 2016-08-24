package com.libtop.weituR.activity.search;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.libtop.weitu.R;
import com.libtop.weituR.activity.search.adapter.HotListAdapter;
import com.libtop.weituR.activity.search.adapter.SearchAdapter;
import com.libtop.weituR.activity.search.dto.HotSearchDto;
import com.libtop.weituR.base.impl.NotifyFragment;
import com.libtop.weituR.dao.SearchBo;
import com.libtop.weituR.dao.bean.Search;
import com.libtop.weituR.http.HttpRequest;
import com.libtop.weituR.utils.ContantsUtil;
import com.libtop.weituR.utils.JsonUtil;
import com.libtop.weituR.widget.listview.ChangeListView;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import okhttp3.Call;

public class SearchPreFragment2 extends NotifyFragment implements SearchAdapter.OnDeleteImgClickListener {

    @Bind(R.id.search_scrollview)
    ScrollView scrollView;
    @Bind(R.id.list)
    ListView mList;
    @Bind(R.id.ll_out_search_history)
    LinearLayout llSearchHistory;
    @Bind(R.id.gv_hot)
    GridView gvHot;
    @Bind(R.id.trash)
    TextView mTrashBtn;
    @Bind(R.id.search_expandable_text)
    TextView mExpandableText;
    @Bind(R.id.search_history_clear)
    TextView mHistoryClearText;

    private List<Search> mData;
    private SearchAdapter mAdapter;
    private SearchBo mBo;
    private SearchActivity mActivity;

    private List<HotSearchDto> mHotList = new ArrayList<>();
    private HotListAdapter hotListAdapter;
    private InputMethodManager imm;

    private boolean expanded=false;
    private boolean isCleared=false;

    private static final String TAG = "SearchPreFragment2";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBo = new SearchBo(mContext);
        mData = mBo.lists();
        ContantsUtil.UPDATE_SEARCH = true;
        mAdapter = new SearchAdapter(mContext, mData, SearchPreFragment2.this);
        hotListAdapter = new HotListAdapter(mContext, mHotList);
        mActivity = (SearchActivity) mContext;
        imm = (InputMethodManager) getContext().
                getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search_pre2;
    }

    private void loadHotSearch() {
        showLoding();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("method", "hotSearch.top");
        params.put("top", 10);
        HttpRequest.loadWithMap(params)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String json, int id) {
                        dismissLoading();
                        if (TextUtils.isEmpty(json)) {
                            showToast("没有相关数据");
                            return;
                        }
                        mHotList.clear();
                        mHotList = JsonUtil.fromJson(json, new TypeToken<List<HotSearchDto>>() {
                        }.getType());
                        hotListAdapter.setData(mHotList);
                        hotListAdapter.notifyDataSetChanged();
                        if (mHotList.size() == 0) {
                            showToast("没有相关数据");
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!ContantsUtil.UPDATE_SEARCH) {
            mData.clear();
            mData.addAll(mBo.lists());
            ContantsUtil.UPDATE_SEARCH = true;
        }
        expanded=false;
        isCleared=false;
        initView();
        setListViewHeight(mList, mData.size(), false);
        addSearchBtn();
    }

    @Override
    public void onCreation(View root) {
        initView();
        loadHotSearch();
    }

    public void setListViewHeight(ListView listView, int size, boolean expanded) {
        if (size == 0) {
            llSearchHistory.setVisibility(View.GONE);
        } else {
            llSearchHistory.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams = listView.getLayoutParams();
            //获取ListView每个Item高度
            View item = mAdapter.getView(0, null, listView);
            item.measure(0, 0);
            int height = item.getMeasuredHeight();
            if (size > 0 & size <= 2) {
                layoutParams.height = size * height;
                mExpandableText.setVisibility(View.GONE);
                mHistoryClearText.setVisibility(View.GONE);
            } else {
                if (expanded) {
                    mExpandableText.setVisibility(View.GONE);
                    mHistoryClearText.setVisibility(View.VISIBLE);
                    layoutParams.height = size * height;
                } else {
                    mExpandableText.setVisibility(View.VISIBLE);
                    layoutParams.height = 2 * height;
                }
            }
            listView.setLayoutParams(layoutParams);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        removeSearchBtn();
        mHistoryClearText.setVisibility(View.GONE);
    }

    private void initView() {
        setListViewHeight(mList, mData.size(), false);
        mExpandableText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expanded=true;
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                setListViewHeight(mList, mData.size(), true);
//					mExpandableText.setVisibility(View.GONE);
//					mHistoryClearText.setVisibility(View.VISIBLE);
                mHistoryClearText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mData.clear();
                        mBo.clear();
                        mAdapter.notifyDataSetChanged();
                        mHistoryClearText.setVisibility(View.GONE);
                        setListViewHeight(mList, mData.size(), false);
                    }
                });
            }
        });
        mList.setAdapter(mAdapter);
        gvHot.setAdapter(hotListAdapter);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                mActivity.search(mData.get(position).getName());
            }
        });
        gvHot.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mActivity.search(mHotList.get(position).title);
            }
        });
        mTrashBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mData.clear();
                mBo.clear();
                mAdapter.notifyDataSetChanged();
                mExpandableText.setVisibility(View.GONE);
                mHistoryClearText.setVisibility(View.GONE);
                setListViewHeight(mList, 0, false);
            }
        });
        mList.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP||expanded||isCleared) {
                    scrollView.requestDisallowInterceptTouchEvent(false);
                    gvHot.requestDisallowInterceptTouchEvent(true);
                    mList.requestDisallowInterceptTouchEvent(true);
                } else {
                    scrollView.requestDisallowInterceptTouchEvent(true);
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
                return false;
            }
        });
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
                return false;
            }
        });
        gvHot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (imm.isActive()) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
                return false;
            }
        });
    }

    private void addSearchBtn() {
        Spinner spinner = (Spinner) mContext.findViewById(R.id.spinner);
        TextView textView = (TextView) mContext.findViewById(R.id.search);
        textView.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.GONE);
    }

    private void removeSearchBtn() {
        Spinner spinner = (Spinner) mContext.findViewById(R.id.spinner);
        TextView textView = (TextView) mContext.findViewById(R.id.search);
        textView.setVisibility(View.GONE);
        spinner.setVisibility(View.VISIBLE);
    }

    @Override
    public void notify(String data) {

    }
    @Override
    public void onDeleteImgTouch(View v, Search search, int position) {
        isCleared=true;
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        mBo.delete(search);
        mData.remove(search);
        mAdapter.notifyDataSetChanged();
        if(expanded){
            setListViewHeight(mList, mData.size(), true);
        }else{
            setListViewHeight(mList, mData.size(), false);
        }
    }
}
