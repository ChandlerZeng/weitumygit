package com.libtop.weituR.utils.selector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.libtop.weitu.R;
import com.libtop.weituR.http.HttpRequest;
import com.libtop.weituR.tool.Preference;
import com.libtop.weituR.utils.selector.view.ImageEditActivity;
import com.libtop.weituR.utils.selector.view.ImageSortActivity;
import com.libtop.weituR.utils.selector.view.MultiImageSelectorActivity2;
import com.libtop.weituR.widget.TagGroup;
import com.libtop.weituR.widget.dialog.TranLoading;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;


public class ImageUploadActivity extends Activity implements View.OnClickListener {
    private String tags, introduction, categoriesName1;
    private int label1;
    /**
     * 默认选择集
     */
    public static final String DEFAULT_SELECTED_LIST = "default_list";
    protected TranLoading mLoading;
    private ArrayList<String> resultList;
    GridView gridView;
    TextView titleView, goView, tvView;
    private String name;
    private int codeId;
    ImageGridAdapter imageGridAdapter;
    EditText et_title, et_desca;
    protected Preference mPreference;
    private String fid, mtitle, aid;
    //TagGroup tagGroup;
    TextView goingDown;
    private boolean isagin = false;
    TagGroup mTagGroup;
    private String[] tagS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);
        resultList = getIntent().getStringArrayListExtra(DEFAULT_SELECTED_LIST);
        resultList.add("");
        mtitle = getIntent().getStringExtra("title");
        tags = getIntent().getStringExtra("tags");
//        tagS = tags.split(",");
        aid = getIntent().getStringExtra("aid");
        introduction = getIntent().getStringExtra("introduction");
        categoriesName1 = getIntent().getStringExtra("categoriesName1");
        isagin = getIntent().getBooleanExtra("isagin", false);
        label1 = getIntent().getIntExtra("label1", 0);
        mLoading = new TranLoading(this);
        mPreference = new Preference(this);
        imageGridAdapter = new ImageGridAdapter(resultList);
        gridView = (GridView) findViewById(R.id.grid);

        gridView.setAdapter(imageGridAdapter);
        titleView = (TextView) findViewById(R.id.title);
        mTagGroup = (TagGroup) findViewById(R.id.tag_group);
        et_title = (EditText) findViewById(R.id.et_title);
        if (mtitle != null && mtitle.length() != 0)
            et_title.setText(mtitle);
        et_desca = (EditText) findViewById(R.id.et_desca);
        if (introduction != null && introduction.length() != 0)
            et_desca.setText(introduction);
        tvView = (TextView) findViewById(R.id.tv_sort);
        //tagGroup = (TagGroup) findViewById(R.id.et_tag);
        if (label1 != 0) {
            if (categoriesName1 != null && categoriesName1.length() != 0)
                tvView.setText(categoriesName1);
        }
        titleView.setText("上传照片");
        goView = (TextView) findViewById(R.id.going_down);
        goView.setText("发布");
        mTagGroup.setTags(tagS);
        //getTage();
        titleView.setOnClickListener(this);
        goView.setOnClickListener(this);
        findViewById(R.id.back_btn).setOnClickListener(this);
        findViewById(R.id.ll_video_sort).setOnClickListener(this);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == resultList.size() - 1) {
                    Intent i = new Intent(ImageUploadActivity.this, MultiImageSelectorActivity2.class);
                    // 是否显示拍摄图片
                    i.putExtra(MultiImageSelectorActivity2.EXTRA_SHOW_CAMERA, false);
                    // 最大可选择图片数量
                    i.putExtra(MultiImageSelectorActivity2.EXTRA_SELECT_COUNT, 9);
                    // 选择模式
                    i.putExtra(MultiImageSelectorActivity2.EXTRA_SELECT_MODE, MultiImageSelectorActivity2.MODE_MULTI);
                    i.putExtra("isUpload", 2);
                    startActivityForResult(i, 1);
                }
            }
        });
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.going_down:
                String title = et_title.getText().toString();
                if (title.length() == 0) {
                    Toast.makeText(ImageUploadActivity.this, "请填写专辑名称", Toast.LENGTH_SHORT).show();
                    return;
                }

