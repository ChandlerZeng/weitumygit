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
import com.libtop.weitu.utils.CollectionUtil;
import com.libtop.weitu.utils.JsonUtil;
import com.libtop.weitu.utils.ListViewUtil;
import com.libtop.weitu.widget.listview.ScrollRefListView;
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
 * Created by LianTu on 2016/6/6.
 */
public class AllFragment extends NotifyFragment
{
    @Bind(R.id.list)
    ScrollRefListView mListview;
    @Bind(R.id.null_txt)
    TextView mNullTxt;

    private AllListAdapter mAdapter;
    private List<AllDto> mData = new ArrayList<AllDto>();
    private boolean isCreate = false, hasData = true, isFirstCreate = false;
    private int curPage = 1;

    private String sortType = "view";
    public static final String VIDEO = "video-album", AUDIO = "audio-album", DOC = "document", PHOTO = "image-album", BOOK = "book";

    private int fragmentType;
    private String thisFragmentType = VIDEO;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        Bundle bundle = this.getArguments();
        fragmentType = bundle.getInt("type", 0);
        mAdapter = new AllListAdapter(mContext, mData);
        curPage = 1;
        isFirstCreate = true;
    }


    @Override
    protected int getLayoutId()
    {
        return R.layout.fragment_search_all;
    }


    @Override
    public void onDestroy()
    {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(MessageEvent event)
    {
        Bundle bm = event.message;
        int pageIndex = bm.getInt("pageIndex");
        if (this.isVisible() && pageIndex == ResultFragment.ALL && !isFirstCreate)
        {
            sortType = bm.getString("sortType");
            curPage = 1;
            loadPage();
        }
    }


    @Override
    public void onCreation(View root)
    {
        initView();
    }


    private void initView()
    {
        ListViewUtil.addPaddingHeader(mContext,mListview);
        mListview.setAdapter(mAdapter);
        mListview.setPullLoadEnable(false);
        mListview.setXListViewListener(new ScrollRefListView.IXListViewListener()
        {
            @Override
            public void onLoadMore()
            {
                if (hasData)
                {
                    loadPage();
                }
            }
        });
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AllDto bean = (AllDto) parent.getItemAtPosition(position);
                startByType(bean.entityType,position - 2);
            }
        });
    }


    @Override
    public void load()
    {
        if (!isCreate)
        {
            curPage = 1;
            mData.clear();
            mAdapter.notifyDataSetChanged();
            isCreate = true;
            loadPage();
            return;
        }
        if (CollectionUtil.isEmpty(mData))
        {
            mNullTxt.setText("未搜索到相关记录");
            mNullTxt.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onResume()
    {
        super.onResume();
        load();
    }


    private void handleResult(String json)
    {
        Log.w("json", json);
        if (curPage == 1)
        {
            dismissLoading();
        }
        if (CheckUtil.isNullTxt(json))
        {
            mNullTxt.setText("未搜索到相关记录");
            mNullTxt.setVisibility(View.VISIBLE);
            return;
        }
        isFirstCreate = false;
        if (!CheckUtil.isNull(json))
        {
            List<AllDto> data = JsonUtil.fromJson(json, new TypeToken<List<AllDto>>()
            {
            }.getType());
            if (data == null || data.isEmpty())
            {
                return;
            }
            if (curPage == 1)
            {
                mData.clear();
            }

            if (data != null && data.size() < 10)
            {
                hasData = false;
                mListview.setPullLoadEnable(false);
            }
            else
            {
                hasData = true;
                mListview.setPullLoadEnable(true);
            }
            for (AllDto allDto:data){
                if (TextUtils.isEmpty(allDto.entityType)){
                    allDto.entityType = thisFragmentType;
                }
            }
            mData.addAll(data);
            mAdapter.setData(mData);
            if (mData.size() == 0 && curPage == 1)
            {
                mNullTxt.setText("未搜索到相关记录");
                mNullTxt.setVisibility(View.VISIBLE);
            }
            else
            {
                mNullTxt.setVisibility(View.GONE);
            }
            curPage++;
        }
        else
        {
            if (curPage == 1)
            {
                showToast("未搜索到相关记录");
            }
        }
    }

    private void startByType(String type, int position)
    {
        if (!TextUtils.isEmpty(type))
        {
            switch (type)
            {
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


    private void openAudio(int position)
    {
        SearchResult result = new SearchResult();
        result.id = mData.get(position).id;
        result.cover = mData.get(position).cover;
        Intent intent = new Intent(mContext, AudioPlayActivity2.class);
        intent.putExtra("resultBean", new Gson().toJson(result));
        mContext.startActivity(intent);
    }


    private void openVideo(int position)
    {
        SearchResult result = new SearchResult();
        result.id = mData.get(position).id;
        Intent intent = new Intent(mContext, VideoPlayActivity2.class);
        intent.putExtra("resultBean", new Gson().toJson(result));
        mContext.startActivity(intent);
    }


    private void openBook(int position)
    {
        Bundle bundle = new Bundle();
        bundle.putString("name", mData.get(position).title);
        bundle.putString("cover", mData.get(position).cover);
        bundle.putString("isbn", mData.get(position).isbn);
        bundle.putString("school", Preference.instance(mContext).getString(Preference.SchoolCode));
        bundle.putBoolean(ContentActivity.FRAG_ISBACK, false);
        bundle.putBoolean(BookDetailFragment.ISFROMMAINPAGE, true);
        bundle.putString(ContentActivity.FRAG_CLS, BookDetailFragment.class.getName());
        mContext.startActivity(bundle, ContentActivity.class);
    }


    private void openPhoto(int position)
    {
        Bundle bundle = new Bundle();
        bundle.putString("type", "img");
        bundle.putString("id", mData.get(position).id);
        mContext.startActivity(bundle, DynamicCardActivity.class);
    }


    private void openDoc(int position)
    {
        Intent intent = new Intent();
        intent.putExtra("url", "");
        intent.putExtra("doc_id", mData.get(position).id);
        intent.setClass(mContext, PdfActivity2.class);
        mContext.startActivity(intent);
        mContext.overridePendingTransition(R.anim.zoomin, R.anim.alpha_outto);
    }


    private void requestSearch()
    {

        Map<String,Object> params = requestMap();
        if (curPage == 1)
        {
            showLoding();
        }
        mNullTxt.setVisibility(View.GONE);
        HttpRequest.loadWithMap(params).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {
                dismissLoading();
            }


            @Override
            public void onResponse(String json, int id)
            {
                handleResult(json);
            }
        });
    }

    private Map<String, Object> requestMap(){
        Map<String, Object> params = new HashMap<String, Object>();
        switch (fragmentType)
        {
            case ResultFragment.ALL:
                //ALL
                params.put("method", "search.api");
                params.put("sort", sortType);
                params.put("keyword", mPreference.getString(Preference.KEYWORD_SEARCH));
                params.put("page", curPage);
                break;
            case ResultFragment.THEME:
                params.put("method", "book.query");
                params.put("key", mPreference.getString(Preference.KEYWORD_SEARCH));
                params.put("lid", mPreference.getString(Preference.SchoolCode));
                params.put("page", curPage);
                thisFragmentType = BOOK;
                break;
            case ResultFragment.BOOK:
                params.put("method", "book.query");
                params.put("key", mPreference.getString(Preference.KEYWORD_SEARCH));
                params.put("lid", mPreference.getString(Preference.SchoolCode));
                params.put("page", curPage);
                thisFragmentType = BOOK;
                break;
            case ResultFragment.VIDEO:
                params.put("method", "mediaAlbum.search");
                params.put("sort", sortType);
                params.put("type", 1);
                params.put("keyword", mPreference.getString(Preference.KEYWORD_SEARCH));
                params.put("page", curPage);
                thisFragmentType = VIDEO;
                break;
            case ResultFragment.AUDIO:
                params.put("method", "mediaAlbum.search");
                params.put("sort", sortType);
                params.put("type", 2);
                params.put("keyword", mPreference.getString(Preference.KEYWORD_SEARCH));
                params.put("page", curPage);
                thisFragmentType = AUDIO;
                break;
            case ResultFragment.DOC:
                params.put("method", "document.search");
                params.put("sort", "timeline");
                params.put("keyword", mPreference.getString(Preference.KEYWORD_SEARCH));
                params.put("page", curPage);
                thisFragmentType = DOC;
                break;
            case ResultFragment.IMAGE:
                params.put("method", "imageAlbum.search");
                params.put("sort", "timeline");
                params.put("keyword", mPreference.getString(Preference.KEYWORD_SEARCH));
                params.put("page", curPage);
                thisFragmentType = PHOTO;
                break;
        }
        return params;
    }


    private void loadPage()
    {
        requestSearch();
    }


    @Override
    public void reSet()
    {
        hasData = true;
        curPage = 1;
        isCreate = false;
    }


    @Override
    public void notify(String data)
    {

    }


    private void hideAndSeek()
    {
        if (mData.size() == 0 && curPage == 1)
        {
            mNullTxt.setText("未搜索到相关记录");
            mNullTxt.setVisibility(View.VISIBLE);
        }
        else
        {
            mNullTxt.setVisibility(View.GONE);
        }
    }
}

