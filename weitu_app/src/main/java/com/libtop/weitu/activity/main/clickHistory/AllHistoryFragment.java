package com.libtop.weitu.activity.main.clickHistory;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.main.adapter.AllHistoryAdapter;
import com.libtop.weitu.activity.search.BookDetailFragment;
import com.libtop.weitu.activity.search.VideoPlayActivity2;
import com.libtop.weitu.activity.search.dto.SearchResult;
import com.libtop.weitu.activity.search.dynamicCardLayout.DynamicCardActivity;
import com.libtop.weitu.activity.source.AudioPlayActivity2;
import com.libtop.weitu.activity.source.PdfActivity2;
import com.libtop.weitu.activity.user.SwipeMenu.SwipeMenu;
import com.libtop.weitu.activity.user.SwipeMenu.SwipeMenuCreator;
import com.libtop.weitu.activity.user.SwipeMenu.SwipeMenuItem;
import com.libtop.weitu.activity.user.SwipeMenu.SwipeMenuListView;
import com.libtop.weitu.base.impl.NotifyFragment;
import com.libtop.weitu.dao.ResultCodeDto;
import com.libtop.weitu.eventbus.MessageEvent;
import com.libtop.weitu.http.MapUtil;
import com.libtop.weitu.http.WeituNetwork;
import com.libtop.weitu.tool.Preference;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by LianTu on 2016/7/15.
 */
public class AllHistoryFragment extends NotifyFragment {
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.list)
    SwipeMenuListView mListView;
    @Bind(R.id.null_txt)
    TextView mNullTxt;

    private AllHistoryAdapter mAdapter;
    private List<ResultBean> mData=new ArrayList<>();
    private boolean isCreate = false, hasData = true;
    private int curPage = 1;

    private String sortType = "timeline";
    private final int ALL = 0;
    public static final int VIDEO=1,AUDIO=2,DOC=3,PHOTO=4,BOOK=5;
    private int type = 0;
    SwipeMenuCreator creator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        Bundle bundle = this.getArguments();
        type = bundle.getInt("type", 0);
        mAdapter = new AllHistoryAdapter(mContext,mData);
        creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        mContext.getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                deleteItem.setWidth(dp2px(135));
                deleteItem.setIcon(R.drawable.delete_c);
                menu.addMenuItem(deleteItem);
            }
        };
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_history_all;
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(MessageEvent event) {
        Bundle bundle = event.message;
        Boolean isClean = bundle.getBoolean("isClean");
        if (isClean){
            getData();
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onCreation(View root) {
        initView();
        getData();
        init();
    }

    private void initView(){
        mListView.setAdapter(mAdapter);
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW);
        swipeRefreshLayout.measure(0,0);
        swipeRefreshLayout.setEnabled(false);
    }

    private void init() {
        mListView.setMenuCreator(creator);
        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        deteteData(position, mData.get(position).id);
                        break;
                }
                return false;
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                startByType(mData.get(position).type, position);
            }
        });
        mListView.setAdapter(mAdapter);
    }


    private void getData() {
        swipeRefreshLayout.setRefreshing(true);
        HashMap<String,Object> map = new HashMap<>();
        map.put("uid",mPreference.getString(Preference.uid));
        if (type != ALL){
            map.put("type",type);
        }
        map.put("method","footprint.query");
        String[] arrays = MapUtil.map2Parameter(map);
        subscription = WeituNetwork.getWeituApi()
                .getHistory(arrays[0],arrays[1],arrays[2])
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ResultBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        swipeRefreshLayout.setRefreshing(false);

                    }

                    @Override
                    public void onNext(List<ResultBean> resultBeen) {
                        swipeRefreshLayout.setRefreshing(false);
                        mData.clear();
                        mData = resultBeen;
                        if (mData.isEmpty()){
                            hideAndSeek();
                        }
                        mAdapter.updateList(mData);
                    }
                });


    }



    private void startByType(int type, int position) {
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

    private void deteteData(final int position, String... idList) {
        if (idList == null || idList.length == 0)
            return;
        swipeRefreshLayout.setRefreshing(true);
        HashMap<String,Object> map = new HashMap<>();
        try {
            JSONArray jsonarray = new JSONArray(Arrays.toString(idList));
            map.put("ids", jsonarray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        map.put("method", "footprint.deleteBatch");
        String[] arrays = MapUtil.map2Parameter(map);
        subscription = WeituNetwork.getWeituApi()
                .getResultCode(arrays[0],arrays[1],arrays[2])
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResultCodeDto>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onNext(ResultCodeDto resultCodeDto) {
                        swipeRefreshLayout.setRefreshing(false);
                        if (resultCodeDto.code==1){
                            getData();
                            showToast("删除成功");
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("isClean",true);
                            EventBus.getDefault().post(new MessageEvent(bundle));
                        }else {
                            showToast("删除失败");
                        }
                    }
                });
    }

    private void openAudio(int position) {
        SearchResult result = new SearchResult();
        result.id = mData.get(position).target.id;
        result.cover = mData.get(position).target.cover;
        Intent intent = new Intent(mContext, AudioPlayActivity2.class);
        intent.putExtra("resultBean", new Gson().toJson(result));
        mContext.startActivity(intent);
    }

    private void openVideo(int position) {
        SearchResult result = new SearchResult();
        result.id = mData.get(position).target.id;
        Intent intent = new Intent(mContext, VideoPlayActivity2.class);
        intent.putExtra("resultBean", new Gson().toJson(result));
        mContext.startActivity(intent);
    }

    private void openBook(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("name", mData.get(position).target.title);
        bundle.putString("cover", mData.get(position).target.cover);
        bundle.putString("auth", mData.get(position).target.author);
        bundle.putString("isbn", mData.get(position).target.isbn);
        bundle.putString("publisher", mData.get(position).target.publisher);
        bundle.putString("school", Preference.instance(mContext)
                .getString(Preference.SchoolCode));
        bundle.putBoolean(BookDetailFragment.ISFROMMAINPAGE,true);
        bundle.putBoolean(ContentActivity.FRAG_ISBACK, false);
        bundle.putString(ContentActivity.FRAG_CLS, BookDetailFragment.class.getName());
        mContext.startActivity(bundle, ContentActivity.class);
    }

    private void openPhoto(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("type", "img");
        bundle.putString("id", mData.get(position).target.id);
        mContext.startActivity(bundle, DynamicCardActivity.class);
    }


    private void openDoc(int position) {
        Intent intent = new Intent();
        intent.putExtra("url", "");
        intent.putExtra("doc_id", mData.get(position).target.id);
        intent.setClass(mContext, PdfActivity2.class);
        mContext.startActivity(intent);
        mContext.overridePendingTransition(R.anim.zoomin,
                R.anim.alpha_outto);
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
            mNullTxt.setVisibility(View.VISIBLE);
        } else {
            mNullTxt.setVisibility(View.GONE);
        }
    }
}
