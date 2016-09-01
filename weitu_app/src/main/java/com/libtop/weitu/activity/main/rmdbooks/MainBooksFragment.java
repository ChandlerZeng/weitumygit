package com.libtop.weitu.activity.main.rmdbooks;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.ContentFragment;
import com.libtop.weitu.activity.main.adapter.MainImageAdapter;
import com.libtop.weitu.activity.main.dto.DocBean;
import com.libtop.weitu.activity.search.BookDetailFragment;
import com.libtop.weitu.activity.search.SearchActivity;
import com.libtop.weitu.http.MapUtil;
import com.libtop.weitu.http.WeituNetwork;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.widget.gridview.FixedGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Title: MainBooksFragment.java
 * CreateTime：16/8/9
 * @author zeng
 */
public class MainBooksFragment extends ContentFragment{
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.scroll)
    ScrollView mScroll;
    @Bind(R.id.ll_favorite_books)
    LinearLayout llFavorite;
    @Bind(R.id.ll_rmd_books)
    LinearLayout llRmd;
    @Bind(R.id.grid_view_favorite_books)
    FixedGridView mGrid2;
    @Bind(R.id.grid_view_rmd_books)
    FixedGridView mGrid1;

    private MainImageAdapter mainImageAdapter;
    private MainImageAdapter mainImageAdapter2;
    private boolean isInitiated=false;


    private List<DocBean> bList = new ArrayList<DocBean>();
    private List<DocBean> bList2 = new ArrayList<DocBean>();
    private CompositeSubscription _subscriptions = new CompositeSubscription();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_books_layout;
    }

    @Override
    public void onCreation(View root) {
        initView();
        initLoad();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                _subscriptions.clear();
                initLoad();
            }
        });
    }

    private void initLoad() {
        swipeRefreshLayout.setRefreshing(true);
        loadBookRecommand();
        loadBookFavorite();
    }

    private void initView() {
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW);
        mainImageAdapter = new MainImageAdapter(mContext, bList);
        mainImageAdapter2 = new MainImageAdapter(mContext, bList2);
        mGrid1.setAdapter(mainImageAdapter);
        mGrid2.setAdapter(mainImageAdapter2);
        mGrid1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DocBean bean = bList.get(position);
                openBook(bean.title, bean.cover, bean.author, bean.isbn, bean.publisher);
            }
        });
        mGrid2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DocBean bean = bList2.get(position);
                openBook(bean.title, bean.cover, bean.author, bean.isbn, bean.publisher);
            }
        });
        mScroll.smoothScrollTo(0, 0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _subscriptions.clear();
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    private void openBook(String bookName,String cover,String author,String isbn,String publisher) {
        Bundle bundle = new Bundle();
        bundle.putString("name", bookName);
        bundle.putString("cover", cover);
        bundle.putString("auth", author);
        bundle.putString("isbn", isbn);
        bundle.putString("publisher", publisher);
        bundle.putString("school", Preference.instance(mContext)
                .getString(Preference.SchoolCode));
        bundle.putBoolean("isFromMainPage", true);
        bundle.putBoolean(ContentActivity.FRAG_ISBACK, true);
        bundle.putString(ContentActivity.FRAG_CLS, BookDetailFragment.class.getName());
        mContext.startActivity(bundle, ContentActivity.class);
    }

    @Nullable
    @OnClick({R.id.rmd_book_btn_back,R.id.rmd_book_search_top,R.id.rmd_books_favorite_more_text,R.id.rmd_books_recommend_more_text})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rmd_book_btn_back:
                mContext.finish();
                break;
            case R.id.rmd_book_search_top:
                mContext.startActivity(null, SearchActivity.class);
                break;
            case R.id.rmd_books_recommend_more_text:
                Bundle bundle7 = new Bundle();
                bundle7.putString("method", "book.listRecommend");
                bundle7.putString("title", "推荐图书排行榜");
                bundle7.putString(ContentActivity.FRAG_CLS, RmdBooksFragment.class.getName());
                bundle7.putBoolean(ContentActivity.FRAG_WITH_ANIM, true);
                bundle7.putBoolean(ContentActivity.FRAG_ISBACK, true);
                mContext.startActivity(bundle7, ContentActivity.class);
                break;
            case R.id.rmd_books_favorite_more_text:
                Bundle bundle8 = new Bundle();
                bundle8.putString("method", "bookRank.list");
                bundle8.putString("title", "热门借阅图书排行榜");
                bundle8.putString(ContentActivity.FRAG_CLS, RmdBooksFragment.class.getName());
                bundle8.putBoolean(ContentActivity.FRAG_WITH_ANIM, true);
                bundle8.putBoolean(ContentActivity.FRAG_ISBACK, true);
                mContext.startActivity(bundle8, ContentActivity.class);
                break;
        }
    }
    private void loadBookRecommand() {
        if(!isInitiated)
            showLoding();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("method", "book.listRecommend");
        params.put("pageSize",6);
        String[] arrays = MapUtil.map2Parameter(params);
        _subscriptions.add(
        WeituNetwork.getWeituApi()
                .getNewest(arrays[0],arrays[1],arrays[2])
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<DocBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<DocBean> docBeens) {
                        swipeRefreshLayout.setRefreshing(false);
                        bList.clear();
                        bList = docBeens;
                        if (bList.isEmpty()){
                            llRmd.setVisibility(View.GONE);
                            return;
                        }else {
                            llRmd.setVisibility(View.VISIBLE);
                        }
                        if (bList.size()>6){
                            bList = bList.subList(0,6);
                        }
                        mainImageAdapter.setData(bList);
                        mainImageAdapter.notifyDataSetChanged();
                        dismissLoading();
                    }
                })
        );
    }
    private void loadBookFavorite() {
        if(!isInitiated)
            showLoding();
        isInitiated=true;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("method", "bookRank.list");
        params.put("lid",mPreference.getString(Preference.SchoolCode));
        params.put("pageSize",6);
        String[] arrays = MapUtil.map2Parameter(params);
        _subscriptions.add(
                WeituNetwork.getWeituApi()
                        .getNewest(arrays[0],arrays[1],arrays[2])
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<List<DocBean>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(List<DocBean> docBeens) {
                                swipeRefreshLayout.setRefreshing(false);
                                bList2.clear();
                                bList2 = docBeens;
                                if (bList2.isEmpty()){
                                    llFavorite.setVisibility(View.GONE);
                                    return;
                                }else {
                                    llFavorite.setVisibility(View.VISIBLE);
                                }
                                if (bList2.size()>6){
                                    bList2 = bList2.subList(0,6);
                                }
                                mainImageAdapter2.setData(bList2);
                                mainImageAdapter2.notifyDataSetChanged();
                                dismissLoading();
                            }
                        })
        );
    }

}
