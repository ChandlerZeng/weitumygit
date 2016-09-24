package com.libtop.weitu.activity.main.dto;


import java.io.Serializable;
import java.util.List;

/**
 * Created by zeng on 2016/9/23.
 */

public class CommentDto implements Serializable{

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
     */

    public String id;
    public String tid;
    public String title;
    public int type;
    public String typeName;
    public String uid;
    public String username;
    public long timeline;
    public String content;
    public int score;
    public int state;
    public int replies;
    public int praises;
    public int praised;
    public String logo;
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

}
