package com.libtop.weituR.activity.main.videoUpload;

/**
 * <p>
 * Title: VideaState.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * CreateTime：16/5/3
 * </p>
 *
 * @author 作者名
 * @version common v1.0
 */
public class VideaState {
    /**
     * 新创建
     */
    public final static int NEW = 0;
    /**
     * 待补充
     */
    public final static int UNCOMPLETE = 1;
    /**
     * 等待转码
     */
    public final static int WAITCHANGE = 5;
    /**
     * 转换失败
     */
    public final static int UNCHANGE = 9;
    /**
     * 转换成功，待审核
     */
    public final static int CHANGESUCCESS = 10;
    /**
     * 已补充
     */
    public final static int COMPLETED = 15;
    /**
     * 审核通过
     */
    public final static int PASS = 20;
    /**
     * 审核不通过
     */
    public final static int UNPASS = 19;
    /**
     * 上架
     */
    public final static int UP = 30;
    /**
     * 下架
     */
    public final static int DOWN = 35;
    /**
     * 删除
     */
    public final static int DELETEED = 40;

    public static String getState(int state) {
        String a = "";
        switch (state){
            case NEW:
                a = "新创建";
                break;
            case UNCOMPLETE:
                a = "待补充";
                break;
            case WAITCHANGE:
                a = "等待转码";
                break;
            case UNCHANGE:
                a = "转换失败";
                break;
            case CHANGESUCCESS:
                a = "转换成功，待审核";
                break;
            case COMPLETED:
                a = "已补充";
                break;
            case PASS:
                a = "审核通过";
                break;
            case UNPASS:
                a = "审核不通过";
                break;
            case UP:
                a = "上架";
                break;
            case DOWN:
                a = "下架";
                break;
            case DELETEED:
                a = "删除";
                break;
        }
        return a;
    }

}
