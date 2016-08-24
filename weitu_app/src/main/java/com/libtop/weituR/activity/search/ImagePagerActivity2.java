package com.libtop.weituR.activity.search;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.libtop.weitu.R;
import com.libtop.weituR.activity.ContentActivity;
import com.libtop.weituR.activity.login.LoginFragment;
import com.libtop.weituR.activity.search.dto.CommentNeedDto;
import com.libtop.weituR.activity.search.dynamicCardLayout.DynamicCardActivity;
import com.libtop.weituR.eventbus.MessageEvent;
import com.libtop.weituR.http.HttpRequest;
import com.libtop.weituR.tool.Preference;
import com.libtop.weituR.utils.CheckUtil;
import com.libtop.weituR.utils.ContantsUtil;
import com.libtop.weituR.utils.NetworkUtil;
import com.libtop.weituR.utils.ShareSdkUtil;
import com.libtop.weituR.utils.selector.MultiImageSelectorFragment;
import com.libtop.weituR.utils.viewpagerbitmap.HackyViewPager;
import com.libtop.weituR.utils.viewpagerbitmap.PhotoViewAttacher;
import com.libtop.weituR.widget.dialog.TranLoading;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class ImagePagerActivity2 extends FragmentActivity implements View.OnClickListener, PhotoViewAttacher.gotoView {
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
    protected Preference mPreference;
//  ImageView describeImage;
    /**
     * 默认选择集
     */
    public static final String DEFAULT_SELECTED_LIST = "default_list";
    public static final String ID_LIST = "id_list";
    public static final String COMMENT_LIST = "comment_list";
    private int width, height;
    View bottomView;
    //    EditText describe;
    TextView tvTitle, imgTitle, titlePlay;
    private List<View> viewList = new ArrayList<View>();
    private List<PhotoViewAttacher> photoList = new ArrayList<PhotoViewAttacher>();
    // private List<String> commentList = new ArrayList<String>();
    private List<String> idList = new ArrayList<String>();
    private int pageCount;
    private int positionD = 0;
    ImageView collectView, commentView, shareView, imgHead;

    String favorite;
    String cover;
    String categoriesName1;
//    String categoriesName2;
    String uploadUsername;
    String imageID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_pager2);
        where = getIntent().getIntExtra("see_pic", 1);
        mLoading = new TranLoading(this);
        mPreference = new Preference(this);
        WindowManager wm = (WindowManager) ImagePagerActivity2.this.getSystemService(Context.WINDOW_SERVICE);
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
        favorite = getIntent().getStringExtra("favorite");
        cover = getIntent().getStringExtra("cover");
        categoriesName1 = getIntent().getStringExtra("categoriesName1");
//        categoriesName2 = getIntent().getStringExtra("categoriesName2");
        uploadUsername = getIntent().getStringExtra("uploadUsername");
        imageID = getIntent().getStringExtra("imageID");
        list = getIntent().getStringArrayListExtra(DEFAULT_SELECTED_LIST);
        // commentList = getIntent().getStringArrayListExtra(COMMENT_LIST);
        idList = getIntent().getStringArrayListExtra(ID_LIST);
        addView(list);
        mPager = (HackyViewPager) findViewById(R.id.pager);
        bottomView = (View) findViewById(R.id.bottom_view);
        bottomView.setOnClickListener(this);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        positionD = getIntent().getIntExtra("position", 0);
        tvTitle.setText(positionD + 1 + "/" + list.size());
//        if (commentList != null) {
//            String a = commentList.get(positionD);
//
//        }
        if (where == 2)
            bottomView.setVisibility(View.VISIBLE);
        // 更新下标
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

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
        collectView = (ImageView) findViewById(R.id.img_collect);
        commentView = (ImageView) findViewById(R.id.img_comment);
        shareView = (ImageView) findViewById(R.id.img_share);
        imgHead = (ImageView) findViewById(R.id.img_head);
        imgTitle = (TextView) findViewById(R.id.title_img);
        titlePlay = (TextView) findViewById(R.id.tv_play_time);
        bindData(cover, imgHead);
        imgTitle.setText(uploadUsername);
        String str = "";
        if (categoriesName1 != null && !categoriesName1.equals("null"))
            str = categoriesName1;
