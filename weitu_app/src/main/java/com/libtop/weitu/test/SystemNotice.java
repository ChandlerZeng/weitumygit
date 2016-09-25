package com.libtop.weitu.test;

/**
 * @author Sai
 * @ClassName: SystemNotice
 * @Description: 系统通知实体类
 * @date 9/13/16 17:23
 */
public class SystemNotice extends Bean
{
    private String id;
    private String fromUid;
    private String toUid;
    private long timeline;
    private int type;
    private String content;
    private int hasRead;
    private String extraId;
    private User fromUser;


    public String getId()
    {
        return id != null ? id : "";
    }


    public void setId(String id)
    {
        this.id = id;
    }


    public String getFromUid()
    {
        return fromUid != null ? fromUid : "";
    }


    public void setFromUid(String fromUid)
    {
        this.fromUid = fromUid;
    }


    public String getToUid()
    {
        return toUid != null ? toUid : "";
    }


    public void setToUid(String toUid)
    {
        this.toUid = toUid;
    }


    public long getTimeline()
    {
        return timeline;
    }


    public void setTimeline(long timeline)
    {
        this.timeline = timeline;
    }


    public int getType()
    {
        return type;
    }


    public void setType(int type)
    {
        this.type = type;
    }


    public String getContent()
    {
        return content != null ? content : "";
    }


    public void setContent(String content)
    {
        this.content = content;
    }


    public int getHasRead()
    {
        return hasRead;
    }


    public void setHasRead(int hasRead)
    {
        this.hasRead = hasRead;
    }


    public String getExtraId()
    {
        return extraId != null ? extraId : "";
    }


    public void setExtraId(String extraId)
    {
        this.extraId = extraId;
    }


    public User getFromUser()
    {
        return fromUser;
    }


    public void setFromUser(User fromUser)
    {
        this.fromUser = fromUser;
    }


    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append("SystemNotice:[");
        sb.append("id").append("=").append(getId()).append(",");
        sb.append("fromUid").append("=").append(getFromUid()).append(",");
        sb.append("toUid").append("=").append(getToUid()).append(",");
        sb.append("timeline").append("=").append(getTimeline()).append(",");
        sb.append("type").append("=").append(getType()).append(",");
        sb.append("content").append("=").append(getContent()).append(",");
        sb.append("hasRead").append("=").append(getHasRead()).append(",");
        sb.append("extraId").append("=").append(getExtraId()).append(",");
        sb.append("fromUser").append("=").append(getFromUser() != null ? getFromUser().toString() : "null");
        sb.append("]");

        return sb.toString();
    }
}
