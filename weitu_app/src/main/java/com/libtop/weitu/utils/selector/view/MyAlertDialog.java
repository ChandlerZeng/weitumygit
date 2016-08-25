package com.libtop.weitu.utils.selector.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.libtop.weitu.R;

/**
 * <p>
 * Title: MyAlertDialog.java
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
public class MyAlertDialog extends Dialog {
    private Context context;
    TextView alertdialog_title_tv, alertdialog_message_tv;
    Button alertdialog_position_button_tv, alertdialog_cancel_button_tv;
    View view, my_alertdialog_dividerline;
    RelativeLayout alertdialog_layout_container;
    private MyAlertDialog dd;

    public static interface MyAlertDialogOnClickCallBack {
        public abstract void onClick();
    }

    public MyAlertDialog(Context context) {
        super(context);
        init(context);
    }

    public MyAlertDialog(Context context, int theme) {
        super(context, theme);
        init(context);
    }

    private void init(Context context) {
        dd = this;
        setMyAlertDialogCancelable(false);
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.my_dialog, null);
        this.setContentView(view);
        alertdialog_layout_container = (RelativeLayout) view.findViewById(R.id.alertdialog_layout_container);
        alertdialog_title_tv = (TextView) view.findViewById(R.id.alertdialog_title_tv);
        alertdialog_message_tv = (TextView) view.findViewById(R.id.alertdialog_message_tv);
        alertdialog_position_button_tv = (Button) view.findViewById(R.id.alertdialog_position_button_tv);
        my_alertdialog_dividerline = view.findViewById(R.id.my_alertdialog_dividerline);
        alertdialog_cancel_button_tv = (Button) view.findViewById(R.id.alertdialog_cancel_button_tv);
        alertdialog_cancel_button_tv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dd.dismiss();
            }
        });
        alertdialog_position_button_tv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dd.dismiss();
            }
        });
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public void setMessageGravity(int gravity) {
        alertdialog_message_tv.setGravity(gravity);
    }


    public void setPositivieButtonContent(String content) {
        alertdialog_position_button_tv.setText(content);
    }


    public void setNegtiveButtonContent(String content) {
        alertdialog_cancel_button_tv.setText(content);
    }

    public void setCancelListener(final MyAlertDialogOnClickCallBack callBack) {
        if (alertdialog_cancel_button_tv.getVisibility() != View.VISIBLE) {
            alertdialog_cancel_button_tv.setVisibility(View.VISIBLE);
            my_alertdialog_dividerline.setVisibility(View.VISIBLE);
        }
        alertdialog_cancel_button_tv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (dd != null)
                    dd.dismiss();
                if (callBack != null)
                    callBack.onClick();
            }
        });


    }

    /**
     * 设置自己的view
     *
     * @param view     新添加的view
     * @param isRemove 是否移除原来的view
     */
    public void addMyView(View view, boolean isRemove) {
        if (isRemove) {
            alertdialog_layout_container.removeAllViews();
        }
        alertdialog_layout_container.addView(view);
    }

    public void setPositionListener(final MyAlertDialogOnClickCallBack callBack) {
        if (alertdialog_position_button_tv.getVisibility() != View.VISIBLE) {
            alertdialog_position_button_tv.setVisibility(View.VISIBLE);
        }
        alertdialog_position_button_tv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (dd != null)
                    dd.dismiss();
                if (callBack != null)
                    callBack.onClick();
            }
        });
    }


    public MyAlertDialog setTitle(String title) {

        if (!TextUtils.isEmpty(title)) {
            if (alertdialog_title_tv.getVisibility() != View.VISIBLE) {
                alertdialog_title_tv.setVisibility(View.VISIBLE);
            }
            alertdialog_title_tv.setText(title);
        }
        return this;
    }


    public MyAlertDialog setMessage(String messageContent) {
        if (!TextUtils.isEmpty(messageContent)) {
            alertdialog_message_tv.setText(messageContent);
        }
        return this;
    }


    /**
     * 1、AlertDialog  捕获返回键的处理，使用setOnKeyListener事件可以。
     * 2、AlertDialog  setView自定义的布局，去除上下左右的距离时使用的是：
     * builder.setView(shareView, 0, 0, 0, 0)，
     *
     * @param
     * @return
     */
    public MyAlertDialog setMyAlertDialogCancelable(boolean cancelable) {
        this.setCancelable(cancelable);   // 使返回键失效


        this.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                return true;//键盘事件无效。
            }

            // 使搜索键失效
//			public boolean onKey(DialogInterface dialog,
//                    int keyCode, KeyEvent event) {
//                if (keyCode == KeyEvent.KEYCODE_SEARCH) {
//                    return true;
//                } else {
//                    return false; // 默认返回 false
//                }
//            }
        });
        return this;
    }

    public void show() {
        super.show();
        Activity owner = (Activity) context;
        if (owner == null)
            return;
        DisplayMetrics dm = new DisplayMetrics();
        owner.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
//      Display localDisplay = owner.getWindowManager().getDefaultDisplay();
//      localLayoutParams.width = (int)(0.8D * localDisplay.getWidth());
        localLayoutParams.width = (int) (0.8D * screenWidth);
        localLayoutParams.height = -2;
        getWindow().setAttributes(localLayoutParams);
    }


}