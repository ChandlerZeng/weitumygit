package com.libtop.weituR.activity.main.upload;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.libtop.weitu.R;
import com.libtop.weituR.activity.ContentActivity;
import com.libtop.weituR.activity.main.adapter.NewestUploadAdapter;
import com.libtop.weituR.activity.main.dto.DocBean;
import com.libtop.weituR.activity.search.BookDetailFragment;
import com.libtop.weituR.activity.search.VideoPlayActivity2;
import com.libtop.weituR.activity.search.dto.SearchResult;
import com.libtop.weituR.activity.search.dynamicCardLayout.DynamicCardActivity;
import com.libtop.weituR.activity.source.AudioPlayActivity2;
import com.libtop.weituR.activity.source.PdfActivity2;
import com.libtop.weituR.activity.user.SwipeMenu.SwipeMenuCreator;
import com.libtop.weituR.activity.user.SwipeMenu.SwipeMenuListView;
import com.libtop.weituR.base.impl.NotifyFragment;
import com.libtop.weituR.eventbus.MessageEvent;
import com.libtop.weituR.http.MapUtil;
import com.libtop.weituR.http.WeituNetwork;
import com.libtop.weituR.tool.Preference;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Zeng on 2016/8/11.
 */
public class NewestUploadContentFragment extends NotifyFragment {
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.list)
    SwipeMenuListView mListView;
    @Bind(R.id.null_txt)
    TextView mNullTxt;

    private NewestUploadAdapter mAdapter;
    //    private List<ResultBean> mData=new ArrayList<>();
    private boolean isCreate = false, hasData = true;
    private int curPage = 1;

    private String sortType = "timeline";
    private final int ALL = 0;
    public static final int VIDEO = 1, AUDIO = 2, DOC = 3, PHOTO = 4, BOOK = 5;
    private int type = 0;
    SwipeMenuCreator creator;
    private CompositeSubscription _subscriptions = new CompositeSubscription();
    private int newestIndex;
    private List<DocBean> uploadList = new ArrayList<DocBean>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        Bundle bundle = this.getArguments();
        type = bundle.getInt("type", 0);
        mAdapter = new NewestUploadAdapter(mContext, uploadList);
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
        if (isClean) {
            loadNewestUpload();
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
        loadNewestUpload();
//        hideAndSeek();
        init();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                _subscriptions.clear();
                newestIndex = 0;
                uploadList.clear();
                loadNewestUpload();
            }
        });
    }

    private void initView() {
        mListView.setAdapter(mAdapter);
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW);
        swipeRefreshLayout.measure(0, 0);
        swipeRefreshLayout.setEnabled(false);
    }

    private void init() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                startByType(uploadList.get(position).type, position);
            }
        });
        mListView.setAdapter(mAdapter);
    }

    //获取最新视频
    private Observable<List<DocBean>> getNewestVideoObservable() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("method", "mediaAlbum.latest");
        params.put("type", 1);
        String[] arrays = MapUtil.map2Parameter(params);
        return WeituNetwork.getWeituApi().getNewest(arrays[0], arrays[1], arrays[2]);
    }

    //获取最新音频
    private Observable<List<DocBean>> getNewestAudioObservable() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("method", "mediaAlbum.latest");
        params.put("type", 2);
        String[] arrays = MapUtil.map2Parameter(params);
        return WeituNetwork.getWeituApi().getNewest(arrays[0], arrays[1], arrays[2]);
    }

    //获取最新文档
    private Observable<List<DocBean>> getNewestDocObservable() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("method", "document.latest");
        params.put("type", 3);
        String[] arrays = MapUtil.map2Parameter(params);
        return WeituNetwork.getWeituApi().getNewest(arrays[0], arrays[1], arrays[2]);
    }

    //获取最新图片
    private Observable<List<DocBean>> getNewestPhotoObservable() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("method", "imageAlbum.latest");
        params.put("type", 4);
        String[] arrays = MapUtil.map2Parameter(params);
        return WeituNetwork.getWeituApi().getNewest(arrays[0], arrays[1], arrays[2]);
    }

    //获取最新图书
    private Observable<List<DocBean>> getNewestBookObservable() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("method", "book.latest");
        params.put("type", 5);
        String[] arrays = MapUtil.map2Parameter(params);
        return WeituNetwork.getWeituApi().getNewest(arrays[0], arrays[1], arrays[2]);
    }

    private void loadNewestUpload() {
        swipeRefreshLayout.setRefreshing(true);
//        mLoading.show();
        Observable<List<DocBean>> newestBookObservable = getNewestBookObservable();
        Observable<List<DocBean>> newestVideoObservable = getNewestVideoObservable();
        Observable<List<DocBean>> newestAudioObservable = getNewestAudioObservable();
        Observable<List<DocBean>> newestDocObservable = getNewestDocObservable();
        Observable<List<DocBean>> newestPhotoObservable = getNewestPhotoObservable();
        _subscriptions.add(
                Observable.concat(newestVideoObservable, newestAudioObservable, newestDocObservable, newestPhotoObservable)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<List<DocBean>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                newestIndex++;
                                if (newestIndex > 3)
                                    newestIndex = 0;
                                swipeRefreshLayout.setRefreshing(false);
                            }

                            @Override
                            public void onNext(List<DocBean> docBeens) {
//                                if (mLoading.isShowing()) {
//                                    mLoading.dismiss();
//                                }
                                newestIndex++;
                                swipeRefreshLayout.setRefreshing(false);
                                if (docBeens.isEmpty()) {
//                                    hideAndSeek();
                                    return;
                                }
                                List<DocBean> uploadlist1 = new ArrayList<DocBean>();
                                List<DocBean> uploadlist2 = new ArrayList<DocBean>();
                                List<DocBean> uploadlist3 = new ArrayList<DocBean>();
                                List<DocBean> uploadlist4 = new ArrayList<DocBean>();
                                List<DocBean> uploadlist5 = new ArrayList<DocBean>();
                                for (DocBean docBean : docBeens) {
                                    docBean.type = newestIndex;
                                    if (docBean.type == 1) {
                                        uploadlist1.add(docBean);
                                    } else if (docBean.type == 2) {
                                        uploadlist2.add(docBean);
                                    } else if (docBean.type == 3) {
                                        uploadlist3.add(docBean);
                                    } else if (docBean.type == 4) {
                                        uploadlist4.add(docBean);
                                    }
                                }
                                if (newestIndex > 3)
                                    newestIndex = 0;
                                if (type == 0) {
                                    uploadList.addAll(docBeens);
                                } else if (type == 1) {
                                    uploadList.addAll(uploadlist1);
                                } else if (type == 2) {
                                    uploadList.addAll(uploadlist2);
                                } else if (type == 3) {
                                    uploadList.addAll(uploadlist3);
                                } else if (type == 4) {
                                    uploadList.addAll(uploadlist4);
                                }
                                hideAndSeek();
                                mAdapter.setData(uploadList);
                                mAdapter.notifyDataSetChanged();
                            }
                        })
        );
    }

    private void startByType(int type, int position) {
        switch (type) {
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

    private void openAudio(int position) {
        SearchResult result = new SearchResult();
        result.id = uploadList.get(position).id;
        result.cover = uploadList.get(position).cover;
        Intent intent = new Intent(mContext, AudioPlayActivity2.class);
        intent.putExtra("resultBean", new Gson().toJson(result));
        mContext.startActivity(intent);
    }

    private void openVideo(int position) {
        SearchResult result = new SearchResult();
        result.id = uploadList.get(position).id;
        Intent intent = new Intent(mContext, VideoPlayActivity2.class);
        intent.putExtra("resultBean", new Gson().toJson(result));
        mContext.startActivity(intent);
    }

    private void openBook(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("name", uploadList.get(position).title);
        bundle.putString("cover", uploadList.get(position).cover);
        bundle.putString("auth", uploadList.get(position).author);
        bundle.putString("isbn", uploadList.get(position).isbn);
        bundle.putString("publisher", uploadList.get(position).publisher);
        bundle.putString("school", Preference.instance(mContext)
                .getString(Preference.SchoolCode));
        bundle.putBoolean(BookDetailFragment.ISFROMMAINPAGE, true);
        bundle.putBoolean(ContentActivity.FRAG_ISBACK, true);
        bundle.putString(ContentActivity.FRAG_CLS, BookDetailFragment.class.getName());
        mContext.startActivity(bundle, ContentActivity.class);
    }

    private void openPhoto(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("type", "img");
        bundle.putString("id", uploadList.get(position).id);
        mContext.startActivity(bundle, DynamicCardActivity.class);
    }


    private void openDoc(int position) {
        Intent intent = new Intent();
        intent.putExtra("url", "");
        intent.putExtra("doc_id", uploadList.get(position).id);
        intent.setClass(mContext, PdfActivity2.class);
        mContext.startActivity(intent);
        mContext.overridePendingTransition(R.anim.zoomin,
                R.anim.alpha_outto);
    }

    @Override
    public void reSet() {
        hasData = true;
        curPage = 1;
        isCreate = false;
    }

    @Override
    public void notify(String data) {

    }

    private void hideAndSeek() {
        if (uploadList.size() == 0 && curPage == 1) {
            mNullTxt.setVisibility(View.VISIBLE);
        } else {
            mNullTxt.setVisibility(View.GONE);
        }
    }
}
