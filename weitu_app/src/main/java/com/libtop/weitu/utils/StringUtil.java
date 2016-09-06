package com.libtop.weitu.utils;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


/**
 * 类型转换工具�?
 *
 * @author longbh
 */
public class StringUtil
{

    /**
     * �?��是否为空
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(String str)
    {
        return (str == null || str.length() == 0);
    }


    /**
     * �?��是否是数�?
     *
     * @param chr
     * @return
     */
    public static boolean isNumber(char chr)
    {
        if (chr < 48 || chr > 57)
        {
            return false;
        }
        return false;
    }


    public static boolean isNumeric(String str)
    {
        for (int i = str.length(); --i >= 0; )
        {
            int chr = str.charAt(i);
            if (chr < 48 || chr > 57)
            {
                return false;
            }
        }
        return true;
    }


    public static String toString(Object obj)
    {
        if (obj == null)
        {
            return "";
        }
        else
        {
            return obj.toString();
        }
    }


    public static int toInt(Object obj)
    {
        if (obj == null || "".equals(obj) || "null".equals(obj))
        {
            return -1;
        }
        else
        {
            return Integer.parseInt(obj.toString());
        }
    }


    public static short toShort(Object obj)
    {
        if (obj == null || "".equals(obj) || "null".equals(obj))
        {
            return -1;
        }
        else
        {
            return Short.parseShort(obj.toString());
        }
    }


    public static int toCount(Object obj)
    {
        if (obj == null || "".equals(obj) || "null".equals(obj))
        {
            return 0;
        }
        else
        {
            return Integer.parseInt(obj.toString());
        }
    }


    public static float toFloat(Object obj)
    {
        if (obj == null || "".equals(obj))
        {
            return 0;
        }
        else
        {
            return Float.parseFloat(obj.toString());
        }
    }


    public static double toDouble(Object obj)
    {
        if (obj == null || "".equals(obj))
        {
            return 0;
        }
        else
        {
            return Double.parseDouble(obj.toString());
        }
    }


    /**
     * 将对象转换成Long�?空对象默认装换成0)
     *
     * @param obj
     * @return
     */
    public static Long toLong(Object obj)
    {
        if (obj == null || "".equals(obj))
        {
            return 0L;
        }
        else
        {
            return Long.parseLong(obj.toString());
        }
    }


    /**
     * 将对象转换成boolean类型,默认为false
     *
     * @param obj
     * @return
     */
    public static Boolean toBoolean(Object obj)
    {
        if (obj == null || "".equals(obj))
        {
            return false;
        }
        return Boolean.valueOf(obj.toString());
    }


    public static String formatDateTime(String dTime)
    {
        String dateTime = "";
        if (dTime != null && !"".equals(dTime) && !dTime.startsWith("1900-01-01"))
        {
            Timestamp t = Timestamp.valueOf(dTime);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            dateTime = formatter.format(t);
        }
        return dateTime;
    }


    public static String reFormatDateTime(String dTime)
    {
        String dateTime = "";
        if (dTime != null && !"".equals(dTime) && !dTime.startsWith("1900-01-01"))
        {
            Timestamp t = Timestamp.valueOf(dTime);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            dateTime = formatter.format(t);
        }
        return dateTime;
    }


    // 得到游标时间
    public static Timestamp getTimestamp(String tDate)
    {
        if (tDate != null)
        {
            Timestamp ts = Timestamp.valueOf(tDate + " 00:00:00");
            return ts;
        }
        return null;
    }


