package com.libtop.weitu.utils;

public class ContantsUtil
{

    public static final int currentVesion = 0;
    public static final String vesion = "1.4.0";

    // 注册临时信息，用了就删除了
    public static String phone;
    public static String passwd;
    public static String caption;
    public static String uid;
    public static String lib;
    public static String libName;

    // 找回密码
    public static String forgetPhone;
    public static String forgetCapton;

    // 更新状态信息
    public static boolean UPDATE_SEARCH = false;
    public static boolean UPDATE_HISTORY = false;

    public static boolean FAVORATE = false;
    public static boolean WANT = false;
    public static boolean SEEPRE = false;
    public static boolean COMMON = false;

    public static final String IS_DEVELOPING = "功能正在开发中,敬请期待...";

    //搜索库 HOST_ADDON
//    public static final String HOST = "http://channel-wt.yuntu.io";//现网环境
//    public static final String UHOST = "http://channel-pp.yuntu.io";//现网环境
//    public static final String HOST_ADDON = "http://www.yuntu.io/";//现网环境
//    public static final String AVATARHOST = "http://avatar.yuntu.io";//现网环境
//    public static final String IMGHOST2 = "http://image.yuntu.io/cover";//现网环境

        public static final String API_FAKE_HOST_PRIVATE = "http://192.168.0.9";//演示环境 TODO
        public static final String API_FAKE_HOST_PUBLIC = "http://115.28.189.104";//演示环境 TODO
//        public static final String HOST = "http://weitu.bookm.cn";//演示环境
//        public static final String HOST_ADDON = "http://www.bookm.cn";//演示环境
//        public static final String UHOST = "http://weitu.bookm.cn";//演示环境
//        public static final String AVATARHOST = "http://avatar.bookm.cn";//演示环境
//        public static final String IMGHOST2="http://image.bookm.cn/cover";//演示环境

        public static final String HOST_ADDON = "http://www.bookus.cn/";//测试环境
        public static final String HOST = "http://weitu.bookus.cn";//测试网环境
        public static final String UHOST = "http://weitu.bookus.cn";//测试网环境
        public static final String AVATARHOST = "http://avatar.bookus.cn";//测试环境
        public static final String IMGHOST2="http://image.bookus.cn/cover";//测试环境

    private static final String IMGHOST = "http://xpimg.qiniudn.com";

    private static final String TEST_UID_CESHI = "565bea2c984ec06f56befda3";
    private static final String TEST_UID_YANSHI = "http://xpimg.qiniudn.com";
    private static final String TEST_UID_ZHENGSHI = "http://xpimg.qiniudn.com";


    private static final String CDHOST = "http://disc.libtop.com";

    // 查询
    public static final String SEARCH = HOST + "/book/query.json";
    public static final String DETAIL = HOST + "/book/get.json";
    public static final String CD = CDHOST + "/disc";
    public static final String CD_DETAIL = CDHOST + "/folder";
    public static final String SCHOOL = HOST + "/library/list.json";
    public static final String LIBRARY = HOST + "/library/get.json";

    // img
    public static final String IMG_BASE = IMGHOST + "/";
    public static final String UPDATE_CHECK_URL = HOST + "/check.json";
    public static final String DOWNLOAD_URL = HOST + "";

    // 登陆注册
    public static final String GETKEY = HOST + "/getKey.json";
    public static final String LOGIN_URL = HOST + "/user/auth.json";
    public static final String CHECK_URL = HOST + "/user/check.json";
    public static final String SEND_CAPTCHA = HOST + "/user/sendCaptcha.json";
    public static final String VALIDATE_CAPTCHA = HOST + "/user/validateCaptcha.json";
    public static final String REGISTOR_URL = HOST + "/user/registerPhone.json";
    public static final String FORGET_PASSWD = HOST + "/user/resetPassword.json";
    public static final String UPDATE_PASSWD = HOST + "/user/updatePassword.json";
    public static final String UPDATE_LIBRARY = HOST + "/user/updateLibrary.json";
    public static final String UPDATE_SEX = HOST + "/user/updateSex.json";
    public static final String FAVORATE_URL = HOST + "/suggestion/save.json";
    public static final String BOOK_LIST = HOST + "/label/list.json";
    public static final String BOOK_ADD = HOST + "/label/add.json";

    //全站搜索
    public static final String FULLY_SEARCH = HOST_ADDON + "/search/api.json";
    //图片库列表
    public static final String LIBRARY_IMAGES = HOST_ADDON + "/image/list.json";
    //音视频列表
    public static final String LIBRARY_MEDIA = HOST_ADDON + "/media/list.json";
    //文档
    public static final String LIBRARY_DOCUMENT = HOST_ADDON + "/document";

    //评论
    public static final String LIBRARY_COMMENT = HOST_ADDON + "/document";


    public static final String getDocumentUrl(String id)
    {
        return LIBRARY_DOCUMENT + "/" + id + ".json";
    }


    public static String getFile(String lid, String isbn, long sequence, long id)
    {
        return CDHOST + "/file/" + isbn + "/" + sequence + "/" + id;
    }


    public static final String CATEGORIES = "/categories";

    public static final String CLASSIFY_TAG_PARENT = HOST_ADDON + CATEGORIES + "/findRoots.json";
    public static final String CLASSIFY_KEY_CHILD = HOST_ADDON + CATEGORIES + "/findChildren.json";

    public static final String NOTICE_LIST = HOST + "/notice/list";
    public static final String NOTICE_CONTENT = HOST + "/notice/get";

    public static final String SECHOST = "";

    public static final String shareContent = "这个资料对我的帮助很大！戳 微图-口袋学习利器\"http://www.yuntu.io/weitu\"下载App，海量高校特色资源随时看";


    /**
     * 获取头像地址
     *
     * @return
     */
    public static String getAvatarUrl(String id)
    {
        String subId = id.substring(0, 8);
        return new StringBuffer(AVATARHOST).append("/").append(Integer.parseInt(subId, 16) % 1024).append("/").append(Integer.parseInt(subId, 16) % 2048).append("/").append(id).append("-150.jpg").toString();
    }


    /**
     * 获取图片地址
     *
     * @param id
     * @return
     */
    public static final String getCoverUrl(String id)
    {
        String subId = id.substring(0, 8);
        return new StringBuffer(IMGHOST2).append("/").append(Integer.parseInt(subId, 16) % 1203).append("/").append(Integer.parseInt(subId, 16) % 3825).append("/").append(id).append("_230X290.jpg").toString();
    }


    public static final String getCoverUrlLittle(String id)
    {
        String subId = id.substring(0, 8);
        return new StringBuffer(IMGHOST2).append("/").append(Integer.parseInt(subId, 16) % 1203).append("/").append(Integer.parseInt(subId, 16) % 3825).append("/").append(id).append("_180X100.jpg").toString();
    }
}
