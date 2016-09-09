package com.libtop.weitu.activity.main.subsubject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentFragment;
import com.libtop.weitu.activity.main.adapter.MoreSubjectAdapter;
import com.libtop.weitu.activity.main.dto.DisplayDto;
import com.libtop.weitu.activity.main.dto.DocBean;
import com.libtop.weitu.activity.search.dynamicCardLayout.DynamicCardActivity;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.http.MapUtil;
import com.libtop.weitu.http.WeituNetwork;
import com.libtop.weitu.utils.JsonUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import okhttp3.Call;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


/**
 * Created by Zeng on 2016/9/7.
 */
public class MoreSubjectFragment extends ContentFragment {

    @Bind(R.id.back_btn)
    ImageView backBtn;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.sub_grid_view)
    GridView subGridView;

    private MoreSubjectAdapter moreSubjectAdapter;
    private List<DocBean> bList = new ArrayList<DocBean>();
    private List<DisplayDto> displayDtoList = new ArrayList<DisplayDto>();

    private CompositeSubscription _subscriptions = new CompositeSubscription();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_more_sub_layout;
    }


    @Override
    public void onCreation(View root) {
        initView();
        loadSubjectRecommand();
    }


    private void initView() {
        title.setText("推荐主题");
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.finish();
            }
        });
        subGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DisplayDto dto = displayDtoList.get(position);
                openPhoto(dto.id);
            }
        });
        moreSubjectAdapter = new MoreSubjectAdapter(mContext,displayDtoList);
        subGridView.setAdapter(moreSubjectAdapter);

    }
    private void loadSubjectRecommand() {
        requestImages();
//        requestBooks();
    }

    private void requestBooks(){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("method", "book.listRecommend");
        String[] arrays = MapUtil.map2Parameter(params);
        _subscriptions.add(
                WeituNetwork.getWeituApi()
                        .getNewest(arrays[0], arrays[1], arrays[2])
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
                                handleBookResult(docBeens);
                            }
                        })
        );
    }
    private void handleBookResult(List<DocBean> docBeens) {
        bList.clear();
        bList = docBeens;
        if (bList.isEmpty())
            return;
//        moreSubjectAdapter.setData(bList);
    }
    private void requestImages()
    {
        showLoding();
        int page = 1;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("method", "imageAlbum.list");
        params.put("page", page);
        HttpRequest.loadWithMap(params).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }


            @Override
            public void onResponse(String json, int id) {
                if (!TextUtils.isEmpty(json)) {
                    dismissLoading();
                    try {
                        List<DisplayDto> listDisplayDtos = JsonUtil.fromJson(json, new TypeToken<List<DisplayDto>>() {
                        }.getType());
                        handleImageResult(listDisplayDtos);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void handleImageResult(List<DisplayDto> displayDtos) {
        displayDtoList.clear();
        displayDtoList = displayDtos;
        if (displayDtoList.isEmpty())
            return;
        moreSubjectAdapter.setData(displayDtoList);
    }
    private void openPhoto(String id) {
        Bundle bundle = new Bundle();
        bundle.putString("type", "img");
        bundle.putString("id", id);
        mContext.startActivity(bundle, DynamicCardActivity.class);
    }
}
