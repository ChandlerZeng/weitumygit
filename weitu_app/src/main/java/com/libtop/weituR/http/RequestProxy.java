//package com.libtop.weituR.http;
//
//import android.text.TextUtils;
//import android.util.Log;
//
//import com.google.gson.JsonObject;
//import com.libtop.weituR.utils.JsonUtil;
//import org.xutils.common.Callback;
//import org.xutils.http.HttpMethod;
//import org.xutils.http.RequestParams;
//import org.xutils.x;
//
//import java.io.File;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by Administrator on 2016/1/30 0030.
// */
//public class RequestProxy {
//    private static final Object lock = new Object();
//    private static RequestProxy instance;
//    private final Map<String,Callback.Cancelable> loadingCallers=new HashMap<String, Callback.Cancelable>();
//
//
//    private RequestProxy() {
//    }
//
//    public static RequestProxy getInstance() {
//        if (instance == null) {
//            synchronized (lock) {
//                if (instance == null) {
//                    instance = new RequestProxy();
//                }
//            }
//        }
//        return instance;
//    }
//
//    public void get(String url,Map<String,String> params, final com.libtop.weituR.http.Callback.Base callback){
//        RequestParams param=new RequestParams(url);
//        param.setMethod(HttpMethod.GET);
//        for (Map.Entry<String,String> entry:params.entrySet()){
//            param.addQueryStringParameter(entry.getKey(),entry.getValue());
//        }
//        x.http().get(param, new Callback.CommonCallback<String>() {
//            @Override
//            public void onSuccess(String result) {
//                handleSuccess(result,callback);
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//                callback.onError(ex);
//            }
//
//            @Override
//            public void onCancelled(CancelledException cex) {
//
//            }
//
//            @Override
//            public void onFinished() {
//
//            }
//        });
//
//    }
//
//    private void handleSuccess(String result, com.libtop.weituR.http.Callback.Base callback){
//        int code=-1;
//        String msg="";
//        String data="";
//        try{
//            JsonObject jo=JsonUtil.getParser().parse(result).getAsJsonObject();
//            code=jo.get("code").getAsInt();
//            msg=jo.get("message").getAsString();
//            data=jo.get("data").toString();
//        }catch (Exception e){
//            Log.e("casterror",e.toString());
//        }
//        if (TextUtils.isEmpty(data)) callback.onFail(-1, "null-data");
//        else {
//            if (code==1){//业务处理成功
//
//                callback.onSuccess(data);
//            }else {//业务处理出错
//                callback.onFail(code, msg);
//            }
//        }
//    }
//
//    public void post(String url,Map<String,String> params, final com.libtop.weituR.http.Callback.Base callback){
//        RequestParams param=new RequestParams(url);
//        param.setMethod(HttpMethod.POST);
//        for (Map.Entry<String,String> entry:params.entrySet()){
//            param.addQueryStringParameter(entry.getKey(),entry.getValue());
//        }
//        x.http().get(param, new Callback.CommonCallback<String>() {
//            @Override
//            public void onSuccess(String result) {
//               handleSuccess(result,callback);
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//                callback.onError(ex);
//            }
//
//            @Override
//            public void onCancelled(CancelledException cex) {
//
//            }
//
//            @Override
//            public void onFinished() {
//
//            }
//        });
//
//    }
//
//    public void upload(String url,Map<String,Object> params
//            ,final com.libtop.weituR.http.Callback.Progress callback){
//        RequestParams param=new RequestParams(url);
//        param.setMethod(HttpMethod.POST);
//        param.setMultipart(true);
//        for (Map.Entry<String,Object> entry:params.entrySet()){
//            Object value=entry.getValue();
//            if (value instanceof File){
//                param.addBodyParameter(entry.getKey(),(File)value);
//            }else {
//                param.addQueryStringParameter(entry.getKey(),(String)value);
//            }
//        }
//        x.http().post(param, new Callback.ProgressCallback<String>() {
//            @Override
//            public void onSuccess(String result) {
//                String msg="";
//                int code=-1;
//                try{
//                    JsonObject jo=JsonUtil.getParser().parse(result).getAsJsonObject();
//                    code=jo.get("code").getAsInt();
//                    msg=jo.get("message").getAsString();
//                }catch (Exception e){
//                    Log.e("casterror",e.toString());
//                }
//                if (code==1){//业务处理成功
//                    callback.onSuccess("");
//                }else {//业务处理出错
//                    callback.onFail(code, msg);
//                }
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//                callback.onError(ex);
//            }
//
//            @Override
//            public void onCancelled(CancelledException cex) {
//                callback.onFail(-1,"upload task cancelled");
//            }
//
//            @Override
//            public void onFinished() {
//
//            }
//
//            @Override
//            public void onWaiting() {
//
//            }
//
//            @Override
//            public void onStarted() {
//
//            }
//
//            @Override
//            public void onLoading(long total, long current, boolean isDownloading) {
//                callback.onTranslation(total, current);
//            }
//        });
//    }
//
//}
