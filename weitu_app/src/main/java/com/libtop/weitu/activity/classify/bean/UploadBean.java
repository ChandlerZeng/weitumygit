package com.libtop.weitu.activity.classify.bean;

/**
 * Created by 陆伟健 on 2016/4/25 0025.
 */
public class UploadBean {
    /**
     * 视频名
     */
    public String one;
    /**
     * 创建时间
     */
    public String two;
    /**
     * 状态
     */
    public String three;
    /**
     * 文件路径
     */
    public String fileUrl;
    /**
     * 文件容量
     */
    public String fileLength;
    /**
     * 视频时长
     */
    public String time;
    /**
     * 上传完成程度百分比
     */
    public int progress;

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getOne() {
        return one;
    }

    public void setOne(String one) {
        this.one = one;
    }

    public String getTwo() {
        return two;
    }

    public void setTwo(String two) {
        this.two = two;
    }

    public String getThree() {
        return three;
    }

    public void setThree(String three) {
        this.three = three;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getFileLength() {
        return fileLength;
    }

    public void setFileLength(String fileLength) {
        this.fileLength = fileLength;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
