package com.libtop.weitu.activity.source;

import android.os.Bundle;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.base.BaseActivity;
import com.libtop.weitu.fileloader.FileLoader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import butterknife.Bind;

public class TxtActivity extends BaseActivity {

	@Bind(R.id.webview)
	TextView textView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setInjectContentView(R.layout.activity_txt_layout);
//		setContentView(R.layout.activity_txt_layout);
		initActivity();
	}

	private void initActivity() {
		Bundle bundle = getIntent().getExtras();
		String path = bundle.getString("url");
		showLoding();
		FileLoader.getInstance(mContext).loadCallBack(path, new FileLoader.CallBack() {
			@Override
			public void callBack(File file) {
			    dismissLoading();
				if (file != null) {
					try {
						String code = codeString(file);
						String txt = readFile(file, code);
						textView.setText(txt);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

	}

	/**
	 * 判断文件的编码格式
	 * 
	 * @param fileName
	 *            :file
	 * @return 文件编码格式
	 * @throws Exception
	 */
	public static String codeString(File fileName) throws Exception {
		BufferedInputStream bin = new BufferedInputStream(new FileInputStream(
				fileName));
		int p = (bin.read() << 8) + bin.read();
		String code = null;

		switch (p) {
		case 0xefbb:
			code = "UTF-8";
			break;
		case 0xfffe:
			code = "Unicode";
			break;
		case 0xfeff:
			code = "UTF-16BE";
			break;
		default:
			code = "GBK";
		}
		bin.close();
		return code;
	}

	private String readFile(File file, String code) throws Exception {
		StringBuffer sBuffer = new StringBuffer();
		FileInputStream fInputStream = new FileInputStream(file);
		InputStreamReader inputStreamReader = new InputStreamReader(
				fInputStream, code);
		BufferedReader in = new BufferedReader(inputStreamReader);
		String strTmp = "";
		while ((strTmp = in.readLine()) != null) {
			sBuffer.append(strTmp + "\n");
		}
		in.close();
		return sBuffer.toString();
	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.alpha_into, R.anim.zoomout);
	}
}
