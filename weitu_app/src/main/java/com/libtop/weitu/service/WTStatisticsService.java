package com.libtop.weitu.service;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;


/**
 * @author Sai
 * @ClassName: WTStatisticsService
 * @Description: 统计分析服务类
 * @date 10/8/16 17:16
 */
public class WTStatisticsService
{
    /**
     * 事件属性KEY: index(以"顺序"为参数时)
     */
    public static final String KEY_INDEX = "index";
    /**
     * 事件属性KEY: category(以"分类名"为参数时)
     */
    public static final String KEY_CATEGORY = "category";

    /*----------------------------------- homepage ---------------------------------------*/
    /**
     * 事件ID: 首页--搜索框
     */
    public static final String EID_HOMEPAGE_SEARCH_CLI = "homepage_search_cli";
    /**
     * 事件ID: 首页--二维码
     */
    public static final String EID_HOMEPAGE_QRCODE_CLI = "homepage_qrcode_cli";
    /**
     * 事件ID: 首页--banner
     */
    public static final String EID_HOMEPAGE_BANNER_CLI = "homepage_banner_cli";
    /**
     * 事件ID: 首页--分类
     */
    public static final String EID_HOMEPAGE_CATEGORY_ITEM_CLI = "homepage_category_item_cli";
    /**
     * 事件ID: 首页--推荐主题
     */
    public static final String EID_HOMEPAGE_SUBJECTRECOMMEND_ITEM_CLI = "homepage_subjectrecommend_item_cli";
    /**
     * 事件ID: 首页--推荐主题--更多
     */
    public static final String EID_HOMEPAGE_SUBJECTRECOMMEND_MORE_CLI = "homepage_subjectrecommend_more_cli";
    /**
     * 事件ID: 首页--推荐主题--更多--点击列表项
     */
    public static final String EID_SUBJECTRECOMMEND_ITEM_CLI = "subjectrecommend_item_cli";
    /**
     * 事件ID: 首页--热门主题
     */
    public static final String EID_HOMEPAGE_SUBJECTPOPULAR_ITEM_CLI = "homepage_subjectpopular_item_cli";
    /**
     * 事件ID: 首页--热门主题--更多
     */
    public static final String EID_HOMEPAGE_SUBJECTPOPULAR_MORE_CLI = "homepage_subjectpopular_more_cli";
    /**
     * 事件ID: 首页--热门主题--更多--点击列表项
     */
    public static final String EID_SUBJECTPOPULAR_ITEM_CLI = "subjectpopular_item_cli";
    /**
     * 事件ID: 首页--推荐资源
     */
    public static final String EID_HOMEPAGE_RESOURCERECOMMEND_ITEM_CLI = "homepage_resourcerecommend_item_cli";
    /**
     * 事件ID: 首页--通知
     */
    public static final String EID_HOMEPAGE_NOTICE_CLI = "homepage_notice_cli";

    /*----------------------------------- notice ---------------------------------------*/
    /**
     * 事件ID: 通知--系统通知
     */
    public static final String EID_NOTICE_DYNAMIC_ITEM_CLI = "notice_dynamic_item_cli";
    /**
     * 事件ID: 通知--校内通知
     */
    public static final String EID_NOTICE_SCHOOLNOTICE_ITEM_CLI = "notice_schoolnotice_item_cli";

    /*----------------------------------- school ---------------------------------------*/
    /**
     * 事件ID: 学堂--主题tab
     */
    public static final String EID_SCHOOL_SUBJECT_TAB_CLI = "school_subject_tab_cli";
    /**
     * 事件ID: 学堂--资源tab
     */
    public static final String EID_SCHOOL_RESOURCE_TAB_CLI = "school_resource_tab_cli";
    /**
     * 事件ID: 学堂--某个主题
     */
    public static final String EID_SCHOOL_SUBJECT_ITEM_CLI = "school_subject_item_cli";
    /**
     * 事件ID: 学堂--某个资源
     */
    public static final String EID_SCHOOL_RESOURCE_ITEM_CLI = "school_resource_item_cli";
    /**
     * 事件ID: 学堂--上传图片
     */
    public static final String EID_SCHOOL_UPLOAD_IMAGE_CLI = "school_upload_image_cli";
    /**
     * 事件ID: 学堂--上传视频
     */
    public static final String EID_SCHOOL_UPLOAD_VIDEO_CLI = "school_upload_video_cli";
    /**
     * 事件ID: 学堂--上传文档
     */
    public static final String EID_SCHOOL_UPLOAD_DOCUMENT_CLI = "school_upload_document_cli";
    /**
     * 事件ID: 学堂--新建主题
     */
    public static final String EID_SCHOOL_CREATESUBJECT_CLI = "school_createsubject_cli";

