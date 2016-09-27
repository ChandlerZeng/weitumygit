package com.libtop.weitu.activity.main.subsubject;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentFragment;
import com.libtop.weitu.activity.main.SubjectDetailActivity;
import com.libtop.weitu.activity.main.adapter.MoreSubjectAdapter;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.test.Subject;
import com.libtop.weitu.test.SubjectResource;
import com.libtop.weitu.utils.ContantsUtil;
import com.libtop.weitu.utils.JSONUtil;
import com.libtop.weitu.widget.NetworkLoadingLayout;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import okhttp3.Call;


/**
 * Created by Zeng on 2016/9/7.
 */
public class MoreSubjectFragment extends ContentFragment implements NetworkLoadingLayout.OnRetryClickListner {

    @Bind(R.id.back_btn)
    ImageView backBtn;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.sub_grid_view)
    GridView subGridView;
    @Bind(R.id.networkloadinglayout)
    NetworkLoadingLayout networkLoadingLayout;

    private MoreSubjectAdapter moreSubjectAdapter;
    private List<Subject> subjectList = new ArrayList<>();

    private boolean isFirstIn = true;


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
    }


    private void initView() {
        if (isFirstIn)
        {
            isFirstIn = false;
            networkLoadingLayout.showLoading();
            requestSubject();
        }
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
        networkLoadingLayout.setOnRetryClickListner(this);
    }

    private void requestSubject()
    {
        String api = "/find/subject/recommend/list";
        HttpRequest.newLoad(ContantsUtil.API_FAKE_HOST_PUBLIC + api, null).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                networkLoadingLayout.showLoadFailAndRetryPrompt();
            }


            @Override
            public void onResponse(String json, int id) {
                if (!TextUtils.isEmpty(json)) {
                    networkLoadingLayout.dismiss();
                    try {
                        SubjectResource subjectResource = JSONUtil.readBean(json, SubjectResource.class);
                        List<Subject> list = new ArrayList<>();
                        list = subjectResource.subjects;
                        if(list.size()==0){
                            networkLoadingLayout.showEmptyPrompt();
                        }
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

    @Override
    public void onRetryClick(View v) {
        requestSubject();
    }
}
