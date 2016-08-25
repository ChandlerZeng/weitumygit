package com.libtop.weitu.base.impl;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.utils.SdCardUtil;

/**
 * Created by Administrator on 2016/1/7 0007.
 */
public class PhotoActivity extends BaseActivity {
    protected static final int REQUEST_CODE_CAMERA =0x0001;
    protected static final int REQUEST_CODE_PHOTO = 0x0002;
    protected static final int REQUEST_CODE_PHOTO_DEAL = 0x0003;

    /**
     * 打开相机
     */
    protected void openCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse(SdCardUtil.TEMP));
        startActivityForResult(intent, REQUEST_CODE_CAMERA);
    }

    /**
     * 打开照片选择
     */
    protected void pickUpPhoto(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CODE_PHOTO);
    }

    /*
	 * 对图片进行剪裁，通过Intent来调用系统自带的图片剪裁API
	 */
    protected void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
		/* aspectX aspectY 是裁剪后图片的宽高比*/
        intent.putExtra("aspectX", 5);
        intent.putExtra("aspectY", 5);
		/* outputX outputY 是裁剪图片宽度*/
        intent.putExtra("outputX", 256);
        intent.putExtra("outputY", 256);
        intent.putExtra("noFaceDetection", true);// 关闭人脸�?��
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse(SdCardUtil.TEMP));
        startActivityForResult(intent, REQUEST_CODE_PHOTO_DEAL);
    }

}
