package com.libtop.weitu.base;

/**
 * Created by Administrator on 2016/1/8 0008.
 */
public class FragmentFactory {
    public static final BaseFragment newFragment(String fragCls){
        BaseFragment newFrag=null;
        try {
            Class<?> cls=Class.forName(fragCls);
//            cls.newInstance();
            newFrag=(BaseFragment)cls.newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return newFrag;
    }
}
