package com.libtop.weitu.utils.selector.adapter;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.libtop.weitu.R;
import com.libtop.weitu.utils.StringUtil;
import com.libtop.weitu.utils.selector.MultiImageSelectorFragment;
import com.libtop.weitu.utils.selector.bean.ListGridImage;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;


/**
 * <p>
 * Title: ImageGridAdapter2.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * CreateTime：16/5/5
 * </p>
 *
 * @author 作者名
 * @version common v1.0
 */
public class ImageGridAdapter2 extends BaseAdapter
{
    List<ListGridImage> mlist;
    private LayoutInflater mInflater;
    final int mGridWidth;
    Context mcontext;
    boolean mAdministration = false;


    public ImageGridAdapter2(List<ListGridImage> list, Context context)
    {
        this.mcontext = context;
        this.mlist = list;
        mInflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int width = 0;
        WindowManager wm = (WindowManager) mcontext.getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
        {
            Point size = new Point();
            wm.getDefaultDisplay().getSize(size);
            width = size.x;
        }
        else
        {
            width = wm.getDefaultDisplay().getWidth();
        }
        mGridWidth = width / 4;
    }


    public ImageGridAdapter2(List<ListGridImage> list, Context context, boolean Administration)
    {
        this.mAdministration = Administration;
        this.mcontext = context;
        this.mlist = list;
        mInflater = (LayoutInflater) mcontext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int width = 0;
        WindowManager wm = (WindowManager) mcontext.getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
        {
            Point size = new Point();
            wm.getDefaultDisplay().getSize(size);
            width = size.x;
        }
        else
        {
            width = wm.getDefaultDisplay().getWidth();
        }
        mGridWidth = width / 4;
    }


    @Override
    public int getCount()
    {
        return mlist.size();
    }


    public Object getItem(int position)
    {
        return mlist.get(position);
    }


    @Override
    public long getItemId(int position)
    {
        return position;
    }


    @Override
    public View getView(int i, View view, ViewGroup parent)
    {
        ViewHolder holder;
        if (view == null)
        {
            view = mInflater.inflate(R.layout.list_item_image, parent, false);
            holder = new ViewHolder(view);
        }
        else
        {
            holder = (ViewHolder) view.getTag();
        }

        if (holder != null)
        {
            holder.setVis(mlist.get(i).isPro());
            holder.setCheck(mlist.get(i).ischeck());
            holder.bindData(getStr(i));
        }
        return view;
    }


    public void setData(List<ListGridImage> list)
    {
        this.mlist = list;
    }


    public String getStr(int i)
    {

        return mlist.get(i).getImageUrl();

    }


    class ViewHolder
    {
        ImageView image;
        ImageView indicator;
        ProgressBar progressBar;
        View mask;


        ViewHolder(View view)
        {
            image = (ImageView) view.findViewById(R.id.image);
            indicator = (ImageView) view.findViewById(R.id.checkmark);
            if (mAdministration)
            {
                indicator.setVisibility(View.VISIBLE);
            }
            else
            {
                indicator.setVisibility(View.GONE);
            }
            mask = view.findViewById(R.id.mask);
            progressBar = (ProgressBar) view.findViewById(R.id.pro);
            view.setTag(this);
        }


        void setImageView()
        {
            image.setImageResource(R.drawable.add_pic);
        }


        void setCheck(boolean isCheak)
        {
            if (isCheak)
            {
                indicator.setImageResource(R.drawable.btn_selected);
            }
            else
            {
                indicator.setImageResource(R.drawable.btn_unselected);
            }
        }


        void setVis(boolean isCheak)
        {
            if (isCheak)
            {
                progressBar.setVisibility(View.VISIBLE);
            }
            else
            {
                progressBar.setVisibility(View.GONE);
            }
        }


        void bindData(String url)
        {
            File imageFile = new File(url);
            if (imageFile.exists())
            {
                Picasso.with(mcontext).load(imageFile).placeholder(R.drawable.default_error).tag(MultiImageSelectorFragment.TAG).resize(mGridWidth, mGridWidth).centerCrop().into(image);
            }
            else
            {
                String notEmptyUrl = StringUtil.getNotEmptyUrl(url);
                Picasso.with(mcontext).load(notEmptyUrl).placeholder(R.drawable.default_error).tag(MultiImageSelectorFragment.TAG).resize(mGridWidth, mGridWidth).centerCrop().into(image);
            }
        }
    }
}
