package com.libtop.weitu.activity.main.upload;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.main.adapter.UploadAdapter;
import com.libtop.weitu.activity.main.dto.VideoBean;
import com.libtop.weitu.activity.main.videoUpload.VideaState;
import com.libtop.weitu.activity.main.videoUpload.VideoCompleteFolderFragment;
import com.libtop.weitu.activity.main.videoUpload.VideoMoveActivity;
import com.libtop.weitu.activity.main.videoUpload.VideoPlayActivity3;
import com.libtop.weitu.activity.main.videoUpload.VideoUploadFragment;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.utils.Preference;
import com.libtop.weitu.utils.DisplayUtil;
import com.libtop.weitu.utils.selector.utils.AlertDialogUtil;
import com.libtop.weitu.utils.selector.view.MyAlertDialog;
import com.libtop.weitu.widget.dialog.TranLoading;
import com.libtop.weitu.widget.view.XListView;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;


public class UploadFileActivity extends Activity implements UploadAdapter.OnOptionImgClickListener
{

    XListView listview;
    private int count = 0;
    private Bundle mBundle;
    private UploadAdapter uploadAdapter;
    List<VideoBean> mlist = new ArrayList<VideoBean>();
    List<VideoBean> mlist2 = new ArrayList<VideoBean>();
    Thread thread = new Thread();
    UploadService uploadService;
    protected TranLoading mLoading;
    private String aid;
    private String albumTitle;
    private String viUrl;
    /**
     * 用户ID
     */
    private String uid;
    /**
     * 文件ID
     */
    private String fid;
    private String uploadUrl;
    private int uploadPost;
    private Context context;
    protected Preference mPreference;
    VideoBean videoBean;
    private int headhead = 0;
    private boolean isVidea = false;


    private Cursor cursor;
    /**
     * 记录ID数据库
     */
    private DBHelper dp;


