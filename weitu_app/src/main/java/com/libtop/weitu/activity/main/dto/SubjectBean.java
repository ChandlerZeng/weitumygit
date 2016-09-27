package com.libtop.weitu.activity.main.dto;

import com.libtop.weitu.utils.StringUtil;

import java.io.Serializable;

/**
 * Created by LianTu on 2016-9-22.
 */

public class SubjectBean implements Serializable
{

    /**
     * id : 57e3975d0412cc54c0903e3d
     * uid : 565bea2c984ec06f56befda3
     * title : 11411
     * introduction :
     * cover : http://image.bookus.cn/cover/474/3363/57e3975d0412cc54c0903e3d.jpg
     * follows : 1
     * label1 : 0
     * timeline : 1474533213740
     * state : 0
     * view : 0
     */

    private String id;
    private String uid;
    private String title;
    private String introduction;
    private String cover;

    private int follows;
    private int label1;
    private int state;
    private int view;
    private int resourceUpdateCount = 0;

    private long timeline;

    private boolean ischecked = false;


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
}
