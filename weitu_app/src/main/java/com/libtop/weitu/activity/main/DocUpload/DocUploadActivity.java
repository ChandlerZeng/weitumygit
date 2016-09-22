package com.libtop.weitu.activity.main.DocUpload;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.main.adapter.UploadDocAdapter;
import com.libtop.weitu.activity.main.dto.DocBean;
import com.libtop.weitu.activity.main.upload.UploadService;
import com.libtop.weitu.activity.main.videoUpload.VideaState;
import com.libtop.weitu.activity.source.PdfActivity;
import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.DisplayUtil;
import com.libtop.weitu.utils.StringUtil;
import com.libtop.weitu.utils.selector.utils.AlertDialogUtil;
import com.libtop.weitu.utils.selector.view.MyAlertDialog;
import com.libtop.weitu.widget.listview.XListView;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;


/**
 * Created by LianTu on 2016/5/4.
 * 文档上传页面
 */
public class DocUploadActivity extends BaseActivity implements UploadDocAdapter.OnOptionImgClickListener
{

    @Bind(R.id.back_btn)
    ImageView mBackBtn;
    @Bind(R.id.title)
    TextView mTitleText;
    @Bind(R.id.commit)
    TextView mCommitBtn;

    @Bind(R.id.upload_list)
    XListView mListview;
    private UploadDocAdapter mAdapter;
    private UploadService uploadService;

    ArrayList<DocBean> mlist = new ArrayList<DocBean>();

    private int mCurPage = 1;
    private boolean hasData = true;

    private String uploadUrl;
    private int uploadPost;
    private int count = 0;
    Thread thread = new Thread();
    private String uid;
    private boolean isUpload;
    private String docFilePath;

    private int fileType;

