package com.libtop.weitu.activity.main.subsubject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.ContentFragment;
import com.libtop.weitu.activity.main.adapter.RmdBooksAdapter;
import com.libtop.weitu.activity.main.adapter.SubjectFileAdapter;
import com.libtop.weitu.activity.search.BookDetailFragment;
import com.libtop.weitu.activity.search.SearchActivity;
import com.libtop.weitu.activity.search.dto.BookDto;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.test.Resource;
import com.libtop.weitu.test.SubjectResource;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.ContantsUtil;
import com.libtop.weitu.widget.listview.XListView;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import okhttp3.Call;


/**
 * Created by Zeng on 2016/9/8
 */
public class MoreRmdFileFragment extends ContentFragment {

    @Bind(R.id.back_btn)
    ImageView backBtn;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.title_container)
    LinearLayout titleContainer;
    @Bind(R.id.rmd_file_list)
    XListView rmdFileList;

    private List<Resource> resourceList = new ArrayList<>();
    private SubjectFileAdapter mAdapter;
    private int mCurPage = 1;
    private boolean hasData = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new SubjectFileAdapter(mContext, resourceList);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_rmdfiles_list;
    }


    @Override
    public void onCreation(View root) {
        initView();
        loadResourceFile();
    }


    private void initView() {
        title.setText("推荐文件");
        rmdFileList.setPullLoadEnable(false);
        rmdFileList.setAdapter(mAdapter);
        rmdFileList.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                mCurPage = 1;
                loadResourceFile();
            }

            @Override
            public void onLoadMore() {
                if (hasData) {
                    loadResourceFile();
                }
            }
        });
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
        Resource resource = resourceList.get(position);
        openBook(resource.name, resource.cover, resource.uploader_name, "9787504444622", "中国商业出版社,2001");//TODO
    }

    private void loadResourceFile(){
        String api = "/find/resource/recommend/list";
        HttpRequest.newLoad(ContantsUtil.API_FAKE_HOST_PUBLIC+api,null).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }


            @Override
            public void onResponse(String json, int id) {
                if (!TextUtils.isEmpty(json)) {
                    rmdFileList.stopRefresh();
                    try {
                        Gson gson = new Gson();
                        SubjectResource subjectResource = gson.fromJson(json, new TypeToken<SubjectResource>() {
                        }.getType());
                        List<Resource> list = new ArrayList<>();
                        list = subjectResource.resources;
                        if (list.size() < 10) {
                            hasData = false;
                            rmdFileList.setPullLoadEnable(false);
                        } else {
                            hasData = true;
                            rmdFileList.setPullLoadEnable(true);
                        }
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

}
