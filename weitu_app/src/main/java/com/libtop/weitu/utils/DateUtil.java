package com.libtop.weitu.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * 时间格式工具类
 */
public class DateUtil
{

    public static Date parseToDate(String s, String style)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        simpleDateFormat.applyPattern(style);
        Date date = null;
        if (s == null || s.length() < 5)
        {
            return null;
        }
        try
        {
            date = simpleDateFormat.parse(s);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return date;
    }


    public static String parseToString(long curentTime)
    {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(curentTime);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = formatter.format(now.getTime());
        return str;
    }

    public static String parseToStringWithoutSS(long curentTime)
    {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(curentTime);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String str = formatter.format(now.getTime());
        return str;
    }


    public static String parseToHHString(long curentTime)
    {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(curentTime);
        SimpleDateFormat formatter = new SimpleDateFormat("HH");
        String str = formatter.format(now.getTime());
        return str;
    }


    public static String parseTommString(long curentTime)
    {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(curentTime);
        SimpleDateFormat formatter = new SimpleDateFormat("mm");
        String str = formatter.format(now.getTime());
        return str;
    }


    public static String parseToDate(long time)
    {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(time);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String str = formatter.format(now.getTime());
        return str;
    }


    public static String parseToMD(long time)
    {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(time);
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd");
        String str = formatter.format(now.getTime());
        return str;
    }


    public static String getSendTimeDistance(long sendTime)
    {
        String timeDistance = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try
        {
            Date d2 = df.parse(parseToString(sendTime));
            Date d1 = df.parse(parseToString(System.currentTimeMillis()));
            long diff = d1.getTime() - d2.getTime();// 这样得到的差值是微秒级别

            // 判断时间是否大于一天
            if (diff < (24 * 60 * 60 * 1000))
            {
                // 判断时间是否大于一小时
                if (diff > (60 * 60 * 1000))
                {
                    timeDistance = DateUtil.parseToHHString(diff) + "小时前";
                }
                else
                {
                    timeDistance = DateUtil.parseTommString(diff) + "分钟前";
                }
            }
            else
            {
                timeDistance = DateUtil.parseToMD(sendTime);
            }
        }
        catch (Exception e)
        {
            Log.e("", "计算时间错误");
        }
        return timeDistance;
    }
}