package com.libtop.weituR.activity.search.dto;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2015/12/23 0023.
 */
public class SearchResult {
    public String id;//id标记
    public String cover;//icon图标
    public String uploadUsername;//上传者
    public String categoriesName1;
    public String categoriesName2;
    public String title;//标题
    public String introduction;//简介

    //音视频项
    public String artist;//艺术家
    public int hot;//热度
    public int view;//浏览数
    public int favorite;//收藏

    public void of(JSONObject object) throws JSONException{
        this.id = object.getString("id");
        this.title = object.getString("title");
        this.cover = object.getString("cover");
        this.uploadUsername=object.getString("uploadUsername");
        try {
            this.artist=object.getString("artist");
        } catch (Exception e) {
            this.artist="";
        }
        this.hot=object.getInt("hot");
        this.view=object.getInt("view");
        this.favorite=object.getInt("favorite");
    }
}
