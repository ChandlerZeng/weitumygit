package com.libtop.weitu.activity.main.notice;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.ContentFragment;
import com.libtop.weitu.http.HttpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by Administrator on 2016/1/14 0014.
 */
public class NoticeContentFragment extends ContentFragment{
    @Bind(R.id.title)
    TextView mTitleText;
    @Bind(R.id.news_detail_title)
    TextView mNewsTitleText;
    @Bind(R.id.news_date_txt)
    TextView mNewsDateText;
    @Bind(R.id.webview)
    WebView mWeb;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_web_content;
    }

    @Override
    public void onCreation(View root) {
        setTitle();
        setUpWebView();
        requestInfo();
    }

    @Override
    public void onDestroy() {
        mWeb.destroy();
        super.onDestroy();
    }

    private void setUpWebView(){
        WebSettings settings=mWeb.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setAllowFileAccess(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDefaultTextEncodingName("UTF-8");
    }

    private void setTitle(){
        mTitleText.setText(R.string.news_detail);
        String title=((ContentActivity)mContext).getCurrentExtra().getString("title");
        String date=((ContentActivity)mContext).getCurrentExtra().getString("date");
        if(title!=null&!title.equals("")){
            mNewsTitleText.setVisibility(View.VISIBLE);
            mNewsTitleText.setText(title);
        }else {
            mNewsTitleText.setVisibility(View.GONE);
        }
        if(date!=null&!date.equals("")){
            mNewsDateText.setVisibility(View.VISIBLE);
            mNewsDateText.setText(date);
        }else {
            mNewsDateText.setVisibility(View.GONE);
        }
    }


    @Nullable
    @OnClick(R.id.back_btn)
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_btn:
                onBackPressed();
                break;
        }
    }

    private void requestInfo(){
        String id=((ContentActivity)mContext).getCurrentExtra().getString("id");
        if (TextUtils.isEmpty(id)) return;
        showLoding();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("method", "notice.get");
        params.put("id",id);
        HttpRequest.loadWithMapSec(params, new HttpRequest.CallBackSec() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String json, int id) {
                dismissLoading();
                if (TextUtils.isEmpty(json)) {
                    showToast("没有相关数据");
                    return;
                }
                try {
                    JSONObject jObj=new JSONObject(json);
                    String content=jObj.getString("content");
                    mWeb.loadDataWithBaseURL(null,content,"text/html","UTF-8",null);
                    mWeb.setWebViewClient(new WebViewClient() {
                        @Override
                        public void onPageFinished(WebView view, String url) {
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
