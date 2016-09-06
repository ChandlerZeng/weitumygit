package com.libtop.weitu.activity.search.dto;

import org.json.JSONException;
import org.json.JSONObject;


public class LabelDto
{

    public int gather;
    public int wish;
    public int past;
    public int like;
    public int unlike;
    public int order;


    public void of(JSONObject data) throws JSONException
    {
        gather = data.getInt("gather");
        wish = data.getInt("wish");
        past = data.getInt("past");
        like = data.getInt("like");
        unlike = data.getInt("unlike");
        order = data.getInt("order");
    }


    public void reset(String tag, int state)
    {
        if ("gather".equals(tag))
        {
            gather = state;
        }
        else if ("wish".equals(tag))
        {
            wish = state;
        }
        else if ("past".equals(tag))
        {
            past = state;
        }
        else if ("like".equals(tag))
        {
            like = state;
        }
        else if ("unlike".equals(tag))
        {
            unlike = state;
        }
        else
        {
            order = state;
        }
    }
}
