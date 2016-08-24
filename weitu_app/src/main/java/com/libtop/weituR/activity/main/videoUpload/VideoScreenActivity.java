package com.libtop.weituR.activity.main.videoUpload;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.libtop.weitu.R;

import java.net.MalformedURLException;
import java.util.List;

public class VideoScreenActivity extends Activity {
    ImageView imageview;
    List<Bitmap> list;
    LinearLayout screenLayout;
    SeekBar seekBar;
    VideoScreenshot videoScreenshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_screen);
        imageview = (ImageView) findViewById(R.id.imageview);
        screenLayout = (LinearLayout) findViewById(R.id.add_bitmap);
        seekBar = (SeekBar) findViewById(R.id.bit_bar);
        videoScreenshot = new VideoScreenshot("/storage/emulated/0/Hgame.mp4",VideoScreenActivity.this);
        try {
            list = videoScreenshot.getTenBitmap(videoScreenshot.getVideoTime());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        int timeLength = (int) (videoScreenshot.getVideoTime() / 1000);
        seekBar.setMax(timeLength);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                try {
                    changeImage(progress);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        initNavigationHSV();
    }

    private void changeImage(int progress) throws MalformedURLException {
        videoScreenshot = new VideoScreenshot("/storage/emulated/0/234.mp4",VideoScreenActivity.this);
        long a = progress * 1000;
        imageview.setImageBitmap(videoScreenshot.getVideoThumbnail(a));
    }


    private void initNavigationHSV() {
        int width = screenLayout.getWidth();
        int height = screenLayout.getHeight();
        int mWidth = width / list.size();
        for (int i = 0; i < list.size(); i++) {
            LayoutInflater mInflater = (LayoutInflater) this
                    .getSystemService(LAYOUT_INFLATER_SERVICE);
            final ImageView rb = (ImageView) mInflater
                    .inflate(R.layout.screen_image, null);
            rb.setLayoutParams(new ViewGroup.LayoutParams(mWidth,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            rb.setImageBitmap(list.get(i));
            screenLayout.addView(rb);
        }
    }

}
