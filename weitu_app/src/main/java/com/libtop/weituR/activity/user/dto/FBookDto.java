package com.libtop.weituR.activity.user.dto;

import org.json.JSONException;
import org.json.JSONObject;

public class FBookDto {
	public String id;
	public String isbn;
	public String code;
	public String bid;
	public String uid;
	public String title;
	public String author;
	public String publisher;
	public String cover;
	
	public void of(JSONObject object) throws JSONException{
		this.id = object.getString("id");
		this.isbn = object.getString("isbn");
		this.title = object.getString("title");
		this.author = object.getString("author");
		this.bid = object.getString("bid");
		this.uid = object.getString("uid");
		this.code = object.getString("code");
		this.publisher = object.getString("publisher");
		this.cover = object.getString("cover");
	}
}
