package com.libtop.weituR.activity.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RatingBar;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.ContentActivity;
import com.libtop.weituR.activity.login.LoginFragment;
import com.libtop.weituR.activity.search.adapter.MainPageAdapter;
import com.libtop.weituR.activity.search.dto.DetailDto;
import com.libtop.weituR.activity.search.dto.LabelDto;
import com.libtop.weituR.base.impl.ImgFragment;
import com.libtop.weituR.base.impl.NotifyFragment;
import com.libtop.weituR.http.HttpRequest;
import com.libtop.weituR.tool.Preference;
import com.libtop.weituR.utils.CheckUtil;
import com.libtop.weituR.utils.ContantsUtil;
import com.libtop.weituR.utils.JsonUtil;
import com.libtop.weituR.utils.StringUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;

public class BookDetailFragment extends ImgFragment{

	@Bind(R.id.auther)
	TextView auther;
	@Bind(R.id.book_report)
	TextView report;
	@Bind(R.id.icon)
	ImageView icon;
	@Bind(R.id.search)
	ImageButton search;

	@Bind(R.id.back_btn)
	ImageButton backBtn;
	@Bind(R.id.title)
	TextView titleView;
	@Bind(R.id.viewpager)
	ViewPager viewpager;
	@Bind(R.id.radioGroup)
	RadioGroup radioGroup;
	@Bind(R.id.cd)
	RadioButton discRadio;
	@Bind(R.id.header_container)
	LinearLayout headerContainer;

	@Bind(R.id.want_con)
	LinearLayout wantCon;
	@Bind(R.id.see_con)
	LinearLayout seeCon;
	@Bind(R.id.favorate_con)
	LinearLayout favorateCon;
	@Bind(R.id.up_con)
	LinearLayout upCon;
	@Bind(R.id.down_con)
	LinearLayout downCon;
	@Bind(R.id.common_con)
	LinearLayout commonCon;
	@Bind(R.id.level)
	RatingBar level;
	@Bind(R.id.common)
	TextView common;

	@Bind(R.id.want)
	TextView want;
	@Bind(R.id.see_pre)
	TextView see;
	@Bind(R.id.favorate)
	TextView favorate;
	@Bind(R.id.up)
	TextView up;
	@Bind(R.id.down)
	TextView down;

	@Bind(R.id.want_label)
	TextView wantLabel;
	@Bind(R.id.see_label)
	TextView seeLabel;
	@Bind(R.id.favorate_label)
	TextView favorateLabel;
	@Bind(R.id.up_label)
	TextView upLabel;
	@Bind(R.id.down_label)
	TextView downLabel;
	@Bind(R.id.post_error)
	Button postError;

	private List<Fragment> datas;
	private MainPageAdapter adapter;
	private DetailDto dto;
	private LabelDto label;

	private int headerHeight = 0;
	private int commonValue = 0;
	private String key;
	private String schoolCode;

