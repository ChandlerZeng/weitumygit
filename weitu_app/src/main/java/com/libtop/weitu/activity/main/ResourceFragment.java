package com.libtop.weitu.activity.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
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
import com.libtop.weitu.activity.user.dto.CollectBean;
import com.libtop.weitu.base.BaseFragment;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.service.WTStatisticsService;
import com.libtop.weitu.utils.Preference;
import com.libtop.weitu.utils.CheckUtil;
import com.libtop.weitu.utils.CollectionUtil;
import com.libtop.weitu.utils.ContextUtil;
import com.libtop.weitu.utils.ImageLoaderUtil;
import com.libtop.weitu.utils.JSONUtil;
import com.libtop.weitu.utils.ListViewUtil;
import com.libtop.weitu.utils.ShowHideOnScroll;
import com.libtop.weitu.utils.selector.view.ImageSelectActivity;
import com.libtop.weitu.viewadapter.CommonAdapter;
import com.libtop.weitu.viewadapter.ViewHolderHelper;
import com.libtop.weitu.widget.NetworkLoadingLayout;
import com.wangjie.androidbucket.utils.ABTextUtil;
import com.wangjie.androidbucket.utils.imageprocess.ABShape;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import okhttp3.Call;


/**
 * Created by LianTu on 2016-9-6.
 */
public class ResourceFragment extends BaseFragment implements NetworkLoadingLayout.OnRetryClickListner,RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener
{

    @Bind(R.id.lv_main_resource)
    ListView lvMainResource;
    @Bind(R.id.networkloadinglayout)
    NetworkLoadingLayout networkLoadingLayout;
    @Bind(R.id.activity_main_rfal)
    RapidFloatingActionLayout rfaLayout;
    @Bind(R.id.activity_main_rfab)
    RapidFloatingActionButton rfaBtn;

    private RapidFloatingActionHelper rfabHelper;

    private ResourceAdapter resourceAdapter;

    private List<CollectBean> mData = new ArrayList<>();

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
        networkLoadingLayout.showLoading();
        HashMap<String, Object> params = new HashMap<>();
        params.put("uid", mPreference.getString(Preference.uid));
        params.put("method", "favorite.query");
        HttpRequest.loadWithMap(params).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {
                networkLoadingLayout.showEmptyAndRetryPrompt();
            }