//        if (categoriesName2 != null && !categoriesName2.equals("null"))
//            str = categoriesName1 + "/" + categoriesName2;
        if (str.length() != 0)
            titlePlay.setText(str);
        collectView.setOnClickListener(this);
        commentView.setOnClickListener(this);
        shareView.setOnClickListener(this);
        if (favorite.equals("1")) {
            collectView.setImageResource(R.drawable.collect);
        } else {
            collectView.setImageResource(R.drawable.collect_no);
        }
//        getDetailData();
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, mPager.getCurrentItem());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.describe:
                bottomView.setBackgroundColor(Color.parseColor("#ffffff"));
                break;
            case R.id.button:
                break;
            case R.id.img_collect:
                collectClick();
                break;
            case R.id.img_comment:
                commentClick();
                break;
            case R.id.img_share:
                shareClick();
                break;
        }
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
                    Toast.makeText(ImagePagerActivity2.this, "修改成功", Toast.LENGTH_SHORT).show();
                    setVis();
                    break;
                case 2:
                    Toast.makeText(ImagePagerActivity2.this, "添加失败", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    mPager.setAdapter(pagerAdapter);
                    mPager.setCurrentItem(positionD);
                    break;
                case 4:
                    favorite = "1";
                    collectView.setImageResource(R.drawable.collect);
                    Toast.makeText(ImagePagerActivity2.this, "收藏成功", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent();
                    intent.putExtra("isCollect",true);
                    setResult(DynamicCardActivity.RESULT_UPDATE,intent);
                    break;
                case 5:
                    favorite = "0";
                    collectView.setImageResource(R.drawable.collect_no);
                    Toast.makeText(ImagePagerActivity2.this, "取消收藏成功", Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent();
                    intent1.putExtra("isCollect",false);
                    setResult(DynamicCardActivity.RESULT_UPDATE,intent1);
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
//                    if (bottomView != null) {
//                        if (bottomView.getVisibility() == View.VISIBLE)
//                            bottomView.setVisibility(View.INVISIBLE);
//                        else
//                            bottomView.setVisibility(View.VISIBLE);
//                    }
                }
            });
            final PhotoViewAttacher mAttacher;
            mAttacher = new PhotoViewAttacher(rbt);
            mAttacher.setGotoView(this);
            photoList.add(mAttacher);
            File imageFile = new File(mlist.get(i));
            if (!imageFile.exists()&&!TextUtils.isEmpty(a)) {
                Picasso.with(ImagePagerActivity2.this).load(a).into(rbt, new Callback() {
                    @Override
                    public void onSuccess() {
                        mAttacher.update();
                    }

                    @Override
                    public void onError() {

                    }
                });
//                ImageLoader.getInstance().init(
//                        ImageLoaderConfiguration.createDefault(ImagePagerActivity2.this));
//                ImageLoader.getInstance().displayImage(a, rbt,
//                        new SimpleImageLoadingListener() {
//                            public void onLoadingStarted(String imageUri, View view) {
//
//                            }
//
//                            public void onLoadingFailed(String imageUri, View view
//                            ) {
//
//                            }
//
//                            public void onLoadingComplete(String imageUri, View view,
//                                                          Bitmap loadedImage) {
//                                mAttacher.update();
//                            }
//                        });
            } else {
                Picasso.with(ImagePagerActivity2.this)
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
    }


    @Override
    public void go() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isActive())
            try {
                inputMethodManager.hideSoftInputFromWindow(ImagePagerActivity2.this.getCurrentFocus().getWindowToken(),
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
        if (bottomView != null) {
            if (bottomView.getVisibility() == View.VISIBLE)
                bottomView.setVisibility(View.INVISIBLE);
            else {
                setVis();
                bottomView.setVisibility(View.VISIBLE);
            }

        }
    }

    //分享点击
    private void shareClick() {
//        UemgShare a = new UemgShare(ImagePagerActivity2.this);
//        a.setImage(list.get(pageCount)).setText("123").share();
        String title = "微图分享";
        String content = "“【图片】"+"”"+ContantsUtil.shareContent;
        String imageUrl = "drawable://" + R.drawable.wbshare;
        ShareSdkUtil.showShareWithLocalImg(ImagePagerActivity2.this,title,content,imageUrl);
    }

    //评论点击,图书type为4
    private void commentClick() {
        Intent intent = new Intent(ImagePagerActivity2.this, CommentActivity.class);
        CommentNeedDto commentNeedDto = new CommentNeedDto();
        commentNeedDto.title = categoriesName1;
        commentNeedDto.author = uploadUsername;
//        commentNeedDto.publisher = categoriesName2;
        commentNeedDto.photoAddress = cover;
        commentNeedDto.tid = idList.get(pageCount);
        commentNeedDto.type = 4;
        intent.putExtra("CommentNeedDto",new Gson().toJson(commentNeedDto));
        startActivity(intent);
//        intent.putExtra("comment_tid", idList.get(pageCount));
//        intent.putExtra("comment_type", "imageAlbum");
//        startActivity(intent);
    }

    //收藏点击
    private void collectClick() {
        if (CheckUtil.isNull(mPreference.getString(Preference.uid))) {
            Bundle bundle2 = new Bundle();
            bundle2.putString(ContentActivity.FRAG_CLS, LoginFragment.class.getName());
            startForResult(bundle2, 100, ContentActivity.class);
        } else {
            putCollect();
        }
    }

    private void putCollect() {
        mLoading.show();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("tid", imageID);
        params.put("uid", mPreference.getString(Preference.uid));
        if (favorite.equals("0")) {
            params.put("type", 4);
            params.put("method", "favorite.save");
        } else {
            params.put("method", "favorite.delete");
        }
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
                                String message = mjson.getString("message");
                                if (message.equals("设置成功")) {
                                    Message msg = updataHandler.obtainMessage();
                                    msg.what = 4;
                                    updataHandler.sendMessage(msg);
                                } else {
                                    Message msg = updataHandler.obtainMessage();
                                    msg.what = 5;
                                    updataHandler.sendMessage(msg);
                                }
                                Bundle bundle = new Bundle();
                                bundle.putBoolean("isDelete",true);
                                EventBus.getDefault().post(new MessageEvent(bundle));
                                if (mLoading.isShowing()) {
                                    mLoading.dismiss();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                if (mLoading.isShowing()) {
                                    mLoading.dismiss();
                                }
                            }
                            return;
                        }

                        if (mLoading.isShowing()) {
                            mLoading.dismiss();
                        }
                    }
                });
    }

    public void getDetailData() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", idList.get(0));
        params.put("ip", NetworkUtil.getLocalIpAddress(ImagePagerActivity2.this));
        params.put("method", "document.get");
        //  params.put("filterType", type);
        //http://www.yuntu.io/image/list.json?aid=56fb2635e4b0a96fbd838dcc
        HttpRequest.loadWithMap(params)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String json, int id) {
                        if (!TextUtils.isEmpty(json)) {

                            try {


                            } catch (Exception e) {
                                e.printStackTrace();

                            }
                            return;
                        }

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100)
            collectClick();
    }

    /**
     * 带回调的跳转
     *
     * @param bundle
     * @param requestCode
     * @param target
     */
    public void startForResult(Bundle bundle, int requestCode, Class<?> target) {
        Intent intent = new Intent(this, target);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.slide_left_in,
                R.anim.slide_left_out);
    }

    void bindData(String url, ImageView image) {

        if (url == null || url.length() == 0)
            return;
        Picasso.with(this)
                .load(url)
                .placeholder(R.drawable.default_image)
                .tag(MultiImageSelectorFragment.TAG)
                .fit()
                .centerCrop()
                .into(image);

    }

}
