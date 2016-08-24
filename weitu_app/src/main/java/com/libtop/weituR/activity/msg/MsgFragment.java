package com.libtop.weituR.activity.msg;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.ContentActivity;
import com.libtop.weituR.activity.main.notice.NoticeFragment;
import com.libtop.weituR.activity.user.FeedBackFragment;
import com.libtop.weituR.base.BaseFragment;
import com.libtop.weituR.utils.ContantsUtil;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/1/21 0021.
 */
public class MsgFragment extends BaseFragment{
    @Bind(R.id.title)
    TextView mTitleText;
    @Bind(R.id.left_logo)
    ImageView mTitleLogo;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_msg;
    }

    @Override
    public void onCreation(View root) {
        setTitle();
    }

    private void setTitle(){
        mTitleText.setText("消息中心");
        mTitleLogo.setVisibility(View.INVISIBLE);
    }

    @Nullable
    @OnClick({R.id.notice,R.id.feed_back,R.id.recommend,R.id.helper})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.notice:
                Bundle bundle2=new Bundle();
                bundle2.putString(ContentActivity.FRAG_CLS, NoticeFragment.class.getName());
                mContext.startActivity(bundle2,ContentActivity.class);
                break;
            case R.id.recommend:
                Toast.makeText(getActivity(), ContantsUtil.IS_DEVELOPING, Toast.LENGTH_SHORT).show();
                break;
            case R.id.feed_back:
                Bundle bundle1=new Bundle();
                bundle1.putBoolean(ContentActivity.FRAG_ISBACK,false);
                bundle1.putBoolean(ContentActivity.FRAG_WITH_ANIM,true);
                bundle1.putString(ContentActivity.FRAG_CLS, FeedBackFragment.class.getName());
                mContext.startActivity(bundle1,ContentActivity.class);
                break;
            case R.id.helper:
                Toast.makeText(getActivity(), ContantsUtil.IS_DEVELOPING,Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
