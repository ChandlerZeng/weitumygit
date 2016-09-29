package com.libtop.weitu.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;



/**
 * @author Sai
 * @ClassName: WTBroadcastReceiver
 * @Description: 广播接收器封装类
 * @date 9/29/16 11:37
 */
public class WTBroadcastReceiver extends BroadcastReceiver
{
    private Handler handler;

    public WTBroadcastReceiver(Handler handler)
    {
        this.handler = handler;
    }


    @Override
    public void onReceive(Context context, Intent intent)
    {
        Message msg = handler.obtainMessage();
        msg.obj = intent;
        handler.sendMessage(msg);
    }
}
