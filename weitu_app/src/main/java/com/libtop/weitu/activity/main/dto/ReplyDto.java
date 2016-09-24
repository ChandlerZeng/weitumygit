package com.libtop.weitu.activity.main.dto;

import com.google.gson.Gson;

/**
 * Created by zeng on 2016/9/24.
 */

public class ReplyDto {

    /**
     * status : 1
     * reply : {"uid":"565bea2c984ec06f56befda3","timeline":1474707479048,"id":"57e6401704128cc10b6444f6","state":0,"rid":"57e5f2690412245af120cf44","user":{"uid":"565bea2c984ec06f56befda3","username":"yjw","avatar":"http://avatar.bookus.cn/556/556/565bea2c984ec06f56befda3-300.jpg","sex":0},"content":"rrr","cid":"57e5f2690412245af120cf44","username":"yjw"}
     */

    public int status;
    /**
     * uid : 565bea2c984ec06f56befda3
     * timeline : 1474707479048
     * id : 57e6401704128cc10b6444f6
     * state : 0
     * rid : 57e5f2690412245af120cf44
     * user : {"uid":"565bea2c984ec06f56befda3","username":"yjw","avatar":"http://avatar.bookus.cn/556/556/565bea2c984ec06f56befda3-300.jpg","sex":0}
     * content : rrr
     * cid : 57e5f2690412245af120cf44
     * username : yjw
     */

    public ReplyBean reply;

    public static class ReplyBean {
        public String uid;
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
