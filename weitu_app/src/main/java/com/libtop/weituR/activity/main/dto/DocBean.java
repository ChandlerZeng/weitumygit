package com.libtop.weituR.activity.main.dto;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by LianTu on 2016/5/5.
 */
public class DocBean {
    public String id;
    public String uploadUid;
    public String uploadUsername;
    public Long uploadTime;
    public Integer state;
    public Integer label1;
    public String stateString;
    public Integer phase;
    public String title;
    public String introduction;
    public Integer hot;
    public Integer comment;
    public Integer view;
    public Integer type;
    public String author;
    public String isbn;
    public String publisher;
    public Integer favorite;
    public Integer open;
    public String[] tags;
    public String filePath;
    public String pdfUrl;
    public int progress;
    public String categoriesName1;
    public String categoriesName2;
    public String fromWhere;
    public Integer likes;
    public String cover;

    public void of(JSONObject object) throws JSONException {
        this.id = object.getString("id");
        this.title = object.getString("title");
        this.cover = object.getString("cover");
        this.uploadUsername=object.getString("uploadUsername");
        this.hot=object.getInt("hot");
        this.view=object.getInt("view");
        this.favorite=object.getInt("favorite");
        this.pdfUrl = object.getString("downloadUrl");

    }

}
