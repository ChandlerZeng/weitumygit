package com.libtop.weituR.activity.main.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.libtop.weitu.R;
import com.libtop.weituR.utils.DisplayUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/1/18 0018.
 */
public class PhotoGridAdapter extends BaseAdapter {
    private HashMap<Integer,Boolean> mCheckboxes=new HashMap<Integer, Boolean>();
    private int mSize;
    private Context mContext;
    private List<File> mData;
//    private ImageOptions mOptions;
    public PhotoGridAdapter(Context context, List<File> data) {
//        super(context, data, R.layout.item_grid_photo);
        mContext=context;
        mData=data;
        mSize=(DisplayUtils.getDisplayWith(mContext)
                -DisplayUtils.dp2px(mContext,10))/3;
//        mOptions=((AppApplication)mContext.getApplicationContext())
//                .getCommenOptions();
//        mOptions = new ImageOptions.Builder()
//                .showloading(R.drawable.content_bg)
//                .showImageOnFail(R.drawable.content_bg)
//                .showImageForEmptyUri(R.drawable.content_bg)
//                .cacheInMemory(true).cacheOnDisk(true)
//                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
//                .build();
        fill(data);
    }

    private void fill(List<File> data){
        mCheckboxes.clear();
        int length=data.size();
        for (int i=0;i<length;i++){
            if (mCheckboxes.get(i)==null){
                mCheckboxes.put(i,false);
            }
        }
    }


    @Override
    public int getCount() {
        return mData.size()+1;
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position-1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_grid_photo, null, false);
            newView(convertView);
        }
        holderView(convertView,position);
        return convertView;
    }

    public void update(List<File> data){
        mData=data;
        fill(data);
        notifyDataSetChanged();
    }

    protected void newView(View convertView) {
        Holder holder = new Holder();
        holder.image = (ImageView) convertView.findViewById(R.id.image);
        holder.check=(CheckBox)convertView.findViewById(R.id.check);
        convertView.setTag(holder);
    }

    protected void holderView(View convertView,int position) {
        Holder holder = (Holder) convertView.getTag();
        ViewGroup.LayoutParams params=holder.image.getLayoutParams();
        params.height=mSize;
        holder.image.setLayoutParams(params);
        if (position==0){
            holder.check.setVisibility(View.GONE);
            holder.image.setBackgroundColor(mContext.getResources().getColor(R.color.black2));
//            holder.image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            holder.image.setImageResource(R.drawable.select_photo);
//            ImageLoader.getInstance().displayImage("drawable://"+R.drawable.select_photo, holder.image, mOptions);
        }else{
            holder.check.setVisibility(View.VISIBLE);
            File f=mData.get(position-1);
//            holder.image.requestLayout();
            holder.image.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
//            ImageLoader.getInstance().displayImage("file:/"+f.getAbsolutePath(), holder.image, mOptions);
            Picasso.with(mContext).load("file:/"+f.getAbsolutePath()).into(holder.image);
//            x.image().bind(holder.image,"file:/"+f.getAbsolutePath(),mOptions);
            Log.e("listTag", holder.check + " " + mCheckboxes.size());
            Boolean checked=mCheckboxes.get(position-1);
            if (checked==null) holder.check.setChecked(false);
            else holder.check.setChecked(checked);
        }


    }

    private class Holder{
        ImageView image;
        CheckBox check;
    }

    public void check(int position){
        boolean isChecked=mCheckboxes.get(position);
        mCheckboxes.put(position,!isChecked);
        notifyDataSetChanged();
    }

    public List<File> getChecked(){
        List<File> result=new ArrayList<File>();
        for (Map.Entry<Integer,Boolean> entry:mCheckboxes.entrySet()){
            if (entry.getValue()){
                result.add((File)mData.get(entry.getKey()));
            }
        }
        return result;
    }
}
