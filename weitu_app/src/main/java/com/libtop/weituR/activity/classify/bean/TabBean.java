package com.libtop.weituR.activity.classify.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 左栏标签
 */
public class TabBean {
    public String name;
    public String id;
    private boolean ischeck=false;

    public void form(JSONObject object) throws JSONException{
        this.id = object.getString("id");
        this.name = object.getString("name");

    }
    public boolean getIscheck(){
        return ischeck;
    }

    public void setIscheck(boolean ischeck){
        this.ischeck=ischeck;
    }
}
