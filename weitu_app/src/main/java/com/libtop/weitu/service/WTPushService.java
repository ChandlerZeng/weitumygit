package com.libtop.weitu.service;

import android.content.Context;

import com.libtop.weitu.utils.LogUtil;

import cn.jpush.android.api.JPushInterface;


/**
 * @author Sai
 * @ClassName: WTPushService
 * @Description: 应用推送服务操作类
 * @date Sep 28, 2016 10:36:02 AM
 */
public class WTPushService
{
    private static final String TAG = WTPushService.class.getSimpleName();

    /**
     * 是否开启极光推送服务
     */
    public static boolean isEnableJPushService = true;


    /**
     * 初始化推送配置
     *
     * @param context                 上下文
     * @param isDebugMode             是否开启debug模式
     * @param isStatisticsEnable      是否开启数据统计功能
     * @param latestNotificationNumber 保留通知数目
     */
    public static void initPushConfigure(Context context, boolean isDebugMode, boolean isStatisticsEnable, int latestNotificationNumber)
    {
        if (isEnableJPushService)
        {
            JPushInterface.setDebugMode(isDebugMode); // 设置日志信息开关(发布时请关闭日志)
            LogUtil.d(TAG, "推送开启日志");

            JPushInterface.init(context); // 初始化JPush
            LogUtil.d(TAG, "推送SDK初始化");

            JPushInterface.setLatestNotificationNumber(context, latestNotificationNumber);
            JPushInterface.setStatisticsEnable(isStatisticsEnable); // 设置数据统计开关
        }
    }


    /**
     * 注册推送服务
     *
     * @param context 上下文
     * @param account 推送账户名(推荐使用应用登录用户的ID)
     */
    public static void registerPushService(Context context, String account)
    {
        if (isEnableJPushService)
        {
            JPushInterface.resumePush(context); // 运行推送服务
            LogUtil.d(TAG, "运行推送服务");

            JPushInterface.setAlias(context, account, null); // 设置推送别名
            LogUtil.d(TAG, "设置推送别名：" + account);
        }
    }


    /**
     * 反注册推送服务
     *
     * @param context 上下文
     */
    public static void unregisterPushService(Context context)
    {
        if (isEnableJPushService)
        {
            JPushInterface.setAliasAndTags(context.getApplicationContext(), "", null); // 取消极光推送别名的设置
            LogUtil.d(TAG, "取消推送别名的设置");

            JPushInterface.stopPush(context.getApplicationContext()); // 停止极光推送功能
            LogUtil.d(TAG, "停止推送服务");
        }
    }


    /**
     * 设置推送通知静默时间.当 (startHour==stopHour && startMinute==stopMinute)时,表示关闭推送通知静默
     *
     * @param startHour   静默开始小时数[0-23]
     * @param context     上下文
     * @param startMinute 静默开始分钟数[0-59]
     * @param stopHour    静默结束小时数[0-23]
     * @param stopMinute  静默结束分钟数[0-59]
     */
    public static void setSilenceTime(Context context, int startHour, int startMinute, int stopHour, int stopMinute)
    {
        JPushInterface.setSilenceTime(context, startHour, startMinute, stopHour, stopMinute);
    }
}
