package com.libtop.weitu.http;

/**
 * Created by Administrator on 2016/1/30 0030.
 */
public interface Callback {
    public interface Base{
        void onSuccess(String result);//业务json数据
        void onError(Throwable ex);//请求异常回调
        void onFail(int code,String msg);//业务处理异常回调

    }

    public interface Progress extends Base{
        void onTranslation(long total,long current);
    }
}
