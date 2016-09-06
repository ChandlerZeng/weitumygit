package com.libtop.weitu.activity.main.dto;

import org.json.JSONException;
import org.json.JSONObject;


public class SchoolDto
{
    public String id;
    public String name;
    public String code;
    public String character;


    public void of(JSONObject object) throws JSONException
    {
        this.name = object.getString("name");
        this.code = object.getString("code");
        this.id = object.getString("id");
        this.character = object.getString("character");
    }
}
