package com.zbar.lib;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.common.HybridBinarizer;
import com.libtop.weitu.R;
import com.libtop.weituR.activity.ContentActivity;
import com.libtop.weituR.activity.search.BookDetailFragment;
import com.libtop.weituR.activity.search.dto.BookDto;
import com.libtop.weituR.base.BaseActivity;
import com.libtop.weituR.http.HttpRequest;
import com.libtop.weituR.tool.Preference;
import com.libtop.weituR.utils.CheckUtil;
import com.libtop.weituR.utils.IsbnUtils;
import com.libtop.weituR.utils.selector.MultiImageSelectorActivity;
import com.zbar.lib.camera.CameraBean;
import com.zbar.lib.camera.CameraManager;
import com.zbar.lib.decode.CaptureActivityHandler;
import com.zbar.lib.decode.InactivityTimer;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;


/**
 * 作者: 陈涛(1076559197@qq.com)
 * <p/>
 * 时间: 2014年5月9日 下午12:25:31
 * <p/>
 * 版本: V_1.0.0
 * <p/>
 * 描述: 扫描界面
 */
public class CaptureActivity extends BaseActivity implements Callback, View.OnClickListener {
    public static final int REQUEST_IMAGE = 3;
    private CaptureActivityHandler handler;
    private boolean hasSurface;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.50f;
    private boolean vibrate;
    //扫描找不到这本书的次数
    private int sorry_count = 0;
    private int xx = 0;
    private int y = 0;
    private int cropWidth = 0;
    private int cropHeight = 0;
    RelativeLayout mContainer = null;
    RelativeLayout mCropLayout = null;
    private boolean isNeedCapture = false;
    TextView ligthView;
    private Dialog alertDialog;


    public boolean isNeedCapture() {
        return isNeedCapture;
    }

    public void setNeedCapture(boolean isNeedCapture) {
        this.isNeedCapture = isNeedCapture;
    }

    public int getX() {
        return xx;
    }

    public void setX(int x) {
        this.xx = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getCropWidth() {
        return cropWidth;
    }

    public void setCropWidth(int cropWidth) {
        this.cropWidth = cropWidth;
    }

    public int getCropHeight() {
        return cropHeight;
    }

    public void setCropHeight(int cropHeight) {
        this.cropHeight = cropHeight;
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_qr_scan);
        // 初始化 CameraManager
        CameraManager.init(getApplication());
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);

        mContainer = (RelativeLayout) findViewById(R.id.capture_containter);
        mCropLayout = (RelativeLayout) findViewById(R.id.capture_crop_layout);

        ImageView mQrLineView = (ImageView) findViewById(R.id.capture_scan_line);
        TranslateAnimation mAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE, 0f,
                TranslateAnimation.RELATIVE_TO_PARENT, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0.9f);
        mAnimation.setDuration(1500);
        mAnimation.setRepeatCount(-1);
        mAnimation.setRepeatMode(Animation.INFINITE);
        mAnimation.setInterpolator(new LinearInterpolator());
        mQrLineView.setAnimation(mAnimation);
        ligthView = (TextView) findViewById(R.id.light);
        ligthView.setOnClickListener(this);
        findViewById(R.id.choose).setOnClickListener(this);
        findViewById(R.id.write).setOnClickListener(this);
        findViewById(R.id.back_btn).setOnClickListener(this);
    }

    boolean flag = true;

    protected void light() {
        Drawable img_on, img_off;
        if (flag == true) {
            flag = false;
            Resources res = getResources();
            img_off = res.getDrawable(R.drawable.close_light);
            img_off.setBounds(0, 0, img_off.getMinimumWidth(), img_off.getMinimumHeight());
            ligthView.setCompoundDrawables(null, img_off, null, null); //设置左图标
            // 开闪光灯
            CameraManager.get().openLight();
        } else {
            flag = true;
            Resources res = getResources();
            img_off = res.getDrawable(R.drawable.light);
            img_off.setBounds(0, 0, img_off.getMinimumWidth(), img_off.getMinimumHeight());
            ligthView.setCompoundDrawables(null, img_off, null, null); //设置左图标
            // 关闪光灯
            CameraManager.get().offLight();
        }

    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.capture_preview);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }

        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }


    public void handleDecode(CameraBean cameraBean) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        int count = cameraBean.getCount();
        String isbn = cameraBean.getResult();
