package com.libtop.weitu.utils;

import android.content.Context;


/**
 * @author Sai
 * @ClassName: MessageRemindUtil
 * @Description: 消息提醒工具类
 * @date 9/29/16 11:44
 */
public class MessageRemindUtil
{
    // 添加动态新消息提醒
    public static void addNewDynamicRemind(Context context)
    {
        Preference.instance(context).putBoolean(Preference.MESSAGE_DYNAMIC_NOTICE, true);
    }


    // 清除动态新消息提醒
    public static void clearDynamicRemind(Context context)
    {
        Preference.instance(context).putBoolean(Preference.MESSAGE_DYNAMIC_NOTICE, false);
    }


    // 是否有新的动态消息
    public static boolean hasNewDynamicMessage(Context context)
    {
        return Preference.instance(context).getBoolean(Preference.MESSAGE_DYNAMIC_NOTICE, false);
    }
}
