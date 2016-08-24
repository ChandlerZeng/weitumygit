package com.libtop.weituR.activity.main.videoUpload;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.libtop.weitu.R;
import com.libtop.weituR.activity.ContentActivity;
import com.libtop.weituR.activity.main.adapter.VideoListAdapter;
import com.libtop.weituR.activity.main.dto.VideoFolderBean;
import com.libtop.weituR.activity.main.upload.UploadFileActivity;
import com.libtop.weituR.base.BaseActivity;
import com.libtop.weituR.http.HttpRequest;
import com.libtop.weituR.tool.Preference;
import com.libtop.weituR.utils.DisplayUtils;
import com.libtop.weituR.utils.selector.utils.AlertDialogUtil;
import com.libtop.weituR.utils.selector.view.MyAlertDialog;
import com.libtop.weituR.widget.listview.XListView;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnItemClick;
import okhttp3.Call;

//视频库列表Activity
public class VideoSelectActivity extends BaseActivity implements VideoListAdapter.OnOptionImgClickListener {
    @Bind(R.id.back_btn)
    ImageView mBackBtn;
    @Bind(R.id.title)
    TextView mTitleText;
    @Bind(R.id.commit)
    TextView mCommitBtn;

    @Bind(R.id.lv_video_select)
    XListView mListView;
    private VideoListAdapter mAdapter;

    private List<VideoFolderBean> mInfos = new ArrayList<VideoFolderBean>();

