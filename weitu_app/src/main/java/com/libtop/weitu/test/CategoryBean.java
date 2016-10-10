package com.libtop.weitu.test;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zeng on 2016/9/17.
 */
public class CategoryBean implements Serializable {
    public int id;
    public String name;
    public int status;
    public List<Categories> categories;

    public class Categories{
        public String code;
        public String name;
        public String cover;
    }
}
