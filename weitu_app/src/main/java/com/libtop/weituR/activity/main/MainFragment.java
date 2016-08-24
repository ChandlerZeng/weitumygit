package com.libtop.weituR.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.ContentActivity;
import com.libtop.weituR.activity.login.LoginFragment;
import com.libtop.weituR.activity.main.adapter.MainIconAdapter;
import com.libtop.weituR.activity.main.clazz.ClassmateFragment;
import com.libtop.weituR.activity.main.lent.LentFragment;
import com.libtop.weituR.activity.main.lesson.LessonTypeListFragment;
import com.libtop.weituR.activity.main.notice.NoticeFragment;
import com.libtop.weituR.activity.main.service.ServiceListFragment;
import com.libtop.weituR.activity.main.upload.UploadFragment;
import com.libtop.weituR.activity.search.CommentActivity;
import com.libtop.weituR.activity.search.SearchActivity;
import com.libtop.weituR.activity.source.PdfActivity3;
import com.libtop.weituR.activity.user.UserBooksFragment;
import com.libtop.weituR.base.BaseFragment;
import com.libtop.weituR.tool.Preference;
import com.libtop.weituR.utils.CheckUtil;
import com.libtop.weituR.utils.ContantsUtil;
import com.libtop.weituR.widget.gridview.FixedGridView;
import com.zbar.lib.CaptureActivity;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * Created by Administrator on 2016/1/8 0008.
 */
public class MainFragment extends BaseFragment {

    @Bind(R.id.title)
    TextView mLibText;
    @Bind(R.id.scroll)
    ScrollView mScroll;

    private MainIconAdapter mAdapter;
    @Bind(R.id.grid_view)
    FixedGridView mGrid;

    @Bind(R.id.banner)
    ImageView mImageView;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_layout2;
    }

    @Override
    public void onCreation(View root) {
        if (mAdapter == null) mAdapter = new MainIconAdapter(mContext);
        mGrid.setAdapter(mAdapter);
        mScroll.smoothScrollTo(0, 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        mLibText.setText(Preference.instance(mContext).getString(
                Preference.SchoolName));
    }

    @Nullable
    @OnClick({R.id.search, R.id.search_top, R.id.open_clazz, R.id.spec_lesson, R.id.news, R.id.service, R.id.banner})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open_clazz:
                Bundle bundle = new Bundle();
                bundle.putInt("from", 1);
                bundle.putString(ContentActivity.FRAG_CLS, LibraryFragment.class.getName());
                mContext.startActivity(bundle, CaptureActivity.class);
                // CaptureActivity ContentActivity
                break;
            case R.id.search:
            case R.id.search_top:
                mContext.startActivity(null, SearchActivity.class);
                break;
            case R.id.spec_lesson:
//                Bundle bundle1=new Bundle();
//                bundle1.putString(ContentActivity.FRAG_CLS, FeedBackFragment.class.getName());
//                mContext.startActivity(bundle1,ContentActivity.class);
                Bundle bundle5 = new Bundle();
                bundle5.putString(ContentActivity.FRAG_CLS, LessonTypeListFragment.class.getName());
                mContext.startActivity(bundle5, ContentActivity.class);
                break;
            case R.id.news:
                Bundle bundle2 = new Bundle();
                bundle2.putString(ContentActivity.FRAG_CLS, NoticeFragment.class.getName());
                mContext.startActivity(bundle2, ContentActivity.class);
                break;
            case R.id.service:
                Bundle bundle3 = new Bundle();
                bundle3.putString(ContentActivity.FRAG_CLS, ServiceListFragment.class.getName());
                mContext.startActivity(bundle3, ContentActivity.class);
                break;
            case R.id.banner:
//                Bundle bundle4=new Bundle();
//                bundle4.putString(ContentActivity.FRAG_CLS, ServiceListFragment.class.getName());
//                bundle4.putString("isfirst","2");
//                mContext.startActivity(bundle4,GudieWebActivity.class);
//                UemgShare a = new UemgShare(mContext);
//                a.setImage("http://www.umeng.com/images/pic/social/integrated_3.png").setText("123").share();
                break;
        }
    }

    @Nullable @OnItemClick(value = R.id.grid_view)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (view.getId()) {
            case R.drawable.grid_icon_classroom://班级
                Bundle bundle3 = new Bundle();
                bundle3.putString(ContentActivity.FRAG_CLS, ClassmateFragment.class.getName());
                mContext.startActivity(bundle3, ContentActivity.class);
                break;
            case R.drawable.grid_icon_lessons://课表
                Toast.makeText(getActivity(), ContantsUtil.IS_DEVELOPING, Toast.LENGTH_SHORT).show();
//                Bundle bundle5=new Bundle();
//                bundle5.putString(ContentActivity.FRAG_CLS, LessonTypeListFragment.class.getName());
//                mContext.startActivity(bundle5, ContentActivity.class);
                break;
            case R.drawable.grid_icon_lent://转借
                Bundle bundle2 = new Bundle();
                bundle2.putString(ContentActivity.FRAG_CLS, LentFragment.class.getName());
                mContext.startActivity(bundle2, ContentActivity.class);
                break;
            case R.drawable.grid_icon_comment://点评
                Toast.makeText(getActivity(), ContantsUtil.IS_DEVELOPING, Toast.LENGTH_SHORT).show();
                break;
            case R.drawable.grid_icon_order://预约
                Toast.makeText(getActivity(), ContantsUtil.IS_DEVELOPING, Toast.LENGTH_SHORT).show();
                String testPdfUrl = "http://n0.libtop.com/1/54f7f4fde4b04364756c45cc.pdf";
                Intent intent = new Intent();
                intent.putExtra("url", testPdfUrl);
                intent.setClass(mContext, PdfActivity3.class);
                mContext.startActivity(intent);
                mContext.overridePendingTransition(R.anim.zoomin,
                        R.anim.alpha_outto);
                break;
            case R.drawable.grid_icon_renew://续借
                Intent intent2 = new Intent();

                intent2.setClass(mContext, CommentActivity.class);
                mContext.startActivity(intent2);
                Toast.makeText(getActivity(), ContantsUtil.IS_DEVELOPING, Toast.LENGTH_SHORT).show();
                break;
            case R.drawable.grid_icon_collection://收藏
                Bundle bundle = new Bundle();
                bundle.putString("action", "gather");
                bundle.putString(ContentActivity.FRAG_CLS, UserBooksFragment.class.getName());
                mContext.startActivity(bundle, ContentActivity.class);
                break;
            case R.drawable.image_icon://上传
                if (CheckUtil.isNull(mPreference.getString(Preference.uid))) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString(ContentActivity.FRAG_CLS, LoginFragment.class.getName());
                    mContext.startActivity(bundle1, ContentActivity.class);
                } else {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString(ContentActivity.FRAG_CLS, UploadFragment.class.getName());
                    mContext.startActivity(bundle1, ContentActivity.class);
                }
                break;
        }
    }
}