	private boolean isFromCapture;
	private String allBookString;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		datas = new ArrayList<Fragment>();
		adapter = new MainPageAdapter(mFm, datas);
		// PlayManager.getInstance(context);// 音乐初始化
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_book_detail;
	}

	@Override
	public void onCreation(View root) {
		initActivity();
	}

	private void initActivity() {
//		wantCon.setOnClickListener(this);
//		seeCon.setOnClickListener(this);
//		favorateCon.setOnClickListener(this);
//		upCon.setOnClickListener(this);
//		downCon.setOnClickListener(this);
//		backBtn.setOnClickListener(this);
//		search.setOnClickListener(this);
//		commonCon.setOnClickListener(this);
//		postError.setOnClickListener(this);
		Bundle bundle = mContext.getIntent().getExtras();
		key = bundle.getString("isbn");
		titleView.setText(bundle.getString("name"));
		auther.setText(bundle.getString("auth"));
		String publisher = bundle.getString("publisher");
		if (!CheckUtil.isNull(publisher)) {
			report.setText(bundle.getString("publisher"));
		}
		schoolCode = bundle.getString("school");
		isFromCapture = bundle.getBoolean("isFromCapture");
		allBookString = bundle.getString("allJson");
//		ImageLoader.getInstance().displayImage(
//				ContantsUtil.IMG_BASE + bundle.getString("cover"), icon,
//				mOptions);
		Picasso.with(mContext).load(ContantsUtil.IMG_BASE + bundle.getString("cover")).into(icon);
//		x.image().bind(icon,ContantsUtil.IMG_BASE + bundle.getString("cover"),mOptions);
		viewpager.setAdapter(adapter);
		headerContainer.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@SuppressWarnings("deprecation")
					@Override
					public void onGlobalLayout() {
						headerHeight = headerContainer.getHeight();
						headerContainer.getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);
					}
				});
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int id) {
				switch (id) {
				case R.id.info:
					viewpager.setCurrentItem(0);
					break;
				case R.id.category:
					viewpager.setCurrentItem(1);
					break;
				case R.id.struct:
					viewpager.setCurrentItem(2);
					break;
				case R.id.cd:
					viewpager.setCurrentItem(3);
					break;
				}
			}
		});

		viewpager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				NotifyFragment fragment = (NotifyFragment) datas.get(position);
				fragment.notify("");
				switch (position) {
				case 0:
					radioGroup.check(R.id.info);
					break;
				case 1:
					radioGroup.check(R.id.category);
					break;
				case 2:
					radioGroup.check(R.id.struct);
					break;
				case 3:
					radioGroup.check(R.id.cd);
					break;
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		loadData(key);
	}

	public void onResume() {
		super.onResume();
		if (ContantsUtil.COMMON) {
			common.setText(Html.fromHtml("(共" + (commonValue + 1)
					+ "条<font color=\"#47b89d\">评论</font>)"));
			ContantsUtil.COMMON = false;
		}
	}

	@Override
	public void onBackPressed() {
		mContext.finish();
	}

	@Nullable
	@OnClick({R.id.search,R.id.back_btn,R.id.see_con,R.id.want_con,R.id.up_con
			,R.id.common_con,R.id.down_con,R.id.favorate_con,R.id.post_error})
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.search:
			SearchActivity.inSearch = true;
			mContext.finish();
			break;
		case R.id.back_btn:
			mContext.finish();
			break;
		case R.id.see_con:
			updateState(see, seeLabel, "past", label.past);
			break;
		case R.id.want_con:
			updateState(want, wantLabel, "wish", label.wish);
			break;
		case R.id.up_con:
			updateState(up, upLabel, "like", label.like);
			break;
		case R.id.common_con:
			Bundle bundle = new Bundle();
			bundle.putString("bid", dto.mid);
			bundle.putString(ContentActivity.FRAG_CLS,CommonFragment.class.getName());
			bundle.putBoolean(ContentActivity.FRAG_ISBACK, true);
			bundle.putBoolean(ContentActivity.FRAG_WITH_ANIM,true);
			mContext.startActivity(bundle, ContentActivity.class);
			break;
		case R.id.down_con:
			updateState(down, downLabel, "unlike", label.unlike);
			break;
		case R.id.favorate_con:
			updateState(favorate, favorateLabel, "gather", label.gather);
			break;
		case R.id.post_error:
			Bundle bundle1 = new Bundle();
			bundle1.putString("mid", dto.mid);
			bundle1.putString(ContentActivity.FRAG_CLS, PostErrorFragment.class.getName());
			bundle1.putBoolean(ContentActivity.FRAG_ISBACK, true);
			bundle1.putBoolean(ContentActivity.FRAG_WITH_ANIM, true);
			mContext.startActivity(bundle1, ContentActivity.class);
			break;
		}
	}

	private void loadData(String key) {
		mLoading.show();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("method", "book.get");
		params.put("lid", schoolCode);
		params.put("bid", key);
		params.put("uid", Preference.instance(mContext)
				.getString(Preference.uid));
		HttpRequest.loadWithMapSec(params, new HttpRequest.CallBackSec() {
			@Override
			public void onError(Call call, Exception e, int id) {
				if (mLoading.isShowing()){
					mLoading.dismiss();
				}
			}

			@Override
			public void onResponse(String json, int id) {
				if (mLoading.isShowing()){
					mLoading.dismiss();
				}
				if (CheckUtil.isNullTxt(json)) {
					showToast("请求超时，请稍后再试");
					return;
				}
				if (!CheckUtil.isNull(json)) {
					try {
						if (isFromCapture){
							if (allBookString!=null){
								json = allBookString;
							}
						}
						JSONObject jsonObj = new JSONObject(json);
						dto = JsonUtil.fromJson(
								jsonObj.getString("book"),
								DetailDto.class);
						if (dto != null) {
							// report.setText(dto.publisher);
							InfoFragment fragment = InfoFragment
									.Instance();
							fragment.loadInfo(dto.introduction);
							datas.add(fragment);
							CatelogFragment fragment1 = CatelogFragment
									.Instance();
							fragment1.loadInfo(dto.catalog);
							datas.add(fragment1);
							FavoriteFragment fragment2 = FavoriteFragment
									.Instance();
							datas.add(fragment2);
							fragment2.loadInfo(dto.indexList);
							if (dto.disc > 0) {
								CdFragment fragment3 = CdFragment
										.Instance(dto.disc);
								datas.add(fragment3);
								discRadio.setVisibility(View.VISIBLE);
							} else {
								discRadio.setVisibility(View.GONE);
							}
							adapter.notifyDataSetChanged();

							// label
							label = new LabelDto();
							label.of(jsonObj.getJSONObject("label"));
							setLabel(label, dto);
						}
					} catch (JSONException e) {
						showToast("数据出错");
						e.printStackTrace();
					}
				} else {
					showToast("请求出错，请稍后再试");
				}
			}
		});
	}

	private void updateState(final TextView view, final TextView labelView,
							 final String tag, int state) {
		if (CheckUtil.isNull(Preference.instance(mContext).getString(
				Preference.uid))) {
			Bundle bundle=new Bundle();
			bundle.putString(ContentActivity.FRAG_CLS, LoginFragment.class.getName());
			bundle.putBoolean(ContentActivity.FRAG_ISBACK, true);
			bundle.putBoolean(ContentActivity.FRAG_WITH_ANIM,true);
			mContext.startActivity(bundle, ContentActivity.class);
			return;
		}
		final int stage;
		if (state == 0) {
			stage = 1;
		} else {
			stage = 0;
		}
		mLoading.show();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("method", "label.add");
		params.put("bid", dto.id);
		params.put("mid", dto.mid);
		params.put("code", schoolCode);
		params.put("action", tag);
		params.put("value", stage);
		HttpRequest.loadWithMapSec(params, new HttpRequest.CallBackSec() {
			@Override
			public void onError(Call call, Exception e, int id) {

			}

			@Override
			public void onResponse(String jsonStr, int id) {
				if (mLoading.isShowing()){
					mLoading.dismiss();
				}
				if (CheckUtil.isNullTxt(jsonStr)) {
					showToast("请求超时，请稍后再试");
					return;
				}
				if (!CheckUtil.isNull(jsonStr)) {
					try {
						JSONObject json = new JSONObject(jsonStr);
						if (json.getInt("code") == 1) {
							if (stage == 1) {
								view.setText(StringUtil.toInt(view
										.getText()) + 1 + "");
								labelView.setTextColor(getResources()
										.getColor(R.color.main_color));
								label.reset(tag, 1);
							} else {
								view.setText(StringUtil.toInt(view
										.getText()) - 1 + "");
								labelView.setTextColor(getResources()
										.getColor(R.color.label_color));
								label.reset(tag, 0);
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	private void setLabel(LabelDto dto, DetailDto detail) {
		if (!CheckUtil.isNull(detail.isbn)) {
			postError.setVisibility(View.VISIBLE);
		}
		see.setText(detail.past + "");
		want.setText(detail.wish + "");
		favorate.setText(detail.gather + "");
		up.setText(detail.like + "");
		down.setText(detail.unlike + "");

		if (dto.past == 1) {
			seeLabel.setTextColor(getResources().getColor(R.color.main_color));
		}

		if (dto.wish == 1) {
			wantLabel.setTextColor(getResources().getColor(R.color.main_color));
		}

		if (dto.gather == 1) {
			favorateLabel.setTextColor(getResources().getColor(
					R.color.main_color));
		}

		if (dto.like == 1) {
			upLabel.setTextColor(getResources().getColor(R.color.main_color));
		}

		if (dto.unlike == 1) {
			downLabel.setTextColor(getResources().getColor(R.color.main_color));
		}
		if (!CheckUtil.isNull(detail.title)) {
			titleView.setText(detail.title);
		}
		if (!CheckUtil.isNull(detail.author)) {
			auther.setText(detail.author);
		}
		if (!CheckUtil.isNull(detail.publisher)) {
			report.setText(detail.publisher);
		}
		level.setProgress((int) (1.0f * detail.score / detail.comment));
		if (detail.mid == null) {
			common.setVisibility(View.GONE);
		} else {
			common.setVisibility(View.VISIBLE);
			commonValue = detail.comment;
			common.setText(Html.fromHtml("(共" + detail.comment
					+ "条<font color=\"#47b89d\">评论</font>)"));
		}
	}

	public boolean setHeardVisibleHeight(int height) {
		int headerHeight = height + headerContainer.getHeight();
		if (headerHeight < 0)
			headerHeight = 0;
		if (headerHeight > this.headerHeight) {
			headerHeight = this.headerHeight;
			return false;
		}
		LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) headerContainer
				.getLayoutParams();
		lp.height = headerHeight;
		headerContainer.setLayoutParams(lp);
		return true;
	}

	public DetailDto getDto() {
		return dto;
	}

	public void show() {
		mLoading.show();
	}

	public void hide() {
		if (mLoading.isShowing()){
			mLoading.dismiss();
		}
	}

	public String getKey() {
		return key;
	}

	public String getSchool() {
		return schoolCode;
	}


}
