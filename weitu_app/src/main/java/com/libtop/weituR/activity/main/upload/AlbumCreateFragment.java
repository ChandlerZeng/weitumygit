//package com.libtop.weituR.activity.main.upload;
//
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.TextView;
//
//import com.libtop.weitu.R;
//import com.libtop.weituR.activity.ContentActivity;
//import com.libtop.weituR.base.BaseFragment;
//import com.libtop.weituR.http.HttpRequest;
//import com.libtop.weituR.tool.Preference;
//import com.zhy.http.okhttp.callback.StringCallback;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.xutils.view.annotation.ContentView;
//import org.xutils.view.annotation.Event;
//import org.xutils.view.annotation.ViewInject;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import okhttp3.Call;
//
///**
// * Created by Administrator on 2016/1/14 0014.
// */
//setInjectContentView(R.layout.fragment_create_album)
//public class AlbumCreateFragment extends BaseFragment {
//    @Bind(R.id.edit)
//    EditText mEdit;
//
//    @Bind(R.id.title)
//    TextView mTitleText;
//
//    @Override
//    public void onCreation(View root) {
//        mTitleText.setText(R.string.create_album);
//    }
//
//    @Override
//    public void onBackPressed() {
//        ((ContentActivity) mContext).popBack();
//    }
//
//    @Nullable @OnClick({R.id.back_btn, R.id.next})
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.back_btn:
//                onBackPressed();
//                break;
//            case R.id.next:
//                commitToCreate();
//                break;
//        }
//    }
//
//    private void commitToCreate() {
//        String name = mEdit.getText().toString();
//        if (TextUtils.isEmpty(name)) {
//            showToast("请填写相册名称");
//            return;
//        }
//        mLoading.show();
//        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("title", name);
//        params.put("uid", mPreference.getString(Preference.uid));
//        params.put("username", mPreference.getString(Preference.UserName));
//       // params.put("libraryCode", mPreference.getString(Preference.SchoolId));
//       params.put("libraryCode", "10564");//10564
//        //http://www.yuntu.io/" http://www.bookus.cn/imageAlbum/add.json
//        params.put("method", "imageAlbum.add");
//        HttpRequest.loadWithMap(params)
//                .execute(new StringCallback() {
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//
//                    }
//
//                    @Override
//                    public void onResponse(String json, int responseId) {
//                        if (mLoading.isShowing()){
//                            mLoading.dismiss();
//                        }
//                        Log.w("data:", json);
//                        String id = "";
//                        String url = "";
//                        try {
//
//                            JSONObject jobj = new JSONObject(json);
//                            id = jobj.getString("aid");
//                            url=jobj.getString("uploadURL");
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        if (TextUtils.isEmpty(id) || TextUtils.isEmpty(url)) {
//                            showToast("创建失败");
//                            return;
//                        }
//                        showToast("创建成功");
//                        toNext(id, url);
//                    }
//                });
//    }
//
//    private void toNext(String id, String uploadUrl) {
//        Bundle bd = new Bundle();
//        bd.putString("aid", id);
//        bd.putString("album_url", uploadUrl);
//        bd.putString(ContentActivity.FRAG_CLS, PhotoSelectFragment.class.getName());
//        bd.putBoolean(ContentActivity.FRAG_ISBACK, true);
//        bd.putBoolean(ContentActivity.FRAG_WITH_ANIM, true);
//        mContext.startActivity(bd, ContentActivity.class);
//    }
//}
