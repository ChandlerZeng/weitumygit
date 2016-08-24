package com.libtop.weituR.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.libtop.weitu.R;

/**
 * Created by LianTu on 2016/5/12.
 */
public class AlertDialogSingle extends AlertDialog {
    Button cancelBtn;
    TextView contentView;
    private CallBack callBack;
    private String content;

    public AlertDialogSingle(Context context, String content) {
        super(context,content);
        setContentView(R.layout.dialog_alert_single);
        this.setCanceledOnTouchOutside(true);
        this.content = content;
        initDialog();
    }

    private void initDialog() {
        cancelBtn = (Button) findViewById(R.id.cancel_btn);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.cancel();
                dismiss();
            }
        });
        contentView = (TextView) findViewById(R.id.dialog_operate_content);
        contentView.setText(content);
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }


    public interface CallBack {
        void cancel();
    }

    public void setContent(String content) {
        contentView.setText(content);
    }
}
