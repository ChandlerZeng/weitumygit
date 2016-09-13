package com.libtop.weitu.activity.notice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.base.MyBaseFragment;


/**
 * @author Sai
 * @ClassName: SchoolNoticeFragment
 * @Description: 校内通知页
 * @date 9/13/16 14:54
 */
public class SchoolNoticeFragment extends MyBaseFragment
{
    private boolean isFirstIn = true;
    private View rootView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        if (rootView != null)
        {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent == null)
            {
                parent.removeView(rootView);
            }
        }
        else
        {
            rootView = inflater.inflate(R.layout.fragment_notice_school_view, container, false);
            initChildView(rootView);
        }

        if (isFirstIn)
        {
            isFirstIn = false;
            //TODO
        }

        return rootView;
    }


    private void initChildView(View view)
    {
        //TODO
    }
}
