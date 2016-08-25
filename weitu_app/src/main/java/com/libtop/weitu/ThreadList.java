package com.libtop.weitu;

import android.os.Handler;

import com.libtop.weitu.utils.selector.bean.ListGridImage;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.vov.vitamio.utils.Log;

/**
 * <p>
 * Title: 自定义多线程管理
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
public class ThreadList {
    private Map<String, UploadThread> linkedHashMap = null;
    private List<String> su = null;

    public ThreadList() {
        linkedHashMap = new LinkedHashMap<String, UploadThread>();
        su = new ArrayList<String>();
    }

    public void summit(String key, uploadMessage um, Handler handler) {
        if (!isExist(key)) {
            UploadThread thread = new UploadThread(um, handler);
            linkedHashMap.put(key, thread);
            thread.start();
            thread.setData("状态:上传中");
            Log.e("状态", "上传中");
        } else
            stopThread(key, um, handler);
    }

    /**
     * 获取进度
     *
     * @param key
     * @return
     */
    public int getProgress(String key) {
        if (!isExist(key)) {
            return getThread(key).getProgess();
        }
        return 0;
    }

    /**
     * 判断线程存在
     *
     * @param key
     * @return
     */
    private boolean isExist(String key) {
        return linkedHashMap.containsKey(key);
    }

    /**
     * 复位线程
     *
     * @param key
     */
    private void stopThread(String key, uploadMessage um, Handler handler) {
        if (getThread(key).isAlive()) {
            getThread(key).stopUpload();
            getThread(key).setData("状态:暂停");
        } else {
            linkedHashMap.remove(key);
            UploadThread thread = new UploadThread(um, handler);
            linkedHashMap.put(key, thread);
            thread.start();
            thread.setData("状态:上传中");
        }
    }


    public List<String> getSu() {
        return su;
    }

    public void reSet() {
        su.clear();
    }

    public void removeThread(String key) {
        if (isExist(key)) {
            linkedHashMap.remove(key);
            su.add(key);
        }
    }

    /**
     * 获取线程
     *
     * @param key
     * @return
     */
    private UploadThread getThread(String key) {
        return linkedHashMap.get(key);
    }

    public List<uploadMessage> getData() {
        List<uploadMessage> list = new ArrayList<uploadMessage>();
        for (Map.Entry<String, UploadThread> entry : linkedHashMap.entrySet()) {
            uploadMessage a = new uploadMessage();
            a.setFid(entry.getKey());
            a.setProgress(entry.getValue().getProgess());
            list.add(a);
        }
        return list;
    }

}
