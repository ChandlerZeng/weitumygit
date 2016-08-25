package com.libtop.weitu.fileloader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

public class FileLoader {

	private static final int IO_BUFFER_SIZE = 8 * 1024;
	private String foldName = "";

	private static FileLoader fileLoader;

	public static FileLoader getInstance(Context context) {
		if (fileLoader == null) {
			fileLoader = new FileLoader(context);
		}
		return fileLoader;
	}

	public FileLoader(Context context) {
		File file = getDiskCacheDir(context, File.separator);
		if (!file.exists()) {
			file.mkdirs();
		}
		foldName = file.getPath() + File.separator;
	}

	public void loadCallBack(String url, CallBack callBack) {
		if (url == null) {
			return;
		}
		String fileName = hashKeyForDisk(url);
		File temp = new File(foldName + fileName);
		if (temp.exists()) {
			callBack.callBack(temp);
		} else {
			new BitmapWorkerTask(callBack).execute(url,temp.getPath());
		}
	}
	
	public void loadTxtCallBack(){
		
	}

	/**
	 * Get a usable cache directory (external if available, internal otherwise).
	 * 
	 * @param context
	 *            The context to use
	 * @param uniqueName
	 *            A unique directory name to append to the cache dir
	 * @return The cache dir
	 */
	public static File getDiskCacheDir(Context context, String uniqueName) {
		// Check if media is mounted or storage is built-in, if so, try and use
		// external cache dir
		// otherwise use internal cache dir
		final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState()) || !isExternalStorageRemovable() ? getExternalCacheDir(
				context).getPath()
				: context.getCacheDir().getPath();

		return new File(cachePath + File.separator + uniqueName);
	}

	/**
	 * Get the external app cache directory.
	 * 
	 * @param context
	 *            The context to use
	 * @return The external cache dir
	 */
	@TargetApi(8)
	public static File getExternalCacheDir(Context context) {
		if (com.libtop.weitu.fileloader.Utils.hasFroyo()) {
			return context.getExternalCacheDir();
		}

		// Before Froyo we need to construct the external cache dir ourselves
		final String cacheDir = "/Android/data/" + context.getPackageName()
				+ "/cache/";
		return new File(Environment.getExternalStorageDirectory().getPath()
				+ cacheDir);
	}

	/**
	 * A hashing method that changes a string (like a URL) into a hash suitable
	 * for using as a disk filename.
	 */
	public String hashKeyForDisk(String key) {
		String cacheKey;
		try {
			final MessageDigest mDigest = MessageDigest.getInstance("MD5");
			mDigest.update(key.getBytes());
			cacheKey = bytesToHexString(mDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			cacheKey = String.valueOf(key.hashCode());
		}
		return cacheKey;
	}

	private String bytesToHexString(byte[] bytes) {
		// http://stackoverflow.com/questions/332079
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}

	/**
	 * Check if external storage is built-in or removable.
	 *
	 * @return True if external storage is removable (like an SD card), false
	 *         otherwise.
	 */
	@TargetApi(9)
	public static boolean isExternalStorageRemovable() {
		if (Utils.hasGingerbread()) {
			return Environment.isExternalStorageRemovable();
		}
		return true;
	}

	public boolean downloadUrlToStream(String urlString, String path) {
		disableConnectionReuseIfNecessary();
		HttpURLConnection urlConnection = null;
		BufferedOutputStream out = null;
		BufferedInputStream in = null;
		boolean state = false;

		try {
			FileOutputStream fos = new FileOutputStream(path, false);
			out = new BufferedOutputStream(fos, IO_BUFFER_SIZE);

			final URL url = new URL(urlString);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setConnectTimeout(20000);
			urlConnection.setReadTimeout(20000);
			in = new BufferedInputStream(urlConnection.getInputStream(),
					IO_BUFFER_SIZE);
			int b;
			while ((b = in.read()) != -1) {
				out.write(b);
			}
			state = true;
		} catch (final IOException e) {
			Log.e("FileLoader", "Error in downloadFile - " + e);
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
			try {
				if (out != null) {
					out.close();
					Log.e("out", "close");
				}
				if (in != null) {
					in.close();
				}
			} catch (final IOException e) {
			}
		}
		return state;
	}

	public static void disableConnectionReuseIfNecessary() {
		// HTTP connection reuse which was buggy pre-froyo
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
			System.setProperty("http.keepAlive", "false");
		}
	}

	private class BitmapWorkerTask extends AsyncTask<Object, Void, String> {

		private CallBack callBack;

		public BitmapWorkerTask(CallBack callBack) {
			this.callBack = callBack;
		}

		@Override
		protected String doInBackground(Object... data) {
			String temp = (String) data[0];
			String path = String.valueOf(data[1]);
			if(!downloadUrlToStream(temp, path)){
				path="";
			}
			return path;
		}

		@Override
		protected void onPostExecute(String value) {
			File file = new File(value);
			callBack.callBack(file);
		}
	}

	public interface CallBack {
		void callBack(File file);
	}

}