            @Override
            public void onResponse(String json, int id)
            {
                dismissLoading();
                if (!TextUtils.isEmpty(json))
                {
                    ArrayList<CollectBean> lists = JSONUtil.readBeanArray(json, CollectBean.class);
                    if (CollectionUtil.isEmpty(lists)){
                        networkLoadingLayout.showEmptyPrompt();
                    }else {
                        networkLoadingLayout.dismiss();
                        resourceAdapter.addAll(lists);
                        mData.addAll(lists);
                    }
                }else
                {
                    networkLoadingLayout.showEmptyAndRetryPrompt();
                }
            }
        });
    }


    private void initView()
    {
        ListViewUtil.addPaddingHeader(mContext,lvMainResource);
        lvMainResource.setOnTouchListener(new ShowHideOnScroll(rfaBtn));
        lvMainResource.setAdapter(resourceAdapter);
        lvMainResource.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                WTStatisticsService.onEvent(getActivity(), WTStatisticsService.EID_SCHOOL_RESOURCE_ITEM_CLI);

                CollectBean bean = (CollectBean) parent.getItemAtPosition(position);
                if (bean.favor.type == ContextUtil.ENTITY_TYPE_BOOK){
                    ContextUtil.openResourceByType(mContext,bean.favor.type, bean.target.getIsbn());
                }else {
                    ContextUtil.openResourceByType(mContext,bean.favor.type, bean.target.getId());
                }
            }
        });
        initFloatActionView();
    }


    private void initFloatActionView()
    {
        RapidFloatingActionContentLabelList rfaContent = new RapidFloatingActionContentLabelList(mContext);
        rfaContent.setOnRapidFloatingActionContentLabelListListener(this);
        List<RFACLabelItem> items = new ArrayList<>();
        items.add(new RFACLabelItem<Integer>()
                .setLabel("上传视频")
                .setResId(R.drawable.upload_video)
                .setIconNormalColor(ContextCompat.getColor(mContext,R.color.newGreen))
                .setIconPressedColor(ContextCompat.getColor(mContext,R.color.green1))
                .setLabelColor(ContextCompat.getColor(mContext,R.color.black))
                .setLabelBackgroundDrawable(ABShape.generateCornerShapeDrawable(ContextCompat.getColor(mContext,R.color.black_semi_transparent), ABTextUtil.dip2px(mContext, 4)))
                .setWrapper(0)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel("上传文档")
                .setResId(R.drawable.upload_doc)
                .setIconNormalColor(ContextCompat.getColor(mContext,R.color.newGreen))
                .setIconPressedColor(ContextCompat.getColor(mContext,R.color.green1))
                .setLabelColor(ContextCompat.getColor(mContext,R.color.black))
                .setLabelSizeSp(14)
                .setLabelBackgroundDrawable(ABShape.generateCornerShapeDrawable(ContextCompat.getColor(mContext,R.color.black_semi_transparent), ABTextUtil.dip2px(mContext, 4)))
                .setWrapper(1)
        );
        items.add(new RFACLabelItem<Integer>()
                .setLabel("上传图片")
                .setResId(R.drawable.upload_image)
                .setIconNormalColor(ContextCompat.getColor(mContext,R.color.newGreen))
                .setIconPressedColor(ContextCompat.getColor(mContext,R.color.green1))
                .setLabelBackgroundDrawable(ABShape.generateCornerShapeDrawable(ContextCompat.getColor(mContext,R.color.black_semi_transparent), ABTextUtil.dip2px(mContext, 4)))
                .setLabelColor(ContextCompat.getColor(mContext,R.color.black))
                .setWrapper(2)
        );
        rfaContent
                .setItems(items)
                .setIconShadowRadius(ABTextUtil.dip2px(mContext, 5))
                .setIconShadowColor(0xff888888)
                .setIconShadowDy(ABTextUtil.dip2px(mContext, 5));
        rfabHelper = new RapidFloatingActionHelper(
                mContext,
                rfaLayout,
                rfaBtn,
                rfaContent
        ).build();
    }


    @Override
    public void onRFACItemLabelClick(int position, RFACLabelItem item) {
        rfabHelper.toggleContent();
        rfacItemClick(position);
    }

    @Override
    public void onRFACItemIconClick(int position, RFACLabelItem item) {
        rfabHelper.toggleContent();
        rfacItemClick(position);
    }

    private void rfacItemClick(int position){
        switch (position){
            case 0 :
                WTStatisticsService.onEvent(getActivity(), WTStatisticsService.EID_SCHOOL_UPLOAD_VIDEO_CLI);
                uploadVideoClick();
                break;
            case 1 :
                WTStatisticsService.onEvent(getActivity(), WTStatisticsService.EID_SCHOOL_UPLOAD_DOCUMENT_CLI);
                uploadDocClick();
                break;
            case 2 :
                WTStatisticsService.onEvent(getActivity(), WTStatisticsService.EID_SCHOOL_UPLOAD_IMAGE_CLI);
                uploadImageClick();
                break;
        }
    }


    @Override
    public void onRetryClick(View v)
    {
        getResourceData();
    }


    private class ResourceAdapter extends CommonAdapter<CollectBean>
    {


        public ResourceAdapter(Context context)
        {
            super(context, R.layout.item_fragment_resource);
        }


        @Override
        public void convert(ViewHolderHelper helper, CollectBean collectBean, int position)
        {
            ImageView resourceCover = helper.getView(R.id.img_item_resource);
            ImageLoaderUtil.loadImage(context,resourceCover,collectBean.target.getCover());

            helper.setText(R.id.tv_item_resource_title,collectBean.target.getTitle());
            helper.setText(R.id.tv_item_resource_uploader,"上传者：" + collectBean.target.getUploadUsername());
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
