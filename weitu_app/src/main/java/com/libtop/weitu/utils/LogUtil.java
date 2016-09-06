package com.libtop.weitu.utils;

import android.util.Log;

import com.libtop.weitu.BuildConfig;


/**
 * 日志工具类
 */
public class LogUtil
{
    private static final boolean LOG_DEBUG = BuildConfig.LOG_DEBUG;


    /**
     * send a INFO log message
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void i(String tag, String msg)
    {
        if (LOG_DEBUG)
        {
            Log.i(tag, msg);
        }
    }


    /**
     * send a DEBUG log message
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void d(String tag, String msg)
    {
        if (LOG_DEBUG)
        {
            Log.d(tag, msg);
        }
    }


    /**
     * send a ERROR log message
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void e(String tag, String msg)
    {
        if (LOG_DEBUG)
        {
            Log.e(tag, msg);
        }
    }


    /**
     * send a VERBOSE log message
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void v(String tag, String msg)
    {
        if (LOG_DEBUG)
        {
            Log.v(tag, msg);
        }
    }


    /**
     * send a WARN log message
     *
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void w(String tag, String msg)
    {
        if (LOG_DEBUG)
        {
            Log.w(tag, msg);
        }
    }


    /**
     * send a INFO log message
     *
     * @param _class Used to identify the source of a log message.
     * @param msg    The message you would like logged.
     */
    public static void i(Object _class, String msg)
    {
        if (LOG_DEBUG)
        {
            Log.i(_class.getClass().getSimpleName(), msg);
        }
    }


    /**
     * send a DEBUG log message
     *
     * @param _class Used to identify the source of a log message.
     * @param msg    The message you would like logged.
     */
    public static void d(Object _class, String msg)
    {
        if (LOG_DEBUG)
        {
            Log.d(_class.getClass().getSimpleName(), msg);
        }
    }


    /**
     * send a ERROR log message
     *
     * @param _class Used to identify the source of a log message.
     * @param msg    The message you would like logged.
     */
    public static void e(Object _class, String msg)
    {
        if (LOG_DEBUG)
        {
            Log.e(_class.getClass().getSimpleName(), msg);
        }
    }


    /**
     * send a VERBOSE log message
     *
     * @param _class Used to identify the source of a log message.
     * @param msg    The message you would like logged.
     */
    public static void v(Object _class, String msg)
    {
        if (LOG_DEBUG)
        {
            Log.v(_class.getClass().getSimpleName(), msg);
        }
    }


    /**
     * send a WARN log message
     *
     * @param _class Used to identify the source of a log message.
     * @param msg    The message you would like logged.
     */
    public static void w(Object _class, String msg)
    {
        if (LOG_DEBUG)
        {
            Log.w(_class.getClass().getSimpleName(), msg);
        }
    }
}
