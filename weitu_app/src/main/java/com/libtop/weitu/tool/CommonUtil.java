package com.libtop.weitu.tool;

import java.util.HashMap;
import java.util.Map;

public class CommonUtil {

	private static Map<String, String> commons = new HashMap<String, String>();

	static {
		// key-value对应输入
		commons.put("sex0", "男");
		commons.put("sex1", "女");

		// 反向value-key对应输入
		commons.put("order", "我的预约");
		commons.put("gather", "我的收藏");
		commons.put("past", "我看过");
		commons.put("wish", "我想看");
	}

	public static String getValue(String key) {
		String value = commons.get(key);
		return value;
	}

}
