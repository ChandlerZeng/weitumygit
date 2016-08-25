package com.libtop.weitu.utils;

import java.io.File;

import android.content.Context;
import android.os.Environment;

public class SdCardUtil {

	public static String Sd = Environment.getExternalStorageDirectory()
			.getPath();
	public static String catch_path = ""; // 应用的cache目录用于存放缓存
	public static String PROJECT_FILE_PATH = Environment
			.getExternalStorageDirectory().getPath() + "/ctibet/plan/"; // 项目路径
	public static String DCIM = Sd + "/DCIM/";

	public static String TEMP = "file:///" + PROJECT_FILE_PATH + "camera.jpg";
	public static String DOWNLOAD_PATH = PROJECT_FILE_PATH + "download/";
	public static String LOADPIC_PATH = PROJECT_FILE_PATH + "replyPic/";

	/**
	 * 判断是否有sd�?
	 */
	public static int checkSdState() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return 0;
		}
		return 1;
	}

	/**
	 * 初始化文件目�?
	 */
	public static void initFileDir(Context context) {
		File projectDir = new File(PROJECT_FILE_PATH);
		if (!projectDir.exists()) {
			projectDir.mkdirs();
		}

		File downloadDir = new File(DOWNLOAD_PATH);
		if (!downloadDir.exists()) {
			downloadDir.mkdirs();
		}
		
		File loadPicDir = new File(LOADPIC_PATH);
		if (!loadPicDir.exists()) {
			loadPicDir.mkdirs();
		}
		catch_path = Environment.getExternalStorageDirectory().getPath()
				+ "/Android/data/" + context.getPackageName() + "/cache/";
	}

	/**
	 * 获取sd卡路�?
	 * 
	 * @return
	 */
	public static String getSDPath() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			return Sd;
		}
		return "";
	}

	public static String getPhotoUrl(String fileName) {
		return PROJECT_FILE_PATH + fileName;
	}
}
