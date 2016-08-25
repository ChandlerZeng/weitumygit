package com.libtop.weitu.activity.source;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.libtop.weitu.R;

import java.util.HashMap;
import java.util.List;

/**
 * Created by LianTu on 2016-8-11.
 */
public class RecyclerSingleChoiseAdapter extends RecyclerView.Adapter<RecyclerSingleChoiseAdapter.NormalTextViewHolder> {
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<String> mList;
    //用于记录每个RadioButton的状态，并保证只可选一个
    private HashMap<String,Boolean> states=new HashMap<String,Boolean>();
    private static OnItemClickLister onItemClickLister;

    public RecyclerSingleChoiseAdapter(Context context,List<String> list,OnItemClickLister onItemClickLister) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        states.put(String.valueOf(0),true);
        mList = list;
        this.onItemClickLister = onItemClickLister;
    }

    @Override
    public NormalTextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NormalTextViewHolder(mLayoutInflater.inflate(R.layout.recycler_single_choise, parent, false));
    }

    @Override
    public void onBindViewHolder(NormalTextViewHolder holder, int position) {
        holder.mTextView.setText(mList.get(position));
        if(states.get(String.valueOf(position)) == null || states.get(String.valueOf(position))== false){
            holder.mTextView.setBackgroundColor(Color.WHITE);
        }else{
            holder.mTextView.setBackgroundResource(R.drawable.green_board);
        }
    }

    public void setNewData(List<String> list){
        mList = list;
        notifyDataSetChanged();
    }


    public void setSingleSelect(int position){
        for(String key:states.keySet()){
            states.put(key, false);

        }
        states.put(String.valueOf(position),true);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public static class NormalTextViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        NormalTextViewHolder(View view) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.text_view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickLister.onItemClick(v,getLayoutPosition());
                }
            });
        }
    }

    public interface OnItemClickLister{
        void onItemClick(View v,int position);
    }
}
