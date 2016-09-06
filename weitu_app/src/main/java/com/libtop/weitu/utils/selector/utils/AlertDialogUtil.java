package com.libtop.weitu.utils.selector.utils;

import android.content.Context;

import com.libtop.weitu.R;
import com.libtop.weitu.utils.selector.view.MyAlertDialog;


/**
 * <p>
 * Title: AlertDialogUtil.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * CreateTime：16/5/11
 * </p>
 *
 * @author 作者名
 * @version common v1.0
 */
public class AlertDialogUtil
{


    /**
     * @param context
     * @param messageId              提示的内容的资源id
     * @param neturalButtonId        确定按钮显示内容的资源id
     * @param negativeButtonId       取消按钮显示的内容资源id
     * @param positiveButtonListener 点击确定的按钮的事件
     * @param cancelListener         取消按钮的时间
     */
    public void showDialog(Context context, String messageId, String neturalButtonId, String negativeButtonId, MyAlertDialog.MyAlertDialogOnClickCallBack positiveButtonListener, MyAlertDialog.MyAlertDialogOnClickCallBack cancelListener)
    {
        MyAlertDialog dialog = new MyAlertDialog(context, R.style.DialogTheme);

        dialog.setMessage(messageId);

        dialog.setPositivieButtonContent(neturalButtonId);

        dialog.setNegtiveButtonContent(negativeButtonId);

        dialog.setPositionListener(positiveButtonListener);
        if (cancelListener == null)
        {
            dialog.setCancelListener(null);
        }
        else
        {
            dialog.setCancelListener(cancelListener);
        }

        dialog.show();
    }
}
