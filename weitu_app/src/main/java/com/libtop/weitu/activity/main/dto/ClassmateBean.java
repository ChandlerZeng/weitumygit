package com.libtop.weitu.activity.main.dto;

/**
 * Created by Administrator on 2016/1/19 0019.
 */
public class ClassmateBean
{
    private String id;
    private String email;
    private String phone;
    private String username;
    private String character;
    private String libraryId;
    private int sex;//0:男1:女


    public String getId()
    {
        return id;
    }


    public void setId(String id)
    {
        this.id = id;
    }


    public String getEmail()
    {
        return email;
    }


    public void setEmail(String email)
    {
        this.email = email;
    }


    public String getPhone()
    {
        return phone;
    }


    public void setPhone(String phone)
    {
        this.phone = phone;
    }


    public String getUserName()
    {
        return username;
    }


    public void setUserName(String userName)
    {
        this.username = userName;
    }


    public String getCharacter()
    {
        return character;
    }


    public void setCharacter(String character)
    {
        this.character = character;
    }


    public String getLibraryId()
    {
        return libraryId;
    }


    public void setLibraryId(String libraryId)
    {
        this.libraryId = libraryId;
    }


    public int getSex()
    {
        return sex;
    }


    public void setSex(int sex)
    {
        this.sex = sex;
    }
}
