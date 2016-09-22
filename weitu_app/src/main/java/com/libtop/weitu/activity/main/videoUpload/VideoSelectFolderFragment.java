package com.libtop.weitu.activity.main.videoUpload;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.ContentFragment;
import com.libtop.weitu.activity.main.adapter.VideoListAdapter2;
import com.libtop.weitu.activity.main.dto.VideoFolderBean;
import com.libtop.weitu.eventbus.MessageEvent;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.DisplayUtil;
import com.libtop.weitu.utils.selector.utils.AlertDialogUtil;
import com.libtop.weitu.utils.selector.view.MyAlertDialog;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
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


/**
 * Created by LianTu on 2016/4/21.
 */
public class VideoSelectFolderFragment extends ContentFragment implements VideoListAdapter2.OnOptionImgClickListener
{
    @Bind(R.id.back_btn)
    ImageView mBackBtn;
    @Bind(R.id.title)
    TextView mTitleText;

    @Bind(R.id.lv_video_select)
    ListView mListVew;
    private VideoListAdapter2 mAdapter;

    private List<VideoFolderBean> mInfos = new ArrayList<VideoFolderBean>();

    private Bundle bm;
    private Bundle bm_sent;

    private Bundle aa;

    private boolean isNew = false;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mAdapter = new VideoListAdapter2(mContext, mInfos, VideoSelectFolderFragment.this);

    }


    @Override
    protected int getLayoutId()
    {
        return R.layout.fragment_video_select_folder;
    }


    @Override
    public void onCreation(View root)
    {
        setTitle();
        if (bm != null)
        {
            bm = null;
        }
        mListVew.setAdapter(mAdapter);

    }


    @Override
    public void onStart()
    {
        super.onStart();
        if (isNew)
        {
            bm_sent = new Bundle();
            bm_sent.putString("target", VideoCompleteFolderFragment.class.getName());
            bm_sent.putBoolean("isnew", true);
            EventBus.getDefault().post(new MessageEvent(bm_sent));
            onBackPressed();
        }
        else
        {
            requestVideos();
        }
    }


    @Override
    public void onDestroy()
    {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    private void requestVideos()
    {
        mAdapter.notifyDataSetChanged();
        //1.获取视频文件夹列表接口
        //http://weitu.bookus.cn/mediaAlbum/query.json?text={"uid":"WEROPOSLDFKSDFOSPFSDFKL","page":1,"method":"mediaAlbum.query"}
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
                mInfos.clear();
                Type collectionType = new TypeToken<List<VideoFolderBean>>()
                {
                }.getType();
                List<VideoFolderBean> beans = new Gson().fromJson(json, collectionType);
                for (VideoFolderBean bean : beans)
                {
                    mInfos.add(bean);
                }
                mAdapter.notifyDataSetChanged();
            }

        });
    }


    // Called in Android UI's main thread
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(MessageEvent event)
    {
        if (!TextUtils.isEmpty(event.message.getString("title")))
        {
            bm = event.message;
            bm.getString("title");
            bm.getString("desc");
            bm.getString("sort");
            isNew = bm.getBoolean("isnew");
        }
    }


    private void setTitle()
    {
        mTitleText.setText("存放目录");
    }


    @Nullable
    @OnClick({R.id.back_btn, R.id.commit, R.id.ll_new_folderBtn})
    public void onClick(View v)
    {
        switch (v.getId())
        {
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


    private void newFolder()
    {
        Bundle bd = new Bundle();
        bd.putString(ContentActivity.FRAG_CLS, VideoNewFolderFragment.class.getName());
        bd.putBoolean(ContentActivity.FRAG_ISBACK, true);
        bd.putBoolean(ContentActivity.FRAG_WITH_ANIM, true);
        mContext.startActivity(bd, ContentActivity.class);
    }


    private void uploadVideo()
    {
        Bundle bd = new Bundle();
        bd.putString(ContentActivity.FRAG_CLS, VideoUploadFragment.class.getName());
        bd.putBoolean(ContentActivity.FRAG_ISBACK, true);
        bd.putBoolean(ContentActivity.FRAG_WITH_ANIM, true);
        mContext.startActivity(bd, ContentActivity.class);
    }


    @Nullable
    @OnItemClick(value = R.id.lv_video_select)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        VideoFolderBean videoFolderBean = (VideoFolderBean) parent.getItemAtPosition(position);
        bm_sent = new Bundle();
        bm_sent.putString("target", VideoCompleteFolderFragment.class.getName());
        bm_sent.putString("folderTitle", videoFolderBean.title);
        bm_sent.putString("folderId", videoFolderBean.id);
        EventBus.getDefault().post(new MessageEvent(bm_sent));
        onBackPressed();
    }


    @Override
    public void onOptionImgTouch(View v, final int position)
    {
        final PopupWindow popupWindow = DisplayUtil.openPopChoice(mContext, R.layout.popup_choise);
        View popView = popupWindow.getContentView();
        popView.findViewById(R.id.btn_cancle).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                popupWindow.dismiss();
            }
        });
        popView.findViewById(R.id.tv_delete).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String title = "您确定要删除？";
                final AlertDialogUtil dialog = new AlertDialogUtil();
                dialog.showDialog(mContext, title, "确定", "取消", new MyAlertDialog.MyAlertDialogOnClickCallBack()
                {
                    @Override
                    public void onClick()
                    {
                        deleteVideoFolder(position);
                    }
                }, null);
                popupWindow.dismiss();

            }
        });
        popView.findViewById(R.id.tv_edit).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Bundle bd = new Bundle();
                bd.putString(ContentActivity.FRAG_CLS, VideoEditFolderFragment.class.getName());
                bd.putBoolean(ContentActivity.FRAG_ISBACK, true);
                bd.putBoolean(ContentActivity.FRAG_WITH_ANIM, true);
                bd.putString("videoId", mInfos.get(position).id);
                mContext.startActivity(bd, ContentActivity.class);
                popupWindow.dismiss();
            }
        });

    }


    private void deleteVideoFolder(final int position)
    {
        //10.删除文件夹
        //http://weitu.bookus.cn/mediaAlbum/delete.json?text={"id":"aaaaaaaaaaaa","method":"mediaAlbum.delete"}
        showLoding();
        Map<String, Object> params = new HashMap<String, Object>();
        final VideoFolderBean bean = mInfos.get(position);
        params.put("id", bean.id);
        params.put("method", "mediaAlbum.delete");//lid
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
                try
                {
                    JSONObject jsonObject = new JSONObject(json);
                    if (((Integer) jsonObject.get("code")) == 1)
                    {
                        showToast(jsonObject.getString("message"));
                        mInfos.remove(bean);
                        mAdapter.notifyDataSetChanged();
                    }
                    else
                    {
                        showToast("删除失败");
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
}
