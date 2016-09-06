package com.libtop.weitu.utils.selector.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.libtop.weitu.R;
import com.libtop.weitu.utils.selector.ImageUploadActivity;
import com.libtop.weitu.utils.selector.MultiImageSelectorFragment;
import com.libtop.weitu.utils.viewpagerbitmap.ImagePagerActivity;

import java.io.File;
import java.util.ArrayList;


/**
 * <p>
 * Title: MultiImageSelectorActivity2.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * CreateTime：16/5/3
 * </p>
 *
 * @author 作者名
 * @version common v1.0
 */
public class MultiImageSelectorActivity2 extends FragmentActivity implements MultiImageSelectorFragment.Callback, View.OnClickListener
{
    private String tags, introduction, title, categoriesName1;
    private int label1;
    public static final int DESCRIPTION_RETURNQ = 30;
    public static final String DEFAULT_SELECTED_LIST = "default_list";
    /**
     * 最大图片选择次数，int类型，默认9
     */
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    /**
     * 图片选择模式，默认多选
     */
    public static final String EXTRA_SELECT_MODE = "select_count_mode";
    /**
     * 是否显示相机，默认显示
     */
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    /**
     * 选择结果，返回为 ArrayList&lt;String&gt; 图片路径集合
     */
    public static final String EXTRA_RESULT = "select_result";
    /**
     * 默认选择集
     */
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_list";

    /**
     * 单选
     */
    public static final int MODE_SINGLE = 0;
    /**
     * 多选
     */
    public static final int MODE_MULTI = 1;

    private ArrayList<String> resultList = new ArrayList<String>();
    // Button mSubmitButton;
    private int mDefaultCount;

    TextView haveSee;

    private int where;
    private String aid;

    TextView button;
    private boolean isagin;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default2);
        isagin = getIntent().getBooleanExtra("isagin", false);
        title = getIntent().getStringExtra("title");
        tags = getIntent().getStringExtra("tags");
        aid = getIntent().getStringExtra("aid");
        categoriesName1 = getIntent().getStringExtra("categoriesName1");
        introduction = getIntent().getStringExtra("introduction");
        label1 = getIntent().getIntExtra("label1", 0);
        Intent intent = getIntent();
        where = intent.getIntExtra("isUpload", 1);
        mDefaultCount = intent.getIntExtra(EXTRA_SELECT_COUNT, 9);
        int mode = intent.getIntExtra(EXTRA_SELECT_MODE, MODE_MULTI);
        boolean isShow = intent.getBooleanExtra(EXTRA_SHOW_CAMERA, true);
        //        if (mode == MODE_MULTI && intent.hasExtra(EXTRA_DEFAULT_SELECTED_LIST)) {
        //            resultList = intent.getStringArrayListExtra(EXTRA_DEFAULT_SELECTED_LIST);
        //        }

        Bundle bundle = new Bundle();
        bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_COUNT, mDefaultCount);
        bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_MODE, mode);
        bundle.putBoolean(MultiImageSelectorFragment.EXTRA_SHOW_CAMERA, isShow);
        bundle.putBoolean("ismy", false);
        bundle.putStringArrayList(MultiImageSelectorFragment.EXTRA_DEFAULT_SELECTED_LIST, resultList);

        getSupportFragmentManager().beginTransaction().add(R.id.image_grid, Fragment.instantiate(this, MultiImageSelectorFragment.class.getName(), bundle)).commit();

        // 返回按钮
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        button = (TextView) findViewById(R.id.commit2);
        button.setVisibility(View.VISIBLE);
        button.setOnClickListener(this);
        haveSee = (TextView) findViewById(R.id.have_see);
        findViewById(R.id.going_down).setOnClickListener(this);
        haveSee.setOnClickListener(this);
    }


    private void updateDoneText()
    {
        //        mSubmitButton.setText(String.format("%s(%d/%d)",
        //                getString(R.string.action_done), resultList.size(), mDefaultCount));
    }


    @Override
    public void onSingleImageSelected(String path)
    {
        Intent data = new Intent();
        resultList.add(path);
        data.putStringArrayListExtra(EXTRA_RESULT, resultList);
        data.putExtra("lamge", path);
        setResult(RESULT_OK, data);
        finish();
    }


    @Override
    public void onImageSelected(String path)
    {
        if (!resultList.contains(path))
        {
            resultList.add(path);
        }
        // 有图片之后，改变按钮状态
        if (resultList.size() > 0)
        {
            updateDoneText();
            //            if(!mSubmitButton.isEnabled()){
            //                mSubmitButton.setEnabled(true);
            //            }
        }
    }


    @Override
    public void onImageUnselected(String path)
    {
        if (resultList.contains(path))
        {
            resultList.remove(path);
        }
        updateDoneText();
        // 当为选择图片时候的状态
        //        if(resultList.size() == 0){
        //            mSubmitButton.setText(R.string.action_done);
        //            mSubmitButton.setEnabled(false);
        //        }
    }


    @Override
    public void onCameraShot(File imageFile)
    {
        if (imageFile != null)
        {
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile)));
            Intent data = new Intent();
            resultList.add(imageFile.getAbsolutePath());
            data.putStringArrayListExtra(EXTRA_RESULT, resultList);
            setResult(RESULT_OK, data);
            finish();
        }
    }


    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        switch (id)
        {
            case R.id.have_see:
                if (resultList != null && resultList.size() > 0)
                {
                    Intent intent = new Intent(MultiImageSelectorActivity2.this, ImagePagerActivity.class);
                    intent.putExtra(ImagePagerActivity.DEFAULT_SELECTED_LIST, resultList);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(MultiImageSelectorActivity2.this, "请选择至少一张图片", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.going_down:
                if (where == 1)
                {
                    Intent intent = new Intent(MultiImageSelectorActivity2.this, ImageUploadActivity.class);
                    intent.putExtra(ImageUploadActivity.DEFAULT_SELECTED_LIST, resultList);
                    intent.putExtra("title", title);
                    intent.putExtra("tags", tags);
                    intent.putExtra("label1", label1);
                    intent.putExtra("aid", aid);
                    intent.putExtra("categoriesName1", categoriesName1);
                    intent.putExtra("introduction", introduction);
                    if (isagin != false)
                    {
                        intent.putExtra("isagin", isagin);
                    }
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Intent data = new Intent();
                    data.putExtra(DEFAULT_SELECTED_LIST, resultList);
                    setResult(DESCRIPTION_RETURNQ, data);
                    finish();
                }
                break;
            case R.id.commit2:
                if (buttonClickedListener != null)
                {
                    buttonClickedListener.onclicked("");
                }
                break;
            default:
                break;
        }
    }


    /**
     * 定义一个接口
     *
     * @author zqy
     */
    public interface OnButtonClickedListener
    {
        /**
         * 里面传个值
         *
         * @param s
         */
        public void onclicked(String s);
    }


    private OnButtonClickedListener buttonClickedListener;


    /**
     * @param buttonClickedListener 写一个对外公开的方法
     */
    public void setButtonClickedListener(OnButtonClickedListener buttonClickedListener)
    {
        this.buttonClickedListener = buttonClickedListener;
    }
}

