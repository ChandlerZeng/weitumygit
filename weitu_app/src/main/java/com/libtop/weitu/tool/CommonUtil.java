package com.libtop.weitu.tool;

import java.util.HashMap;
import java.util.Map;

public class CommonUtil {

	private static Map<String, String> commons = new HashMap<String, String>();

	static {
		commons.put("sex0", "男");
		commons.put("sex1", "女");
	}

	public static String getValue(String key) {
		String value = commons.get(key);
		return value;
	}

}
