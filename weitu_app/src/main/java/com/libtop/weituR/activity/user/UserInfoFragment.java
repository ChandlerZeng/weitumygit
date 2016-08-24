package com.libtop.weituR.activity.user;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.ContentActivity;
import com.libtop.weituR.activity.main.LibraryFragment;
import com.libtop.weituR.base.impl.PhotoFragment;
import com.libtop.weituR.dao.ResultCodeDto;
import com.libtop.weituR.http.MapUtil;
import com.libtop.weituR.http.WeituNetwork;
import com.libtop.weituR.tool.CommonUtil;
import com.libtop.weituR.tool.Preference;
import com.libtop.weituR.utils.ClippingPicture;
import com.libtop.weituR.utils.ContantsUtil;
import com.libtop.weituR.utils.SdCardUtil;
import com.libtop.weituR.utils.selector.MultiImageSelectorActivity;
import com.libtop.weituR.utils.selector.utils.AlertDialogUtil;
import com.libtop.weituR.utils.selector.view.MyAlertDialog;
import com.libtop.weituR.widget.dialog.PhotoPickup;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.FuncN;
import rx.schedulers.Schedulers;

/**

 * Created by Administrator on 2016/1/11 0011.
 */
public class UserInfoFragment extends PhotoFragment {
    public static final int REQUEST_IMAGE = 2;

//    @Bind(R.id.back_btn)
//    ImageView mBackBtn;

//    @Bind(R.id.password)
//    RelativeLayout mPasswdBtn;
//    @Bind(R.id.name_value)
//    TextView mNickText;
    @Bind(R.id.sex_value)
    TextView mSexText;
//    @Bind(R.id.sex_con)
//    RelativeLayout mSexBtn;
//    @Bind(R.id.avatar)
//    RelativeLayout mAvatarBtn;
    @Bind(R.id.tv_library)
    TextView tvLibrary;
    @Bind(R.id.img_head)
    ImageView imgHead;

//    private SexDialog mDialog;
    private PhotoPickup mPickup;
    private Bitmap mBitmap;

    private String mSex = "";

    public static boolean isUpdateAvatar;
    private boolean isUpdateSex;

//    private String tempSName,tempSCode,tempSId;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        mDialog = new SexDialog(mContext);
//        mDialog.setCall(new BaseListDialog.CallBack() {
//            @Override
//            public void callBack(String key, String value) {
//                if (!mSex.equals(key)) {
//                    mSex = key;
//                    mSexText.setText("性别："+value);
//                    mPreference.putString(Preference.sex, mSex);
//                    isUpdateSex = true;
////                    updateSex();
//                }
//            }
//        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_user_info2;
    }

    private void  openSexDialog(){
        String title = "请选择性别";
        final AlertDialogUtil dialog = new AlertDialogUtil();
        dialog.showDialog(mContext, title, "女同学", "男同学", new MyAlertDialog.MyAlertDialogOnClickCallBack() {
            @Override
            public void onClick() {
                //女同学
                mSexText.setText("性别：女");
                mSex = "1";
                mPreference.putString(Preference.sex, mSex);
                isUpdateSex = true;

            }
        }, new MyAlertDialog.MyAlertDialogOnClickCallBack() {
            @Override
            public void onClick() {
                //男同学
                mSexText.setText("性别：男");
                mSex = "0";
                mPreference.putString(Preference.sex, mSex);
                isUpdateSex = true;
            }
        });
    }

    @Override
    public void onCreation(View root) {
        mSex = mPreference.getString(Preference.sex);
        mSexText.setText("性别："+CommonUtil.getValue("sex" + mSex));
//        String nickName = mPreference.getString(Preference.UserName);
//        mNickText.setText(nickName);
        String uid = mPreference.getString(Preference.uid);
        String avatar = ContantsUtil.getAvatarUrl(uid);
        Picasso.with(mContext)
                .load(avatar)
                .placeholder(R.drawable.user_default_icon)
                .error(R.drawable.user_default_icon)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(imgHead);
//        savePhoto();
    }

    @Override
    public void onResume() {
        super.onResume();
        String schoolName = mPreference.getString(Preference.SchoolName);
        if (!TextUtils.isEmpty(schoolName)){
            tvLibrary.setText(schoolName);
        }
    }

    //    private void saveSTemp(){
