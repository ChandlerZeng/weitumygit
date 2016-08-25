package com.libtop.weituR.activity.main;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.ContentActivity;
import com.libtop.weituR.activity.classify.ClassifyFragment;
import com.libtop.weituR.activity.login.LoginFragment;
import com.libtop.weituR.activity.startup.StartupActivity;
import com.libtop.weituR.activity.user.UserCenterFragment;
import com.libtop.weituR.activity.user.UserCollect.UserCollectActivity;
import com.libtop.weituR.base.BaseActivity;
import com.libtop.weituR.base.FragmentFactory;
import com.libtop.weituR.http.HttpRequest;
import com.libtop.weituR.tool.Preference;
import com.libtop.weituR.utils.CheckUtil;
import com.libtop.weituR.utils.ContantsUtil;
import com.libtop.weituR.utils.PopupW.MoreWindow;
import com.libtop.weituR.widget.NoSlideViewPager;
import com.libtop.weituR.widget.dialog.AlertDialog;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;

/**
 * Created by Administrator on 2016/1/8 0008.
 */
public class MainActivity extends BaseActivity {
    //    @Bind(R.id.radio_group)
//    RadioGroup mRadios;
    private long mLastBackPress = 0;
    private AlertDialog mAlert;
    @Bind(R.id.mViewPager)
    NoSlideViewPager mViewPager;
//    @Bind(R.id.collect_status)
//    RadioButton mcollectStatus;
    private int indicatorWidth;
    //    @Bind(R.id.msg)
//    RelativeLayout mMsgBtn;
//    @Bind(R.id.msg_num)
//    TextView mMsgNumText;
    @Bind(R.id.tv_home)
    TextView home;
    @Bind(R.id.img_home)
    ImageView imgHome;
    @Bind(R.id.tv_clazz)
    TextView clazz;
    @Bind(R.id.img_clazz)
    ImageView imgClazz;
//    @Bind(R.id.collect_status)
//    TextView collectStatus;
    @Bind(R.id.tv_personal)
    TextView personal;
    @Bind(R.id.img_personal)
    ImageView imgPersonal;
    MoreWindow mMoreWindow;
    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    public int[] tabTitle = {R.string.tab_mian, R.string.tab_collect,R.string.tab_clazz , R.string.tab_personal}; // 标题
    public int[] tabImage = {R.drawable.selector_main_home, R.drawable.selector_main_clazz, R.drawable.selector_main_msg, R.drawable.selector_main_personal};
    TabFragmentPagerAdapter mAdapter;
    @Bind(R.id.weibo)
    ImageView button;

    private final int TOTAL_FRAGMENT = 3;

