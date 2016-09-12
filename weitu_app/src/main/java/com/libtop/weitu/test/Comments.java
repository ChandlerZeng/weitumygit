package com.libtop.weitu.test;

import java.util.List;

/**
 * Created by zeng on 2016/9/12.
 */
public class Comments {
    public int cid;
    public int uid;
    public int rid;
    public String content;
    public int my_praise;
    public int count_praise;
    public int count_reply;
    public long t_create;
    /**
     * uid : 1
     * mobile : 13800000001
     * name : 小华
     * logo : http://v1.qzone.cc/avatar/201403/15/22/11/53245faf8a2ed399.jpg%21200x200.jpg
     * sex : 0
     */

    public UserBean user;
    /**
     * rid : 1
     * name : Android开发进阶 从小工到专家
     * intro : 疯狂Android的编程指南 不止从入门到精通 更深入讲解开发编程核心知识点 Android 源码设计模式解析与实战作者何红辉力作
     * cover : http://img3x0.ddimg.cn/60/3/23913510-1_w_21.jpg
     * uid : 1
     * uploader_name : 小华
     * t_upload : 1429513964619
     * type : 3
     * file_type : 0
     * count_comment : 5
     * user : {"uid":1,"mobile":13800000001,"name":"小华","logo":"http://v1.qzone.cc/avatar/201403/15/22/11/53245faf8a2ed399.jpg%21200x200.jpg","sex":0}
     */

    public Resource resource;
    public List<ReplyBean> replys;

}
