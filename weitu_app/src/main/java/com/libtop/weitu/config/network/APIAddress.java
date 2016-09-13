package com.libtop.weitu.config.network;

/**
 * @author Sai
 * @ClassName: APIAddress
 * @Description: 后台API地址配置信息类
 * @date 9/13/16 20:08
 */
public class APIAddress
{
    private static final String API_DOMAIN = "http://115.28.189.104";  //域名


    /**---------------------------------  通知  ----------------------------------------**/
    /**
     * 获取与我相关的通知列表
     */
    public static final String NOTICE_MY_LIST_URL = API_DOMAIN + "/notice/my/list";
    /**
     * 获取某条与我相关的通知的信息
     */
    public static final String NOTICE_MY_INFO_URL = API_DOMAIN + "/notice/my/info";
    /**
     * 删除某条与我相关的通知
     */
    public static final String NOTICE_MY_DEL_URL = API_DOMAIN + "/notice/my/del";
    /**
     * 获取校内通知列表
     */
    public static final String NOTICE_SCHOOL_LIST_URL = API_DOMAIN + "/notice/school/list";
}
