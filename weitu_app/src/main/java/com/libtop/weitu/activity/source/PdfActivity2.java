package com.libtop.weitu.activity.source;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.login.LoginFragment;
import com.libtop.weitu.activity.main.subsubject.SelectSubjectFragment;
import com.libtop.weitu.activity.search.CommentActivity;
import com.libtop.weitu.activity.search.dto.CommentNeedDto;
import com.libtop.weitu.activity.source.Bean.DocResultBean;
import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.eventbus.MessageEvent;
import com.libtop.weitu.fileloader.FileLoader;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.CheckUtil;
import com.libtop.weitu.utils.ContantsUtil;
import com.libtop.weitu.utils.JsonUtil;
import com.libtop.weitu.utils.NetworkUtil;
import com.libtop.weitu.utils.ShareSdkUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;


public class PdfActivity2 extends BaseActivity implements OnPageChangeListener
{
    @Bind(R.id.pdfView)
    PDFView pdfView;
    @Bind(R.id.page)
    TextView title;
    @Bind(R.id.title_container)
    LinearLayout titleContainer;
    @Bind(R.id.page_seekbar)
    SeekBar pageSeekBar;
    @Bind(R.id.img_collect)
    ImageView imgCollect;
    @Bind(R.id.ll_tool_bottom)
    LinearLayout llBottom;

    private boolean showFlag = true;
    private boolean isPageChange = false;
    private boolean rotateFlag = false;
    private int currentPage = 1;
    private int pageCount = 1;
    private int lastPage;
    private String tid;
    private DocResultBean docResultBean;

    private boolean isOpen = true;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setInjectContentView(R.layout.activity_pdf3);
        noNetThanExit(mContext);

        tid = getIntent().getStringExtra("doc_id");
        getPDF();

