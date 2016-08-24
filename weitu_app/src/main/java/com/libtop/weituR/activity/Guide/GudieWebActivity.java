package com.libtop.weituR.activity.Guide;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import com.libtop.weitu.R;
import com.libtop.weituR.activity.main.MainActivity;
import com.libtop.weituR.tool.Preference;
import com.libtop.weituR.utils.CheckUtil;

public class GudieWebActivity extends Activity {
    WebView webView;
    TextView title;
    protected Preference mPreference;
    String username = "";
    String passwork = "";
    String ytcid = "";

    private String isfirst = "2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gudie_web);
        webView = (WebView) findViewById(R.id.gudie_webview);

        mPreference = new Preference(this);
        isfirst = getIntent().getStringExtra("isfirst");
        if (Build.VERSION.SDK_INT >= 19) {
            webView.getSettings().setLoadsImagesAutomatically(true);
        } else {
            webView.getSettings().setLoadsImagesAutomatically(false);
        }
        if (CheckUtil.isNull(mPreference.getString(Preference.uid))) {

        } else {
            username = mPreference.getString(Preference.UserName);
        }

        // http://www.bookus.cn/activity/activity?aid=56e7dcc1fb62948f593d3419
        title = (TextView) findViewById(R.id.title);
        title.setText("");
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        //活动的引导页
        //http://op.bookus.cn/activity/index?aid=56e7dcc1fb62948f593d3419测试
        //http://op.yuntu.io/activity/index?aid=5704a361e4b0834fe591e1e8正式
        webView.loadUrl("http://op.yuntu.io/activity/index?aid=5704a361e4b0834fe591e1e8");
        webView.setWebViewClient(new WebViewClient() {
                                     public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                         view.loadUrl(url);
                                         return true;
                                     }

                                     public void onPageFinished(WebView view, String url) {
                                         if (!webView.getSettings().getLoadsImagesAutomatically()) {
                                             webView.getSettings().setLoadsImagesAutomatically(true);
                                         }
                                         title.setText(view.getTitle());
                                     }
                                 }
        );
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goMain();
            }
        });
        findViewById(R.id.commit).setVisibility(View.INVISIBLE);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();// 返回前一个页面
            return true;
        } else {
            goMain();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void goMain() {
        if (isfirst.equals("1")) {
            Intent intent = new Intent(GudieWebActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            finish();
        }
    }
}
