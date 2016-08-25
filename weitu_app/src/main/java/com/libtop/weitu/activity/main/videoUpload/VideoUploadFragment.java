package com.libtop.weitu.activity.main.videoUpload;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.ContentFragment;
import com.libtop.weitu.activity.main.adapter.BitmapCache;
import com.libtop.weitu.activity.main.adapter.VideoUploadAdapter;
import com.libtop.weitu.activity.main.dto.VideoBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by LianTu on 2016/4/25.
 */
public class VideoUploadFragment extends ContentFragment {
    @Bind(R.id.title)
    TextView mTitleText;

    @Bind(R.id.gridview)
    GridView mGridView;

    private VideoUploadAdapter mAdapter;

    private List<VideoBean> mInfos = new ArrayList<VideoBean>();

    private Cursor cursor;

    private String albumName,videoFolderId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new VideoUploadAdapter(mContext, mInfos);
        Bundle bundle = ((ContentActivity)getActivity()).getCurrentExtra();
        albumName =bundle.getString("albumName");
        videoFolderId = bundle.getString("videoFolderId");
        getVideos();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_video_upload;
    }

    @Override
    public void onCreation(View root) {
        setTitle();
        BitmapCache.InitBitmapCache();
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                VideoBean videoBean = (VideoBean) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(albumName)&&!TextUtils.isEmpty(videoFolderId)){
                    videoBean.albumName = albumName;
                    videoBean.videoFolderId = videoFolderId;
                }
                Bundle bd = new Bundle();
                bd.putString(ContentActivity.FRAG_CLS, VideoCompleteFolderFragment.class.getName());
                bd.putBoolean(ContentActivity.FRAG_ISBACK, true);
                bd.putBoolean(ContentActivity.FRAG_WITH_ANIM, true);
                bd.putString("videoBean", new Gson().toJson(videoBean));
                mContext.startActivity(bd, ContentActivity.class);
            }
        });
//        mGrid.setOnItemClickListener(this);

    }

    private void getVideos() {

        String[] thumbColumns = new String[]{
                MediaStore.Video.Thumbnails.DATA,
                MediaStore.Video.Thumbnails.VIDEO_ID
        };

        String[] mediaColumns = new String[]{
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DURATION

        };

        //首先检索SDcard上所有的video
        cursor = mContext.managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mediaColumns, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                VideoBean info = new VideoBean();

                try {

                    info.filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                    info.mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
                    info.title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                    info.videoSize = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
                    info.videDduration = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                    info.createTime = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN));
                } catch (Exception e) {

                }


                //获取当前Video对应的Id，然后根据该ID获取其Thumb
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                String selection = MediaStore.Video.Thumbnails.VIDEO_ID + "=?";
                String[] selectionArgs = new String[]{
                        id + ""
                };
//              Cursor thumbCursor =  getContentResolver().query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, thumbColumns, selection, selectionArgs, null);
                Cursor thumbCursor = mContext.managedQuery(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI, thumbColumns, selection, selectionArgs, null);

                if (thumbCursor.moveToFirst()) {
                    info.thumbPath = thumbCursor.getString(thumbCursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA));

                }

                //然后将其加入到videoList
                mInfos.add(info);

            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    @Nullable
    @OnClick({R.id.back_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                onBackPressed();
                break;
        }
    }

    private void setTitle() {
        mTitleText.setText("本地视频");
    }
}
