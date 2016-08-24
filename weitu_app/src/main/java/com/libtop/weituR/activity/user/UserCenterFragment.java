package com.libtop.weituR.activity.user;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.ContentActivity;
import com.libtop.weituR.activity.login.LoginFragment;
import com.libtop.weituR.activity.main.DocUpload.DocUploadActivity;
import com.libtop.weituR.activity.main.LibraryFragment;
import com.libtop.weituR.activity.main.clickHistory.ClickHistoryActivity2;
import com.libtop.weituR.activity.main.lesson.LessonTypeListFragment;
import com.libtop.weituR.activity.main.upload.UploadFragment;
import com.libtop.weituR.activity.main.videoUpload.VideoSelectActivity;
import com.libtop.weituR.activity.user.UserCollect.UserCollectActivity;
import com.libtop.weituR.base.BaseFragment;
import com.libtop.weituR.tool.Preference;
import com.libtop.weituR.utils.CheckUtil;
import com.libtop.weituR.utils.ContantsUtil;
import com.libtop.weituR.utils.selector.view.ImageSelectActivity;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/1/8 0008.
 */
public class UserCenterFragment extends BaseFragment {
    @Bind(R.id.photo)
    ImageView mIconImg;
    @Bind(R.id.user_name)
    TextView mUserText;
    @Bind(R.id.library_name)
    TextView mLibText;
    @Bind(R.id.img_sex)
    ImageView imgSex;


//    private ImageOptions mOptions;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_user_center3;
    }


    @Nullable
    @OnClick({R.id.ll_upload_video,R.id.ll_upload_doc,R.id.ll_upload_photo,R.id.ll_history,R.id.ll_setting,
            R.id.lesson, R.id.collected, R.id.msg, R.id.comment, R.id.about_us, R.id.setting, R.id.left_msg
            , R.id.library, R.id.rl_login_msg, R.id.upload})
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        String cls = "";
        switch (v.getId()) {

            case R.id.ll_upload_video:
                if (CheckUtil.isNull(mPreference.getString(Preference.uid))) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString(ContentActivity.FRAG_CLS, LoginFragment.class.getName());
                    mContext.startActivity(bundle1, ContentActivity.class);
                } else {
                    Intent intent3 = new Intent(mContext, VideoSelectActivity.class);
                    mContext.startActivity(intent3);
                }
                break;
            case R.id.ll_upload_photo:
                if (CheckUtil.isNull(mPreference.getString(Preference.uid))) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString(ContentActivity.FRAG_CLS, LoginFragment.class.getName());
                    mContext.startActivity(bundle1, ContentActivity.class);
                } else {
                    Intent intent3 = new Intent(mContext, ImageSelectActivity.class);
                    mContext.startActivity(intent3);
                }
                break;
            case R.id.ll_upload_doc:
                if (CheckUtil.isNull(mPreference.getString(Preference.uid))) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString(ContentActivity.FRAG_CLS, LoginFragment.class.getName());
                    mContext.startActivity(bundle1, ContentActivity.class);
                } else {
                    Intent intent3 = new Intent(mContext, DocUploadActivity.class);
                    mContext.startActivity(intent3);
                }
                break;
            case R.id.ll_history:
                if (CheckUtil.isNull(mPreference.getString(Preference.uid))) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString(ContentActivity.FRAG_CLS, LoginFragment.class.getName());
                    mContext.startActivity(bundle1, ContentActivity.class);
                } else {
                    Intent intent = new Intent(mContext, ClickHistoryActivity2.class);
                    startActivity(intent);
                }
                break;
            case R.id.ll_setting:
                cls = SettingFragment.class.getName();
                break;
            case R.id.lesson:
                cls = LessonTypeListFragment.class.getName();
                break;
            case R.id.collected:
                if (CheckUtil.isNull(mPreference.getString(Preference.uid))) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString(ContentActivity.FRAG_CLS, LoginFragment.class.getName());
                    mContext.startActivity(bundle1, ContentActivity.class);
                } else {
                    Intent intent = new Intent(mContext, UserCollectActivity.class);
                    startActivity(intent);
                }

                break;
            case R.id.msg:
                Toast.makeText(getActivity(), ContantsUtil.IS_DEVELOPING, Toast.LENGTH_SHORT).show();
                break;
            case R.id.left_msg:
                if (CheckUtil.isNull(mPreference.getString(Preference.uid))) {
                    Toast.makeText(getActivity(), "请登录!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Bundle bundle1 = new Bundle();
                bundle1.putBoolean(ContentActivity.FRAG_ISBACK, false);
                bundle1.putBoolean(ContentActivity.FRAG_WITH_ANIM, true);
                bundle1.putString(ContentActivity.FRAG_CLS, FeedBackFragment.class.getName());
                mContext.startActivity(bundle1, ContentActivity.class);
                break;
            case R.id.comment:
                Intent intent = new Intent(mContext,MyCommentActivity.class);
                startActivity(intent);
                break;
            case R.id.about_us:
                Toast.makeText(getActivity(), ContantsUtil.IS_DEVELOPING, Toast.LENGTH_SHORT).show();
                break;
            case R.id.setting:
                cls = SettingFragment.class.getName();
                break;
            case R.id.library:
                bundle.putInt("from", 1);
                cls = LibraryFragment.class.getName();
                break;
            case R.id.rl_login_msg:
                if (CheckUtil.isNull(mPreference.getString(Preference.uid))) {
                    cls = LoginFragment.class.getName();
                } else {
                    cls = UserInfoFragment.class.getName();
                }
                break;
            case R.id.upload:
                if (CheckUtil.isNull(mPreference.getString(Preference.uid))) {
                    Bundle bundle2 = new Bundle();
                    bundle2.putString(ContentActivity.FRAG_CLS, LoginFragment.class.getName());
                    mContext.startActivity(bundle2, ContentActivity.class);
                } else {
                    Bundle bundle2 = new Bundle();
                    bundle2.putString(ContentActivity.FRAG_CLS, UploadFragment.class.getName());
                    mContext.startActivity(bundle2, ContentActivity.class);
                }
//                Bundle bundle2=new Bundle();
//                bundle2.putString(ContentActivity.FRAG_CLS, UploadFragment.class.getName());
//                mContext.startActivity(bundle2, ContentActivity.class);
                break;
        }
        if (!TextUtils.isEmpty(cls)) {
            bundle.putString(ContentActivity.FRAG_CLS, cls);
            mContext.startActivity(bundle, ContentActivity.class);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (CheckUtil.isNull(mPreference.getString(Preference.uid))) {
            mUserText.setText("点击登录");

//            mMineContainer.setVisibility(View.GONE);
//            mMineView.setVisibility(View.GONE);
            mIconImg.setImageResource(R.drawable.user_default_icon);
        } else {
            mUserText.setText(mPreference.getString(Preference.UserName));
            String sexString = mPreference.getString(Preference.sex);
            if (!TextUtils.isEmpty(sexString)){
                if (sexString.equals("0")) {
                    imgSex.setBackgroundResource(R.drawable.male);
                }else {
                    imgSex.setBackgroundResource(R.drawable.female);
                }
            }
            String uid = mPreference.getString(Preference.uid);
            String avatar = ContantsUtil.getAvatarUrl(uid);

            //设置头像
            Uri uri = Uri.parse(avatar);
            Picasso picasso = Picasso.with(mContext);

            if (UserInfoFragment.isUpdateAvatar) {
//                ImagePipeline imagePipeline = Fresco.getImagePipeline();
//                imagePipeline.evictFromMemoryCache(uri);
//                imagePipeline.evictFromDiskCache(uri);
                picasso.invalidate(uri);
                UserInfoFragment.isUpdateAvatar = false;
            }

            picasso.load(uri)
                    .transform(new CircleTransform())
                    .placeholder(R.drawable.user_default_icon)
                    .error(R.drawable.user_default_icon)
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .into(mIconImg);
//            mIconImg.setImageURI(uri);


//            x.image().bind(mIconImg, avatar, mOptions);
//            ImageLoader.getInstance().displayImage(avatar, mIconImg, mOptions);
//            mMineView.setVisibility(View.VISIBLE);
//            mMineContainer.setVisibility(View.VISIBLE);
        }
        if (CheckUtil.isNull(mPreference.getString(Preference.SchoolCode))) {
            mLibText.setText("选择图书馆");
        } else {
            mLibText.setText(mPreference.getString(Preference.SchoolName));
        }
    }

    public class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap,
                    BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }

}
