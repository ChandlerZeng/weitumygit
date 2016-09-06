package com.libtop.weitu.activity.search.dto;

import org.json.JSONException;
import org.json.JSONObject;


public class CommonDto
{

    public String id;
    public String tid;
    public String uid;
    public String username;
    public long timeline;
    public String ip;
    public String content;
    public int state;
    public int score;


    public void of(JSONObject object) throws JSONException
    {
        this.id = object.getString("id");
        this.tid = object.getString("tid");
        this.uid = object.getString("uid");
        this.username = object.getString("username");
        this.timeline = object.getLong("timeline");
        this.ip = object.getString("ip");
        this.content = object.getString("content");
        this.state = object.getInt("state");
        this.score = object.getInt("score");
    }
}