//                if (tagGroup.getTags().length > 4) {
//                    Toast.makeText(ImageUploadActivity.this, "标签超过4项", Toast.LENGTH_SHORT).show();
//                    return;
//                } else if (tagGroup.getTags().length == 0) {
//                    Toast.makeText(ImageUploadActivity.this, "请输入标签", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                String[] a = mTagGroup.getTags();
                String desca = et_desca.getText().toString();
                if (desca.length() == 0) {
                    Toast.makeText(ImageUploadActivity.this, "请填写简介", Toast.LENGTH_SHORT).show();
                    return;
                }
                String labelname = tvView.getText().toString();
                if (labelname.length() == 0) {
                    Toast.makeText(ImageUploadActivity.this, "请选择分类", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (resultList.size() < 2) {
                    Toast.makeText(ImageUploadActivity.this, "请添加图片", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (aid != null && aid.length() != 0) {
                    Message msg = updataHandler.obtainMessage();
                    msg.what = 1;
                    updataHandler.sendMessage(msg);
                } else
                    saveImageList(title, desca, a);
                break;
            case R.id.back_btn:
                finish();
                break;
            case R.id.ll_video_sort:
                Intent i = new Intent(ImageUploadActivity.this, ImageSortActivity.class);
                startActivityForResult(i, 1);
                break;
            default:
                break;
        }
    }


    class ImageGridAdapter extends BaseAdapter {
        List<String> mlist;
        private LayoutInflater mInflater;
        final int mGridWidth;

        public ImageGridAdapter(List<String> list) {
            this.mlist = list;
            mInflater = (LayoutInflater) ImageUploadActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            int width = 0;
            WindowManager wm = (WindowManager) ImageUploadActivity.this.getSystemService(Context.WINDOW_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                Point size = new Point();
                wm.getDefaultDisplay().getSize(size);
                width = size.x;
            } else {
                width = wm.getDefaultDisplay().getWidth();
            }
            mGridWidth = width / 4;
        }

        @Override
        public int getCount() {
            return mlist.size();
        }

        public Object getItem(int position) {
            return mlist.get(position);
        }

        public void setData(List<String> list) {
            this.mlist = list;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int i, View view, ViewGroup parent) {
            ViewHolder holder;
            if (view == null) {
                view = mInflater.inflate(R.layout.list_item_image, parent, false);
                holder = new ViewHolder(view);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            if (holder != null) {
                if (i == mlist.size())
                    holder.setImageView();
                else
                    holder.bindData(getStr(i));
            }
            return view;
        }


        public String getStr(int i) {

            return mlist.get(i);

        }

        class ViewHolder {
            ImageView image;
            ImageView indicator;
            View mask;

            ViewHolder(View view) {
                image = (ImageView) view.findViewById(R.id.image);
                indicator = (ImageView) view.findViewById(R.id.checkmark);
                indicator.setVisibility(View.GONE);
                mask = view.findViewById(R.id.mask);
                view.setTag(this);
            }

            void setImageView() {
                image.setImageResource(R.drawable.add_pic);
            }

            void bindData(String url) {

                File imageFile = new File(url);
                if (imageFile.exists()) {
                    // 显示图片
                    Picasso.with(ImageUploadActivity.this)
                            .load(imageFile)
                            .placeholder(R.drawable.default_error)
                            .tag(MultiImageSelectorFragment.TAG)
                            .resize(mGridWidth, mGridWidth)
                            .centerCrop()
                            .into(image);
                } else {
                    image.setImageResource(R.drawable.add_pic);
                }
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case ImageSortActivity.DESCRIPTION_RETURNQ:
                Bundle ab = data.getExtras();
                name = ab.getString("name");
                codeId = ab.getInt("code");
                tvView.setText(name);
                break;
            case MultiImageSelectorActivity2.DESCRIPTION_RETURNQ:
                List<String> aresultList;
                aresultList = data.getStringArrayListExtra(MultiImageSelectorActivity2.DEFAULT_SELECTED_LIST);
                addlenght(aresultList);
                break;
        }
    }

    private void addlenght(List<String> aresultList) {
        resultList.remove(resultList.size() - 1);
        for (int i = 0; i < aresultList.size(); i++) {
            String a = aresultList.get(i);
            resultList.add(a);
        }
        resultList.add("");
        imageGridAdapter.setData(resultList);
        imageGridAdapter.notifyDataSetChanged();
    }

    private void saveImageList(String title, String introduction, String[] label) {
        showLoding();
        Map<String, Object> params = new HashMap<String, Object>();
        try {
            JSONArray jsonarray = new JSONArray(Arrays.toString(label));
            params.put("tags", jsonarray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mtitle = title;
        params.put("title", title);
        params.put("introduction", introduction);
        params.put("label1", codeId);
        params.put("uid", mPreference.getString(Preference.uid));//lid
        params.put("method", "imageAlbum.save");
        HttpRequest.loadWithMap(params)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String json, int id) {
                        if (!TextUtils.isEmpty(json)) {
                            try {
                                JSONObject mjson = new JSONObject(json);
                                fid = mjson.getString("id");
                                Message msg = updataHandler.obtainMessage();
                                msg.what = 1;
                                updataHandler.sendMessage(msg);
                                dismissLoading();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                dismissLoading();
                            }
                            return;
                        }

                        dismissLoading();
                    }
                });
    }

    private void showLoding(){
        if (mLoading!=null&&!mLoading.isShowing())
            mLoading.show();
    }

    private void dismissLoading(){
        if (mLoading!=null&&mLoading.isShowing())
            mLoading.dismiss();
    }

    private Handler updataHandler = new Handler() {
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    resultList.remove(resultList.size() - 1);
                    Intent intent = new Intent(ImageUploadActivity.this, ImageEditActivity.class);
                    intent.putExtra("iswhere", 2);
                    if (aid != null)
                        intent.putExtra("aid", aid);
                    else
                        intent.putExtra("aid", fid);
                    intent.putExtra("isagin", isagin);
                    intent.putExtra("title", mtitle);
                    intent.putExtra("uid", mPreference.getString(Preference.uid));
                    intent.putExtra(DEFAULT_SELECTED_LIST, resultList);
                    startActivity(intent);
                    finish();
                    break;
                case 2:
                    break;
                case 3:
                    break;
                default:
                    break;
            }
        }
    };


    //    private String[] getViewText() {
//
//        int count = mAddPhotoLayout.getChildCount();
//        String[] Slist = new String[count];
//        for (int i = 0; i < count; i++) {
//            String a = ((TextView) mAddPhotoLayout.getChildAt(i)).getText().toString().trim();
//            Slist[i] = a;
//        }
//        return Slist;
//    }
}
