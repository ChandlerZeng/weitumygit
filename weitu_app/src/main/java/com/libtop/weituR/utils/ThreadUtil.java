package com.libtop.weituR.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Handler;

public class ThreadUtil {
	
	public static ExecutorService executorService = Executors
			.newFixedThreadPool(5);
	public static Handler handler = new Handler();

	/**
	 * 异步调用
	 * @param call
	 */
	public static void thread(final ThreadCall call){
		executorService.submit(new Runnable() {			
			@Override
			public void run() {
				call.run();
				handler.post(new Runnable() {					
					@Override
					public void run() {
						call.callBack();
					}
				});
			}
		});
	}
	
	public interface ThreadCall{
		void run();
		void callBack();
	}
}