    /*----------------------------------- subject ---------------------------------------*/
    /**
     * 事件ID: 主题--进入主题页面
     */
    public static final String EID_SUBJECTDETAIL_PAGES = "subjectdetail_pages";
    /**
     * 事件ID: 主题--关注
     */
    public static final String EID_SUBJECTDETAIL_DOFOLLOW_CLI = "subjectdetail_dofollow_cli";
    /**
     * 事件ID: 主题--主题信息
     */
    public static final String EID_SUBJECTDETAIL_TITLE_CLI = "subjectdetail_title_cli";
    /**
     * 事件ID: 主题--已关注
     */
    public static final String EID_SUBJECTDETAIL_UNDOFOLLOW_CLI = "subjectdetail_undofollow_cli";
    /**
     * 事件ID: 主题--编辑
     */
    public static final String EID_SUBJECTDETAIL_EDIT_CLI = "subjectdetail_edit_cli";
    /**
     * 事件ID: 主题--打开资源
     */
    public static final String EID_SUBJECTDETAIL_RESOURCE_ITEM_CLI = "subjectdetail_resource_item_cli";

    /*----------------------------------- videoplay ---------------------------------------*/
    /**
     * 事件ID: 视频--进入视频页面
     */
    public static final String EID_VIDEOPLAY_PAGES = "videoplay_pages";
    /**
     * 事件ID: 视频--暂停
     */
    public static final String EID_VIDEOPLAY_PAUSE_CLI = "videoplay_pause_cli";
    /**
     * 事件ID: 视频--播放
     */
    public static final String EID_VIDEOPLAY_PLAY_CLI = "videoplay_play_cli";
    /**
     * 事件ID: 视频--拖动进度条
     */
    public static final String EID_VIDEOPLAY_PROGRESSBAR_CLI = "videoplay_progressbar_cli";
    /**
     * 事件ID: 视频--全屏
     */
    public static final String EID_VIDEOPLAY_INTOFULLSCREEN_CLI = "videoplay_intofullscreen_cli";
    /**
     * 事件ID: 视频--退出全屏
     */
    public static final String EID_VIDEOPLAY_EXITFULLSCREEN_CLI = "videoplay_exitfullscreen_cli";
    /**
     * 事件ID: 视频--收录
     */
    public static final String EID_VIDEOPLAY_DOINCLUDE_CLI = "videoplay_doinclude_cli";
    /**
     * 事件ID: 视频--收藏
     */
    public static final String EID_VIDEOPLAY_DOFAV_CLI = "videoplay_dofav_cli";
    /**
     * 事件ID: 视频--取消收藏
     */
    public static final String EID_VIDEOPLAY_UNDOFAV_CLI = "videoplay_undofav_cli";
    /**
     * 事件ID: 视频--评论
     */
    public static final String EID_VIDEOPLAY_COMMENT_CLI = "videoplay_comment_cli";
    /**
     * 事件ID: 视频--分享
     */
    public static final String EID_VIDEOPLAY_SHARE_CLI = "videoplay_share_cli";

    /*----------------------------------- audioplay ---------------------------------------*/
    /**
     * 事件ID: 音频--进入音频页面
     */
    public static final String EID_AUDIOPLAY_PAGES = "audioplay_pages";
    /**
     * 事件ID: 音频--暂停
     */
    public static final String EID_AUDIOPLAY_PAUSE_CLI = "audioplay_pause_cli";
    /**
     * 事件ID: 音频--播放
     */
    public static final String EID_AUDIOPLAY_PLAY_CLI = "audioplay_play_cli";
    /**
     * 事件ID: 音频--上一首
     */
    public static final String EID_AUDIOPLAY_PREVIOUS_CLI = "audioplay_previous_cli";
    /**
     * 事件ID: 音频--下一首
     */
    public static final String EID_AUDIOPLAY_NEXT_CLI = "audioplay_next_cli";
    /**
     * 事件ID: 音频--拖动进度条
     */
    public static final String EID_AUDIOPLAY_PROGRESSBAR_CLI = "audioplay_progressbar_cli";
    /**
     * 事件ID: 音频--文本
     */
    public static final String EID_AUDIOPLAY_TEXT_CLI = "audioplay_text_cli";
    /**
     * 事件ID: 音频--收录
     */
    public static final String EID_AUDIOPLAY_DOINCLUDE_CLI = "audioplay_doinclude_cli";
    /**
     * 事件ID: 音频--收藏
     */
    public static final String EID_AUDIOPLAY_DOFAV_CLI = "audioplay_dofav_cli";
    /**
     * 事件ID: 音频--取消收藏
     */
    public static final String EID_AUDIOPLAY_UNDOFAV_CLI = "audioplay_undofav_cli";
    /**
     * 事件ID: 音频--评论
     */
    public static final String EID_AUDIOPLAY_COMMENT_CLI = "audioplay_comment_cli";
    /**
     * 事件ID: 音频--分享
     */
    public static final String EID_AUDIOPLAY_SHARE_CLI = "audioplay_share_cli";

