package com.libtop.weitu.activity.main.rmdbooks;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.ContentFragment;
import com.libtop.weitu.activity.main.adapter.RmdBooksAdapter;
import com.libtop.weitu.activity.search.BookDetailFragment;
import com.libtop.weitu.activity.search.SearchActivity;
import com.libtop.weitu.activity.search.dto.BookDto;
import com.libtop.weitu.dao.HistoryBo;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.ContantsUtil;
import com.libtop.weitu.widget.listview.XListView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnItemClick;
import okhttp3.Call;

/**
 * Created by Zeng on 2016/8/6
 */
public class RmdBooksFragment extends ContentFragment {
    @Bind(R.id.title)
    TextView mTitleText;
    @Bind(R.id.rmd_book_list)
    XListView mListView;

    private List<BookDto> listBooks = new ArrayList<BookDto>();
    private RmdBooksAdapter mAdapter;
    private int mCurPage = 1;
    private boolean hasData = true;
    private HistoryBo mHbo;
    private Bundle bundle;

    private String title;
    private String method;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = ((ContentActivity) getActivity()).getCurrentExtra();
        title = bundle.getString("title");
        method = bundle.getString("method");
        mHbo = new HistoryBo(mContext);
        mAdapter = new RmdBooksAdapter(mContext, listBooks);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_rmdbooks_list;
    }

    @Override
    public void onCreation(View root) {
        initView();
        loadRmdBooks();
    }

    private void initView() {
        mTitleText.setText(title);
        mListView.setAdapter(mAdapter);
        mListView.setPullLoadEnable(false);
        mListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                mCurPage = 1;
                loadRmdBooks();
            }

            @Override
            public void onLoadMore() {
                if (hasData) {
                    loadRmdBooks();
                }
            }
        });
        mCurPage = 1;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Nullable
    @OnClick({R.id.rmd_book_btn_back,R.id.rmd_book_search_top})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rmd_book_btn_back:
                onBackPressed();
                break;
            case R.id.rmd_book_search_top:
                mContext.startActivity(null, SearchActivity.class);
                break;
        }
    }

    @Nullable
    @OnItemClick(value = R.id.rmd_book_list)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //显示图书详情
        BookDto dto = listBooks.get(position - 1);
        // 保存点击记录
        mHbo.saveUpdate(
                dto.title,
                dto.id,
                dto.cover,
                dto.author,
                "",
                Preference.instance(mContext).getString(
                        Preference.SchoolCode), dto.publisher);
        ContantsUtil.UPDATE_HISTORY = false;
        Bundle bundle = new Bundle();
        bundle.putString("name", dto.title);
        bundle.putString("cover", dto.cover);
        bundle.putString("auth", dto.author);
        bundle.putString("isbn", dto.isbn);
        bundle.putString("publisher", dto.publisher);
        bundle.putString("school", Preference.instance(mContext)
                .getString(Preference.SchoolCode));
        bundle.putBoolean("isFromMainPage", true);
        bundle.putString(ContentActivity.FRAG_CLS, BookDetailFragment.class.getName());
        bundle.putBoolean(ContentActivity.FRAG_WITH_ANIM, true);
        bundle.putBoolean(ContentActivity.FRAG_ISBACK, true);
        mContext.startActivity(bundle, ContentActivity.class);
    }

    private void loadRmdBooks() {
        showLoding();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("page", mCurPage);
        params.put("method", method);
        HttpRequest.loadWithMapSec(params, new HttpRequest.CallBackSec() {
            @Override
            public void onError(Call call, Exception e, int id) {
                dismissLoading();
                showToast("无法连接服务器，请检查网络");
                mListView.stopRefresh();
            }

            @Override
            public void onResponse(String json, int id) {
                dismissLoading();
                if (TextUtils.isEmpty(json)) {
                    showToast("没有相关数据");
                    return;
                }
                mListView.stopRefresh();
                try {
                    listBooks.clear();
                    JSONArray array = new JSONArray(json);
                    for (int i = 0; i < array.length(); i++) {
                        BookDto bean = new BookDto();
                        bean.of(array.getJSONObject(i));
                        bean.title.replaceAll("　　","").trim();
                        listBooks.add(bean);
                    }
                    if (listBooks.size() < 10) {
                        hasData = false;
                        mListView.setPullLoadEnable(false);
                    } else {
                        hasData = true;
                        mListView.setPullLoadEnable(true);
                    }
                    mCurPage++;
                    mAdapter.setData(listBooks);
                    mAdapter.notifyDataSetChanged();
                    if (listBooks.size() == 0) {
                        showToast("没有相关数据");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showToast("Json数据解析错误");
                }
            }
        });
    }
}
