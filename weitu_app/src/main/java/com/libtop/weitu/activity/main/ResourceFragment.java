package com.libtop.weitu.activity.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.login.LoginFragment;
import com.libtop.weitu.activity.main.DocUpload.DocUploadActivity;
import com.libtop.weitu.activity.main.clickHistory.ResultBean;
import com.libtop.weitu.activity.main.videoUpload.VideoSelectActivity;
import com.libtop.weitu.activity.search.BookDetailFragment;
import com.libtop.weitu.activity.search.VideoPlayActivity2;
import com.libtop.weitu.activity.search.dto.SearchResult;
import com.libtop.weitu.activity.search.dynamicCardLayout.DynamicCardActivity;
import com.libtop.weitu.activity.source.AudioPlayActivity2;
import com.libtop.weitu.activity.source.PdfActivity2;
import com.libtop.weitu.base.BaseFragment;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.CheckUtil;
import com.libtop.weitu.utils.JsonUtil;
import com.libtop.weitu.utils.selector.view.ImageSelectActivity;
import com.libtop.weitu.viewadapter.CommonAdapter;
import com.libtop.weitu.viewadapter.ViewHolderHelper;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
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

    private List<ResultBean> mData = new ArrayList<>();

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
        HashMap<String, Object> params = new HashMap<>();
        params.put("uid", mPreference.getString(Preference.uid));
        params.put("method", "footprint.query");
        HttpRequest.loadWithMap(params).execute(new StringCallback()
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
                    List<ResultBean> mlist = JsonUtil.fromJson(json, new TypeToken<List<ResultBean>>()
                    {
                    }.getType());
                    resourceAdapter.addAll(mlist);
                    mData.addAll(mlist);
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
                ResultBean resultBean = (ResultBean) parent.getItemAtPosition(position);
                startByType(resultBean.type, position);
            }
        });
    }

    private void startByType(int type, int position)
    {
        switch (type)
        {
            case BOOK:
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
                break;
        }
    }

    private void openAudio(int position)
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
        bundle.putBoolean(ContentActivity.FRAG_ISBACK, false);
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
    }


    private class ResourceAdapter extends CommonAdapter<ResultBean>
    {


        public ResourceAdapter(Context context)
        {
            super(context, R.layout.item_fragment_resource);
        }


        @Override
        public void convert(ViewHolderHelper helper, ResultBean resultBean, int position)
        {
            ImageView resourceCover = helper.getView(R.id.img_item_resource);
            Picasso.with(context).load(resultBean.target.cover).fit().into(resourceCover);

            helper.setText(R.id.tv_item_resource_title,resultBean.target.title);
            helper.setText(R.id.tv_item_resource_uploader,resultBean.target.uploadUsername);
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
