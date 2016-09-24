package com.libtop.weitu.activity.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.google.gson.reflect.TypeToken;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.main.dto.SubjectBean;
import com.libtop.weitu.base.BaseFragment;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.CollectionUtil;
import com.libtop.weitu.utils.ContextUtil;
import com.libtop.weitu.utils.ImageLoaderUtil;
import com.libtop.weitu.utils.JsonUtil;
import com.libtop.weitu.viewadapter.CommonAdapter;
import com.libtop.weitu.viewadapter.ViewHolderHelper;
import com.libtop.weitu.widget.NetworkLoadingLayout;
import com.melnykov.fab.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;


/**
 * Created by LianTu on 2016-9-6.
 */
public class SubjectFragment extends BaseFragment implements NetworkLoadingLayout.OnRetryClickListner
{

    @Bind(R.id.gv_main_theme)
    GridView gvMainTheme;
    @Bind(R.id.fab_main_new_subject)
    FloatingActionButton fabMainNewTheme;
    @Bind(R.id.networkloadinglayout)
    NetworkLoadingLayout networkLoadingLayout;

    private ThemeAdapter themeAdapter;
    

    @Override
    protected int getLayoutId()
    {
        return R.layout.fragment_main_subject;
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        themeAdapter = new ThemeAdapter(mContext);
    }


    @Override
    public void onCreation(View root)
    {
        super.onCreation(root);
        initView();
        getSubjectData();
    }


    private void initView()
    {
        networkLoadingLayout.setOnRetryClickListner(this);
        gvMainTheme.setAdapter(themeAdapter);
        fabMainNewTheme.attachToListView(gvMainTheme);
        gvMainTheme.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                SubjectBean subjectBean = (SubjectBean) parent.getItemAtPosition(position);
                ContextUtil.openSubjectDetail(mContext,subjectBean.getId());
            }
        });
    }


    private void getSubjectData()
    {
        networkLoadingLayout.showLoading();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("uid", Preference.instance(mContext).getString(Preference.uid));
        params.put("page", 1);
        params.put("method", "subject.my");
        HttpRequest.loadWithMap(params).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {
                networkLoadingLayout.showLoadFailAndRetryPrompt();
            }


            @Override
            public void onResponse(String json, int id)
            {
                if (!TextUtils.isEmpty(json))
                {
                    List<SubjectBean> lists = JsonUtil.fromJson(json, new TypeToken<List<SubjectBean>>(){}.getType());
                    if (CollectionUtil.isEmpty(lists)){
                        networkLoadingLayout.showEmptyAndRetryPrompt();
                    }else {
                        networkLoadingLayout.dismiss();
                        themeAdapter.addAll(lists);
                    }
                }
            }
        });
    }


    @Override
    public void onRetryClick(View v)
    {
        getSubjectData();
    }


    private class ThemeAdapter extends CommonAdapter<SubjectBean>
    {


        public ThemeAdapter(Context context)
        {
            super(context, R.layout.item_fragment_subject);
        }


        @Override
        public void convert(ViewHolderHelper helper, SubjectBean subjectBean, int position)
        {
            ImageView themeCover = helper.getView(R.id.img_item_subject);
            ImageView newCover = helper.getView(R.id.img_item_subject_new);
            Picasso.with(mContext).load(subjectBean.getCover())
                    .placeholder(ImageLoaderUtil.DEFAULT_BIG_IMAGE_RESOURCE_ID)
                    .error(ImageLoaderUtil.DEFAULT_BIG_IMAGE_RESOURCE_ID)
                    .fit()
                    .into(themeCover);

            helper.setText(R.id.tv_item_subject, subjectBean.getTitle());
        }
    }


    @OnClick({R.id.fab_main_new_subject})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.fab_main_new_subject:
                newSubjectClick();
                break;
        }
    }


    private void newSubjectClick()
    {
        Intent intent = new Intent(mContext, NewSubjectActivity.class);
        mContext.startActivity(intent);
    }

}
