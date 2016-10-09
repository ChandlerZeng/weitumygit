package com.libtop.weitu.activity.user;

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
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.main.LibraryFragment;
import com.libtop.weitu.base.impl.PhotoFragment;
import com.libtop.weitu.dao.ResultCodeDto;
import com.libtop.weitu.http.MapUtil;
import com.libtop.weitu.http.WeituNetwork;
import com.libtop.weitu.utils.Preference;
import com.libtop.weitu.utils.ClippingPicture;
import com.libtop.weitu.utils.ContantsUtil;
import com.libtop.weitu.utils.SdCardUtil;
import com.libtop.weitu.utils.selector.MultiImageSelectorActivity;
import com.libtop.weitu.utils.selector.utils.AlertDialogUtil;
import com.libtop.weitu.utils.selector.view.MyAlertDialog;
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
public class UserInfoFragment extends PhotoFragment
{
    public static final int REQUEST_IMAGE = 2;

    @Bind(R.id.sex_value)
    TextView mSexText;
    @Bind(R.id.tv_library)
    TextView tvLibrary;
    @Bind(R.id.img_head)
    ImageView imgHead;

    private Bitmap mBitmap;

    private String mSex = "";

    public static boolean isUpdateAvatar;
    private boolean isUpdateSex;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected int getLayoutId()
    {
        return R.layout.fragment_user_info;
    }


    private void openSexDialog()
    {
        String title = "请选择性别";
        final AlertDialogUtil dialog = new AlertDialogUtil();
        dialog.showDialog(mContext, title, "女同学", "男同学", new MyAlertDialog.MyAlertDialogOnClickCallBack()
        {
            @Override
            public void onClick()
            {
                //女同学
                mSexText.setText("性别：女");
                mSex = "1";
                mPreference.putString(Preference.sex, mSex);
                isUpdateSex = true;

            }
        }, new MyAlertDialog.MyAlertDialogOnClickCallBack()
        {
            @Override
            public void onClick()
            {
                //男同学
                mSexText.setText("性别：男");
                mSex = "0";
                mPreference.putString(Preference.sex, mSex);
                isUpdateSex = true;
            }
        });
    }


    @Override
    public void onCreation(View root)
    {
        mSex = mPreference.getString(Preference.sex);
        if (!mSex.equals("0") && !mSex.equals("1"))
        {
            mSex = "0"; //当为其它的未知性别时, 默认显示为"男"
        }

        mSexText.setText("性别：" + (mSex.equals("0") ? "男" : "女"));
        String uid = mPreference.getString(Preference.uid);
        String avatar = ContantsUtil.getAvatarUrl(uid);
        Picasso.with(mContext).load(avatar).placeholder(R.drawable.user_default_icon).error(R.drawable.user_default_icon).networkPolicy(NetworkPolicy.NO_CACHE).into(imgHead);
    }


    @Override
    public void onResume()
    {
        super.onResume();
        String schoolName = mPreference.getString(Preference.SchoolName);
        if (!TextUtils.isEmpty(schoolName))
        {
            tvLibrary.setText(schoolName);
        }
    }


    @Nullable
    @OnClick({R.id.tv_cancel, R.id.password, R.id.sex_value, R.id.avatar, R.id.tv_library, R.id.tv_save})
    public void onClick(View v)
    {
        Bundle bundle = new Bundle();
        String cls = "";
        switch (v.getId())
        {
            case R.id.tv_cancel:
                mContext.finish();
                break;
            //修改密码
            case R.id.password:
                break;

            //修改性别
            case R.id.sex_value:
                openSexDialog();
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
        if (!TextUtils.isEmpty(cls))
        {
            bundle.putBoolean(ContentActivity.FRAG_ISBACK, true);
            bundle.putString(ContentActivity.FRAG_CLS, cls);
            mContext.startActivity(bundle, ContentActivity.class);
        }
    }


    @Override
    public void onBackPressed()
    {
        mContext.finish();
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }


    private void loadPickUp()
    {
        Intent intent = new Intent(getActivity(), MultiImageSelectorActivity.class);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, 0);
        mContext.startActivityForResult(intent, REQUEST_IMAGE);
    }


