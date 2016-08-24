package com.libtop.weituR.activity.main.dto;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2016/1/14 0014.
 */
public class NoticeInfo {
    public String id;
    public String title;
    public long dateLine;

    public void form(JSONObject object) throws JSONException {
        this.id = object.getString("id");
        this.title = object.getString("title").replaceAll(" ","").trim();
        this.dateLine=object.getLong("dateLine");
    }
}
