package com.libtop.weitu.base.impl;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import com.libtop.weitu.base.BaseFragment;
import com.libtop.weitu.utils.SdCardUtil;


/**
 * Created by Administrator on 2016/1/11 0011.
 */
public abstract class PhotoFragment extends BaseFragment
{
    protected static final int REQUEST_CODE_PHOTO_DEAL = 0x0003;



    /*
     * 对图片进行剪裁，通过Intent来调用系统自带的图片剪裁API
	 */
    protected void cropPhoto(Uri uri)
    {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
		/* aspectX aspectY 是裁剪后图片的宽高比*/
        intent.putExtra("aspectX", 5);
        intent.putExtra("aspectY", 5);
		/* outputX outputY 是裁剪图片宽度 256 256*/
        intent.putExtra("outputX", 60);
        intent.putExtra("outputY", 60);
        intent.putExtra("noFaceDetection", true);// 关闭人脸�?��
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse(SdCardUtil.TEMP));
        mContext.startActivityForResult(intent, REQUEST_CODE_PHOTO_DEAL);
    }
}
