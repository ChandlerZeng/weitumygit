package com.libtop.weitu;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * <p>
 * Title: uploadMessage.java
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
public class uploadMessage implements Parcelable {
    private String id;
    private int progress;
    private String fileurl;
    private String uid;
    private String fid;
    private String uploadUrl;
    private int uploadPost;
    private String state;


    public uploadMessage() {
        super();
    }

    public uploadMessage(String id, int progress, String fileurl, String uid, String fid, String uploadUrl, int uploadPost, String state) {
        super();
        this.id = id;
        this.progress = progress;
        this.fileurl = fileurl;
        this.uid = uid;
        this.fid = fid;
        this.uploadUrl = uploadUrl;
        this.uploadPost = uploadPost;
        this.state = state;
    }


    @Override
    public boolean equals(Object o) {

        uploadMessage us = (uploadMessage) o;
        if (this.uid.equals(us.uid) && this.fid.equals(us.fid)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Log.i("main", "客户端User被序列化");
        dest.writeString(id);
        dest.writeInt(progress);
        dest.writeString(fileurl);
        dest.writeString(uid);
        dest.writeString(fid);
        dest.writeString(uploadUrl);
        dest.writeInt(uploadPost);
        dest.writeString(state);
    }

    public static final Parcelable.Creator<uploadMessage> CREATOR = new Creator<uploadMessage>() {

        @Override
        public uploadMessage[] newArray(int size) {
            return new uploadMessage[size];
        }

        @Override
        public uploadMessage createFromParcel(Parcel source) {
            Log.i("main", "客户端User被反序列化");
            return new uploadMessage(source.readString(), source.readInt(), source.readString(), source.readString(),
                    source.readString(), source.readString(), source.readInt(), source.readString());
        }
    };

    public String getFileurl() {
        return fileurl;
    }

    public void setFileurl(String fileurl) {
        this.fileurl = fileurl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public int getUploadPost() {
        return uploadPost;
    }

    public void setUploadPost(int uploadPost) {
        this.uploadPost = uploadPost;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}
