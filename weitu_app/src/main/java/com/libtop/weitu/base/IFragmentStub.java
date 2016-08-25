package com.libtop.weitu.base;

import android.view.View;

/**
 * Created by Administrator on 2016/1/8 0008.
 */
public interface IFragmentStub {
//    /**
//     * 提供fragment界面的id
//     * @return
//     */
//    int getLayoutId();

    /**
     * 界面View对象创建时的回调方法
     * @param root
     */
    void onCreation(View root);

    /**
     * fragment回退事件
     */
    void onBackPressed();

}
