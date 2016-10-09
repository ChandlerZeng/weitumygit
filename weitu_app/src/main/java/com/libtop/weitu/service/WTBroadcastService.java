package com.libtop.weitu.service;

import android.content.Context;
import android.content.Intent;


/**
 * @author Sai
 * @ClassName: WTBroadcastService
 * @Description: 广播服务帮助类
 * @date 9/29/16 10:36
 */
public class WTBroadcastService
{
    public static final String EXTRA_DYNAMIC_ID = "dynamic_id";

    /**
     * 有新动态消息
     */
    public static final String ACTION_NEW_DYNAMIC_NOTICE = ".ACTION_NEW_DYNAMIC_NOTICE";


    // 有新的动态消息
    public static void sendNewDynamicNoticeBroadcast(Context context, String id)
    {
        Intent intent = new Intent(ACTION_NEW_DYNAMIC_NOTICE);
        intent.putExtra(EXTRA_DYNAMIC_ID, id);
        context.sendBroadcast(intent);
    }
}
