package com.libtop.weitu.widget.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.libtop.weitu.R;


/**
 * 相册、拍照选择弹出框
 *
 * @author Administrator
 */
public class PhotoPickup extends Dialog implements OnClickListener
{

    private ClickListener alertDo;
    RelativeLayout bg;


    public PhotoPickup(Context context)
    {
        super(context, R.style.Dialog);
        this.setCanceledOnTouchOutside(true);
        setContentView(R.layout.dialog_photo_pickup);
        initDialog();
    }


    private void initDialog()
    {
        Button camera = (Button) findViewById(R.id.btn_take_photo);
        camera.setOnClickListener(this);
        Button photo = (Button) findViewById(R.id.btn_pick_photo);
        photo.setOnClickListener(this);
        Button cancel = (Button) findViewById(R.id.btn_cancel);
        cancel.setOnClickListener(this);

        bg = (RelativeLayout) findViewById(R.id.container_dialog);
        bg.setOnClickListener(this);
        Window window = this.getWindow();
        window.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.photo_dialog_style);  //添加动画

    }


    public interface ClickListener
    {
        void selectBtn(int selectId);
    }


    public void setClickListener(ClickListener list)
    {
        this.alertDo = list;
    }


    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btn_take_photo:
                alertDo.selectBtn(1);
                dismiss();
                break;
            case R.id.btn_pick_photo:
                alertDo.selectBtn(2);
                dismiss();
                break;
            case R.id.container_dialog:
            case R.id.btn_cancel:
                dismiss();
                break;
        }
    }
}
