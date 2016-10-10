package com.libtop.weitu.activity.main.dto;

import com.libtop.weitu.utils.StringUtil;

import java.util.List;

/**
 * Created by zeng on 2016/10/9.
 */

public class SubjectResourceBean {
    private String id;
    private String uid;
    private String artist;
    private String title;
    private String cover;
    private String isbn;
    private String author;
    private String publisher;
    private String introduction;
    private String categoriesName2;
    private String categoriesName1;
    private String entityType;
    private String uploadUsername;
    private String[] tags;
    private Integer label1;
    private Integer label2;
    private Integer label3;
    private long timeline;
    private Integer type;
    private Integer hot;
    private Integer state;
    private Integer comment;
    private Integer favorite;
    private Integer open;
    private Object lid;
    private Integer view;
    private Object uploadUid;
    private Integer mediaCount;
    private Integer channel;
    private int follows;
    private int resourceUpdateCount = 0;
    private boolean ischecked;
    private List<ResourceBean> resourcesList;

    public String getArtist() {
        return StringUtil.getString(artist);
    }

    public void setArtist(String artist) {
        this.artist = artist;
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


    public String getId()
    {
        return StringUtil.getString(id);
    }


    public void setId(String id)
    {
        this.id = id;
    }


    public String getUid()
    {
        return StringUtil.getString(uid);
    }


    public void setUid(String uid)
    {
        this.uid = uid;
    }


    public String getTitle()
    {
        return StringUtil.getString(title);
    }


    public void setTitle(String title)
    {
        this.title = title;
    }


    public String getIntroduction()
    {
        return StringUtil.getString(introduction);
    }


    public void setIntroduction(String introduction)
    {
        this.introduction = introduction;
    }


    public String getCover()
    {
        return StringUtil.getString(cover);
    }


    public void setCover(String cover)
    {
        this.cover = cover;
    }


    public int getResourceUpdateCount()
    {
        return resourceUpdateCount;
    }


    public void setResourceUpdateCount(int resourceUpdateCount)
    {
        this.resourceUpdateCount = resourceUpdateCount;
    }


    public int getFollows()
    {
        return follows;
    }


    public void setFollows(int follows)
    {
        this.follows = follows;
    }


    public int getLabel1()
    {
        return label1;
    }


    public void setLabel1(int label1)
    {
        this.label1 = label1;
    }


    public int getState()
    {
        return state;
    }


    public void setState(int state)
    {
        this.state = state;
    }


    public int getView()
    {
        return view;
    }


    public void setView(int view)
    {
        this.view = view;
    }


    public long getTimeline()
    {
        return timeline;
    }


    public void setTimeline(long timeline)
    {
        this.timeline = timeline;
    }


    public boolean ischecked()
    {
        return ischecked;
    }


    public void setIschecked(boolean ischecked)
    {
        this.ischecked = ischecked;
    }


    public List<ResourceBean> getResourcesList()
    {
        return resourcesList;
    }

    public void setResourcesList(List<ResourceBean> resourcesList)
    {
        this.resourcesList = resourcesList;
    }

    public String getCategoriesName1() {
        return StringUtil.getString(categoriesName1);
    }

    public void setCategoriesName1(String categoriesName1) {
        this.categoriesName1 = categoriesName1;
    }

    public String getCategoriesName2() {
        return StringUtil.getString(categoriesName2);
    }

    public void setCategoriesName2(String categoriesName2) {
        this.categoriesName2 = categoriesName2;
    }

    public int getLabel2() {
        return label2;
    }

    public void setLabel2(int label2) {
        this.label2 = label2;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public void setLabel1(Integer label1) {
        this.label1 = label1;
    }

    public void setLabel2(Integer label2) {
        this.label2 = label2;
    }

    public Integer getLabel3() {
        return label3;
    }

    public void setLabel3(Integer label3) {
        this.label3 = label3;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getHot() {
        return hot;
    }

    public void setHot(Integer hot) {
        this.hot = hot;
    }

    public Integer getComment() {
        return comment;
    }

    public void setComment(Integer comment) {
        this.comment = comment;
    }

    public Integer getFavorite() {
        return favorite;
    }

    public void setFavorite(Integer favorite) {
        this.favorite = favorite;
    }

    public Integer getOpen() {
        return open;
    }

    public void setOpen(Integer open) {
        this.open = open;
    }

    public Object getLid() {
        return lid;
    }

    public void setLid(Object lid) {
        this.lid = lid;
    }

    public void setView(Integer view) {
        this.view = view;
    }

    public Object getUploadUid() {
        return uploadUid;
    }

    public void setUploadUid(Object uploadUid) {
        this.uploadUid = uploadUid;
    }

    public Integer getMediaCount() {
        return mediaCount;
    }

    public void setMediaCount(Integer mediaCount) {
        this.mediaCount = mediaCount;
    }

    public Integer getChannel() {
        return channel;
    }

    public void setChannel(Integer channel) {
        this.channel = channel;
    }
}
