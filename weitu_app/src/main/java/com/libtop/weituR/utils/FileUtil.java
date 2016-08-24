package com.libtop.weituR.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.widget.Toast;

public class FileUtil {


	/**
	 * 调用本机文件管理器，在onActivityResult 进行结果操作
	 */
	public static void startFileActivityForResult(Activity activity){
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		try {
			activity.startActivityForResult(intent, 1);
		} catch (android.content.ActivityNotFoundException ex) {
			// Potentially direct the user to the Market with a Dialog
			Toast.makeText(activity, "请安装文件管理器", Toast.LENGTH_SHORT)
					.show();
		}
	}

	/**
	 * 读取文件为byte[]
	 * 
	 * @param filePath
	 * @return
	 */
	public static byte[] readFile(String filePath) {
		final File file = new File(filePath);
		if (!file.exists()) {
			return null;
		}
		try {
			InputStream stream = new FileInputStream(file);
			byte[] buffer = new byte[(int) file.length()];
			stream.read(buffer);
			stream.close();
			return buffer;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 保存文件
	 * 
	 * @param filePath
	 * @param data
	 */
	public static void saveFile(String filePath, byte[] data) {
		File targetFile = new File(filePath);
		FileOutputStream osw;
		try {
			if (!targetFile.exists()) {
				targetFile.createNewFile();
				osw = new FileOutputStream(targetFile);
				osw.write(data);
				osw.close();
			} else {
				osw = new FileOutputStream(targetFile, true);
				osw.write(data);
				osw.flush();
				osw.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 文件拷贝
	 * 
	 * @param prefile
	 * @param newfile
	 */
	public static int copyFile(String prefile, String newfile) {
		try {
			InputStream fosfrom = new FileInputStream(prefile);
			OutputStream fosto = new FileOutputStream(newfile);
			byte bt[] = new byte[1024];
			int c;
			while ((c = fosfrom.read(bt)) > 0) {
				fosto.write(bt, 0, c);
			}
			fosfrom.close();
			fosto.close();
			return 0;

		} catch (Exception ex) {
			return -1;
		}
	}

	/**
	 * asset文件夹读取文�?
	 * 
	 * @param context
	 * @param path
	 * @return
	 */
	public static String readTxtFromAsset(Context context, String path) {
		Resources resource = context.getResources();
		AssetManager am = resource.getAssets();
		InputStream is = null;
		String content = "";
		try {
			is = am.open(path);
			int length = is.available();
			byte[] buffer = new byte[length];
			is.read(buffer);
			content = EncodingUtils.getString(buffer, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return content;
	}

	/**
	 * 从sd卡读文件
	 * 
	 * @param path
	 * @return
	 */
	public static String readTxtFromSd(String path) {
		String content = "";
		InputStream stream = null;
		final File file = new File(path);
		if (!file.exists()) {
			return null;
		}
		try {
			stream = new FileInputStream(file);
			byte[] buffer = new byte[(int) file.length()];
			stream.read(buffer);
			content = EncodingUtils.getString(buffer, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (stream != null) {
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return content;
	}

    /**
     * 删除单个文件
     * @param   sPath    被删除文件的文件�?
     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }
    /**
     * 删除目录（文件夹）以及目录下的文�?
     * @param   sPath 被删除目录的文件路径
     * @return  目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String sPath) {
        //如果sPath不以文件分隔符结尾，自动添加文件分隔�?
        if (!sPath.endsWith(File.separator)) {
            sPath = sPath + File.separator;
        }
        File dirFile = new File(sPath);
        //如果dir对应的文件不存在，或者不是一个目录，则�?�?
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        boolean flag = true;
        //删除文件夹下的所有文�?包括子目�?
        File[] files = dirFile.listFiles();
        for (int i = 0; i < files.length; i++) {
            //删除子文�?
            if (files[i].isFile()) {
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } //删除子目�?
            else {
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前目录
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }

}
