package com.libtop.weituR.activity.search.dto;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 2015/12/24 0024.
 */
public class DocResult {
    public String id;//唯一标识
    public String pdfUrl;//地址
    public String title;//标题

    public void form(JSONObject jObj) throws JSONException {
        this.id = jObj.getString("id");
        this.title = jObj.getString("title");
        this.pdfUrl=jObj.getString("pdfUrl");
    }

}
