package com.libtop.weituR.activity.source;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.libtop.weitu.R;
import com.libtop.weituR.base.BaseActivity;
import com.libtop.weituR.fileloader.FileLoader;

import java.io.File;

import butterknife.Bind;
import butterknife.OnClick;

//import com.joanzapata.pdfview.PDFView;
//import com.joanzapata.pdfview.listener.OnPageChangeListener;

public class PdfActivity2 extends BaseActivity implements OnPageChangeListener

{

	@Bind(R.id.pdfView)
	PDFView pdfView;
	@Bind(R.id.page)
	TextView title;

	@Bind(R.id.title_container)
	LinearLayout titleContainer;
	@Bind(R.id.rl_pdf_bottom)
	RelativeLayout rlBottom;
	@Bind(R.id.page_seekbar)
	SeekBar pageSeekBar;

	private boolean showFlag = true;
	private boolean isPageChange = false;
	private boolean rotateFlag = false;
	private int currentPage = 1;
	private int pageCount = 1;
	private int lastPage;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setInjectContentView(R.layout.activity_pdf_layout2);
//		setContentView(R.layout.activity_pdf_layout);
		initActivity();

		pdfView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//没有换页的点击
				if (showFlag && !isPageChange&&!pdfView.isZooming()){
					pageSeekBar.setProgress(currentPage-1);
					titleContainer.setVisibility(View.VISIBLE);
					rlBottom.setVisibility(View.VISIBLE);
					showFlag = false;
				}else {
					titleContainer.setVisibility(View.GONE);
					rlBottom.setVisibility(View.GONE);
					showFlag = true;
				}
			}
		});

		pageSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				pdfView.jumpTo(progress+1);
				int allPage = pageSeekBar.getMax() + 1;
				title.setText((progress+1) + "/" + allPage);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {

			}
		});

//		pdfView.setOnTouchListener(new View.OnTouchListener() {
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				titleContainer.setVisibility(View.GONE);
//				rlBottom.setVisibility(View.GONE);
//				return true;
//			}
//		});

		pdfView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				titleContainer.setVisibility(View.GONE);
				rlBottom.setVisibility(View.GONE);
				return false;
			}
		});
	}
	
	@Nullable
	@OnClick({R.id.img_collect,R.id.img_comment,R.id.img_share,R.id.img_rotate,R.id.back_btn})
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.img_collect:
				collectClick();
				break;
			case R.id.img_comment:
				commentClick();
				break;
			case R.id.img_share:
				shareClick();
				break;
			case R.id.img_rotate:
				rotateClick();
				break;
			case R.id.back_btn:
				onBackPressed();
				break;
		}
	}

	//旋转屏幕点击
	private void rotateClick() {
		rotateFlag = !rotateFlag;
		if (rotateFlag){
			//横屏
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}else {
			//竖屏
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}

	//分享点击
	private void shareClick() {
//		Toast.makeText(mContext,"share Click",Toast.LENGTH_SHORT).show();
//		UemgShare a = new UemgShare(mContext);
//		String str = "www.baidu.com";
//		a.setImage(str).setTitle("321").setText("123").share();
	}
	//评论点击
	private void commentClick() {
		Toast.makeText(mContext,"commment Click",Toast.LENGTH_SHORT).show();
	}

	//收藏点击
	private void collectClick() {
		Toast.makeText(mContext,"collect Click",Toast.LENGTH_SHORT).show();
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
                          .onPageChange(PdfActivity2.this).load();

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
		currentPage = page;
		this.pageCount = pageCount;
		pageSeekBar.setMax(pageCount-1);
		title.setText(page + "/" + pageCount);
		if (page == lastPage){
			isPageChange = false;
		}else {
			isPageChange = true;
		}
		lastPage = page;
	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.alpha_into, R.anim.zoomout);
	}
}
