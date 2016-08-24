package com.libtop.weituR.activity.main.clickHistory;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.libtop.weitu.R;
import com.libtop.weituR.activity.ContentActivity;
import com.libtop.weituR.activity.main.adapter.ClickHistoryAdapter;
import com.libtop.weituR.activity.search.BookDetailFragment2;
import com.libtop.weituR.activity.search.VideoPlayActivity4;
import com.libtop.weituR.activity.search.VideoPlayActivity5;
import com.libtop.weituR.activity.search.dto.SearchResult;
import com.libtop.weituR.activity.search.dynamicCardLayout.DynamicCardActivity;
import com.libtop.weituR.activity.source.AudioPlayActivity4;
import com.libtop.weituR.activity.source.PdfActivity3;
import com.libtop.weituR.activity.user.SwipeMenu.SwipeMenu;
import com.libtop.weituR.activity.user.SwipeMenu.SwipeMenuCreator;
import com.libtop.weituR.activity.user.SwipeMenu.SwipeMenuItem;
import com.libtop.weituR.activity.user.SwipeMenu.SwipeMenuListView;
import com.libtop.weituR.base.BaseActivity;
import com.libtop.weituR.http.HttpRequest;
import com.libtop.weituR.tool.Preference;
import com.libtop.weituR.utils.JsonUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by LianTu on 2016/6/22.
 * 浏览足迹页面
 */
public class ClickHistoryActivity extends BaseActivity {
    @Bind(R.id.title)
    TextView mTitleText;
    @Bind(R.id.list)
    SwipeMenuListView mListView;
    @Bind(R.id.commit)
    TextView commitText;
    @Bind(R.id.delete_view)
    View view;
    @Bind(R.id.have_see)
    TextView allText;
    @Bind(R.id.going_down)
    TextView deleteText;
    @Bind(R.id.nullCo)
    View noView;

    private String mAction;
    private int mCurentPage = 1;
    private ClickHistoryAdapter mAdapter;
    private List<ResultBean> mData;
    private boolean hasData = false;
    SwipeMenuCreator creator;

    public static final int VIDEO=1,AUDIO=2,DOC=3,PHOTO=4,BOOK=5;

//    @Override
//    public void onCreation(View root) {
//        setTitle();
//        init();
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setInjectContentView(R.layout.fragment_click_history);
        mData = new ArrayList<ResultBean>();
        mAdapter = new ClickHistoryAdapter(mContext, mData);
//        CollectionBean a = new CollectionBean();
//        a.introduction = "在重写的时，我们常常在方法中复用，以提高性能，能够回收并重用，但是多个Item布局类型不同时，的回收和重用会出现问题。比如有些行为纯文本，有些行则是图文混排，这里纯文本行为一类布局，图文混排的行为第二类布局。单一类型的很简单，下面着重介绍一下包含多种类型视图布局的情形。";
//        a.type = 1;
//        a.title = "java秘籍";
//        CollectionBean b = new CollectionBean();
//        b.type = 0;
//        b.title = "大学人生职业规划";
//        CollectionBean c = new CollectionBean();
//        c.type = 0;
//        c.title = "面试技巧.pdf";
//        CollectionBean d = new CollectionBean();
//        d.type = 0;
//        d.title = "印度文化";
//        mData.add(a);
//        mData.add(b);
//        mData.add(c);
//        mData.add(d);
        // step 1. create a MenuCreator
        creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        mContext.getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(135));
                // set a icon
                deleteItem.setIcon(R.drawable.delete_c);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };

        getData();
        setTitle();
        init();
    }


    private void getData() {
        showLoding();
        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("page", "1");
        params.put("uid", mPreference.getString(Preference.uid));
        params.put("method", "footprint.query");
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
                            mData = JsonUtil.fromJson(json, new TypeToken<List<ResultBean>>() {
                            }.getType());
//                    for (ResultBean bean:mData){
//                        Log.w("guanglog",bean.id);
//                        Log.w("guanglog",bean.target.id);
//
//                    }
                            if (mData == null || mData.size() == 0) {
                                noView.setVisibility(View.VISIBLE);
                                mListView.setVisibility(View.GONE);
                            } else {
                                noView.setVisibility(View.GONE);
                                mListView.setVisibility(View.VISIBLE);
                                mAdapter.setNewData(mData);
                            }
                        } else {
                            if (mData == null || mData.size() == 0) {
                                noView.setVisibility(View.VISIBLE);
                                mListView.setVisibility(View.GONE);
                            }
                        }
                    }
                });

    }

    private void setTitle() {
//        mBackBtn.setOnClickListener(this);
        mTitleText.setText("浏览记录");
        commitText.setText("编辑");
    }

    @Nullable
    @OnClick({R.id.back_btn, R.id.commit, R.id.have_see, R.id.going_down})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                mContext.finish();
                break;
            case R.id.commit:
                if (commitText.getText().toString().equals("编辑")) {
                    commitText.setText("取消");
                    view.setVisibility(View.VISIBLE);
                    mAdapter.setVisableView();
                } else {
                    commitText.setText("编辑");
                    view.setVisibility(View.GONE);
                    mAdapter.setVisableView();
                }
                break;
            case R.id.have_see:
                mAdapter.setAllView();
                break;
            case R.id.going_down:
                String[] a = mAdapter.cleanView();
                deteteData(50, a);
                setVisableView();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        mContext.finish();
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
                startFragment(mData.get(position).type, position);
            }
        });
        mListView.setAdapter(mAdapter);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    private void setVisableView() {
        if (mData.size() == 0) {
            noView.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
        }
    }

    private void startFragment(int type, int position) {
        switch (type){
            case VIDEO:
                openVideo(position);
                break;
            case AUDIO:
                openAudio(position);
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

    private void openAudio(int position) {
        SearchResult result = new SearchResult();
        result.id = mData.get(position).target.id;
        result.cover = mData.get(position).target.cover;
        Intent intent = new Intent(mContext, AudioPlayActivity4.class);
        intent.putExtra("resultBean", new Gson().toJson(result));
        mContext.startActivity(intent);
    }

    private void openVideo(int position) {
        SearchResult result = new SearchResult();
        result.id = mData.get(position).target.id;
        Intent intent = new Intent(mContext, VideoPlayActivity5.class);
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
        bundle.putBoolean(ContentActivity.FRAG_ISBACK, true);
        bundle.putString(ContentActivity.FRAG_CLS, BookDetailFragment2.class.getName());
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
        intent.putExtra("doc_id", mData.get(position).id);
        intent.setClass(mContext, PdfActivity3.class);
        mContext.startActivity(intent);
        mContext.overridePendingTransition(R.anim.zoomin,
                R.anim.alpha_outto);
    }

    private void deteteData(final int position, String... idList) {
        if (idList == null || idList.length == 0)
            return;
        showLoding();
        Map<String, Object> params = new HashMap<String, Object>();
        try {
            JSONArray jsonarray = new JSONArray(Arrays.toString(idList));
            params.put("ids", jsonarray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("method", "footprint.deleteBatch");
        HttpRequest.loadWithMap(params)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String json, int id) {
                        if (!TextUtils.isEmpty(json)) {
                            try {
                                dismissLoading();
                                JSONObject mjson = new JSONObject(json);
                                if (mjson.getInt("code")==1){
//                            getData();
                                    if (!mData.isEmpty()){
                                        mData.remove(position);
                                    }
                                    mAdapter.setNewData(mData);
                                    Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                dismissLoading();
                            }
                            return;
                        } else {
                            setVisableView();
                        }

                        dismissLoading();
                    }
                });
    }
}