    /*----------------------------------- document ---------------------------------------*/
    /**
     * 事件ID: 文档--进入文档页面
     */
    public static final String EID_DOCUMENT_PAGES = "document_pages";
    /**
     * 事件ID: 文档--切换横屏
     */
    public static final String EID_DOCUMENT_ROTATE_CLI = "document_rotate_cli";
    /**
     * 事件ID: 文档--拖动进度条
     */
    public static final String EID_DOCUMENT_PROGRESSBAR_CLI = "document_progressbar_cli";
    /**
     * 事件ID: 文档--收录
     */
    public static final String EID_DOCUMENT_DOINCLUDE_CLI = "document_doinclude_cli";
    /**
     * 事件ID: 文档--收藏
     */
    public static final String EID_DOCUMENT_DOFAV_CLI = "document_dofav_cli";
    /**
     * 事件ID: 文档--取消收藏
     */
    public static final String EID_DOCUMENT_UNDOFAV_CLI = "document_undofav_cli";
    /**
     * 事件ID: 文档--评论
     */
    public static final String EID_DOCUMENT_COMMENT_CLI = "document_comment_cli";
    /**
     * 事件ID: 文档--分享
     */
    public static final String EID_DOCUMENT_SHARE_CLI = "document_share_cli";

    /*----------------------------------- bookdetail ---------------------------------------*/
    /**
     * 事件ID: 图书--进入图书页面
     */
    public static final String EID_BOOKDETAIL_PAGES = "bookdetail_pages";
    /**
     * 事件ID: 图书--馆藏
     */
    public static final String EID_BOOKDETAIL_STORAGE_CLI = "bookdetail_storage_cli";
    /**
     * 事件ID: 图书--光盘
     */
    public static final String EID_BOOKDETAIL_CD_CLI = "bookdetail_cd_cli";
    /**
     * 事件ID: 图书--试读
     */
    public static final String EID_BOOKDETAIL_TRYREAD_CLI = "bookdetail_tryread_cli";
    /**
     * 事件ID: 图书--收录
     */
    public static final String EID_BOOKDETAIL_DOINCLUDE_CLI = "bookdetail_doinclude_cli";
    /**
     * 事件ID: 图书--收藏
     */
    public static final String EID_BOOKDETAIL_DOFAV_CLI = "bookdetail_dofav_cli";
    /**
     * 事件ID: 图书--取消收藏
     */
    public static final String EID_BOOKDETAIL_UNDOFAV_CLI = "bookdetail_undofav_cli";
    /**
     * 事件ID: 图书--评论
     */
    public static final String EID_BOOKDETAIL_COMMENT_CLI = "bookdetail_comment_cli";
    /**
     * 事件ID: 图书--分享
     */
    public static final String EID_BOOKDETAIL_SHARE_CLI = "bookdetail_share_cli";

    /*----------------------------------- searchpre ---------------------------------------*/
    /**
     * 事件ID: 搜索--热门搜索
     */
    public static final String EID_SEARCHPRE_HOT_ITEM_CLI = "searchpre_hot_item_cli";
    /**
     * 事件ID: 搜索--搜索历史
     */
    public static final String EID_SEARCHPRE_HISTORY_ITEM_CLI = "searchpre_history_item_cli";
    /**
     * 事件ID: 搜索--删除单个搜索
     */
    public static final String EID_SEARCHPRE_HISTORY_ITEM_DEL_CLI = "searchpre_history_item_del_cli";
    /**
     * 事件ID: 搜索--清空搜索
     */
    public static final String EID_SEARCHPRE_HISTORY_CLEAR_CLI = "searchpre_history_clear_cli";
    /**
     * 事件ID: 搜索--语音搜索
     */
    public static final String EID_SEARCHPRE_VOICE_CLI = "searchpre_voice_cli";
    /**
     * 事件ID: 搜索--点击搜索
     */
    public static final String EID_SEARCHPRE_SEARCH_CLI = "searchpre_search_cli";

