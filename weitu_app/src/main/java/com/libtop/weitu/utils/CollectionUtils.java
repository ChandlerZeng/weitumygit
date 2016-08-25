package com.libtop.weitu.utils;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/28 0028.
 */
public class CollectionUtils {
    public final static boolean isEmpty(Collection<?> data){
        return data==null||data.isEmpty();
    }

    public final static boolean isEmpty(Map<?,?> data){
        return data==null||data.isEmpty();
    }
}
