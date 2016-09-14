package com.libtop.weitu.activity.comment;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.libtop.weitu.R;
import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.test.CommentBean;
import com.libtop.weitu.utils.ContantsUtil;
import com.libtop.weitu.utils.DateUtil;
import com.libtop.weitu.utils.selector.MultiImageSelectorFragment;
import com.libtop.weitu.widget.listview.ChangeListView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

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

    private String cid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_detail);
        ButterKnife.bind(this);
        initView();
    }

    private void initView(){
        title.setText("评论详情");
        Bundle bundle = getIntent().getExtras();
        cid = bundle.getString("cid");
        getData(String.valueOf(cid));

    }

    @OnClick({R.id.back_btn, R.id.comment_detail_link_layout, R.id.commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                mContext.finish();
                break;
            case R.id.comment_detail_link_layout:
                break;
            case R.id.commit:
                break;
        }
    }

    private void getData(String cid)
    {
//        http://115.28.189.104/resource/comment/info?cid=4
        showLoding();
        Map<String, Object> map = new HashMap<>();
        String method = "/resource/comment/info";
        String api = "/resource/comment/info";
        map.put("cid", cid);
        HttpRequest.newLoad(ContantsUtil.API_FAKE_HOST_PUBLIC + method, map).execute(new StringCallback() {
//        OkHttpUtils.get().url(ContantsUtil.API_FAKE_HOST_PUBLIC + api)
//                .addParams("cid", cid)
//                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }


            @Override
            public void onResponse(String json, int id) {
                if (!TextUtils.isEmpty(json)) {
                    dismissLoading();
                    try {
                        Gson gson = new Gson();
                        CommentBean data = gson.fromJson(json, new TypeToken<CommentBean>() {
                        }.getType());
                        if (data.comment.content != null) {
                            tvCommnet1.setText(data.comment.content);
                        }
                        if (data.comment.user.name != null) {
                            tvUserName.setText(data.comment.user.name);
                        }
                        if (data.comment.t_create != 0) {
                            tvTime.setText(DateUtil.parseToStringWithoutSS(data.comment.t_create));
                        }
                        if (data.comment.user.logo != null) {
                            Picasso.with(mContext).load(data.comment.user.logo).transform(new CircleTransform()).error(R.drawable.head_image).placeholder(R.drawable.head_image).fit().centerCrop().into(imgHead);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    private class CircleTransform implements Transformation {
        @Override
        public Bitmap transform(Bitmap source) {
            int size = Math.min(source.getWidth(), source.getHeight());

            int x = (source.getWidth() - size) / 2;
            int y = (source.getHeight() - size) / 2;

            Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
            if (squaredBitmap != source) {
                source.recycle();
            }

            Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
            paint.setAntiAlias(true);

            float r = size / 2f;
            canvas.drawCircle(r, r, r, paint);

            squaredBitmap.recycle();
            return bitmap;
        }

        @Override
        public String key() {
            return "circle";
        }
    }
}
