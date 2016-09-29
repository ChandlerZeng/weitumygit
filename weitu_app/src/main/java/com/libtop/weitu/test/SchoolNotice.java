package com.libtop.weitu.test;

/**
 * @author Sai
 * @ClassName: SchoolNotice
 * @Description: 校内通知实体类
 * @date 9/14/16 00:27
 */
public class SchoolNotice extends Bean
{
    private String id;
    private String title;
    private long dateLine;


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


    public long getDateLine()
    {
        return dateLine;
    }


    public void setDateLine(long dateLine)
    {
        this.dateLine = dateLine;
    }


    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append("SchoolNotice:[");
        sb.append("id").append("=").append(getId()).append(",");
        sb.append("title").append("=").append(getTitle()).append(",");
        sb.append("dateLine").append("=").append(getDateLine());
        sb.append("]");

        return sb.toString();
    }
}
