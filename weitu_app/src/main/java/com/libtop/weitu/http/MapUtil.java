package com.libtop.weitu.http;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by LianTu on 2016/7/11.
 */
public class MapUtil {

    public static String[] map2Parameter(Map<String,Object> map){
        JSONObject jsonObject = new JSONObject(map);
        String requestJson = jsonObject.toString();

        String method = (String) map.get("method");
        String[] methods = method.split("\\.");
        ArrayList<String> lists = new ArrayList<>();
        lists.addAll(Arrays.asList(methods));
        lists.add(requestJson);
        return  lists.toArray(new String[3]);
    }
}
