package com.libtop.weituR.activity.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.search.adapter.MainPageAdapter;
import com.libtop.weituR.base.impl.NotifyFragment;
import com.libtop.weituR.eventbus.MessageEvent;

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

    private List<Fragment> mFrags;
    private MainPageAdapter mAdapter;
    private boolean isCreate = false;

    private int pageIndex = 0;

    public static final int ALL=0,BOOK=1,VIDEO=2,AUDIO=3,DOC=4,IMAGE=5;

    private ArrayAdapter<CharSequence> adapter = null;

    private boolean isThreeSpinner = true;
    private Spinner spinner;
    //记录页面所选择的排序方法
    private HashMap<Integer,Integer> map = new HashMap<>();

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
        initSpinner();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_result_types_layout;
    }

    private void initSpinner(){
        spinner = (Spinner)mContext.findViewById(R.id.spinner);
        TextView textView = (TextView) mContext.findViewById(R.id.search);
        textView.setVisibility(View.GONE);
        List<CharSequence> planets = new ArrayList<CharSequence>();
        planets.add("阅读最多");
        planets.add("评论最多");
        planets.add("收藏最多");
        adapter = new ArrayAdapter<CharSequence>(mContext,R.layout.spinner_outlook, planets);
//        adapter = ArrayAdapter.createFromResource(mContext,R.array.spinner_array,R.layout.spinner_outlook);
        //设置下拉列表风格
        adapter.setDropDownViewResource(R.layout.spinner_drop_down);
        //将适配器添加到spinner中去
        spinner.setAdapter(adapter);
        spinner.setVisibility(View.VISIBLE);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub
                Bundle bundle = new Bundle();
                bundle.putInt("pageIndex",pageIndex);
                map.put(pageIndex,arg2);
                if (isThreeSpinner){
                    bundle.putString("sortType",sortTransform(arg2+1));
                }else {
                    bundle.putString("sortType",sortTransform(arg2));
                }
                EventBus.getDefault().post(new MessageEvent(bundle));

            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
    }

    private void threeFilter(){
        adapter.clear();
        adapter.addAll(getResources().getStringArray(R.array.spinner_array2));
    }

    private void fourFilter(){
        adapter.clear();
        adapter.addAll(getResources().getStringArray(R.array.spinner_array));
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
//        mViewPager.setOnPageChangeListener(this);
//        mRadioGroup.setOnCheckedChangeListener(this);
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
        spinner.setSelection(map.get(pageIndex));
        adapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(pageIndex);
//        ((NotifyFragment)mAdapter.getItem(itemIndex)).load();
    }

//    @Override
//    public void onPageScrolled(int i, float v, int i1) {
//
//    }

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

//    @Override
//    public void onPageScrollStateChanged(int i) {
//
//    }

    public void setCreate() {
        isCreate = false;
    }

}
