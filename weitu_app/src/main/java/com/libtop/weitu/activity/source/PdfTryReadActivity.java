package com.libtop.weitu.activity.source;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.google.gson.Gson;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.search.BookDetailFragment;
import com.libtop.weitu.activity.search.CommentActivity;
import com.libtop.weitu.activity.search.dto.BookDetailDto;
import com.libtop.weitu.activity.search.dto.CommentNeedDto;
import com.libtop.weitu.activity.search.dto.DetailDto;
import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.eventbus.MessageEvent;
import com.libtop.weitu.fileloader.FileLoader;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.CheckUtil;
import com.libtop.weitu.utils.ContantsUtil;
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


/**
 * Created by LianTu on 2016-8-6.
 */
public class PdfTryReadActivity extends BaseActivity implements OnPageChangeListener
{
    @Bind(R.id.pdfView)
    PDFView pdfView;
    @Bind(R.id.page)
    TextView title;
    @Bind(R.id.title_container)
    LinearLayout titleContainer;
    @Bind(R.id.rl_pdf_bottom)
    RelativeLayout rlBottom;
    @Bind(R.id.page_seekbar)
    SeekBar pageSeekBar;
    @Bind(R.id.img_head)
    ImageView imageHead;
    @Bind(R.id.tv_publisher)
    TextView tvPublisher;
    @Bind(R.id.tv_play_time)
    TextView titlePlay;
    @Bind(R.id.img_collect)
    ImageView imgCollect;

    private boolean showFlag = true;
    private boolean isPageChange = false;
    private boolean rotateFlag = false;
    private int currentPage = 1;
    private int pageCount = 1;
    private int lastPage;
    private String tid;

    private boolean isOpen = true;

    private String pdfUrl;
    private BookDetailDto bookDetailDto;
    private DetailDto data;

    private boolean isCollectShow;
    private boolean isChangeScreen = true;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setInjectContentView(R.layout.activity_pdf3);
        pdfUrl = getIntent().getStringExtra("url");
        String bookString = getIntent().getStringExtra("BookDetailDto");
        isCollectShow = getIntent().getBooleanExtra("isCollectShow", false);
        if (isCollectShow)
        {
            imgCollect.setBackgroundResource(R.drawable.collect);
        }
        else
        {
            imgCollect.setBackgroundResource(R.drawable.collect_no);
        }
        bookDetailDto = new Gson().fromJson(bookString, BookDetailDto.class);
        data = bookDetailDto.book;
        if (!TextUtils.isEmpty(pdfUrl))
        {
            initActivity(pdfUrl);
        }
        initView();

    }


    @Nullable
    @OnClick({R.id.img_collect, R.id.img_comment, R.id.img_share, R.id.img_rotate, R.id.back_btn})
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.img_collect:
                collectClick();
                break;
            case R.id.img_comment:
                commentClick();
                break;
            case R.id.img_share:
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


    private void shareClick()
    {
        String title = "微图分享";
        String content = "“【书】" + data.title + "”" + ContantsUtil.shareContent;
        String imageUrl = "drawable://" + R.drawable.wbshare;
        ShareSdkUtil.showShareWithLocalImg(mContext, title, content, imageUrl);
    }


    private void collectClick()
    {
        if (isCollectShow)
        {
            requestCancelCollect();
        }
        else
        {
            requestCollect();
        }
    }


    private void requestCancelCollect()
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("uid", mPreference.getString(Preference.uid));
        params.put("tid", data.isbn);
        params.put("method", "favorite.delete");
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
                Log.w("json", json);
                dismissLoading();
                if (CheckUtil.isNullTxt(json))
                {
                    return;
                }
                if (!CheckUtil.isNull(json))
                {
                    JSONObject mjson = null;
                    try
                    {
                        mjson = new JSONObject(json);
                        if (mjson.getInt("code") == 1)
                        {
                            Toast.makeText(mContext, "取消收藏成功", Toast.LENGTH_SHORT).show();
                            isCollectShow = false;
                            imgCollect.setBackgroundResource(R.drawable.collect_no);
                        }
                        else
                        {
                            Toast.makeText(mContext, "取消收藏失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    showToast("未搜索到相关记录");
                }
            }
        });
    }


    private void requestCollect()
    {
        showLoding();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("uid", mPreference.getString(Preference.uid));
        params.put("tid", data.isbn);
        params.put("type", 5);
        params.put("method", "favorite.save");
        HttpRequest.loadWithMap(params).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {

            }


            @Override
            public void onResponse(String json, int id)
            {
                Log.w("json", json);
                dismissLoading();
                if (CheckUtil.isNullTxt(json))
                {
                    return;
                }
                if (!CheckUtil.isNull(json))
                {
                    JSONObject mjson = null;
                    try
                    {
                        mjson = new JSONObject(json);
                        if (mjson.getInt("code") == 1)
                        {
                            Toast.makeText(mContext, "收藏成功", Toast.LENGTH_SHORT).show();
                            isCollectShow = true;
                            imgCollect.setBackgroundResource(R.drawable.collect);
                        }
                        else
                        {
                            Toast.makeText(mContext, "收藏失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                }
                else
                {
                    showToast("未搜索到相关记录");
                }
            }
        });

    }


    //书本的type为5
    private void commentClick()
    {
        Intent intent = new Intent(mContext, CommentActivity.class);
        CommentNeedDto commentNeedDto = new CommentNeedDto();
        commentNeedDto.title = data.title;
        commentNeedDto.author = data.author;
        commentNeedDto.publisher = data.publisher;
        String imgPath = ContantsUtil.IMG_BASE + data.cover;
        commentNeedDto.photoAddress = imgPath;
        commentNeedDto.tid = data.isbn;
        commentNeedDto.type = 5;
        intent.putExtra("CommentNeedDto", new Gson().toJson(commentNeedDto));
        startActivity(intent);
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
                    pdfView.fromFile(file).defaultPage(0).onPageChange(PdfTryReadActivity.this).load();

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
        Bundle bundle = new Bundle();
        bundle.putString("target", BookDetailFragment.class.getName());
        bundle.putBoolean("isCollectShow", isCollectShow);
        EventBus.getDefault().post(new MessageEvent(bundle));
        finish();
        overridePendingTransition(R.anim.alpha_into, R.anim.zoomout);
    }


    private void initView()
    {
        if (bookDetailDto.favorite == 1)
        {
            imgCollect.setImageResource(R.drawable.collect);
        }
        else
        {
            imgCollect.setImageResource(R.drawable.collect_no);
        }
        title.setText(data.title);
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
                    rlBottom.setVisibility(View.VISIBLE);
                    showFlag = false;
                }
                else
                {
                    titleContainer.setVisibility(View.GONE);
                    rlBottom.setVisibility(View.GONE);
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
                if (!isChangeScreen)
                {
                    pdfView.jumpTo(progress + 1);
                }
                isChangeScreen = false;
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
                rlBottom.setVisibility(View.GONE);
                return false;
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
    }

}
