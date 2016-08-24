package com.libtop.weituR.activity.search;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.libtop.weitu.R;
import com.libtop.weituR.activity.search.dto.BookDetailDto;
import com.libtop.weituR.activity.search.dto.DetailDto;
import com.libtop.weituR.activity.source.PdfTryReadActivity;
import com.libtop.weituR.base.impl.NotifyFragment;
import com.libtop.weituR.utils.ContantsUtil;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by LianTu on 2016-8-6.
 */
public class TryReadFragment extends NotifyFragment {

    @Bind(R.id.img_book)
    ImageView imgBook;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.author)
    TextView author;
    @Bind(R.id.publisher)
    TextView publisher;

    private BookDetailDto bookDetailDto;
    private DetailDto data;

    public static TryReadFragment Instance() {
        TryReadFragment fragment = new TryReadFragment();
        return fragment;
    }

    public void loadInfo(BookDetailDto temps) {
        bookDetailDto = temps;
       data = temps.book;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_book_tryread;
    }

    @Override
    public void onCreation(View root) {
        initView();
    }

    private void initView() {
        title.setText(data.title);
        author.setText(data.author);
        publisher.setText(data.publisher);
        if (!TextUtils.isEmpty(data.cover)){
            String imgPath = ContantsUtil.IMG_BASE+data.cover;
            Picasso.with(mContext).load(imgPath).fit().centerInside().into(imgBook);
        }
    }

    @Override
    public void notify(String data) {

    }

    @OnClick({R.id.ll_board,R.id.btn_read})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_read:
//                break;
            case R.id.ll_board:
                openDoc();
                break;

    }

}

    private void openDoc() {
        Intent intent = new Intent();
        if (bookDetailDto == null){
            showToast("该图书没有试读内容");
            return;
        }
        if (!TextUtils.isEmpty(data.downloadUrl))
            intent.putExtra("url", data.downloadUrl);
        intent.putExtra("doc_id", data.id);
        intent.putExtra("BookDetailDto",new Gson().toJson(bookDetailDto));
        intent.setClass(mContext, PdfTryReadActivity.class);
        mContext.startActivity(intent);
        mContext.overridePendingTransition(R.anim.zoomin,
                R.anim.alpha_outto);
    }
    }
