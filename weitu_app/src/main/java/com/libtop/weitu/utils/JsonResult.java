package com.libtop.weitu.utils;

import com.google.gson.reflect.TypeToken;

public class JsonResult {

    public int status;
    public String message;
    public Object data;

    public JsonResult(int status, String message, Object data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public <T> T get(Class<T> clazz) {
        return JsonUtil.fromObject(data, clazz);
    }

    public <T> T get(TypeToken<T> token) {
        return JsonUtil.fromObject(data, token);
    }

    public <T> T getDecrypt(Class<T> clazz) {
        if(data == null){
            return null;
        }
        return JsonUtil.fromJson(String.valueOf(data), clazz);
    }

    public <T> T getDecrypt(TypeToken<T> token) {
        if(data == null){
            return null;
        }
        return JsonUtil.fromJson(String.valueOf(data), token);
    }

    public String toString() {
        return JsonUtil.toJson(this);
    }

    /**
     * 由字符串构造
     *
     * @param value
     * @return
     */
    public static JsonResult fromString(String value) {
        if (value == null) {
            return null;
        }
        JsonResult result = JsonUtil.fromJson(value, JsonResult.class);
        return result;
    }

}
