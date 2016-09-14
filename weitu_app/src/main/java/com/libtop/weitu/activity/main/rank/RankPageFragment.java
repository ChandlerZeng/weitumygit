package com.libtop.weitu.activity.main.rank;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.classify.adapter.ClassifySubDetailAdapter;
import com.libtop.weitu.activity.user.SwipeMenu.SwipeMenuListView;
import com.libtop.weitu.base.BaseFragment;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.test.CategoryResult;
import com.libtop.weitu.test.SubjectResource;
import com.libtop.weitu.utils.ContantsUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import okhttp3.Call;


/**
 * Created by Zeng on 2016/9/7.
 */
public class RankPageFragment extends BaseFragment
{
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.list)
    SwipeMenuListView mListView;
    @Bind(R.id.null_txt)
    TextView mNullTxt;

    private ClassifySubDetailAdapter mAdapter;
    private List<CategoryResult> categoryResultList = new ArrayList<>();

    private final int ALL = 0;
    public static final int HOT_SUB = 0, HOT_RES = 1, NEWEST_SUB = 2, NEWEST_RES = 3;
    public static final int VIDEO = 1, AUDIO = 2, DOC = 3, PHOTO = 4, BOOK = 5;
    private String type;


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
        getData();
    }


    private void initView()
    {
        swipeRefreshLayout.setRefreshing(false);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
//                startByType(categoryResultList.get(position).type, position);
            }
        });
//        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW);
//        swipeRefreshLayout.measure(0, 0);
//        swipeRefreshLayout.setEnabled(false);
    }

    private void getData()
    {
        showLoding();
        HashMap<String, Object> map = new HashMap<>();
        map.put("type", type);
        String api = "/find/rank/list";
        HttpRequest.newLoad(ContantsUtil.API_FAKE_HOST_PUBLIC + api, map).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }


            @Override
            public void onResponse(String json, int id) {
                if (!TextUtils.isEmpty(json)) {
                    dismissLoading();
                    Gson gson = new Gson();
                    SubjectResource subjectResource = gson.fromJson(json, new TypeToken<SubjectResource>() {
                    }.getType());
                    categoryResultList.clear();
                    if (type.equals("subject")) {
                        categoryResultList.addAll(subjectResource.subjects);
                    } else {
                        categoryResultList.addAll(subjectResource.resources);
                    }
                    if (categoryResultList.isEmpty()) {
                        hideAndSeek();
                    }
                    mAdapter.setNewData(categoryResultList);
                }
            }
        });
    }
    /*private void getData()
    {
        showLoding();
//        swipeRefreshLayout.setRefreshing(true);
        HashMap<String, Object> map = new HashMap<>();
        map.put("uid", mPreference.getString(Preference.uid));
        if (type != ALL)
        {
            map.put("type", type);
        }
        map.put("method", "footprint.query");
        String[] arrays = MapUtil.map2Parameter(map);
        subscription = WeituNetwork.getWeituApi().getHistory(arrays[0], arrays[1], arrays[2]).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<ResultBean>>()
        {
            @Override
            public void onCompleted()
            {

            }


            @Override
            public void onError(Throwable e)
            {
//                swipeRefreshLayout.setRefreshing(false);

            }


            @Override
            public void onNext(List<ResultBean> resultBeen) {
                dismissLoading();
//                swipeRefreshLayout.setRefreshing(false);
                mData.clear();
                mData = resultBeen;
                if (mData.isEmpty())
                {
                    hideAndSeek();
                }
                mAdapter.setData(mData);
            }
        });


    }*/


    private void startByType(int type, int position)
    {
        switch (type)
        {
            /*case BOOK:
                openBook(position);
                break;
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
                break;*/
        }
    }




    /*private void openAudio(int position)
    {
        SearchResult result = new SearchResult();
        result.id = mData.get(position).target.id;
        result.cover = mData.get(position).target.cover;
        Intent intent = new Intent(mContext, AudioPlayActivity2.class);
        intent.putExtra("resultBean", new Gson().toJson(result));
        mContext.startActivity(intent);
    }


    private void openVideo(int position)
    {
        SearchResult result = new SearchResult();
        result.id = mData.get(position).target.id;
        Intent intent = new Intent(mContext, VideoPlayActivity2.class);
        intent.putExtra("resultBean", new Gson().toJson(result));
        mContext.startActivity(intent);
    }


    private void openBook(int position)
    {
        Bundle bundle = new Bundle();
        bundle.putString("name", mData.get(position).target.title);
        bundle.putString("cover", mData.get(position).target.cover);
        bundle.putString("auth", mData.get(position).target.author);
        bundle.putString("isbn", mData.get(position).target.isbn);
        bundle.putString("publisher", mData.get(position).target.publisher);
        bundle.putString("school", Preference.instance(mContext).getString(Preference.SchoolCode));
        bundle.putBoolean(BookDetailFragment.ISFROMMAINPAGE, true);
        bundle.putBoolean(ContentActivity.FRAG_ISBACK, true);
        bundle.putString(ContentActivity.FRAG_CLS, BookDetailFragment.class.getName());
        mContext.startActivity(bundle, ContentActivity.class);
    }


    private void openPhoto(int position)
    {
        Bundle bundle = new Bundle();
        bundle.putString("type", "img");
        bundle.putString("id", mData.get(position).target.id);
        mContext.startActivity(bundle, DynamicCardActivity.class);
    }


    private void openDoc(int position)
    {
        Intent intent = new Intent();
        intent.putExtra("url", "");
        intent.putExtra("doc_id", mData.get(position).target.id);
        intent.setClass(mContext, PdfActivity2.class);
        mContext.startActivity(intent);
        mContext.overridePendingTransition(R.anim.zoomin, R.anim.alpha_outto);
    }*/

    private void hideAndSeek()
    {
        if (categoryResultList.size() == 0)
        {
            mNullTxt.setVisibility(View.VISIBLE);
        }
        else
        {
            mNullTxt.setVisibility(View.GONE);
        }
    }
}