    private int mCurPage = 1;
    private boolean hasData = true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setInjectContentView(R.layout.fragment_video_select);
        mAdapter = new VideoListAdapter(mContext, mInfos, VideoSelectActivity.this);
        setTitle();
        mCommitBtn.setText("上传视频");
        mListView.setAdapter(mAdapter);
        mListView.setPullLoadEnable(false);
        mListView.setXListViewListener(new XListView.IXListViewListener() {
            @Override
            public void onRefresh() {
                mCurPage = 1;
                requestVideos();
            }

            @Override
            public void onLoadMore() {
                if (hasData) {
                    requestVideos();
                }
            }
        });
        mCurPage = 1;
//        mGrid.setOnItemClickListener(this);
    }


    @Override
    public void onStart() {
        super.onStart();
        mCurPage = 1;
        requestVideos();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void requestVideos() {
//        for (int i = 0; i < 10; i++) {
//            VideoFolderBean bean = new VideoFolderBean();
//            bean.floderName = i + " floderName";
//            bean.authorityLimit = i +" authorityLimit";
//            bean.videoNum = i +"";
//            mInfos.add(bean);
//        }
//        mAdapter.notifyDataSetChanged();
        //1.获取视频文件夹列表接口
        //http://weitu.bookus.cn/mediaAlbum/query.json?text={"uid":"WEROPOSLDFKSDFOSPFSDFKL","page":1,"method":"mediaAlbum.query"}
//        if (!mInfos.isEmpty()){
//            return;
//        }
        if (mCurPage==1){
            showLoding();
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("page", mCurPage);
        params.put("uid", mPreference.getString(Preference.uid));//lid
        params.put("method", "mediaAlbum.query");
        HttpRequest.loadWithMap(params)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String json, int id) {
                        if (mLoading != null && mLoading.isShowing() && mCurPage == 1) {
                            mLoading.dismiss();
                            mInfos.clear();
                        }


                        if (TextUtils.isEmpty(json)) {
                            if (mContext != null) {
                                Toast.makeText(mContext, R.string.netError, Toast.LENGTH_SHORT).show();
                            }
//                    showToast("没有相关数据");
                            return;
                        }

                        Type collectionType = new TypeToken<List<VideoFolderBean>>() {
                        }.getType();
                        List<VideoFolderBean> beans = new Gson().fromJson(json, collectionType);
                        if (beans.size() < 10) {
                            hasData = false;
                            mListView.setPullLoadEnable(false);
                        } else {
                            hasData = true;
                            mListView.setPullLoadEnable(true);
                        }
                        for (VideoFolderBean bean : beans) {
                            mInfos.add(bean);
                        }
                        mCurPage++;
                        mListView.stopRefresh();
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }


    private void setTitle() {
//        mBackBtn.setOnClickListener(this);
        mTitleText.setText("视频库");
//        mCommitBtn.setOnClickListener(this);
    }

    @Nullable
    @OnClick({R.id.back_btn, R.id.commit, R.id.ll_new_folderBtn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                onBackPressed();
                break;
            case R.id.commit:
                uploadVideo();
                break;
            case R.id.ll_new_folderBtn:
                newFolder();
                break;
        }
    }

    private void newFolder() {
        Bundle bd = new Bundle();
        bd.putString(ContentActivity.FRAG_CLS, VideoNewFolderFragment.class.getName());
        bd.putBoolean(ContentActivity.FRAG_ISBACK, false);
        bd.putBoolean(ContentActivity.FRAG_WITH_ANIM, true);
        mContext.startActivity(bd, ContentActivity.class);
    }

    private void uploadVideo() {
        Bundle bd = new Bundle();
        bd.putString(ContentActivity.FRAG_CLS, VideoUploadFragment.class.getName());
        bd.putBoolean(ContentActivity.FRAG_ISBACK, false);
        bd.putBoolean(ContentActivity.FRAG_WITH_ANIM, true);
        mContext.startActivity(bd, ContentActivity.class);
    }


    //    @Nullable @OnClick(R.id.imgBtn_video_choice)
//    private void onllVideoClick(View v) {
//        switch (v.getId()){
//            case R.id.imgBtn_video_choice:
//                Toast.makeText(getActivity(),"imgBtn_video_choice click",Toast.LENGTH_SHORT).show();
//                break;
//        }
//    }
    @Nullable @OnItemClick(value = R.id.lv_video_select)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //传folder Id过去
        VideoFolderBean videoFolderBean = (VideoFolderBean) parent.getItemAtPosition(position);
        Intent intent = new Intent(this, UploadFileActivity.class);
        intent.putExtra("isVidea", true);
        intent.putExtra("aid", videoFolderBean.id);
        intent.putExtra("albumTitle", videoFolderBean.title);
        startActivity(intent);
//        Toast.makeText(getActivity(),"lv_video_select click",Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onOptionImgTouch(View v, final int position) {
        final PopupWindow popupWindow = DisplayUtils.openPopChoice(mContext, R.layout.popup_choise);
        View popView = popupWindow.getContentView();
        popView.findViewById(R.id.btn_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        popView.findViewById(R.id.tv_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = "您确定要删除？";
                final AlertDialogUtil dialog = new AlertDialogUtil();
                dialog.showDialog(mContext, title, "确定", "取消", new MyAlertDialog.MyAlertDialogOnClickCallBack() {
                    @Override
                    public void onClick() {
                        deleteVideoFolder(position);
                    }
                }, null);
//                AlertDialog alertDialog =  new AlertDialog(mContext,"您确定要删除？");
//                alertDialog.setCallBack(new AlertDialog.CallBack() {
//                    @Override
//                    public void callBack() {
//                        deleteVideoFolder(position);
//                    }
//
//                    @Override
//                    public void cancel() {
//
//                    }
//                });
//                alertDialog.show();
                popupWindow.dismiss();

            }
        });
        popView.findViewById(R.id.tv_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bd = new Bundle();
                bd.putString(ContentActivity.FRAG_CLS, VideoEditFolderFragment.class.getName());
                bd.putBoolean(ContentActivity.FRAG_ISBACK, false);
                bd.putBoolean(ContentActivity.FRAG_WITH_ANIM, true);
                bd.putString("videoFolderBean", new Gson().toJson(mInfos.get(position), VideoFolderBean.class));
                mContext.startActivity(bd, ContentActivity.class);
                popupWindow.dismiss();
            }
        });

//        Toast.makeText(mContext, position + "imgBtn_video_choice click", Toast.LENGTH_SHORT).show();
    }

    private void deleteVideoFolder(final int position) {
        //10.删除文件夹
        //http://weitu.bookus.cn/mediaAlbum/delete.json?text={"id":"aaaaaaaaaaaa","method":"mediaAlbum.delete"}
        showLoding();
        Map<String, Object> params = new HashMap<String, Object>();
        final VideoFolderBean bean = mInfos.get(position);
        params.put("id", bean.id);
        params.put("method", "mediaAlbum.delete");//lid
        HttpRequest.loadWithMap(params)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String json, int id) {
                        dismissLoading();
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            if (((Integer) jsonObject.get("code")) == 1) {
                                Toast.makeText(mContext, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                mInfos.remove(bean);
                                mAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(mContext, "删除失败", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }
}
