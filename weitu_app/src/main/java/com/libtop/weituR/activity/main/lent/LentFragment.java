package com.libtop.weituR.activity.main.lent;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weituR.base.BaseFragment;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/1/18 0018.
 */
public class LentFragment extends BaseFragment{
    @Bind(R.id.title)
    TextView mTitleText;
//    @Bind(id = R.id.edit_current)
//    EditText mCurEdit;
//    @Bind(id = R.id.edit_new)
//    EditText mNewEdit;
//    @Bind(id = R.id.edit_isbn)
//    EditText mISBNEdit;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_lent;
    }

    @Override
    public void onCreation(View root) {
        setTitle();
    }

    private void setTitle(){
        mTitleText.setText("转借");
    }

    @Nullable
    @OnClick({R.id.back_btn,R.id.commit})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_btn:
                onBackPressed();
                break;
            case R.id.commit:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        mContext.finish();
    }
}
