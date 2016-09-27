package com.libtop.weitu.activity.main.dto;

import com.google.gson.Gson;

/**
 * Created by zeng on 2016/9/24.
 */

public class ReplyDto {

    /**
     * status : 1
     * reply : {"uid":"565bea2c984ec06f56befda3","repliedUser":{"uid":"565bea2c984ec06f56befda3","username":"yjw","avatar":"http://avatar.bookus.cn/556/556/565bea2c984ec06f56befda3-300.jpg","sex":0},"timeline":1474855220376,"id":"57e8813404122049539a35bc","state":0,"rid":"57e8812204122049539a35ba","user":{"uid":"565bea2c984ec06f56befda3","username":"yjw","avatar":"http://avatar.bookus.cn/556/556/565bea2c984ec06f56befda3-300.jpg","sex":0},"content":"eee","cid":"57e5f2290412245af120cf3e","username":"yjw"}
     */

    public int status;
    /**
     * uid : 565bea2c984ec06f56befda3
     * repliedUser : {"uid":"565bea2c984ec06f56befda3","username":"yjw","avatar":"http://avatar.bookus.cn/556/556/565bea2c984ec06f56befda3-300.jpg","sex":0}
     * timeline : 1474855220376
     * id : 57e8813404122049539a35bc
     * state : 0
     * rid : 57e8812204122049539a35ba
     * user : {"uid":"565bea2c984ec06f56befda3","username":"yjw","avatar":"http://avatar.bookus.cn/556/556/565bea2c984ec06f56befda3-300.jpg","sex":0}
     * content : eee
     * cid : 57e5f2290412245af120cf3e
     * username : yjw
     */

    public ReplyBean reply;


    public static class ReplyBean {
        public String uid;
        /**
         * uid : 565bea2c984ec06f56befda3
         * username : yjw
         * avatar : http://avatar.bookus.cn/556/556/565bea2c984ec06f56befda3-300.jpg
         * sex : 0
         */

        public UserBean repliedUser;
        public long timeline;
        public String id;
        public int state;
        public String rid;
        /**
         * uid : 565bea2c984ec06f56befda3
         * username : yjw
         * avatar : http://avatar.bookus.cn/556/556/565bea2c984ec06f56befda3-300.jpg
         * sex : 0
         */

        public UserBean user;
        public String content;
        public String cid;
        public String username;

        public static class UserBean {
            public String uid;
            public String username;
            public String avatar;
            public int sex;

        }
    }
}
