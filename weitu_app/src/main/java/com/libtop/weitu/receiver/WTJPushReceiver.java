package com.libtop.weitu.receiver;

/**
 * Created by LianTu on 2016/4/6.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.libtop.weitu.activity.main.MainActivity;
import com.libtop.weitu.service.WTBroadcastService;
import com.libtop.weitu.utils.Preference;
import com.libtop.weitu.utils.LogUtil;
import com.libtop.weitu.utils.MessageRemindUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;



/**
 *
 */
public class WTJPushReceiver extends BroadcastReceiver
{
    private static final String TAG = "JPush";

    /**
     * 推送消息类型: 关注主题 1
     */
    private static final int PUSH_TYPE_FOLLOW_SUBJECT  = 1;
    /**
     * 推送消息类型: 评论 2
     */
    private static final int PUSH_TYPE_COMMENT = 2;
    /**
     * 推送消息类型: 回复 3
     */
    private static final int PUSH_TYPE_REPLY = 3;

    /**
     * 推送消息附加字段名: 消息类型 push_type
     */
    private static final String EXTRA_KEY_PUSH_TYPE = "push_type";
    /**
     * 推送消息附加字段名: 数据记录的ID(一般为动态的ID值)
     */
    private static final String EXTRA_KEY_ID = "id";
    /**
     * 推送消息附加字段名: 接收消息用户的uid
     */
    private static final String EXTRA_KEY_UID = "uid";


    @Override
    public void onReceive(Context context, Intent intent)
    {
        Bundle bundle = intent.getExtras();
        Log.d(TAG, "[WTJPushReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));


        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction()))  //用户注册
        {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[WTJPushReceiver] 接收Registration Id : " + regId);

        }
        else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction()))  //用户接收到推送下来的自定义消息
        {
            Log.d(TAG, "[WTJPushReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            handleCustomMessage(context, bundle);
        }
        else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction()))  //用户接收到推送下来的通知
        {
            Log.d(TAG, "[WTJPushReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[WTJPushReceiver] 接收到推送下来的通知的ID: " + notifactionId);

        }
        else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction()))  //用户打开自定义通知栏的intent
        {
            Log.d(TAG, "[WTJPushReceiver] 用户点击打开了通知");

            Intent i = new Intent(context, MainActivity.class);
            i.putExtras(bundle);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);
        }
        else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction()))  //用户接收富媒体推送回调函数的intent
        {
            Log.d(TAG, "[WTJPushReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
        }
        else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction()))  //用户发生改变
        {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Log.w(TAG, "[WTJPushReceiver]" + intent.getAction() + " connected state change to " + connected);
        }
        else
        {
            Log.d(TAG, "[WTJPushReceiver] Unhandled intent - " + intent.getAction());
        }
    }


    private void handleCustomMessage(Context context, Bundle bundle)
    {
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        int pushType;
        String id;
        String uid;

        try
        {
            JSONObject jsonObject = new JSONObject(extras);
            pushType = jsonObject.optInt(EXTRA_KEY_PUSH_TYPE, -1);
            id = jsonObject.optString(EXTRA_KEY_ID, "");
            uid = jsonObject.optString(EXTRA_KEY_UID, "");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            LogUtil.e(TAG, e.toString());

            return;
        }

        if (checkCustomMessage(context, pushType, id, uid))
        {
            MessageRemindUtil.addNewDynamicRemind(context);
            WTBroadcastService.sendNewDynamicNoticeBroadcast(context, id);
        }
        else
        {
            LogUtil.d(TAG, "接收到未知的推送消息, 或者当前登录用户不是消息接收者");
        }
    }


    private boolean checkCustomMessage(Context context, int pushType, String id, String uid)
    {
        if (pushType == PUSH_TYPE_FOLLOW_SUBJECT || pushType == PUSH_TYPE_COMMENT || pushType == PUSH_TYPE_REPLY)
        {
            String loginUid = Preference.instance(context).getString(Preference.uid);
            if (!TextUtils.isEmpty(id) && !TextUtils.isEmpty(loginUid) && loginUid.equals(uid))  //若当前登录则为消息的接收者
            {
                return true;
            }
        }

        return false;
    }


    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle)
    {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet())
        {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID))
            {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            }
            else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE))
            {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            }
            else if (key.equals(JPushInterface.EXTRA_EXTRA))
            {
                if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty())
                {
                    Log.i(TAG, "This message has no Extra data");
                    continue;
                }

                try
                {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext())
                    {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                }
                catch (JSONException e)
                {
                    Log.e(TAG, "Get message extra JSON error!");
                }
            }
            else
            {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }
}