    private boolean isUploaded = true;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setInjectContentView(R.layout.activity_doc_upload);
        initView();
        getUploadUrl();
    }


    private void requestDoc()
    {
        //        请求文档列表
        //        http://weitu.bookus.cn/document/query.json?text={"uid":"565bea2c984ec06f56befda3","page":1,"method":"document.query"}
        if (mCurPage == 1)
        {
            showLoding();
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("uid", Preference.instance(mContext).getString(Preference.uid));
        params.put("page", mCurPage);
        params.put("method", "document.query");
        HttpRequest.loadWithMap(params).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {

            }


            @Override
            public void onResponse(String json, int id)
            {
                if (mLoading != null && mLoading.isShowing() && mCurPage == 1)
                {
                    mLoading.dismiss();
                    mlist.clear();
                }
                if (TextUtils.isEmpty(json))
                {
                    Toast.makeText(mContext, R.string.netError, Toast.LENGTH_SHORT).show();
                    return;
                }
                mListview.stopRefresh();
                Type collectionType = new TypeToken<List<DocBean>>()
                {
                }.getType();
                List<DocBean> docBeans = new Gson().fromJson(json, collectionType);
                if (docBeans == null)
                {
                    return;
                }
                if (docBeans.size() < 10)
                {
                    hasData = false;
                    mListview.setPullLoadEnable(false);
                }
                else
                {
                    hasData = true;
                    mListview.setPullLoadEnable(true);
                }
                for (DocBean bean : docBeans)
                {
                    if (bean.state != null)
                    {
                        bean.stateString = "状态:" + VideaState.getState(bean.state);
                    }
                }
                mlist.addAll(docBeans);
                if (isUpload && !TextUtils.isEmpty(docFilePath))
                {
                    mlist.get(0).filePath = docFilePath;
                    startUpload(mlist.get(0).filePath, 0);
                    count = 0;
                    isUpload = false;
                }
                mCurPage++;
                Message msg = updataHandler2.obtainMessage();
                msg.what = 1;
                updataHandler2.sendMessage(msg);
                mAdapter.notifyDataSetChanged();
            }
        });
    }


    private void initView()
    {
        uid = Preference.instance(mContext).getString(Preference.uid);
        mTitleText.setText("文件列表");
        mCommitBtn.setText("上传文档");
        mAdapter = new UploadDocAdapter(mContext, mlist, this);
        mListview.setAdapter(mAdapter);
        mListview.setPullLoadEnable(false);
        mListview.setXListViewListener(new XListView.IXListViewListener()
        {
            @Override
            public void onRefresh()
            {
                mCurPage = 1;
                requestDoc();
            }


            @Override
            public void onLoadMore()
            {
                if (hasData)
                {
                    requestDoc();
                }
            }
        });
        mCurPage = 1;
    }


    @Nullable
    @OnClick({R.id.back_btn, R.id.commit})
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.back_btn:
                onBackPressed();
                break;
            case R.id.commit:
                showFileChooser();
                break;
        }
    }


    private void lookPdf(String tempUrl)
    {
        if (!TextUtils.isEmpty(tempUrl))
        {
            Intent intent = new Intent();
            intent.putExtra("url", tempUrl);
            intent.setClass(mContext, PdfActivity.class);
            mContext.startActivityForResult(intent, 0x5554);
            mContext.overridePendingTransition(R.anim.zoomin, R.anim.alpha_outto);
        }
    }


    /**
     * 根据返回选择的文件，来进行上传操作
     **/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == Activity.RESULT_OK)
        {
            if (requestCode == 12341)
            {
                mCurPage = 1;
                requestDoc();
                return;
            }
            if (requestCode == 1234)
            {
                isUpload = true;
                docFilePath = data.getStringExtra("filePath");
                mCurPage = 1;
                requestDoc();
                return;
            }

            Uri uri = data.getData();
            String fName = uri.getPath().toString();
            String type = getFileType(fName).toLowerCase();
            if (type.equals("doc") || type.equals("xls") || type.equals("ppt") || type.equals("docx") || type.equals("xlsx") || type.equals("pptx") || type.equals("pdf") || type.equals("odt") || type.equals("ods"))
            {
                fileType = StringUtil.getUpLoadType(type);
                Toast.makeText(mContext, type, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, ContentActivity.class);
                Bundle bd = new Bundle();
                bd.putString(ContentActivity.FRAG_CLS, DocEditFragment.class.getName());
                bd.putBoolean(ContentActivity.FRAG_ISBACK, false);
                bd.putBoolean(ContentActivity.FRAG_WITH_ANIM, true);
                bd.putBoolean("uploadDoc", true);
                bd.putString("docPath", fName);
                intent.putExtras(bd);
                mContext.startActivityForResult(intent, 1234);

            }
            else
            {
                Toast.makeText(mContext, "不支持该格式的文件", Toast.LENGTH_SHORT).show();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public String getFileType(String fName)
    {
        String end = fName.substring(fName.lastIndexOf(".") + 1, fName.length()).toLowerCase();
        return end;
    }


    /**
     * 调用文件选择软件来选择文件
     **/
    private void showFileChooser()
    {
        Intent intent = new Intent(mContext, FileChooserActivity.class);
        startActivityForResult(intent, 1);
    }


    @Override
    public void onOptionImgTouch(View v, final int position)
    {
        final PopupWindow popupWindow = DisplayUtil.openPopChoice(mContext, R.layout.popup_choise);
        View popView = popupWindow.getContentView();

        popView.findViewById(R.id.tv_edit).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(mContext, ContentActivity.class);
                Bundle bd = new Bundle();
                bd.putString(ContentActivity.FRAG_CLS, DocEditFragment.class.getName());
                bd.putBoolean(ContentActivity.FRAG_ISBACK, false);
                bd.putBoolean(ContentActivity.FRAG_WITH_ANIM, true);
                bd.putString("docBean", new Gson().toJson(mlist.get(position)));
                intent.putExtras(bd);
                mContext.startActivityForResult(intent, 12341);
                popupWindow.dismiss();
            }
        });

        //popwindow的删除按钮点击
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
                        requestDelete(position);
                    }
                }, null);
                popupWindow.dismiss();
            }
        });

        //popwindow的取消按钮点击
        popView.findViewById(R.id.btn_cancle).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                popupWindow.dismiss();
            }
        });

    }


    private void requestDelete(final int position)
    {
        //2.删除文档接口
        //        http://weitu.bookus.cn/ document /delete.json?text={"id":"aaaaaaaaaaaa","method":"document.delete"}
        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", mlist.get(position).id);
        params.put("method", "document.delete");
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
                Log.w("guanglog", json);
                if (TextUtils.isEmpty(json))
                {
                    Toast.makeText(mContext, "删除失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                mlist.remove(position);
                mAdapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void onImageTouch(View v, int position)
    {
        if (!TextUtils.isEmpty(mlist.get(position).pdfUrl))
        {
            lookPdf(mlist.get(position).pdfUrl);
        }

    }


    @Override
    public void onUpload(View v, int position)
    {
        if (isUploaded)
        {
            onImageTouch(v, position);
        }
        else
        {
            String a = mlist.get(0).filePath;
            if (!TextUtils.isEmpty(a))
            {
                startUpload(a, 0);
                count = 0;
            }
        }
    }


    private void getUploadUrl()
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("method", "node.server");
        showLoding();
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
                        requestDoc();
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


    private void startUpload(final String fileUrl, final int position)
    {

        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                if (mAdapter.pView.size() == 0)
                {
                    return;
                }
                uploadService = new UploadService(uploadUrl, uploadPost, DocUploadActivity.this, updataHandler, mAdapter.pView.get(count), fileType);
                try
                {
                    File file = new File(fileUrl);
                    String fid = mlist.get(position).id;
                    Log.w("guanglog", "test file id + " + fid);
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


    private Handler updataHandler2 = new Handler()
    {
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 1:
                    mAdapter.setData(mlist);
                    mAdapter.notifyDataSetChanged();
            }
        }
    };

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
                    mAdapter.pView.get(count).setProgress(progress);
                    mAdapter.setData(mlist);
                    mAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    mlist.get(count).stateString = "状态:暂停";
                    mAdapter.setData(mlist);
                    isUploaded = false;
                    mAdapter.notifyDataSetInvalidated();
                    break;
                case 3:
                    mlist.get(count).stateString = "状态:上传中";
                    isUploaded = false;
                    mAdapter.setData(mlist);
                    mAdapter.notifyDataSetInvalidated();
                    break;
                case 4:
                    mlist.get(count).stateString = "状态:上传完毕";
                    isUploaded = true;
                    mAdapter.setData(mlist);
                    mAdapter.notifyDataSetInvalidated();
                    break;
                default:
                    break;
            }
        }
    };
}
