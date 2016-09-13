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
    private String from_uid;
    private String to_uid;
    private long t_create;
    private String type;
    private String content;
    private int has_read;
    private String extra_id;
    private User from_user;


    public String getId()
    {
        return id != null ? id : "";
    }


    public void setId(String id)
    {
        this.id = id;
    }


    public String getFrom_uid()
    {
        return from_uid != null ? from_uid : "";
    }


    public void setFrom_uid(String from_uid)
    {
        this.from_uid = from_uid;
    }


    public String getTo_uid()
    {
        return to_uid != null ? to_uid : "";
    }


    public void setTo_uid(String to_uid)
    {
        this.to_uid = to_uid;
    }


    public long getT_create()
    {
        return t_create;
    }


    public void setT_create(long t_create)
    {
        this.t_create = t_create;
    }


    public String getType()
    {
        return type != null ? type : "";
    }


    public void setType(String type)
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


    public int getHas_read()
    {
        return has_read;
    }


    public void setHas_read(int has_read)
    {
        this.has_read = has_read;
    }


    public String getExtra_id()
    {
        return extra_id != null ? extra_id : "";
    }


    public void setExtra_id(String extra_id)
    {
        this.extra_id = extra_id;
    }


    public User getFrom_user()
    {
        return from_user;
    }


    public void setFrom_user(User from_user)
    {
        this.from_user = from_user;
    }


    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append("SystemNotice:[");
        sb.append("id").append("=").append(getId()).append(",");
        sb.append("from_uid").append("=").append(getFrom_uid()).append(",");
        sb.append("to_uid").append("=").append(getTo_uid()).append(",");
        sb.append("t_create").append("=").append(getT_create()).append(",");
        sb.append("type").append("=").append(getType()).append(",");
        sb.append("content").append("=").append(getContent()).append(",");
        sb.append("has_read").append("=").append(getHas_read()).append(",");
        sb.append("extra_id").append("=").append(getExtra_id()).append(",");
        sb.append("from_user").append("=").append(getFrom_uid() != null ? getFrom_uid() : "null");
        sb.append("]");

        return sb.toString();
    }
}
