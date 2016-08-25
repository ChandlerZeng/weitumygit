package com.libtop.weitu.activity.main;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.ListPopupWindow;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.ContentFragment;
import com.libtop.weitu.activity.classify.adapter.ClassifyCheckAdapter;
import com.libtop.weitu.activity.classify.bean.ClassifyBean;
import com.libtop.weitu.activity.main.adapter.SelectedAdapter;
import com.libtop.weitu.activity.main.dto.DisplayDto;
import com.libtop.weitu.activity.search.SearchActivity;
import com.libtop.weitu.activity.search.VideoPlayActivity2;
import com.libtop.weitu.activity.search.dto.SearchResult;
import com.libtop.weitu.activity.search.dynamicCardLayout.DynamicCardActivity;
import com.libtop.weitu.activity.source.AudioPlayActivity2;
import com.libtop.weitu.activity.source.PdfActivity2;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.utils.JsonUtil;
import com.libtop.weitu.widget.listview.XListView;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * <p>
 * Title: DelicateFragment.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * CreateTime：16/6/22
 * </p>
 *
 * @author 陆
 * @version common v1.0
 */

public class DelicateFragment extends ContentFragment {
    @Bind(R.id.delicate)
    XListView listview;
    @Bind(R.id.title)
    TextView tvTitle;
    @Bind(R.id.title_container)
    LinearLayout titleBar;
    private SelectedAdapter selectedAdapter;
    private List<DisplayDto> mData = new ArrayList<DisplayDto>();
    private Bundle bundle;
    private String method;
    private int page = 1;
    private int type = 3;

    private ListPopupWindow mListFilterPop;
    private ClassifyCheckAdapter filterCheckAdapter;
//    comment:评论数最多；favorite:收藏数最多；timeline:最新上传；view:浏览数最多
    private String filterString = "view";

