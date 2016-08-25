package com.libtop.weitu.activity.classify.bean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/13.
 */
public class KeyBean {
    public String name;
    public List<Child> subCategories;

    public void form(JSONObject object) throws JSONException {
        this.name = object.getString("name");
        JSONArray array=object.getJSONArray("subCategories");
        if (array!=null){
            subCategories=new ArrayList<Child>();
            for (int i=0;i<array.length();i++){
                Child child=new Child();
                child.form(array.getJSONObject(i));
                subCategories.add(child);
            }
        }
    }

    public class Child{
        public String name;
        public int code;

        private void form(JSONObject object) throws JSONException{
            this.name = object.getString("name");
            this.code=object.getInt("code");
        }
    }



}
