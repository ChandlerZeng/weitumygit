package com.libtop.weituR.activity.source;

import android.os.Bundle;
import android.widget.TextView;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.libtop.weitu.R;
import com.libtop.weituR.base.BaseActivity;
import com.libtop.weituR.fileloader.FileLoader;

import java.io.File;

import butterknife.Bind;

//import com.joanzapata.pdfview.PDFView;
//import com.joanzapata.pdfview.listener.OnPageChangeListener;

public class PdfActivity extends BaseActivity implements OnPageChangeListener

{

	@Bind(R.id.pdfView)
	PDFView pdfView;
	@Bind(R.id.page)
	TextView title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setInjectContentView(R.layout.activity_pdf_layout);
//		setContentView(R.layout.activity_pdf_layout);
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
				if (file != null&&file.exists()&&file.length()>0) {
					pdfView.fromFile(file).defaultPage(0)
                          .onPageChange(PdfActivity.this).load();

				}else {
					setResult(0x5555);
					finish();
					overridePendingTransition(R.anim.alpha_into, R.anim.zoomout);
				}
			}
		});
		//pdfView.fromAsset(ABOUT_FILE).defaultPage(0).onPageChange(this).load();
	}

	@Override
	public void onPageChanged(int page, int pageCount) {
		title.setText(page + "/" + pageCount);
	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.alpha_into, R.anim.zoomout);
	}
}
