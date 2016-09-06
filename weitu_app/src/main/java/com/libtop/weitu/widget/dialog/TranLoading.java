package com.libtop.weitu.widget.dialog;

import android.app.Dialog;
import android.content.Context;

import com.libtop.weitu.R;


public class TranLoading extends Dialog
{

    public TranLoading(Context context)
    {
        super(context, R.style.TranDialog);
        setContentView(R.layout.dialog_tran_loading);
        setCanceledOnTouchOutside(false);
    }

}
