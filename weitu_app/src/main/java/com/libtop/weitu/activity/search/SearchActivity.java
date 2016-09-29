package com.libtop.weitu.activity.search;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.RootStub;
import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.base.impl.NotifyFragment;
import com.libtop.weitu.dao.SearchBo;
import com.libtop.weitu.utils.Preference;
import com.libtop.weitu.utils.CheckUtil;
import com.libtop.weitu.utils.ContantsUtil;
import com.libtop.weitu.utils.JsonParser;
import com.umeng.analytics.MobclickAgent;

import butterknife.Bind;
import butterknife.OnClick;


public class SearchActivity extends BaseActivity implements RootStub
{

    public static boolean inSearch = false;

    @Bind(R.id.edit)
    EditText mNameEdit;
    @Bind(R.id.speak)
    ImageView mSpeakBtn;
    @Bind(R.id.delete)
    ImageView mDeleteBtn;

    private ArrayAdapter<CharSequence> adapter = null;
    // 讯飞
    private SpeechRecognizer mIat;
    private RecognizerDialog mIatDialog;
    private NotifyFragment mFragment;
    private SearchBo mBo;
    private String mCurentTag = "";
    private InputMethodManager mImm;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setInjectContentView(R.layout.fragment_search_layout);
        MobclickAgent.onEvent(mContext, "_search");

