//package com.libtop.weituR.http.old_del;
//
//import android.content.Context;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.libtop.weituR.tool.Preference;
//import com.libtop.weituR.utils.AESUtils;
//import com.libtop.weituR.utils.ArrayUtils;
//import com.libtop.weituR.utils.CheckUtil;
//import com.libtop.weituR.utils.CollectionUtils;
//import com.libtop.weituR.utils.ContantsUtil;
//import com.libtop.weituR.utils.LargeMappedFiles;
//import com.libtop.weituR.utils.RSAUtils;
//import com.libtop.weituR.utils.ThreadUtil;
//import com.zhy.http.okhttp.OkHttpUtils;
//
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.HttpStatus;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.mime.HttpMultipartMode;
//import org.apache.http.entity.mime.MultipartEntityBuilder;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.params.HttpConnectionParams;
//import org.apache.http.params.HttpParams;
//import org.apache.http.protocol.HTTP;
//import org.apache.http.util.EntityUtils;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.math.BigInteger;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.nio.charset.Charset;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import okhttp3.Response;
//
//@Deprecated
//public class HttpServiceUtil {
//
//    private static int REQUEST_MAX_TIME = 15000;
//    private static int RESPONSE_MAX_TIME = 20000;
//
//    private static String sid;
//    public static String uid;
//    private static byte[] aesKey;
//    private static String privateKey = "";
//    private static String publicKey = "";
//
//    /**
//     * 初始化参数
//     *
//     * @param context
//     */
//    public static void initParams(Context context) {
//        sid = Preference.instance(context).getString(Preference.SID);
//        uid = Preference.instance(context).getString(Preference.uid);
//        aesKey = ArrayUtils.hexToByte(Preference.instance(context).getString(
//                Preference.AESKEY));
//    }
//
//    public static void reset() {
//        sid = null;
//        uid = null;
//        aesKey = null;
//    }
//
//    /**
//     * 请求HTTP服务
//     *
//     * @param url    请求的URL地址
//     * @param params 请求是附带的参数
//     * @return
//     */
//    public static String post(String url, String params, String file, String sid) {
//        HttpClient client = new DefaultHttpClient();
//        StringBuilder builder = new StringBuilder();
//        HttpPost httprequest = new HttpPost(url);
//        HttpParams httpparams = client.getParams();
//        HttpConnectionParams.setConnectionTimeout(httpparams, REQUEST_MAX_TIME);
//        HttpConnectionParams.setSoTimeout(httpparams, RESPONSE_MAX_TIME);
//        List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
//        if (params != null) {
//            nameValue.add(new BasicNameValuePair("text", params));
//        }
//        if (!CheckUtil.isNull(sid)) {
//            nameValue.add(new BasicNameValuePair("sid", sid));
//        }
//        // if (file != null) {
//        // nameValue.add(new BasicNameValuePair("file", file));
//        // }
//        try {
//            httprequest.setEntity(new UrlEncodedFormEntity(nameValue, "utf-8"));
//            HttpResponse response = client.execute(httprequest);
//            int status = response.getStatusLine().getStatusCode();
//            if (status == HttpStatus.SC_OK) {
//                String strResult = EntityUtils.toString(response.getEntity(),
//                        "UTF-8");
//                builder.append(strResult);
//            } else {
//                builder.append("null");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            builder.append("");
//        }
//        return builder.toString();
//    }
//
//    private static void logRequestMap(Map<String, Object> params) {
//        Set<String> key = params.keySet();
//        for (Iterator it = key.iterator(); it.hasNext(); ) {
//            String s = (String) it.next();
//            Log.e("", "key=" + s + "||" + "value=" + params.get(s));
//        }
//    }
//
//    /**
//     * 请求HTTP服务
//     *
//     * @param url    请求的URL地址
//     * @param params 请求是附带的参数
//     * @return
//     */
//    public static String post(String url, Map<String, Object> params) {
//        HttpClient client = new DefaultHttpClient();
//        StringBuilder builder = new StringBuilder();
//        HttpPost httprequest = new HttpPost(url);
//        HttpParams httpparams = client.getParams();
//        HttpConnectionParams.setConnectionTimeout(httpparams, REQUEST_MAX_TIME);
//        HttpConnectionParams.setSoTimeout(httpparams, RESPONSE_MAX_TIME);
//        List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
//        if (params != null) {
//            Set<String> keySet = params.keySet();
//            Iterator<String> iterator = keySet.iterator();
//            while (iterator.hasNext()) {
//                String key = (String) iterator.next();
//                String value = params.get(key) + "";
//                nameValue.add(new BasicNameValuePair(key, value));
//            }
//        }
//        logRequestMap(params);
//        try {
//            httprequest.setEntity(new UrlEncodedFormEntity(nameValue, "utf-8"));
//            HttpResponse response = client.execute(httprequest);
//            int status = response.getStatusLine().getStatusCode();
//            if (status == HttpStatus.SC_OK) {
//                String strResult = EntityUtils.toString(response.getEntity(),
//                        "GBK");
//                builder.append(strResult);
//            } else {
//                builder.append("");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            builder.append("");
//        } finally {
//            if (client != null && client.getConnectionManager() != null) {
//                client.getConnectionManager().shutdown();
//            }
//        }
//        return builder.toString();
//    }
//
//    private static String get(String url, Map<String, Object> params) {
//        HttpClient client = new DefaultHttpClient();
//        StringBuilder builder = new StringBuilder();
//        StringBuilder paramAppend = new StringBuilder();
//        paramAppend.append(url);
//        if (params != null) {
//            paramAppend.append("?");
//            Set<String> keySet = params.keySet();
//            Iterator<String> iterator = keySet.iterator();
//            while (iterator.hasNext()) {
//                String key = (String) iterator.next();
//                String value = (String) params.get(key);
//                paramAppend.append(key + "=" + value + "&");
//            }
//            paramAppend.deleteCharAt(paramAppend.length() - 1);
//        }
//        Log.e("dd", paramAppend.toString());
//        HttpGet httpget = new HttpGet(paramAppend.toString());
//        HttpParams httpParams = client.getParams();
//        HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_MAX_TIME);
//        HttpConnectionParams.setSoTimeout(httpParams, RESPONSE_MAX_TIME);
//        try {
//            HttpResponse response = client.execute(httpget);
//            int status = response.getStatusLine().getStatusCode();
//            if (status == HttpStatus.SC_OK) {
//                String strResult = EntityUtils.toString(response.getEntity(),
//                        "GBK");
//                builder.append(strResult);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return builder.toString();
//    }
//
//    public static void request(final String url, final String method,
//                               final Map<String, Object> params, final CallBack callBack) {
//        ThreadUtil.executorService.submit(new Runnable() {
//            @Override
//            public void run() {
//                final String result;
//                if ("get".equals(method)) {
//                    result = get(url, params);
//                } else {
//                    result = post(url, params);
//                }
//                Log.e("result", result + "----");
//                ThreadUtil.handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        callBack.callback(result);
//                    }
//                }, 500);
//            }
//        });
//    }
//
////    public static void requestSec(final Context context, final boolean state,
////                                  final Map<String, Object> params, final CallBack callBack) {
////        ThreadUtil.executorService.submit(new Runnable() {
////            @Override
////            public void run() {
////                final String result;
////                if (getKey(context)) {
////                    JSONObject jsonObj = new JSONObject(params);
////                    Log.e("params", jsonObj.toString());
////                    String encode = AESUtils.encrypt(aesKey, jsonObj.toString());
////                    String url = "";
////                    if (state) {
////                        url = ContantsUtil.UHOST;
////                    } else {
////                        url = ContantsUtil.HOST;
////                    }
////                    String data = post(url, encode, null, sid);
////                    Log.e("result", data);
////                    result = data;
////                } else {
////                    result = "";
////                }
////                ThreadUtil.handler.postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
////                        callBack.callback(result);
////                    }
////                }, 500);
////            }
////        });
////    }
//
//    private static HttpEntity getUploadEntity(String path, final Map<String, Object> addition, final OnRequestCallBack callBack) {
//        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
//        builder.setCharset(Charset.forName(HTTP.UTF_8));
//        builder.addBinaryBody("file", new File(path));
//        if (!CollectionUtils.isEmpty(addition)) {
//            for (Map.Entry<String, Object> entry : addition.entrySet()) {
//                builder.addTextBody(entry.getKey(), (String) entry.getValue());
//            }
//        }
//        HttpEntity entity = builder.build();
//        final long total = entity.getContentLength();
//        ProgressOutEntity postEntity = new ProgressOutEntity(entity, new ProgressOutEntity.ProgressListener() {
//            @Override
//            public void transferred(final long transferedBytes) {
//                ThreadUtil.handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (callBack != null) callBack.onUpload(transferedBytes, total);
//                    }
//                }, 100);
//            }
//        });
//        return postEntity;
//    }
//
//    private static HttpEntity getUploadEntity(File[] files, final Map<String, Object> addition, final OnRequestCallBack callBack) {
//        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
//        builder.setCharset(Charset.forName(HTTP.UTF_8));
//        if (files != null) {
//            for (int i = 0; i < files.length; i++) {
//                builder.addBinaryBody("file" + i, files[i]);
//            }
//        }
//        if (!CollectionUtils.isEmpty(addition)) {
//            List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
//            Set<String> keySet = addition.keySet();
//            Iterator<String> iterator = keySet.iterator();
//            while (iterator.hasNext()) {
//                String key = (String) iterator.next();
//                String value = addition.get(key) + "";
//                nameValue.add(new BasicNameValuePair(key, value));
//            }
//            for (NameValuePair p : nameValue) {
//                builder.addTextBody(p.getName(), p.getValue());
//            }
//        }
//        HttpEntity entity = builder.build();
//        final long total = entity.getContentLength();
//        ProgressOutEntity postEntity = new ProgressOutEntity(entity, new ProgressOutEntity.ProgressListener() {
//            @Override
//            public void transferred(final long transferedBytes) {
//                ThreadUtil.handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (callBack != null) callBack.onUpload(transferedBytes, total);
//                    }
//                }, 100);
//            }
//        });
//        return postEntity;
//    }
//
//    /**
//     * 文件上传
//     *
//     * @param url
//     * @param path
//     * @param params
//     * @param callBack
//     */
//    public static void upload(final String url, final String path, final Map<String, Object> params, final OnRequestCallBack callBack) {
//        ThreadUtil.executorService.submit(new Runnable() {
//            @Override
//            public void run() {
//                HttpClient client = new DefaultHttpClient();
//                final StringBuilder builder = new StringBuilder();
//                HttpPost httprequest = new HttpPost(url);
//                HttpParams httpparams = client.getParams();
//                HttpConnectionParams.setConnectionTimeout(httpparams, REQUEST_MAX_TIME);
//                HttpConnectionParams.setSoTimeout(httpparams, RESPONSE_MAX_TIME);
//
//                try {
//                    httprequest.setEntity(getUploadEntity(path, params, callBack));
//
//                    HttpResponse response = client.execute(httprequest);
//                    final int status = response.getStatusLine().getStatusCode();
//                    if (status == HttpStatus.SC_OK) {
//                        String strResult = EntityUtils.toString(response.getEntity(),
//                                "UTF-8");
//                        builder.append(strResult);
//                    } else {
//                        builder.append("");
//                        ThreadUtil.handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                callBack.onFalid(status);
//                            }
//                        }, 500);
//                        return;
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    builder.append("");
//                    ThreadUtil.handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            callBack.onFalid(-1);
//                        }
//                    }, 500);
//                    return;
//                } finally {
//                    if (client != null && client.getConnectionManager() != null) {
//                        client.getConnectionManager().shutdown();
//                    }
//                }
//                ThreadUtil.handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        callBack.onSucc(builder.toString());
//                    }
//                }, 500);
//            }
//        });
//    }
//
//    public static void upload(final String url, final File[] files, final Map<String, Object> params, final OnRequestCallBack callBack) {
//        ThreadUtil.executorService.submit(new Runnable() {
//            @Override
//            public void run() {
//                HttpClient client = new DefaultHttpClient();
//                final StringBuilder builder = new StringBuilder();
//                HttpPost httprequest = new HttpPost(url);
//                HttpParams httpparams = client.getParams();
//                HttpConnectionParams.setConnectionTimeout(httpparams, REQUEST_MAX_TIME);
//                HttpConnectionParams.setSoTimeout(httpparams, RESPONSE_MAX_TIME);
//
//                try {
//                    httprequest.setEntity(getUploadEntity(files, params, callBack));
//
//                    HttpResponse response = client.execute(httprequest);
//                    final int status = response.getStatusLine().getStatusCode();
//                    if (status == HttpStatus.SC_OK) {
//                        String strResult = EntityUtils.toString(response.getEntity(),
//                                "UTF-8");
//                        builder.append(strResult);
//                    } else {
//                        builder.append("");
//                        ThreadUtil.handler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                callBack.onFalid(status);
//                            }
//                        }, 500);
//                        return;
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    builder.append("");
//                    ThreadUtil.handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            callBack.onFalid(-1);
//                        }
//                    }, 500);
//                    return;
//                } finally {
//                    if (client != null && client.getConnectionManager() != null) {
//                        client.getConnectionManager().shutdown();
//                    }
//                }
//                ThreadUtil.handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        callBack.onSucc(builder.toString());
//                    }
//                }, 500);
//            }
//        });
//    }
//
//    public interface OnRequestCallBack {
//        public void onSucc(String json);
//
//        public void onFalid(int code);
//
//        public void onUpload(long current, long total);
//    }
//
//    /**
//     * 退出登录
//     *
//     * @param callBack
//     */
////    public static void logout(final Context context, final CallBack callBack) {
////        ThreadUtil.executorService.submit(new Runnable() {
////            @Override
////            public void run() {
////                Map<String, Object> params = new HashMap<String, Object>();
////                params.put("sid", sid);
////                params.put("method", "logout");
////                JSONObject jsonObj = new JSONObject(params);
////                final String result = post("", jsonObj.toString(), null, sid);
////                sid = "";
////                uid = "";
////                Preference.instance(context).remove(Preference.SID);
////                Preference.instance(context).remove(Preference.uid);
////                ThreadUtil.handler.postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
////                        callBack.callback(result);
////                    }
////                }, 500);
////            }
////        });
////    }
//
//    /**
//     * 登陆
//     *
//     * @param context
//     * @param password
//     * @param callBack
//     */
//    public static void login(final Context context, final String phone,
//                             final String password, final CallBack callBack) {
//        ThreadUtil.executorService.submit(new Runnable() {
//            @Override
//            public void run() {
//                final String result;
//                if (getKey(context)) {
//                    Map<String, Object> params = new HashMap<String, Object>();
//                    params.put("method", "user.auth");
//                    params.put("phone", phone);
//                    params.put("password", password);
//                    JSONObject jsonObj = new JSONObject(params);
//                    String encode = AESUtils.encrypt(aesKey, jsonObj.toString());
//                    result = post(ContantsUtil.HOST, encode, null, sid);
//                } else {
//                    result = "";
//                }
//                ThreadUtil.handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (CheckUtil.isNull(result)) {
//                            callBack.callback("");
//                        } else {
//                            callBack.callback(result);
//                        }
//                    }
//                }, 500);
//            }
//        });
//    }
//
////    private static boolean getKey(Context context) {
////        if (!CheckUtil.isNull(sid)) {
////            return true;
////        }
////        Map<String, Object> params = new HashMap<String, Object>();
////        params.put("method", "keymap.getKey");
////        Map<String, BigInteger> keys = RSAUtils.generateKey();
////        privateKey = keys.get("privateExponent").toString(36);
////        publicKey = keys.get("publicModulus").toString(36);
////        params.put("key", publicKey); // 传递公匙到服务器
////        JSONObject jsonObj = new JSONObject(params);
////        String jsonStr = jsonObj.toString();
////        String result = "";
////        try {
////            Response response = OkHttpUtils
////                    .post()
////                    .url(ContantsUtil.UHOST)//
////                    .addParams("text",jsonStr)
////                    .build()//
////                    .execute( );
////            result = response.body().string();
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//////        String result = post(ContantsUtil.UHOST, jsonStr, null, "");
////        if (CheckUtil.isNull(result)) {
////            return false;
////        } else {
////            try {
////                JSONObject preJson = new JSONObject(result);
////                String decodeTxt = RSAUtils.decrypt(
////                        preJson.getString("cipher"), publicKey, privateKey);
////                JSONObject resultJson = new JSONObject(decodeTxt);
////                sid = resultJson.getString("sid");
////                String keyString = resultJson.getString("key");
////                Preference.instance(context).putString(Preference.SID, sid);
////                aesKey = ArrayUtils.hexToByte(keyString);
////                Preference.instance(context).putString(Preference.AESKEY,
////                        keyString);
////            } catch (JSONException e) {
////                e.printStackTrace();
////            }
////        }
////        return true;
////    }
//
//    private static boolean getKey(Context context) {
//        if (!CheckUtil.isNull(sid)) {
//            return true;
//        }
//        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("method", "keymap.getKey");
//        Map<String, BigInteger> keys = RSAUtils.generateKey();
//        privateKey = keys.get("privateExponent").toString(36);
//        publicKey = keys.get("publicModulus").toString(36);
//        params.put("key", publicKey); // 传递公匙到服务器
//        JSONObject jsonObj = new JSONObject(params);
//        String jsonStr = jsonObj.toString();
//        String result = post(ContantsUtil.UHOST, jsonStr, null, "");
//        if (CheckUtil.isNull(result)) {
//            return false;
//        } else {
//            try {
//                JSONObject preJson = new JSONObject(result);
//                String decodeTxt = RSAUtils.decrypt(
//                        preJson.getString("cipher"), publicKey, privateKey);
//                JSONObject resultJson = new JSONObject(decodeTxt);
//                sid = resultJson.getString("sid");
//                String keyString = resultJson.getString("key");
//                Preference.instance(context).putString(Preference.SID, sid);
//                aesKey = ArrayUtils.hexToByte(keyString);
//                Preference.instance(context).putString(Preference.AESKEY,
//                        keyString);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        return true;
//    }
//
//    /**
//     * 请求HTTP服务后的回调函数接口
//     *
//     * @author longbh
//     */
//    public interface CallBack {
//        public void callback(String json);
//    }
//
//    public static boolean isLogin() {
//        return CheckUtil.isNull(uid);
//    }
//
//    public static void setUid(String userid) {
//        uid = userid;
//    }
//
//    public static void main(String args[]) {
//        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("method", "userDetail");
//        // params.put("email", "weishenmexyz@163.com");
//        // params.put("password", "123456");
//        // request(true, params, new Callback() {
//        // @Override
//        // public void callback(String json) {
//        // System.out.println(json);
//        // }
//        // });
//    }
//
//
//    public static void postFile(final Context context, final Map<String, String> params, final int number, final LargeMappedFiles largeMappedFiles, final CallBack callBack) {
//        ThreadUtil.executorService.submit(new Runnable() {
//            @Override
//            public void run() {
//                final String result;
//                if (getKey(context)) {
//                    //   result = post2("http://192.168.0.131:8090/upload/media", params, mbyte);
//                    result = "123";
//                    dopost(number, params, largeMappedFiles, callBack);
//                } else {
//                    result = "";
//                }
////                ThreadUtil.handler.postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
////                        if (CheckUtil.isNull(result)) {
////                            callBack.callback("");
////                        } else {
////                            callBack.callback(result);
////                        }
////                    }
////                }, 500);
//            }
//        });
//    }
//
//    public static void getFile(final String url, final Context context, final Map<String, String> params, final CallBack callBack) {
//        ThreadUtil.executorService.submit(new Runnable() {
//            @Override
//            public void run() {
//                final String result;
//                // http://nt1.libtop.com/upload/media
//                if (getKey(context)) {
//                    result = get3(url, params, callBack, context);
//                } else {
//                    result = "";
//                }
////                ThreadUtil.handler.postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
////                        if (CheckUtil.isNull(result)) {
////                            callBack.callback("");
////                        } else {
////                            callBack.callback(result);
////                        }
////                    }
////                }, 500);
//            }
//        });
//    }
//
//    public static String post2(String url, Map<String, String> params, byte[] mbyte) {
//        HttpClient client = new DefaultHttpClient();
//        StringBuilder builder = new StringBuilder();
//        HttpPost httprequest = new HttpPost(url);
//        HttpParams httpparams = client.getParams();
//        HttpConnectionParams.setConnectionTimeout(httpparams, REQUEST_MAX_TIME);
//        HttpConnectionParams.setSoTimeout(httpparams, RESPONSE_MAX_TIME);
//        List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
//        if (params != null) {
//            Set<String> keySet = params.keySet();
//            Iterator<String> iterator = keySet.iterator();
//            while (iterator.hasNext()) {
//                String key = (String) iterator.next();
//                String value = params.get(key) + "";
//                nameValue.add(new BasicNameValuePair(key, value));
//            }
//        }
//        try {
//            httprequest.setEntity(new UrlEncodedFormEntity(nameValue, "utf-8"));
//            // ByteArrayEntity arrayEntity = new ByteArrayEntity(mbyte);
//            // httprequest.setEntity(entity);
//            // InputStream sbs = new ByteArrayInputStream(mbyte);
//            HttpResponse response = client.execute(httprequest);
//            int status = response.getStatusLine().getStatusCode();
//            if (status == HttpStatus.SC_OK) {
//                String strResult = EntityUtils.toString(response.getEntity(),
//                        "GBK");
//                builder.append(strResult);
//            } else {
//                builder.append("");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            builder.append("");
//        } finally {
//            if (client != null && client.getConnectionManager() != null) {
//                client.getConnectionManager().shutdown();
//            }
//        }
//        return builder.toString();
//    }
//
//
//    public static String get2(String url, Map<String, String> params, CallBack callBack, Context context) {
//        HttpClient client = new DefaultHttpClient();
//        StringBuilder builder = new StringBuilder();
//        StringBuilder paramAppend = new StringBuilder();
//        paramAppend.append(url);
//
//        if (params != null) {
//            paramAppend.append("?");
//            Set<String> keySet = params.keySet();
//            Iterator<String> iterator = keySet.iterator();
//            while (iterator.hasNext()) {
//                String key = (String) iterator.next();
//                String value = (String) params.get(key);
//                paramAppend.append(key + "=" + value + "&");
//            }
//            paramAppend.deleteCharAt(paramAppend.length() - 1);
//        }
//        //   String s = "http://192.168.0.131:8090/upload/media?flowTotalSize=1666&flowChunkNumber=1&method=user.auth&flowFilename=5.txt&flowRelativePath=5.txt&flowIdentifier=95bb33de984e2b4bbec533d5&flowChunkSize=1666";
//        HttpGet httpget = new HttpGet(paramAppend.toString());
//        //  HttpGet httpget = new HttpGet(s);
////        httpget.setHeader("User-agent", "Mozilla/5.0 (X11; Fedora; Linux x86_64; rv:36.0) Gecko/20100101 Firefox/36.0");
////        httpget.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
////        httpget.setHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
////        httpget.setHeader("Content-Type", "application/x-www-form-urlencoded");
////        httpget.setHeader("Accept-Encoding", "gzip,deflate");
//
//        HttpParams httpParams = client.getParams();
//        HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_MAX_TIME);
//        HttpConnectionParams.setSoTimeout(httpParams, RESPONSE_MAX_TIME);
//        try {
//            HttpResponse response = client.execute(httpget);
//            int status = response.getStatusLine().getStatusCode();
//
//            String a = response.toString();
//            //999状态为已上传
//            if (status == 999) {
//                callBack.callback("999");
//            } else if (status == 200) {
//                String strResult = EntityUtils.toString(response.getEntity(),
//                        "GBK");
//                builder.append(strResult);
//                callBack.callback(builder.toString());
//            } else {
//                Toast.makeText(context, "网络出错", Toast.LENGTH_SHORT).show();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return builder.toString();
//    }
//
//    public static String get3(String url, Map<String, String> params, CallBack callBack, Context context) {
//        HttpClient client = new DefaultHttpClient();
//        StringBuilder builder = new StringBuilder();
//        StringBuilder paramAppend = new StringBuilder();
//        paramAppend.append(url);
//
//        if (params != null) {
//            paramAppend.append("?");
//            Set<String> keySet = params.keySet();
//            Iterator<String> iterator = keySet.iterator();
//            while (iterator.hasNext()) {
//                String key = (String) iterator.next();
//                String value = (String) params.get(key);
//                paramAppend.append(key + "=" + value + "&");
//            }
//            paramAppend.deleteCharAt(paramAppend.length() - 1);
//        }
//        //   String s = "http://192.168.0.131:8090/upload/media?flowTotalSize=1666&flowChunkNumber=1&method=user.auth&flowFilename=5.txt&flowRelativePath=5.txt&flowIdentifier=95bb33de984e2b4bbec533d5&flowChunkSize=1666";
//        HttpGet httpget = new HttpGet(url);
//        //  HttpGet httpget = new HttpGet(s);
////        httpget.setHeader("User-agent", "Mozilla/5.0 (X11; Fedora; Linux x86_64; rv:36.0) Gecko/20100101 Firefox/36.0");
////        httpget.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
////        httpget.setHeader("Accept-Language", "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3");
////        httpget.setHeader("Content-Type", "application/x-www-form-urlencoded");
////        httpget.setHeader("Accept-Encoding", "gzip,deflate");
//
//        HttpParams httpParams = client.getParams();
//        HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_MAX_TIME);
//        HttpConnectionParams.setSoTimeout(httpParams, RESPONSE_MAX_TIME);
//        try {
//            HttpResponse response = client.execute(httpget);
//            int status = response.getStatusLine().getStatusCode();
//
//            String a = response.toString();
//            //999状态为已上传
//            if (status == 999) {
//                callBack.callback("999");
//            } else if (status == 200) {
//                String strResult = EntityUtils.toString(response.getEntity(),
//                        "GBK");
//                builder.append(strResult);
//                callBack.callback(builder.toString());
//            } else {
//                Toast.makeText(context, "网络出错", Toast.LENGTH_SHORT).show();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return builder.toString();
//    }
//
//    /**
//     * 书本详情页
//     *
//     * @param bookId
//     * @param callBack
//     */
////    public static void requestBook(final Context context, final String bookId,
////                                   final String uid, final CallBack callBack) {
////        ThreadUtil.executorService.submit(new Runnable() {
////            @Override
////            public void run() {
////                final String result;
////                if (getKey(context)) {
////                    Map<String, Object> params = new HashMap<String, Object>();
////                    params.put("method", "book.getBook");
////                    params.put("bid", bookId);
////                    params.put("uid", uid);
////                    JSONObject jsonObj = new JSONObject(params);
////                    result = post(ContantsUtil.HOST, jsonObj.toString(), null, null);
////                } else {
////                    result = "";
////                }
////                ThreadUtil.handler.postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
////                        if (CheckUtil.isNull(result)) {
////                            callBack.callback("");
////                        } else {
////                            callBack.callback(result);
////                        }
////                    }
////                }, 500);
////            }
////        });
////    }
//
//    /**
//     * 书本详情页
//     */
////    public static void requestWithMap(final Context context, final Map<String, Object> params,
////                                      final CallBack callBack) {
////        ThreadUtil.executorService.submit(new Runnable() {
////            @Override
////            public void run() {
////                final String result;
////                if (getKey(context)) {
////                    JSONObject jsonObj = new JSONObject(params);
////                    result = post(ContantsUtil.HOST, jsonObj.toString(), null, null);
////                } else {
////                    result = "";
////                }
////                ThreadUtil.handler.postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
////                        if (CheckUtil.isNull(result)) {
////                            callBack.callback("");
////                        } else {
////                            callBack.callback(result);
////                        }
////                    }
////                }, 500);
////            }
////        });
////    }
//
//
//    public static void dopost(int number, Map<String, String> params, LargeMappedFiles largeMappedFiles, CallBack callBack) {
//        System.out.println("begin send");
//
//        byte[] mbyte = largeMappedFiles.readFile(number);
//        URL url = null;
//        HttpURLConnection httpConn = null;
//        OutputStream output = null;
//        // DataOutputStream outr = null;
//        StringBuilder builder = new StringBuilder();
//        StringBuilder paramAppend = new StringBuilder();
//        paramAppend.append("http://192.168.0.131:8090/upload/media");
//        //  String s = "http://192.168.0.131:8090/upload/media?flowTotalSize=1666&flowChunkNumber=1&method=user.auth&flowFilename=5.txt&flowRelativePath=5.txt&flowIdentifier=95bb33de984e2b4bbec533d5&flowChunkSize=1666";
//        if (params != null) {
//            paramAppend.append("?");
//            Set<String> keySet = params.keySet();
//            Iterator<String> iterator = keySet.iterator();
//            while (iterator.hasNext()) {
//                String key = (String) iterator.next();
//                String value = (String) params.get(key);
//                paramAppend.append(key + "=" + value + "&");
//            }
//            paramAppend.deleteCharAt(paramAppend.length() - 1);
//        }
//        try {
//            url = new URL(paramAppend.toString());
//
//            httpConn = (HttpURLConnection) url.openConnection();
//            HttpURLConnection.setFollowRedirects(true);
//            httpConn.setDoOutput(true);
//            httpConn.setRequestMethod("POST");
//
//
//            httpConn.setRequestProperty("Content-Type", "text/xml");
//            httpConn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
//            httpConn.setRequestProperty("Accept-Encoding", "gzip, deflate");
//            httpConn.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
//            httpConn.setRequestProperty("Cache-Control", "no-cache");
//            httpConn.setRequestProperty("Connection", "keep-alive");
//            httpConn.setRequestProperty("Host", "nt1.libtop.com");
//            httpConn.setRequestProperty("Origin", "http://cnsl.libtop.com");
//            httpConn.setRequestProperty("Pragma", "no-cache");
//            httpConn.setRequestProperty("Referer", "http://cnsl.libtop.com/media/upload?aid=570c5d30984e4e35c060ebad");
//            httpConn.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Fedora; Linux x86_64; rv:41.0) Gecko/20100101 Firefox/41.0");
//
//            httpConn.connect();
//            output = httpConn.getOutputStream();
//            output.write(mbyte, 0, mbyte.length);
//            output.flush();
//            output.close();
//            //  outr = new DataOutputStream(output);
////            BufferedOutputStream a = new BufferedOutputStream(output);
////            a.write(mbyte, 0, mbyte.length);
////
//////            a.writeTo(output);
////            a.flush();
////            a.close();
//            // 写入请求参数
////            outr.write(mbyte, 0, mbyte.length);
////            outr.flush();
////            outr.close();
//            System.out.println("send ok");
//            int code = httpConn.getResponseCode();
//            System.out.println("code " + code);
//            System.out.println(httpConn.getResponseMessage());
//            java.io.InputStream is = httpConn.getInputStream();
//            BufferedReader reader = new BufferedReader(
//                    new InputStreamReader(is));
//            String a = reader.readLine();
//            Log.e("finished", "" + a);
//            if (a.contains("finished")) {
//                String bb = "";
//                return;
//            }
//            callBack.callback("999");
//            //读取响应内容
//            String sCurrentLine = "";
//            String sTotalString = "";
//            if (code == 200) {
//                while ((sCurrentLine = reader.readLine()) != null)
//                    if (sCurrentLine.length() > 0)
//                        sTotalString = sTotalString + sCurrentLine.trim();
//            } else {
//                sTotalString = "远程服务器连接失败,错误代码:" + code;
//
//            }
//            System.out.println("response:" + sTotalString);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void requestWithMap2(final String url, final Context context, final Map<String, Object> params,
//                                       final CallBack callBack) {
//        ThreadUtil.executorService.submit(new Runnable() {
//            @Override
//            public void run() {
//                final String result;
//                if (getKey(context)) {
//                    JSONObject jsonObj = new JSONObject(params);
//                    String a = jsonObj.toString();
//                    result = post2(url, jsonObj.toString(), null, callBack);
//                } else {
//                    result = "";
//                }
////                ThreadUtil.handler.postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
////                        if (CheckUtil.isNull(result)) {
////                            callBack.callback("");
////                        } else {
////                            callBack.callback(result);
////                        }
////                    }
////                }, 500);
//            }
//        });
//    }
//
//    public static void requestWithMap3(final String url, final Context context, final Map<String, String> params,
//                                       final CallBack callBack) {
//        ThreadUtil.executorService.submit(new Runnable() {
//            @Override
//            public void run() {
//                final String result;
//                if (getKey(context)) {
//                    JSONObject jsonObj = new JSONObject(params);
//                    String a = jsonObj.toString();
//                    result = get2(url, params,  callBack,context);
//                } else {
//                    result = "";
//                }
//            }
//        });
//    }
//
//    public static String post2(String url, String params, String file, final CallBack callBack) {
//        HttpClient client = new DefaultHttpClient();
//        StringBuilder builder = new StringBuilder();
//        HttpPost httprequest = new HttpPost(url);
//        HttpParams httpparams = client.getParams();
//        HttpConnectionParams.setConnectionTimeout(httpparams, REQUEST_MAX_TIME);
//        HttpConnectionParams.setSoTimeout(httpparams, RESPONSE_MAX_TIME);
//        List<NameValuePair> nameValue = new ArrayList<NameValuePair>();
//        if (params != null) {
//            nameValue.add(new BasicNameValuePair("text", params));
//        }
//        if (!CheckUtil.isNull(sid)) {
//            nameValue.add(new BasicNameValuePair("sid", sid));
//        }
//        // if (file != null) {
//        // nameValue.add(new BasicNameValuePair("file", file));
//        // }
//        try {
//            httprequest.setEntity(new UrlEncodedFormEntity(nameValue, "utf-8"));
//            HttpResponse response = client.execute(httprequest);
//            int status = response.getStatusLine().getStatusCode();
//            if (status == HttpStatus.SC_OK) {
//                String strResult = EntityUtils.toString(response.getEntity(),
//                        "UTF-8");
//                builder.append(strResult);
//                callBack.callback(builder.toString());
//            } else {
//                builder.append("null");
//                callBack.callback("0");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            builder.append("");
//            callBack.callback("0");
//        }
//        return builder.toString();
//    }
//
////    public static void requestWithMap3(final String url, final Context context, final Map<String, Object> params,
////                                       final CallBack callBack) {
////        ThreadUtil.executorService.submit(new Runnable() {
////            @Override
////            public void run() {
////                final String result;
////                String jsonresult = "";// 定义返回字符串
////                if (getKey(context)) {
////                    JSONObject jsonObj = new JSONObject(params);
////                    result = post2(url, jsonObj.toString(), null, callBack);
////                } else {
////                    result = "";
////                }
////                Iterator<Map.Entry<String, Object>> it = params.entrySet().iterator();
////
////
////                JSONObject object = new JSONObject();// 创建一个总的对象，这个对象对整个json串
////                try {
////                    JSONArray jsonarray = new JSONArray();// json数组，里面包含的内容为pet的所有对象
////                    while (it.hasNext()) {
////                        Map.Entry<String, Object> entry = it.next();
////                        if (entry.getKey().equals("label1")) {
////                            object.put(entry.getKey(), (Integer) entry.getValue());
////                        } else if (entry.getKey().equals("tags")) {
////                            String json = "[\"first\",\"second\"]";
////                            JSONArray jsonArray = (JSONArray) JSONSerializer.toJSON(json);
////                            object.put(entry.getKey(), (Integer) entry.getValue());
////                        } else {
////                            object.put(entry.getKey(), entry.getValue());
////                        }
////                    }
////
////                    object.put("startStrTime", starttime);
////                    object.put("endStrTime", endtime);
////                    object.put("length", distanceLong);
////                    object.put("duration", dur);
////                    object.put("remark", eventcon);
////                    object.put("walking_road", walking_road);
////                    object.put("walking_dur", walking_dur);
////                    object.put("driving_road", driving_road);
////                    object.put("driving_dur", driving_dur);
////                    object.put("grid_id", grid_id);
////                    object.put("event_id", eventid);
////                    object.put("locList", jsonarray);// 向总对象里面添加包含pet的数组
////                    jsonresult = object.toString();// 生成返回字符串
////                } catch (Exception e) {
////                    // TODO Auto-generated catch block
////                    e.printStackTrace();
////
////                }
////            }
////        });
////    }
//}