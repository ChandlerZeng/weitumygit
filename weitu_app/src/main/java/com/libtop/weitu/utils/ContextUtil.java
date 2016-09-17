package com.libtop.weitu.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.comment.CommentDetailActivity;
import com.libtop.weitu.activity.main.SubjectDetailActivity;
import com.libtop.weitu.activity.main.notice.NoticeContentFragment;


/**
 * @author Sai
 * @ClassName: ContextUtil
 * @Description: 上下文跳转帮助类
 * @date 9/17/16 20:20
 */
public class ContextUtil
{
    public static void doTest(Context context)
    {
        //TDOO for test
    }


    // 查看主题详情
    public static void readSubjectDetail(Context context)
    {
        Intent intent = new Intent(context, SubjectDetailActivity.class);
        context.startActivity(intent);
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
    public static void readSchoolNoticeDetail(Context context, String id, String title, long createTime)
    {
        //TODO 待修改传值协议

        Bundle bundle = new Bundle();
        bundle.putString(ContentActivity.FRAG_CLS, NoticeContentFragment.class.getName());
        bundle.putBoolean(ContentActivity.FRAG_WITH_ANIM, true);
        bundle.putBoolean(ContentActivity.FRAG_ISBACK, false);
        bundle.putString("id", "577f47ebe4b0e74f06d27007");  //TODO 用于测试, 写死ID
        bundle.putString("title", title);
        bundle.putString("date", DateUtil.parseToDate(createTime));

        Intent intent = new Intent(context, ContentActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