//        Toast.makeText(getApplicationContext(), isbn, Toast.LENGTH_LONG).show();
        if (URLUtil.isNetworkUrl(isbn)){
            Uri uri1 = Uri.parse(isbn);
            startActivity(new Intent(Intent.ACTION_VIEW,uri1));
        }else if (CheckUtil.isNumeric(isbn)){
            if ( count == 13){
                String aa = IsbnUtils.obscure(isbn);
                loadPage(aa);
//                    Toast.makeText(getApplicationContext(), aa, Toast.LENGTH_LONG).show();
            }else if ( count == 10){
                String isbn13 = IsbnUtils.covertText10ToText13(isbn);
                String text_isbn13 = isbn13.replaceAll("-", "");
                String aa = IsbnUtils.obscure(text_isbn13);
                loadPage(aa);
//                    Toast.makeText(getApplicationContext(), aa, Toast.LENGTH_LONG).show();
            }
        }else {
            new AlertDialog.Builder(CaptureActivity.this).setTitle(R.string.content2weima)
                    .setMessage(isbn).create().show();
//            Toast.makeText(getApplicationContext(), R.string.notResolveType, Toast.LENGTH_LONG).show();
        }


//        if (count == 13) {
//            String isbn13 = IsbnUtils.convert(isbn);
//            String aa = IsbnUtils.obscure(isbn13);
//            loadPage(aa);
//        } else if (count == 10){
//            String isbn13 = IsbnUtils.covertText10ToText13(isbn);
//            String text_isbn13 = isbn13.replaceAll("-", "");
//            String aa = IsbnUtils.obscure(text_isbn13);
//            loadPage(aa);
//        }
        //String a = IsbnUtils.obscure(result);
//        Intent intent = new Intent(CaptureActivity.this, SearchActivity.class);
//        intent.putExtra("search", result);
//        startActivity(intent);
//        finish();
        // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
        //延时连续扫描
         handler.sendEmptyMessageDelayed(R.id.restart_preview,750);
