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

    /**---------------------------------  主题  ----------------------------------------**/
    /**
     * 我的主题(创建+关注)
     */
    public static final String SUBJECT_MY_ALL_LIST = API_DOMAIN + "/subject/my_all/list";
    /**
     * 获取某主题的资源列表(支持加载更多)
     */
    public static final String SUBJECT_RESOURCE_LIST = API_DOMAIN + "/subject/resource/list";


    /**---------------------------------  资源  ----------------------------------------**/
    /**
     * 获取我的资源列表
     */
    public static final String RESOURCE_MY_ALL_LIST = API_DOMAIN + "/resource/my_all/list";


    /**---------------------------------  用户  ----------------------------------------**/
    /**
     * 获取用户的评论列表
     */
    public static final String USER_COMMENT_LIST = API_DOMAIN + "/user/comment/list";
}
