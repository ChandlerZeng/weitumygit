package com.libtop.weituR.http;

import java.util.List;

/**
 * Created by Administrator on 2016/1/30 0030.
 */
public class RequestResult<T> {
    private T data;//业务返回数据
    private String message;//业务处理信息
    private int code;//业务处理状态码:成功1

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


}
