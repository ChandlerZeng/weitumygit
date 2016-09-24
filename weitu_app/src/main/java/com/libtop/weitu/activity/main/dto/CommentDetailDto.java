package com.libtop.weitu.activity.main.dto;

import java.util.List;

/**
 * Created by zeng on 2016/9/24.
 */

public class CommentDetailDto {
    /**
     * id : 57e5f7ba0412245af120cf4e
     * tid : 9787512385191
     * title : Java程序设计案例教程
     * type : 5
     * typeName : 图书
     * uid : 565bea2c984ec06f56befda3
     * username : yjw
     * timeline : 1474688954738
     * content : rrr
     * score : 0
     * state : 0
     * replyList : [{"id":"57e617e004128d461998ef63","cid":"57e5f7ba0412245af120cf4e","uid":"565bea2c984ec06f56befda3","username":"yjw","content":"rrrrr","timeline":1474697184547,"state":0},{"id":"57e615f304128d461998ef5e","cid":"57e5f7ba0412245af120cf4e","uid":"565bea2c984ec06f56befda3","username":"yjw","content":"tttt","timeline":1474696691638,"state":0},{"id":"57e614ed04128d461998ef5c","cid":"57e5f7ba0412245af120cf4e","uid":"565bea2c984ec06f56befda3","username":"yjw","content":"ttttt","timeline":1474696429901,"state":0},{"id":"57e614ea04128d461998ef5a","cid":"57e5f7ba0412245af120cf4e","uid":"565bea2c984ec06f56befda3","username":"yjw","content":"yyyy","timeline":1474696426722,"state":0},{"id":"57e614da04128d461998ef56","cid":"57e5f7ba0412245af120cf4e","uid":"565bea2c984ec06f56befda3","username":"yjw","content":"rrrr","timeline":1474696410945,"state":0}]
     * praises : 9
     * praised : 0
     * replies : 0
     */

    public CommentDto comment;
    /**
     * comment : {"id":"57e5f7ba0412245af120cf4e","tid":"9787512385191","title":"Java程序设计案例教程","type":5,"typeName":"图书","uid":"565bea2c984ec06f56befda3","username":"yjw","timeline":1474688954738,"content":"rrr","score":0,"state":0,"replyList":[{"id":"57e617e004128d461998ef63","cid":"57e5f7ba0412245af120cf4e","uid":"565bea2c984ec06f56befda3","username":"yjw","content":"rrrrr","timeline":1474697184547,"state":0},{"id":"57e615f304128d461998ef5e","cid":"57e5f7ba0412245af120cf4e","uid":"565bea2c984ec06f56befda3","username":"yjw","content":"tttt","timeline":1474696691638,"state":0},{"id":"57e614ed04128d461998ef5c","cid":"57e5f7ba0412245af120cf4e","uid":"565bea2c984ec06f56befda3","username":"yjw","content":"ttttt","timeline":1474696429901,"state":0},{"id":"57e614ea04128d461998ef5a","cid":"57e5f7ba0412245af120cf4e","uid":"565bea2c984ec06f56befda3","username":"yjw","content":"yyyy","timeline":1474696426722,"state":0},{"id":"57e614da04128d461998ef56","cid":"57e5f7ba0412245af120cf4e","uid":"565bea2c984ec06f56befda3","username":"yjw","content":"rrrr","timeline":1474696410945,"state":0}],"praises":9,"praised":0,"replies":0}
     * praisedUsers : [{"id":"565bea2c984ec06f56befda3","email":"yjw@libtop.com","phone":"13539470451","username":"yjw","password":"3c64eee19cc53496050e34ba7eb74b3b8554d11cc130196d4c4a51fe91654242776a5a26dbebf63579b4d1d08ff55c45b463f9e447fbe3764b80af7e06705bbb","salt":"9a01b2f75b136a8f1cf4736e8844114b9b407c48966a2f83c310248ef1a8c536","character":"y","sex":0,"lid":"552f0673e4b040a89d228862","validatedEmail":1,"validatedPhone":0,"timeline":0}]
     * praised : 0
     */

    public int praised;
    /**
     * id : 565bea2c984ec06f56befda3
     * email : yjw@libtop.com
     * phone : 13539470451
     * username : yjw
     * password : 3c64eee19cc53496050e34ba7eb74b3b8554d11cc130196d4c4a51fe91654242776a5a26dbebf63579b4d1d08ff55c45b463f9e447fbe3764b80af7e06705bbb
     * salt : 9a01b2f75b136a8f1cf4736e8844114b9b407c48966a2f83c310248ef1a8c536
     * character : y
     * sex : 0
     * lid : 552f0673e4b040a89d228862
     * validatedEmail : 1
     * validatedPhone : 0
     * timeline : 0
     */

    public List<PraisedUsersBean> praisedUsers;
}
