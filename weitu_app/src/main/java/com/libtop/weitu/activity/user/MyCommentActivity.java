package com.libtop.weitu.activity.user;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.search.dto.CommentResult;
import com.libtop.weitu.activity.user.SwipeMenu.SwipeMenu;
import com.libtop.weitu.activity.user.SwipeMenu.SwipeMenuCreator;
import com.libtop.weitu.activity.user.SwipeMenu.SwipeMenuItem;
import com.libtop.weitu.activity.user.SwipeMenu.SwipeMenuListView;
import com.libtop.weitu.activity.user.adapter.MyCommentAdapter;
import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.http.MapUtil;
import com.libtop.weitu.http.WeituNetwork;
import com.libtop.weitu.tool.Preference;
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
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by LianTu on 2016/7/12.
 */
public class MyCommentActivity extends BaseActivity{
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
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout refreshLayout;

    private String mAction;
    private int mCurentPage = 1;
    private MyCommentAdapter mAdapter;
    private List<CommentResult> mData;
    private boolean hasData = false;
    SwipeMenuCreator creator;

    public static final int VIDEO=1,AUDIO=2,DOC=3,PHOTO=4,BOOK=5;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setInjectContentView(R.layout.activity_my_comment);
        mData = new ArrayList<CommentResult>();
        mAdapter = new MyCommentAdapter(mContext, mData);
        refreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW);
        refreshLayout.setEnabled(false);
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

        getData();
        setTitle();
        init();
    }


    //获取我的评论列表数据
    private void getData() {
//        http://weitu.bookus.cn/comment/listByUser.json?text={"uid":"565bea2c984ec06f56befda3","page":1,"method":"comment.listByUser"}
        refreshLayout.setRefreshing(true);
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("uid", mPreference.getString(Preference.uid));
        params.put("page", 1);
        params.put("method", "comment.listByUser");
        String[] arrays = MapUtil.map2Parameter(params);
        subscription = WeituNetwork.getWeituApi()
                .getComment(arrays[0],arrays[1],arrays[2])
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<CommentResult>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        refreshLayout.setRefreshing(false);
                        Log.w("gunaglog","error + "+e);
                    }

                    @Override
                    public void onNext(List<CommentResult> commentResults) {
                        refreshLayout.setRefreshing(false);
                        mData.clear();
                        mData = commentResults;
                        if (mData.isEmpty()) {
                            noView.setVisibility(View.VISIBLE);
                            mListView.setVisibility(View.GONE);
                        } else {
                            noView.setVisibility(View.GONE);
                            mListView.setVisibility(View.VISIBLE);
                            mAdapter.setData(mData);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });

    }

    private void setTitle() {
        mTitleText.setText("我的评论");
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



    private void deteteData(final int position, String... idList) {
//        删除评论
//        http://weitu.bookus.cn/comment/delete.json?text={"cid":["560a3dbe984e84048d36b4da","560a3dbe984e84048d36b4dd"],"uid":"565bea2c984ec06f56befda3",method":"comment.delete"}
        refreshLayout.setRefreshing(true);
        if (idList == null || idList.length == 0)
            return;
        Map<String, Object> params = new HashMap<String, Object>();
        try {
            JSONArray jsonarray = new JSONArray(Arrays.toString(idList));
            params.put("cid", jsonarray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("uid",mPreference.getString(Preference.uid));
        params.put("method", "comment.delete");
        HttpRequest.loadWithMap(params)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String json, int id) {
                        if (!TextUtils.isEmpty(json)) {
                            try {
                                JSONObject jsonObject = new JSONObject(json);
                                int result = jsonObject.getInt("code");
                                if (result == 1){
                                    getData();
                                    showToast("删除成功");
                                }else {
                                    showToast("删除失败");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else {
                            refreshLayout.setRefreshing(false);
                        }
                    }
                });
        if (idList == null || idList.length == 0)
            return;
    }
}
