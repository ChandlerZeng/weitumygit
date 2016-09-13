package com.libtop.weitu.test;

/**
 * @author Sai
 * @ClassName: User
 * @Description: 用户实体类
 * @date 9/13/16 17:26
 */
public class User extends Bean
{
    private String uid;
    private String mobile;
    private String name;
    private String logo;
    private int sex;


    public String getUid()
    {
        return uid;
    }


    public void setUid(String uid)
    {
        this.uid = uid;
    }


    public String getMobile()
    {
        return mobile != null ? mobile : "";
    }


    public void setMobile(String mobile)
    {
        this.mobile = mobile;
    }


    public String getName()
    {
        return name != null ? name : "";
    }


    public void setName(String name)
    {
        this.name = name;
    }


    public String getLogo()
    {
        return logo != null ? logo : "";
    }


    public void setLogo(String logo)
    {
        this.logo = logo;
    }


    public int getSex()
    {
        return sex;
    }


    public void setSex(int sex)
    {
        this.sex = sex;
    }


    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append("User:[");
        sb.append("uid").append("=").append(getUid()).append(",");
        sb.append("mobile").append("=").append(getMobile()).append(",");
        sb.append("name").append("=").append(getName()).append(",");
        sb.append("logo").append("=").append(getLogo()).append(",");
        sb.append("sex").append("=").append(getSex());
        sb.append("]");

        return sb.toString();
    }
}
