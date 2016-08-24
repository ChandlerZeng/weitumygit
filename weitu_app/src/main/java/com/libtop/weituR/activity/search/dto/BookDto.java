package com.libtop.weituR.activity.search.dto;

import org.json.JSONException;
import org.json.JSONObject;

public class BookDto {
	
	public String id;
	public String isbn;
	public String isbnList;
	public String title;
	public String author;
	public String cover;
	public String introduction;
	public String publisher;
	public String categoriesName1;
	public String categoriesName2;

	public void of(JSONObject object) throws JSONException{
		this.id = object.getString("id");
		this.isbn = object.getString("isbn");
		this.isbnList = object.getString("isbnList");
		this.title = object.getString("title");
		this.author = object.getString("author");
		this.cover = object.getString("cover");
		this.introduction = object.getString("introduction");
		this.publisher = object.getString("publisher");
	}

}
