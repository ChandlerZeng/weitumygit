package com.libtop.weituR.activity.main.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.main.dto.ClassmateBean;
import com.libtop.weituR.base.BaseAdapter;
import com.libtop.weituR.utils.CheckUtil;
import com.libtop.weituR.utils.DisplayUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/1/19 0019.
 */
public class MateSortAdapter extends BaseAdapter<ClassmateBean> implements SectionIndexer {
    private GradientDrawable[] mShapes=new GradientDrawable[2];
    private int[] colors={Color.parseColor("#2dc5ac"),Color.parseColor("#10537d")};

    public MateSortAdapter(Context context, List<ClassmateBean> data) {
        super(context, data, R.layout.item_list_mate);
        setShapes();
    }

    private void setShapes(){
        int size= DisplayUtils.dp2px(mContext, 45);
        for (int i=0;i<mShapes.length;i++){
            GradientDrawable gd=new GradientDrawable();
            gd.setColor(colors[i]);
            gd.setShape(GradientDrawable.OVAL);
            gd.setSize(size, size);
            mShapes[i]=gd;
        }
    }

    public void update(List<ClassmateBean> list) {
        mData = list;
        notifyDataSetChanged();
    }

    @Override
    protected void newView(View convertView) {
        Holder holder = new Holder();
        holder.head = (TextView) convertView.findViewById(R.id.head);
        holder.name=(TextView)convertView.findViewById(R.id.name);
        holder.lent=(TextView)convertView.findViewById(R.id.lent);
        holder.letter=(TextView)convertView.findViewById(R.id.catalog);
        holder.line = convertView.findViewById(R.id.line);
        convertView.setTag(holder);
    }

    @Override
    protected void holderView(View convertView, ClassmateBean classmateBean, int position) {
        Holder holder = (Holder) convertView.getTag();
//        if (position<=3){
//            holder.hotIcon.setVisibility(View.VISIBLE);
//        }else {
//            holder.hotIcon.setVisibility(View.GONE);
//        }
//        holder.titleText.setText(noticeInfo.title);
//        holder.comments.setText(0+"");
//        holder.timeStr.setText(DateUtil.getSendTimeDistance(noticeInfo.dateLine));

        String pre = "";
        if(position != 0){
            pre = mData.get(position - 1).getCharacter();
        }
        if (!CheckUtil.checkEquels(pre, classmateBean.getCharacter())) {
            holder.letter.setVisibility(View.VISIBLE);
            holder.letter.setText(classmateBean.getCharacter().toUpperCase());
            holder.line.setVisibility(View.VISIBLE);
        } else {
            holder.letter.setVisibility(View.GONE);
            holder.line.setVisibility(View.GONE);
        }
        holder.name.setText(classmateBean.getUserName());
        holder.lent.setText(classmateBean.getLibraryId());
        holder.head.setText(getShort(classmateBean.getUserName()));

        if (position%2==0){
            holder.head.setBackgroundDrawable(mShapes[1]);
        }else {
            holder.head.setBackgroundDrawable(mShapes[0]);
        }

    }

    private String getShort(String source){
        int length=source.length();
        if (length==2) return source;
        return source.substring(length-2,length);
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = mData.get(i).getCharacter();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == sectionIndex) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        return mData.get(position).getCharacter().charAt(0);
    }

    private class Holder{
        TextView head,name,lent,letter;
        View line;
    }
}
