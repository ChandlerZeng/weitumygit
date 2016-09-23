package com.libtop.weitu.activity.main.clickHistory;

import com.libtop.weitu.utils.StringUtil;


/**
 * Created by LianTu on 2016/6/23.
 */
public class TargetBean
{
    private String id;
    private String lid;
    private String artist;
    private String isbn;
    private String author;
    private String publisher;
    private String title;
    private String cover;
    private String introduction;
    private String categoriesName1;
    private String categoriesName2;
    private String uploadUid;
    private String uploadUsername;
    private String[] tags;

    private Integer state;
    private Integer type;
    private Integer channel;
    private Integer label1;
    private Integer label2;
    private Integer label3;
    private Integer hot;
    private Integer comment;
    private Integer view;
    private Integer favorite;
    private Integer open;

    private Long timeline;

    private Object mediaCount;

    private boolean ischecked = false;


    public String getCover()
    {
        return StringUtil.getString(cover);
    }


    public void setCover(String cover)
    {
        this.cover = cover;
    }


    public String getId()
    {
        return StringUtil.getString(id);
    }


    public void setId(String id)
    {
        this.id = id;
    }


    public String getLid()
    {
        return StringUtil.getString(lid);
    }


    public void setLid(String lid)
    {
        this.lid = lid;
    }


    public String getArtist()
    {
        return StringUtil.getString(artist);
    }


    public void setArtist(String artist)
    {
        this.artist = artist;
    }


    public String getIsbn()
    {
        return StringUtil.getString(isbn);
    }


    public void setIsbn(String isbn)
    {
        this.isbn = isbn;
    }


    public String getAuthor()
    {
        return StringUtil.getString(isbn);
    }


    public void setAuthor(String author)
    {
        this.author = author;
    }


    public String getPublisher()
    {
        return StringUtil.getString(publisher);
    }


    public void setPublisher(String publisher)
    {
        this.publisher = publisher;
    }


    public String getTitle()
    {
        return StringUtil.getString(publisher);
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


    public String getCategoriesName1()
    {
        return StringUtil.getString(categoriesName1);
    }


    public void setCategoriesName1(String categoriesName1)
    {
        this.categoriesName1 = categoriesName1;
    }


    public String getCategoriesName2()
    {
        return StringUtil.getString(categoriesName2);
    }


    public void setCategoriesName2(String categoriesName2)
    {
        this.categoriesName2 = categoriesName2;
    }


    public String getUploadUid()
    {
        return StringUtil.getString(uploadUid);
    }


    public void setUploadUid(String uploadUid)
    {
        this.uploadUid = uploadUid;
    }


    public String getUploadUsername()
    {
        return StringUtil.getString(uploadUsername);
    }


    public void setUploadUsername(String uploadUsername)
    {
        this.uploadUsername = uploadUsername;
    }


    public String[] getTags()
    {
        return tags;
    }


    public void setTags(String[] tags)
    {
        this.tags = tags;
    }


    public Integer getState()
    {
        return state;
    }


    public void setState(Integer state)
    {
        this.state = state;
    }


    public Integer getType()
    {
        return type;
    }


    public void setType(Integer type)
    {
        this.type = type;
    }


    public Integer getChannel()
    {
        return channel;
    }


    public void setChannel(Integer channel)
    {
        this.channel = channel;
    }


    public Integer getLabel1()
    {
        return label1;
    }


    public void setLabel1(Integer label1)
    {
        this.label1 = label1;
    }


    public Integer getLabel2()
    {
        return label2;
    }


    public void setLabel2(Integer label2)
    {
        this.label2 = label2;
    }


    public Integer getLabel3()
    {
        return label3;
    }


    public void setLabel3(Integer label3)
    {
        this.label3 = label3;
    }


    public Integer getHot()
    {
        return hot;
    }


    public void setHot(Integer hot)
    {
        this.hot = hot;
    }


    public Integer getComment()
    {
        return comment;
    }


    public void setComment(Integer comment)
    {
        this.comment = comment;
    }


    public Integer getView()
    {
        return view;
    }


    public void setView(Integer view)
    {
        this.view = view;
    }


    public Integer getFavorite()
    {
        return favorite;
    }


    public void setFavorite(Integer favorite)
    {
        this.favorite = favorite;
    }


    public Integer getOpen()
    {
        return open;
    }


    public void setOpen(Integer open)
    {
        this.open = open;
    }


    public Long getTimeline()
    {
        return timeline;
    }


    public void setTimeline(Long timeline)
    {
        this.timeline = timeline;
    }


    public Object getMediaCount()
    {
        return mediaCount;
    }


    public void setMediaCount(Object mediaCount)
    {
        this.mediaCount = mediaCount;
    }


    public boolean ischecked()
    {
        return ischecked;
    }


    public void setIschecked(boolean ischecked)
    {
        this.ischecked = ischecked;
    }
}
