package com.libtop.weitu.utils;

import java.util.Collection;


/**
 * @author Sai
 * @ClassName: CollectionUtil
 * @Description: 集合工具类
 * @date 9/13/16 15:00
 */
public class CollectionUtil
{
    /**
     * 判断Collection是否为null或没有元素
     *
     * @param collection
     * @return 当collection==null或为空时返回 true, 否则返回 false.
     */
    public static boolean isEmpty(Collection<?> collection)
    {
        return (collection == null || collection.isEmpty());
    }


    /**
     * 获取Collection的长度
     *
     * @param collection
     * @return 当collection==null时返回0, 否则返回collection.size();
     */
    public static int getSize(Collection<?> collection)
    {
        return (collection != null ? collection.size() : 0);
    }
}
