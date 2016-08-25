package com.libtop.weitu;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.widget.Toast;

import com.libtop.weitu.activity.main.upload.DBHelper;
import com.libtop.weitu.activity.main.upload.UploadService;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.vov.vitamio.utils.Log;

/**
 * <p>
 * Title: UploadService.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * CreateTime：16/5/24
 * </p>
 *
 * @author 陆
 * @version common v1.0
 */
public class UploadServices extends Service {
    private MsgBinder msgBinder = null;
    private Map<String, uploadMessage> linkedHashMap = new LinkedHashMap<String, uploadMessage>();
    Thread thread = new Thread();
    UploadService uploadService;
    private String uploadUrl;


    private int uploadPost;
    private ThreadList threadList = new ThreadList();
    /**
     * 记录ID数据库
     */
    private DBHelper dp = new DBHelper(this);

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        msgBinder = new MsgBinder();
    }

    public IBinder onBind(Intent intent) {

        uploadMessage um = (uploadMessage) intent.getParcelableExtra("bean");
        if (um != null) {
            String key = um.getFid();
            threadList.summit(key, um, updataHandler);
            Log.e("收到了", "收到了");
        }
        return msgBinder;
    }


    public class MsgBinder extends IMyAidlInterface.Stub {

        /**
         * 获取进度
         *
         * @return
         * @throws RemoteException
         */
        @Override
        public List<uploadMessage> getMes() throws RemoteException {
            return threadList.getData();
        }

        /**
         * 接收信息
         *
         * @param um
         * @throws RemoteException
         */
        @Override
        public void setBean(uploadMessage um) throws RemoteException {
            if (um != null) {
                String key = um.getFid();
                threadList.summit(key, um, updataHandler);
            }
        }

        @Override
        public List<String> stop() throws RemoteException {
            return threadList.getSu();
        }


    }

    public void startUpload(final String key) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                uploadService = new UploadService(uploadUrl, uploadPost, UploadServices.this, null, null, 3);
                try {
                    String url = linkedHashMap.get(key).getFileurl();
                    String uid = linkedHashMap.get(key).getUid();
                    String fid = linkedHashMap.get(key).getFid();
                    File file = new File(url);
                    uploadService.upload(uid, fid, file);
                } catch (Exception e) {

                }
            }

        };

        thread = new Thread(runnable);
        thread.start();

    }

//    @Override
//    public void message(String str, String id) {
//        threadList.removeThread(id);
//        dp.Droptablename(id);
//        Log.e("完成" + id, id);
//    }

    private Handler updataHandler = new Handler() {
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    int progress = msg.arg1;
                    break;
                case 2:
                    break;
                case 4:

                    String id = msg.getData().getString("key");
                    threadList.removeThread(id);
                    dp.Droptablename(id);
                    Log.e("完成" + id, "");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public boolean onUnbind(Intent intent) {
        threadList.reSet();
        return super.onUnbind(intent);
    }
}
