package com.libtop.weitu.activity.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.ContentFragment;
import com.libtop.weitu.activity.login.LoginFragment;
import com.libtop.weitu.activity.main.subsubject.SelectSubjectFragment;
import com.libtop.weitu.activity.search.adapter.MainPageAdapter;
import com.libtop.weitu.activity.search.dto.BookDetailDto;
import com.libtop.weitu.activity.search.dto.CommentNeedDto;
import com.libtop.weitu.activity.search.dto.DetailDto;
import com.libtop.weitu.activity.search.dto.LabelDto;
import com.libtop.weitu.activity.source.PdfTryReadActivity;
import com.libtop.weitu.base.impl.NotifyFragment;
import com.libtop.weitu.eventbus.MessageEvent;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.CheckUtil;
import com.libtop.weitu.utils.ContantsUtil;
import com.libtop.weitu.utils.IsbnUtils;
import com.libtop.weitu.utils.JSONUtil;
import com.libtop.weitu.utils.ShareSdkUtil;
import com.libtop.weitu.utils.StringUtil;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;


public class BookDetailFragment extends ContentFragment
{

    @Bind(R.id.auther)
    TextView auther;
    @Bind(R.id.book_report)
    TextView report;
    @Bind(R.id.icon)
    ImageView icon;
    @Bind(R.id.search)
    ImageButton search;

    @Bind(R.id.back_btn)
    ImageView backBtn;
    @Bind(R.id.title)
    TextView titleView;
    @Bind(R.id.viewpager)
    ViewPager viewpager;
    @Bind(R.id.radioGroup)
    RadioGroup radioGroup;
    @Bind(R.id.cd)
    RadioButton discRadio;
    @Bind(R.id.try_read)
    RadioButton tryReadRadio;

    @Nullable
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Nullable
    @Bind(R.id.img_collect)
    ImageView imgCollect;

    private List<Fragment> datas;
    private MainPageAdapter adapter;
    private DetailDto dto;
    private BookDetailDto bookdto;
    private LabelDto label;

    private String key;
    private String schoolCode;

    private boolean isFromCapture;
    private String allBookString;

    private boolean isCollectShow;

    private String imgPath;

