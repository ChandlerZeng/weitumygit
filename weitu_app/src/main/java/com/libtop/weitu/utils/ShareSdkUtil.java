package com.libtop.weitu.utils;

import android.content.Context;
import android.text.TextUtils;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;


/**
 * Created by LianTu on 2016/7/1.
 */
public class ShareSdkUtil
{

    public static void showShareWithLocalImg(Context context, String title, String content, String localImgPath)
    {
        ShareSdkUtil.showShare(context, title, content, localImgPath, null);
    }


    public static void showShare(Context context, String title, String content, String imgPath)
    {
        ShareSdkUtil.showShare(context, title, content, null, imgPath);
    }


    public static void showShare(Context context, String title, String content, String localImgPath, String imgPath)
    {
        ShareSDK.initSDK(context);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(title);
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://yuntu.io");
        // text是分享文本，所有平台都需要这个字段
        oks.setText(content);
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        if (!TextUtils.isEmpty(localImgPath))
        {
            oks.setImagePath(localImgPath);//确保SDcard下面存在此张图片
        }
        /** imageUrl是图片的网络路径，新浪微博、人人网、QQ空间和Linked-In支持此字段 */
        oks.setImageUrl(imgPath);
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://www.yuntu.io/");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        //        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite("微图");
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://www.yuntu.io/");

        // 启动分享GUI
        oks.show(context);
    }
}
