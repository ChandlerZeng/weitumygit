//package com.libtop.weituR.activity.classify;
//
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//
//import com.libtop.weitu.R;
//import com.libtop.weituR.activity.ContentActivity;
//import com.libtop.weituR.activity.classify.adapter.KeysListAdapter;
//import com.libtop.weituR.activity.classify.adapter.TagsAdapter;
//import com.libtop.weituR.activity.classify.adapter.TagsAdapter2;
//import com.libtop.weituR.activity.classify.bean.KeyBean;
//import com.libtop.weituR.activity.classify.bean.TabBean;
//import com.libtop.weituR.activity.search.SearchActivity;
//import com.libtop.weituR.base.BaseFragment;
//import com.libtop.weituR.http.old_del.HttpServiceUtil;
//import com.libtop.weituR.tool.Preference;
//import com.libtop.weituR.utils.CollectionUtils;
//import com.libtop.weituR.utils.ContantsUtil;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.xutils.view.annotation.ContentView;
//import org.xutils.view.annotation.Event;
//import org.xutils.view.annotation.ViewInject;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by Administrator on 2016/1/13.
// */
//setInjectContentView(R.layout.fragment_classify)
//public class ClassifyFragment extends BaseFragment implements KeysListAdapter.OnKeySelectedListener {
//    @Bind(R.id.list_tags)
//    ListView mTagListView;
//    @Bind(R.id.list_keys)
//    ListView mKeyListView;
//    @Bind(R.id.container)
//    LinearLayout mContainer;
//
//    private TagsAdapter mTagAdapter;
//    private KeysListAdapter mKeysAdapter;
//
//    private TagsAdapter2 mTagAdapter2;
//
//    private List<TabBean> mTags = new ArrayList<TabBean>();
//    private List<KeyBean> mKeys = new ArrayList<KeyBean>();
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mTagAdapter2 = new TagsAdapter2(mContext, mTags);
//        // mTagAdapter=new TagsAdapter(mContext,mTags);
//        mKeysAdapter = new KeysListAdapter(mContext, mKeys, this);
//    }
//
//    public int count = 0;
//
//    @Override
//    public void onCreation(View root) {
//        mTagListView.setAdapter(mTagAdapter2);
//        mKeyListView.setAdapter(mKeysAdapter);
////        fillTestedTags();
//        if (mTags.isEmpty() || mKeys.isEmpty()) {
//            requestTags();
//        }
//    }
//
////    //测试用
////    private void fillTestedTags(){
////        mTags.clear();
////        mTags.add("abc");
////        mTags.add("bcd");
////        mTags.add("yiu");
////        mTags.add("uio");
////        mTags.add("klj");
////        mTagAdapter.update(mTags);
////    }
//
////    //测试用
////    private void fillTestedKeys(){
////        List<String> keys=new ArrayList<String>();
////        keys.add("actions");
////        keys.add("black");
////        keys.add("ops");
////        keys.add("upload");
////        keys.add("kick");
////        KeyBean bean1=new KeyBean();
////        bean1.setTitle("title1");
////        bean1.setKeys(keys);
////        KeyBean bean2=new KeyBean();
////        bean2.setTitle("title2");
////        bean2.setKeys(keys);
////        KeyBean bean3=new KeyBean();
////        bean3.setTitle("title3");
////        bean3.setKeys(keys);
////        mKeys.clear();
////        mKeys.add(bean1);
////        mKeys.add(bean2);
////        mKeys.add(bean3);
////        mKeysAdapter.updata(mKeys);
////    }
//
//    @Nullable @OnClick(value = R.id.list_tags, type = AdapterView.OnItemClickListener.class)
//    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        //mTagAdapter.selectItem(position);
//        mTagAdapter2.setData(position);
//        //请求关键字
////        fillTestedKeys();
//        requestKeys(mTags.get(position).id);
//    }
//
//
//    @Override
//    public void onSelectKey(String key, int parentIndex, int childIndex) {
//        mPreference.putString(Preference.KEYWORD_SEARCH, key);
//        String[] keys = null;
//        int[] codes = null;
//        List<KeyBean.Child> chs = mKeys.get(parentIndex).subCategories;
//        if (!CollectionUtils.isEmpty(chs)) {
//            keys = new String[chs.size()];
//            codes = new int[chs.size()];
//            for (int i = 0; i < chs.size(); i++) {
//                keys[i] = chs.get(i).name;
//                codes[i] = chs.get(i).code;
//            }
//        }
//        Bundle bd = new Bundle();
////        bd.putBoolean(ContentActivity.FRAG_ISBACK,true);
////        bd.putBoolean(ContentActivity.FRAG_WITH_ANIM,true);
//        bd.putString(ContentActivity.FRAG_CLS, ClassifyInfosFragment.class.getName());
//        bd.putStringArray("keys", keys);
//        bd.putIntArray("codes", codes);
//        mContext.startActivity(bd, ContentActivity.class);
//    }
//
//    private void requestTags() {
//        if (mLoading!=null && !mLoading.isShowing()) mLoading.show();
//        HttpServiceUtil.request(ContantsUtil.CLASSIFY_TAG_PARENT, "get", null, new HttpServiceUtil.CallBack() {
//            @Override
//            public void callback(String json) {
////                mLoading.dismiss();
//                if (TextUtils.isEmpty(json)) {
////                    showToast("没有相关数据");
////                    mNullText.setText("未搜索到相关记录");
////                    mNullText.setVisibility(View.VISIBLE);
//                    return;
//                }
//                try {
//                    mTags.clear();
//                    JSONArray array = new JSONArray(json);
//                    for (int i = 0; i < array.length(); i++) {
//                        TabBean bean = new TabBean();
//                        bean.form(array.getJSONObject(i));
//                        mTags.add(bean);
//                    }
//                    // mTagAdapter.update(mTags);
//                    mTagAdapter2.setData(mTags);
////                    mTagAdapter2.notifyDataSetChanged();
//
//
//                    if (mTags.size() == 0) {
//                        showToast("没有相关数据");
////                        mNullText.setText("未搜索到相关记录");
////                        mNullText.setVisibility(View.VISIBLE);
//                        return;
//                    }
//                    //mTagAdapter.selectItem(0);
//                    requestKeys(mTags.get(0).id);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });
//    }
//
//    private void requestKeys(String id) {
//        if (mLoading!=null&&!mLoading.isShowing()) mLoading.show();
//        Map<String, Object> params = new HashMap<String, Object>();
//        params.put("id", id);
//        HttpServiceUtil.request(ContantsUtil.CLASSIFY_KEY_CHILD, "get", params, new HttpServiceUtil.CallBack() {
//            @Override
//            public void callback(String json) {
//                if (mLoading.isShowing()){
//                    mLoading.dismiss();
//                }
//                if (TextUtils.isEmpty(json)) {
//                    showToast("没有相关数据");
////                    mNullText.setText("未搜索到相关记录");
////                    mNullText.setVisibility(View.VISIBLE);
//                    return;
//                }
//                try {
//                    mKeys.clear();
//                    JSONArray array = new JSONArray(json);
//                    for (int i = 0; i < array.length(); i++) {
//                        KeyBean bean = new KeyBean();
//                        bean.form(array.getJSONObject(i));
//                        mKeys.add(bean);
//                    }
//                    mKeysAdapter.update(mKeys);
//                    if (mTags.size() == 0) {
//                        showToast("没有相关数据");
////                        mNullText.setText("未搜索到相关记录");
////                        mNullText.setVisibility(View.VISIBLE);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });
//    }
//
//    @Nullable @OnClick(R.id.container)
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.container:
//                mContext.startActivity(null, SearchActivity.class);
//                break;
//        }
//    }
//}
