package com.libtop.weitu;

import android.os.Handler;
import android.os.Message;

import com.libtop.weituR.activity.main.upload.UploadService;

import java.io.File;

import io.vov.vitamio.utils.Log;

/**
 * <p>
 * Title: UploadThread.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * CreateTime：16/5/24
 * </p>
 *
 * @author 作者名
 * @version common v1.0
 */
public class UploadThread extends Thread {
    private String uploadUrl;
    private int uploadPost;
    String url;
    String uid;
    String fid;
    UploadService uploadService;
    private Handler handler;

    public UploadThread(uploadMessage um, Handler handler) {
        super();
        this.uploadUrl = um.getUploadUrl();
        this.uploadPost = um.getUploadPost();
        this.url = um.getFileurl();
        this.uid = um.getUid();
        this.fid = um.getFid();
        this.handler = handler;
        uploadService = new UploadService(uploadUrl, uploadPost, handler, 3);
    }

    public UploadThread(String uploadUrl, int uploadPost, String url, String uid, String fid, Command command) {
        super();
        this.uploadUrl = uploadUrl;
        this.uploadPost = uploadPost;
        this.url = url;
        this.uid = uid;
        this.fid = fid;

        uploadService = new UploadService(uploadUrl, uploadPost, null, 3);
    }

    @Override
    public void run() {
        super.run();
        try {
            File file = new File(url);
            uploadService.upload(uid, fid, file);
        } catch (Exception e) {
            Log.e("UploadThread", e.toString());
        }
    }

    public int getProgess() {
        return uploadService.countY;
    }

    public void setData(String str) {
        Message msg = handler.obtainMessage();
        msg.what = 1;
        handler.sendMessage(msg);
    }

    public void stopUpload() {
        uploadService.stopSocket();
    }
}