//        tempSName = mPreference.getString(Preference.SchoolName);
//        tempSCode = mPreference.getString(Preference.SchoolCode);
//        tempSId = mPreference.getString(Preference.SchoolId);
//    }

    @Nullable
    @OnClick({R.id.tv_cancel, R.id.password, R.id.sex_value, R.id.avatar,R.id.tv_library,R.id.tv_save})
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        String cls = "";
        switch (v.getId()) {
            case R.id.tv_cancel:
                mContext.finish();
                break;
            //修改密码
            case R.id.password:
//                ((ContentActivity) mContext).changeFragment(UpdatePasswdFragment.class.getName()
//                        , true, true);
                break;

            //修改性别
            case R.id.sex_value:
                openSexDialog();
//                mDialog.show();
                break;
            //修改头像
            case R.id.avatar:
                loadPickUp();
                break;
            //修改图书馆
            case R.id.tv_library:
                bundle.putInt("from", 1);
                cls = LibraryFragment.class.getName();
                break;

            //修改图书馆
            case R.id.tv_save:
                saveUserInfo();
                break;
        }
        if (!TextUtils.isEmpty(cls)) {
            bundle.putBoolean(ContentActivity.FRAG_ISBACK,true);
            bundle.putString(ContentActivity.FRAG_CLS, cls);
            mContext.startActivity(bundle, ContentActivity.class);
        }
    }

    @Override
    public void onBackPressed() {
        mContext.finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        mDialog.dismiss();
    }

    private void setTitle(View root) {
//        mBackBtn.setOnClickListener(this);
        ((TextView) root.findViewById(R.id.title)).setText(R.string.user_info);
    }

    private void loadPickUp() {
//        if (mPickup == null) {
//            mPickup = new PhotoPickup(mContext);
//            mPickup.setClickListener(new PhotoPickup.ClickListener() {
//                @Override
//                public void selectBtn(int selectId) {
//                    switch (selectId) {
//                        case 1:
//                            openCamera();
//                            break;
//                        case 2:
                            // pickUpPhoto();
                            Intent intent = new Intent(getActivity(), MultiImageSelectorActivity.class);
                            // 是否显示拍摄图片
                            intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
                            // 最大可选择图片数量
                            intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);
                            // 选择模式
                            intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, 0);
                            // 默认选择
                            mContext.startActivityForResult(intent, REQUEST_IMAGE);
//                            break;
//                    }
//                }
//            });
//        }
//        mPickup.show();
    }

    @Override
    public void onResult(int request, int result, Intent data) {
        if (result != Activity.RESULT_OK) {
            return;
        }
        switch (request) {
            case REQUEST_CODE_CAMERA:
                cropPhoto(Uri.parse(SdCardUtil.TEMP));
                break;
//            case REQUEST_CODE_PHOTO:
//                Uri uri = data.getData();
//                cropPhoto(uri);
//                break;f
            case REQUEST_IMAGE:
                String a = "file:///"+data.getStringExtra("lamge");
                //Uri uri = d1ata.getData();
                cropPhoto(Uri.parse(a));
                break;
            case REQUEST_CODE_PHOTO_DEAL:
                mBitmap = ClippingPicture.resizeBitmap(
                        Uri.parse(SdCardUtil.TEMP).getPath(), 60, 60);
                if (mBitmap == null) {
                    showToast("读取图片失败，请重新选择图片");
                    break;
                }
                imgHead.setImageBitmap(mBitmap);
                isUpdateAvatar = true;
//                savePhoto();
                break;
        }
    }

    private void saveUserInfo() {
        showLoding();
        unsubscribe();

        List<Observable<ResultCodeDto>> observablelists = new ArrayList<>();

        if (isUpdateSex){
            Observable<ResultCodeDto> sexObservable = getSexObservable();
            observablelists.add(sexObservable);
        }

        if (mBitmap!=null){
            Observable<ResultCodeDto> avatarObservable = getAvatarObservable();
            observablelists.add(avatarObservable);
        }

        Observable<ResultCodeDto> libObservable = getLibObservable();
        observablelists.add(libObservable);

        subscription = Observable.zip(observablelists,
                new FuncN<List<ResultCodeDto>>() {
                    @Override
                    public List<ResultCodeDto> call(Object... args) {
                        List<ResultCodeDto> lists = new ArrayList<ResultCodeDto>();
                        for (Object arg:args){
                            ResultCodeDto resultCodeDto = (ResultCodeDto) arg;
                            lists.add(resultCodeDto);
                        }
                        return lists;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<ResultCodeDto>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        dismissLoading();
                        showToast("更新失败");
                        Log.w("guanglog","个人信息更新失败"+e);
                    }

                    @Override
                    public void onNext(List<ResultCodeDto> resultCodeDtos) {
                        dismissLoading();
                        for (ResultCodeDto resultCodeDto : resultCodeDtos){
                            if (resultCodeDto==null || resultCodeDto.code != 1){
                                showToast("更新失败");
                                return;
                            }else {
                                showToast("更新成功");
                            }
                        }
                    }
                });

    }

    //更新头像请求
    private Observable<ResultCodeDto> getAvatarObservable(){
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("method", "user.uploadAvatar");
        params.put("uid", Preference.instance(mContext).getString(Preference.uid));
        params.put("avatar", ClippingPicture.bitmapToBase64(mBitmap));
        String[] arrays = MapUtil.map2Parameter(params);
        return WeituNetwork.getWeituApi().getResultCode(arrays[0],arrays[1],arrays[2]);
    }

    //更新性别请求
    private Observable<ResultCodeDto> getSexObservable(){
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("method", "user.updateSex");
        params.put("uid", Preference.instance(mContext).getString(Preference.uid));
//        params.put("phone", mPreference.getString(Preference.phone));
        params.put("sex", mSex);
        String[] arrays = MapUtil.map2Parameter(params);
        return WeituNetwork.getWeituApi().getResultCode(arrays[0],arrays[1],arrays[2]);
    }

    //更新图书馆请求
    private Observable<ResultCodeDto> getLibObservable(){
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("uid", Preference.instance(mContext).getString(Preference.uid));
        params.put("lid", mPreference.getString(Preference.SchoolId));
        params.put("method", "user.updateLibrary");
        String[] arrays = MapUtil.map2Parameter(params);
        return WeituNetwork.getWeituApi().getResultCode(arrays[0],arrays[1],arrays[2]);
    }

}
