package com.libtop.weitu.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class Preference
{

    // share数据保存
    public static final String NEWPART_DEVICE_CODE = "NEWPART_DEVICE_CODE";
    public final static String FIRST_IN = "FIRST_IN";

    public final static String UPDATE_STATE = "update_state";
    public final static String UPDATE_TIME = "update_time";

    // 用户信息
    public static String SchoolName = "SchoolName";
    public static String SchoolCode = "SchoolCode";
    public static String SchoolId = "SchoolId";
    public static String UserName = "UserName";
    public static String hid = "hid";
    public static String uid = "userId";
    public static String phone = "phone";
    public static String sex = "sex";
    public static String SID = "SID";
    public static String AESKEY = "AESKEY";

    private final String SHARED_PREFERENCE_NAME = "com.metasoft.library";
    private static Preference catche;
    private SharedPreferences spf;

    //搜索关键字
    public static final String KEYWORD_SEARCH = "search_keyword";
    public static final String KEYWORD_CATECODE = "catecode_keyword";

    /**
     * 持久化数据键名, 动态消息提醒
     */
    public static final String MESSAGE_DYNAMIC_NOTICE = "message_dynamic_notice";


    public static Preference instance(Context context)
    {
        if (catche == null)
        {
            catche = new Preference(context);
        }
        return catche;
    }


    public Preference(Context context)
    {
        spf = context.getApplicationContext().getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }


    public void putInt(String key, int value)
    {
        spf.edit().putInt(key, value).commit();
    }


    public void putLong(String key, long value)
    {
        spf.edit().putLong(key, value).commit();
    }


    public void putBoolean(String key, boolean value)
    {
        spf.edit().putBoolean(key, value).commit();
    }


    public void putString(String key, String value)
    {
        spf.edit().putString(key, value).commit();
    }


    public int getInt(String key)
    {
        return getInt(key, 0);
    }


    public int getInt(String key, int defValue)
    {
        return spf.getInt(key, defValue);
    }


    public long getLong(String key)
    {
        return getLong(key, 0);
    }


    public long getLong(String key, long def)
    {
        return spf.getLong(key, def);
    }


    public boolean getBoolean(String key)
    {
        return getBoolean(key, false);
    }


    public boolean getBoolean(String key, boolean defValue)
    {
        return spf.getBoolean(key, defValue);
    }


    public String getString(String key)
    {
        return getString(key, "");
    }


    public String getString(String key, String defValue)
    {
        return spf.getString(key, defValue);
    }


    public void clearData()
    {
        spf.edit().clear().commit();
    }


    public void remove(String key)
    {
        spf.edit().remove(key).commit();
    }
}
