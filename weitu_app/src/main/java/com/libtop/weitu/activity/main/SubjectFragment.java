package com.libtop.weitu.activity.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.libtop.weitu.R;
import com.libtop.weitu.base.BaseFragment;
import com.libtop.weitu.config.network.APIAddress;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.test.Subject;
import com.libtop.weitu.test.SubjectResource;
import com.libtop.weitu.utils.CollectionUtil;
import com.libtop.weitu.utils.JsonUtil;
import com.libtop.weitu.viewadapter.CommonAdapter;
import com.libtop.weitu.viewadapter.ViewHolderHelper;
import com.libtop.weitu.widget.NetworkLoadingLayout;
import com.melnykov.fab.FloatingActionButton;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.callback.StringCallback;

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
                Subject dpDto = (Subject) parent.getItemAtPosition(position);
                Intent intent = new Intent(mContext, SubjectDetailActivity.class);
                intent.putExtra("cover",dpDto.cover);
                startActivity(intent);
            }
        });
    }


    private void getSubjectData()
    {
        networkLoadingLayout.showLoading();
        HttpRequest.newLoad(APIAddress.SUBJECT_MY_ALL_LIST).execute(new StringCallback()
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
                    SubjectResource subjectResource = JsonUtil.fromJson(json, SubjectResource.class );
                    if (CollectionUtil.isEmpty(subjectResource.subjects)){
                        networkLoadingLayout.showEmptyAndRetryPrompt();
                    }else {
                        networkLoadingLayout.dismiss();
                        subjectResource.subjects.addAll(subjectResource.subjects);
                        themeAdapter.addAll(subjectResource.subjects);
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


    private class ThemeAdapter extends CommonAdapter<Subject>
    {


        public ThemeAdapter(Context context)
        {
            super(context, R.layout.item_fragment_subject);
        }


        @Override
        public void convert(ViewHolderHelper helper, Subject subject, int position)
        {
            ImageView themeCover = helper.getView(R.id.img_item_subject);
            ImageView newCover = helper.getView(R.id.img_item_subject_new);
            Picasso.with(context).load(subject.cover).fit().into(themeCover);

            helper.setText(R.id.tv_item_subject, subject.name);
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
