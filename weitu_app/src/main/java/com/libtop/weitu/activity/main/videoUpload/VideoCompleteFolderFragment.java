package com.libtop.weitu.activity.main.videoUpload;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.ContentFragment;
import com.libtop.weitu.activity.main.dto.VideoBean;
import com.libtop.weitu.activity.main.dto.VideoFolderBean;
import com.libtop.weitu.activity.main.upload.UploadFileActivity;
import com.libtop.weitu.eventbus.MessageEvent;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.tool.Preference;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;


/**
 * Created by LianTu on 2016/4/25.
 * 视频上传的编辑信息页面
 */
public class VideoCompleteFolderFragment extends ContentFragment
{

    @Bind(R.id.title)
    TextView mTitleText;

    @Bind(R.id.tv_sort)
    TextView mSortText;

    @Bind(R.id.et_title)
    EditText mEditTitleText;

    @Bind(R.id.et_desc)
    EditText mDescText;

    @Bind(R.id.btn_new_folder)
    Button mBtnFolder;

    @Bind(R.id.img_video_photo)
    ImageView mImgPhoto;

    @Bind(R.id.btn_video_cover)
    Button mBtnCover;

    @Bind(R.id.commit)
    TextView commitView;

    private Bundle bundle;
    private Bundle bm;
    private VideoBean videoBean;

    private List<VideoFolderBean> mInfos = new ArrayList<VideoFolderBean>();

    private boolean isnew = false;

