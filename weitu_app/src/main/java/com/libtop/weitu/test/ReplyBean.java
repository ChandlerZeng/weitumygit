package com.libtop.weitu.test;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by zeng on 2016/9/12.
 */
public class ReplyBean implements Serializable {

    /**
     * id : 1
     * cid : 1
     * uid : 1
     * reply_uid : 2
     * content : 是啊!是啊!
     * t_create : 1429513964619
     * user : {"uid":1,"mobile":13800000001,"name":"小华","logo":"http://v1.qzone.cc/avatar/201403/15/22/11/53245faf8a2ed399.jpg%21200x200.jpg","sex":0}
     * reply_user : {"uid":2,"mobile":13800000002,"name":"小旭","logo":"http://imgsrc.baidu.com/forum/w%3D580/sign=7e774f6f4710b912bfc1f6f6f3fcfcb5/aab04e90f603738d1a1104f6b31bb051f919ec38.jpg","sex":0}
     */

    public String id;
    public String cid;
    public String uid;
    public String reply_uid;
    public String content;
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
     * uid : 2
     * mobile : 13800000002
     * name : 小旭
     * logo : http://imgsrc.baidu.com/forum/w%3D580/sign=7e774f6f4710b912bfc1f6f6f3fcfcb5/aab04e90f603738d1a1104f6b31bb051f919ec38.jpg
     * sex : 0
     */

    public UserBean reply_user;

}
