package com.libtop.weitu.activity.main.clazz;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.main.dto.ClassmateBean;
import com.libtop.weitu.utils.DisplayUtils;


/**
 * Created by Administrator on 2016/1/19 0019.
 */
public class MemberPop extends PopupWindow
{
    private Context mContext;
    private GradientDrawable mBg = new GradientDrawable();
    TextView mTvHead, mTvName, mTvSex, mTvMobile, mTvMail, mTvLent;


    public MemberPop(Context context)
    {
        super(context);
        mContext = context;
        mBg.setShape(GradientDrawable.OVAL);
        mBg.setColor(mContext.getResources().getColor(R.color.green2));
        int size = DisplayUtils.dp2px(mContext, 80);
        mBg.setSize(size, size);
        setup();
    }


    private String getShort(String source)
    {
        int length = source.length();
        if (length == 2)
        {
            return source;
        }
        return source.substring(length - 2, length);
    }


    private void setup()
    {
        View root = LayoutInflater.from(mContext).inflate(R.layout.pop_member_info, null);
        setContentView(root);
        mTvHead = (TextView) root.findViewById(R.id.head);
        mTvName = (TextView) root.findViewById(R.id.name);
        mTvSex = (TextView) root.findViewById(R.id.sex);
        mTvMobile = (TextView) root.findViewById(R.id.mobile);
        mTvMail = (TextView) root.findViewById(R.id.mail);
        mTvLent = (TextView) root.findViewById(R.id.lent);

        mTvHead.setBackgroundDrawable(mBg);

        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());
        int width = DisplayUtils.getDisplayWith(mContext) - DisplayUtils.dp2px(mContext, 55);
        setWidth(width);
        setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        setAnimationStyle(R.style.pull_right_pop_an);
        setOnDismissListener(new OnDismissListener()
        {
            @Override
            public void onDismiss()
            {
                setBackgroundAlpha(1.0f);
            }
        });
    }


    public void setData(ClassmateBean bean)
    {
        if (bean != null)
        {
            mTvHead.setText(getShort(bean.getUserName()));
            mTvName.setText(bean.getUserName());
            mTvSex.setText(bean.getSex() == 0 ? "男" : "女");
            mTvMobile.setText(bean.getPhone());
            mTvMail.setText(bean.getEmail());
            mTvLent.setText(bean.getLibraryId());
        }
    }


    private void setBackgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = ((Activity) mContext).getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        ((Activity) mContext).getWindow().setAttributes(lp);
    }


    public void showOnParent(View view)
    {
        setBackgroundAlpha(0.6f);
        showAtLocation(view, Gravity.RIGHT, 0, 0);
    }

}
