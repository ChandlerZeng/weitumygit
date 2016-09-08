package com.libtop.weitu.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.libtop.weitu.R;


public class NetworkLoadingLayout extends RelativeLayout implements OnClickListener
{
    private ImageView emptyPromptIv;
    private TextView emptyPromptTv;
    private TextView retryPromptTv;
    private ProgressBar progressBar;

    private OnRetryClickListner onRetryClickListner;


    public NetworkLoadingLayout(Context context)
    {
        super(context);
        init();
    }


    public NetworkLoadingLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }


    private void init()
    {
        inflate(getContext(), R.layout.network_loading_layout, this);

        emptyPromptIv = (ImageView) findViewById(R.id.network_loading_layout_empty_prompt_imageview);
        emptyPromptIv.setVisibility(View.GONE);
        emptyPromptTv = (TextView) findViewById(R.id.network_loading_layout_empty_prompt_textview);
        emptyPromptTv.setVisibility(View.GONE);

        retryPromptTv = (TextView) findViewById(R.id.network_loading_layout_retry_prompt_textview);
        retryPromptTv.setVisibility(View.GONE);
        progressBar = (ProgressBar) findViewById(R.id.network_loading_layout_progressbar);
        progressBar.setVisibility(View.GONE);

        setOnClickListener(this);
    }


    public void setEmptyPromptText(CharSequence text)
    {
        emptyPromptTv.setText(text);
    }


    public void showLoading()
    {
        setVisibility(View.VISIBLE);

        emptyPromptIv.setVisibility(View.GONE);
        emptyPromptTv.setVisibility(View.GONE);
        retryPromptTv.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }


    public void showLoadFailAndRetryPrompt()
    {
        showCustomRetryPrompt(R.string.network_error_and_touch_retry);
    }


    public void showEmptyAndRetryPrompt()
    {
        showCustomRetryPrompt(R.string.empty_data_and_touch_retry);
    }


    public void showCustomRetryPrompt(int textResId)
    {
        if (getContext() != null)
        {
            showCustomRetryPrompt(getContext().getString(textResId));
        }
    }


    public void showCustomRetryPrompt(CharSequence text)
    {
        setVisibility(View.VISIBLE);

        progressBar.setVisibility(View.GONE);
        emptyPromptIv.setVisibility(View.GONE);
        emptyPromptTv.setVisibility(View.GONE);

        retryPromptTv.setText(text);
        retryPromptTv.setVisibility(View.VISIBLE);
    }


    public void showEmptyPrompt()
    {
        setVisibility(View.VISIBLE);

        retryPromptTv.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        emptyPromptIv.setVisibility(View.VISIBLE);
        emptyPromptTv.setVisibility(View.VISIBLE);

        setOnClickListener(null);
    }


    public void dismiss()
    {
        if (getVisibility() != View.GONE)
        {
            setVisibility(View.GONE);
        }
    }


    public void setOnRetryClickListner(OnRetryClickListner onRetryClickListner)
    {
        this.onRetryClickListner = onRetryClickListner;
    }


    public OnRetryClickListner getOnRetryClickListner()
    {
        return onRetryClickListner;
    }


    public interface OnRetryClickListner
    {
        void onRetryClick(View v);
    }


    @Override
    public void onClick(View v)
    {
        if (progressBar.getVisibility() != View.VISIBLE)
        {
            showLoading();
            if (onRetryClickListner != null)
            {
                onRetryClickListner.onRetryClick(this);
            }
        }
    }
}