    public static String getTimeInMillis(Date sDate, Date eDate)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sDate);
        long timethis = calendar.getTimeInMillis();

        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(eDate);
        long timeend = calendar2.getTimeInMillis();
        long thedaymillis = timeend - timethis;
        return thedaymillis < 1000 ? thedaymillis + "毫秒" : (thedaymillis / 1000) + "";
    }


    public static boolean checkStr(String str)
    {
        boolean bool = true;
        if (str == null || "".equals(str.trim()))
        {
            bool = false;
        }
        return bool;
    }


    public static String buildFirstChar(String str)
    {
        if (str == null)
        {
            return null;
        }
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }


    public static double double2point(double ff)
    {
        int j = (int) Math.round(ff * 10000);
        double k = (double) j / 100.00;

        return k;
    }


    public String snumberFormat(double unm)
    {
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(unm);
    }


    public static String delSpaceString(String d)
    {
        String ret = "";
        if (d != null)
        {
            ret = d.trim();
        }
        return ret;
    }


    /**
     * 数据定长输出
     *
     * @param pattern 长度及其格式（例如：定长�?0位，不足则前面补零，那么pattern�?0000000000"�?
     * @param number
     * @return
     */
    public static String getDecimalFormat(String pattern, String number)
    {
        DecimalFormat myformat = new DecimalFormat();
        myformat.applyPattern(pattern);
        int num = Integer.parseInt(toInt(number) + "");
        // �?��长度
        if ((num + "").length() > pattern.length())
        {
            String newNumber = (num + "").substring(0, pattern.length() - 1);
            num = Integer.parseInt(newNumber);
        }
        return myformat.format(num);
    }


    public static String formatDecimal(String pattern, int number)
    {
        DecimalFormat myformat = new DecimalFormat();
        myformat.applyPattern(pattern);
        if ((number + "").length() > pattern.length())
        {
            String newNumber = (number + "").substring(0, pattern.length() - 1);
            number = Integer.parseInt(newNumber);
        }
        return myformat.format(number);
    }


    public static String bytesToHexString(byte[] src, int length)
    {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || length <= 0)
        {
            return null;
        }
        for (int i = 0; i < length; i++)
        {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2)
            {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }


    public static float max(float... values)
    {
        float max = 0;
        for (float item : values)
        {
            if (max == 0)
            {
                max = item;
            }
            else
            {
                max = Math.max(max, item);
            }
        }
        return max;
    }


    public static float min(float... values)
    {
        float min = 0;
        for (float item : values)
        {
            if (min == 0)
            {
                min = item;
            }
            else
            {
                if (item == 0)
                {
                    continue;
                }
                min = Math.min(min, item);
            }
        }
        return min;
    }


    public static int level(float value, float level[])
    {
        for (int i = 0; i < level.length; i++)
        {
            if (value < level[i])
            {
                return i;
            }
        }
        return level.length;
    }


    public static void main(String[] args)
    {

        for (int i = 0; i < 1000; i++)
        {

        }

    }


    private enum FileType
    {
        mp3,
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY,
        NOVALUE;


        public static FileType toFileType(String str)
        {
            try
            {
                return valueOf(str);
            }
            catch (Exception ex)
            {
                return NOVALUE;
            }
        }
    }


    //-1为没有该字段
    public static int getUpLoadType(String type)
    {
        HashMap map = new HashMap();
        map.put("mp3", 0);
        map.put("mp4", 1);
        map.put("pdf", 2);
        map.put("swf", 3);
        map.put("doc", 4);
        map.put("xls", 5);
        map.put("ppt", 6);
        map.put("docx", 7);
        map.put("xlsx", 8);
        map.put("pptx", 9);
        map.put("odt", 10);
        map.put("ods", 11);
        map.put("jpg", 12);
        map.put("wav", 13);
        map.put("flac", 14);
        map.put("avi", 15);
        map.put("dat", 16);
        map.put("flv", 17);
        map.put("rmvb", 18);
        map.put("wmv", 19);
        map.put("asf", 20);
        map.put("mpg", 21);
        map.put("f4v", 22);
        map.put("rm", 23);
        map.put("m2t", 24);
        map.put("vob", 25);
        map.put("mkv", 26);
        map.put("mov", 27);
        int filetype = -1;
        try
        {
            filetype = (Integer) map.get(type.toLowerCase());
        }
        catch (NullPointerException e)
        {
        }
        return filetype;
    }

}