        mBo = new SearchBo(mContext);
        mImm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        mNameEdit.requestFocus();
        mNameEdit.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if (actionId == EditorInfo.IME_ACTION_SEARCH)
                {
                    String text = v.getText().toString();
                    if (!CheckUtil.isNull(text))
                    {
                        search(text);
                    }
                    return true;
                }
                return false;
            }
        });
        init();
    }


    protected void onStart()
    {
        super.onStart();
        if (inSearch)
        {
            boolean isAdd = false;
            String tag = "history";
            mFragment = (NotifyFragment) mFm.findFragmentByTag(tag);
            if (mFragment == null)
            {
                mFragment = new SearchPreFragment();
                isAdd = false;
            }
            replaceFragment(tag, mFragment, isAdd);
            inSearch = false;
        }
    }


    private void init()
    {
        mNameEdit.addTextChangedListener(new TextWatcher()
        {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
            {
            }


            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
            {
            }


            @Override
            public void afterTextChanged(Editable edit)
            {
                if (CheckUtil.isNull(edit))
                {
                    mDeleteBtn.setVisibility(View.GONE);
                    mSpeakBtn.setVisibility(View.VISIBLE);
                    String tag = "history";
                    mFragment = (NotifyFragment) mFm.findFragmentByTag(tag);
                    replaceFragment(tag, mFragment, true);
                }
                else
                {
                    mDeleteBtn.setVisibility(View.VISIBLE);
                    mSpeakBtn.setVisibility(View.GONE);
                }
            }
        });
        mNameEdit.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                boolean isAdd = false;
                String tag = "history";
                mFragment = (NotifyFragment) mFm.findFragmentByTag(tag);
                if (mFragment == null)
                {
                    mFragment = new SearchPreFragment();
                    isAdd = false;
                }
                replaceFragment(tag, mFragment, isAdd);
            }
        });
        replaceFragment("history", new SearchPreFragment(), false);
    }


    private void startRecorder()
    {
        if (mIat == null)
        {
            mIat = SpeechRecognizer.createRecognizer(mContext, new InitListener()
            {
                @Override
                public void onInit(int code)
                {
                    if (code != ErrorCode.SUCCESS)
                    {
                        showTip("初始化失败,错误码：" + code);
                    }
                }
            });
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
            // 设置语音前端点
            mIat.setParameter(SpeechConstant.VAD_BOS, "4000");
            // 设置语音后端点
            mIat.setParameter(SpeechConstant.VAD_EOS, "1000");
            // 设置标点符号
            mIat.setParameter(SpeechConstant.ASR_PTT, "0");
            // 设置音频保存路径
            mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, "/sdcard/toplib/weitu/wavaudio.pcm");
        }
        mIatDialog = new RecognizerDialog(mContext, mInitListener);
        mIatDialog.setListener(recognizerDialogListener);
        mIatDialog.show();
    }


    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener()
    {

        @Override
        public void onInit(int code)
        {
            if (code != ErrorCode.SUCCESS)
            {
                showTip("初始化失败,错误码：" + code);
            }
        }
    };

    /**
     * 听写UI监听器
     */
    private RecognizerDialogListener recognizerDialogListener = new RecognizerDialogListener()
    {
        public void onResult(RecognizerResult results, boolean isLast)
        {
            String text = JsonParser.parseIatResult(results.getResultString());
            if (!CheckUtil.isNull(text))
            {
                search(text);
            }
        }


        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error)
        {
            showTip(error.getPlainDescription(true));
        }

    };


    @Nullable
    @OnClick({R.id.back_btn, R.id.delete, R.id.speak, R.id.search})
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.back_btn:
                finish();
                break;
            case R.id.delete:
                mNameEdit.setText("");
                break;
            case R.id.speak:
                startRecorder();
                break;
            case R.id.search:
                search(mNameEdit.getText() + "");
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(mNameEdit.getApplicationWindowToken(), 0);
                break;
        }
    }


    public void search(String keyTxt)
    {
        mNameEdit.setText(keyTxt);
        mNameEdit.setSelection(keyTxt.length());
        mImm.hideSoftInputFromWindow(mContext.getCurrentFocus().getWindowToken(), 0);
        if (CheckUtil.isNull(keyTxt))
        {
            Toast.makeText(mContext, "请输入一个关键字", Toast.LENGTH_SHORT).show();
            return;
        }
        mPreference.putString(Preference.KEYWORD_SEARCH, keyTxt);
        ContantsUtil.UPDATE_SEARCH = false;
        mBo.sageUpdate(keyTxt);
        String tag;
        boolean isAdd = false;
        if (!"search".equals(mCurentTag))
        {
            tag = "search";
            mFragment = (NotifyFragment) getSupportFragmentManager().findFragmentByTag(tag);
            if (mFragment == null)
            {
                mFragment = new ResultFragment();
                isAdd = false;
            }
            else
            {
                ResultFragment temp = (ResultFragment) mFragment;
                temp.setCreate();
                temp.notify("");
            }
            replaceFragment(tag, mFragment, isAdd);
        }
        else
        {
            mFragment.notify("");
        }
    }


    public void replaceFragment(String tag, NotifyFragment tempFragment, boolean isAdd)
    {
        mCurentTag = tag;
        FragmentTransaction tran = mFm.beginTransaction();
        tran.replace(R.id.content_fragment, tempFragment, tag);
        if (!isAdd)
        {
            tran.addToBackStack(tag);
        }
        tran.commitAllowingStateLoss();
    }


    @Override
    public void onBackPressed()
    {
        if (!(mFragment instanceof ResultFragment))
        {
            finish();
        }
        else
        {
            boolean isAdd = false;
            String tag = "history";
            mFragment = (NotifyFragment) getSupportFragmentManager().findFragmentByTag(tag);
            if (mFragment == null)
            {
                mFragment = new SearchPreFragment();
                isAdd = false;
            }
            replaceFragment(tag, mFragment, isAdd);
        }
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if (mIat != null)
        {
            mIat.cancel();
            mIat.destroy();
            mIat = null;
        }
        if (mIatDialog != null)
        {
            mIatDialog.dismiss();
            mIatDialog = null;
        }
        if (mFragment != null)
        {
            mFragment = null;
        }
    }


    public void showTip(final String str)
    {
        mContext.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
            }
        });
    }


    // 点击空白区域 自动隐藏软键盘
    public boolean onTouchEvent(MotionEvent event)
    {
        if (null != this.getCurrentFocus())
        {
            /**
             * 点击空白位置 隐藏软键盘
             */
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(mContext.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }


    @Override
    public String getKey()
    {
        return mNameEdit.getText() + "";
    }

}
