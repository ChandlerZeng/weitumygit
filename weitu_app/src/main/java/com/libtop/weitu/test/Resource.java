package com.libtop.weitu.test;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zeng on 2016/9/9.
 */
public class Resource implements CategoryResult{
        public int rid;
        public String name;
        public String intro;
        public String cover;
        public int uid;
        public String uploader_name;
        public long t_upload;
        public int type;
        public int file_type;
        public int count_comment;

        public UserBean user;


        public static class UserBean {
            public int uid;
            public long mobile;
            public String name;
            public String logo;
            public int sex;

            public static UserBean objectFromData(String str) {

                return new Gson().fromJson(str, UserBean.class);
        }
    }
}
