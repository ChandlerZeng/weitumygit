package com.libtop.weitu.viewadapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.util.Linkify;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.viewadapter.CommonAdapter.OnViewInflateListener;


public class ViewHolderHelper
{
    private final SparseArray<View> viewSparseArray;
    private final Context context;
    private View convertView;


    private ViewHolderHelper(Context context, ViewGroup parent, int layoutId, int position)
    {
        this(context, parent, layoutId, position, null);
    }


    private ViewHolderHelper(Context context, ViewGroup parent, int layoutId, int position, OnViewInflateListener listener)
    {
        this.viewSparseArray = new SparseArray<View>();
        this.context = context;

        convertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        if (listener != null)
        {
            listener.onInflate(convertView);
        }

        convertView.setTag(layoutId, this);
    }


    public static ViewHolderHelper get(Context context, View convertView, ViewGroup parent, int layoutId, int position)
    {
        return get(context, convertView, parent, layoutId, position, null);
    }


    public static ViewHolderHelper get(Context context, View convertView, ViewGroup parent, int layoutId, int position, OnViewInflateListener listener)
    {
        if (convertView == null || convertView.getTag(layoutId) == null)
        {
            return new ViewHolderHelper(context, parent, layoutId, position, listener);
        }

        return (ViewHolderHelper) convertView.getTag(layoutId);
    }


    @SuppressWarnings("unchecked")
    public <T extends View> T getView(int viewId)
    {
        View view = viewSparseArray.get(viewId);
        if (view == null)
        {
            view = convertView.findViewById(viewId);
            viewSparseArray.put(viewId, view);
        }

        return (T) view;
    }


    public View getConvertView()
    {
        return convertView;
    }


    public ViewHolderHelper setText(int viewId, CharSequence text)
    {
        TextView view = getView(viewId);
        view.setText(text);

        return this;
    }


    public ViewHolderHelper setText(int viewId, int textResId)
    {
        return setText(viewId, context.getResources().getText(textResId));
    }


    public ViewHolderHelper setHint(int viewId, CharSequence text)
    {
        TextView view = getView(viewId);
        view.setHint(text);

        return this;
    }


    public ViewHolderHelper setHint(int viewId, int textResId)
    {
        return setHint(viewId, context.getResources().getText(textResId));
    }


    public ViewHolderHelper setTextColor(int viewId, int color)
    {
        TextView view = getView(viewId);
        view.setTextColor(color);

        return this;
    }


    public ViewHolderHelper setImageResource(int viewId, int resId)
    {
        ImageView view = getView(viewId);
        view.setImageResource(resId);

        return this;
    }


    public ViewHolderHelper setImageBitmap(int viewId, Bitmap bitmap)
    {
        ImageView view = getView(viewId);
        view.setImageBitmap(bitmap);

        return this;
    }


    public ViewHolderHelper setImageDrawable(int viewId, Drawable drawable)
    {
        ImageView view = getView(viewId);
        view.setImageDrawable(drawable);

        return this;
    }


    public ViewHolderHelper setBackgroundColor(int viewId, int color)
    {
        View view = getView(viewId);
        view.setBackgroundColor(color);

        return this;
    }


    public ViewHolderHelper setBackgroundResource(int viewId, int resId)
    {
        View view = getView(viewId);
        view.setBackgroundResource(resId);

        return this;
    }


    public ViewHolderHelper setAlpha(int viewId, float alpha)
    {
        View view = getView(viewId);
        view.setAlpha(alpha);

        return this;
    }


    public ViewHolderHelper setVisibility(int viewId, int visibility)
    {
        View view = getView(viewId);
        view.setVisibility(visibility);

        return this;
    }


    public ViewHolderHelper setEnabled(int viewId, boolean enabled)
    {
        View view = getView(viewId);
        view.setEnabled(enabled);

        return this;
    }


    public ViewHolderHelper linkify(int viewId)
    {
        TextView view = getView(viewId);
        Linkify.addLinks(view, Linkify.ALL);

        return this;
    }


    public ViewHolderHelper setChecked(int viewId, boolean checked)
    {
        Checkable view = (Checkable) getView(viewId);
        view.setChecked(checked);

        return this;
    }
}
