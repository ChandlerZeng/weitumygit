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
import com.libtop.weitu.utils.SdCardUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class AppApplication extends Application {
	private static AppApplication mInstance;
	private static DaoMaster daoMaster;
	private static DaoSession daoSession;
//	private static SQLiteDatabase sqLiteDatabase;
	private static String DB_NAME = "com.metasoft.library";

//	private ImageOptions mCommenOptions;
//	private ImageOptions mIconOptions;
//
//	private DbManager.DaoConfig mDaoConfig;

	//上下文
	private static Context context;
	public static Context getContext() {    return context;}

	@Override
	public void onCreate() {
		super.onCreate();
		context = getApplicationContext();
		//捕捉全局异常，取消默认弹窗，重新启动App
		if (!BuildConfig.DEBUG)
			Thread.setDefaultUncaughtExceptionHandler(new MyUnCaughtExceptionHandler());
		initOkhttp();
		HttpRequest.initParams(getApplicationContext());
//		x.Ext.init(this);
//		x.Ext.setDebug(true);
		//图片初始化
		SdCardUtil.initFileDir(getApplicationContext());
		initChart();
		SpeechUtility.createUtility(getApplicationContext(), "appid=56824e38");

		//个推初始化
		if (mInstance == null)
			mInstance = this;
	}

	//捕捉全局异常，取消默认弹窗，重新启动App
	class MyUnCaughtExceptionHandler implements Thread.UncaughtExceptionHandler{

		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			ex.printStackTrace();
			// do some work here
			//延时启动Activity
//			Intent mStartActivity = new Intent(context, StartupActivity.class);
//			int mPendingIntentId = 123456;
//			PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
//			AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
//			mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 2000, mPendingIntent);
////
//			android.os.Process.killProcess(android.os.Process.myPid());
//			System.exit(0);
			Intent intent = new Intent(context, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);

			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(1);
		}
	}

	private void initOkhttp(){
		HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
		logging.setLevel(HttpLoggingInterceptor.Level.BODY);
		OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
				//其他配置
				.build();

		OkHttpUtils.initClient(okHttpClient);
	}


	//各个平台的配置，建议放在全局Application或者程序入口
	{
		//微信    wx12342956d1cab4f9,a5ae111de7d9ea137e88a5e02c07c94d
//		PlatformConfig.setWeixin("wxd84d649fcca1cdf6", "767c850b8588a064d17d726be3fdabbd");
//		//豆瓣RENREN平台目前只能在服务器端配置
//		//新浪微博
//		PlatformConfig.setSinaWeibo("1849435917", "6365d1498f2e2da983b6d4b8c9a6c165");
//		PlatformConfig.setQQZone("1105269395", "moeoPdHVW4iqM2gt");
	}

	private void initChart() {
//		EMChat.getInstance().init(getApplicationContext());
//		EMChat.getInstance().setDebugMode(true); // 临时
//		// 获取到EMChatOptions对象
//		EMChatOptions options = EMChatManager.getInstance().getChatOptions();
//		// 默认添加好友时，是不需要验证的，改成需要验证
//		options.setAcceptInvitationAlways(false);
//		// 默认环信是不维护好友关系列表的，如果app依赖环信的好友关系，把这个属性设置为true
//		options.setUseRoster(false);
//        // 设置收到消息是否有新消息通知(声音和震动提示)，默认为true
//        options.setNotifyBySoundAndVibrate(true);
//        // 设置收到消息是否有声音提示，默认为true
//        options.setNoticeBySound(true);
//        // 设置收到消息是否震动 默认为true
//        options.setNoticedByVibrate(true);
//        // 设置语音消息播放是否设置为扬声器播放 默认为true
//        options.setUseSpeaker(true);
        // 设置notification消息点击时，跳转的intent为自定义的intent
//        options.setOnNotificationClickListener(new OnNotificationClickListener() {
//
//            @Override
//            public Intent onNotificationClick(EMMessage message) {
//                Intent intent = new Intent(getApplicationContext(), ChartActivity.class);
//                ChatType chatType = message.getChatType();
//                if (chatType == ChatType.Chat) { // 单聊信息
//                    intent.putExtra("userId", message.getFrom());
//                    intent.putExtra("chatType", ChartActivity.CHATTYPE_SINGLE);
//                } else { // 群聊信息
//                            // message.getTo()为群聊id
//                    intent.putExtra("groupId", message.getTo());
//                    intent.putExtra("chatType", ChartActivity.CHATTYPE_GROUP);
//                }
//                return intent;
//            }
//        });
//        ChartManager.getInstance().initManager(getApplicationContext());
	}

	/**
	 * 取得DaoMaster
	 * 
	 * @param context
	 * @return
	 */
	public static DaoMaster getDaoMaster(Context context) {
		if (daoMaster == null) {
			DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME,
					null);
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
	public static DaoSession getDaoSession(Context context) {
		if (daoSession == null) {
			if (daoMaster == null) {
				daoMaster = getDaoMaster(context);
			}
			daoSession = daoMaster.newSession();
		}
		return daoSession;
	}

//	public static SQLiteDatabase getSQLiteDatabase(Context context) {
//		if (sqLiteDatabase == null) {
//			DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME,
//					null);
//			sqLiteDatabase = helper.getWritableDatabase();
//		}
//		return sqLiteDatabase;
//	}

//	public ImageOptions getCommenOptions(){
//		if (mCommenOptions==null){
//			mCommenOptions=new ImageOptions.Builder()
//					.setLoadingDrawableId(R.drawable.content_bg)
//					.setFailureDrawableId(R.drawable.content_bg)
//					.setImageScaleType(ImageView.ScaleType.CENTER_INSIDE)
//					.setUseMemCache(true)
//					.build();
//		}
//		return mCommenOptions;
//	}
//
//	public ImageOptions getIconOptions(){
//		if (mIconOptions==null){
//			mIconOptions=new ImageOptions.Builder()
//					.setLoadingDrawableId(R.drawable.user_default_icon)
//					.setFailureDrawableId(R.drawable.user_default_icon)
//					.setUseMemCache(true)
//					.setRadius(DisplayUtils.dp2px(this, 35))
//					.build();
//		}
//		return mIconOptions;
//	}
//
//	public DbManager.DaoConfig getDaoConfig(){
//		if (mDaoConfig==null){
//			mDaoConfig=new DbManager.DaoConfig()
//					.setDbName("weitu_db")
//					.setDbVersion(1)
//					.setDbOpenListener(new DbManager.DbOpenListener() {
//						@Override
//						public void onDbOpened(DbManager db) {
//							db.getDatabase().enableWriteAheadLogging();
//						}
//					}).setDbUpgradeListener(new DbManager.DbUpgradeListener() {
//						@Override
//						public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
//
//						}
//					});
//
//		}
//		return mDaoConfig;
//	}
}
