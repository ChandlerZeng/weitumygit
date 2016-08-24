package com.libtop.weituR.activity.classify.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by LianTu on 2016/7/19.
 */
public class ClassifyBean implements Serializable {

    /**
     * id : 5715e518984e8ea4387c7e64
     * name : 文学
     * code : 100000
     * parent : null
     * path : null
     * children : []
     * subCategories : null
     * count : 0
     */

    public String id;
    public String name;
    public long code;
//    public Object parent;
//    public Object path;
    public List<ClassifyBean> subCategories;
    public long count;
//    public String[] children;
    public String countString;
}
