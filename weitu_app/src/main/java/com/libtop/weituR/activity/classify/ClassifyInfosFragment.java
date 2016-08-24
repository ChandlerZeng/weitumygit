package com.libtop.weituR.activity.classify;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.ContentFragment;
import com.libtop.weituR.activity.search.AudiosFragment;
import com.libtop.weituR.activity.search.DocsFragment;
import com.libtop.weituR.activity.search.ImagesFragment;
import com.libtop.weituR.activity.search.SearchActivity;
import com.libtop.weituR.activity.search.VideosFragment;
import com.libtop.weituR.base.BaseFragment;
import com.libtop.weituR.base.FragmentFactory;
import com.libtop.weituR.base.impl.NotifyFragment;
import com.libtop.weituR.tool.Preference;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/1/21 0021.
 */
public class ClassifyInfosFragment extends ContentFragment {
    @Bind(R.id.keys)
    RadioButton mKeysBtn;
    @Bind(R.id.type)
    RadioButton mTypesBtn;
    @Bind(R.id.order)
    RadioButton mOrderBtn;

    @Bind(R.id.radio_group)
    RadioGroup mRadioGroup;

    @Bind(R.id.edit)
    EditText mTitleEdit;

    private TagsPop mPop;

    private String [] mTypes;
    private String [] mOrders;
    private String [] mKeys;
    private int [] mCodes;

    private String mCurrentTag;
    private String mCurrentKey;
    private String mCurrentOrder;
    private String mCurrentType;

    private boolean isRuned=false;



    private int getCode(String value){
        int index=-1;
        for (int i=0;i<mKeys.length;i++){
            if (value.equals(mKeys[i])){
                index=i;
                break;
            }
        }
        return mCodes[index];
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mKeys=mBundle.getStringArray("keys");
        mCodes=mBundle.getIntArray("codes");
        mPop=new TagsPop(mContext,mKeys);
        mTypes=getResources().getStringArray(R.array.source_type);
        mOrders=getResources().getStringArray(R.array.order_condition);
        mPop.setOnItemClickListener(new TagsPop.OnItemClickListener() {
            @Override
            public void onItemClick(TagsPop.STATUS status, String value) {
                //切换fragment
                switch (status) {
                    case KEYS:
                        mPreference.putInt(Preference.KEYWORD_CATECODE,getCode(value));
                        mKeysBtn.setText(value);
                        mCurrentKey=value;
                        loadCurrent();
                        break;
                    case TYPES:
                        mTypesBtn.setText(value);
                        mCurrentType=value;
                        if (value.equals("视频")){
                            replace("video",VideosFragment.class.getName());
                        }else if (value.equals("音频")){
                            replace("audio",AudiosFragment.class.getName());
                        }else if (value.equals("文档")){
                            replace("docs",DocsFragment.class.getName());
                        }else if (value.equals("相册")){
                            replace("image",ImagesFragment.class.getName());
                        }
                        loadCurrent();
                        break;
                    case OREDER:
                        mCurrentOrder=value;
                        mOrderBtn.setText(value);
                        loadCurrent();
                        break;

                }
            }
        });
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                onCheckedChanged1(group,checkedId);
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_classify_info;
    }

    private void loadCurrent(){
        NotifyFragment noo=((NotifyFragment)mFm.findFragmentByTag(mCurrentTag));
        if (noo!=null){
            noo.reSet();
            noo.load();
        }
    }

    @Override
    public void onDestroy() {
        mPop.dismiss();
        super.onDestroy();
    }

    @Override
    public void onCreation(View root) {
        super.onCreation(root);
//        mKeysBtn.setOnClickListener(this);
//        mTypesBtn.setOnClickListener(this);
//        mOrderBtn.setOnClickListener(this);
//        mTitleContainer.setOnClickListener(this);
        mTitleEdit.setVisibility(View.INVISIBLE);

        if (!isRuned){
            mCurrentKey=mKeys[0];
            mCurrentType=mTypes[0];
            mCurrentOrder=mOrders[0];
            mPreference.putInt(Preference.KEYWORD_CATECODE, mCodes[0]);
            replace("video", VideosFragment.class.getName());
        }
        mKeysBtn.setText(mCurrentKey);
        mTypesBtn.setText(mCurrentType);
        mOrderBtn.setText(mCurrentOrder);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isRuned){
            isRuned=true;
            loadCurrent();
        }
    }

    @Nullable
    @OnClick({R.id.back_btn,R.id.container})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_btn:
                onBackPressed();
                break;
            case R.id.container:
                mContext.startActivity(null, SearchActivity.class);
                break;
        }
    }

    private void replace(String tag,String cls){
        FragmentTransaction ft=mFm.beginTransaction();
        BaseFragment frag=(BaseFragment)mFm.findFragmentByTag(tag);
        if (frag==null){
            frag= FragmentFactory.newFragment(cls);
        }
        mCurrentTag=tag;
        ft.replace(R.id.content,frag,tag);
        ft.commit();
    }

    public void onCheckedChanged1(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.keys:
                mPop.show(mRadioGroup, TagsPop.STATUS.KEYS,mKeys);
                break;
            case R.id.type:
                mPop.show(mRadioGroup, TagsPop.STATUS.TYPES,mTypes);
                break;
            case R.id.order:
                mPop.show(mRadioGroup, TagsPop.STATUS.OREDER, mOrders);
                break;
        }
    }
}
