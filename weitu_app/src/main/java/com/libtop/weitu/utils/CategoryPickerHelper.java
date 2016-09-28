package com.libtop.weitu.utils;

import android.content.Context;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.model.IPickerViewData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.libtop.weitu.application.AppApplication;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by LianTu on 2016-9-26.
 */

public class CategoryPickerHelper
{
    private static  CategoryPickerHelper helper;
    private ArrayList<ArrayList<CategoryBean>> secondCategories;
    private ArrayList<CategoryBean> primaryCategories;
    private OptionsPickerView pvOptions;
    private OnCategorySelectListener listener;

    private CategoryPickerHelper(){
        secondCategories = new ArrayList<>();
        String json = FileUtil.readTxtFromAsset(AppApplication.getContext(),"categories.json");
        primaryCategories = new Gson().fromJson(json ,new TypeToken<List<CategoryBean>>(){}.getType());
        for (CategoryBean bean : primaryCategories){
            secondCategories.add(bean.getSubs());
        }
    }

    private void createPvOpitons(Context context){
        pvOptions = new OptionsPickerView(context);
        pvOptions.setPicker(primaryCategories, secondCategories,  true);
        pvOptions.setTitle("选择分类");
        pvOptions.setCyclic(false, false, false);
        pvOptions.setSelectOptions(1, 1, 1);
        pvOptions.setCancelable(true);
        pvOptions.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                CategoryBean primaryCategory = primaryCategories.get(options1);
                CategoryBean secondCategory = primaryCategory.getSubs().get(option2);

                if (listener != null)
                {
                    listener.onCategorySelect(primaryCategory, secondCategory);
                }
            }
        });
    }

    public synchronized static CategoryPickerHelper getInstance()
    {
        if (helper == null)
        {
            helper = new CategoryPickerHelper();
        }

        return helper;
    }


    public boolean isShowing(){
        return pvOptions.isShowing();
    }

    public void showPicker(Context context){
        createPvOpitons(context);
        pvOptions.show();
    }

    public void dismissPicker(){
        pvOptions.dismiss();
    }

    public void setOnCategorySelectListener(final OnCategorySelectListener listener){
        this.listener = listener;
    }

    public interface OnCategorySelectListener
    {
        void onCategorySelect(CategoryBean primaryCategory, CategoryBean secondCategory);
    }


    public class CategoryBean implements IPickerViewData
    {

        /**
         * code : 100000
         * name : 文学/新闻传播
         * subs : [{"code":100200,"name":"中国语言文学"},{"code":100300,"name":"外国语言文学"},{"code":100600,"name":"新闻传播学"},{"code":101300,"name":"其他"},{"code":100700,"name":"国学"}]
         */

        private int code;
        private String name;
        /**
         * code : 100200
         * name : 中国语言文学
         */

        private ArrayList<CategoryBean> subs;


        public int getCode()
        {
            return code;
        }


        public void setCode(int code)
        {
            this.code = code;
        }


        public String getName()
        {
            return name;
        }


        public void setName(String name)
        {
            this.name = name;
        }


        public ArrayList<CategoryBean> getSubs()
        {
            return subs;
        }


        public void setSubs(ArrayList<CategoryBean> subs)
        {
            this.subs = subs;
        }


        @Override
        public String getPickerViewText()
        {
            return getName();
        }
    }
}
