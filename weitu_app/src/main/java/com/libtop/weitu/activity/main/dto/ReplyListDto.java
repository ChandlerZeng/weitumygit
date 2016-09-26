package com.libtop.weitu.activity.main.dto;

import com.google.gson.Gson;

/**
 * Created by zeng on 2016/9/23.
 */

public class ReplyListDto {

    /**
     * id : 57e884f004122049539a35c2
     * cid : 57e5f2290412245af120cf3e
     * rid : 57e8846b04122049539a35c0
     * uid : 565bea2c984ec06f56befda3
     * username : yjw
     * content : yui
     * timeline : 1474856176088
     * state : 0
     * user : {"id":"565bea2c984ec06f56befda3","username":"yjw","sex":0,"validatedEmail":0,"validatedPhone":0,"timeline":0,"avatar":"http://avatar.bookus.cn/556/556/565bea2c984ec06f56befda3-300.jpg"}
     * repliedUser : {"id":"565bea2c984ec06f56befda3","username":"yjw","sex":0,"validatedEmail":0,"validatedPhone":0,"timeline":0,"avatar":"http://avatar.bookus.cn/556/556/565bea2c984ec06f56befda3-300.jpg"}
     */

    public String id;
    public String cid;
    public String rid;
    public String uid;
    public String username;
    public String content;
    public long timeline;
    public int state;
    /**
     * id : 565bea2c984ec06f56befda3
     * username : yjw
     * sex : 0
     * validatedEmail : 0
     * validatedPhone : 0
     * timeline : 0
     * avatar : http://avatar.bookus.cn/556/556/565bea2c984ec06f56befda3-300.jpg
     */

    public UserBean user;
    /**
     * id : 565bea2c984ec06f56befda3
     * username : yjw
     * sex : 0
     * validatedEmail : 0
     * validatedPhone : 0
     * timeline : 0
     * avatar : http://avatar.bookus.cn/556/556/565bea2c984ec06f56befda3-300.jpg
     */

    public UserBean repliedUser;

    public static class UserBean {
        public String id;
        public String username;
        public int sex;
        public int validatedEmail;
        public int validatedPhone;
        public int timeline;
        public String avatar;

    }
}
