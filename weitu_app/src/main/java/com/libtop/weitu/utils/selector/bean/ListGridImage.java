package com.libtop.weitu.utils.selector.bean;

/**
 * <p>
 * Title: ListGridImage.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * CreateTime：16/5/8
 * </p>
 *
 * @author 作者名
 * @version common v1.0
 */

public class ListGridImage {

    public String introduction;

    public String imageUrl;

    public String id;

    public String comment;

    public boolean ischeck = false;

    public boolean pro = false;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean ischeck() {
        return ischeck;
    }

    public void setIscheck(boolean ischeck) {
        this.ischeck = ischeck;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public boolean isPro() {
        return pro;
    }

    public void setPro(boolean pro) {
        this.pro = pro;
    }

}
