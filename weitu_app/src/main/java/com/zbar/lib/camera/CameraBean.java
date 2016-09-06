package com.zbar.lib.camera;

import android.graphics.Bitmap;


/**
 * Created by Administrator on 2016/4/8 0008.
 */
public class CameraBean
{

    private int count;
    private Bitmap bitmap;
    private String result;


    public int getCount()
    {
        return count;
    }


    public void setCount(int count)
    {
        this.count = count;
    }


    public Bitmap getBitmap()
    {
        return bitmap;
    }


    public void setBitmap(Bitmap bitmap)
    {
        this.bitmap = bitmap;
    }


    public String getResult()
    {
        return result;
    }


    public void setResult(String result)
    {
        this.result = result;
    }


}
