package com.libtop.weituR.widget.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by zeng on 2016/8/19.
 */
public class RemakeXListView extends XListView {
    public RemakeXListView(Context context) {
        super(context);
    }

    public RemakeXListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RemakeXListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setPullLoadEnable(boolean enable) {
        super.setPullLoadEnable(enable);
    }
    public void setPullLoadEnable(boolean enable,boolean hasdata){
        super.setPullLoadEnable(enable);
        if(!enable&!hasdata){
//            mPullLoading = false;
            mFooterView.show();
            mFooterView.setState(XListViewFooter.STATE_LOADED);
            //make sure "pull up" don't show a line in bottom when listview with one page
            setFooterDividersEnabled(true);
            // both "pull up" and "click" will invoke load more.
            mFooterView.setOnClickListener(null);
        }
    }
}
