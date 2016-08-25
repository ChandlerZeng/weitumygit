package com.libtop.weitu.activity.search.dto;

/**
 * <p>
 * Title: 评论信息
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * CreateTime：16/5/31
 * </p>
 *
 * @author 陆
 * @version common v1.0
 */
public class CommentResult {

    /**
     * id : 577d59bd984e14c160db825b
     * tid : 563c69b4984e338019914a66
     * title : 大学英语六级复习资料(完整版).pdf
     * type : 3
     * typeName : 文档
     * uid : 565bea2c984ec06f56befda3
     * username : yjw
     * timeline : 1467832765160
     * ip : null
     * content : 测试测试测试
     * score : 0
     * state : 1
     * avatarPath : null
     * replyList : null
     * quotedComment : null
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
    public String avatarPath;
    public CommentResult quotedComment;
    public boolean ischecked;
}
