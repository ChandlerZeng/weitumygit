package com.libtop.weitu.http;

import android.content.Context;

import com.libtop.weitu.application.AppApplication;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.AESUtils;
import com.libtop.weitu.utils.ArrayUtils;
import com.libtop.weitu.utils.CheckUtil;
import com.libtop.weitu.utils.ContantsUtil;
import com.libtop.weitu.utils.RSAUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.request.RequestCall;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;


/**
 * Created by LianTu on 2016/7/29.
 */
public class HttpRequest
{

    private static String sid;
    public static String uid;
    private static byte[] aesKey;
    private static String privateKey = "";
    private static String publicKey = "";


    /**
     * 初始化参数
     *
     * @param context
     */
    public static void initParams(Context context)
    {
        sid = Preference.instance(context).getString(Preference.SID);
        uid = Preference.instance(context).getString(Preference.uid);
        aesKey = ArrayUtils.hexToByte(Preference.instance(context).getString(Preference.AESKEY));
    }


    public static void reset()
    {
        sid = null;
        uid = null;
        aesKey = null;
    }


    public static RequestCall loadWithMap(Map<String, Object> map)
    {
        String[] arrays = MapUtil.map2Parameter(map);
        return OkHttpUtils.get().url(ContantsUtil.HOST + "/" + arrays[0] + "/" + arrays[1]).addParams("text", arrays[2]).build();
    }


    public static void loadWithMapSec(final Map<String, Object> map, final CallBackSec callBackSec)
    {
        getKey(AppApplication.getContext(), new HttpRequest.CallBack1()
        {
            @Override
            public void callback(boolean isSucess)
            {
                if (isSucess)
                {
                    HttpRequest.loadSec(map).execute(new StringCallback()
                    {
                        @Override
                        public void onError(Call call, Exception e, int id)
                        {
                            callBackSec.onError(call, e, id);
                        }


                        @Override
                        public void onResponse(String response, int id)
                        {
                            callBackSec.onResponse(response, id);
                        }
                    });
                }
                else
                {
                    callBackSec.onError(null, new Exception("请求AES的Key出错"), 0);
                }
            }
        });
    }


    private static RequestCall loadSec(Map<String, Object> map)
    {
        JSONObject jsonObject = new JSONObject(map);
        String requestJson = jsonObject.toString();
        String encode = AESUtils.encrypt(aesKey, requestJson);
        return OkHttpUtils.post().url(ContantsUtil.HOST).addParams("text", encode).addParams("sid", sid).build();
    }


    /**
     * 请求HTTP服务后的回调函数接口
     *
     * @author longbh
     */
    private interface CallBack1
    {
        public void callback(boolean isSucess);
    }


    public interface CallBackSec
    {
        public void onError(Call call, Exception e, int id);

        public void onResponse(String response, int id);
    }


    private static void getKey(final Context context, final CallBack1 callBackSec)
    {
        if (!CheckUtil.isNull(sid))
        {
            callBackSec.callback(true);
            return;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("method", "keymap.getKey");
        Map<String, BigInteger> keys = RSAUtils.generateKey();
        privateKey = keys.get("privateExponent").toString(36);
        publicKey = keys.get("publicModulus").toString(36);
        params.put("key", publicKey); // 传递公匙到服务器
        JSONObject jsonObj = new JSONObject(params);
        String jsonStr = jsonObj.toString();
        OkHttpUtils.post().url(ContantsUtil.HOST)//
                .addParams("text", jsonStr).build()//
                .execute(new StringCallback()
                {
                    @Override
                    public void onError(Call call, Exception e, int id)
                    {
                        callBackSec.callback(false);
                    }


                    @Override
                    public void onResponse(String result, int id)
                    {
                        if (CheckUtil.isNull(result))
                        {
                            return;
                        }
                        else
                        {
                            try
                            {
                                JSONObject preJson = new JSONObject(result);
                                String decodeTxt = RSAUtils.decrypt(preJson.getString("cipher"), publicKey, privateKey);
                                JSONObject resultJson = new JSONObject(decodeTxt);
                                sid = resultJson.getString("sid");
                                String keyString = resultJson.getString("key");
                                Preference.instance(context).putString(Preference.SID, sid);
                                aesKey = ArrayUtils.hexToByte(keyString);
                                Preference.instance(context).putString(Preference.AESKEY, keyString);
                                callBackSec.callback(true);
                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }


}