    private List<TextView> textList = new ArrayList<TextView>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("test draw time start", System.currentTimeMillis() + "");
        super.onCreate(savedInstanceState);
        setInjectContentView(R.layout.activity_main_2);
        if (StartupActivity.instance != null) {
            StartupActivity.instance.finish();
            StartupActivity.instance = null;
        }
//        setContentView(R.layout.activity_main_2);
//        UmengUpdateAgent.update(this);
//        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        JPushInterface.resumePush(getApplicationContext());
//        checkUpdate();
        initFragment();
        init();
        // initNavigationHSV();
        setListener();
//        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setPagingEnabled(false);
        mAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        // ((RadioButton) mRadios.getChildAt(0)).performClick();

//        setState(0, true);
        imgHome.setBackgroundResource(R.drawable.main_tag_checked_home);
        home.setTextColor(getResources().getColor(R.color.green2));

    }

    private void showMoreWindow(View view) {
        if (null == mMoreWindow) {
            mMoreWindow = new MoreWindow(this);
            mMoreWindow.init();
        }

        mMoreWindow.showMoreWindow(view, 100);
    }

    private void init() {
        WindowManager wm = this.getWindowManager();
        int width = wm.getDefaultDisplay().getWidth();

        indicatorWidth = width / 4;
    }

    public void initFragment() {
        MainFragment one = new MainFragment();
        ClassifyFragment two = new ClassifyFragment();
//        MsgFragment three = new MsgFragment();
        UserCenterFragment four = new UserCenterFragment();
        fragmentList.add(one);
        fragmentList.add(two);
//        fragmentList.add(three);
        fragmentList.add(four);
    }


    public void replaceFragment(String fragCls) {
        FragmentTransaction tran = mFm.beginTransaction();
        Fragment frag = mFm.findFragmentByTag(fragCls);
        boolean isAdd = true;
        if (frag == null) {
            frag = FragmentFactory.newFragment(fragCls);
            isAdd = false;
        }
        tran.replace(R.id.container, frag, fragCls);
        if (!isAdd) {
            tran.addToBackStack(fragCls);
        }
        tran.commitAllowingStateLoss();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        Log.e("test draw time end", System.currentTimeMillis() + "");
    }

    private void checkUpdate() {
        long pre = mPreference.getLong(Preference.UPDATE_TIME, 0);
        if (System.currentTimeMillis() - pre < 24 * 60 * 60 * 1000) {
            return;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("version", ContantsUtil.currentVesion);
        params.put("method", "issue.update");
        HttpRequest.loadWithMapSec(params, new HttpRequest.CallBackSec() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }

            @Override
            public void onResponse(String json, int id) {
                if ("".equals(json) || json == null) {
                    mPreference.putLong(Preference.UPDATE_TIME,
                            System.currentTimeMillis());
                } else {
                    try {
                        JSONObject data = new JSONObject(json);
                        String link = data.getString("link");
                        if (!"none".equals(link)) {
                            showUpdateDialog(data.getString("link"));
                            mPreference.putBoolean(
                                    Preference.UPDATE_STATE, true);
                            mPreference.putLong(Preference.UPDATE_TIME,
                                    System.currentTimeMillis());
                        }
                    } catch (Exception e) {
                    }
                }
            }
        });
    }


    private void showUpdateDialog(final String url) {
        mAlert = new AlertDialog(mContext, "有新版本，您确定要更新吗");
        mAlert.setCallBack(new AlertDialog.CallBack() {
            @Override
            public void cancel() {
                mPreference.putBoolean(Preference.UPDATE_STATE, false);
            }

            @Override
            public void callBack() {
                Uri uri = Uri.parse(url);
                Intent downloadIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(downloadIntent);
            }
        });
        mAlert.show();
    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        if (tempTime - mLastBackPress < 2000) {
            finishSimple();
        } else {
            Toast.makeText(mContext, "再按一次，退出应用", Toast.LENGTH_SHORT).show();
        }
        mLastBackPress = tempTime;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAlert != null) {
            mAlert.dismiss();
        }
    }


    @Nullable
    @OnClick({R.id.ll_home, R.id.ll_clazz, R.id.ll_collect_status, R.id.ll_personal,R.id.weibo})
    public void onClick(View v) {
        imgHome.setBackgroundResource(R.drawable.main_tag_unchecked_home);
        home.setTextColor(ContextCompat.getColor(mContext,R.color.grey1));
        imgClazz.setBackgroundResource(R.drawable.main_tag_unchecked_clazz);
        clazz.setTextColor(ContextCompat.getColor(mContext,R.color.grey1));
        imgPersonal.setBackgroundResource(R.drawable.main_tag_unchecked_personal);
        personal.setTextColor(ContextCompat.getColor(mContext,R.color.grey1));
        switch (v.getId()) {
            case R.id.ll_home:
                imgHome.setBackgroundResource(R.drawable.main_tag_checked_home);
                home.setTextColor(ContextCompat.getColor(mContext,R.color.green2));
                mViewPager.setCurrentItem(0);
//                setState(1, true);
                break;
            case R.id.ll_clazz:
                imgClazz.setBackgroundResource(R.drawable.main_tag_checked_clazz);
                clazz.setTextColor(ContextCompat.getColor(mContext,R.color.green2));
                mViewPager.setCurrentItem(1);
                break;
            case R.id.ll_collect_status:
//                setState(3, true);
                if (CheckUtil.isNull(mPreference.getString(Preference.uid))) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putString(ContentActivity.FRAG_CLS, LoginFragment.class.getName());
                    mContext.startActivity(bundle1, ContentActivity.class);
                } else {
                    openCollect();
                }
                break;
            case R.id.ll_personal:
                imgPersonal.setBackgroundResource(R.drawable.main_tag_checked_personal);
                personal.setTextColor(ContextCompat.getColor(mContext,R.color.green2));
                mViewPager.setCurrentItem(2);
                break;
            case R.id.weibo:
                showMoreWindow(v);
                break;
        }
    }

    private void openCollect(){
        Intent intent = new Intent(mContext, UserCollectActivity.class);
        startActivity(intent);
    }

    public class TabFragmentPagerAdapter extends FragmentPagerAdapter {

        public TabFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0) {
            Fragment ft = null;
            switch (arg0) {
                case 0:
                    ft = fragmentList.get(0);
                    break;
                case 1:
                    ft = fragmentList.get(1);
                    break;
                case 2:
                    ft = fragmentList.get(2);
                    break;
                case 3:
//                    ft = fragmentList.get(3);
                    break;
                default:
                    ft = fragmentList.get(0);
                    break;
            }
            return ft;
        }

        @Override
        public int getCount() {

            return TOTAL_FRAGMENT;
        }

    }

    // 互相监听
    private void setListener() {

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
//                setState(position+1, false);
//                if (mRadios != null
//                        && mRadios.getChildCount() > position) {
//                    ((RadioButton) mRadios.getChildAt(position))
//                            .performClick();
//                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

//        mRadios
//                .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//
//                    @Override
//                    public void onCheckedChanged(RadioGroup group, int checkedId) {
//
//
//                        mViewPager.setCurrentItem(checkedId); // ViewPager
//                        // 跟随一起 切换
//
//                    }
//                });

    }

    private void setState(int position, boolean isViewPage) {
        Drawable h = getResources().getDrawable(R.drawable.main_tag_unchecked_home);
        Drawable h2 = getResources().getDrawable(R.drawable.main_tag_checked_home);
        h.setBounds(0, 0, h.getMinimumWidth(), h.getMinimumHeight());
        h2.setBounds(0, 0, h.getMinimumWidth(), h.getMinimumHeight());
        Drawable c = getResources().getDrawable(R.drawable.main_tag_unchecked_clazz);
        Drawable c2 = getResources().getDrawable(R.drawable.main_tag_checked_clazz);
        c.setBounds(0, 0, h.getMinimumWidth(), h.getMinimumHeight());
        c2.setBounds(0, 0, h.getMinimumWidth(), h.getMinimumHeight());
        Drawable m = getResources().getDrawable(R.drawable.main_tag_unchecked_collect);
        Drawable m2 = getResources().getDrawable(R.drawable.main_tag_checked_collect);
        m.setBounds(0, 0, h.getMinimumWidth(), h.getMinimumHeight());
        m2.setBounds(0, 0, h.getMinimumWidth(), h.getMinimumHeight());
        Drawable p = getResources().getDrawable(R.drawable.main_tag_unchecked_personal);
        Drawable p2 = getResources().getDrawable(R.drawable.main_tag_checked_personal);
        p.setBounds(0, 0, h.getMinimumWidth(), h.getMinimumHeight());
        p2.setBounds(0, 0, h.getMinimumWidth(), h.getMinimumHeight());
        home.setCompoundDrawables(null, h, null, null);
        clazz.setCompoundDrawables(null, c, null, null);
//        collectStatus.setCompoundDrawables(null, m, null, null);
        personal.setCompoundDrawables(null, p, null, null);
        home.setTextColor(ContextCompat.getColor(mContext,R.color.grey1));
        clazz.setTextColor(ContextCompat.getColor(mContext,R.color.grey1));
//        collectStatus.setTextColor(ContextCompat.getColor(mContext,R.color.grey1));
        personal.setTextColor(ContextCompat.getColor(mContext,R.color.grey1));

        if (position>1){
            position = position+1;
        }

        switch (position) {
            case 1:
                home.setCompoundDrawables(null, h2, null, null);
                home.setTextColor(ContextCompat.getColor(mContext,R.color.green2));
                break;
            case 2:
//                collectStatus.setCompoundDrawables(null, m2, null, null);
//                collectStatus.setTextColor(ContextCompat.getColor(mContext,R.color.green2));
                break;
            case 3:
                clazz.setCompoundDrawables(null, c2, null, null);
                clazz.setTextColor(ContextCompat.getColor(mContext,R.color.green2));
                break;
            case 4:
                personal.setCompoundDrawables(null, p2, null, null);
                personal.setTextColor(ContextCompat.getColor(mContext,R.color.green2));
                break;
        }
        if (position>1){
            position = position-1;
        }
        if (isViewPage)
            mViewPager.setCurrentItem(position - 1);
    }

}
