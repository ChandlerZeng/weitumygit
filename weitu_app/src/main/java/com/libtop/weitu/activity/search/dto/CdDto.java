package com.libtop.weitu.activity.search.dto;

import org.json.JSONException;
import org.json.JSONObject;


public class CdDto
{

    public long id;
    public String name;
    public String size;
    public String type;

    public int playState = -1;


    public void of(JSONObject object) throws JSONException
    {
        this.id = object.getLong("id");
        this.name = object.getString("name");
        this.size = object.getString("size");
        this.type = object.getString("type");
    }

}
