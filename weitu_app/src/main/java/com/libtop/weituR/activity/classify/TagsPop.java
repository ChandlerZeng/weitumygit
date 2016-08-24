package com.libtop.weituR.activity.classify;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weituR.utils.DisplayUtils;

/**
 * Created by Administrator on 2016/1/25 0025.
 */
public class TagsPop extends PopupWindow implements AdapterView.OnItemClickListener {
    private Context mContext;
    private String[] mCurrentSource;
    private String mCurrentStr;
    private StringAdapter mAdapter;
    private STATUS mStatus;
    private OnItemClickListener mOnItemClickListener;

    public TagsPop(Context context,String[] keys) {
        super(context);
        mContext=context;
        mCurrentSource=keys;
        mCurrentStr=mCurrentSource[0];
        setup();
    }

    private void setBackgroundAlpha(float value){
        Activity activity=(Activity)mContext;
        WindowManager.LayoutParams lp=activity.getWindow().getAttributes();
        lp.alpha = value;
        activity.getWindow().setAttributes(lp);
    }

    private void setup(){
        View root= LayoutInflater.from(mContext).inflate(R.layout.pop_simple_string_list,null);
        setContentView(root);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setFocusable(true);
        setBackgroundDrawable(new ColorDrawable(0x000000));
        ListView listView=(ListView)root.findViewById(R.id.list);
        mAdapter=new StringAdapter();
        listView.setAdapter(mAdapter);
        listView.setBackgroundColor(mContext.getResources().getColor(R.color.grey6));
        listView.setOnItemClickListener(this);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                setBackgroundAlpha(1.0f);
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        mOnItemClickListener=onItemClickListener;
    }

    public void show(View parent,STATUS status,String[] source){
        mStatus=status;
        mCurrentSource=source;
        mAdapter.notifyDataSetChanged();
        setBackgroundAlpha(0.5f);
        showAsDropDown(parent);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mOnItemClickListener!=null) {
            mCurrentStr=mCurrentSource[position];
            mOnItemClickListener.onItemClick(mStatus,mCurrentSource[position]);
        }
        dismiss();
    }

    private class StringAdapter extends BaseAdapter{


        @Override
        public int getCount() {
            return mCurrentSource.length;
        }

        @Override
        public Object getItem(int position) {
            return mCurrentSource[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView==null){
                convertView=LayoutInflater.from(mContext).inflate(R.layout.item_list_string2,null);
            }
            TextView tv=(TextView)convertView.findViewById(R.id.text);
            String s=mCurrentSource[position];
            tv.setText(s);
            if (s.equals(mCurrentStr)) tv.setTextColor(mContext.getResources().getColor(R.color.green2));
            else tv.setTextColor(mContext.getResources().getColor(R.color.grey3));
            return convertView;
        }
    }

    public enum STATUS{
        KEYS,TYPES,OREDER
    }

    public interface OnItemClickListener{
        void onItemClick(STATUS status,String value);
    }
}
