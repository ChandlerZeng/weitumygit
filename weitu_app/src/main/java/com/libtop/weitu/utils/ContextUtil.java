package com.libtop.weitu.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.google.gson.Gson;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.comment.CommentDetailActivity;
import com.libtop.weitu.activity.main.SubjectDetailActivity;
import com.libtop.weitu.activity.main.dto.ResourceBean;
import com.libtop.weitu.activity.notice.NoticeContentFragment;
import com.libtop.weitu.activity.search.BookDetailFragment;
import com.libtop.weitu.activity.search.VideoPlayActivity2;
import com.libtop.weitu.activity.search.dto.SearchResult;
import com.libtop.weitu.activity.search.dynamicCardLayout.DynamicCardActivity;
import com.libtop.weitu.activity.source.AudioPlayActivity2;
import com.libtop.weitu.activity.source.PdfActivity2;
import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.utils.selector.MultiImageSelectorActivity;


/**
 * @author Sai
 * @ClassName: ContextUtil
 * @Description: 上下文跳转帮助类
 * @date 9/17/16 20:20
 */
public class ContextUtil
{
    /**
     * 实体类型: 视频 1
     */
    public static final int ENTITY_TYPE_VIDEO = 1;
    /**
     * 实体类型: 音频 2
     */
    public static final int ENTITY_TYPE_AUDIO = 2;
    /**
     * 实体类型: 文档 3
     */
    public static final int ENTITY_TYPE_DOC = 3;
    /**
     * 实体类型: 图片 4
     */
    public static final int ENTITY_TYPE_PHOTO = 4;
    /**
     * 实体类型: 图书 5
     */
    public static final int ENTITY_TYPE_BOOK = 5;
    /**
     * 实体类型: 主题 7
     */
    public static final int ENTITY_TYPE_SUBJECT = 7;


    /**
     * 选择图片
     *
     * @param activity
     * @param showCamera    是否显示相机
     * @param selectCount   最大图片选择张数
     * @param selectMode    图片选择模式[0: 单选; 1: 多选]
     * @param requestCode   请求码
     */
    public static void chooseImage(Activity activity, boolean showCamera, int selectCount, int selectMode, int requestCode)
    {
        Intent intent = new Intent(activity, MultiImageSelectorActivity.class);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, showCamera);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, selectCount);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, selectMode);
        activity.startActivityForResult(intent, requestCode);
    }


    /**
     * 对uri对应的图片文件进行裁剪
     *
     * @param activity
     * @param inputUri    输入文件Uri(图片文件来源于该uri)
     * @param outputUri   输出文件Uri(裁剪结果也写入到该uri)
     * @param aspectX     X方向上的比例
     * @param aspectY     Y方向上的比例
     * @param outputX     裁剪区的宽
     * @param outputY     裁剪区的高
     * @param requestCode 是否将数据保留在Bitmap中返回
     * @param requestCode
     */
    public static void cropImage(Activity activity, Uri inputUri, Uri outputUri, int aspectX, int aspectY, int outputX, int outputY, int requestCode)
    {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(inputUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        activity.startActivityForResult(intent, requestCode);
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


    public static void openResource(Context context, ResourceBean resourceBean, boolean isFragIsBack)
    {
        int type = getResourceType(resourceBean.getEntityType());
        switch (type)
        {
            case ENTITY_TYPE_BOOK:
                openResourceByType(context, type, resourceBean.getIsbn(), isFragIsBack);
                break;

            default:
                openResourceByType(context, type, resourceBean.getId());
                break;
        }
    }


    // 通过类型打开资源(视频，音频, 文档，图库, 图书)
    public static void openResourceByType(Context context, int type, String id)
    {
        openResourceByType(context, type, id, false);
    }


    //通过类型打开视频，文档，音频，图库，isFragIsBack 图书在Fragment中是否返回上一个Fragment
    public static void openResourceByType(Context context, int type, String id, boolean isFragIsBack)
    {
        switch (type)
        {
            case ENTITY_TYPE_BOOK:
                openBook(context, id, isFragIsBack);
                break;

            case ENTITY_TYPE_VIDEO:
                openVideo(context, id);
                break;

            case ENTITY_TYPE_AUDIO:
                openAudio(context, id);
                break;

            case ENTITY_TYPE_DOC:
                openDoc(context, id);
                break;

            case ENTITY_TYPE_PHOTO:
                openPhoto(context, id);
                break;

            default:
                LogUtil.d("openResourceByType", "未支持的资源类型, 无法打开");
                break;
        }
    }


    // 打开视频
    public static void openVideo(Context context, String id)
    {
        SearchResult result = new SearchResult();
        result.id = id;

        Intent intent = new Intent(context, VideoPlayActivity2.class);
        intent.putExtra("resultBean", new Gson().toJson(result));

        context.startActivity(intent);
    }


    // 打开音频
    public static void openAudio(Context context, String id)
    {
        SearchResult result = new SearchResult();
        result.id = id;
        result.cover = "";

        Intent intent = new Intent(context, AudioPlayActivity2.class);
        intent.putExtra("resultBean", new Gson().toJson(result));

        context.startActivity(intent);
    }


    // 打开文档
    public static void openDoc(Context context, String id)
    {
        Intent intent = new Intent(context, PdfActivity2.class);
        intent.putExtra("url", "");
        intent.putExtra("doc_id", id);

        context.startActivity(intent);
    }


    // 打开图书
    public static void openBook(Context context, String isbn, boolean isFragIsBack)
    {
        Bundle bundle = new Bundle();
        bundle.putString("name", "");
        bundle.putString("cover", "");
        bundle.putString("auth", "");
        bundle.putString("isbn", isbn);
        bundle.putString("publisher", "");
        bundle.putString("school", Preference.instance(context).getString(Preference.SchoolCode));
        bundle.putBoolean(BookDetailFragment.ISFROMMAINPAGE, true);
        bundle.putBoolean(ContentActivity.FRAG_ISBACK, isFragIsBack);
        bundle.putString(ContentActivity.FRAG_CLS, BookDetailFragment.class.getName());

        ((BaseActivity) context).startActivity(bundle, ContentActivity.class);
    }


    // 打开图库
    public static void openPhoto(Context context, String id)
    {
        Bundle bundle = new Bundle();
        bundle.putString("type", "img");
        bundle.putString("id", id);

        ((BaseActivity) context).startActivity(bundle, DynamicCardActivity.class);
    }


    // 打开主题详情
    public static void openSubjectDetail(Context context, String id)
    {
        Intent intent = new Intent(context, SubjectDetailActivity.class);
        intent.putExtra("id", id);

        context.startActivity(intent);
    }


    // 注销广播接收器
    public static void unregisterReceiver(Context context, BroadcastReceiver receiver)
    {
        if (context != null && receiver != null)
        {
            context.unregisterReceiver(receiver);
        }
    }


    public static int getResourceType(String entityType)
    {
        switch (entityType)
        {
            case "video-album":
                return ENTITY_TYPE_VIDEO;

            case "audio-album":
                return ENTITY_TYPE_AUDIO;

            case "document":
                return ENTITY_TYPE_DOC;

            case "book":
                return ENTITY_TYPE_BOOK;

            default:
                return ENTITY_TYPE_PHOTO;  //TODO should do test
        }
    }
}
