package com.libtop.weitu.activity.main.rank;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.classify.adapter.ClassifySubDetailAdapter;
import com.libtop.weitu.activity.main.SubjectDetailActivity;
import com.libtop.weitu.activity.search.BookDetailFragment;
import com.libtop.weitu.base.BaseFragment;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.test.CategoryResult;
import com.libtop.weitu.test.Resource;
import com.libtop.weitu.test.Subject;
import com.libtop.weitu.test.SubjectResource;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.ContantsUtil;

import com.libtop.weitu.utils.ContextUtil;
import com.libtop.weitu.utils.ListViewUtil;
import com.libtop.weitu.widget.NetworkLoadingLayout;

import com.libtop.weitu.widget.listview.XListView;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import okhttp3.Call;


/**
 * Created by Zeng on 2016/9/7.
 */
public class RankPageFragment extends BaseFragment implements NetworkLoadingLayout.OnRetryClickListner
{
    @Bind(R.id.networkloadinglayout)
    NetworkLoadingLayout networkLoadingLayout;
    @Bind(R.id.xlist)
    XListView xListView;


    private ClassifySubDetailAdapter mAdapter;
    private List<CategoryResult> categoryResultList = new ArrayList<>();
    private List<Subject> subjectList = new ArrayList<>();
    private List<Resource> resourceList = new ArrayList<>();

    public static final int VIDEO = 1, AUDIO = 2, DOC = 3, PHOTO = 4, BOOK = 5;
    private String type;
    private int mCurPage = 1;
    private boolean hasData = false;
    private boolean isFirstIn = true;
    private boolean isRefreshed = false;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        type = bundle.getString("type", "subject");
        mAdapter = new ClassifySubDetailAdapter(mContext, categoryResultList);
    }


    @Override
    protected int getLayoutId()
    {
        return R.layout.fragment_rank_page;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }


    @Override
    public void onCreation(View root)
    {
        initView();
    }


    private void initView()
    {
        if (isFirstIn)
        {
            isFirstIn = false;
            networkLoadingLayout.showLoading();
            getData();
        }
        ListViewUtil.addPaddingHeader(mContext,xListView);
        xListView.setAdapter(mAdapter);
        xListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                if (type.equals("subject")) {
                    Subject subject = subjectList.get(position - 1);
                    Intent intent = new Intent(mContext, SubjectDetailActivity.class);
                    intent.putExtra("cover", subject.cover);
                    startActivity(intent);
                } else if (type.equals("resource")) {
//                    openBook(resourceList.get(position).name, resourceList.get(position).cover, resourceList.get(position).uploader_name, "9787504444622", "中国商业出版社,2001");//TODO

                    Resource resource = resourceList.get(position-1);
                    ContextUtil.openResourceByType(mContext, resource.type, resource.rid, true);
                }
            }
        });

        xListView.setPullLoadEnable(false);
        xListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                isRefreshed=true;
                getData();
                mCurPage = 1;
            }

            @Override
            public void onLoadMore() {
                if (hasData) {
                    getData();
                }
            }
        });
        networkLoadingLayout.setOnRetryClickListner(this);
    }

    private void getData()
    {
        HashMap<String, Object> map = new HashMap<>();
        map.put("type", type);
        String api = "/find/rank/list";
        HttpRequest.newLoad(ContantsUtil.API_FAKE_HOST_PUBLIC + api, map).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if(mCurPage>1){

                }else if(!isRefreshed) {
                    networkLoadingLayout.showLoadFailAndRetryPrompt();
                }
            }


            @Override
            public void onResponse(String json, int id) {
                if (!TextUtils.isEmpty(json)) {
                    xListView.stopRefresh();
                    if(mCurPage==1){
                        networkLoadingLayout.dismiss();
                    }
                    try{
                        Gson gson = new Gson();
                        SubjectResource subjectResource = gson.fromJson(json, new TypeToken<SubjectResource>() {
                        }.getType());
                        categoryResultList.clear();
                        if (type.equals("subject")) {
                            categoryResultList.addAll(subjectResource.subjects);
                            subjectList= subjectResource.subjects;
                            if (subjectList.size() < 10) {
                                hasData = false;
                                xListView.setPullLoadEnable(false);
                            } else {
                                hasData = true;
                                xListView.setPullLoadEnable(true);
                            }
                        } else {
                            categoryResultList.addAll(subjectResource.resources);
                            resourceList = subjectResource.resources;
                            if (resourceList.size() < 10) {
                                hasData = false;
                                xListView.setPullLoadEnable(false);
                            } else {
                                hasData = true;
                                xListView.setPullLoadEnable(true);
                            }
                        }
                        if(categoryResultList.size()==0 && mCurPage ==1){
                            networkLoadingLayout.showEmptyPrompt();
                        }
                        mCurPage++;
                        mAdapter.setNewData(categoryResultList);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    @Override
    public void onRetryClick(View v) {
        mCurPage = 1;
        getData();
    }
}
