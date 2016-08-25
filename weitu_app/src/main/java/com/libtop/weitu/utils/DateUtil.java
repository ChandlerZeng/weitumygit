package com.libtop.weitu.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import android.util.Log;

/**
 * 时间格式�?
 */
public class DateUtil {

	public static final String yyMMdd = "yy-MM-dd";
	public static final String yyyyMMdd = "yyyy-MM-dd";
	public static final String HHmmss = "HH:mm:ss";
	public static final String HHmm = "HH:mm";
	public static final String yyMMddHHmmss = "yy-MM-dd HH:mm:ss";
	public static final String yyyyMMddHHmm = "yyyy-MM-dd HH:mm";
	public static final String yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";
	public static final String MMddHHmm = "MM-dd hh:mm aa";

	public static final String MMdd = "MM-dd";
	public static final String yyyyMM = "yyyy-MM";

	public static final String yyyymm = "yyyy-MM";
	public static final String yyyymmdd = "yyyy-MM-dd";

	public static Date parseToDate(String s, String style) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
		simpleDateFormat.applyPattern(style);
		Date date = null;
		if (s == null || s.length() < 5)
			return null;
		try {
			date = simpleDateFormat.parse(s);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	public static long parseToDateLong(String s, String style) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
		simpleDateFormat.applyPattern(style);
		Date date = null;
		if (s == null || s.length() < 5)
			return 0;
		try {
			date = simpleDateFormat.parse(s);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date.getTime();
	}

	public static Date parseToTimesDate(String sTime) {
		Date date = null;
		if (sTime == null || "".equals(sTime)) {
			return null;
		}
		date = Timestamp.valueOf(sTime);
		return date;
	}

	public static String parseToString(String s, String style) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
		simpleDateFormat.applyPattern(style);
		Date date = null;
		String str = null;
		if (s == null || s.length() < 8)
			return null;
		try {
			date = simpleDateFormat.parse(s);
			str = simpleDateFormat.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return str;
	}

	public static String parseToString(Date date, String style) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
		simpleDateFormat.applyPattern(style);
		String str = null;
		if (date == null)
			return null;
		str = simpleDateFormat.format(date);
		return str;
	}

	public static String getNowTime() {
		Date nowDate = new Date();
		Calendar now = Calendar.getInstance();
		now.setTime(nowDate);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = formatter.format(now.getTime());
		return str;
	}

	public static String parseToString(long curentTime) {
		Calendar now = Calendar.getInstance();
		now.setTimeInMillis(curentTime);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = formatter.format(now.getTime());
		return str;
	}

	public static String parseToString(long curentTime, String style) {
		Calendar now = Calendar.getInstance();
		now.setTimeInMillis(curentTime);
		SimpleDateFormat formatter = new SimpleDateFormat(style);
		String str = formatter.format(now.getTime());
		return str;
	}

	public static String parseToHHString(long curentTime) {
		Calendar now = Calendar.getInstance();
		now.setTimeInMillis(curentTime);
		SimpleDateFormat formatter = new SimpleDateFormat("HH");
		String str = formatter.format(now.getTime());
		return str;
	}

	public static String parseTommString(long curentTime) {
		Calendar now = Calendar.getInstance();
		now.setTimeInMillis(curentTime);
		SimpleDateFormat formatter = new SimpleDateFormat("mm");
		String str = formatter.format(now.getTime());
		return str;
	}

	public static String parseToDate(long time) {
		Calendar now = Calendar.getInstance();
		now.setTimeInMillis(time);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String str = formatter.format(now.getTime());
		return str;
	}
	
	public static String parseToMD(long time) {
		Calendar now = Calendar.getInstance();
		now.setTimeInMillis(time);
		SimpleDateFormat formatter = new SimpleDateFormat("MM-dd");
		String str = formatter.format(now.getTime());
		return str;
	}

	public static String getNowShortTime() {
		Date nowDate = new Date();
		Calendar now = Calendar.getInstance();
		now.setTime(nowDate);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String str = formatter.format(now.getTime());
		return str;
	}

	public static String getNextDate(String ts, int i) {
		Calendar now = Calendar.getInstance();
		Timestamp t = Timestamp.valueOf(ts + " 00:00:00");
		now.setTime(t);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		now.add(Calendar.DAY_OF_MONTH, +(i));
		String dt = formatter.format(now.getTime());
		return dt;
	}

	public static String getNextTime(String ts, int i) {
		Calendar now = Calendar.getInstance();
		Timestamp t = Timestamp.valueOf(ts);
		System.out.println(t + "     " + ts);
		now.setTime(t);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		now.add(Calendar.MINUTE, +(i));
		String dt = formatter.format(now.getTime());
		return dt;
	}

	public static void formatDateTime(Map<String, String> map, String key,
			String style) {
		String dateTime = com.libtop.weitu.utils.StringUtil.toString(map.get(key));
		if (!"".equals(dateTime)) {
			Timestamp tTamp = Timestamp.valueOf(dateTime);
			SimpleDateFormat formatter = new SimpleDateFormat(style);
			String f_dateTime = formatter.format(tTamp);
			map.put(key, f_dateTime);
		}
	}

	public static long formatDateMills(String s, String style) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
		simpleDateFormat.applyPattern(style);
		Date date = null;
		if (s == null || s.length() < 5)
			return 0;
		try {
			date = simpleDateFormat.parse(s);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date.getTime();
	}

	public static String getSendTimeDistance1(long sendTime) {
		String timeDistance = "";
		// 时间差
		long time = System.currentTimeMillis() - sendTime;
		// 判断时间是否大于一天
		if (time < (24 * 60 * 60 * 1000)) {
			// 判断时间是否大于一小时
			if (time > (60 * 60 * 1000)) {
				timeDistance = DateUtil.parseToHHString(time) + "小时前";
			} else {
				timeDistance = DateUtil.parseTommString(time) + "分钟前";
			}
		} else {
			timeDistance = DateUtil.parseToMD(sendTime);
		}
		return timeDistance;
	}

	public static String getSendTimeDistance(long sendTime) {
		String timeDistance = "";
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date d2 = df.parse(parseToString(sendTime));
			Date d1 = df.parse(parseToString(System.currentTimeMillis()));
			long diff = d1.getTime() - d2.getTime();// 这样得到的差值是微秒级别

			// 判断时间是否大于一天
			if (diff < (24 * 60 * 60 * 1000)) {
				// 判断时间是否大于一小时
				if (diff > (60 * 60 * 1000)) {
					timeDistance = DateUtil.parseToHHString(diff) + "小时前";
				} else {
					timeDistance = DateUtil.parseTommString(diff) + "分钟前";
				}
			} else {
				timeDistance = DateUtil.parseToMD(sendTime);
			}
		} catch (Exception e) {
			Log.e("", "计算时间错误");
		}
		return timeDistance;
	}

	public static long getPreTime(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.add(Calendar.DATE, day);
		calendar.add(Calendar.MONTH, month);
		calendar.add(Calendar.YEAR, year);
		return calendar.getTimeInMillis();
	}
	
	public static int parseToInt(long time){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");
		String str = formatter.format(time);
		return StringUtil.toInt(str);
	}

	public static void main(String[] args) {
		System.out.println(getNextDate("2009-05-16", 1));
	}

}