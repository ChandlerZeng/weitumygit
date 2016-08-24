package com.libtop.weituR.tool;

import android.content.Context;
import android.content.SharedPreferences;

public class Preference {

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
	public static final String KEYWORD_SEARCH="search_keyword";
	public static final String KEYWORD_CATECODE="catecode_keyword";

	public static Preference instance(Context context) {
		if (catche == null) {
			catche = new Preference(context);
		}
		return catche;
	}

	public Preference(Context context) {
		spf = context.getApplicationContext().getSharedPreferences(SHARED_PREFERENCE_NAME,
				Context.MODE_PRIVATE);
	}

	public void putBoolean(String key, boolean value) {
		spf.edit().putBoolean(key, value).commit();
	}

	public boolean getBoolean(String key) {
		return spf.getBoolean(key, false);
	}

	public void putString(String key, String value) {
		spf.edit().putString(key, value).commit();
	}

	public String getString(String key) {
		return spf.getString(key, "");
	}

	public void putInt(String key, int value) {
		spf.edit().putInt(key, value).commit();
	}

	public void putLong(String key, long value) {
		spf.edit().putLong(key, value).commit();
	}

	public int getInt(String key) {
		return spf.getInt(key, 0);
	}

	public int getInt(String key, int defaultValue) {
		return spf.getInt(key, defaultValue);
	}

	public long getLong(String key) {
		return spf.getLong(key, 0);
	}

	public long getLong(String key, long def) {
		return spf.getLong(key, def);
	}

	public void clearData() {
		spf.edit().clear().commit();
	}

	public void remove(String key) {
		spf.edit().remove(key).commit();
	}

	public void commit() {
		spf.edit().commit();
	}

}