    private boolean isFromMainPage = false;
    public static final String ISFROMMAINPAGE = "isFromMainPage";


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        datas = new ArrayList<Fragment>();
        adapter = new MainPageAdapter(mFm, datas);
        noNetThanExit(mContext);
    }


    @Override
    protected int getLayoutId()
    {
        return R.layout.activity_book_detail2;
    }


    @Override
    public void onCreation(View root)
    {
        initActivity();
    }


    private void initActivity()
    {
        Bundle bundle = ((ContentActivity) mContext).getCurrentExtra();
        key = bundle.getString("isbn");
        isFromMainPage = bundle.getBoolean(ISFROMMAINPAGE, false);
        titleView.setText(bundle.getString("name"));
        tvTitle.setText(bundle.getString("name"));
        auther.setText(bundle.getString("auth"));
        String publisher = bundle.getString("publisher");
        if (!CheckUtil.isNull(publisher))
        {
            report.setText(bundle.getString("publisher"));
        }
        schoolCode = bundle.getString("school");
        isFromCapture = bundle.getBoolean("isFromCapture");
        allBookString = bundle.getString("allJson");
        if(bundle.getString("cover").contains("http")){
            imgPath = bundle.getString("cover");
        } else {
            imgPath = ContantsUtil.IMG_BASE + bundle.getString("cover");
        }
        Picasso.with(mContext).load(imgPath).fit().into(icon);
        viewpager.setAdapter(adapter);
        radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int id) {
                switch (id) {
                    case R.id.info:
                        viewpager.setCurrentItem(0);
                        break;
                    case R.id.try_read:
                        tryReadClick();
                        break;
                    case R.id.struct:
                        viewpager.setCurrentItem(1);
                        break;
                    case R.id.cd:
                        viewpager.setCurrentItem(2);
                        break;
                }
            }
        });

        viewpager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                NotifyFragment fragment = (NotifyFragment) datas.get(position);
                fragment.notify("");
                switch (position) {
                    case 0:
                        radioGroup.check(R.id.info);
                        break;
                    case 1:
                        radioGroup.check(R.id.struct);
                        break;
                    case 2:
                        radioGroup.check(R.id.cd);
                        break;
                    case 3:
                        break;
                }
            }


            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }


            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
        loadData(key);
    }


    private void tryReadClick()
    {
        Intent intent = new Intent();
        if (dto == null)
        {
            showToast("该图书没有试读内容");
            return;
        }
        if (!TextUtils.isEmpty(dto.downloadUrl))
        {
            intent.putExtra("url", dto.downloadUrl);
        }
        intent.putExtra("doc_id", dto.id);
        intent.putExtra("isCollectShow", isCollectShow);
        intent.putExtra("BookDetailDto", new Gson().toJson(bookdto));
        intent.setClass(mContext, PdfTryReadActivity.class);
        mContext.startActivity(intent);
        mContext.overridePendingTransition(R.anim.zoomin, R.anim.alpha_outto);
    }


    public void onResume()
    {
        super.onResume();
        if (ContantsUtil.COMMON)
        {
            ContantsUtil.COMMON = false;
        }
    }


    @Nullable
    @OnClick({R.id.ll_tool_include,R.id.ll_tool_collect, R.id.ll_tool_comment, R.id.ll_tool_share, R.id.search, R.id.back_btn})
    public void onClick(View view)
    {
        switch (view.getId())
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
            case R.id.search:
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
        bundle.putBoolean(ContentActivity.FRAG_ISBACK, true);
        if(dto.isbn!=null){
            bundle.putString("tid",dto.isbn);
            bundle.putInt("type",5);
        }
        mContext.startActivity(bundle, ContentActivity.class);
    }


    private void shareClick()
    {
        String title = "微图分享";
        String content = "“【书】" + dto.title + "”" + ContantsUtil.shareContent;
        String imageUrl = "drawable://" + R.drawable.wbshare;
        ShareSdkUtil.showShareWithLocalImg(mContext, title, content, imageUrl);
    }


    //书本的type为5
    private void commentClick()
    {
        Intent intent = new Intent(mContext, CommentActivity.class);
        CommentNeedDto commentNeedDto = new CommentNeedDto();
        if(dto!=null){
            commentNeedDto.title = dto.title;
            commentNeedDto.author = dto.author;
            commentNeedDto.publisher = dto.publisher;
            commentNeedDto.photoAddress = imgPath;
            commentNeedDto.tid = dto.isbn;
        }
        commentNeedDto.type = 5;
        intent.putExtra("CommentNeedDto", new Gson().toJson(commentNeedDto));
        mContext.startActivityWithFlag(intent, Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
        params.put("tid", dto.isbn);
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
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("isDelete", true);
                            EventBus.getDefault().post(new MessageEvent(bundle));
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


    @Override
    public void onDestroyView()
    {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }


    private void requestCollect()
    {
        showLoding();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("uid", mPreference.getString(Preference.uid));
        params.put("tid", dto.isbn);
        params.put("type", 5);
        params.put("method", "favorite.save");
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
                            Toast.makeText(mContext, "收藏成功", Toast.LENGTH_SHORT).show();
                            isCollectShow = true;
                            imgCollect.setBackgroundResource(R.drawable.collect);
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("isDelete", true);
                            EventBus.getDefault().post(new MessageEvent(bundle));
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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(MessageEvent event)
    {
        if (event.message.getString("target")!=null && event.message.getString("target").equals(this.getClass().getName().toString()))
        {
            Bundle bundle = event.message;
            isCollectShow = bundle.getBoolean("isCollectShow");
            if (isCollectShow)
            {
                imgCollect.setBackgroundResource(R.drawable.collect);
            }
            else
            {
                imgCollect.setBackgroundResource(R.drawable.collect_no);
            }
        }
    }


    private void loadData(String key)
    {
        showLoding();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("uid", Preference.instance(mContext).getString(Preference.uid));
        if (isFromMainPage)
        {
            params.put("method", "book.getBook");
            params.put("bid", IsbnUtils.obscure(key));
        }
        else
        {
            params.put("method", "book.get");
            params.put("lid", schoolCode);
            params.put("bid", key);
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
                dismissLoading();
                if (CheckUtil.isNullTxt(json))
                {
                    showToast("请求超时，请稍后再试");
                    return;
                }
                if (!CheckUtil.isNull(json))
                {
                    if (isFromCapture)
                    {
                        if (allBookString != null)
                        {
                            json = allBookString;
                        }
                    }
                    BookDetailDto bookDetailDto = JSONUtil.readBean(json, BookDetailDto.class);
                    bookdto = bookDetailDto;
                    isCollectShow = (bookDetailDto.favorite == 1);
                    if (isCollectShow)
                    {
                        imgCollect.setBackgroundResource(R.drawable.collect);
                    }
                    else
                    {
                        imgCollect.setBackgroundResource(R.drawable.collect_no);
                    }
                    dto = bookDetailDto.book;
                    if (dto != null)
                    {
                        String path = ContantsUtil.IMG_BASE + dto.cover;
                        Picasso.with(mContext).load(path).fit().into(icon);
                        InfoFragment fragment = InfoFragment.Instance();
                        fragment.loadInfo(dto);
                        datas.add(fragment);
                        if (!TextUtils.isEmpty(dto.downloadUrl))
                        {
                            tryReadRadio.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            tryReadRadio.setVisibility(View.GONE);
                        }
                        FavoriteFragment fragment2 = FavoriteFragment.Instance();
                        datas.add(fragment2);
                        fragment2.loadInfo(dto.indexList);
                        if (dto.disc > 0)
                        {
                            CdFragment fragment3 = CdFragment.Instance(dto.disc);
                            datas.add(fragment3);
                            discRadio.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            discRadio.setVisibility(View.GONE);
                        }
                        adapter.notifyDataSetChanged();

                        label = bookDetailDto.label;
                        setLabel(label, dto);
                    }
                }
                else
                {
                    showToast("请求出错，请稍后再试");
                }
            }
        });
    }


    private void updateState(final TextView view, final TextView labelView, final String tag, int state)
    {
        if (CheckUtil.isNull(Preference.instance(mContext).getString(Preference.uid)))
        {
            Bundle bundle = new Bundle();
            bundle.putString(ContentActivity.FRAG_CLS, LoginFragment.class.getName());
            bundle.putBoolean(ContentActivity.FRAG_ISBACK, true);
            bundle.putBoolean(ContentActivity.FRAG_WITH_ANIM, true);
            mContext.startActivity(bundle, ContentActivity.class);
            return;
        }
        final int stage;
        if (state == 0)
        {
            stage = 1;
        }
        else
        {
            stage = 0;
        }
        showLoding();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("method", "label.add");
        params.put("bid", dto.id);
        params.put("mid", dto.mid);
        params.put("code", schoolCode);
        params.put("action", tag);
        params.put("value", stage);
        HttpRequest.loadWithMap(params).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {

            }


            @Override
            public void onResponse(String jsonStr, int id)
            {
                dismissLoading();
                if (CheckUtil.isNullTxt(jsonStr))
                {
                    showToast("请求超时，请稍后再试");
                    return;
                }
                if (!CheckUtil.isNull(jsonStr))
                {
                    try
                    {
                        JSONObject json = new JSONObject(jsonStr);
                        if (json.getInt("code") == 1)
                        {
                            if (stage == 1)
                            {
                                view.setText(StringUtil.toInt(view.getText()) + 1 + "");
                                labelView.setTextColor(getResources().getColor(R.color.main_color));
                                label.reset(tag, 1);
                            }
                            else
                            {
                                view.setText(StringUtil.toInt(view.getText()) - 1 + "");
                                labelView.setTextColor(getResources().getColor(R.color.label_color));
                                label.reset(tag, 0);
                            }
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    private void setLabel(LabelDto dto, DetailDto detail)
    {
        if (!CheckUtil.isNull(detail.title))
        {
            titleView.setText(detail.title);
        }
        if (!CheckUtil.isNull(detail.author))
        {
            auther.setText(detail.author);
        }
        if (!CheckUtil.isNull(detail.publisher))
        {
            report.setText(detail.publisher);
        }
    }


    public DetailDto getDto()
    {
        return dto;
    }


    public void show()
    {
        showLoding();
    }


    public void hide()
    {
        dismissLoading();
    }


    public String getKey()
    {
        return key;
    }


    public String getSchool()
    {
        return schoolCode;
    }


}
