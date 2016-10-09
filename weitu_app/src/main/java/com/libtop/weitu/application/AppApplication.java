package com.libtop.weitu.application;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.iflytek.cloud.SpeechUtility;
import com.libtop.weitu.BuildConfig;
import com.libtop.weitu.activity.main.MainActivity;
import com.libtop.weitu.dao.bean.DaoMaster;
import com.libtop.weitu.dao.bean.DaoSession;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.service.WTPushService;
import com.libtop.weitu.utils.SdCardUtil;
import com.umeng.analytics.MobclickAgent;
import com.zhy.http.okhttp.OkHttpUtils;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;


public class AppApplication extends Application
{
    private static AppApplication mInstance;
    private static Context context;

    private static DaoMaster daoMaster;
    private static DaoSession daoSession;
    private static String DB_NAME = "com.metasoft.library";


    public synchronized static AppApplication getInstance()
    {
        return mInstance;
    }


    public static Context getContext()
    {
        return context;
    }


    @Override
    public void onCreate()
    {
        super.onCreate();

        mInstance = this;
        context = getApplicationContext();

        //捕捉全局异常，取消默认弹窗，重新启动App
        if (!BuildConfig.DEBUG)
        {
            Thread.setDefaultUncaughtExceptionHandler(new MyUnCaughtExceptionHandler());
        }
        initOkhttp();
        HttpRequest.initParams(getApplicationContext());
        //图片初始化
        SdCardUtil.initFileDir(getApplicationContext());
        SpeechUtility.createUtility(getApplicationContext(), "appid=56824e38");

        WTPushService.initPushConfigure(this, BuildConfig.LOG_DEBUG, BuildConfig.LOG_DEBUG, 1);
    }


    //捕捉全局异常，取消默认弹窗，重新启动App
    class MyUnCaughtExceptionHandler implements Thread.UncaughtExceptionHandler
    {

        @Override
        public void uncaughtException(Thread thread, Throwable ex)
        {
            ex.printStackTrace();
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

            MobclickAgent.onKillProcess(getInstance());  // 保存umeng统计数据
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }


    private void initOkhttp()
    {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(logging).build();

        OkHttpUtils.initClient(okHttpClient);
    }


    /**
     * 取得DaoMaster
     *
     * @param context
     * @return
     */
    public static DaoMaster getDaoMaster(Context context)
    {
        if (daoMaster == null)
        {
            DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }


    /**
     * 取得DaoSession
     *
     * @param context
     * @return
     */
    public static DaoSession getDaoSession(Context context)
    {
        if (daoSession == null)
        {
            if (daoMaster == null)
            {
                daoMaster = getDaoMaster(context);
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }
}
