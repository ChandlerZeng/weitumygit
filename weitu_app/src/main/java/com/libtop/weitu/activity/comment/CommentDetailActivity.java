package com.libtop.weitu.activity.comment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.widget.listview.ChangeListView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CommentDetailActivity extends BaseActivity {

    @Bind(R.id.back_btn)
    ImageView backBtn;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.title_container)
    LinearLayout titleContainer;
    @Bind(R.id.img_head)
    ImageView imgHead;
    @Bind(R.id.tv_user_name)
    TextView tvUserName;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.tv_commnet1)
    TextView tvCommnet1;
    @Bind(R.id.comment_layout1)
    RelativeLayout commentLayout1;
    @Bind(R.id.subject_file_image)
    ImageView subjectFileImage;
    @Bind(R.id.comment_file_title)
    TextView commentFileTitle;
    @Bind(R.id.comment_file_author)
    TextView commentFileAuthor;
    @Bind(R.id.subject_file_ll)
    LinearLayout subjectFileLl;
    @Bind(R.id.comment_detail_link_layout)
    LinearLayout commentDetailLinkLayout;
    @Bind(R.id.likeLayout)
    LinearLayout likeLayout;
    @Bind(R.id.comment_detail_grid_view)
    GridView commentDetailGridView;
    @Bind(R.id.list_reply)
    ChangeListView listReply;
    @Bind(R.id.comment_layout2)
    LinearLayout commentLayout2;
    @Bind(R.id.edit_comment)
    EditText editComment;
    @Bind(R.id.commit)
    Button commit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_detail);
        ButterKnife.bind(this);
        initView();
    }

    private void initView(){
        title.setText("评论详情");

    }

    @OnClick({R.id.back_btn, R.id.comment_detail_link_layout, R.id.commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                break;
            case R.id.comment_detail_link_layout:
                break;
            case R.id.commit:
                break;
        }
    }
}
