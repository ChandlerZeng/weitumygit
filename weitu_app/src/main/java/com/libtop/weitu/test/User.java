package com.libtop.weitu.test;

/**
 * @author Sai
 * @ClassName: User
 * @Description: 用户实体类
 * @date 9/13/16 17:26
 */
public class User extends Bean
{

    private String id;
    private String email;
    private String phone;
    private String username;
    private int sex;
    private String lid;

    public String getId()
    {
        return id != null ? id : "";
    }


    public void setId(String id)
    {
        this.id = id;
    }


    public String getEmail()
    {
        return email != null ? email : "";
    }


    public void setEmail(String email)
    {
        this.email = email;
    }


    public String getPhone()
    {
        return phone != null ? phone : "";
    }


    public void setPhone(String phone)
    {
        this.phone = phone;
    }


    public String getUsername()
    {
        return username != null ? username : "";
    }


    public void setUsername(String username)
    {
        this.username = username;
    }


    public int getSex()
    {
        return sex;
    }


    public void setSex(int sex)
    {
        this.sex = sex;
    }


    public String getLid()
    {
        return lid != null ? lid : "";
    }


    public void setLid(String lid)
    {
        this.lid = lid;
    }


    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append("User:[");
        sb.append("id").append("=").append(getId()).append(",");
        sb.append("email").append("=").append(getEmail()).append(",");
        sb.append("phone").append("=").append(getPhone()).append(",");
        sb.append("username").append("=").append(getUsername()).append(",");
        sb.append("sex").append("=").append(getSex()).append(",");
        sb.append("lid").append("=").append(getLid());
        sb.append("]");

        return sb.toString();
    }
}
