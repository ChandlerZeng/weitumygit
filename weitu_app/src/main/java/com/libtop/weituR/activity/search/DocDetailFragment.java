//package com.libtop.weituR.activity.search;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.Html;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.View;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.libtop.weitu.R;
//import com.libtop.weituR.activity.ContentActivity;
//import com.libtop.weituR.activity.source.PdfActivity;
//import com.libtop.weituR.activity.search.dto.DocResult;
//import com.libtop.weituR.base.impl.ImgFragment;
//import com.libtop.weituR.utils.CheckUtil;
//import com.libtop.weituR.utils.ContantsUtil;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.xutils.view.annotation.ContentView;
//import org.xutils.view.annotation.ViewInject;
//import org.xutils.x;
//
///**
// * Created by Administrator on 2015/12/24 0024.
// */
//setInjectContentView(R.layout.activity_doc_detail)
//public class DocDetailFragment extends ImgFragment{
//    @Bind(R.id.icon)
//    ImageView mIcon;
//    @Bind(R.id.type)
//    TextView mType;
//    @Bind(R.id.heat)
//    TextView mHeat;
//    @Bind(R.id.collected)
//    TextView mCollected;
//    @Bind(R.id.browsed)
//    TextView mBrowsed;
//    @Bind(R.id.lookup)
//    TextView mLookupBtn;
//    @Bind(R.id.back_btn)
//    ImageView mBackBtn;
//    @Bind(R.id.title)
//    TextView mTitle;
//
//    private Bundle mCached;
//    private DocResult mCachedData;
//
//    @Override
//    public void onCreation(View root) {
//        mCached=((ContentActivity)mContext).getCurrentExtra();
//        mTitle.setText(mCached.getString("title"));
//        mBackBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });
//
//        mLookupBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String tempUrl = mCachedData.pdfUrl;
//                if (!TextUtils.isEmpty(tempUrl)) {
//                    Intent intent = new Intent();
//                    intent.putExtra("url", tempUrl);
//                    intent.setClass(mContext, PdfActivity.class);
//                    mContext.startActivityForResult(intent, 0x5554);
//                    mContext.overridePendingTransition(R.anim.zoomin,
//                            R.anim.alpha_outto);
//                }
//            }
//        });
//
//        setDetail();
//        setClicked(false);
//        load();
//    }
//
//    @Override
//    public void onBackPressed() {
//        if(mCached.getBoolean(ContentActivity.FRAG_ISBACK)){
//            ((ContentActivity)mContext).popBack();
//        }else{
//            mContext.finish();
//        }
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mCachedData=new DocResult();
//    }
//
//    //    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_doc_detail);
////        mLoading = new TranLoading(context);
////        mCachedData=new DocResult();
////        mCached=getIntent().getExtras();
////
////        mTitle.setText(mCached.getString("title"));
////        mBackBtn.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                finish();
////            }
////        });
////
////        mLookupBtn.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                String tempUrl = mCachedData.pdfUrl;
////                if (!TextUtils.isEmpty(tempUrl)) {
////                    Intent intent = new Intent();
////                    intent.putExtra("url", tempUrl);
////                    intent.setClass(context, PdfActivity.class);
////                    context.startActivityForResult(intent, 0x5554);
////                    context.overridePendingTransition(R.anim.zoomin,
////                            R.anim.alpha_outto);
////                }
////            }
////        });
////
////        setDetail();
////        setClicked(false);
////        load();
////    }
//
//    private void setDetail(){
//        String type=mCached.getString("type");
//        mType.setText(Html.fromHtml("<font color=\"#999999\">类型: </font><font color=\"#666666\">"
//                + (TextUtils.equals(type,"document")?"文档":"未知")
//                + "</font>"));
//        mHeat.setText(Html.fromHtml("<font color=\"#999999\">热度: </font><font color=\"#666666\">"
//                + mCached.getInt("hot") + "</font>"));
//        mCollected.setText(Html.fromHtml("<font color=\"#999999\">收藏: </font><font color=\"#666666\">"
//                + mCached.getInt("favorite") + "</font>"));
//        mBrowsed.setText(Html.fromHtml("<font color=\"#999999\">浏览: </font><font color=\"#666666\">"
//                + mCached.getInt("views") + "</font>"));
////        ImageLoader.getInstance().displayImage(
////                ContantsUtil.getCoverUrl(mCached.getString("id")), mIcon,
////                mOptions);
//        x.image().bind(mIcon,ContantsUtil.getCoverUrl(mCached.getString("id")),mOptions);
//    }
//
////    @Override
////    protected void onNewIntent(Intent intent) {
////        super.onNewIntent(intent);
////        mCached=intent.getExtras();
////        mTitle.setText(mCached.getString("title"));
////        load();
////    }
//
//    private void load(){
//        if (mCached==null){
//            return;
//        }
//        mLoading.show();
//        String url=ContantsUtil.getDocumentUrl(mCached.getString("id"));
//        Log.w("url:", url);
//        HttpServiceUtil.request(url, "get", null, new HttpServiceUtil.CallBack() {
//            @Override
//            public void callback(String json) {
//                Log.w("json", json);
//                mLoading.hide();
//                if (CheckUtil.isNullTxt(json)) {
//                    mCachedData.pdfUrl = "";
//                    mCachedData.id = "";
//                    mCachedData.title = "";
//                    setClicked(false);
//                    return;
//                }
//                if (!CheckUtil.isNull(json)) {
//                    try {
//                        JSONObject object = new JSONObject(json);
//                        mCachedData.form(object);
//                        setClicked(true);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        mCachedData.pdfUrl = "";
//                        mCachedData.id = "";
//                        mCachedData.title = "";
//                        setClicked(false);
//                    }
//                } else {
//                    showToast("未搜索到相关记录");
//                    setClicked(false);
//                }
//            }
//        });
//    }
//
//    @Override
//    public void onResult(int request, int result, Intent data) {
//        if (result==0x5555){
//            showToast("文件加载失败!");
//        }
//    }
//
//    private void setClicked(boolean clickable){
//        mLookupBtn.setClickable(clickable);
//        if (clickable){
//            mLookupBtn.setBackgroundResource(R.drawable.shape_bg_g1);
//        }else{
//            mLookupBtn.setBackgroundResource(R.drawable.shape_bg_g2);
//        }
//    }
//
//}
