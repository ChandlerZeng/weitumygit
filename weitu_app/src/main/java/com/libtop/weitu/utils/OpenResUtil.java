package com.libtop.weitu.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.search.BookDetailFragment;
import com.libtop.weitu.activity.search.VideoPlayActivity2;
import com.libtop.weitu.activity.search.dto.SearchResult;
import com.libtop.weitu.activity.search.dynamicCardLayout.DynamicCardActivity;
import com.libtop.weitu.activity.source.AudioPlayActivity2;
import com.libtop.weitu.activity.source.PdfActivity2;
import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.tool.Preference;


/**
 * Created by LianTu on 2016-9-18.
 */
public class OpenResUtil
{

    public static final int VIDEO = 0, AUDIO = 1, DOC = 4, PHOTO = 2, BOOK = 3;

    public static void startByType(Context context,int type, String id,boolean isFragIsBack)
    {
        switch (type)
        {
            case BOOK:
                openBook(context,id,isFragIsBack);
                break;
            case VIDEO:
                openVideo(context,id);
                break;
            case AUDIO:
                openAudio(context,id);
                break;
            case DOC:
                openDoc(context,id);
                break;
            case PHOTO:
                openPhoto(context,id);
                break;
        }
    }

    public static void startByType(Context context,int type, String id)
    {
        startByType(context,type,id,false);
    }

    public static void openVideo(Context context,String id)
    {
        SearchResult result = new SearchResult();
//        result.id = id;
        result.id = "54d46b13e4b0b5810235a93b";
        Intent intent = new Intent(context, VideoPlayActivity2.class);
        intent.putExtra("resultBean", new Gson().toJson(result));
        context.startActivity(intent);
    }

    public static void openAudio(Context context,String id)
    {
        SearchResult result = new SearchResult();
//        result.id = id;
        result.id = "54d5a0d1e4b0fcffa69e9f3d";
        result.cover = "";
        Intent intent = new Intent(context, AudioPlayActivity2.class);
        intent.putExtra("resultBean", new Gson().toJson(result));
        context.startActivity(intent);
    }

    public static void openBook(Context context,String isbn,boolean isFragIsBack)
    {
        Bundle bundle = new Bundle();
        bundle.putString("name", "");
        bundle.putString("cover", "");
        bundle.putString("auth", "");
//        bundle.putString("isbn", isbn);
        bundle.putString("isbn", "9787505374973");
        bundle.putString("publisher", "");
        bundle.putString("school", Preference.instance(context).getString(Preference.SchoolCode));
        bundle.putBoolean(BookDetailFragment.ISFROMMAINPAGE, true);
        bundle.putBoolean(ContentActivity.FRAG_ISBACK, isFragIsBack);
        bundle.putString(ContentActivity.FRAG_CLS, BookDetailFragment.class.getName());
        ((BaseActivity)context).startActivity(bundle, ContentActivity.class);
    }

    public static void openPhoto(Context context,String id)
    {
        Bundle bundle = new Bundle();
        bundle.putString("type", "img");
//        bundle.putString("id", id);
        bundle.putString("id", "56178242e4b088b55663c299");
        ((BaseActivity)context).startActivity(bundle, DynamicCardActivity.class);
    }


    public static void openDoc(Context context,String id)
    {
        Intent intent = new Intent();
        intent.putExtra("url", "");
//        intent.putExtra("doc_id",id);
        intent.putExtra("doc_id","55165d0ce4b03cc84f8238f5");
        intent.setClass(context, PdfActivity2.class);
        context.startActivity(intent);
    }
}