    private List<VideoBean> noList;
    /**
     * 页数
     */
    private int pageCount = 1;
    boolean up = false;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_file);
        mLoading = new TranLoading(UploadFileActivity.this);
        mPreference = new Preference(UploadFileActivity.this);
        uid = mPreference.getString(Preference.uid);
        mBundle = getIntent().getExtras();
        context = UploadFileActivity.this;
        isVidea = getIntent().getBooleanExtra("isVidea", false);
        String json = mBundle.getString("videoBean");
        up = mBundle.getBoolean("up");

        aid = getIntent().getStringExtra("aid");
        albumTitle = getIntent().getStringExtra("albumTitle");
        String tilte = "默认文件";
        videoBean = new Gson().fromJson(json, VideoBean.class);
        if (videoBean != null)
        {
            videoBean.state = "状态:待上传";
            tilte = videoBean.videoAlbum;
            mlist.add(videoBean);
            if (aid == null)
            {
                aid = videoBean.videoFolderId;
            }
        }
        dp = new DBHelper(this);
        cursor = dp.query("SELECT * FROM yuntu", null);
        int count = cursor.getCount();
        if (count != 0)
        {
            noList = new ArrayList<VideoBean>();
            for (int i = 0; i < count; i++)
            {
                cursor.moveToPosition(i);
                VideoBean a = new VideoBean();
                String tablename = cursor.getString(0);
                String url = cursor.getString(1);
                a.videoId = tablename;
                a.filePath = url;
                noList.add(a);
            }
        }


        uploadAdapter = new UploadAdapter(UploadFileActivity.this, mlist, UploadFileActivity.this);
        listview = (XListView) findViewById(R.id.upload_list);
        listview.setAdapter(uploadAdapter);
        listview.setPullLoadEnable(false);
        listview.setXListViewListener(new XListView.IXListViewListener()
        {
            @Override
            public void onRefresh()
            {
                reRequestLoad();
            }


            @Override
            public void onLoadMore()
            {
                pageCount++;
                getVideoList();
            }
        });
        if (up)
        {
            listview.setPullRefreshEnable(false);
        }
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                exitApp();
            }
        });
        if (albumTitle != null)
        {
            tilte = albumTitle;
        }
        ((TextView) findViewById(R.id.title)).setText(tilte);
        TextView right = (TextView) findViewById(R.id.commit);
        right.setText("上传视频");
        right.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent myIntent = new Intent();
                Bundle bundle2 = new Bundle();
                bundle2.putString(ContentActivity.FRAG_CLS, VideoUploadFragment.class.getName());
                if (!TextUtils.isEmpty(albumTitle))
                {
                    bundle2.putString("albumName", albumTitle);
                }
                if (!TextUtils.isEmpty(aid))
                {
                    bundle2.putString("videoFolderId", aid);
                }
                if (mlist.size() > 0)
                {
                    VideoBean vb = mlist.get(0);
                    if (vb != null &&
                            !TextUtils.isEmpty(vb.albumName) &&
                            !TextUtils.isEmpty(vb.videoFolderId))
                    {
                        bundle2.putString("albumName", vb.albumName);
                        bundle2.putString("videoFolderId", vb.videoFolderId);
                    }
                }
                myIntent.putExtras(bundle2);
                myIntent.setClass(UploadFileActivity.this, ContentActivity.class);
                startActivity(myIntent);
                finish();
            }
        });

        getUploadUrl();
    }


    private void reRequestLoad()
    {
        pageCount = 1;
        mlist2.clear();
        getVideoList();
    }


    private void UploadFid(final String fileUrl, int position)
    {
        String a;
        if (fid == null)
        {
            getFid(position, fileUrl);
        }
        else
        {
            startUpload(fileUrl, position);
        }

    }


    private void getFid(final int position, final String fileUrl)
    {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("uid", uid);
        params.put("aid", mlist.get(position).videoFolderId);
        params.put("title", mlist.get(position).titleChange);
        params.put("introduction", mlist.get(position).desc);
        params.put("duration", mlist.get(position).videDduration / 1000);
        params.put("size", mlist.get(position).videoSize);
        params.put("method", "media.save");
        if (mLoading != null && !mLoading.isShowing())
        {
            mLoading.show();
        }
        HttpRequest.loadWithMap(params).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {

            }


            @Override
            public void onResponse(String json, int id)
            {
                if (!TextUtils.isEmpty(json))
                {
                    try
                    {
                        if (mLoading.isShowing())
                        {
                            mLoading.dismiss();
                        }
                        JSONObject mjson = new JSONObject(json);
                        fid = mjson.getString("id");
                        dp.insertTalbe(fid, mlist.get(position).filePath);
                        startUpload(fileUrl, position);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                        if (mLoading.isShowing())
                        {
                            mLoading.dismiss();
                        }
                    }
                    return;
                }
                if (mLoading.isShowing())
                {
                    mLoading.dismiss();
                }
            }
        });
    }


    public void startUpload(final String fileUrl, int position)
    {
        Log.e("123", "");
        headhead++;
        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                uploadService = new UploadService(uploadUrl, uploadPost, UploadFileActivity.this, updataHandler, uploadAdapter.pView.get(count), 1);
                try
                {
                    File file = new File(fileUrl);
                    uploadService.upload(uid, fid, file);
                }
                catch (Exception e)
                {
                }
            }

        };
        if (thread.isAlive())
        {
            uploadService.stopSocket();
            Message msg = updataHandler.obtainMessage();
            msg.what = 2;
            updataHandler.sendMessage(msg);
        }
        else
        {
            thread = new Thread(runnable);
            thread.start();
            Message msg = updataHandler.obtainMessage();
            msg.what = 3;
            updataHandler.sendMessage(msg);
        }
    }


    private Handler updataHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 1:
                    int progress = msg.arg1;
                    mlist.get(count).progress = progress;
                    uploadAdapter.pView.get(count).setProgress(progress);
                    uploadAdapter.setData(mlist);
                    uploadAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    mlist.get(count).state = "状态:暂停";
                    uploadAdapter.setData(mlist);
                    uploadAdapter.notifyDataSetInvalidated();
                    break;
                case 3:
                    mlist.get(count).state = "状态:上传中";
                    uploadAdapter.setData(mlist);
                    uploadAdapter.notifyDataSetInvalidated();
                    break;
                case 4:
                    mlist.get(count).state = "状态:上传完毕";
                    uploadAdapter.setData(mlist);
                    uploadAdapter.notifyDataSetInvalidated();
                    dp.Droptablename(fid);
                    break;
                case 5:
                    reRequestLoad();
                    Toast.makeText(UploadFileActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                    break;
                case 6:
                    Toast.makeText(UploadFileActivity.this, "设置封面成功", Toast.LENGTH_SHORT).show();
                    break;
                case 7:
                    String a = mlist.get(0).filePath;
                    UploadFid(a, 0);
                    break;
                default:
                    break;
            }
        }
    };

    private Handler updataHandler2 = new Handler()
    {
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 1:
                    listview.stopRefresh();
                    if (msg.arg1 == 0)
                    {
                        listview.setPullLoadEnable(false);
                    }
                    else
                    {
                        listview.setPullLoadEnable(true);
                    }
                    removal();
                    break;
                case 3:
                    int position = msg.arg2;
                    Intent intent = new Intent(UploadFileActivity.this, VideoPlayActivity3.class);
                    intent.putExtra(VideoPlayActivity3.MEDIA_PATH, viUrl);
                    // intent.putExtra(VideoPlayActivity3.MEDIA_PATH, mlist.get(position).thumbPath + ".mp4");
                    intent.putExtra(VideoPlayActivity3.MEDIA_NAME, "视频预览");
                    intent.putExtra("notShowButtom", true);
                    intent.putExtra("videoBean", new Gson().toJson(mlist.get(position)));
                    startActivity(intent);
                    break;
            }
        }
    };


    private void removal()
    {
        Log.e("123", "");

        if (noList == null)
        {
            uploadAdapter.setData(mlist);
            uploadAdapter.notifyDataSetChanged();
            if (mlist.size() == 0 || mlist == null)
            {
                return;
            }
            String a = mlist.get(0).filePath;
            UploadFid(a, 0);
            count = 0;
        }
        else
        {
            for (int i = 0; i < mlist.size(); i++)
            {
                String a = mlist.get(i).videoId;
                if (a == null)
                {
                    break;
                }
                for (int j = 0; j < noList.size(); j++)
                {
                    if (a.equals(noList.get(j).videoId))
                    {
                        mlist.get(i).state = "未上传完毕";
                        mlist.get(i).filePath = noList.get(j).filePath;
                    }
                }
            }
            uploadAdapter.setData(mlist);
            uploadAdapter.notifyDataSetChanged();
            if (mlist.size() == 0)
            {
                return;
            }
            String a = mlist.get(0).filePath;
            UploadFid(a, 0);
            count = 0;
        }
    }


    private void getUploadUrl()
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("method", "node.server");
        mLoading.show();
        HttpRequest.loadWithMap(params).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {

            }


            @Override
            public void onResponse(String json, int id)
            {
                if (!TextUtils.isEmpty(json))
                {
                    try
                    {
                        JSONObject mjson = new JSONObject(json);
                        uploadUrl = mjson.getString("ip");
                        uploadPost = mjson.getInt("port");
                        getVideoList();
                        if (mLoading.isShowing())
                        {
                            mLoading.dismiss();
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                        if (mLoading.isShowing())
                        {
                            mLoading.dismiss();
                        }
                    }
                    return;
                }
                if (mLoading.isShowing())
                {
                    mLoading.dismiss();
                }
            }
        });
    }


    private void getVideoList()
    {
        mLoading.show();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("aid", aid);
        params.put("uid", mPreference.getString(Preference.uid));
        params.put("page", pageCount);
        params.put("method", "media.query");
        HttpRequest.loadWithMap(params).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {

            }


            @Override
            public void onResponse(String json, int id)
            {
                if (!TextUtils.isEmpty(json))
                {
                    //   showToast("没有相关数据");
                    try
                    {
                        JSONObject ajson = new JSONObject(json);
                        JSONArray mjson = ajson.getJSONArray("mediaList");
                        JSONObject bjson = ajson.getJSONObject("mediaAlbum");
                        String filename = bjson.getString("title");
                        Log.w("guanglog", json);

                        if (videoBean != null)
                        {
                            mlist2.add(videoBean);
                        }

                        for (int i = 0; i < mjson.length(); i++)
                        {
                            JSONObject njson = mjson.getJSONObject(i);
                            String title = njson.isNull("title") ? "" : njson.getString("title");
                            String aid = njson.isNull("aid") ? "" : njson.getString("aid");
                            String introduction = njson.isNull("introduction") ? "" : njson.getString("introduction");
                            String albumName = njson.isNull("albumName") ? "" : njson.getString("albumName");
                            String url = njson.isNull("url") ? "" : njson.getString("url");
                            String cover = njson.isNull("cover") ? "" : njson.getString("cover");
                            long timeline = njson.isNull("timeline") ? 0 : njson.getLong("timeline");
                            long duration = njson.isNull("duration") ? 0 : njson.getLong("duration");
                            int state = njson.isNull("state") ? 99 : njson.getInt("state");
                            long mSize = njson.isNull("size") ? 0 : njson.getLong("size");
                            String uploadUid = njson.isNull("id") ? "" : njson.getString("id");
                            VideoBean a = new VideoBean();
                            a.albumName = filename;
                            a.introduction = introduction;
                            a.title = title;
                            a.titleChange = title;
                            a.createTime = timeline;
                            a.videDduration = duration;
                            a.videoFolderId = aid;
                            String str = "状态:" + VideaState.getState(state);
                            a.state = str;
                            a.videoSize = mSize;
                            a.videoId = uploadUid;
                            a.coverUrl = cover;
                            a.thumbPath = url;
                            mlist2.add(a);
                        }

                        getList();
                        Message msg = updataHandler2.obtainMessage();
                        msg.what = 1;
                        if (mjson.length() < 10)
                        {
                            msg.arg1 = 0;
                        }
                        else
                        {
                            msg.arg1 = 1;
                        }
                        updataHandler2.sendMessage(msg);

                        if (mLoading.isShowing())
                        {
                            mLoading.dismiss();
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                        if (mLoading.isShowing())
                        {
                            mLoading.dismiss();
                        }
                    }
                    return;
                }
                if (json.equals("1"))
                {
                    if (mLoading.isShowing())
                    {
                        mLoading.dismiss();
                    }
                }
                if (mLoading.isShowing())
                {
                    mLoading.dismiss();
                }
            }
        });
    }


    private void getList()
    {
        mlist.clear();
        for (int i = 0; i < mlist2.size(); i++)
        {
            mlist.add(mlist2.get(i));
        }
    }


    @Override
    public void onOptionImgTouch(View v, final int position)
    {
        final PopupWindow popupWindow = DisplayUtil.openPopChoice(UploadFileActivity.this, R.layout.popup_choise2);
        View popView = popupWindow.getContentView();
        final String mid = mlist.get(position).videoId;
        popView.findViewById(R.id.line_view).setVisibility(View.VISIBLE);
        popView.findViewById(R.id.tv_set_albumImg).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mLoading.show();
                popupWindow.dismiss();
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("mid", mid);
                params.put("method", "mediaAlbum.setCover");
                HttpRequest.loadWithMap(params).execute(new StringCallback()
                {
                    @Override
                    public void onError(Call call, Exception e, int id)
                    {

                    }


                    @Override
                    public void onResponse(String json, int id)
                    {
                        if (!TextUtils.isEmpty(json))
                        {
                            try
                            {
                                JSONObject mjson = new JSONObject(json);
                                int code = mjson.getInt("code");
                                if (code == 1)
                                {
                                    Message msg = updataHandler.obtainMessage();
                                    msg.what = 1;
                                    updataHandler.sendMessage(msg);
                                }
                                if (mLoading.isShowing())
                                {
                                    mLoading.dismiss();
                                }
                            }
                            catch (JSONException e)
                            {
                                e.printStackTrace();
                                if (mLoading.isShowing())
                                {
                                    mLoading.dismiss();
                                }
                            }
                            return;
                        }
                        if (mLoading.isShowing())
                        {
                            mLoading.dismiss();
                        }
                    }
                });

            }
        });
        popView.findViewById(R.id.tv_move).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                popupWindow.dismiss();
                Intent myIntent = new Intent(UploadFileActivity.this, VideoMoveActivity.class);
                String id = mlist.get(position).videoId;
                if (id == null)
                {
                    Toast.makeText(UploadFileActivity.this, "文件还未上传", Toast.LENGTH_SHORT).show();
                    return;
                }
                myIntent.putExtra("fid", id);
                startActivity(myIntent);
                context.getCacheDir();


            }
        });
        popView.findViewById(R.id.tv_edit).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent myIntent = new Intent();
                Bundle bundle2 = new Bundle();
                VideoBean a = mlist.get(position);
                a.fromWhere = UploadFileActivity.class.getName();
                String json = new Gson().toJson(a);
                bundle2.putString(ContentActivity.FRAG_CLS, VideoCompleteFolderFragment.class.getName());
                bundle2.putString("videoBean", json);
                myIntent.putExtras(bundle2);
                myIntent.setClass(UploadFileActivity.this, ContentActivity.class);
                startActivity(myIntent);
                popupWindow.dismiss();
            }
        });
        popView.findViewById(R.id.tv_delete).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                popupWindow.dismiss();
                String title = "您确定要删除？";
                final AlertDialogUtil dialog = new AlertDialogUtil();
                dialog.showDialog(UploadFileActivity.this, title, "确定", "取消", new MyAlertDialog.MyAlertDialogOnClickCallBack()
                {
                    @Override
                    public void onClick()
                    {
                        DeleteVideo(popupWindow, position);
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


    private void DeleteVideo(PopupWindow popupWindow, int position)
    {
        mLoading.show();
        popupWindow.dismiss();
        String id = mlist.get(position).videoId;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
        params.put("method", "media.delete");
        HttpRequest.loadWithMap(params).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {

            }


            @Override
            public void onResponse(String json, int id)
            {
                if (!TextUtils.isEmpty(json))
                {
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
                        if (mLoading.isShowing())
                        {
                            mLoading.dismiss();
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                        if (mLoading.isShowing())
                        {
                            mLoading.dismiss();
                        }
                    }
                    return;
                }
                if (mLoading.isShowing())
                {
                    mLoading.dismiss();
                }
            }
        });
    }


    @Override
    public void onImageTouch(View v, int position)
    {
        VideoBean videoBean = mlist.get(position);
        String a = videoBean.videoId;
        getVideoUrl(a, position);
    }


    @Override
    public void onUpload(View v, int position)
    {
        String state = mlist.get(position).state;
        if (state.equals("状态:待上传") || state.equals("未上传完毕") || state.equals("状态:暂停") || state.equals("状态:上传中"))
        {
            String a = mlist.get(position).filePath;
            String id = mlist.get(position).videoId;
            if (id != null && id.length() != 0)
            {
                fid = id;
            }
            UploadFid(a, position);
            count = position;
        }
        else
        {
            getFileUrl(mlist.get(position).videoId, position);

        }


    }


    private void getVideoUrl(String id, int position)
    {
        Intent intent = new Intent(UploadFileActivity.this, VideoPlayActivity3.class);
        intent.putExtra(VideoPlayActivity3.MEDIA_PATH, mlist.get(position).thumbPath + ".mp4");
        intent.putExtra(VideoPlayActivity3.MEDIA_NAME, "视频预览");
        intent.putExtra("notShowButtom", true);
        intent.putExtra("videoBean", new Gson().toJson(mlist.get(position)));
        startActivity(intent);
    }


    public void exitApp()
    {
        if (uploadService != null)
        {
            if (uploadService.isWorking)
            {
                showMyDialog();
            }
            else
            {
                finish();
            }
        }
        else
        {
            finish();
        }
    }


    /**
     * 退出提醒
     */
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {

        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            exitApp();
        }
        return false;
    }


    private void showMyDialog()
    {
        String title = "有视频正在上传，需要退出吗?";
        final AlertDialogUtil dialog = new AlertDialogUtil();
        dialog.showDialog(UploadFileActivity.this, title, "确定", "我错了", new MyAlertDialog.MyAlertDialogOnClickCallBack()
        {
            @Override
            public void onClick()
            {
                if (thread.isAlive())
                {
                    uploadService.stopSocket();
                    finish();
                }
                else
                {
                    finish();
                }
            }
        }, null);
    }


    private void getFileUrl(String id, final int count)
    {
        mLoading.show();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", id);
        params.put("method", "xfile.get");
        HttpRequest.loadWithMap(params).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {

            }


            @Override
            public void onResponse(String json, int id)
            {
                if (!TextUtils.isEmpty(json))
                {
                    //   showToast("没有相关数据");
                    try
                    {
                        JSONObject ajson = new JSONObject(json);
                        JSONObject mjson = ajson.getJSONObject("data");
                        viUrl = mjson.getString("url") + ".mp4";
                        Message msg = updataHandler2.obtainMessage();
                        msg.what = 3;
                        msg.arg1 = count;
                        updataHandler2.sendMessage(msg);

                        if (mLoading.isShowing())
                        {
                            mLoading.dismiss();
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                        if (mLoading.isShowing())
                        {
                            mLoading.dismiss();
                        }
                    }
                    return;
                }
                if (json.equals("1"))
                {
                    if (mLoading.isShowing())
                    {
                        mLoading.dismiss();
                    }
                }
                if (mLoading.isShowing())
                {
                    mLoading.dismiss();
                }
            }
        });
    }


    @Override
    protected void onResume()
    {
        super.onResume();

    }
}
