package com.libtop.weitu.test;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by zeng on 2016/9/9.
 */
public class test {

    /**
     * status : 1
     * subjects : [{"sid":1,"name":"iOS动画案例之会跳舞的界面(上)","intro":"Jake大神的又一个新作,利用在iOS动画基础课程的基础上，添加了更丰富的动画使界面有了生命，很大程度的提升用户体验，让我们的界面更加优美生动","cover":"http://img.mukewang.com/57874063000195dc06000338-240-135.jpg","category":{"id":1,"name":"移动开发"},"uid":1,"count_follow":100,"has_new":0,"user":{"uid":1,"mobile":13800000001,"name":"小华","logo":"http://v1.qzone.cc/avatar/201403/15/22/11/53245faf8a2ed399.jpg%21200x200.jpg","sex":0}},{"sid":2,"name":"iOS动画案例之会跳舞的界面(下)","intro":"通过对之前《iOS动画案例之会跳舞的界面（上）》的学习 我们需要使用的元素都已经设计好了。接下来我们就可以使用这些元素将它们组合在一起让它们来跳舞吧!","cover":"http://img.mukewang.com/5707773c000120e906000338-240-135.jpg","category":{"id":1,"name":"移动开发"},"uid":2,"count_follow":200,"has_new":0,"user":{"uid":2,"mobile":13800000002,"name":"小旭","logo":"http://imgsrc.baidu.com/forum/w%3D580/sign=7e774f6f4710b912bfc1f6f6f3fcfcb5/aab04e90f603738d1a1104f6b31bb051f919ec38.jpg","sex":0}},{"sid":3,"name":"iOS9新特性之UIStackView","intro":"随着iOS机型越来越多，产品UI设计也越来越丰富，一个简单易用的UI集成控键工具，会大大的提高开发人员的效率。本课程将从实例出发介绍iOS9新组键UIStackView的使用，感受不一样的集成控键","cover":"http://img.mukewang.com/5678b421000118d406000338-240-135.jpg","category":{"id":1,"name":"移动开发"},"uid":3,"count_follow":300,"has_new":1,"user":{"uid":3,"mobile":13800000003,"name":"小超","logo":"http://img.qqbody.com/uploads/allimg/201412/30-155813_832.jpg","sex":0}},{"sid":4,"name":"iOS9新特性之UITesting","intro":"在Xcode7中，苹果介绍了一种新的方式来管理我们应用界面的测试工作，那就是UITesting。UI Testing 允许我们对 UI 元素进行查找，交互，验证属性和状态。不需要我们手指反复点击，即完成自动化测试工作","cover":"http://img.mukewang.com/566fdaa00001f55f06000338-240-135.jpg","category":{"id":1,"name":"移动开发"},"uid":4,"count_follow":400,"has_new":1,"user":{"uid":4,"mobile":13800000004,"name":"小文","logo":"http://www.qqwangming.org/uploads/allimg/160513/1050093645-8.jpg","sex":0}},{"sid":5,"name":"AIDL-小白成长记","intro":"AIDL是Android开发中比较常见的技术，想成为一名合格的Android攻城狮，看本次课程就对了！本节课将带领同学们快速入门AIDL相关技术，从基本语法，到案例解析。打造一键式学习过程！","cover":"http://img.mukewang.com/57075a800001081e06000338-240-135.jpg","category":{"id":1,"name":"移动开发"},"uid":5,"count_follow":500,"has_new":0,"user":{"uid":5,"mobile":13800000005,"name":"小军","logo":"http://qq1234.org/uploads/allimg/140929/3_140929114039_1.jpg","sex":0}},{"sid":6,"name":"Android-百度地图之导航","intro":"百度Andriod 导航SDK为Android移动端应用提供了一套简单易用的导航服务接口，适用于Android 2.1及以上版本。专注于为广大开发者提供最好的导航服务，通过使用百度导航SDK，开发者可以轻松为应用程序实现专业、高效、精准的导航功能。","cover":"http://img.mukewang.com/575fc4c10001de0406000338-240-135.jpg","category":{"id":1,"name":"移动开发"},"uid":6,"count_follow":600,"has_new":0,"user":{"uid":6,"mobile":13800000006,"name":"小勇","logo":"http://f.hiphotos.baidu.com/zhidao/wh%3D600%2C800/sign=aea8b3770b23dd542126af6ee1399fe6/e850352ac65c103836f801d1b0119313b07e891b.jpg","sex":0}},{"sid":7,"name":"360加速球核心效果实现","intro":"本课程通过高仿360加速球核心效果，带大家综合实战自定义控件，事件触摸处理，以及浮窗体等重难点技术。让大家在实战中体会这些知识点在实际项目中的运用，从而能学习致用，打造出更加酷绚的UI和体验更好的产品。","cover":"http://img.mukewang.com/579f0fb10001c5e006000338-240-135.jpg","category":{"id":1,"name":"移动开发"},"uid":7,"count_follow":700,"has_new":1,"user":{"uid":7,"mobile":13800000007,"name":"小海","logo":"http://v1.qzone.cc/avatar/201508/30/00/39/55e1e026dc781749.jpg%21200x200.jpg","sex":0}},{"sid":8,"name":"Android的各种Drawable讲解","intro":"介绍各自Drawable：BitmapDrawable、LayerDrawable、LevelListDrawable、TransitionDrawable、InsetDrawable等的常见用法和使用场景","cover":"http://img.mukewang.com/5784a3310001c2e506000338-240-135.jpg","category":{"id":1,"name":"移动开发"},"uid":8,"count_follow":800,"has_new":1,"user":{"uid":8,"mobile":13800000008,"name":"小茵","logo":"http://www.qqwangming.org/uploads/allimg/160519/1051444I6-15.jpg","sex":1}}]
     */

    public int status;
    /**
     * sid : 1
     * name : iOS动画案例之会跳舞的界面(上)
     * intro : Jake大神的又一个新作,利用在iOS动画基础课程的基础上，添加了更丰富的动画使界面有了生命，很大程度的提升用户体验，让我们的界面更加优美生动
     * cover : http://img.mukewang.com/57874063000195dc06000338-240-135.jpg
     * category : {"id":1,"name":"移动开发"}
     * uid : 1
     * count_follow : 100
     * has_new : 0
     * user : {"uid":1,"mobile":13800000001,"name":"小华","logo":"http://v1.qzone.cc/avatar/201403/15/22/11/53245faf8a2ed399.jpg%21200x200.jpg","sex":0}
     */

    public List<SubjectsBean> subjects;

    public static test objectFromData(String str) {

        return new Gson().fromJson(str, test.class);
    }

    public static class SubjectsBean {
        public int sid;
        public String name;
        public String intro;
        public String cover;
        /**
         * id : 1
         * name : 移动开发
         */

        public CategoryBean category;
        public int uid;
        public int count_follow;
        public int has_new;
        /**
         * uid : 1
         * mobile : 13800000001
         * name : 小华
         * logo : http://v1.qzone.cc/avatar/201403/15/22/11/53245faf8a2ed399.jpg%21200x200.jpg
         * sex : 0
         */

        public UserBean user;

        public static SubjectsBean objectFromData(String str) {

            return new Gson().fromJson(str, SubjectsBean.class);
        }

        public static class CategoryBean {
            public int id;
            public String name;

            public static CategoryBean objectFromData(String str) {

                return new Gson().fromJson(str, CategoryBean.class);
            }
        }

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
}
