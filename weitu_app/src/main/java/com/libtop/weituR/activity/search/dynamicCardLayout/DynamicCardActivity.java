package com.libtop.weituR.activity.search.dynamicCardLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.search.ImagePagerActivity2;
import com.libtop.weituR.http.HttpRequest;
import com.libtop.weituR.tool.Preference;
import com.libtop.weituR.utils.CheckUtil;
import com.libtop.weituR.utils.NetworkUtil;
import com.libtop.weituR.utils.selector.MultiImageSelectorFragment;
import com.libtop.weituR.utils.selector.bean.ListGridImage;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import okhttp3.Call;

public class DynamicCardActivity extends Activity implements LazyScrollView.OnScrollListener {
    private static final int COLUMNCOUNT = 3;
    private int columnWidth = 250;// 每个item的宽度
    private int itemHeight = 0;
    private int rowCountPerScreen = 3;
    private int cols = 4;// 当前总列数
    private ArrayList<Integer> colYs = new ArrayList<Integer>();
    private LayoutInflater mInflater;
    RelativeLayout rootView;
    private LinkedHashMap<String, List<ListGridImage>> linkedHashMap2;
    private List<Point> lostPoint = new ArrayList<Point>();// 用于记录空白块的位置
    private int currentPage = 1;
    private LazyScrollView rootScroll;
    private Bundle mBundle;
    protected Preference mPreference;
    private String id;
    private String type;
    private List<ListGridImage> imageList;
    TextView tvTitle;

    //前坑
    ArrayList<String> idList = new ArrayList<String>();
    ArrayList<String> commentList = new ArrayList<String>();
    ArrayList<String> urlList = new ArrayList<String>();

    String favorite;
    String cover;
//    String categoriesName1;
    String categoriesName2;
    String uploadUsername;
    String imageID;
    String title;
    public static final int RESULT_UPDATE = 250;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_dynamic_card);
        linkedHashMap2 = new LinkedHashMap<String, List<ListGridImage>>();
        mPreference = new Preference(this);
        mBundle = getIntent().getExtras();
        id = mBundle.getString("id");
        type = mBundle.getString("type");
        init();
    }

    private void init() {
        rootView = (RelativeLayout) this.findViewById(R.id.rootView);
        rootView.setPersistentDrawingCache(ViewGroup.PERSISTENT_ANIMATION_CACHE);
        rootScroll = (LazyScrollView) this.findViewById(R.id.rootScroll);
        rootScroll.setOnScrollListener(this);
        rootScroll.getView();
        mInflater = getLayoutInflater();
        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        Configuration cf = this.getResources().getConfiguration();
        tvTitle = (TextView) findViewById(R.id.title);
        tvTitle.setText("");
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if (cf.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            rowCountPerScreen = 3;
        } else {
            rowCountPerScreen = 6;
        }
        columnWidth = width / COLUMNCOUNT;
        itemHeight = height / rowCountPerScreen;

        for (int i = 0; i < 4; i++) {
            colYs.add(0);
        }
        getImageList();
    }

    private synchronized void addView(View view, String uri, final int i) {
        placeBrick(view);
        ImageView picView = (ImageView) view.findViewById(R.id.imageView);
        String a = uri;
        if (TextUtils.isEmpty(a))
            return;
        Picasso.with(DynamicCardActivity.this)
                .load(a)
                .tag(MultiImageSelectorFragment.TAG)
                .fit()
                .centerCrop()
                .into(picView);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DynamicCardActivity.this, ImagePagerActivity2.class);
                String str = imageList.get(i).getImageUrl();
                intent.putExtra("position", i);
                intent.putExtra("see_pic", 2);
                intent.putExtra(ImagePagerActivity2.DEFAULT_SELECTED_LIST, urlList);
                intent.putExtra("favorite", favorite);
                intent.putExtra("cover", cover);
