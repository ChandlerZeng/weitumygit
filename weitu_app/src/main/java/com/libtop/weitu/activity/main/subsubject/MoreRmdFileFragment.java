package com.libtop.weitu.activity.main.subsubject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentFragment;
import com.libtop.weitu.activity.main.adapter.ResourceFileAdapter;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.test.Resource;
import com.libtop.weitu.test.SubjectResource;
import com.libtop.weitu.utils.ContantsUtil;
import com.libtop.weitu.utils.ContextUtil;
import com.libtop.weitu.utils.ListViewUtil;
import com.libtop.weitu.widget.NetworkLoadingLayout;
import com.libtop.weitu.widget.view.XListView;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnItemClick;
import okhttp3.Call;


/**
 * Created by Zeng on 2016/9/8
 */
public class MoreRmdFileFragment extends ContentFragment implements NetworkLoadingLayout.OnRetryClickListner{

    @Bind(R.id.back_btn)
    ImageView backBtn;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.title_container)
    LinearLayout titleContainer;
    @Bind(R.id.rmd_file_list)
    XListView rmdFileList;
    @Bind(R.id.networkloadinglayout)
    NetworkLoadingLayout networkLoadingLayout;

    private List<Resource> resourceList = new ArrayList<>();
    private ResourceFileAdapter mAdapter;
    private int mCurPage = 1;
    private boolean hasData = true;
    private boolean isFirstIn = true;
    private boolean isRefreshed = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new ResourceFileAdapter(mContext, resourceList);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_rmdfiles_list;
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
            loadResourceFile();
        }
        title.setText("推荐资源");
        ListViewUtil.addPaddingHeader(mContext,rmdFileList);
        rmdFileList.setPullLoadEnable(false);
        rmdFileList.setAdapter(mAdapter);
        rmdFileList.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                isRefreshed=true;
                loadResourceFile();
                mCurPage = 1;
            }

            @Override
            public void onLoadMore() {
                if (hasData) {
                    loadResourceFile();
                }
            }
        });
        networkLoadingLayout.setOnRetryClickListner(this);
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    @Nullable
    @OnClick({R.id.back_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                onBackPressed();
                break;
        }
    }


    @Nullable
    @OnItemClick(value = R.id.rmd_file_list)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //显示图书详情
        Resource resource = resourceList.get(position-2);
//        openBook(resource.name, resource.cover, resource.uploader_name, "9787504444622", "中国商业出版社,2001");//TODO

        ContextUtil.openResourceByType(mContext, resource.type, resource.rid, true);

    }

    private void loadResourceFile(){
        String api = "/find/resource/recommend/list";
        HttpRequest.newLoad(ContantsUtil.API_FAKE_HOST_PUBLIC+api,null).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if(mCurPage>1){

                }else if(!isRefreshed){
                    networkLoadingLayout.showLoadFailAndRetryPrompt();
                }
            }


            @Override
            public void onResponse(String json, int id) {
                if (!TextUtils.isEmpty(json)) {
                    rmdFileList.stopRefresh();
                    if(mCurPage==1){
                        networkLoadingLayout.dismiss();
                    }
                    try {
                        Gson gson = new Gson();
                        SubjectResource subjectResource = gson.fromJson(json, new TypeToken<SubjectResource>() {
                        }.getType());
                        List<Resource> list = new ArrayList<>();
                        list = subjectResource.resources;

                        if (list.size() < 20) {
                            hasData = false;
                            rmdFileList.setPullLoadEnable(false);
                        } else {
                            hasData = true;
                            rmdFileList.setPullLoadEnable(true);
                        }
                        if(list.size()==0 && mCurPage ==1){
                            networkLoadingLayout.showEmptyPrompt();
                        }
                        mCurPage++;
                        handleResourceFile(list);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void handleResourceFile(List<Resource> resources) {
        resourceList.clear();
        resourceList = resources;
        if (resourceList.isEmpty())
            return;

        mAdapter.setData(resourceList);
    }

    @Override
    public void onRetryClick(View v) {
        loadResourceFile();
    }
}
