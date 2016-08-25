package com.libtop.weitu.activity.search.dto;

import org.json.JSONException;
import org.json.JSONObject;

public class BookItem {
	
	public String number;
	public String code;
	public String location;
	public String status;

	public void of(JSONObject object) throws JSONException {
		this.number = object.getString("number");
		this.code = object.getString("code");
		this.location = object.getString("location");
		this.status = object.getString("status");
	}
}
