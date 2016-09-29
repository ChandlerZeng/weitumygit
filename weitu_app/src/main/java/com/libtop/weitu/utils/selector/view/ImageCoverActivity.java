package com.libtop.weitu.utils.selector.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.libtop.weitu.R;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.utils.Preference;
import com.libtop.weitu.utils.selector.MultiImageSelectorFragment;
import com.libtop.weitu.widget.TagGroup;
import com.libtop.weitu.widget.dialog.TranLoading;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;


public class ImageCoverActivity extends Activity implements View.OnClickListener
{
    public static final int DESCRIPTION_RETURNQ = 50;
    EditText et_title, et_desca;
    private String name;
    TextView tv_sort;
    private int codeId;
    private String mid;
    protected TranLoading mLoading;
    ImageView imgCover;
    private String uid;
    protected Preference mPreference;
    int mGridWidth;
    private String tags, introduction, categoriesName1, title, folder;
    private int label1;
    private String imageUrl;
    TextView tvTitle;
    TextView goingDown;
    TagGroup mTagGroup;
    private String[] tagS;


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
            editStart = et_desca.getSelectionStart();
            editEnd = et_desca.getSelectionEnd();

            if (temp.length() > 50)
            {
                Toast.makeText(ImageCoverActivity.this, "你输入的字数已经超过了限制！", Toast.LENGTH_SHORT).show();
                s.delete(editStart - 1, editEnd);
                int tempSelection = editStart;
                et_desca.setText(s);
                et_desca.setSelection(tempSelection);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_cover);
        mLoading = new TranLoading(this);
        folder = getIntent().getStringExtra("folder");
        title = getIntent().getStringExtra("title");
        tags = getIntent().getStringExtra("tags");
        tagS = tags.split(",");
        categoriesName1 = getIntent().getStringExtra("categoriesName1");
        introduction = getIntent().getStringExtra("introduction");
        label1 = getIntent().getIntExtra("label1", 0);
        imageUrl = getIntent().getStringExtra("imageUrl");
        mTagGroup = (TagGroup) findViewById(R.id.tag_group);
        tvTitle = (TextView) findViewById(R.id.title);
        et_title = (EditText) findViewById(R.id.et_title);
        et_desca = (EditText) findViewById(R.id.et_desca);
        tv_sort = (TextView) findViewById(R.id.tv_sort);
        imgCover = (ImageView) findViewById(R.id.img_cover);
        findViewById(R.id.modify).setOnClickListener(this);
        findViewById(R.id.ll_video_sort).setOnClickListener(this);
        imgCover.setOnClickListener(this);
        mPreference = new Preference(this);
        uid = mPreference.getString(Preference.uid);
        mid = getIntent().getStringExtra("id");
        if (title != null && title.length() != 0)
        {
            tvTitle.setText(title);
            et_title.setText(title);
        }
        if (introduction != null && introduction.length() != 0)
        {
            et_desca.setText(introduction);
        }
        if (label1 != 0)
        {
            if (categoriesName1 != null && categoriesName1.length() != 0)
            {
                tv_sort.setText(categoriesName1);
            }
        }
        mTagGroup.setTags(tagS);
        int width = 0;
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
        {
            Point size = new Point();
            wm.getDefaultDisplay().getSize(size);
            width = size.x;
        }
        else
        {
            width = wm.getDefaultDisplay().getWidth();
        }
        mGridWidth = width - 20;
        if (imageUrl != null && imageUrl.length() != 0)
        {
            Picasso.with(this).load(imageUrl).tag(MultiImageSelectorFragment.TAG).resize(mGridWidth, mGridWidth).centerCrop().into(imgCover);
        }

        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        et_desca.addTextChangedListener(watcher);
        goingDown = (TextView) findViewById(R.id.going_down);
        goingDown.setText("修改");
        goingDown.setOnClickListener(this);
    }


    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        switch (id)
        {
            case R.id.modify:
                requestVidea();
                break;
            case R.id.ll_video_sort:
                Intent i = new Intent(ImageCoverActivity.this, ImageSortActivity.class);
                startActivityForResult(i, 1);
                break;
            case R.id.img_cover:
                Intent intent = new Intent(ImageCoverActivity.this, ImageEditActivity.class);
                intent.putExtra("aid", mid);
                intent.putExtra("uid", uid);
                intent.putExtra("iswhere", 4);
                startActivityForResult(intent, 1);
                break;
            case R.id.going_down:
                requestVidea();
                break;

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
                    Toast.makeText(ImageCoverActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode)
        {
            case ImageSortActivity.DESCRIPTION_RETURNQ:
                Bundle ab = data.getExtras();
                name = ab.getString("name");
                label1 = ab.getInt("code");
                tv_sort.setText(name);
                break;
            case DESCRIPTION_RETURNQ:
                Bundle bb = data.getExtras();
                String url = bb.getString("cover");
                String a = url;
                String id = bb.getString("id");
                if (!TextUtils.isEmpty(a))
                {
                    Picasso.with(this).load(a).tag(MultiImageSelectorFragment.TAG).resize(mGridWidth, mGridWidth).centerCrop().into(imgCover);
                }
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("id", id);
                params.put("method", "image.setAsAlbumCover");
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
                        dismissLoading();
                        if (!TextUtils.isEmpty(json))
                        {

                            try
                            {
                                JSONObject array = new JSONObject(json);
                                int a = array.getInt("code");
                                if (a == 1)
                                {

                                }
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
                break;
        }
    }


    private void showLoding()
    {
        if (mLoading != null && !mLoading.isShowing())
        {
            mLoading.show();
        }
    }


    private void dismissLoading()
    {
        if (mLoading != null && mLoading.isShowing())
        {
            mLoading.dismiss();
        }
    }


    private void requestVidea()
    {
        String title = et_title.getText().toString();
        if (title.length() == 0)
        {
            Toast.makeText(ImageCoverActivity.this, "请填写专辑名称", Toast.LENGTH_SHORT).show();
            return;
        }

        String desca = et_desca.getText().toString();
        if (desca.length() == 0)
        {
            Toast.makeText(ImageCoverActivity.this, "请填写简介", Toast.LENGTH_SHORT).show();
            return;
        }
        if (tv_sort.getText().toString().length() == 0)
        {
            Toast.makeText(ImageCoverActivity.this, "请选择分类", Toast.LENGTH_SHORT).show();
            return;
        }
        String[] a = mTagGroup.getTags();
        Map<String, Object> params = new HashMap<String, Object>();
        try
        {
            JSONArray jsonarray = new JSONArray(Arrays.toString(a));
            params.put("tags", jsonarray);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        params.put("id", mid);
        params.put("title", title);
        params.put("introduction", desca);
        params.put("label1", "" + label1);
        params.put("method", "imageAlbum.update");
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
                dismissLoading();
                if (!TextUtils.isEmpty(json))
                {

                    try
                    {
                        JSONObject array = new JSONObject(json);
                        int code = array.getInt("code");
                        if (code == 1)
                        {
                            Message msg = updataHandler.obtainMessage();
                            msg.what = 1;
                            updataHandler.sendMessage(msg);
                        }
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
}
