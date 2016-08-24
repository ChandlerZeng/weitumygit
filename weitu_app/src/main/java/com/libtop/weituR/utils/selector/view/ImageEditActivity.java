package com.libtop.weituR.utils.selector.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.main.upload.UploadService;
import com.libtop.weituR.http.HttpRequest;
import com.libtop.weituR.utils.selector.ImageUploadActivity;
import com.libtop.weituR.utils.selector.adapter.ListGridAdapter;
import com.libtop.weituR.utils.selector.bean.ListGridImage;
import com.libtop.weituR.utils.selector.utils.AlertDialogUtil;
import com.libtop.weituR.widget.dialog.TranLoading;
import com.libtop.weituR.widget.listview.XListView;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class ImageEditActivity extends Activity implements View.OnClickListener {
    String aid, uid;
    protected TranLoading mLoading;
    private LinkedHashMap<String, List<ListGridImage>> linkedHashMap;
    private LinkedHashMap<String, List<ListGridImage>> linkedHashMap2;
    ListGridAdapter listGridAdapter;
    XListView listView;
    private int where;
    private ArrayList<String> resultList;
    private String uploadUrl, title;
    private int uploadPost;
    UploadService uploadService;
    Thread thread = new Thread();
    TextView titleView;
    String fid;
    private int count = 0;
    View bottomView;
    private int isCover = 1;
    private boolean stopUp = false;
    private String tags, introduction, categoriesName1;
    private int label1;
    private int pageCount = 1;
    private boolean isagin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit);
        where = getIntent().getIntExtra("iswhere", 1);
        aid = getIntent().getStringExtra("aid");
        uid = getIntent().getStringExtra("uid");
        title = getIntent().getStringExtra("title");
        tags = getIntent().getStringExtra("tags");
        isagin = getIntent().getBooleanExtra("isagin", false);
        categoriesName1 = getIntent().getStringExtra("categoriesName1");
        introduction = getIntent().getStringExtra("introduction");
        label1 = getIntent().getIntExtra("label1", 0);
        mLoading = new TranLoading(ImageEditActivity.this);
        titleView = (TextView) findViewById(R.id.title);
        listView = (XListView) findViewById(R.id.show_image);
        listView.setPullLoadEnable(false);
        listView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                pageCount = 1;
                getImageList();
            }

            @Override
            public void onLoadMore() {
                pageCount++;
                getImageList();
            }
        });
        linkedHashMap = new LinkedHashMap<String, List<ListGridImage>>();
        linkedHashMap2 = new LinkedHashMap<String, List<ListGridImage>>();
        bottomView = (View) findViewById(R.id.bottom_view);
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitApp();
            }
        });
        if (where == 1) {
            listGridAdapter = new ListGridAdapter(ImageEditActivity.this, linkedHashMap);
            listView.setAdapter(listGridAdapter);
            //看图片
            if (!TextUtils.isEmpty(title)) {
                titleView.setText(title);
            }
            getImageList();
            TextView textView = (TextView) findViewById(R.id.going_down);
            textView.setText("上传照片");
            textView.setOnClickListener(this);
        } else if (where == 2) {
            isagin = getIntent().getBooleanExtra("isagin", false);
            listGridAdapter = new ListGridAdapter(ImageEditActivity.this, linkedHashMap);
            listView.setAdapter(listGridAdapter);
            //上传图片
            titleView.setText("上传照片");
            resultList = getIntent().getStringArrayListExtra(ImageUploadActivity.DEFAULT_SELECTED_LIST);
            TextView textView = (TextView) findViewById(R.id.going_down);
            textView.setText("上传照片");
            textView.setOnClickListener(this);
            if (isagin) {
                getImageList();

                getUploadUrl();

            } else {
                linkedHashMap.put("上传图片", changeData());
                listGridAdapter.setData(linkedHashMap);
                listGridAdapter.notifyDataSetChanged();
                getUploadUrl();
            }
        } else if (where == 3) {
            bottomView.setVisibility(View.VISIBLE);
            listGridAdapter = new ListGridAdapter(ImageEditActivity.this, linkedHashMap, true, 1);
            listView.setAdapter(listGridAdapter);
            //管理图片
            titleView.setText("管理照片");
            findViewById(R.id.move).setOnClickListener(this);
            findViewById(R.id.delete).setOnClickListener(this);
            getImageList();
        } else {
            listGridAdapter = new ListGridAdapter(ImageEditActivity.this, linkedHashMap, false, 2);
            listView.setAdapter(listGridAdapter);
            //管理图片
            titleView.setText("编辑封面");
            findViewById(R.id.move).setOnClickListener(this);
            findViewById(R.id.delete).setOnClickListener(this);
            getImageList();
        }

    }


    private List<ListGridImage> changeData() {
        List<ListGridImage> a = new ArrayList<ListGridImage>();
        for (int i = 0; i < resultList.size(); i++) {
            ListGridImage listGridImage = new ListGridImage();
            listGridImage.setImageUrl(resultList.get(i));
            listGridImage.setPro(true);
            a.add(listGridImage);
        }
        return a;
    }

    private void showLoding(){
        if (mLoading!=null&&!mLoading.isShowing())
            mLoading.show();
    }

    private void dismissLoading(){
        if (mLoading!=null&&mLoading.isShowing())
            mLoading.dismiss();
    }

    public void getImageList() {
        showLoding();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("page", pageCount);
        params.put("aid", aid);
        params.put("uid", uid);
        params.put("method", "image.query");
        HttpRequest.loadWithMap(params)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String json, int requestId) {
                        if (!TextUtils.isEmpty(json)) {
                            try {
                                if (pageCount == 1)
                                    linkedHashMap.clear();


                                linkedHashMap2.clear();
                                JSONObject mjson = new JSONObject(json);
                                JSONObject njson = mjson.getJSONObject("images");
                                Iterator it = njson.keys();
                                List<String> keyListstr = new ArrayList<String>();
                                while (it.hasNext()) {
                                    keyListstr.add(it.next().toString());
                                }
                                int pagecount = 0;
                                for (int i = 0; i < keyListstr.size(); i++) {
                                    JSONArray ajson = njson.getJSONArray(keyListstr.get(i));
                                    List<ListGridImage> a = new ArrayList<ListGridImage>();
                                    for (int j = 0; j < ajson.length(); j++) {
                                        JSONObject xjson = ajson.getJSONObject(j);
                                        String id = xjson.isNull("id") ? "" : xjson.getString("id");
                                        String url = xjson.isNull("url") ? "" : xjson.getString("url");
                                        if (TextUtils.isEmpty(url)){
                                            url= "http://nt1.libtop.com/f/"+id+".jpg";
                                        }
                                        String introduction = xjson.isNull("introduction") ? "" : xjson.getString("introduction");
                                        ListGridImage listGridImage = new ListGridImage();
                                        listGridImage.setImageUrl(url);
                                        listGridImage.id = id;
                                        listGridImage.setIntroduction(introduction);
                                        a.add(listGridImage);
                                        pagecount++;
                                    }
                                    addHaspMap(keyListstr.get(i), a);
                                    //  linkedHashMap.put(keyListstr.get(i), a);
                                }
                                Message msg = updataHandler.obtainMessage();
                                msg.what = 2;
                                if (pagecount > 19)
                                    msg.arg1 = 1;
                                else
                                    msg.arg1 = 0;
                                updataHandler.sendMessage(msg);

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
    }


    private void addHaspMap(String key, List<ListGridImage> list) {
        boolean some = false;
        for (Map.Entry<String, List<ListGridImage>> entry : linkedHashMap2.entrySet()) {
            if (key.equals((String) entry.getKey())) {
                addListData((List<ListGridImage>) entry.getValue(), list);
                some = true;
            }
        }
        if (!some) {
            linkedHashMap2.put(key, list);
        }
    }

    private void addListData(List<ListGridImage> olist, List<ListGridImage> tlist) {
        for (int i = 0; i < tlist.size(); i++) {
            olist.add(tlist.get(i));
        }
    }

    private Handler updataHandler = new Handler() {
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if (ImageEditActivity.this == null) {

            }
            switch (msg.what) {
                case 2:
                    if (isagin) {
                        linkedHashMap.put("上传图片", changeData());
                        listView.setPullLoadEnable(false);
                    } else
                        listView.setPullLoadEnable(true);
                    for (Map.Entry<String, List<ListGridImage>> entry : linkedHashMap2.entrySet()) {
                        linkedHashMap.put(entry.getKey(), entry.getValue());
                    }
                    listGridAdapter.setData(linkedHashMap);
                    listGridAdapter.notifyDataSetChanged();
                    listView.stopRefresh();

                    if (msg.arg1 == 1)
                        listView.setPullLoadEnable(true);
                    else
                        listView.setPullLoadEnable(false);

                    break;
                case 3:
                    Toast.makeText(ImageEditActivity.this, "移动成功", Toast.LENGTH_SHORT).show();
                    getImageList();
                    break;
                case 4:
                    if (stopUp)
                        return;
                    List<ListGridImage> a = linkedHashMap.get("上传图片");
                    a.get(count).setPro(false);
                    listGridAdapter.notifyDataSetChanged();
                    count++;
                    if (count < resultList.size())
                        getFid();
                    break;
                case 5:
                    Toast.makeText(ImageEditActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                    getImageList();
                    break;
                case 6:
                    stopUp = true;
                case 7:
                    listView.setPullLoadEnable(false);
                    break;
                default:
                    break;
            }
        }
    };

    private void getUploadUrl() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("method", "node.server");
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
                                uploadUrl = mjson.getString("ip");
                                uploadPost = mjson.getInt("port");
                                getFid();
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
    }


    private void getFid() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("uid", uid);
        params.put("aid", aid);
        params.put("title", title);
        params.put("method", "image.save");
        showLoding();
        HttpRequest.loadWithMap(params)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String json, int id) {
                        // mLoading.dismiss();
                        if (!TextUtils.isEmpty(json)) {
                            //   showToast("没有相关数据");
                            try {
                                dismissLoading();
                                JSONObject mjson = new JSONObject(json);
                                fid = "";
                                fid = mjson.getString("id");
                                startUpload();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                dismissLoading();
                            }
                            return;
                        }
                        dismissLoading();
                    }
                });
    }

    public void startUpload() {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                uploadService = new UploadService(uploadUrl, uploadPost, ImageEditActivity.this, updataHandler, null, 12);
                try {
                    File file = new File(resultList.get(count));
                    uploadService.upload(uid, fid, file);
                } catch (Exception e) {

                }
            }

        };

        thread = new Thread(runnable);
        thread.start();

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.move:
                List<String> alist1 = getListData();
                if (alist1.isEmpty()){
                    Toast.makeText(ImageEditActivity.this,"抱歉！没有图片可以移动",Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(ImageEditActivity.this, ImageSelectActivity.class);
                    intent.putExtra("choosePic", 2);
                    startActivityForResult(intent, 10);
                }
                break;
            case R.id.delete:
                List<String> alist = getListData();
                String[] sss = new String[alist.size()];
                for (int i = 0; i < alist.size(); i++) {
                    sss[i] = alist.get(i);
                }
                showMyDialog2("", sss);
                break;
            case R.id.going_down:
                Intent intent1 = new Intent(ImageEditActivity.this, MultiImageSelectorActivity2.class);
                // 是否显示拍摄图片
                intent1.putExtra(MultiImageSelectorActivity2.EXTRA_SHOW_CAMERA, false);
                // 最大可选择图片数量
                intent1.putExtra(MultiImageSelectorActivity2.EXTRA_SELECT_COUNT, 9);
                // 选择模式
                intent1.putExtra(MultiImageSelectorActivity2.EXTRA_SELECT_MODE, MultiImageSelectorActivity2.MODE_MULTI);
                intent1.putExtra("aid", aid);
                intent1.putExtra("isUpload", 1);
                intent1.putExtra("title", title);
                intent1.putExtra("tags", tags);
                intent1.putExtra("label1", label1);
                intent1.putExtra("categoriesName1", categoriesName1);
                intent1.putExtra("introduction", introduction);
                intent1.putExtra("isagin", true);
                // 默认选择
                startActivity(intent1);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case ImageSelectActivity.SELECT_RETURNQ:
                Bundle ab = data.getExtras();
                String name = ab.getString("aid");
                List<String> alist = getListData();
                String[] sss = new String[alist.size()];
                for (int i = 0; i < alist.size(); i++) {
                    sss[i] = alist.get(i);
                }
                showMyDialog(name, sss);

                break;

        }

    }

    /**
     * 遍历取数据
     */
    private List<String> getListData() {
        List<String> alist = new ArrayList<String>();
        List<ListGridImage> list = new ArrayList<ListGridImage>();
        List<List<ListGridImage>> imageList = new ArrayList<List<ListGridImage>>();
        // Iterator iter = linkedHashMap.entrySet().iterator();
        for (Map.Entry<String, List<ListGridImage>> entry : linkedHashMap.entrySet()) {
            imageList.add((List<ListGridImage>) entry.getValue());
        }

        for (int i = 0; i < imageList.size(); i++) {
            List<ListGridImage> mlist = imageList.get(i);
            for (int j = 0; j < mlist.size(); j++)
                list.add(mlist.get(j));
        }

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).ischeck())
                alist.add(list.get(i).id);
        }
        return alist;
    }

    /**
     * 退出提醒
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitApp();
        }
        return false;
    }

    public void exitApp() {
        if (uploadService != null) {
            if (uploadService.isWorking) {
                showMyDialog3();
            } else {
                finish();
            }
        } else {
            finish();
        }
    }


    private void showMyDialog3() {
        String title = "有图片正在上传，需要退出吗?";
        final AlertDialogUtil dialog = new AlertDialogUtil();
        dialog.showDialog(ImageEditActivity.this, title, "确定", "我错了", new MyAlertDialog.MyAlertDialogOnClickCallBack() {
            @Override
            public void onClick() {
                if (thread.isAlive()) {
                    stopUp = true;
                    uploadService.stopSocket();
                    finish();
                } else {
                    finish();
                }
            }
        }, null);
    }

    private void showMyDialog(final String name, final String[] sss) {
        String title = "里面包含" + sss.length + "张图片确定要移动吗?";
        final AlertDialogUtil dialog = new AlertDialogUtil();
        dialog.showDialog(ImageEditActivity.this, title, "确定", "取消", new MyAlertDialog.MyAlertDialogOnClickCallBack() {
            @Override
            public void onClick() {

                Map<String, Object> params = new HashMap<String, Object>();
                try {
                    JSONArray jsonarray = new JSONArray(Arrays.toString(sss));
                    params.put("id", jsonarray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params.put("aid", name);
                params.put("method", "image.changeAlbum");
                showLoding();
                HttpRequest.loadWithMap(params)
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {

                            }

                            @Override
                            public void onResponse(String json, int id) {
                                if (!TextUtils.isEmpty(json)) {
                                    try {
                                        dismissLoading();
                                        JSONObject mjson = new JSONObject(json);
                                        int codeId = mjson.getInt("code");
                                        if (codeId == 1) {
                                            Message msg = updataHandler.obtainMessage();
                                            msg.what = 3;
                                            updataHandler.sendMessage(msg);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        dismissLoading();
                                    }
                                    return;
                                }
                                dismissLoading();
                            }
                        });
            }
        }, null);
    }

    private void showMyDialog2(final String name, final String[] sss) {
        String title = "里面包含" + sss.length + "张图片确定要删除吗?";
        final AlertDialogUtil dialog = new AlertDialogUtil();
        dialog.showDialog(ImageEditActivity.this, title, "确定", "取消", new MyAlertDialog.MyAlertDialogOnClickCallBack() {
            @Override
            public void onClick() {

                Map<String, Object> params = new HashMap<String, Object>();
                try {
                    JSONArray jsonarray = new JSONArray(Arrays.toString(sss));
                    params.put("id", jsonarray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params.put("method", "image.delete");
                showLoding();
                HttpRequest.loadWithMap(params)
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {

                            }

                            @Override
                            public void onResponse(String json, int id) {
                                if (!TextUtils.isEmpty(json)) {
                                    try {
                                        dismissLoading();
                                        JSONObject mjson = new JSONObject(json);
                                        int codeId = mjson.getInt("code");
                                        if (codeId == 1) {
                                            Message msg = updataHandler.obtainMessage();
                                            msg.what = 5;
                                            updataHandler.sendMessage(msg);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        dismissLoading();
                                    }
                                    return;
                                }
                                dismissLoading();
                            }
                        });
            }
        }, null);
    }

}
