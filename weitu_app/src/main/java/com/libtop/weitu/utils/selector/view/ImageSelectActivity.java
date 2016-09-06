package com.libtop.weitu.utils.selector.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.main.dto.VideoBean;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.DisplayUtils;
import com.libtop.weitu.utils.selector.adapter.UploadAdapter2;
import com.libtop.weitu.utils.selector.utils.AlertDialogUtil;
import com.libtop.weitu.widget.dialog.TranLoading;
import com.libtop.weitu.widget.listview.XListView;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;


public class ImageSelectActivity extends Activity implements UploadAdapter2.OnOptionImgClickListener
{
    public static final int SELECT_RETURNQ = 20;
    TextView goView;
    XListView mListVew;
    private UploadAdapter2 uploadAdapter;
    List<VideoBean> mlist = new ArrayList<VideoBean>();
    protected TranLoading mLoading;
    protected Preference mPreference;
    //1为正常 2为移动文件夹
    private int choosePic = 1;
    private int pageCount = 1;
    List<VideoBean> mlist2 = new ArrayList<VideoBean>();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_select);
        goView = (TextView) findViewById(R.id.commit);
        mLoading = new TranLoading(ImageSelectActivity.this);
        mPreference = new Preference(ImageSelectActivity.this);
        choosePic = getIntent().getIntExtra("choosePic", 1);
        if (choosePic == 2)
        {
            goView.setVisibility(View.GONE);
        }
        goView.setText("上传照片");
        ((TextView) findViewById(R.id.title)).setText("照片库");
        goView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(ImageSelectActivity.this, MultiImageSelectorActivity2.class);
                // 是否显示拍摄图片
                intent.putExtra(MultiImageSelectorActivity2.EXTRA_SHOW_CAMERA, false);
                // 最大可选择图片数量
                intent.putExtra(MultiImageSelectorActivity2.EXTRA_SELECT_COUNT, 9);
                // 选择模式
                intent.putExtra(MultiImageSelectorActivity2.EXTRA_SELECT_MODE, MultiImageSelectorActivity2.MODE_MULTI);
                intent.putExtra("isUpload", 1);
                // 默认选择
                startActivity(intent);
            }
        });
        //getVideoList();
        mListVew = (XListView) findViewById(R.id.lv_video_select);
        mListVew.setPullLoadEnable(false);
        mListVew.setXListViewListener(new XListView.IXListViewListener()
        {
            @Override
            public void onRefresh()
            {
                pageCount = 1;
                getVideoList();
            }


            @Override
            public void onLoadMore()
            {
                pageCount++;
                getVideoList();
            }
        });
        //首次网络请求
        pageCount = 1;
        getVideoList();
        //
        uploadAdapter = new UploadAdapter2(ImageSelectActivity.this, mlist, ImageSelectActivity.this);
        mListVew.setAdapter(uploadAdapter);
        mListVew.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (position > mlist.size())
                {
                    pageCount++;
                    getVideoList();
                }
                int mpo = position - 1;
                String aid = mlist.get(mpo).videoId;
                String tags = mlist.get(mpo).mimeType;
                String title = mlist.get(mpo).titleChange;
                String categoriesName1 = mlist.get(mpo).desc;
                int label1 = mlist.get(mpo).coverSequence;
                String introduction = mlist.get(mpo).introduction;
                if (choosePic == 1)
                {
                    Intent intent = new Intent(ImageSelectActivity.this, ImageEditActivity.class);
                    intent.putExtra("iswhere", 1);
                    intent.putExtra("title", title);
                    intent.putExtra("tags", tags);
                    intent.putExtra("label1", label1);
                    intent.putExtra("categoriesName1", categoriesName1);
                    intent.putExtra("introduction", introduction);
                    intent.putExtra("aid", aid);
                    intent.putExtra("uid", mPreference.getString(Preference.uid));
                    startActivity(intent);
                }
                else
                {
                    Intent data = new Intent();
                    data.putExtra("aid", aid);
                    setResult(SELECT_RETURNQ, data);
                    finish();
                }
            }
        });
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        //        pageCount = 1;
        //        getVideoList();
    }


    private void getVideoList()
    {
        showLoding();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("page", pageCount);
        params.put("uid", mPreference.getString(Preference.uid));//lid
        params.put("method", "imageAlbum.query");
        HttpRequest.loadWithMap(params).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {

            }


            @Override
            public void onResponse(String json, int reqestId)
            {
                if (!TextUtils.isEmpty(json))
                {
                    //   showToast("没有相关数据");
                    try
                    {
                        JSONArray mjson = new JSONArray(json);
                        if (pageCount == 1)
                        {
                            mlist2.clear();
                        }
                        for (int i = 0; i < mjson.length(); i++)
                        {
                            VideoBean a = new VideoBean();
                            JSONObject njson = mjson.getJSONObject(i);
                            String title = njson.isNull("title") ? "" : njson.getString("title");
                            String id = njson.isNull("id") ? "" : njson.getString("id");
                            String uid = njson.isNull("uploadUid") ? "" : njson.getString("uploadUid");
                            String categoriesName1 = njson.isNull("categoriesName1") ? "" : njson.getString("categoriesName1");
                            if (!njson.isNull("tags"))
                            {
                                JSONArray likes = njson.getJSONArray("tags");
                                String tag = changeList(likes);
                                a.mimeType = tag;
                            }
                            // String tag = njson.isNull("tags") ? "" : njson.getString("tags");
                            int label1 = njson.isNull("label1") ? 0 : njson.getInt("label1");
                            long mSize = njson.isNull("imageCount") ? 0 : njson.getLong("imageCount");
                            String introduction = njson.isNull("introduction") ? "" : njson.getString("introduction");
                            String cover = njson.isNull("cover") ? "" : njson.getString("cover");

                            a.titleChange = title;
                            a.coverSequence = label1;
                            a.introduction = introduction;
                            a.filePath = cover;
                            String str = "";
                            a.desc = categoriesName1;
                            a.videoFolderId = uid;
                            a.state = str;
                            a.videoSize = mSize;
                            a.videoId = id;
                            mlist2.add(a);
                        }
                        pageCount++;
                        Message msg = updataHandler.obtainMessage();
                        msg.what = 1;
                        if (mjson.length() < 10)
                        {
                            msg.arg1 = 0;
                        }
                        else
                        {
                            msg.arg1 = 1;
                        }
                        updataHandler.sendMessage(msg);

                        dismissLoading();
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                        dismissLoading();
                    }
                    return;
                }

                dismissLoading();
            }
        });
    }


    private void showLoding()
    {
        if (mLoading != null && !mLoading.isShowing())
        {
            mLoading.show();
        }
    }


    private void dismissLoading()
    {
        if (mLoading != null && mLoading.isShowing())
        {
            mLoading.dismiss();
        }
    }


    private String changeList(JSONArray json)
    {
        String a = "";
        try
        {

            for (int i = 0; i < json.length(); i++)
            {
                if (i == 0)
                {
                    if (json.length() == 1)
                    {
                        a = json.getString(i);
                    }
                    else
                    {
                        a = json.getString(i) + ",";
                    }
                }
                else
                {
                    a = a + "," + json.getString(i);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return a;
    }


    private Handler updataHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 1:
                    if (msg.arg1 == 0)
                    {
                        mListVew.setPullLoadEnable(false);
                    }
                    else
                    {
                        mListVew.setPullLoadEnable(true);
                    }
                    getList();
                    mListVew.stopRefresh();
                    uploadAdapter.setData(mlist);
                    uploadAdapter.notifyDataSetChanged();
                    break;
                case 2:

                    mListVew.setPullLoadEnable(false);
                    break;
                case 3:
                    break;
                case 5:
                    pageCount = 1;
                    getVideoList();
                    break;
                default:
                    break;
            }
        }
    };


    private void getList()
    {
        mlist.clear();
        for (int i = 0; i < mlist2.size(); i++)
        {
            mlist.add(mlist2.get(i));
        }
    }


    @Override
    public void onOptionImgTouch(View v, int position)
    {
        final PopupWindow popupWindow = DisplayUtils.openPopChoice(ImageSelectActivity.this, R.layout.popup_choise2);
        View popView = popupWindow.getContentView();
        TextView one, two, three, four;
        one = (TextView) popView.findViewById(R.id.tv_set_albumImg);
        one.setText("管理照片");
        three = (TextView) popView.findViewById(R.id.tv_edit);
        three.setText("编辑专辑");
        final String mid = mlist.get(position).videoId;
        final String id = mlist.get(position).videoFolderId;
        final String tags = mlist.get(position).mimeType;
        final String title = mlist.get(position).titleChange;
        final String categoriesName1 = mlist.get(position).desc;
        final int label1 = mlist.get(position).coverSequence;
        final String introduction = mlist.get(position).introduction;
        final String imageUrl = mlist.get(position).filePath;
        popView.findViewById(R.id.tv_set_albumImg).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                Intent intent = new Intent(ImageSelectActivity.this, ImageEditActivity.class);
                intent.putExtra("aid", mid);
                intent.putExtra("uid", mPreference.getString(Preference.uid));
                intent.putExtra("iswhere", 3);
                startActivity(intent);
                popupWindow.dismiss();
            }
        });
        popView.findViewById(R.id.tv_move).setVisibility(View.GONE);
        popView.findViewById(R.id.tv_edit).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(ImageSelectActivity.this, ImageCoverActivity.class);
                intent.putExtra("id", mid);
                intent.putExtra("folder", id);
                intent.putExtra("imageUrl", imageUrl);
                intent.putExtra("title", title);
                intent.putExtra("tags", tags);
                intent.putExtra("label1", label1);
                intent.putExtra("categoriesName1", categoriesName1);
                intent.putExtra("introduction", introduction);
                startActivity(intent);
                popupWindow.dismiss();
            }
        });
        popView.findViewById(R.id.tv_delete).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // deleteVideoFolder(position);
                popupWindow.dismiss();
                String title = "您确定要删除？";
                final AlertDialogUtil dialog = new AlertDialogUtil();
                dialog.showDialog(ImageSelectActivity.this, title, "确定", "取消", new MyAlertDialog.MyAlertDialogOnClickCallBack()
                {
                    @Override
                    public void onClick()
                    {
                        deleteFolder(popupWindow, mid);
                    }
                }, null);

            }
        });
        popView.findViewById(R.id.btn_cancle).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                popupWindow.dismiss();
            }
        });
    }


    private void deleteFolder(PopupWindow popupWindow, String mid)
    {
        showLoding();
        popupWindow.dismiss();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", mid);
        params.put("method", "imageAlbum.delete");
        HttpRequest.loadWithMap(params).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {

            }


            @Override
            public void onResponse(String json, int id)
            {
                // mLoading.dismiss();
                if (!TextUtils.isEmpty(json))
                {
                    //   showToast("没有相关数据");
                    try
                    {
                        JSONObject mjson = new JSONObject(json);
                        int code = mjson.isNull("code") ? 999 : mjson.getInt("code");
                        if (code == 1)
                        {
                            Message msg = updataHandler.obtainMessage();
                            msg.what = 5;
                            updataHandler.sendMessage(msg);
                        }
                        dismissLoading();
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                        dismissLoading();
                    }
                    return;
                }
                dismissLoading();
            }
        });
    }

}