//                intent.putExtra("categoriesName1", categoriesName1);
//                intent.putExtra("categoriesName2", categoriesName2);
                intent.putExtra("uploadUsername", uploadUsername);
                intent.putExtra("imageID", imageID);
                intent.putExtra(ImagePagerActivity2.ID_LIST, idList);
                startActivityForResult(intent,1);
            }
        });
        rootView.addView(view);

        // fb.display(picView, uri);
    }

    // 布局算法

    /**
     * 原理：动态规划
     *
     * @param view
     */
    private void placeBrick(View view) {
        ViewGroup.LayoutParams brick = (ViewGroup.LayoutParams) view.getLayoutParams();
        int groupCount, colSpan, rowSpan;
        List<Integer> groupY = new ArrayList<Integer>();
        List<Integer> groupColY = new ArrayList<Integer>();
        colSpan = (int) Math.ceil(brick.width / this.columnWidth);// 计算跨几列
        colSpan = Math.min(colSpan, this.cols);// 取最小的列数
        rowSpan = (int) Math.ceil(brick.height / this.itemHeight);
        Log.e("VideoShowActivity", "colSpan:" + colSpan);
        if (colSpan == 1) {
            groupY = this.colYs;
            // 如果存在白块则从添加到白块中
            if (lostPoint.size() > 0 && rowSpan == 1) {
                Point point = lostPoint.get(0);
                int pTop = point.y;
                int pLeft = this.columnWidth * point.x;// 放置的left
                android.widget.RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        brick.width, brick.height);
                params.leftMargin = pLeft;
                params.topMargin = pTop;
                view.setLayoutParams(params);
                lostPoint.remove(0);
                return;
            }

        } else {// 说明有跨列
            groupCount = this.cols + 1 - colSpan;// 添加item的时候列可以填充的列index
            for (int j = 0; j < groupCount; j++) {
                groupColY = this.colYs.subList(j, j + colSpan);
                groupY.add(j, Collections.max(groupColY));// 选择几个可添加的位置
            }
        }
        int minimumY;

        minimumY = Collections.min(groupY);// 取出几个可选位置中最小的添加
        int shortCol = 0;
        int len = groupY.size();
        for (int i = 0; i < len; i++) {
            if (groupY.get(i) == minimumY) {
                shortCol = i;// 获取到最小y值对应的列值
                break;
            }
        }
        int pTop = minimumY;// 这是放置的Top
        int pLeft = this.columnWidth * shortCol;// 放置的left
        android.widget.RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                brick.width, brick.height);
        params.leftMargin = pLeft;
        params.topMargin = pTop;
        view.setLayoutParams(params);
        if (colSpan != 1) {
            for (int i = 0; i < this.cols; i++) {
                if (minimumY > this.colYs.get(i)) {// 出现空行
                    int y = minimumY - this.colYs.get(i);
                    for (int j = 0; j < y / itemHeight; j++) {
                        lostPoint.add(new Point(i, this.colYs.get(i)
                                + itemHeight * j));
                    }
                }
            }
        }
        int setHeight = minimumY + brick.height, setSpan = this.cols + 1 - len;
        for (int i = 0; i < setSpan; i++) {
            this.colYs.set(shortCol + i, setHeight);
        }
    }


    private void initList() {
        // 动态计算ListView
        if (imageList != null) {
            Random r = new Random();

            for (int i = 0; i < imageList.size(); i++) {
                View v = mInflater.inflate(R.layout.weibo_text_item, null);
                int nextInt = r.nextInt(50);
                // 模拟分为三种情况
                if (nextInt > 40) {
                    // 跨两列两行
                    android.widget.RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            columnWidth * 2, itemHeight * 2);
                    v.setLayoutParams(params);
                } else if (nextInt > 30) {
                    // 跨一列两行
                    android.widget.RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            columnWidth, itemHeight * 2);

                    v.setLayoutParams(params);
                } else if (nextInt > 25) {
                    // 跨两列一行
                    android.widget.RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            columnWidth * 2, itemHeight);

                    v.setLayoutParams(params);
                } else {
                    android.widget.RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            columnWidth, itemHeight);

                    v.setLayoutParams(params);
                }
                addView(v, imageList.get(i).getImageUrl(), i);
            }
        }
    }


    @Override
    public void onBottom() {

    }

    @Override
    public void onAutoScroll(int l, int t, int oldl, int oldt) {

    }


    public void getImageList() {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
        params.put("ip", NetworkUtil.getLocalIpAddress2(DynamicCardActivity.this));
        params.put("method", "imageAlbum.get");
        if (!CheckUtil.isNull(mPreference.getString(Preference.uid))) {
            params.put("uid", mPreference.getString(Preference.uid));
        }
        HttpRequest.loadWithMap(params)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String json, int responseId) {
                        if (!TextUtils.isEmpty(json)) {
                            if (imageList == null)
                                imageList = new ArrayList<ListGridImage>();
                            try {
                                JSONObject mjson = new JSONObject(json);
                                JSONObject njson = mjson.getJSONObject("imageAlbum");
                                favorite = mjson.getString("favorite");
                                cover = njson.getString("cover");
                                title = njson.getString("title");
//                                categoriesName1 = njson.getString("categoriesName1");
//                        categoriesName2 = njson.getString("categoriesName2");
                                uploadUsername = njson.getString("uploadUsername");
                                imageID = njson.getString("id");
                                JSONArray ajson = mjson.getJSONArray("imageList");
                                for (int i = 0; i < ajson.length(); i++) {
                                    ListGridImage bean = new ListGridImage();
                                    JSONObject a = ajson.getJSONObject(i);
                                    String url = a.isNull("url") ? "" : a.getString("url");
                                    String id = a.isNull("id") ? "" : a.getString("id");
                                    // String comment = a.isNull("comment") ? "" : a.getString("comment");
                                    idList.add(id);
                                    // commentList.add(comment);
                                    urlList.add(url);
                                    //bean.setComment(comment);
                                    bean.setId(id);
                                    bean.setImageUrl(url);
                                    imageList.add(bean);
                                }
                                Message msg = handler.obtainMessage();
                                msg.what = 1;
                                handler.sendMessage(msg);

                            } catch (JSONException e) {
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
        if (resultCode == RESULT_UPDATE){
            if (data.getBooleanExtra("isCollect",false)){
                favorite = "1";
            }else {
                favorite = "0";
            }
        }
    }


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    tvTitle.setText(title);
                    initList();
                    break;

                default:
                    break;
            }
        }
    };

}