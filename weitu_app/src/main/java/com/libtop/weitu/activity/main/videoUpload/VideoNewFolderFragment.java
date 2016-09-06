package com.libtop.weitu.activity.main.videoUpload;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.ContentFragment;
import com.libtop.weitu.eventbus.MessageEvent;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.widget.TagGroup;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;


/**
 * Created by LianTu on 2016/4/25.
 */
public class VideoNewFolderFragment extends ContentFragment
{

    @Bind(R.id.title)
    TextView mTitleText;

    @Bind(R.id.tv_sort)
    TextView mSortText;

    @Bind(R.id.et_title)
    EditText mEditTitleText;

    @Bind(R.id.et_desc)
    EditText mDescText;


    private Bundle bm;

    @Bind(R.id.commit)
    TextView commitView;

    @Bind(R.id.tag_group)
    TagGroup mTagGroup;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }


    @Override
    protected int getLayoutId()
    {
        return R.layout.fragment_video_newfolder;
    }


    @Override
    public void onCreation(View root)
    {
        setTitle();
        mEditTitleText.addTextChangedListener(wrongTextWatcher);
        mDescText.addTextChangedListener(watcher);

    }


    @Override
    public void onStart()
    {
        super.onStart();
        if (bm != null)
        {
            String sortText = bm.getString("sort");
            mSortText.setText(sortText);
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
    @OnClick({R.id.back_btn, R.id.ll_video_sort, R.id.ll_video_authority, R.id.btn_new_folder, R.id.commit})
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
        }
    }


    private void newFolder()
    {
        if (TextUtils.isEmpty(mEditTitleText.getText()))
        {
            Toast.makeText(getActivity(), "名称不能为空", Toast.LENGTH_SHORT).show();
        }
        else
        {
            try
            {
                bm.getInt("sortId");
            }
            catch (NullPointerException e)
            {
                Toast.makeText(getActivity(), "请选择分类", Toast.LENGTH_SHORT).show();
                return;
            }
            requestSaveAlbum();
        }
    }


    private void requestSaveAlbum()
    {
        showLoding();
        //3.保存（新建）文件夹
        //http://weitu.bookus.cn/mediaAlbum/save.json?text={"uid":"565bea2c984ec06f56befda3","tags":"good","title":"well","introduction":"enen","label1":5000,"method":"mediaAlbum.save"}
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("uid", mPreference.getString(Preference.uid));
        String[] ss = mTagGroup.getTags();
        try
        {
            JSONArray jsonarray = new JSONArray(Arrays.toString(ss));
            params.put("tags", jsonarray);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        params.put("title", mEditTitleText.getText().toString());
        params.put("introduction", mDescText.getText().toString());
        params.put("label1", bm.getInt("sortId"));
        params.put("method", "mediaAlbum.save");
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
                Log.w("guanglog", " map callback  " + json);
                Bundle bm1 = new Bundle();
                bm1.putString("title", mEditTitleText.getText().toString());
                bm1.putString("desc", mDescText.getText().toString());
                bm1.putString("sort", mSortText.getText().toString());
                bm1.putBoolean("isnew", true);
                EventBus.getDefault().post(new MessageEvent(bm1));
                onBackPressed();
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(MessageEvent event)
    {
        if (event.message.getString("target").equals(this.getClass().getName().toString()))
        {
            bm = event.message;
        }
    }


    private void setTitle()
    {
        mTitleText.setText("新建视频文件夹");
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
        bd.putString(ContentActivity.FRAG_CLS, VideoSortFragment.class.getName());
        bd.putBoolean(ContentActivity.FRAG_ISBACK, true);
        bd.putBoolean(ContentActivity.FRAG_WITH_ANIM, true);
        mContext.startActivity(bd, ContentActivity.class);

    }


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
}
