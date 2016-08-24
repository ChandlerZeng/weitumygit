package com.libtop.weituR.utils.viewpagerbitmap;

import java.util.ArrayList;
import java.util.List;

public class ImageBean {
	public static String url = "http://192.168.7.106:8080/st_review";
	public static StringBuffer bean[] = {};

	public static String[] fuckyou() {
		StringBuffer file_dowload_url1;
		String[] list = new String[30];
		for (int i = 0; i < 30; i++) {
			file_dowload_url1 = new StringBuffer(url + "/pdf/");
			file_dowload_url1.append("jc");
			file_dowload_url1.append("/");
			file_dowload_url1.append("126287872");
			file_dowload_url1.append("/");
			file_dowload_url1.append("2014-12-26");
			file_dowload_url1.append("/");
			file_dowload_url1.append("sbs");
			file_dowload_url1.append("/");
			file_dowload_url1.append("" + i);
			file_dowload_url1.append(".");
			file_dowload_url1.append("jpg");

			list[i] = file_dowload_url1.toString();
		}
		return list;
	}

}
