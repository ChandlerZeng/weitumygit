package com.libtop.weitu.test;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zeng on 2016/9/9.
 */
public class Resource implements CategoryResult{
        public String rid;
        public String name;
        public String intro;
        public String cover;
        public String uid;
        public String uploader_name;
        public long t_upload;
        public int type;
        public int file_type;
        public int count_comment;

        public UserBean user;

}
