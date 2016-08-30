package com.libtop.weitu.activity.search;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.ListPopupWindow;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.classify.adapter.ClassifyCheckAdapter;
import com.libtop.weitu.activity.classify.bean.ClassifyBean;
import com.libtop.weitu.activity.search.adapter.MainPageAdapter;
import com.libtop.weitu.base.impl.NotifyFragment;
import com.libtop.weitu.eventbus.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnPageChange;

/**
 * Created by Administrator on 2015/12/23 0023.
 */
public class ResultFragment extends NotifyFragment {
    @Bind(R.id.viewpager)
    ViewPager mViewPager;
    @Bind(R.id.radioGroup)
    RadioGroup mRadioGroup;

    private ImageView imgSearchFilter;

    private List<Fragment> mFrags;
    private MainPageAdapter mAdapter;
    private boolean isCreate = false;

    private int pageIndex = 0;

    public static final int ALL=0,BOOK=1,VIDEO=2,AUDIO=3,DOC=4,IMAGE=5;

    private boolean isThreeSpinner = true;
    private ClassifyCheckAdapter filterCheckAdapter;
    private ListPopupWindow mListFilterPop;
    //记录页面所选择的排序方法
    private HashMap<Integer,Integer> map = new HashMap<>();
    private List<ClassifyBean> filterList = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFrags=new ArrayList<Fragment>();
        mFrags.add(new AllFragment());
        mFrags.add(new BooksFragment());
        mFrags.add(new VideosFragment());
        mFrags.add(new AudiosFragment());
        mFrags.add(new DocsFragment());
        mFrags.add(new ImagesFragment());
        mAdapter=new MainPageAdapter(getChildFragmentManager(),mFrags);
        initFilter();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_result_types_layout;
    }

    private void initFilter(){
        ImageView imgSearchFilter = (ImageView) getActivity().findViewById(R.id.search_filter);
        imgSearchFilter.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mListFilterPop.show();
            }
        });
        LinearLayout llBoard = (LinearLayout) getActivity().findViewById(R.id.ll_board);
            String[] filters = new String[]{"阅读最多","评论最多","收藏最多"};
            for (int i=0;i<filters.length;i++){
                ClassifyBean classifyBean = new ClassifyBean();
                classifyBean.name = filters[i];
                filterList.add(classifyBean);
            }
            filterCheckAdapter = new ClassifyCheckAdapter(mContext,filterList,true);
            mListFilterPop = new ListPopupWindow(mContext);
            mListFilterPop.setAdapter(filterCheckAdapter);
            mListFilterPop.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
            mListFilterPop.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
            mListFilterPop.setAnchorView(llBoard);//设置ListPopupWindow的锚点，即关联PopupWindow的显示位置和这个锚点
            mListFilterPop.setBackgroundDrawable(new ColorDrawable(0x99000000));
            mListFilterPop.setModal(true);//设置是否是模式
            mListFilterPop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("pageIndex",pageIndex);
                    map.put(pageIndex,position);
                    if (isThreeSpinner){
                        bundle.putString("sortType",sortTransform(position+1));
                    }else {
                        bundle.putString("sortType",sortTransform(position));
                    }
                    EventBus.getDefault().post(new MessageEvent(bundle));
                    filterCheckAdapter.setCheck(position);
                    mListFilterPop.dismiss();
                }
            });
    }


    private void threeFilter(){
        filterList.clear();
        String[] filters = getResources().getStringArray(R.array.spinner_array2);
        for (int i=0;i<filters.length;i++){
            ClassifyBean classifyBean = new ClassifyBean();
            classifyBean.name = filters[i];
            filterList.add(classifyBean);
        }
        filterCheckAdapter.upDateData(filterList);
    }

    private void fourFilter(){
        filterList.clear();
        String[] filters = getResources().getStringArray(R.array.spinner_array);
        for (int i=0;i<filters.length;i++){
            ClassifyBean classifyBean = new ClassifyBean();
            classifyBean.name = filters[i];
            filterList.add(classifyBean);
        }
        filterCheckAdapter.upDateData(filterList);
    }

    private String sortTransform(int position){
        String sortType = "timeline";
        switch (position){
            case 0:
                sortType="timeline";
                break;
            case 1:
                sortType="view";
                break;
            case 2:
                sortType="comment";
                break;
            case 3:
                sortType="favorite";
                break;
        }
        return sortType;
    }


    @Override
    public void onCreation(View root) {
        initView();
    }

    private void initView(){
        mViewPager.setAdapter(mAdapter);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                onCheckedChanged1(group,checkedId);
            }
        });
        //设置首次进入选择图书页面
        mViewPager.setCurrentItem(1);
    }

    @Override
    public void notify(String data) {
        int current=mViewPager.getCurrentItem();
        int max=mAdapter.getCount();
        for (int i=0;i<max;i++){
            NotifyFragment frag=(NotifyFragment)mAdapter.getItem(i);
            frag.reSet();
            if (current==i){
                frag.load();
            }
        }
    }

    public void onCheckedChanged1(RadioGroup group, int checkedId) {
        pageIndex=-1;
        fourFilter();
        isThreeSpinner = false;
        switch (checkedId){
            case R.id.search_all:{
                    pageIndex = ALL;
                    isThreeSpinner = true;
                    threeFilter();
            }
                break;
            case R.id.search_books:{
                    pageIndex = BOOK;
                    isThreeSpinner = true;
                    threeFilter();
            }
                break;
            case R.id.search_video:
                pageIndex=VIDEO;
                break;
            case R.id.search_audio:
                pageIndex=AUDIO;
                break;
            case R.id.search_document:
                pageIndex=DOC;
                break;
            case R.id.search_images:
                pageIndex=IMAGE;
                break;
        }
        if (map.get(pageIndex)==null){
            map.put(pageIndex,0);
        }
        filterCheckAdapter.setCheck(map.get(pageIndex));
        mViewPager.setCurrentItem(pageIndex);
    }

    @Nullable @OnPageChange(value = R.id.viewpager)
    public void onPageSelected(int i) {
        switch (i){
            case ALL:
                mRadioGroup.check(R.id.search_all);
                break;
            case BOOK:
                mRadioGroup.check(R.id.search_books);
                break;
            case VIDEO:
                mRadioGroup.check(R.id.search_video);
                break;
            case AUDIO:
                mRadioGroup.check(R.id.search_audio);
                break;
            case DOC:
                mRadioGroup.check(R.id.search_document);
                break;
            case IMAGE:
                mRadioGroup.check(R.id.search_images);
                break;
        }
    }


    public void setCreate() {
        isCreate = false;
    }

}
