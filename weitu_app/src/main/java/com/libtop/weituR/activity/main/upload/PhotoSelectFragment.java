//package com.libtop.weituR.activity.main.upload;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.GridView;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.libtop.weitu.R;
//import com.libtop.weituR.activity.ContentActivity;
//import com.libtop.weituR.activity.main.adapter.PhotoGridAdapter;
//import com.libtop.weituR.base.impl.PhotoFragment;
//import com.libtop.weituR.http.HttpRequest;
//import com.libtop.weituR.tool.Preference;
//import com.libtop.weituR.utils.CollectionUtils;
//import com.libtop.weituR.utils.SdCardUtil;
//import com.zhy.http.okhttp.callback.StringCallback;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.xutils.view.annotation.ContentView;
//import org.xutils.view.annotation.Event;
//import org.xutils.view.annotation.ViewInject;
//
//import java.io.File;
//import java.io.FileFilter;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import okhttp3.Call;
//
///**
// * Created by Administrator on 2016/1/18 0018.
// */
//setInjectContentView(R.layout.fragment_select_photo)
//public class PhotoSelectFragment extends PhotoFragment {
//    @Bind(R.id.back_btn)
//    ImageView mBackBtn;
//    @Bind(R.id.title)
//    TextView mTitleText;
//    @Bind(R.id.commit)
//    TextView mCommitBtn;
//
//    @Bind(R.id.grid_view)
//    GridView mGrid;
//    private PhotoGridAdapter mAdapter;
//    private List<File> mImgs=new ArrayList<File>();
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mAdapter=new PhotoGridAdapter(mContext,mImgs);
//    }
//
//    @Override
//    public void onCreation(View root) {
//        setTitle();
//        mGrid.setAdapter(mAdapter);
////        mGrid.setOnItemClickListener(this);
//        fileTask.execute();
//    }
//
//    private void setTitle(){
////        mBackBtn.setOnClickListener(this);
//        mTitleText.setText("选择图片");
////        mCommitBtn.setOnClickListener(this);
//    }
//
//    @Nullable @OnClick({R.id.back_btn,R.id.commit})
//    public void onClick(View v) {
//        switch (v.getId()){
//            case R.id.back_btn:
//                onBackPressed();
//                break;
//            case R.id.commit:
//                commit();
//                break;
//        }
//    }
//
//
//    private String getArrayContent(String[] datas){
//        StringBuffer s=new StringBuffer();
//        int size=datas.length;
//        for (int i=0;i<size;i++){
//            s.append(datas[i]);
//            if (i!=size-1){
//                s.append("|");
//            }
//        }
//        return s.toString();
//    }
//
//    private String[] getFileNames(File[] files){
//        String[] s=new String[files.length];
//        for(int i=0;i<files.length;i++){
//            s[i]=files[i].getName();
//        }
//        return s;
//    }
//
//    private void commit(){
//        List<File> files=mAdapter.getChecked();
//        if (CollectionUtils.isEmpty(files)){
//            showToast("请选择要提交的照片");
//            return;
//        }
//        File[] f=new File[files.size()];
//        request(((ContentActivity)mContext).getCurrentExtra().getString("aid")
//                ,files.toArray(f));
//    }
//
//
//    private void request(final String id, final File[] files){
//        Map<String, Object> addition = new HashMap<String, Object>();
//        addition.put("aid", id);
////        addition.put("fileName",new String[]{new File(path).getName()});
//        addition.put("fileName",getArrayContent(getFileNames(files)));
//       // addition.put("libraryCode", mPreference.getString(Preference.SchoolId));
//        addition.put("libraryCode", "10564");
//        addition.put("method", "image.add");
//        mLoading.show();
//        HttpRequest.loadWithMap(addition)
//                .execute(new StringCallback() {
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//
//                    }
//
//                    @Override
//                    public void onResponse(String json, int id) {
//                        Log.w("data:", json);
//                        String[] ids;
//                        try {
//                            JSONArray array = new JSONArray(json);
//                            ids = new String[array.length()];
//                            for (int i = 0; i < ids.length; i++) {
//                                ids[i] = String.valueOf(array.get(i));
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            if (mLoading.isShowing()){
//                                mLoading.dismiss();
//                            }
//                            showToast("上传出错");
//                            return;
//                        }
//                        requestFinal(((ContentActivity) mContext).getCurrentExtra().getString("album_url")
//                                , files, ids);
//                    }
//                });
//    }
//
//    private void requestFinal(String url,File[] files,String[] ids){
//        Map<String, Object> params=new HashMap<String, Object>();
//        params.put("tids", getArrayContent(ids));
//        HttpServiceUtil.upload(url, files, params, new HttpServiceUtil.OnRequestCallBack() {
//            @Override
//            public void onSucc(String json) {
//                Log.w("upload succ:", json);
//                if (mLoading.isShowing()){
//                    mLoading.dismiss();
//                }
//                showToast("上传成功");
//                onBackPressed();
//            }
//
//            @Override
//            public void onFalid(int code) {
//                Log.w("upload faild:", code + "");
//                if (mLoading.isShowing()){
//                    mLoading.dismiss();
//                }
//                showToast("上传失败");
//            }
//
//            @Override
//            public void onUpload(long current, long total) {
//
//            }
//        });
//    }
//
//    @Override
//    public void onBackPressed() {
//        ((ContentActivity)mContext).popBack();
//    }
//
//    @Nullable @OnClick(value = R.id.grid_view,type = AdapterView.OnItemClickListener.class)
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        if (position>0){
//            mAdapter.check(position-1);
//        }else if (position==0){
//            openCamera();
//        }
//    }
//
//    private AsyncTask<Void,Void,Void> fileTask=new AsyncTask<Void, Void, Void>() {
//        @Override
//        protected Void doInBackground(Void... params) {
//            serachImgFiles("/sdcard");
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            mAdapter.update(mImgs);
//        }
//    };
//
//    private Handler mHandler=new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            if (msg.what==0){
//                mAdapter.update(mImgs);
//            }
//        }
//    };
//
//    private int mCount=0;
//
//    private void serachImgFiles(String path){
//        File root=new File(path);
//        File[] fs=root.listFiles(mFileFilter);
//        for (File f:fs) {
//            if(f.isDirectory()){
//                serachImgFiles(f.getPath());
//            }else{
//                mImgs.add(f);
//                mCount++;
//                if (mCount==15){
//                    mHandler.sendEmptyMessage(0);
//                    mCount=0;
//                }
//            }
//        }
//    }
//
//    private ImgFilter mFileFilter=new ImgFilter();
//
//    private class ImgFilter implements FileFilter {
//
//        @Override
//        public boolean accept(File pathname) {
//            if(pathname.isDirectory())
//                return true;
//            else{
//                String name = pathname.getName();
//                if(name.endsWith(".png") || name.endsWith(".bmp")
//                        ||name.endsWith(".jpg") || name.endsWith(".jepg"))
//                    return true;
//                else
//                    return false;
//            }
//        }
//    }
//
//    @Override
//    public void onResult(int request, int result, Intent data) {
//       if (request==REQUEST_CODE_CAMERA&&result== Activity.RESULT_OK){
//           cropPhoto(Uri.parse(SdCardUtil.TEMP));
//       }else if (request==REQUEST_CODE_PHOTO_DEAL&&result==Activity.RESULT_OK){
//
//       }
//    }
//}
