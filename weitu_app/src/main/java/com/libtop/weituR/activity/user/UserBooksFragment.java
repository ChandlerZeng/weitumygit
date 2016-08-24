package com.libtop.weituR.activity.user;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.libtop.weitu.R;
import com.libtop.weituR.activity.ContentActivity;
import com.libtop.weituR.activity.ContentFragment;
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
import com.libtop.weituR.activity.user.adapter.CollectionAdapter;
import com.libtop.weituR.activity.user.dto.CollectionBean;
import com.libtop.weituR.http.HttpRequest;
import com.libtop.weituR.tool.CommonUtil;
import com.libtop.weituR.tool.Preference;
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
 * Created by Administrator on 2016/1/11 0011.
 */
public class UserBooksFragment extends ContentFragment {
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
    private CollectionAdapter mAdapter;
    private List<CollectionBean> mData;
    private boolean hasData = false;
    SwipeMenuCreator creator;

    @Override
    public void onCreation(View root) {
        setTitle();
        init();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mData = new ArrayList<CollectionBean>();
        mAdapter = new CollectionAdapter(mContext, mData);
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
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_user_books;
    }


    private void getData() {

        showLoding();
        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("page", "1");
        params.put("uid", mPreference.getString(Preference.uid));
        params.put("method", "favorite.query");
        HttpRequest.loadWithMap(params)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String json, int id) {
                        if (!TextUtils.isEmpty(json)) {
                            //   showToast("没有相关数据");
                            mData.clear();
                            try {
                                JSONArray mjson = new JSONArray(json);
                                for (int i = 0; i < mjson.length(); i++) {
                                    JSONObject ajson = mjson.getJSONObject(i);
                                    CollectionBean bean = new CollectionBean();
                                    JSONObject favor = ajson.getJSONObject("favor");
                                    JSONObject target = ajson.getJSONObject("target");
                                    bean.id = favor.getString("id");
                                    bean.typedef = favor.getString("type");
                                    bean.type = target.isNull("type") ? 3 : target.getInt("type");
                                    bean.title = target.getString("title");
                                    bean.timeLine = favor.getLong("timeline");
                                    bean.uploadUsername = target.isNull("uploadUsername") ? "" : target.getString("uploadUsername");
                                    bean.introduction = target.getString("introduction");
                                    bean.isbn = target.isNull("isbn") ? "" : target.getString("isbn");
                                    bean.author = target.isNull("author") ? "" : target.getString("author");
                                    bean.publisher = target.isNull("publisher") ? "" : target.getString("publisher");
                                    mData.add(bean);
                                }
                                Message msg = updataHandler.obtainMessage();
                                msg.what = 1;
                                updataHandler.sendMessage(msg);
                                dismissLoading();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                dismissLoading();
                                Message msg = updataHandler.obtainMessage();
                                msg.what = 2;
                                updataHandler.sendMessage(msg);
                            }
                            return;
                        } else {
                            Message msg = updataHandler.obtainMessage();
                            msg.what = 2;
                            updataHandler.sendMessage(msg);
                        }

                        dismissLoading();
                    }
                });
    }

    private void setTitle() {
//        mBackBtn.setOnClickListener(this);
        mTitleText.setText("我的收藏");
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
        Bundle bundle = mContext.getIntent().getExtras();
        mAction = bundle.getString("action");
        mTitleText.setText(CommonUtil.getValue(mAction));

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                startFragment(mData.get(position).typedef, position, mData.get(position).type);
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

    private Handler updataHandler = new Handler() {
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (mData.size() == 0) {
                        noView.setVisibility(View.VISIBLE);
                        mListView.setVisibility(View.GONE);
                    } else {
                        mAdapter.setNewData(mData);
                        noView.setVisibility(View.GONE);
                        mListView.setVisibility(View.VISIBLE);
                    }
                    break;
                case 2:
                    setVisableView();
                    break;
                case 3:
//                    mData.remove(msg.arg1);
//                    mAdapter.setData(mData);
//                    mAdapter.notifyDataSetChanged();
//                    getData();
                    if (!mData.isEmpty()){
                        mData.remove(0);
                    }
                    mAdapter.setNewData(mData);
                    Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    private void startFragment(String str, int position, int type) {
        if (str.equals("")) {

        } else if (str.equals("document")) {
            openDoc(mData.get(position).id);
        } else if (str.equals("img")) {
//            Intent intent = new Intent();
//            intent.putExtra("position", 0);
//            intent.putExtra("see_pic", 2);
//            intent.setClass(mContext, ImagePagerActivity2.class);
//            startActivity(intent);
            openPhoto(mData.get(position).id);
        } else if (str.equals("book")) {
            Bundle bundle = new Bundle();
            bundle.putString("name", mData.get(position).title);
            bundle.putString("cover", mData.get(position).cover);
            bundle.putString("auth", mData.get(position).author);
            bundle.putString("isbn", mData.get(position).isbn);
            bundle.putString("publisher", mData.get(position).publisher);
            bundle.putString("school", Preference.instance(mContext)
                    .getString(Preference.SchoolCode));
            bundle.putBoolean(ContentActivity.FRAG_ISBACK,true);
            bundle.putString(ContentActivity.FRAG_CLS, BookDetailFragment2.class.getName());
            mContext.startActivity(bundle, ContentActivity.class);
        } else if (str.equals("mediaAlbum")) {
            if (type == 1) {
//                Bundle bundle = new Bundle();
//                bundle.putString("name", mData.get(position).title);
//                bundle.putString("cover", mData.get(position).cover);
//                bundle.putString("isbn", mData.get(position).isbn);
//                bundle.putString("school", Preference.instance(mContext)
//                        .getString(Preference.SchoolCode));
//                bundle.putString(ContentActivity.FRAG_CLS, BookDetailFragment2.class.getName());
//                mContext.startActivity(bundle, ContentActivity.class);
                openVideo(mData.get(position).id);
            } else {
                openAudio(mData.get(position).id,mData.get(position).cover);
            }
        }
    }

    private void openAudio(String id,String cover) {
        SearchResult result = new SearchResult();
        result.id = id;
        result.cover = cover;
        Intent intent = new Intent(mContext, AudioPlayActivity4.class);
        intent.putExtra("resultBean", new Gson().toJson(result));
        mContext.startActivity(intent);
    }

    private void openVideo(String id) {
        SearchResult result = new SearchResult();
        result.id = id;
        Intent intent = new Intent(mContext, VideoPlayActivity5.class);
        intent.putExtra("resultBean", new Gson().toJson(result));
        mContext.startActivity(intent);
    }

    private void openPhoto(String id) {
        Bundle bundle = new Bundle();
        bundle.putString("type", "img");
        bundle.putString("id", id);
        mContext.startActivity(bundle, DynamicCardActivity.class);
    }

    private void openDoc(String id) {
        Intent intent = new Intent();
        intent.putExtra("url", "");
        intent.putExtra("doc_id", id);
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
        params.put("method", "favorite.deleteBatch");
        HttpRequest.loadWithMap(params)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String json, int id) {
                        if (!TextUtils.isEmpty(json)) {
                            try {
                                JSONObject mjson = new JSONObject(json);
                                String a = mjson.getString("message");
                                if (a.equals("取消收藏成功")) {
                                    Message msg = updataHandler.obtainMessage();
                                    msg.what = 3;
                                    msg.arg1 = position;
                                    updataHandler.sendMessage(msg);
                                }
                                dismissLoading();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                dismissLoading();
                            }
                            return;
                        } else {
                            Message msg = updataHandler.obtainMessage();
                            msg.what = 2;
                            updataHandler.sendMessage(msg);
                        }

                        dismissLoading();
                    }
                });
    }

}