    private boolean hasData = true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = ((ContentActivity) getActivity()).getCurrentExtra();
        method = bundle.getString("method");
        type = bundle.getInt("type");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.delicate_layout;
    }

    @Override
    public void onCreation(View root) {
        selectedAdapter = new SelectedAdapter(mContext, mData, 0, method);
        listview.setAdapter(selectedAdapter);
        listview.setPullLoadEnable(false);
        listview.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getNoticeData();
            }

            @Override
            public void onLoadMore() {
                if (hasData) {
//                    page++;
                    getNoticeData();
                }
//                page++;
//                getNoticeData();
            }
        });
        page = 1;
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (method) {
                    case "mediaAlbum.list": {
                        if (type == 1) {
                            openVideo(position - 1);
                        } else {
                            openAudio(position - 1);
                        }
                    }
                    break;
                    case "document.list":
                        openDoc(position - 1);
                        break;
                    case "imageAlbum.list":
                        openPhoto(position - 1);
                        break;
                }
            }
        });
        getNoticeData();
        if (method.equals("document.list"))
            tvTitle.setText("文档");
        else if (method.equals("imageAlbum.list"))
            tvTitle.setText("图库");
        initPopView();
    }

    private void initPopView() {
        String[] filters = new String[]{"综合","浏览数最多","评论数最多","收藏数最多","最新上传"};
        List<ClassifyBean> filterList = new ArrayList<>();
        for (int i=0;i<filters.length;i++){
            ClassifyBean classifyBean = new ClassifyBean();
            classifyBean.name = filters[i];
            filterList.add(classifyBean);
        }
        filterCheckAdapter = new ClassifyCheckAdapter(mContext,filterList,true);
        mListFilterPop = new ListPopupWindow(mContext);
        mListFilterPop.setAdapter(filterCheckAdapter);
        mListFilterPop.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        mListFilterPop.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
//        mListFilterPop.setBackgroundDrawable(null);
        mListFilterPop.setAnchorView(titleBar);//设置ListPopupWindow的锚点，即关联PopupWindow的显示位置和这个锚点
        mListFilterPop.setBackgroundDrawable(new ColorDrawable(0x99000000));
        mListFilterPop.setModal(true);//设置是否是模式
        mListFilterPop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                setFilter(position);
                filterCheckAdapter.setCheck(position);
                getNoticeData();
                mListFilterPop.dismiss();
            }
        });
    }

    private void setFilter(int position){
        switch (position){
            //综合
            case 0:
                filterString = "view";
                break;
            //浏览数最多
            case 1:
                filterString = "view";
                break;
            //评论数最多
            case 2:
                filterString = "comment";
                break;
            //收藏数最多
            case 3:
                filterString = "favorite";
                break;
            //最新上传
            case 4:
                filterString = "timeline";
                break;
        }
    }

    private void openAudio(int position) {
        SearchResult result = new SearchResult();
        result.id = mData.get(position).id;
        result.introduction=mData.get(position).introduction;
        result.cover = mData.get(position).cover;
        Intent intent = new Intent(mContext, AudioPlayActivity2.class);
        intent.putExtra("resultBean", new Gson().toJson(result));
        mContext.startActivity(intent);
    }

    private void openVideo(int position) {
        SearchResult result = new SearchResult();
        result.id = mData.get(position).id;
        Intent intent = new Intent(mContext, VideoPlayActivity2.class);
        intent.putExtra("resultBean", new Gson().toJson(result));
        mContext.startActivity(intent);
    }


    private void openPhoto(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("type", "img");
        bundle.putString("id", mData.get(position).id);
        mContext.startActivity(bundle, DynamicCardActivity.class);
    }

    private void openDoc(int position) {
        Intent intent = new Intent();
        intent.putExtra("url", "");
        intent.putExtra("doc_id", mData.get(position).id);
        intent.setClass(mContext, PdfActivity2.class);
        mContext.startActivity(intent);
        mContext.overridePendingTransition(R.anim.zoomin,
                R.anim.alpha_outto);
    }


    private void getNoticeData() {
        if(page==1)
        showLoding();
        Map<String, Object> params = new HashMap<String, Object>();
        if (type == 1){
            tvTitle.setText("视频");
            params.put("type", 1);
        } else if (type == 2){
            tvTitle.setText("音频");
            params.put("type", 2);
        }
        params.put("sort",filterString);
        params.put("method", method);
        params.put("page", page);
        HttpRequest.loadWithMap(params)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
//                        mLoading.dismiss();
//                        listview.stopRefresh();
                    }

                    @Override
                    public void onResponse(String json, int id) {
                        dismissLoading();
                        listview.stopRefresh();
//                        List<DisplayDto> mlist=new ArrayList<DisplayDto>();
                        if (!TextUtils.isEmpty(json)) {
                            try {
//                                JSONArray mjson = new JSONArray(json);
                                if (page ==1) {
                                    mData.clear();
                                }
                                List<DisplayDto> mlist = JsonUtil.fromJson(json, new TypeToken<List<DisplayDto>>() {
                                }.getType());
                                mData.addAll(mlist);
                                if (mlist.size() < 10) {
                                    hasData = false;
                                    listview.setPullLoadEnable(false);
                                } else {
                                    hasData = true;
                                    listview.setPullLoadEnable(true);
                                }
                                page++;
                                selectedAdapter.setData(mData);
                                selectedAdapter.notifyDataSetChanged();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return;
                        }
                        if (mData.size() == 0) {
                            showToast("没有相关数据");
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Nullable
    @OnClick({R.id.back_btn, R.id.container,R.id.img_search,R.id.search_filter})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                onBackPressed();
                break;
            case R.id.img_search:
                searchClick();
                break;
            case R.id.search_filter:
                searchFilterClick();
                break;
        }
    }

    //更改筛选
    private void searchFilterClick() {
        mListFilterPop.show();
    }

    //点击搜索
    private void searchClick() {
        mContext.startActivity(null, SearchActivity.class);
    }

}