//         handler.sendEmptyMessage(R.id.restart_preview);


    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);

            Point point = CameraManager.get().getCameraResolution();
            int width = point.y;
            int height = point.x;

            int x = mCropLayout.getLeft() * width / mContainer.getWidth();
            int y = mCropLayout.getTop() * height / mContainer.getHeight();

            int cropWidth = mCropLayout.getWidth() * width / mContainer.getWidth();
            int cropHeight = mCropLayout.getHeight() * height / mContainer.getHeight();

            setX(x);
            setY(y);
            setCropWidth(cropWidth);
            setCropHeight(cropHeight);
            // 设置是否需要截图
            setNeedCapture(true);


        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(CaptureActivity.this);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public Handler getHandler() {
        return handler;
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.light:
                light();
                break;
            case R.id.write:
                showDialog();
                break;
            case R.id.choose:
                Intent intent = new Intent(CaptureActivity.this, MultiImageSelectorActivity.class);
                // 是否显示拍摄图片
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
                // 最大可选择图片数量
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);
                // 选择模式
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, 0);
                // 默认选择
                startActivityForResult(intent, REQUEST_IMAGE);
                break;
            case R.id.back_btn:
                finish();
                break;
        }

    }

    private void showDialog() {
        alertDialog = new Dialog(mContext);
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.write_layout);
        window.setBackgroundDrawable(new ColorDrawable(0));
        TextView cancel = (TextView) window.findViewById(R.id.cancel_btn);
        TextView sure = (TextView) window.findViewById(R.id.sure_btn);
        final EditText isbn = (EditText) window.findViewById(R.id.edit_isbn);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isbn.getText().length() != 13) {
                    Toast.makeText(CaptureActivity.this, R.string.isbnNote, Toast.LENGTH_SHORT).show();
                } else {
                    String aa = IsbnUtils.obscure(isbn.getText().toString());
                    loadPage(aa);
                }
            }
        });
    }

    private void loadPage(String bookId) {
        String uid = Preference.instance(mContext)
                .getString(Preference.uid);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("method", "book.getBook");
        params.put("bid", bookId);
        params.put("uid", uid);
        HttpRequest.loadWithMap(params)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String json, int id) {
                        if (TextUtils.isEmpty(json)) {
                            Toast.makeText(CaptureActivity.this, R.string.netError, Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(json);
                                BookDto dto = new BookDto();
                                dto.of(jsonObject.getJSONObject("book"));
                                if (TextUtils.isEmpty(dto.title) || dto.title.equals("null")) {
                                    sorry_count++;
                                    //若三次扫描都来到这，找不到这本书的可能比较大
                                    if (sorry_count>3){
                                        Toast.makeText(CaptureActivity.this, R.string.noThisBook, Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    sorry_count = 0;
                                    Bundle bundle = new Bundle();
                                    bundle.putString("name", dto.title);
                                    bundle.putString("cover", dto.cover);
                                    bundle.putString("auth", dto.author);
                                    bundle.putString("isbn", dto.id);
                                    bundle.putString("publisher", dto.publisher);
                                    bundle.putString("school", Preference.instance(mContext)
                                            .getString(Preference.SchoolCode));
                                    bundle.putBoolean("isFromCapture",true);
                                    bundle.putString("allJson",json);
                                    bundle.putString(ContentActivity.FRAG_CLS, BookDetailFragment.class.getName());
                                    CaptureActivity.this.startActivity(bundle, ContentActivity.class);
                                    Log.w("guanglog", "bookId" + dto.id);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_IMAGE:
                String a = "file:///" + data.getStringExtra("lamge");
                ContentResolver cr = this.getContentResolver();
                Uri uri = null;
                Bitmap bm = null;
                try {
                    uri = Uri.parse(a);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (bm != null)
                    bm.recycle();
                String b = "";
                try {
                    //对bitmap进行压缩
//                    Bitmap original = BitmapFactory.decodeStream(cr.openInputStream(uri));
//                    ByteArrayOutputStream out = new ByteArrayOutputStream();
//                    original.compress(Bitmap.CompressFormat.PNG, 100, out);
//                    original = null;
//                    bm = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
//                    out = null;
                    bm = BitmapFactory.decodeStream(cr.openInputStream(uri));
                    b = deCode(bm);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if (b != null ) {
                    if (URLUtil.isNetworkUrl(b)){
                        Uri uri1 = Uri.parse(b);
                        startActivity(new Intent(Intent.ACTION_VIEW,uri1));
                    }else if (CheckUtil.isNumeric(b)){
                        if ( b.length() == 13){
                            String aa = IsbnUtils.obscure(b);
                            sorry_count = 4;
                            loadPage(aa);
//                    Toast.makeText(getApplicationContext(), aa, Toast.LENGTH_LONG).show();
                        }
                    }else {
                        new AlertDialog.Builder(CaptureActivity.this).setTitle(R.string.content2weima)
                                .setMessage(b).create().show();
//                        Toast.makeText(getApplicationContext(), R.string.notResolveType, Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(getApplicationContext(), R.string.notResolveType, Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    public String deCode(Bitmap bitmap) {
        if (bitmap == null) {
            Log.i("deCode", "-----------------------null");
            return "null";
        }
        //防止图片文件过大崩溃
        if (bitmap.getHeight()*bitmap.getHeight() >= 12979200){
            Toast.makeText(getApplicationContext(), "抱歉，图片文件过大", Toast.LENGTH_LONG).show();
            return null;
        }

        Hashtable<DecodeHintType, Object> hints = null;
        initHints(hints, null, "UTF8");
        MultiFormatReader multiFormatReader = new MultiFormatReader();
        multiFormatReader.setHints(hints);

        LuminanceSource source = new BitmapLuance(bitmap);

        BinaryBitmap bit = new BinaryBitmap(new HybridBinarizer(source));

        try {
            return multiFormatReader.decodeWithState(bit).getText();

        } catch (ReaderException re) {
            // continue
        } finally {
            multiFormatReader.reset();
        }
        return null;
    }

    public void initHints(Hashtable<DecodeHintType, Object> hints,
                          Vector<BarcodeFormat> decodeFormats, String CODE_STYLE) {
        hints = new Hashtable<DecodeHintType, Object>(2);
        if (decodeFormats == null || decodeFormats.isEmpty()) {
            decodeFormats = new Vector<BarcodeFormat>();
            decodeFormats.addAll(MyDecodeFormatManager.ONE_D_FORMATS);
            decodeFormats.addAll(MyDecodeFormatManager.QR_CODE_FORMATS);
            decodeFormats.addAll(MyDecodeFormatManager.DATA_MATRIX_FORMATS);
        }

        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        if (CODE_STYLE != null) {
            Log.i("initHints", "-----------------------4");
        }

    }


    /**
     * @param str 待验证的字符串
     * @return 如果是符合网址格式的字符串, 返回<b>true</b>,否则为<b>false</b>
     */
    public boolean isHomepage(String str) {
        String regex = "http://(([a-zA-z0-9]|-){1,}\\.){1,}[a-zA-z0-9]{1,}-*";
        return match(regex, str);
    }

    /**
     * @param regex 正则表达式字符串
     * @param str   要匹配的字符串
     * @return 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
     */
    private boolean match(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
}