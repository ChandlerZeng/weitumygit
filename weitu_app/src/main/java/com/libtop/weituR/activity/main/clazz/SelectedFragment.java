package com.libtop.weituR.activity.main.clazz;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weituR.base.BaseFragment;

import butterknife.Bind;

/**
 * <p>
 * Title: SelectedFragment.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * CreateTime：16/6/12
 * </p>
 *
 * @author 陆
 * @version common v1.0
 */
public class SelectedFragment extends BaseFragment {
//    @Bind(R.id.back_btn)
//    XListView listView;
//    @Bind(R.id.back_btn)
//    ImageView mBackBtn;
    @Nullable
    @Bind(R.id.title)
    TextView mTitleText;

    private Bundle bm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_selected;
    }

    @Override
    public void onCreation(View root) {
        setTitle();
    }

    private void setTitle() {
        mTitleText.setText("上传");
    }

}
