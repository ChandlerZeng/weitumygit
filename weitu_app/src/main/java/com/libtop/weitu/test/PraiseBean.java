package com.libtop.weitu.test;


import java.io.Serializable;
import java.util.List;

/**
 * Created by zeng on 2016/9/15.
 */
public class PraiseBean implements Serializable {

    /**
     * status : 1
     * praise_users : [{"uid":"1","mobile":13800000001,"name":"小华","logo":"http://v1.qzone.cc/avatar/201403/15/22/11/53245faf8a2ed399.jpg%21200x200.jpg","sex":0},{"uid":"2","mobile":13800000002,"name":"小旭","logo":"http://imgsrc.baidu.com/forum/w%3D580/sign=7e774f6f4710b912bfc1f6f6f3fcfcb5/aab04e90f603738d1a1104f6b31bb051f919ec38.jpg","sex":0},{"uid":"3","mobile":13800000003,"name":"小超","logo":"http://img.qqbody.com/uploads/allimg/201412/30-155813_832.jpg","sex":0},{"uid":"4","mobile":13800000004,"name":"小文","logo":"http://www.qqwangming.org/uploads/allimg/160513/1050093645-8.jpg","sex":0},{"uid":"5","mobile":13800000005,"name":"小军","logo":"http://qq1234.org/uploads/allimg/140929/3_140929114039_1.jpg","sex":0},{"uid":"6","mobile":13800000006,"name":"小勇","logo":"http://f.hiphotos.baidu.com/zhidao/wh%3D600%2C800/sign=aea8b3770b23dd542126af6ee1399fe6/e850352ac65c103836f801d1b0119313b07e891b.jpg","sex":0},{"uid":"7","mobile":13800000007,"name":"小海","logo":"http://v1.qzone.cc/avatar/201508/30/00/39/55e1e026dc781749.jpg%21200x200.jpg","sex":0},{"uid":"8","mobile":13800000008,"name":"小茵","logo":"http://www.qqwangming.org/uploads/allimg/160519/1051444I6-15.jpg","sex":1}]
     */

    public int status;
    /**
     * uid : 1
     * mobile : 13800000001
     * name : 小华
     * logo : http://v1.qzone.cc/avatar/201403/15/22/11/53245faf8a2ed399.jpg%21200x200.jpg
     * sex : 0
     */

    public List<UserBean> praise_users;


}
