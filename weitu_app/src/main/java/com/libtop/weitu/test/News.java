package com.libtop.weitu.test;

/**
 * @author Sai
 * @ClassName: News
 * @Description: 校内通知实体类
 * @date 9/14/16 00:27
 */
public class News extends Bean
{
    private String id;
    private String title;
    private long t_create;
    private String url;


    public String getId()
    {
        return id != null ? id : "";
    }


    public void setId(String id)
    {
        this.id = id;
    }


    public String getTitle()
    {
        return title != null ? title : "";
    }


    public void setTitle(String title)
    {
        this.title = title;
    }


    public long getT_create()
    {
        return t_create;
    }


    public void setT_create(long t_create)
    {
        this.t_create = t_create;
    }


    public String getUrl()
    {
        return url != null ? url : "";
    }


    public void setUrl(String url)
    {
        this.url = url;
    }


    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append("News:[");
        sb.append("id").append("=").append(getId()).append(",");
        sb.append("title").append("=").append(getTitle()).append(",");
        sb.append("t_create").append("=").append(getT_create()).append(",");
        sb.append("url").append("=").append(getUrl());
        sb.append("]");

        return sb.toString();
    }
}
