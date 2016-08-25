package com.libtop.weituR.activity.Guide;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.main.MainActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;

public class GudieActivity extends Activity {
    String jsonstr = "";
    ImageView mIconImg;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Fresco.initialize(GudieActivity.this);
        setContentView(R.layout.activity_gudie);
        handler = new MyHandler();
        mIconImg = (ImageView) findViewById(R.id.photo);

        Thread the = new Thread(new Runnable() {
            @Override
            public void run() {
                httpR();
            }
        });
        the.start();
        ((TextView) findViewById(R.id.gudie_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CaptureActivity  GudieWebActivity
                Intent intent = new Intent(GudieActivity.this, GudieWebActivity.class);
                intent.putExtra("isfirst", "1");
                intent.putExtra("jsonstr", jsonstr);
                startActivity(intent);
                finish();
            }
        });
        gestureDetector = new GestureDetector(this, new ActivitySetting(this));
        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(GudieActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);


    }

    public void httpR() {
        String url = "http://op.bookus.cn/activity/splash.json";
        OkHttpUtils.get()
                .url(url)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String strResult, int id) {
                        if (TextUtils.isEmpty(strResult)) {
                            return;
                        }
                        getInfo(strResult);
                    }
                });
    }


    private void getInfo(String jsonstr) {
        try {
            JSONObject json = new JSONObject(jsonstr);
            JSONObject data = json
                    .getJSONObject("data");
            jsonstr = data.isNull(data.getString("splashImageURL")) ? data.getString("splashImageURL") : "";

//            Message msg = handler.obtainMessage(1, 1, 0);
//            handler.sendMessage(msg);
        } catch (JSONException ee) {
            ee.printStackTrace();

        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {


        goMain();
        return super.onKeyDown(keyCode, event);
    }

    private void goMain() {
        Intent intent = new Intent(GudieActivity.this, MainActivity.class);

        startActivity(intent);
        finish();
    }

    class MyHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            //区分消息的种类
            if (msg.what == 1) {
                Uri uri = Uri.parse(jsonstr);
                mIconImg.setImageURI(uri);
            }

        }
    }

    MyHandler handler;

    class ActivitySetting extends GestureDetector.SimpleOnGestureListener {
        private Context mContext;
        private int mTopCount;

        ActivitySetting(Context context) {
            this.mContext = context;

        }

        public boolean onSingleTapUp(MotionEvent e) {
            Intent intent = new Intent(mContext, MainActivity.class);
            mContext.startActivity(intent);
            finish();
            return super.onSingleTapUp(e);
        }

    }


    public boolean onTouchEvent(MotionEvent e) {
        return gestureDetector.onTouchEvent(e);
    }
}
