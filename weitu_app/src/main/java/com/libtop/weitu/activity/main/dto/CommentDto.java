package com.libtop.weitu.activity.main.dto;

import com.libtop.weitu.utils.StringUtil;

import java.util.List;

/**
 * Created by zeng on 2016/9/23.
 */

public class CommentDto {

    /**
     * id : 57e4d24d04126079b94edeb9
     * tid : 9787561929285
     * title : 一本突破新日语能力考试N5级词汇
     * type : 5
     * typeName : 图书
     * uid : 565bea2c984ec06f56befda3
     * username : yjw
     * timeline : 1474613837288
     * content : 这本书是真的好，太nice了
     * score : 0
     * state : 0
     * replyList : [{"id":"57e4df890412245af120cf1d","cid":"57e4d24d04126079b94edeb9","uid":"565bea2c984ec06f56befda3","username":"yjw","content":"这个神回复","timeline":1474617225935,"state":0}]
     * praises : 0
     * replies : 0
     */

    private String id;
    private String tid;
    private String title;
    private String typeName;
    private String uid;
    private String username;
    private String content;
    private String logo;
    private long timeline;
    public int type;
    public int score;
    public int state;
    public int my_praise;
    public int count_reply;
    public int praises;
    public int replies;
    public boolean isExpanded = false;
    /**
     * id : 57e4df890412245af120cf1d
     * cid : 57e4d24d04126079b94edeb9
     * uid : 565bea2c984ec06f56befda3
     * username : yjw
     * content : 这个神回复
     * timeline : 1474617225935
     * state : 0
     */

    public List<ReplyListDto> replyList;


    public long getTimeline()
    {
        return timeline;
    }


    public void setTimeline(long timeline)
    {
        this.timeline = timeline;
    }


    public String getId()
    {
        return StringUtil.getString(id);
    }


    public void setId(String id)
    {
        this.id = id;
    }


    public String getTid()
    {
        return StringUtil.getString(tid);
    }


    public void setTid(String tid)
    {
        this.tid = tid;
    }


    public String getTitle()
    {
        return StringUtil.getString(title);
    }


    public void setTitle(String title)
    {
        this.title = title;
    }


    public String getTypeName()
    {
        return StringUtil.getString(typeName);
    }


    public void setTypeName(String typeName)
    {
        this.typeName = typeName;
    }


    public String getUid()
    {
        return StringUtil.getString(uid);
    }


    public void setUid(String uid)
    {
        this.uid = uid;
    }


    public String getUsername()
    {
        return StringUtil.getString(username);
    }


    public void setUsername(String username)
    {
        this.username = username;
    }


    public String getContent()
    {
        return StringUtil.getString(content);
    }


    public void setContent(String content)
    {
        this.content = content;
    }


    public String getLogo()
    {
        return ((logo == null || logo.equals("null")) ?"http://":logo);
    }


    public void setLogo(String logo)
    {
        this.logo = logo;
    }
}
