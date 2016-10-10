package com.libtop.weitu.activity.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.libtop.weitu.R;
import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.dao.ResultCodeDto;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.utils.CategoryPickerHelper;
import com.libtop.weitu.utils.ClippingPicture;
import com.libtop.weitu.utils.ContextUtil;
import com.libtop.weitu.utils.ImageLoaderUtil;
import com.libtop.weitu.utils.JSONUtil;
import com.libtop.weitu.utils.Preference;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;


public class NewSubjectActivity extends BaseActivity
{
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.img_cover)
    ImageView imgCover;
    @Bind(R.id.et_subject_title)
    EditText etSubjectTitle;
    @Bind(R.id.et_subject_desc)
    EditText etSubjectDesc;
    @Bind(R.id.tv_subject_sort)
    TextView tvSubjectSort;

    public static final int REQUEST_IMAGE = 123;
    private String name;
    private int label1 = -1;
    public final static int RESULT_SUCCESSS = 200;
    private boolean isFromSelect = false;

    private boolean isEdit = false;
    private String idString = "";

    private CategoryPickerHelper categoryPickerHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setInjectContentView(R.layout.activity_main_new_subject);
        isFromSelect = getIntent().getBooleanExtra("fromSelect", false);
        initView();
        initData();
    }


    @Override
    protected void onResume()
    {
        super.onResume();
    }


    @Override
    protected void onPause()
    {
        super.onPause();
    }


    private void initData()
    {
        categoryPickerHelper = CategoryPickerHelper.getInstance();
        categoryPickerHelper.setOnCategorySelectListener(new CategoryPickerHelper.OnCategorySelectListener()
        {
            @Override
            public void onCategorySelect(CategoryPickerHelper.CategoryBean primaryCategory, CategoryPickerHelper.CategoryBean secondCategory)
            {
                name = primaryCategory.getName()+"    "+secondCategory.getName();
                label1 = primaryCategory.getCode();
                tvSubjectSort.setText(name);
//                LogUtil.w("guanglog","primaryCategory   + " + primaryCategory.getName() + "secondCategory    "+secondCategory.getName() );
            }
        });
    }


    private void initView()
    {
        isEdit = getIntent().getBooleanExtra("isEdit",false);
        idString = getIntent().getStringExtra("id");
        if (isEdit){
           title.setText("编辑主题");
        }else {
           title.setText("新建主题");
        }
        ImageLoaderUtil.loadRoundImage(mContext,imgCover, ImageLoaderUtil.RESOURCE_ID_IMAGE_BIG);
    }


    @OnClick({R.id.back_btn, R.id.img_cover, R.id.ll_theme_sort,R.id.commit})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.back_btn:
                onBackPressed();
                break;
            case R.id.img_cover:
                ContextUtil.chooseImage(mContext, true, 1, 0, REQUEST_IMAGE);
                break;
            case R.id.ll_theme_sort:
                themeSortClick();
                break;
            case R.id.commit:
                completeClick();
                break;
        }
    }


    private void completeClick()
    {
        if (TextUtils.isEmpty(etSubjectTitle.getText().toString()) ){
            Toast.makeText(mContext,"名称不能为空",Toast.LENGTH_SHORT).show();
            return;
        }else if (label1 == -1){
            Toast.makeText(mContext,"分类不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        if (isEdit){
            requestUpdateData();
        }else {
            requestSaveData();
        }
    }

    //更新主题
    //    http://weitu.bookus.cn/subject/update.json?text=
    //    // {"id":"56f97d8d984e741f1420awr8","title":"testsubject","introduction":"ssss","label1":130000,"uid":"56f97d8d984e741f1420a19e","cover":"wroiuowroiweruweruweir==","method":"subject.update"}
    private void requestUpdateData(){
        showLoding();
        Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.bg_new_subject);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id",idString );
        params.put("uid", Preference.instance(mContext).getString(Preference.uid));
        params.put("title", etSubjectTitle.getText().toString());
        params.put("introduction", etSubjectDesc.getText().toString());
        params.put("label1", label1);
        params.put("cover", ClippingPicture.bitmapToBase64(icon));
        params.put("method", "subject.update");
        HttpRequest.loadWithMap(params).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {
                Toast.makeText(mContext,R.string.netError,Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onResponse(String json, int id)
            {
                dismissLoading();
                Toast.makeText(mContext,json,Toast.LENGTH_SHORT).show();
                if (!TextUtils.isEmpty(json))
                {
                    ResultCodeDto resultCodeDto = JSONUtil.readBean(json, ResultCodeDto.class);
                    if (resultCodeDto == null){
                        Toast.makeText(mContext,R.string.netError,Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (resultCodeDto.code == 1){
                        Toast.makeText(mContext,"主题更新成功",Toast.LENGTH_SHORT).show();
                    }else
                    {
                        Toast.makeText(mContext,resultCodeDto.message,Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
//新建主题
//    http://weitu.bookus.cn/subject/save.json?text=
//    {"title":"testsubject","introduction":"ssss","label1":130000,"uid":"56f97d8d984e741f1420a19e","cover":"wroiuowroiweruweruweir==","method":"subject.save"}
    private void requestSaveData()
    {
        showLoding();
        Bitmap icon = BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.bg_new_subject);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("uid", Preference.instance(mContext).getString(Preference.uid));
        params.put("title", etSubjectTitle.getText().toString());
        params.put("introduction", etSubjectDesc.getText().toString());
        params.put("label1", label1);
        params.put("cover", ClippingPicture.bitmapToBase64(icon));
        params.put("method", "subject.save");
        HttpRequest.loadWithMap(params).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {
                Toast.makeText(mContext,R.string.netError,Toast.LENGTH_SHORT).show();
            }


            @Override
            public void onResponse(String json, int id)
            {
                dismissLoading();
                Toast.makeText(mContext,json,Toast.LENGTH_SHORT).show();
                if (!TextUtils.isEmpty(json))
                {
                    ResultCodeDto resultCodeDto = JSONUtil.readBean(json, ResultCodeDto.class);
                    if (resultCodeDto == null){
                        Toast.makeText(mContext,R.string.netError,Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (resultCodeDto.code == 1){
                        Toast.makeText(mContext,"主题创建成功",Toast.LENGTH_SHORT).show();
                        if(isFromSelect){
                            setResult(RESULT_SUCCESSS);
                            finish();
                        }
                    }else
                    {
                        Toast.makeText(mContext,resultCodeDto.message,Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }


    @Override
    public void onBackPressed()
    {
        if (categoryPickerHelper.isShowing())
        {
            categoryPickerHelper.dismissPicker();
            return;
        }
        super.onBackPressed();
    }


    private void themeSortClick()
    {
        categoryPickerHelper.showPicker(mContext);
//        Intent i = new Intent(mContext, ImageSortActivity.class);
//        startActivityForResult(i, 1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE
                && resultCode == Activity.RESULT_OK){
            String a = "file:///" + data.getStringExtra("lamge");
            ImageLoaderUtil.loadRoundImage(mContext,imgCover,a,ImageLoaderUtil.RESOURCE_ID_IMAGE_BIG);
            Toast.makeText(mContext,"Good image ",Toast.LENGTH_SHORT).show();
        }
//        switch (resultCode){
//            case ImageSortActivity.DESCRIPTION_RETURNQ:
//                Bundle ab = data.getExtras();
//                name = ab.getString("name");
//                label1 = ab.getInt("code");
//                tvSubjectSort.setText(name);
//                break;
//
//        }
    }
}