    private TextWatcher wrongTextWatcher = new TextWatcher()
    {
        private CharSequence temp;
        private int editStart;
        private int editEnd;


        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            temp = s;
        }


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
        }


        @Override
        public void afterTextChanged(Editable s)
        {
            editStart = mEditTitleText.getSelectionStart();
            editEnd = mEditTitleText.getSelectionEnd();

            String limitEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
            Pattern pattern = Pattern.compile(limitEx);
            Matcher m = pattern.matcher(temp);
            if (m.find())
            {
                showToast("不允许输入特殊符号！");
                s.delete(editStart - 1, editEnd);
                int tempSelection = editStart;
                mEditTitleText.setText(s);
                mEditTitleText.setSelection(tempSelection);
            }

        }
    };

    private TextWatcher watcher = new TextWatcher()
    {
        private CharSequence temp;
        private int editStart;
        private int editEnd;


        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {
            temp = s;
        }


        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        {
        }


        @Override
        public void afterTextChanged(Editable s)
        {
            editStart = mDescText.getSelectionStart();
            editEnd = mDescText.getSelectionEnd();

            if (temp.length() > 50)
            {
                showToast("你输入的字数已经超过了限制！");
                s.delete(editStart - 1, editEnd);
                int tempSelection = editStart;
                mDescText.setText(s);
                mDescText.setSelection(tempSelection);
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        bundle = ((ContentActivity) getActivity()).getCurrentExtra();
        String json = bundle.getString("videoBean");
        videoBean = new Gson().fromJson(json, VideoBean.class);

    }


    @Override
    protected int getLayoutId()
    {
        return R.layout.fragment_video_complete;
    }


    @Override
    public void onCreation(View root)
    {
        setTitle();
        if (videoBean != null)
        {
            initView();
        }
    }


    @Override
    public void onStart()
    {
        super.onStart();
        if (bm != null)
        {
            if (!TextUtils.isEmpty(bm.getString("videoBean")))
            {
                String vb = bm.getString("videoBean");
                videoBean = new Gson().fromJson(vb, VideoBean.class);
            }
            else if (!TextUtils.isEmpty(bm.getString("folderTitle")))
            {
                videoBean.albumName = bm.getString("folderTitle");
                mSortText.setText(videoBean.albumName);
            }
        }
        if (isnew)
        {
            requestVideos();
        }
    }


    private void initView()
    {
        mEditTitleText.addTextChangedListener(wrongTextWatcher);
        mDescText.addTextChangedListener(watcher);
        if (!TextUtils.isEmpty(videoBean.filePath))
        {
            new AsyncTask<Integer, String, Bitmap>()
            {

                @Override
                protected Bitmap doInBackground(Integer... params)
                {
                    Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(videoBean.filePath, MediaStore.Video.Thumbnails.MINI_KIND);
                    return bitmap;
                }


                //set photoView and holder
                protected void onPostExecute(Bitmap bitmap)
                {
                    if (bitmap != null)
                    {
                        mImgPhoto.setImageBitmap(bitmap);
                    }
                }
            }.execute(0);
        }
        else
        {
            mImgPhoto.setImageResource(R.drawable.default_image);
        }
        if (!TextUtils.isEmpty(videoBean.coverUrl))
        {
            Picasso.with(mContext).load(videoBean.coverUrl).fit().into(mImgPhoto);
        }
        if (!TextUtils.isEmpty(videoBean.titleChange))
        {
            mEditTitleText.setText(videoBean.titleChange);
        }
        if (!TextUtils.isEmpty(videoBean.introduction))
        {
            mDescText.setText(videoBean.introduction);
        }
        if (!TextUtils.isEmpty(videoBean.albumName))
        {
            mSortText.setText(videoBean.albumName);
        }
        else if (!TextUtils.isEmpty(videoBean.titleChange))
        {

            mSortText.setText(videoBean.titleChange);
        }
    }


    @Override
    public void onDestroy()
    {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    @Override
    public void onPause()
    {
        try
        {
            ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mContext.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        catch (Exception e)
        {

        }
        super.onPause();
    }


    @Nullable
    @OnClick({R.id.back_btn, R.id.ll_video_sort, R.id.ll_video_authority, R.id.btn_new_folder, R.id.btn_video_cover, R.id.commit})
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.back_btn:
                onBackPressed();
                break;
            case R.id.ll_video_sort:
                videoSort();
                break;
            case R.id.ll_video_authority:
                videoAuthority();
                break;
            case R.id.btn_new_folder:
                newFolder();
                break;
            case R.id.commit:
                newFolder();
                break;
            case R.id.btn_video_cover:
                videoCover();
                break;
        }
    }


    private void videoCover()
    {
        Bundle bd = new Bundle();
        bd.putString(ContentActivity.FRAG_CLS, VideoCoverFragment.class.getName());
        bd.putBoolean(ContentActivity.FRAG_ISBACK, true);
        bd.putBoolean(ContentActivity.FRAG_WITH_ANIM, true);
        bd.putString("videoBean", new Gson().toJson(videoBean));
        mContext.startActivity(bd, ContentActivity.class);
    }


    private void newFolder()
    {
        //开始上传按钮点击后执行
        if (TextUtils.isEmpty(mEditTitleText.getText()))
        {
            Toast.makeText(getActivity(), "名称不能为空", Toast.LENGTH_SHORT).show();
        }
        else if (mDescText.getText().toString().length() > 50)
        {
        }
        else
        {
            if (TextUtils.isEmpty(videoBean.albumName))
            {
                Toast.makeText(getActivity(), "请选择目录", Toast.LENGTH_SHORT).show();
                return;
            }
            if (videoBean.fromWhere != null && videoBean.fromWhere.equals(UploadFileActivity.class.getName()))
            {
                requestUpdate();
            }
            else
            {
                requestSaveAlbum();
            }
        }

    }


    //修改按钮点击后执行
    private void requestUpdate()
    {
        //4.修改视频属性, id 是该视频的id
        //http://weitu.bookus.cn/media/update.json?text={"id":"WEROPOSLDFKSDFOSPFSDFKL","title":"well","introduction":"enen","method":"media.update"}
        //4.修改视频属性, id 是该视频的id
        //http://weitu.bookus.cn/media/update.json?text={"id":"WEROPOSLDFKSDFOSPFSDFKL","aid":"albumID","title":"well","introduction":"enen","method":"media.update"}
        showLoding();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", videoBean.videoId);
        params.put("title", mEditTitleText.getText().toString());//lid
        params.put("aid", videoBean.videoFolderId);//lid
        params.put("introduction", mDescText.getText().toString());//lid
        params.put("method", "media.update");
        HttpRequest.loadWithMap(params).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {

            }


            @Override
            public void onResponse(String json, int id)
            {
                dismissLoading();
                Log.w("guanglog", "what the fuck + " + json);
                requestSaveCover();
                if (TextUtils.isEmpty(json))
                {
                    showToast(getResources().getString(R.string.netError));
                    return;
                }
            }
        });

    }


    private void requestSaveCover()
    {
        //11.修改封面
        //      #define UploadVideo_UpdateCover_URL DN@"http://weitu.bookus.cn/media/setCover.json?"
        //      http://weitu.bookus.cn/media/setCover.json?text={"id":"1234567890abcdefg","sequence":5,"method":"media.setCover"}
        if (!TextUtils.isEmpty(videoBean.coverUrl))
        {
            showLoding();
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("id", videoBean.videoId);
            params.put("sequence", videoBean.coverSequence);//lid
            params.put("method", "media.setCover");
            HttpRequest.loadWithMap(params).execute(new StringCallback()
            {
                @Override
                public void onError(Call call, Exception e, int id)
                {

                }


                @Override
                public void onResponse(String json, int id)
                {
                    dismissLoading();
                    if (TextUtils.isEmpty(json))
                    {
                        showToast(getResources().getString(R.string.netError));
                        return;
                    }
                    onBackPressed();
                }
            });
        }
        else
        {
            onBackPressed();
        }
    }


    //开始按钮点击后执行
    private void requestSaveAlbum()
    {
        Bundle bundle2 = new Bundle();
        videoBean.titleChange = mEditTitleText.getText().toString();
        videoBean.desc = mDescText.getText().toString();
        videoBean.videoAlbum = mSortText.getText().toString();
        if (bm != null && !TextUtils.isEmpty(bm.getString("folderId")))
        {
            videoBean.videoFolderId = bm.getString("folderId");
        }
        bundle2.putString("videoBean", new Gson().toJson(videoBean));
        bundle2.putBoolean("up", true);
        mContext.startActivity(bundle2, UploadFileActivity.class);
        mContext.finish();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(MessageEvent event)
    {
        if (event.message.getString("target").equals(this.getClass().getName().toString()))
        {
            bm = event.message;
            isnew = bm.getBoolean("isnew");
        }

    }


    private void setTitle()
    {

        if (videoBean.fromWhere == null)
        {
            mTitleText.setText("完善信息");
            return;
        }

        if (videoBean.fromWhere.equals(UploadFileActivity.class.getName()))
        {
            mTitleText.setText("编辑信息");
            mBtnFolder.setText("上传");
            mBtnCover.setVisibility(View.VISIBLE);
        }
        else
        {
            mTitleText.setText("完善信息");
        }
    }


    private void videoAuthority()
    {
        Bundle bd = new Bundle();
        bd.putString(ContentActivity.FRAG_CLS, VideoAuthorityFragment.class.getName());
        bd.putBoolean(ContentActivity.FRAG_ISBACK, true);
        bd.putBoolean(ContentActivity.FRAG_WITH_ANIM, true);
        mContext.startActivity(bd, ContentActivity.class);
    }


    private void videoSort()
    {
        Bundle bd = new Bundle();
        bd.putString(ContentActivity.FRAG_CLS, VideoSelectFolderFragment.class.getName());
        bd.putBoolean(ContentActivity.FRAG_ISBACK, true);
        bd.putBoolean(ContentActivity.FRAG_WITH_ANIM, true);
        mContext.startActivity(bd, ContentActivity.class);
    }


    private void requestVideos()
    {
        showLoding();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("page", 1);
        params.put("uid", mPreference.getString(Preference.uid));//lid
        params.put("method", "mediaAlbum.query");
        HttpRequest.loadWithMap(params).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {

            }


            @Override
            public void onResponse(String json, int id)
            {
                dismissLoading();
                if (TextUtils.isEmpty(json))
                {
                    showToast("没有相关数据");
                    return;
                }

                Type collectionType = new TypeToken<List<VideoFolderBean>>()
                {
                }.getType();
                List<VideoFolderBean> beans = new Gson().fromJson(json, collectionType);
                for (VideoFolderBean bean : beans)
                {
                    mInfos.add(bean);
                }
                if (mInfos.size() > 0)
                {
                    mSortText.setText(mInfos.get(0).title);
                    videoBean.albumName = mInfos.get(0).title;
                    videoBean.videoId = mInfos.get(0).id;
                    videoBean.videoFolderId = mInfos.get(0).id;
                }
            }
        });
    }

}