    /*----------------------------------- searchresult ---------------------------------------*/
    /**
     * 事件ID: 搜索--搜索结果--综合
     */
    public static final String EID_SEARCHRESULT_ALL_TAB_CLI = "searchresult_all_tab_cli";
    /**
     * 事件ID: 搜索--搜索结果--综合--点击列表项
     */
    public static final String EID_SEARCHRESULT_ALL_ITEM_CLI = "searchresult_all_item_cli";
    /**
     * 事件ID: 搜索--搜索结果--主题
     */
    public static final String EID_SEARCHRESULT_SUBJECT_TAB_CLI = "searchresult_subject_tab_cli";
    /**
     * 事件ID: 搜索--搜索结果--主题--点击列表项
     */
    public static final String EID_SEARCHRESULT_SUBJECT_ITEM_CLI = "searchresult_subject_item_cli";
    /**
     * 事件ID: 搜索--搜索结果--图书
     */
    public static final String EID_SEARCHRESULT_BOOK_TAB_CLI = "searchresult_book_tab_cli";
    /**
     * 事件ID: 搜索--搜索结果--图书--点击列表项
     */
    public static final String EID_SEARCHRESULT_BOOK_ITEM_CLI = "searchresult_book_item_cli";
    /**
     * 事件ID: 搜索--搜索结果--视频
     */
    public static final String EID_SEARCHRESULT_VIDEO_TAB_CLI = "searchresult_video_tab_cli";
    /**
     * 事件ID: 搜索--搜索结果--视频--点击列表项
     */
    public static final String EID_SEARCHRESULT_VIDEO_ITEM_CLI = "searchresult_video_item_cli";
    /**
     * 事件ID: 搜索--搜索结果--音频
     */
    public static final String EID_SEARCHRESULT_AUDIO_TAB_CLI = "searchresult_audio_tab_cli";
    /**
     * 事件ID: 搜索--搜索结果--音频--点击列表项
     */
    public static final String EID_SEARCHRESULT_AUDIO_ITEM_CLI = "searchresult_audio_item_cli";
    /**
     * 事件ID: 搜索--搜索结果--文档
     */
    public static final String EID_SEARCHRESULT_DOC_TAB_CLI = "searchresult_doc_tab_cli";
    /**
     * 事件ID: 搜索--搜索结果--文档--点击列表项
     */
    public static final String EID_SEARCHRESULT_DOC_ITEM_CLI = "searchresult_doc_item_cli";
    /**
     * 事件ID: 搜索--搜索结果--图库
     */
    public static final String EID_SEARCHRESULT_IMAGE_TAB_CLI = "searchresult_image_tab_cli";
    /**
     * 事件ID: 搜索--搜索结果--图库--点击列表项
     */
    public static final String EID_SEARCHRESULT_IMAGE_ITEM_CLI = "searchresult_image_item_cli";

    /*----------------------------------- main ---------------------------------------*/
    /**
     * 事件ID: 我的tab
     */
    public static final String EID_MAIN_USERCENTER_TAB_CLI = "main_usercenter_tab_cli";

    /*----------------------------------- usercenter ---------------------------------------*/
    /**
     * 事件ID: 我的--右上角设置
     */
    public static final String EID_USERCENTER_FLOATSETTINGS_CLI = "usercenter_floatsettings_cli";
    /**
     * 事件ID: 我的--头像
     */
    public static final String EID_USERCENTER_LOGO_CLI = "usercenter_logo_cli";
    /**
     * 事件ID: 我的--图书馆
     */
    public static final String EID_USERCENTER_LIBRARY_CLI = "usercenter_library_cli";
    /**
     * 事件ID: 我的--视频
     */
    public static final String EID_USERCENTER_MYVIDEO_CLI = "usercenter_myvideo_cli";
    /**
     * 事件ID: 我的--文档
     */
    public static final String EID_USERCENTER_MYDOC_CLI = "usercenter_mydoc_cli";
    /**
     * 事件ID: 我的--图库
     */
    public static final String EID_USERCENTER_MYIMAGE_CLI = "usercenter_myimage_cli";
    /**
     * 事件ID: 我的--我赞过的
     */
    public static final String EID_USERCENTER_MYPRAISE_CLI = "usercenter_mypraise_cli";
    /**
     * 事件ID: 我的--我的评论
     */
    public static final String EID_USERCENTER_MYCOMMENT_CLI = "usercenter_mycomment_cli";
    /**
     * 事件ID: 我的--反馈
     */
    public static final String EID_USERCENTER_FEEDBACK_CLI = "usercenter_feedback_cli";
    /**
     * 事件ID: 我的--设置
     */
    public static final String EID_USERCENTER_SETTINGS_CLI = "usercenter_settings_cli";

