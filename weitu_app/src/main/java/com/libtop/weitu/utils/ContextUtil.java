package com.libtop.weitu.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.comment.CommentDetailActivity;
import com.libtop.weitu.activity.main.SubjectDetailActivity;
import com.libtop.weitu.activity.notice.NoticeContentFragment;
import com.libtop.weitu.activity.search.BookDetailFragment;
import com.libtop.weitu.activity.search.VideoPlayActivity2;
import com.libtop.weitu.activity.search.dto.SearchResult;
import com.libtop.weitu.activity.search.dynamicCardLayout.DynamicCardActivity;
import com.libtop.weitu.activity.source.AudioPlayActivity2;
import com.libtop.weitu.activity.source.PdfActivity2;
import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.tool.Preference;


/**
 * @author Sai
 * @ClassName: ContextUtil
 * @Description: 上下文跳转帮助类
 * @date 9/17/16 20:20
 */
public class ContextUtil
{

    //真接口
    public static final int VIDEO = 1, AUDIO = 2, DOC = 3, PHOTO = 4, BOOK = 5, SUBJECT = 7;
    //假接口
//    public static final int VIDEO = 0, AUDIO = 1, DOC = 4, PHOTO = 2, BOOK = 3;


    public static void doTest(Context context)
    {
        //TODO for test
    }


    // 查看评论详情
    public static void readCommentDetail(Context context, String cid)
    {
        //TODO 待修改为直接使用 intent 传值

        Bundle bundle = new Bundle();
        bundle.putString("cid", cid);

        Intent intent = new Intent(context, CommentDetailActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


    // 查看校内通知详情
    public static void readSchoolNoticeDetail(Context context, String id, String title, long dateLine)
    {
        //TODO 待修改传值协议

        Bundle bundle = new Bundle();
        bundle.putString(ContentActivity.FRAG_CLS, NoticeContentFragment.class.getName());
        bundle.putBoolean(ContentActivity.FRAG_WITH_ANIM, true);
        bundle.putBoolean(ContentActivity.FRAG_ISBACK, false);
        bundle.putString("id", id);
        bundle.putString("title", title);
        bundle.putString("date", DateUtil.parseToDate(dateLine));

        Intent intent = new Intent(context, ContentActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


    //通过类型打开视频，文档，音频，图库，isFragIsBack 图书在Fragment中是否返回上一个Fragment
    public static void openResourceByType(Context context,int type, String id,boolean isFragIsBack)
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


    public static void openSubjectDetail(Context context,String id){
        Intent intent = new Intent(context, SubjectDetailActivity.class);
        intent.putExtra("id",id);
        context.startActivity(intent);
    }


    //通过类型打开视频，文档，音频，图库
    public static void openResourceByType(Context context,int type, String id)
    {
        openResourceByType(context,type,id,false);
    }


    //打开视频
    public static void openVideo(Context context,String id)
    {
        SearchResult result = new SearchResult();
        result.id = id;
//        result.id = "54d46b13e4b0b5810235a93b"; // TODO: 假数据，写死
        Intent intent = new Intent(context, VideoPlayActivity2.class);
        intent.putExtra("resultBean", new Gson().toJson(result));
        context.startActivity(intent);
    }


    //打开音频
    public static void openAudio(Context context,String id)
    {
        SearchResult result = new SearchResult();
        result.id = id;
//        result.id = "54d5a0d1e4b0fcffa69e9f3d";  // TODO: 假数据，写死
        result.cover = "";
        Intent intent = new Intent(context, AudioPlayActivity2.class);
        intent.putExtra("resultBean", new Gson().toJson(result));
        context.startActivity(intent);
    }


    //打开图书
    public static void openBook(Context context,String isbn,boolean isFragIsBack)
    {
        Bundle bundle = new Bundle();
        bundle.putString("name", "");
        bundle.putString("cover", "");
        bundle.putString("auth", "");
        bundle.putString("isbn", isbn);
//        bundle.putString("isbn", "9787505374973"); // TODO: 假数据，写死
        bundle.putString("publisher", "");
        bundle.putString("school", Preference.instance(context).getString(Preference.SchoolCode));
        bundle.putBoolean(BookDetailFragment.ISFROMMAINPAGE, true);
        bundle.putBoolean(ContentActivity.FRAG_ISBACK, isFragIsBack);
        bundle.putString(ContentActivity.FRAG_CLS, BookDetailFragment.class.getName());
        ((BaseActivity)context).startActivity(bundle, ContentActivity.class);
    }


    //打开图库
    public static void openPhoto(Context context,String id)
    {
        Bundle bundle = new Bundle();
        bundle.putString("type", "img");
        bundle.putString("id", id);
//        bundle.putString("id", "56178242e4b088b55663c299"); // TODO: 假数据，写死
        ((BaseActivity)context).startActivity(bundle, DynamicCardActivity.class);
    }


    //打开文档
    public static void openDoc(Context context,String id)
    {
        Intent intent = new Intent();
        intent.putExtra("url", "");
        intent.putExtra("doc_id",id);
//        intent.putExtra("doc_id","55165d0ce4b03cc84f8238f5"); // TODO: 假数据，写死
        intent.setClass(context, PdfActivity2.class);
        context.startActivity(intent);
    }
}
