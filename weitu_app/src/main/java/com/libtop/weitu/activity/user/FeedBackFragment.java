package com.libtop.weitu.activity.user;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.base.BaseFragment;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.CheckUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;


/**
 * Created by Administrator on 2016/1/11 0011.
 */
public class FeedBackFragment extends BaseFragment
{
    @Bind(R.id.content)
    EditText mContentEdit;


    @Nullable
    @OnClick({R.id.back_btn, R.id.send_info})
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.back_btn:
                onBackPressed();
                break;
            case R.id.send_info:
                sendBack(mContentEdit.getText() + "");
                break;
        }
    }


    @Override
    public void onBackPressed()
    {
        if (((ContentActivity) mContext).getCurrentExtra().getBoolean(ContentActivity.FRAG_ISBACK))
        {
            ((ContentActivity) mContext).popBack();
        }
        else
        {
            mContext.finish();
        }
    }


    @Override
    protected int getLayoutId()
    {
        return R.layout.fragment_feedback;
    }


    @Override
    public void onCreation(View root)
    {
        setTitle(root);
    }


    private void setTitle(View root)
    {
        ((TextView) root.findViewById(R.id.title)).setText("我有建议");
    }


    private void sendBack(String content)
    {
        if (CheckUtil.isNull(content))
        {
            showToast("说点什么吧");
            return;
        }
        showLoding();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("method", "suggestion.save");
        params.put("uid", Preference.instance(mContext).getString(Preference.uid));
        params.put("content", content);
        HttpRequest.loadWithMapSec(params, new HttpRequest.CallBackSec()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {

            }


            @Override
            public void onResponse(String json, int id)
            {
                dismissLoading();
                if (CheckUtil.isNullTxt(json))
                {
                    showToast("请求超时，请稍后再试");
                    return;
                }
                if (CheckUtil.isNull(json))
                {
                    showToast("网络连接超时，请稍后再试");
                }
                else
                {
                    Toast.makeText(mContext, "感谢您提的宝贵意见", Toast.LENGTH_SHORT).show();
                    mContext.finish();
                }
            }
        });
    }
}
