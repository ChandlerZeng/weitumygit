package com.libtop.weituR.activity.search.dto;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 媒体类
 * Created by Administrator on 2015/12/24 0024.
 */
//@Table(name = "media")
public class MediaResult implements Parcelable{
//    @Column(name = "main_id", isId = true)
    private int hook;
//    @Column(name = "id")
    public String id;//唯一标识
//    @Column(name = "url")
    public String url;//地址
//    @Column(name = "title")
    public String title;//标题
//    @Column(name = "intro")
    public String introduction;
//    @Column(name = "view_num")
    public int view;
//    @Column(name = "user")
    public String uploadUsername;

    public MediaResult() {
    }

    public int getHook() {
        return hook;
    }

    public void setHook(int hook) {
        this.hook = hook;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUploadUsername() {
        return uploadUsername;
    }

    public void setUploadUsername(String uploadUsername) {
        this.uploadUsername = uploadUsername;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private MediaResult(Parcel source){
        id=source.readString();
        url=source.readString();
        title=source.readString();
        introduction=source.readString();
        view=source.readInt();
        uploadUsername=source.readString();
    }

    public void form(JSONObject jObj) throws JSONException{
        this.id = jObj.getString("id");
        this.title = jObj.getString("title");
        this.url=jObj.getString("url");
        this.introduction=jObj.getString("introduction");
        this.view=jObj.getInt("view");
        this.uploadUsername=jObj.getString("uploadUsername");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(url);
        dest.writeString(title);
        dest.writeString(introduction);
        dest.writeInt(view);
        dest.writeString(uploadUsername);
    }

    public static final Creator<MediaResult> CREATOR=new Creator<MediaResult>(){


        @Override
        public MediaResult createFromParcel(Parcel source) {
            return new MediaResult(source);
        }

        @Override
        public MediaResult[] newArray(int size) {
            return new MediaResult[size];
        }
    };
}
