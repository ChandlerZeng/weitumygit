package com.libtop.weituR.utils.viewpagerbitmap;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.libtop.weitu.R;
import com.libtop.weituR.http.HttpRequest;
import com.libtop.weituR.utils.selector.MultiImageSelectorFragment;
import com.libtop.weituR.widget.dialog.TranLoading;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class ImagePagerActivity extends FragmentActivity implements View.OnClickListener, PhotoViewAttacher.gotoView {
    private static final String STATE_POSITION = "STATE_POSITION";
    public static final String EXTRA_IMAGE_INDEX = "image_index";
    public static final String EXTRA_IMAGE_URLS = "image_urls";
    public static final String EXTRA_TITLE = "title";
    private int where = 1;
    protected TranLoading mLoading;
    private HackyViewPager mPager;
    private int pagerPosition;
    //TextView indicator;
    List<String> list = new ArrayList<String>();
    TextView title, back;
    Button backbutton;
    private String titlename;
    ImageView describeImage;
    /**
     * 默认选择集
     */
    public static final String DEFAULT_SELECTED_LIST = "default_list";
    public static final String ID_LIST = "id_list";
    public static final String COMMENT_LIST = "comment_list";
    private int width, height;
    View bottomView;
    EditText describe;
    TextView button, tvTitle;
    private List<View> viewList = new ArrayList<View>();
    private List<PhotoViewAttacher> photoList = new ArrayList<PhotoViewAttacher>();
    private List<String> commentList = new ArrayList<String>();
    private List<String> idList = new ArrayList<String>();
    private int pageCount;
    private int positionD = 0;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.image_detail_pager);
        where = getIntent().getIntExtra("see_pic", 1);
        mLoading = new TranLoading(this);
        WindowManager wm = (WindowManager) ImagePagerActivity.this.getSystemService(Context.WINDOW_SERVICE);
        int width = 0;
        int height = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            wm.getDefaultDisplay().getSize(size);
            width = size.x;
            height = size.y;
        } else {
            width = wm.getDefaultDisplay().getWidth();
            height = wm.getDefaultDisplay().getHeight();
        }
        this.width = width - 10;
        this.height = height;

        pagerPosition = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);
        String[] urls = getIntent().getStringArrayExtra(EXTRA_IMAGE_URLS);
        titlename = getIntent().getStringExtra(EXTRA_TITLE);

        list = getIntent().getStringArrayListExtra(DEFAULT_SELECTED_LIST);
        commentList = getIntent().getStringArrayListExtra(COMMENT_LIST);
        idList = getIntent().getStringArrayListExtra(ID_LIST);
        addView(list);
        mPager = (HackyViewPager) findViewById(R.id.pager);
        describeImage = (ImageView) findViewById(R.id.describe_image);
        bottomView = (View) findViewById(R.id.bottom_view);
        bottomView.setOnClickListener(this);
        button = (TextView) findViewById(R.id.button);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        button.setOnClickListener(this);
        describe = (EditText) findViewById(R.id.describe);
        describe.setOnClickListener(this);
        positionD = getIntent().getIntExtra("position", 0);
        tvTitle.setText(positionD + 1 + "/" + list.size());
        if (commentList != null) {
            String a = commentList.get(positionD);
            if (a == null || a.equals("")) {
                describe.setText("");
                describe.setHint("添加描述...");
            } else {
                describe.setText(a);
            }
        }
        if (where == 2)
            bottomView.setVisibility(View.VISIBLE);
        // 更新下标
        mPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int arg0) {
                pageCount = arg0;
                setVis();
                tvTitle.setText(pageCount + 1 + "/" + list.size());
                bottomView.setVisibility(View.VISIBLE);
                if (commentList != null) {
                    String a = commentList.get(arg0);
                    if (a == null || a.equals("")) {
                        describe.setText("");
                        describe.setHint("添加描述...");
                    } else {
                        describe.setText(a);
                    }
                }
            }

        });
        if (savedInstanceState != null) {
            pagerPosition = savedInstanceState.getInt(STATE_POSITION);
        }
        // 返回按钮
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void initCommit(){

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, mPager.getCurrentItem());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.describe:
                bottomView.setBackgroundColor(Color.parseColor("#ffffff"));
                button.setVisibility(View.VISIBLE);
                describeImage.setVisibility(View.GONE);
                describe.setFocusable(true);
                describe.setFocusableInTouchMode(true);
                describe.requestFocus();
                break;
            case R.id.button:
                String a = describe.getText().toString();
                if (a == null || a.equals("")) {
                    Toast.makeText(ImagePagerActivity.this, "请写入内容", Toast.LENGTH_SHORT).show();
                    return;
                }


                Map<String, Object> params = new HashMap<String, Object>();
                params.put("id", idList.get(pageCount));
                params.put("introduction", a);
                params.put("method", "image.postIntroduction");
                showLoding();
                HttpRequest.loadWithMap(params)
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {

                            }

                            @Override
                            public void onResponse(String json, int id) {
                                if (!TextUtils.isEmpty(json)) {
                                    //   showToast("没有相关数据");
                                    try {
                                        JSONObject mjson = new JSONObject(json);
                                        int code = mjson.getInt("code");
                                        if (code == 1) {
                                            Message msg = updataHandler.obtainMessage();
                                            msg.what = 1;
                                            updataHandler.sendMessage(msg);
                                        } else {
                                            Message msg = updataHandler.obtainMessage();
                                            msg.what = 2;
                                            updataHandler.sendMessage(msg);
                                        }
                                        dismissLoading();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        dismissLoading();
                                    }
                                    return;
                                }
                                dismissLoading();
                            }
                        });
                break;
        }
    }

    private void showLoding(){
        if (mLoading!=null&&!mLoading.isShowing())
            mLoading.show();
    }

    private void dismissLoading(){
        if (mLoading!=null&&mLoading.isShowing())
            mLoading.dismiss();
    }

    private List<String> changetolist(String[] urls) {
        List<String> mlist = new ArrayList<String>();
        for (int i = 0; i < urls.length; i++) {
            String str = urls[i];
            mlist.add(str);
        }
        return mlist;
    }

    private Handler updataHandler = new Handler() {
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Toast.makeText(ImagePagerActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                    setVis();
                    break;
                case 2:
                    Toast.makeText(ImagePagerActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    mPager.setAdapter(pagerAdapter);
                    mPager.setCurrentItem(positionD);
                    break;
                default:
                    break;
            }
        }
    };

    private void addView(List<String> mlist) {
        for (int i = 0; i < mlist.size(); i++) {
            String a = mlist.get(i);
            LayoutInflater mInflater = (LayoutInflater) this
                    .getSystemService(LAYOUT_INFLATER_SERVICE);
            final View rb = (View) mInflater.inflate(R.layout.image_detail_fragment,
                    null);
            rb.setId(i);
            ImageView rbt;
            rbt = (ImageView) rb.findViewById(R.id.image);
            rb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (bottomView != null) {
                        if (bottomView.getVisibility() == View.VISIBLE)
                            bottomView.setVisibility(View.INVISIBLE);
                        else
                            bottomView.setVisibility(View.VISIBLE);
                    }
                }
            });
            final PhotoViewAttacher mAttacher;
            mAttacher = new PhotoViewAttacher(rbt);
            mAttacher.setGotoView(this);
            photoList.add(mAttacher);
            File imageFile = new File(mlist.get(i));
            if (!imageFile.exists()) {
                if (TextUtils.isEmpty(a))
                    return;
                Picasso.with(ImagePagerActivity.this).load(a).fit().into(rbt, new Callback() {
                    @Override
                    public void onSuccess() {
                        mAttacher.update();
                    }

                    @Override
                    public void onError() {

                    }
                });
//                ImageLoader.getInstance().init(
//                        ImageLoaderConfiguration.createDefault(ImagePagerActivity.this));
//                ImageLoader.getInstance().displayImage(a, rbt,
//                        new SimpleImageLoadingListener() {
//                            public void onLoadingStarted(String imageUri, View view) {
//
//                            }
//
//                            public void onLoadingFailed(String imageUri, View view
//                            ) {
//
//
//                            }
//
//                            public void onLoadingComplete(String imageUri, View view,
//                                                          Bitmap loadedImage) {
//
//                                mAttacher.update();
//                            }
//                        });
            } else {
                Picasso.with(ImagePagerActivity.this)
                        .load(imageFile)
                        .placeholder(R.drawable.default_error)
                        .tag(MultiImageSelectorFragment.TAG)
                        .resize(width, width)
                        .centerCrop()
                        .into(rbt);

            }
            viewList.add(rb);
        }
        Thread a = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message msg = updataHandler.obtainMessage();
                msg.what = 3;
                updataHandler.sendMessage(msg);
            }
        });
        a.start();

    }

    PagerAdapter pagerAdapter = new PagerAdapter() {

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {

            return arg0 == arg1;
        }

        @Override
        public int getCount() {

            return viewList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position,
                                Object object) {
            container.removeView(viewList.get(position));

        }

        @Override
        public int getItemPosition(Object object) {

            return super.getItemPosition(object);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return "title";
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewList.get(position));


            photoList.get(position).update();
            return viewList.get(position);
        }

    };

    private void setVis() {
        bottomView.setBackgroundColor(Color.parseColor("#000000"));
        button.setVisibility(View.GONE);
        describeImage.setVisibility(View.VISIBLE);
    }


    @Override
    public void go() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isActive())
            try {
                inputMethodManager.hideSoftInputFromWindow(ImagePagerActivity.this.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        if (bottomView != null) {
            if (bottomView.getVisibility() == View.VISIBLE)
                bottomView.setVisibility(View.INVISIBLE);
            else {
                setVis();
                bottomView.setVisibility(View.VISIBLE);
            }

        }
    }

    @Override
    public void getView() {

    }
}