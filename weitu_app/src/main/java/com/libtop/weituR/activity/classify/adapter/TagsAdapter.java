package com.libtop.weituR.activity.classify.adapter;

import android.content.Context;
import android.view.View;
import android.widget.RadioButton;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.classify.bean.TabBean;
import com.libtop.weituR.base.BaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/1/13.
 */
public class TagsAdapter extends BaseAdapter<TabBean> {
    private Map<Integer,Boolean> mBtnStatus=new HashMap<Integer,Boolean>();

    public TagsAdapter(Context context, List<TabBean> data) {
        super(context, data, R.layout.item_list_search_tab);
        int size=data.size();
        for (int i=0;i<size;i++){
            mBtnStatus.put(i,false);
        }
    }

    @Override
    protected void newView(View convertView) {

    }

    public void update(List<TabBean> data){
        int size=data.size();
        for (int i=0;i<size;i++){
            mBtnStatus.put(i,false);
        }
        notifyDataSetChanged();
    }

    @Override
    protected void holderView(View convertView,TabBean s, int position) {
        RadioButton button=(RadioButton)convertView.findViewById(R.id.radio);
        button.setText(s.name);
        button.setChecked(mBtnStatus.get(position));
    }

    public void selectItem(int position){
        int size=mData.size();
        for (int i=0;i<size;i++){
            if (position==i){
                mBtnStatus.put(i,true);
            }else{
                mBtnStatus.put(i,false);
            }
        }
        notifyDataSetChanged();
    }
}
