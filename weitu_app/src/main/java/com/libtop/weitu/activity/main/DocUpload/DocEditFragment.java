package com.libtop.weitu.activity.main.DocUpload;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.ContentFragment;
import com.libtop.weitu.activity.main.dto.DocBean;
import com.libtop.weitu.activity.main.videoUpload.VideoAuthorityFragment;
import com.libtop.weitu.activity.main.videoUpload.VideoSortFragment;
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
import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;


/**
 * Created by LianTu on 2016/4/25.
 */
public class DocEditFragment extends ContentFragment{

    @Bind(R.id.title)
    TextView mTitleText;

    @Bind(R.id.tv_sort)
    TextView mSortText;

    @Bind(R.id.et_title)
    EditText mEditTitleText;

    @Bind(R.id.et_desc)
    EditText mDescText;

    @Bind(R.id.btn_new_folder)
    Button mBtnUploadDoc;

    private Bundle bm;
    private Bundle bundle;
    private DocBean docBean;

    @Bind(R.id.tag_group)
    TagGroup mTagGroup;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = ((ContentActivity)getActivity()).getCurrentExtra();
        docBean = new Gson().fromJson(bundle.getString("docBean"), DocBean.class);
        EventBus.getDefault().register(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_doc_edit;
    }

    @Override
    public void onCreation(View root) {
        setTitle();
        mDescText.addTextChangedListener(watcher);
        if (!TextUtils.isEmpty(bundle.getString("docPath"))){
            File file = new File(bundle.getString("docPath"));
            String fileName = file.getName().substring(0,file.getName().lastIndexOf("."));
            mEditTitleText.setText(fileName);
        }
        if (docBean!=null){
            mEditTitleText.setText(docBean.title);
            if (docBean.tags!=null&&docBean.tags.length!=0){
                mTagGroup.setTags(docBean.tags);
            }
            if (!TextUtils.isEmpty(docBean.categoriesName1)){
                mSortText.setText(docBean.categoriesName1);
            }
            if (!TextUtils.isEmpty(docBean.introduction)){
                mDescText.setText(docBean.introduction);
            }
        }else {
            docBean = new DocBean();
        }
        if (bundle.getBoolean("uploadDoc")){
            mTitleText.setText("上传文档");
            mBtnUploadDoc.setText("发布");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (bm != null){
            String sortText = bm.getString("sort");
            docBean.label1 = bm.getInt("sortId");
            mSortText.setText(sortText);
        }
    }

    private TextWatcher watcher = new TextWatcher() {
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
                if (mContext!=null){
                    Toast.makeText(mContext,"你输入的字数已经超过了限制！",Toast.LENGTH_SHORT).show();
                }
//                showToast("你输入的字数已经超过了限制！");
                s.delete(editStart - 1, editEnd);
                int tempSelection = editStart;
                mDescText.setText(s);
                mDescText.setSelection(tempSelection);
            }
        }
    };

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onPause() {
        InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        try {
            ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mContext.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }catch (Exception e){

        }
        super.onPause();
    }

    @Nullable
    @OnClick({R.id.back_btn,R.id.ll_video_sort,R.id.ll_video_authority,R.id.btn_new_folder})
    public void onClick(View v) {
        switch (v.getId()){
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

    private void newFolder() {
        if (TextUtils.isEmpty(mEditTitleText.getText())) {
            Toast.makeText(getActivity(), "名称不能为空", Toast.LENGTH_SHORT).show();
//        } else if (mDescText.getText().toString().length() > 50) {
//            Toast.makeText(getActivity(),mDescText.getText().toString().length()+"个字,"+"描述超过50字", Toast.LENGTH_SHORT).show();
//        } else if (mTagText.getTags().length > 4) {
//            Toast.makeText(getActivity(), "标签超过4项", Toast.LENGTH_SHORT).show();
//        }else if (tagOver8(mTagText.getTags())){
//            Toast.makeText(getActivity(), "有标签超过8个字符", Toast.LENGTH_SHORT).show();
        }else {
//            try {
//                bm.getInt("sortId");
//            }catch (NullPointerException e){
            if (TextUtils.isEmpty(mSortText.getText().toString())||mSortText.getText().toString().equals("请选择分类")){
                Toast.makeText(getActivity(),"请选择分类",Toast.LENGTH_SHORT).show();
                return;
            }
//                return;
//            }
            if (bundle.getBoolean("uploadDoc")){
                requestUpDoc();
            }else {
                requestSaveAlbum();
            }
        }
    }

    private boolean tagOver8(String[] tags){
        boolean result = false;
        if (tags!=null){
            for (String tag:tags){
                if (tag.length()>8){
                    result = true;
                }
            }
        }
        return  result;
    }

    private void requestUpDoc() {
        //4.上传文档接口
        //http://weitu.bookus.cn/document/save.json?text={"uid":"565bea2c984ec06f56befda3","tags":["good"],"title":"well","introduction":"enen","label1":100000,"method":"document.save"}
        showLoding();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("uid",Preference.instance(mContext)
                .getString(Preference.uid));
        String[] ss = mTagGroup.getTags();
//        Arrays.asList(ss);
//        JsonArray jsonArray = new JsonArray();
//        jsonArray.add(Arrays.toString(ss));

        try {
            JSONArray jsonarray = new JSONArray(Arrays.toString(ss));
            params.put("tags",jsonarray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("title",mEditTitleText.getText().toString());
        params.put("introduction",mDescText.getText().toString());
        params.put("label1",bm.getInt("sortId"));
        params.put("method", "document.save");
        HttpRequest.loadWithMap(params)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String json, int id) {
                        dismissLoading();
                        Log.w("guanglog", json);
                        if (TextUtils.isEmpty(json)) {
                            showToast("上传失败");
                            return;
                        }
                        Intent data = new Intent();
                        data.putExtra("filePath", bundle.getString("docPath"));
                        getActivity().setResult(Activity.RESULT_OK, data);
                        onBackPressed();
                    }
                });

    }

    private void requestSaveAlbum() {
        showLoding();
        //3.文档编辑信息接口
        //http://weitu.bookus.cn/document/update.json?text={"id":"572b21ef984ed96c492170f9","tags":["good"],"title":"well","introduction":"enen","label1":5000,"method":"document.update"}
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", docBean.id);
        String[] ss = mTagGroup.getTags();
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(Arrays.toString(ss));
        try {
            JSONArray jsonarray = new JSONArray(Arrays.toString(ss));
            params.put("tags",jsonarray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        params.put("title",mEditTitleText.getText().toString());
        params.put("introduction",mDescText.getText().toString());
        params.put("label1",docBean.label1);
        params.put("method","document.update");
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
                                getActivity().setResult(Activity.RESULT_OK);
                                dismissLoading();
                                onBackPressed();
                            } else {
                                Toast.makeText(mContext,"修改失败",Toast.LENGTH_SHORT).show();
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

    private void setTitle(){
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
        Bundle bd = new Bundle();
        bd.putString(ContentActivity.FRAG_CLS, VideoSortFragment.class.getName());
        bd.putBoolean(ContentActivity.FRAG_ISBACK, true);
        bd.putBoolean(ContentActivity.FRAG_WITH_ANIM, true);
        mContext.startActivity(bd, ContentActivity.class);
    }

}
