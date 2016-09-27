package com.libtop.weitu.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class JSONUtil
{
    private static final String TAG = JSONUtil.class.getSimpleName();


    private static Boolean getBoolean(String json, String name)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.getBoolean(name);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            LogUtil.e(TAG, e.toString());
        }

        return null;
    }


    public static Double getDouble(String json, String name)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.getDouble(name);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            LogUtil.e(TAG, e.toString());
        }

        return null;
    }


    public static Integer getInt(String json, String name)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.getInt(name);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            LogUtil.e(TAG, e.toString());
        }

        return null;
    }


    public static Long getLong(String json, String name)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.getLong(name);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            LogUtil.e(TAG, e.toString());
        }

        return null;
    }


    public static String getString(String json, String name)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(json);
            return jsonObject.getString(name);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            LogUtil.e(TAG, e.toString());
        }

        return null;
    }


    /**
     * 将JSON数据转换为实体类对象
     *
     * @param jsonObject 原始JSONObject对象
     * @param name       待转换的对象对应的JSONObject键名
     * @param clazz      实体类的类名.需实现 Serializable 接口
     * @return T 实体类对象
     */
    public static <T> T readBean(JSONObject jsonObject, String name, Class<T> clazz)
    {
        String json = null;
        try
        {
            json = jsonObject.getJSONObject(name).toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LogUtil.e(TAG, e.toString());

            return null;
        }

        return readBean(json, clazz);
    }


    /**
     * 将JSON数据转换为实体类对象
     *
     * @param json  待转换的JSON数据
     * @param clazz 实体类的类名.需实现 Serializable 接口
     * @return T 实体类对象
     */
    public static <T> T readBean(String json, Class<T> clazz)
    {
        try
        {
            ObjectMapper objectMapper = createObjectMapper();
            T t = objectMapper.readValue(json, clazz);

            return t;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LogUtil.e(TAG, e.toString());
        }

        return null;
    }


    /**
     * 将JSON数据转换为实体类，以实体类对象数组的形式返回
     *
     * @param jsonObject 原始JSONObject对象
     * @param name       待转换的数组对象对应的JSONObject键名
     * @param clazz      实体类的类名.需实现 Serializable 接口
     * @return ArrayList&lt;T&gt; 实体类对象数组
     */
    public static <T> ArrayList<T> readBeanArray(JSONObject jsonObject, String name, Class<T> clazz)
    {
        JSONArray array = null;
        try
        {
            array = jsonObject.getJSONArray(name);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LogUtil.e(TAG, e.toString());

            return null;
        }

        return readBeanArray(array, clazz);
    }


    /**
     * 将JSON数据转换为实体类，以实体类对象数组的形式返回
     *
     * @param jsonArray 待转换的JSON数据
     * @param clazz     实体类的类名.需实现 Serializable 接口
     * @return ArrayList&lt;T&gt; 实体类对象数组
     */
    public static <T> ArrayList<T> readBeanArray(String jsonArray, Class<T> clazz)
    {
        JSONArray array = null;
        try
        {
            array = new JSONArray(jsonArray);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LogUtil.e(TAG, e.toString());

            return null;
        }

        return readBeanArray(array, clazz);
    }


    /**
     * 将JSON数据转换为实体类，以实体类对象数组的形式返回
     *
     * @param array 待转换的JSONArray数据
     * @param clazz 实体类的类名.需实现 Serializable 接口
     * @return ArrayList&lt;T&gt; 实体类对象数组
     */
    public static <T> ArrayList<T> readBeanArray(JSONArray array, Class<T> clazz)
    {
        try
        {
            int length = array.length();
            ArrayList<T> list = new ArrayList<T>(length);

            ObjectMapper objectMapper = createObjectMapper();
            for (int i = 0; i < length; i++)
            {
                T t = objectMapper.readValue(array.getString(i), clazz);
                if (t != null)
                {
                    list.add(t);
                }
            }

            return list;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LogUtil.e(TAG, e.toString());
        }

        return null;
    }


    /**
     * 将实体对象转换为JSON格式数据
     *
     * @param object 要转换的实体对象
     * @return String JSON格式数据
     */
    public static String writeBeanToJSON(Object object)
    {
        try
        {
            ObjectMapper objectMapper = createObjectMapper();
            String json = objectMapper.writeValueAsString(object);

            return json;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LogUtil.d(TAG, e.toString());
        }

        return null;
    }


    private static ObjectMapper createObjectMapper()
    {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // 设置忽略未知的属性

        return objectMapper;
    }
}
