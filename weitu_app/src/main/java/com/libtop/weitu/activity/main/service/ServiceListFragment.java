package com.libtop.weitu.activity.main.service;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.search.SearchActivity;
import com.libtop.weitu.activity.user.FeedBackFragment;
import com.libtop.weitu.base.BaseAdapter;
import com.libtop.weitu.base.BaseFragment;
import com.libtop.weitu.utils.ContantsUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * Created by Administrator on 2016/1/18 0018.
 */
public class ServiceListFragment extends BaseFragment{

    @Bind(R.id.title)
    TextView mTitleText;
    @Bind(R.id.list)
    ListView mList;

    private ServicesAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<String> dat=new ArrayList<String>();
        String []tags=this.mContext.getResources().getStringArray(R.array.service_list);
            for (String tag:tags) {
                dat.add(tag);
            }
        mAdapter=new ServicesAdapter(mContext,dat);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_service_list;
    }

    private void setTitle(){
        mTitleText.setText("读者服务");
    }

    @Override
    public void onCreation(View root) {
        setTitle();
        mList.setAdapter(mAdapter);
    }

    @Nullable
    @OnItemClick(value = R.id.list)
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0:
                mContext.startActivity(null, SearchActivity.class);
                break;
            case 1:
                Bundle bundle1=new Bundle();
                bundle1.putBoolean(ContentActivity.FRAG_ISBACK, true);
                bundle1.putBoolean(ContentActivity.FRAG_WITH_ANIM, true);
                bundle1.putString(ContentActivity.FRAG_CLS, FeedBackFragment.class.getName());
                mContext.startActivity(bundle1,ContentActivity.class);
                break;
            default:
                Toast.makeText(getActivity(), ContantsUtil.IS_DEVELOPING, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Nullable @OnClick(R.id.back_btn)
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_btn:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        mContext.finish();
    }

    private class ServicesAdapter extends BaseAdapter<String>{
//        private int[] mIcons={R.drawable.service_icon_search,R.drawable.service_icon_msg};
        //屏蔽未显示
        private int[] mIcons={R.drawable.service_icon_search,R.drawable.service_icon_msg
                ,R.drawable.service_icon_news,R.drawable.service_icon_cloud
                ,R.drawable.service_icon_lift,R.drawable.service_icon_speek
                ,R.drawable.service_icon_learning};
        public ServicesAdapter(Context context,List<String> data) {
            super(context, data,R.layout.item_list_simple);
//            String []tags=this.mContext.getResources().getStringArray(R.array.service_list);
//            for (String tag:tags) {
//                mData.add(tag);
//            }
        }

        @Override
        protected void newView(View convertView) {

        }

        @Override
        protected void holderView(View convertView, String s, int position) {
            TextView tv=(TextView)convertView.findViewById(R.id.title);
            ImageView im=(ImageView)convertView.findViewById(R.id.icon);
            im.setImageResource(mIcons[position]);
            tv.setText(s);
        }

    }
}