    /*----------------------------------- comment ---------------------------------------*/
    /**
     * 事件ID: 评论--进入评论页面
     */
    public static final String EID_COMMENT_PAGES = "comment_pages";
    /**
     * 事件ID: 评论--发表评论
     */
    public static final String EID_COMMENT_ADDCOMMENT_CLI = "comment_addcomment_cli";
    /**
     * 事件ID: 评论--回复
     */
    public static final String EID_COMMENT_ADDREPLY_CLI = "comment_addreply_cli";
    /**
     * 事件ID: 评论--赞
     */
    public static final String EID_COMMENT_DOPRAISE_CLI = "comment_dopraise_cli";
    /**
     * 事件ID: 评论--取消赞
     */
    public static final String EID_COMMENT_UNDOPRAISE_CLI = "comment_undopraise_cli";
    /**
     * 事件ID: 评论--展开
     */
    public static final String EID_COMMENT_EXPAND_CLI = "comment_expand_cli";
    /**
     * 事件ID: 评论--收起
     */
    public static final String EID_COMMENT_COLLAPSE_CLI = "comment_collapse_cli";
    /**
     * 事件ID: 评论--点击评论
     */
    public static final String EID_COMMENT_ITEM_CLI = "comment_item_cli";
    /**
     * 事件ID: 评论--点击回复
     */
    public static final String EID_COMMENT_REPLYMENU_CLI = "comment_replymenu_cli";

    /*----------------------------------- commentdetail ---------------------------------------*/
    /**
     * 事件ID: 评论详情--点击链接
     */
    public static final String EID_COMMENTDETAIL_RESOURCE_CLI = "commentdetail_resource_cli";
    /**
     * 事件ID: 评论详情--赞
     */
    public static final String EID_COMMENTDETAIL_PRAISE_CLI = "commentdetail_praise_cli";
    /**
     * 事件ID: 评论详情--发表回复
     */
    public static final String EID_COMMENTDETAIL_ADDREPLY_CLI = "commentdetail_addreply_cli";
    /**
     * 事件ID: 评论详情--点击回复
     */
    public static final String EID_COMMENTDETAIL_REPLY_ITEM_CLI = "commentdetail_reply_item_cli";


    /**
     * 统计发生次数(计数事件)
     *
     * @param context 上下文
     * @param eventId 统计的事件ID（事件id可用英文或数字，不建议使用中文）
     */
    public static void onEvent(Context context, String eventId)
    {
        MobclickAgent.onEvent(context, eventId);
    }


    /**
     * 统计点击行为各属性被触发的次数(计数事件)
     *
     * @param context 上下文
     * @param eventId 统计的事件ID（事件id可用英文或数字，不建议使用中文）
     * @param map     当前事件的属性和取值（Key-Value键值对）
     */
    public static void onEvent(Context context, String eventId, HashMap<String, String> map)
    {
        MobclickAgent.onEvent(context, eventId, map);
    }


    /**
     * 统计数值型变量的值的分布(计算事件)
     *
     * @param context 上下文
     * @param eventId 统计的事件ID（事件id可用英文或数字，不建议使用中文）
     * @param map     当前事件的属性和取值（Key-Value键值对）
     * @param du      当前事件的数值，取值范围是-2,147,483,648 到 +2,147,483,647 之间的有符号整数
     */
    public static void onEventValue(Context context, String eventId, HashMap<String, String> map, int du)
    {
        MobclickAgent.onEventValue(context, eventId, map, du);
    }


    /**
     * 创建 map 对象
     *
     * @param key
     * @param value
     * @return
     */
    public static HashMap<String, String> createMap(String key, String value)
    {
        HashMap<String, String> map = new HashMap<>();
        map.put(key, value);

        return map;
    }
}
