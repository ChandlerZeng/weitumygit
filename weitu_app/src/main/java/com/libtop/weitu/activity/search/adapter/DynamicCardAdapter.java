package com.libtop.weitu.activity.search.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.libtop.weitu.R;
import com.libtop.weitu.utils.DisplayUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by LianTu on 2016-8-29.
 */
public class DynamicCardAdapter extends RecyclerView.Adapter<DynamicCardViewHolder>
{
    private List<String> lists;
    private Context context;
    private List<Integer> heights;
    private OnItemClickListener mListener;
    private int baseHeight;


    public DynamicCardAdapter(Context context, List<String> lists, int columnNum)
    {
        this.context = context;
        this.lists = lists;
        this.baseHeight = DisplayUtils.getDisplayHeight(context) / columnNum;
        if (!lists.isEmpty())
        {
            getRandomHeight(this.lists);
        }
    }


    private void getRandomHeight(List<String> lists)
    {//得到随机item的高度
        heights = new ArrayList<>();
        for (int i = 0; i < lists.size(); i++)
        {
            heights.add((int) ((int) (baseHeight + Math.random() * this.baseHeight) * 0.5));
        }
    }


    public interface OnItemClickListener
    {
        void ItemClickListener(View view, int postion);
    }


    public void setOnClickListener(OnItemClickListener listener)
    {
        this.mListener = listener;
    }


    @Override
    public DynamicCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recycler_photo, parent, false);
        DynamicCardViewHolder viewHolder = new DynamicCardViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final DynamicCardViewHolder holder, int position)
    {
        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();//得到item的LayoutParams布局参数
        params.height = heights.get(position);//把随机的高度赋予item布局
        holder.itemView.setLayoutParams(params);//把params设置给item布局

        //        holder.mTv.setText(lists.get(position));//为控件绑定数据
        String url;
        if (lists != null && !TextUtils.isEmpty(lists.get(position)))
        {
            url = lists.get(position);
        }
        else
        {
            url = "http://";
        }
        Picasso.with(context).load(url).error(R.drawable.default_image).placeholder(R.drawable.default_image).fit().centerCrop().into(holder.imgPhoto);
        if (mListener != null)
        {//如果设置了监听那么它就不为空，然后回调相应的方法
            holder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    int pos = holder.getLayoutPosition();//得到当前点击item的位置pos
                    mListener.ItemClickListener(holder.itemView, pos);//把事件交给我们实现的接口那里处理
                }
            });
        }
    }


    public void setNewData(List<String> lists)
    {
        this.lists = lists;
        if (!lists.isEmpty())
        {
            getRandomHeight(this.lists);
        }
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount()
    {
        return lists.size();
    }
}


class DynamicCardViewHolder extends RecyclerView.ViewHolder
{
    ImageView imgPhoto;


    public DynamicCardViewHolder(View itemView)
    {
        super(itemView);
        imgPhoto = (ImageView) itemView.findViewById(R.id.img_photo);
    }
}
