package com.libtop.weituR.activity.main.videoUpload;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.libtop.weitu.R;
import com.libtop.weituR.activity.ContentActivity;
import com.libtop.weituR.activity.ContentFragment;
import com.libtop.weituR.activity.main.dto.VideoFolderBean;
import com.libtop.weituR.eventbus.MessageEvent;
import com.libtop.weituR.http.HttpRequest;
import com.libtop.weituR.widget.TagGroup;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import io.vov.vitamio.utils.StringUtils;
import okhttp3.Call;

/**
 * Created by LianTu on 2016/4/25.
 */
public class VideoEditFolderFragment extends ContentFragment {

    @Bind(R.id.title)
    TextView mTitleText;

    @Bind(R.id.tv_sort)
    TextView mSortText;

    @Bind(R.id.et_title)
    EditText mEditTitleText;

    @Bind(R.id.et_desc)
    EditText mDescText;

    @Bind(R.id.tag_group)
    TagGroup mTagGroup;

    private Bundle bm;
    private Bundle bundle;
    private VideoFolderBean videoFolderBean;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = ((ContentActivity) getActivity()).getCurrentExtra();
        videoFolderBean = new Gson().fromJson(bundle.getString("videoFolderBean"), VideoFolderBean.class);
        EventBus.getDefault().register(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_video_editfolder;
    }

    @Override
    public void onCreation(View root) {
        initView();
    }

    private void initView() {
        setTitle();
        if (videoFolderBean != null) {
            mEditTitleText.setText(videoFolderBean.title);
            if (!TextUtils.isEmpty(videoFolderBean.introduction)) {
                mDescText.setText(videoFolderBean.introduction);
            }
            if (videoFolderBean.tags != null && videoFolderBean.tags.length != 0) {
//                mTagText.setText(videoFolderBean.tags);
                mTagGroup.setTags(videoFolderBean.tags);
            }
        }
        mDescText.addTextChangedListener(textWatcher);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (bm != null) {
            videoFolderBean.categoriesName1 = bm.getString("sort");
            videoFolderBean.label1 = bm.getInt("sortId");
        }
        mSortText.setText(videoFolderBean.categoriesName1);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onPause() {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mContext.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {

        }
//
//        if (isOpen){
//            imm.toggleSoftInput(
//                    0, InputMethodManager.HIDE_NOT_ALWAYS);
//        }
        super.onPause();
    }

    @Nullable
    @OnClick({R.id.back_btn, R.id.ll_video_sort, R.id.ll_video_authority, R.id.btn_new_folder})
    public void onClick(View v) {
        switch (v.getId()) {
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
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {
        private CharSequence temp;
        private int editStart;
        private int editEnd;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub
            temp = s;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
            editStart = mDescText.getSelectionStart();
            editEnd = mDescText.getSelectionEnd();

            if (temp.length() > 50) {
                Toast.makeText(mContext, "你输入的字数已经超过了限制！", Toast.LENGTH_SHORT).show();
                s.delete(editStart - 1, editEnd);
                int tempSelection = editStart;
                mDescText.setText(s);
                mDescText.setSelection(tempSelection);
            }
        }
    };

    private void newFolder() {
        if (TextUtils.isEmpty(mEditTitleText.getText())) {
            Toast.makeText(getActivity(), "名称不能为空", Toast.LENGTH_SHORT).show();
//        }else if(mDescText.getText().toString().length()>50){
//            Toast.makeText(getActivity(),mDescText.getText().toString().length()+"个字,"+"描述超过50字",Toast.LENGTH_SHORT).show();
//        }else if (mTagText.getText().toString().length()>8){
//            Toast.makeText(getActivity(),"标签超过8个字符",Toast.LENGTH_SHORT).show();
        } else {
//            try {
//                bm.getInt("sortId");
//            }catch (NullPointerException e){
//                Toast.makeText(getActivity(),"请选择分类",Toast.LENGTH_SHORT).show();
//                return;
//            }
            requestSaveAlbum();
        }
    }

    private void requestSaveAlbum() {
        showLoding();
        //修改文件夹属性， id 是该文件夹的id
        //http://weitu.bookus.cn/mediaAlbum/update.json?text={"id":"WEROPOSLDFKSDFOSPFSDFKL","tags":"good","title":"well","introduction":"enen","label1":5000,"method":"mediaAlbum.update"}
        Map<String, Object> params = new HashMap<String, Object>();
        if (videoFolderBean != null) {
            params.put("id", videoFolderBean.id);
        }
        String[] tagStrngs = mTagGroup.getTags();
        String tagString = StringUtils.join(tagStrngs, ",");
        params.put("tags", tagString);//lid
        params.put("title", mEditTitleText.getText().toString());
        params.put("introduction", mDescText.getText().toString());
        params.put("label1", videoFolderBean.label1);
        params.put("method", "mediaAlbum.update");
        HttpRequest.loadWithMap(params)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String json, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(json);
                            if (((Integer) jsonObject.get("code")) == 1) {
                                Toast.makeText(mContext, "修改成功", Toast.LENGTH_SHORT).show();
                                dismissLoading();
                                onBackPressed();
                            } else {
                                Toast.makeText(mContext, "修改失败", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(MessageEvent event) {
        bm = event.message;
    }

    private void setTitle() {
        mTitleText.setText("编辑信息");
    }

    private void videoAuthority() {
        Bundle bd = new Bundle();
        bd.putString(ContentActivity.FRAG_CLS, VideoAuthorityFragment.class.getName());
        bd.putBoolean(ContentActivity.FRAG_ISBACK, true);
        bd.putBoolean(ContentActivity.FRAG_WITH_ANIM, true);
        mContext.startActivity(bd, ContentActivity.class);
    }

    private void videoSort() {
        // tags = tagsView.getTags();
        Bundle bd = new Bundle();
        bd.putString(ContentActivity.FRAG_CLS, VideoSortFragment.class.getName());
        bd.putBoolean(ContentActivity.FRAG_ISBACK, true);
        bd.putBoolean(ContentActivity.FRAG_WITH_ANIM, true);
        mContext.startActivity(bd, ContentActivity.class);
    }

}
