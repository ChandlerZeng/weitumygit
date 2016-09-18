package com.libtop.weitu.activity.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.login.LoginFragment;
import com.libtop.weitu.activity.main.DocUpload.DocUploadActivity;
import com.libtop.weitu.activity.main.videoUpload.VideoSelectActivity;
import com.libtop.weitu.base.BaseFragment;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.test.Resource;
import com.libtop.weitu.test.SubjectResource;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.CheckUtil;
import com.libtop.weitu.utils.ContantsUtil;
import com.libtop.weitu.utils.JsonUtil;
import com.libtop.weitu.utils.OpenResUtil;
import com.libtop.weitu.utils.selector.view.ImageSelectActivity;
import com.libtop.weitu.viewadapter.CommonAdapter;
import com.libtop.weitu.viewadapter.ViewHolderHelper;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;


/**
 * Created by LianTu on 2016-9-6.
 */
public class ResourceFragment extends BaseFragment
{

    @Bind(R.id.lv_main_resource)
    ListView lvMainResource;

    private ResourceAdapter resourceAdapter;

    public static final int VIDEO = 1, AUDIO = 2, DOC = 3, PHOTO = 4, BOOK = 5;

    private List<Resource> mData = new ArrayList<>();

    @Override
    protected int getLayoutId()
    {
        return R.layout.fragment_main_resource;
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        resourceAdapter = new ResourceAdapter(mContext);
    }


    @Override
    public void onCreation(View root)
    {
        super.onCreation(root);
        initView();
        getResourceData();
    }

    private void getResourceData()
    {
        showLoding();
        HttpRequest.newLoad(ContantsUtil.RESOURCE_MY_ALL_LIST).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {
            }


            @Override
            public void onResponse(String json, int id)
            {
                dismissLoading();
                if (!TextUtils.isEmpty(json))
                {
                    SubjectResource subjectResource = JsonUtil.fromJson(json, SubjectResource.class);
                    resourceAdapter.addAll(subjectResource.resources);
                    mData.addAll(subjectResource.resources);
                }
            }
        });
    }


    private void initView()
    {
        lvMainResource.setAdapter(resourceAdapter);
        lvMainResource.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Resource resource = (Resource) parent.getItemAtPosition(position);
                OpenResUtil.startByType(mContext,resource.type, resource.rid);
            }
        });
    }




    private class ResourceAdapter extends CommonAdapter<Resource>
    {


        public ResourceAdapter(Context context)
        {
            super(context, R.layout.item_fragment_resource);
        }


        @Override
        public void convert(ViewHolderHelper helper, Resource resource, int position)
        {
            ImageView resourceCover = helper.getView(R.id.img_item_resource);
            Picasso.with(context).load(resource.cover).fit().into(resourceCover);

            helper.setText(R.id.tv_item_resource_title,resource.name);
            helper.setText(R.id.tv_item_resource_uploader,resource.uploader_name);
        }
    }

    @OnClick({R.id.fab_main_upload_video, R.id.fab_main_upload_doc, R.id.fab_main_upload_image})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.fab_main_upload_video:
                uploadVideoClick();
                break;
            case R.id.fab_main_upload_doc:
                uploadDocClick();
                break;
            case R.id.fab_main_upload_image:
                uploadImageClick();
                break;
        }
    }


    private void uploadDocClick()
    {
        if (CheckUtil.isNull(mPreference.getString(Preference.uid)))
        {
            Bundle bundle1 = new Bundle();
            bundle1.putString(ContentActivity.FRAG_CLS, LoginFragment.class.getName());
            mContext.startActivity(bundle1, ContentActivity.class);
        }else
        {
            Intent intent = new Intent(mContext, DocUploadActivity.class);
            mContext.startActivity(intent);
        }
    }


    private void uploadImageClick()
    {
        if (CheckUtil.isNull(mPreference.getString(Preference.uid)))
        {
            Bundle bundle1 = new Bundle();
            bundle1.putString(ContentActivity.FRAG_CLS, LoginFragment.class.getName());
            mContext.startActivity(bundle1, ContentActivity.class);
        }else
        {
            Intent intent = new Intent(mContext, ImageSelectActivity.class);
            mContext.startActivity(intent);
        }
    }


    private void uploadVideoClick()
    {
        if (CheckUtil.isNull(mPreference.getString(Preference.uid)))
        {
            Bundle bundle1 = new Bundle();
            bundle1.putString(ContentActivity.FRAG_CLS, LoginFragment.class.getName());
            mContext.startActivity(bundle1, ContentActivity.class);
        }else
        {
            Intent intent = new Intent(mContext, VideoSelectActivity.class);
            mContext.startActivity(intent);
        }
    }
}
