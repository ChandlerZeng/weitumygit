package com.libtop.weituR.activity.classify;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.classify.adapter.ClassifyAdapter;
import com.libtop.weituR.activity.classify.bean.ClassifyBean;
import com.libtop.weituR.activity.search.SearchActivity;
import com.libtop.weituR.base.BaseFragment;
import com.libtop.weituR.http.MapUtil;
import com.libtop.weituR.http.WeituNetwork;
import com.libtop.weituR.utils.ACache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by LianTu on 2016/7/19.
 */
public class ClassifyFragment2 extends BaseFragment {

    @Bind(R.id.list)
    ListView listView;

    private ACache mCache;

    private ClassifyAdapter mAdapter;
    private List<ClassifyBean> mData = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new ClassifyAdapter(mContext, mData);
        mCache = ACache.get(mContext);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_classify2;
    }

    @Override
    public void onCreation(View root) {
        initView();
        getData();
    }

    private void getData() {
//        一级分类
//        http://weitu.bookus.cn/categories/root.json?text={"method":"categories.root"}
        List<ClassifyBean> classifyBeens = (List<ClassifyBean>) mCache.getAsObject("classifyBeens");
        if (classifyBeens != null && !classifyBeens.isEmpty())
            handleResult(classifyBeens);
        Map<String, Object> map = new HashMap<>();
        map.put("method", "categories.root");
        String[] arrays = MapUtil.map2Parameter(map);
        subscription = WeituNetwork.getWeituApi()
                .getClassify(arrays[0], arrays[1], arrays[2])
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ClassifyBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        /*List<ClassifyBean> classifyBeens = (List<ClassifyBean>) mCache.getAsObject("classifyBeens");
                        if (classifyBeens == null || classifyBeens.isEmpty())
                            return;
                        onNext(classifyBeens);*/
                    }

                    @Override
                    public void onNext(List<ClassifyBean> classifyBeens) {
                        mCache.put("classifyBeens", (Serializable) classifyBeens);
                        handleResult(classifyBeens);
                    }
                });
    }

    private void handleResult(List<ClassifyBean> classifyBeens) {
        mData.clear();
        if (classifyBeens == null && classifyBeens.isEmpty())
            return;
        mData = classifyBeens;
        mAdapter.setData(mData);
        mAdapter.notifyDataSetChanged();
    }

    private void initView() {
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ClassifyBean classifyBean = mData.get(position);
                Intent intent = new Intent(mContext, ClassifyDetailActivity.class);
                intent.putExtra("code", classifyBean.code);
                intent.putExtra("name", classifyBean.name);
                startActivity(intent);
            }
        });
    }

    @Nullable
    @OnClick({R.id.search})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search:
                searchClick();
                break;
        }
    }

    private void searchClick() {
        mContext.startActivity(null, SearchActivity.class);
    }

}
