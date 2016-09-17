package com.libtop.weitu.activity.main.subsubject;

import android.content.Intent;
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
import com.libtop.weitu.activity.main.SubjectDetailActivity;
import com.libtop.weitu.activity.main.adapter.MoreSubjectAdapter;
import com.libtop.weitu.activity.main.dto.DisplayDto;
import com.libtop.weitu.activity.main.dto.DocBean;
import com.libtop.weitu.activity.search.dynamicCardLayout.DynamicCardActivity;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.http.MapUtil;
import com.libtop.weitu.http.WeituNetwork;
import com.libtop.weitu.test.Subject;
import com.libtop.weitu.test.SubjectResource;
import com.libtop.weitu.utils.ContantsUtil;
import com.libtop.weitu.utils.JsonUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.Serializable;
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
    private List<Subject> subjectList = new ArrayList<>();

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
                Subject subject = subjectList.get(position);
                Intent intent = new Intent(mContext, SubjectDetailActivity.class);
                intent.putExtra("cover",subject.cover);
                startActivity(intent);
            }
        });
        moreSubjectAdapter = new MoreSubjectAdapter(mContext,subjectList);
        subGridView.setAdapter(moreSubjectAdapter);

    }
    private void loadSubjectRecommand() {
        requestSubject();
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
    private void requestSubject()
    {
        showLoding();
        String api = "/find/subject/recommend/list";
        HttpRequest.newLoad(ContantsUtil.API_FAKE_HOST_PUBLIC + api, null).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }


            @Override
            public void onResponse(String json, int id) {
                if (!TextUtils.isEmpty(json)) {
                    dismissLoading();
                    try {
                        SubjectResource subjectResource = JsonUtil.fromJson(json, new TypeToken<SubjectResource>() {
                        }.getType());
                        List<Subject> list = new ArrayList<>();
                        list = subjectResource.subjects;
                        handleSubjectResult(list);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void handleSubjectResult(List<Subject> subList) {
        subjectList.clear();
        subjectList = subList;
        if (subjectList.isEmpty())
            return;
        moreSubjectAdapter.setData(subjectList);
    }
    private void openPhoto(String id) {
        Bundle bundle = new Bundle();
        bundle.putString("type", "img");
        bundle.putString("id", id);
        mContext.startActivity(bundle, DynamicCardActivity.class);
    }
}
