package com.libtop.weitu.activity.main.upload;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.main.DocUpload.DocUploadActivity;
import com.libtop.weitu.activity.main.videoUpload.VideoSelectActivity;
import com.libtop.weitu.base.BaseFragment;
import com.libtop.weitu.utils.LargeMappedFiles;
import com.libtop.weitu.utils.selector.view.ImageSelectActivity;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import io.vov.vitamio.utils.Log;

/**
 * Created by Administrator on 2016/1/25 0025.
 */
public class UploadFragment extends BaseFragment {
    private static final int REQUEST_IMAGE = 2;
    @Bind(R.id.back_btn)
    ImageView mBackBtn;
    @Bind(R.id.title)
    TextView mTitleText;
    @Bind(R.id.video)
    LinearLayout mVideoBtn;
    @Bind(R.id.image)
    LinearLayout mImgBtn;
    @Bind(R.id.document)
    LinearLayout mDocBtn;

    private void setTitle() {
        mTitleText.setText("上传");
    }

    private int addSize = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_upload;
    }

    @Override
    public void onCreation(View root) {
        setTitle();
    }

    @Nullable
    @OnClick({R.id.back_btn, R.id.video, R.id.image, R.id.document})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                onBackPressed();
                break;
            case R.id.video:
//                postI(addSize);
                  Intent intent3 = new Intent(mContext, VideoSelectActivity.class);
                  startActivity(intent3);
//                Bundle bd1 = new Bundle();
//                bd1.putString(ContentActivity.FRAG_CLS, VideoSelectFragment.class.getName());
//                bd1.putBoolean(ContentActivity.FRAG_ISBACK, true);
//                bd1.putBoolean(ContentActivity.FRAG_WITH_ANIM, true);
//                mContext.startActivity(bd1, ContentActivity.class);
                //Toast.makeText(mContext, ContantsUtil.IS_DEVELOPING, Toast.LENGTH_SHORT).show();
                break;
            case R.id.image:
//                Bundle bd = new Bundle();
//                bd.putString(ContentActivity.FRAG_CLS, AlbumCreateFragment.class.getName());
//                bd.putBoolean(ContentActivity.FRAG_ISBACK, true);
//                bd.putBoolean(ContentActivity.FRAG_WITH_ANIM, true);
//                mContext.startActivity(bd, ContentActivity.class);
//                Toast.makeText(mContext, ContantsUtil.IS_DEVELOPING, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, ImageSelectActivity.class);
                // 是否显示拍摄图片
//                intent.putExtra(MultiImageSelectorActivity2.EXTRA_SHOW_CAMERA, false);
//                // 最大可选择图片数量
//                intent.putExtra(MultiImageSelectorActivity2.EXTRA_SELECT_COUNT, 9);
//                // 选择模式
//                intent.putExtra(MultiImageSelectorActivity2.EXTRA_SELECT_MODE, MultiImageSelectorActivity2.MODE_MULTI);
//                // 默认选择

                mContext.startActivity(intent);
                break;
            case R.id.document:
//                Toast.makeText(mContext, ContantsUtil.IS_DEVELOPING, Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(mContext, DocUploadActivity.class);
                startActivity(intent1);
                break;
        }
    }


    public void postI(int countSize) {
        LargeMappedFiles a = new LargeMappedFiles(mContext, "/storage/emulated/0/1122.png");
        int count = a.getFlowChunkNumber();
        if (countSize > count)
            return;
        getFile(a, countSize);
    }

    @Override
    public void onBackPressed() {
        mContext.finish();
    }

    public void getFile(final LargeMappedFiles largeMappedFiles, int i) {
        //LargeMappedFiles a = new LargeMappedFiles(mContext, "/storage/emulated/0/123.flv");
        final byte[] mbyte = largeMappedFiles.readFile(i);
        Log.i("mbyte" + i, mbyte.hashCode());
        final int number = i;
        //+ (int) mbyte.length
        final String flowChunkSize = "" + largeMappedFiles.flowChunkSize;
        final String flowTotalSize = "" + largeMappedFiles.getFileCountSize();
        final String flowIdentifier = "44bb33de984e2b4bbec55555";
        final String flowFilename = "1122.png";
        final String flowRelativePath = "1122.png";
        final String flowChunkNumber = "" + i;
        Map<String, String> params = new HashMap<String, String>();
        params.put("method", "user.auth");
        params.put("flowChunkSize", flowChunkSize);
        params.put("flowTotalSize", flowTotalSize);
        params.put("flowIdentifier", flowIdentifier);
        params.put("flowFilename", flowFilename);
        params.put("flowRelativePath", flowRelativePath);
        params.put("flowChunkNumber", flowChunkNumber);
        // postFile(flowChunkSize, flowTotalSize, flowIdentifier, flowFilename, flowRelativePath, flowChunkNumber, number, largeMappedFiles);
//        HttpServiceUtil.getFile(mContext, params
//                , new HttpServiceUtil.CallBack() {
//            @Override
//            public void callback(String jsonStr) {
//                mLoading.dismiss();
//           //     if (jsonStr.equals("200")) {
////                    addSize++;
////                    postI(addSize);
////                } else {
//                    postFile(flowChunkSize, flowTotalSize, flowIdentifier, flowFilename, flowRelativePath, flowChunkNumber, number, largeMappedFiles);
//             //   }
//            }
//        });
    }

//    public void postFile(String flowChunkSize, String flowTotalSize, String flowIdentifier, String flowFilename, String flowRelativePath, String flowChunkNumber, int number, LargeMappedFiles largeMappedFiles) {
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("method", "user.auth");
//        params.put("flowChunkSize", flowChunkSize);
//        params.put("flowTotalSize", flowTotalSize);
//        params.put("flowIdentifier", flowIdentifier);
//        params.put("flowFilename", flowFilename);
//        params.put("flowRelativePath", flowRelativePath);
//        params.put("flowChunkNumber", flowChunkNumber);
//        HttpServiceUtil.postFile(mContext, params, number, largeMappedFiles
//                , new HttpServiceUtil.CallBack() {
//            @Override
//            public void callback(String jsonStr) {
//                if (mLoading.isShowing()){
//                    mLoading.dismiss();
//                }
//                addSize++;
////                postI(addSize);
//            }
//        });
//    }


}