        tid = getIntent().getStringExtra("doc_id");
    }


    @Nullable
    @OnClick({R.id.ll_tool_include,R.id.ll_tool_collect, R.id.ll_tool_comment, R.id.ll_tool_share, R.id.img_rotate, R.id.back_btn})
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.ll_tool_include:
                includeClick();
                break;
            case R.id.ll_tool_collect:
                collectClick();
                break;
            case R.id.ll_tool_comment:
                commentClick();
                break;
            case R.id.ll_tool_share:
                shareClick();
                break;
            case R.id.img_rotate:
                rotateClick();
                break;
            case R.id.back_btn:
                onBackPressed();
                break;
        }
    }


    private void includeClick()
    {
        Bundle bundle = new Bundle();
        bundle.putString(ContentActivity.FRAG_CLS, SelectSubjectFragment.class.getName());
        mContext.startActivity(bundle,ContentActivity.class);
    }


    //旋转屏幕点击
    private void rotateClick()
    {
        rotateFlag = !rotateFlag;
        if (rotateFlag)
        {
            //横屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        else
        {
            //竖屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }


    //分享点击
    private void shareClick()
    {
        String title = "微图分享";
        String content = "“【文档】" + docResultBean.document.title + "”" + ContantsUtil.shareContent;
        String imageUrl = "drawable://" + R.drawable.wbshare;
        ShareSdkUtil.showShareWithLocalImg(mContext, title, content, imageUrl);

    }


    //评论点击,文档的type为3
    private void commentClick()
    {
        Intent intent = new Intent(PdfActivity2.this, CommentActivity.class);
        CommentNeedDto commentNeedDto = new CommentNeedDto();
        commentNeedDto.title = docResultBean.document.title;
        commentNeedDto.tid = tid;
        commentNeedDto.type = 3;
        intent.putExtra("CommentNeedDto", new Gson().toJson(commentNeedDto));
        startActivity(intent);
    }


    //收藏点击
    private void collectClick()
    {
        if (CheckUtil.isNull(mPreference.getString(Preference.uid)))
        {
            Bundle bundle2 = new Bundle();
            bundle2.putString(ContentActivity.FRAG_CLS, LoginFragment.class.getName());
            startForResult(bundle2, 100, ContentActivity.class);
        }
        else
        {
            putCollect();
        }

    }


    private void initActivity(String path)
    {
        showLoding();
        FileLoader.getInstance(mContext).loadCallBack(path, new FileLoader.CallBack()
        {
            @Override
            public void callBack(File file)
            {
                dismissLoading();
                if (file != null && file.exists() && file.length() > 0)
                {
                    pdfView.fromFile(file).defaultPage(0).onPageChange(PdfActivity2.this).load();

                }
                else
                {
                    showToast("该文档不存在");
                }
            }
        });
    }


    @Override
    public void onPageChanged(int page, int pageCount)
    {
        currentPage = page;
        if (currentPage > 1 && !isOpen)
        {
            showToast("私有内容只能看第一页");
            return;
        }
        this.pageCount = pageCount;
        pageSeekBar.setMax(pageCount - 1);
        pageSeekBar.setProgress(currentPage - 1);
        title.setText(page + "/" + pageCount);
        if (page == lastPage)
        {
            isPageChange = false;
        }
        else
        {
            isPageChange = true;
        }
        lastPage = page;
    }


    @Override
    public void onBackPressed()
    {
        finish();
        overridePendingTransition(R.anim.alpha_into, R.anim.zoomout);
    }


    private void putCollect()
    {
        showLoding();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("tid", tid);
        params.put("uid", mPreference.getString(Preference.uid));
        if (docResultBean.favorite == 0)
        {
            params.put("type", 3);
            params.put("method", "favorite.save");
        }
        else
        {
            params.put("method", "favorite.delete");
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
                        JSONObject mjson = new JSONObject(json);
                        String message = mjson.getString("message");
                        if (message.equals("设置成功"))
                        {
                            docResultBean.favorite = 1;
                            imgCollect.setImageResource(R.drawable.collect);
                            Toast.makeText(PdfActivity2.this, "收藏成功", Toast.LENGTH_SHORT).show();
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("isDelete", true);
                            EventBus.getDefault().post(new MessageEvent(bundle));
                        }
                        else
                        {
                            docResultBean.favorite = 0;
                            imgCollect.setImageResource(R.drawable.collect_no);
                            Toast.makeText(PdfActivity2.this, "取消收藏成功", Toast.LENGTH_SHORT).show();
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("isDelete", true);
                            EventBus.getDefault().post(new MessageEvent(bundle));
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


    private void getPDF()
    {
        showLoding();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", tid);
        params.put("uid", mPreference.getString(Preference.uid));
        params.put("ip", NetworkUtil.getLocalIpAddress(PdfActivity2.this));
        params.put("method", "document.get");
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
                if (!TextUtils.isEmpty(json))
                {
                    DocResultBean resultBean = JsonUtil.fromJson(json, new TypeToken<DocResultBean>()
                    {
                    }.getType());
                    if (resultBean.code == 0)
                    {
                        Toast.makeText(mContext, "该文档不存在", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    docResultBean = resultBean;
                    if (docResultBean.document.open != null)
                    {
                        isOpen = (docResultBean.document.open == 1);
                    }
                    initActivity(docResultBean.document.pdfUrl);
                    initView();
                    dismissLoading();
                }


            }
        });
    }


    private void initView() {
        if (docResultBean.favorite == 1)
        {
            imgCollect.setImageResource(R.drawable.collect);
        }
        else
        {
            imgCollect.setImageResource(R.drawable.collect_no);
        }
        title.setText(docResultBean.document.title);
        pdfView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //没有换页的点击
                if (showFlag && !isPageChange && !pdfView.isZooming())
                {
                    pageSeekBar.setProgress(currentPage - 1);
                    titleContainer.setVisibility(View.VISIBLE);
                    llBottom.setVisibility(View.VISIBLE);
                    showFlag = false;
                }
                else
                {
                    titleContainer.setVisibility(View.GONE);
                    llBottom.setVisibility(View.GONE);
                    showFlag = true;
                }
            }
        });

        pageSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                if (!isOpen)
                {
                    showToast("私有内容只能看第一页");
                    return;
                }
                pdfView.jumpTo(progress + 1);
                int allPage = pageSeekBar.getMax() + 1;
                title.setText((progress + 1) + "/" + allPage);
            }


            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }


            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {

            }
        });


        pdfView.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View v)
            {
                titleContainer.setVisibility(View.GONE);
                llBottom.setVisibility(View.GONE);
                return false;
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 100)
        {
            collectClick();
        }
    }

}