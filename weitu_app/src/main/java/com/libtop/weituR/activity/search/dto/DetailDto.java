package com.libtop.weituR.activity.search.dto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DetailDto {

	public String id;
	public String mid;
	public String isbn;
	public List<String> isbnList;
	public String title;
	public String author;
	public String cover;
	public String introduction;
	public String catalog;
	public String publisher;
	public String downloadUrl;
	public int disc;
	public List<BookItem> indexList;
	public int comment;
	public int score;
	public int gather;
	public int wish;
	public int past;
	public int like;
	public int unlike;
	public int order;

	public DetailDto() {
		indexList = new ArrayList<BookItem>();
	}

	public void of(JSONObject object) throws JSONException {
		this.id = object.getString("id");
		this.mid = object.getString("mid");
		this.isbn = object.getString("isbn");
		this.isbnList = list(object.getJSONArray("isbnList"));
		this.title = object.getString("title");
		this.author = object.getString("author");
		this.cover = object.getString("cover");
		this.introduction = object.getString("introduction");
		this.catalog = object.getString("catalog");
		this.publisher = object.getString("publisher");
		this.disc = object.getInt("disc");
		comment = object.getInt("comment");
		score = object.getInt("score");
		gather = object.getInt("gather");
		wish = object.getInt("wish");
		past = object.getInt("past");
		like = object.getInt("like");
		unlike = object.getInt("unlike");
		order = object.getInt("order");
	}

	private List<String> list(JSONArray array) throws JSONException {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < array.length(); i++) {
			list.add(array.get(i).toString());
		}
		return list;
	}
}