    @Override
    public void onResult(int request, int result, Intent data)
    {
        if (result != Activity.RESULT_OK)
        {
            return;
        }
        switch (request)
        {
            case REQUEST_CODE_CAMERA:
                cropPhoto(Uri.parse(SdCardUtil.TEMP));
                break;
            case REQUEST_IMAGE:
                String a = "file:///" + data.getStringExtra("lamge");
                //Uri uri = d1ata.getData();
                cropPhoto(Uri.parse(a));
                break;
            case REQUEST_CODE_PHOTO_DEAL:
                mBitmap = ClippingPicture.resizeBitmap(Uri.parse(SdCardUtil.TEMP).getPath(), 60, 60);
                if (mBitmap == null)
                {
                    showToast("读取图片失败，请重新选择图片");
                    break;
                }
                imgHead.setImageBitmap(mBitmap);
                isUpdateAvatar = true;
                break;
        }
    }


    private void saveUserInfo()
    {
        showLoding();
        unsubscribe();

        List<Observable<ResultCodeDto>> observablelists = new ArrayList<>();

        if (isUpdateSex)
        {
            Observable<ResultCodeDto> sexObservable = getSexObservable();
            observablelists.add(sexObservable);
        }

        if (mBitmap != null)
        {
            Observable<ResultCodeDto> avatarObservable = getAvatarObservable();
            observablelists.add(avatarObservable);
        }

        Observable<ResultCodeDto> libObservable = getLibObservable();
        observablelists.add(libObservable);

        subscription = Observable.zip(observablelists, new FuncN<List<ResultCodeDto>>()
        {
            @Override
            public List<ResultCodeDto> call(Object... args)
            {
                List<ResultCodeDto> lists = new ArrayList<ResultCodeDto>();
                for (Object arg : args)
                {
                    ResultCodeDto resultCodeDto = (ResultCodeDto) arg;
                    lists.add(resultCodeDto);
                }
                return lists;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<ResultCodeDto>>()
        {
            @Override
            public void onCompleted()
            {
            }


            @Override
            public void onError(Throwable e)
            {
                dismissLoading();
                showToast("更新失败");
                Log.w("guanglog", "个人信息更新失败" + e);
            }


            @Override
            public void onNext(List<ResultCodeDto> resultCodeDtos)
            {
                dismissLoading();
                for (ResultCodeDto resultCodeDto : resultCodeDtos)
                {
                    if (resultCodeDto == null || resultCodeDto.code != 1)
                    {
                        showToast("更新失败");
                        return;
                    }
                    else
                    {
                        showToast("更新成功");
                    }
                }
            }
        });

    }


    //更新头像请求
    private Observable<ResultCodeDto> getAvatarObservable()
    {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("method", "user.uploadAvatar");
        params.put("uid", Preference.instance(mContext).getString(Preference.uid));
        params.put("avatar", ClippingPicture.bitmapToBase64(mBitmap));
        String[] arrays = MapUtil.map2Parameter(params);
        return WeituNetwork.getWeituApi().getResultCode(arrays[0], arrays[1], arrays[2]);
    }


    //更新性别请求
    private Observable<ResultCodeDto> getSexObservable()
    {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("method", "user.updateSex");
        params.put("uid", Preference.instance(mContext).getString(Preference.uid));
        params.put("sex", mSex);
        String[] arrays = MapUtil.map2Parameter(params);
        return WeituNetwork.getWeituApi().getResultCode(arrays[0], arrays[1], arrays[2]);
    }


    //更新图书馆请求
    private Observable<ResultCodeDto> getLibObservable()
    {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("uid", Preference.instance(mContext).getString(Preference.uid));
        params.put("lid", mPreference.getString(Preference.SchoolId));
        params.put("method", "user.updateLibrary");
        String[] arrays = MapUtil.map2Parameter(params);
        return WeituNetwork.getWeituApi().getResultCode(arrays[0], arrays[1], arrays[2]);
    }

}
