package com.libtop.weitu.activity.main.dto;

import org.json.JSONException;
import org.json.JSONObject;


public class SelectSubBean
{
    public String id;
    public String title;
    public String cover;
    public boolean isChecked = false;

    public void form(JSONObject object) throws JSONException
    {
        this.id = object.getString("id");
        this.title = object.getString("title");
        this.cover = object.getString("cover");

    }
}
