package com.libtop.weitu.activity.search;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.search.adapter.CommentAdapter;
import com.libtop.weitu.activity.search.dto.CommentNeedDto;
import com.libtop.weitu.activity.search.dto.CommentResult;
import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.http.MapUtil;
import com.libtop.weitu.http.WeituNetwork;
import com.libtop.weitu.test.CommentBean;
import com.libtop.weitu.test.Comments;
import com.libtop.weitu.test.HttpRequestTest;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.JsonUtil;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class CommentActivity extends BaseActivity implements CommentAdapter.OnReplyClickListener
{

    @Bind(R.id.edit_comment)
    EditText editText;
    @Bind(R.id.list_comment)
    ListView listView;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.sub_title)
    TextView subTitle;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout refreshLayout;
    private CommentAdapter commentAdapter;

    private List<CommentResult> list;
    private List<Comments> list2;//TODO

    private CommentNeedDto commentNeedDto;

    private boolean isReply = false;
    private String cid;

    private CommentBean commentBean; //TODO
    private List<Comments> commentsList = new ArrayList<>(); //TODO


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setInjectContentView(R.layout.activity_comment);
        String json = getIntent().getStringExtra("CommentNeedDto");
        commentNeedDto = JsonUtil.fromJson(json, new TypeToken<CommentNeedDto>()
        {
        }.getType());

        if (!TextUtils.isEmpty(commentNeedDto.title))
        {
            subTitle.setText(commentNeedDto.title);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
//                    onBackPressed();
                }
            }
        });

        title.setText("评论");
        list2 = new ArrayList<Comments>();
//        getData();
        getFakeData();
        commentAdapter = new CommentAdapter(this, list2, this);
        listView.setAdapter(commentAdapter);
    }


    //    影音，书本，文档，图库的评论列表接口
    //    http://weitu.bookus.cn/comment/list.json?text={"uid":"565bea2c984ec06f56befda3","tid":"563c69b4984e338019914a66","page":1,"method":"comment.list"}
    private void getData()
    {
        showLoding();
        HashMap<String, Object> map = new HashMap<>();
        map.put("tid", commentNeedDto.tid);
        map.put("page", 1);
        map.put("method", "comment.list");
        map.put("uid", mPreference.getString(Preference.uid));
        String[] arrays = MapUtil.map2Parameter(map);
        subscription = WeituNetwork.getWeituApi().getComment(arrays[0], arrays[1], arrays[2]).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<List<CommentResult>>()
        {
            @Override
            public void onCompleted()
            {
                Log.w("guanglog", "completed");
            }


            @Override
            public void onError(Throwable e)
            {
                Log.w("guanglog", "error + " + e);
            }


            @Override
            public void onNext(List<CommentResult> commentResults)
            {
                dismissLoading();
                list.clear();
                list.addAll(commentResults);
                commentAdapter.setData(list2);
                commentAdapter.notifyDataSetChanged();
                editText.setText("");
                isReply=false;
            }
        });
    }


    @Nullable
    @OnClick({R.id.back_btn, R.id.commit})
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.back_btn:
                onBackPressed();
                break;
            case R.id.commit:
                upload(v);
                break;
        }
    }


    private void upload(View v)
    {
        String str = editText.getText().toString().trim();
        if (str == null || str.length() == 0)
        {
            Toast.makeText(CommentActivity.this, "评论不能为空!", Toast.LENGTH_SHORT).show();
            return;
        }
        hideKeyBoard(v);
        if (isReply)
        {
            putReply(str);
        }
        else
        {
            putComment(str);
        }
    }


    private void putReply(String content)
    {
        showLoding();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("tid", commentNeedDto.tid);
        params.put("cid", cid);
        params.put("uid", mPreference.getString(Preference.uid));
        params.put("type", commentNeedDto.type);
        params.put("content", content);
        params.put("method", "comment.save");
        HttpRequest.loadWithMap(params).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {

            }


            @Override
            public void onResponse(String json, int id)
            {
                if (!TextUtils.isEmpty(json))
                {
                    //   showToast("没有相关数据");
                    getData();
                    return;
                }
                else
                {
                    dismissLoading();
                }
            }
        });
    }


    private void putComment(String content)
    {
        //        http://weitu.bookus.cn/comment/save.json?text={"uid":"565bea2c984ec06f56befda3","tid":"563c69b4984e338019914a66","type":1,"content":"我觉得很有意思哦",method":"comment.save"}
        showLoding();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("tid", commentNeedDto.tid);
        params.put("uid", mPreference.getString(Preference.uid));
        params.put("type", commentNeedDto.type);
        params.put("content", content);
        params.put("method", "comment.save");
        HttpRequest.loadWithMap(params).execute(new StringCallback()
        {
            @Override
            public void onError(Call call, Exception e, int id)
            {

            }


            @Override
            public void onResponse(String json, int id)
            {
                if (!TextUtils.isEmpty(json))
                {
                    //   showToast("没有相关数据");
                    getData();
                    return;
                }
                else
                {
                    dismissLoading();
                }
            }
        });
    }


    private Handler updataHandler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 1:
                    commentAdapter.setData(list2);
                    commentAdapter.notifyDataSetChanged();
                    editText.setText("");
                    break;
                case 2:
                    getData();
                    break;
                default:
                    break;
            }

        }
    };


    @Override
    public void onReplyTouch(View v, int position)
    {
        CommentResult commentResult = list.get(position);
        isReply = true;
        if (commentResult.content != null && !TextUtils.isEmpty(commentResult.content))
        {
            cid = commentResult.id;
            editText.requestFocus();
            String first = "回复";
            SpannableStringBuilder spannableString = getGreenStrBuilder(first,commentResult.username);
            editText.setText(spannableString);
            editText.setSelection(spannableString.length());
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
        else
        {
            cid = commentResult.id;
            editText.requestFocus();
        }
    }
    private SpannableStringBuilder getGreenStrBuilder(String first, String append)
    {
        String builderAppend = append + " ";
        SpannableStringBuilder builder = new SpannableStringBuilder(first + builderAppend);

        ForegroundColorSpan greenSpan = new ForegroundColorSpan(Color.parseColor("#47885D"));
        builder.setSpan(greenSpan, first.length(), first.length()+builderAppend.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }
    private void getFakeData()
    {
        //  http://192.168.0.9/resource/comment/list
        Map<String, Object> map = new HashMap<>();
        String method = "resource/comment/list";
        map.put("method", method);
        HttpRequestTest.loadWithMapPublic(map).execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }


            @Override
            public void onResponse(String json, int id) {
                if (!TextUtils.isEmpty(json)) {
                    try {
                        Gson gson = new Gson();
                        CommentBean data = gson.fromJson(json, new TypeToken<CommentBean>() {
                        }.getType());
                        commentsList.clear();
                        if (data.comments!=null) {
                            commentsList.addAll(data.comments);
                        }
                        commentAdapter.setData(commentsList);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
