package com.libtop.weitu.activity.main.dto;

import com.libtop.weitu.test.CategoryResult;
import com.libtop.weitu.utils.StringUtil;

/**
 * Created by zeng on 2016/9/28.
 */

public class ResourceBean implements CategoryResult {
    public String artist;
    public Object lid;
    public Integer channel;
    public String title;
    public Integer type;
    public Integer hot;
    public String cover;
    public Integer view;
    public Object uploadUid;
    public Integer mediaCount;
    public String id;
    public String isbn;
    public Integer state;
    public String author;
    public String publisher;
    public String introduction;
    public String categoriesName2;
    public String categoriesName1;
    public String entityType;
    public String uploadUsername;
    public Integer label1;
    public Integer label2;
    public Integer label3;
    public String[] tags;
    public long timeline;
    public Integer comment;
    public Integer favorite;
    public Integer open;

    public String getArtist() {
        return StringUtil.getString(artist);
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return StringUtil.getString(title);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return StringUtil.getString(cover);
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getId() {
        return StringUtil.getString(id);
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsbn() {
        return StringUtil.getString(isbn);
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getAuthor() {
        return StringUtil.getString(author);
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return StringUtil.getString(publisher);
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getIntroduction() {
        return StringUtil.getString(introduction);
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getCategoriesName2() {
        return StringUtil.getString(categoriesName2);
    }

    public void setCategoriesName2(String categoriesName2) {
        this.categoriesName2 = categoriesName2;
    }

    public String getCategoriesName1() {
        return StringUtil.getString(categoriesName1);
    }

    public void setCategoriesName1(String categoriesName1) {
        this.categoriesName1 = categoriesName1;
    }

    public String getEntityType() {
        return StringUtil.getString(entityType);
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getUploadUsername() {
        return StringUtil.getString(uploadUsername);
    }

    public void setUploadUsername(String uploadUsername) {
        this.uploadUsername = uploadUsername;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }
}
