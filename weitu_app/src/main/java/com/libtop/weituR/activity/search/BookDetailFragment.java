package com.libtop.weituR.activity.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.libtop.weitu.R;
import com.libtop.weituR.activity.ContentActivity;
import com.libtop.weituR.activity.ContentFragment;
import com.libtop.weituR.activity.login.LoginFragment;
import com.libtop.weituR.activity.search.adapter.MainPageAdapter;
import com.libtop.weituR.activity.search.dto.BookDetailDto;
import com.libtop.weituR.activity.search.dto.CommentNeedDto;
import com.libtop.weituR.activity.search.dto.DetailDto;
import com.libtop.weituR.activity.search.dto.LabelDto;
import com.libtop.weituR.activity.source.PdfTryReadActivity;
import com.libtop.weituR.base.impl.NotifyFragment;
import com.libtop.weituR.eventbus.MessageEvent;
import com.libtop.weituR.http.HttpRequest;
import com.libtop.weituR.tool.Preference;
import com.libtop.weituR.utils.CheckUtil;
import com.libtop.weituR.utils.ContantsUtil;
import com.libtop.weituR.utils.IsbnUtils;
import com.libtop.weituR.utils.JsonUtil;
import com.libtop.weituR.utils.ShareSdkUtil;
import com.libtop.weituR.utils.StringUtil;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;


public class BookDetailFragment extends ContentFragment{

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
	@Bind(R.id.try_read)
	RadioButton tryReadRadio;
//	@Bind(R.id.header_container)
//	LinearLayout headerContainer;

//	@Bind(R.id.want_con)
//	LinearLayout wantCon;
//	@Bind(R.id.see_con)
//	LinearLayout seeCon;
//	@Bind(R.id.favorate_con)
//	LinearLayout favorateCon;
//	@Bind(R.id.up_con)
//	LinearLayout upCon;
//	@Bind(R.id.down_con)
//	LinearLayout downCon;
//	@Bind(R.id.common_con)
//	LinearLayout commonCon;
//	@Bind(R.id.level)
//	RatingBar level;
//	@Bind(R.id.common)
//	TextView common;

//	@Bind(R.id.want)
//	TextView want;
//	@Bind(R.id.see_pre)
//	TextView see;
//	@Bind(R.id.favorate)
//	TextView favorate;
//	@Bind(R.id.up)
//	TextView up;
//	@Bind(R.id.down)
//	TextView down;

	@Nullable
	@Bind(R.id.want_label)
	TextView wantLabel;
	@Nullable
	@Bind(R.id.see_label)
	TextView seeLabel;
	@Nullable
	@Bind(R.id.favorate_label)
	TextView favorateLabel;
	@Nullable
	@Bind(R.id.up_label)
	TextView upLabel;
	@Nullable
	@Bind(R.id.tv_title)
	TextView tvTitle;
	//	@Bind(R.id.post_error)
//	Button postError;
	@Nullable
	@Bind(R.id.img_collect)
	ImageView imgCollect;

	private List<Fragment> datas;
	private MainPageAdapter adapter;
	private DetailDto dto;
	private BookDetailDto bookdto;
	private LabelDto label;

	private int headerHeight = 0;
	private int commonValue = 0;
	private String key;
	private String schoolCode;

	private boolean isFromCapture;
	private String allBookString;

	@Nullable
	@Bind(R.id.down_label)
	TextView downLabel;
	private boolean isCollectShow;

	private String imgPath;

	private boolean isFromMainPage = false;
	public static final String ISFROMMAINPAGE = "isFromMainPage";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		datas = new ArrayList<Fragment>();
		adapter = new MainPageAdapter(mFm, datas);
		// PlayManager.getInstance(context);// 音乐初始化
	}

	@Override
	protected int getLayoutId() {
		return R.layout.activity_book_detail2;
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
		Bundle bundle = ((ContentActivity)mContext).getCurrentExtra();
		key = bundle.getString("isbn");
		isFromMainPage = bundle.getBoolean(ISFROMMAINPAGE,false);
		titleView.setText(bundle.getString("name"));
		tvTitle.setText(bundle.getString("name"));
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
//		x.image().bind(icon,ContantsUtil.IMG_BASE + bundle.getString("cover"),mOptions);
		imgPath = ContantsUtil.IMG_BASE+bundle.getString("cover");
		Picasso.with(mContext).load(imgPath).fit().into(icon);
		viewpager.setAdapter(adapter);
//		headerContainer.getViewTreeObserver().addOnGlobalLayoutListener(
//				new OnGlobalLayoutListener() {
//					@SuppressWarnings("deprecation")
//					@Override
//					public void onGlobalLayout() {
//						headerHeight = headerContainer.getHeight();
//						headerContainer.getViewTreeObserver()
//								.removeGlobalOnLayoutListener(this);
//					}
//				});
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int id) {
				switch (id) {
					case R.id.info:
						viewpager.setCurrentItem(0);
						break;
					case R.id.try_read:
//					viewpager.setCurrentItem(1);
						tryReadClick();
						break;
					case R.id.struct:
//					viewpager.setCurrentItem(2);
						viewpager.setCurrentItem(1);
						break;
					case R.id.cd:
//					viewpager.setCurrentItem(3);
						viewpager.setCurrentItem(2);
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
//					radioGroup.check(R.id.try_read);
						radioGroup.check(R.id.struct);
						break;
					case 2:
//					radioGroup.check(R.id.struct);
						radioGroup.check(R.id.cd);
						break;
					case 3:
//					radioGroup.check(R.id.cd);
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

	private void tryReadClick() {
		Intent intent = new Intent();
		if (dto == null){
			showToast("该图书没有试读内容");
			return;
		}
		if (!TextUtils.isEmpty(dto.downloadUrl))
			intent.putExtra("url", dto.downloadUrl);
		intent.putExtra("doc_id", dto.id);
		intent.putExtra("BookDetailDto",new Gson().toJson(bookdto));
		intent.setClass(mContext, PdfTryReadActivity.class);
		mContext.startActivity(intent);
		mContext.overridePendingTransition(R.anim.zoomin,
				R.anim.alpha_outto);
	}

	public void onResume() {
		super.onResume();
		if (ContantsUtil.COMMON) {
//			common.setText(Html.fromHtml("(共" + (commonValue + 1)
//					+ "条<font color=\"#47b89d\">评论</font>)"));
			ContantsUtil.COMMON = false;
		}
	}

//	@Override
//	public void onBackPressed() {
////		mContext.finish();
//		((ContentActivity)mContext).popBack();
//	}

	@Nullable
	@OnClick({R.id.img_collect,R.id.img_comment,R.id.img_share,R.id.search,R.id.back_btn,R.id.see_con,R.id.want_con,R.id.up_con
			,R.id.common_con,R.id.down_con,R.id.favorate_con,R.id.post_error})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.img_collect:
				collectClick();
				break;
			case R.id.img_comment:
				commentClick();
				break;
			case R.id.img_share:
				shareClick();
				break;
			case R.id.search:
//			SearchActivity.inSearch = true;
//			mContext.finish();
				break;
			case R.id.back_btn:
				onBackPressed();
				break;
			case R.id.see_con:
//			updateState(see, seeLabel, "past", label.past);
				break;
			case R.id.want_con:
//			updateState(want, wantLabel, "wish", label.wish);
				break;
			case R.id.up_con:
//			updateState(up, upLabel, "like", label.like);
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
//			updateState(down, downLabel, "unlike", label.unlike);
				break;
			case R.id.favorate_con:
//			updateState(favorate, favorateLabel, "gather", label.gather);
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

	private void shareClick() {
//		Toast.makeText(mContext,"share click",Toast.LENGTH_SHORT).show();
//		UemgShare a = new UemgShare(mContext);
//		String str = "www.baidu.com";
//		a.setImage(str).setT
//		String imgUrl = "http://n0.libtop.com/1/" + tid + ".pdf";
		String title = "微图分享";
		String content = "“【书】"+dto.title+"”"+ContantsUtil.shareContent;
		String imageUrl = "drawable://" + R.drawable.wbshare;
		ShareSdkUtil.showShareWithLocalImg(mContext,title,content,imageUrl);
	}

	//书本的type为5
	private void commentClick() {
		Intent intent = new Intent(mContext, CommentActivity.class);
		CommentNeedDto commentNeedDto = new CommentNeedDto();
		commentNeedDto.title = dto.title;
		commentNeedDto.author = dto.author;
		commentNeedDto.publisher = dto.publisher;
		commentNeedDto.photoAddress = imgPath;
		commentNeedDto.tid = dto.isbn;
		commentNeedDto.type = 5;
		intent.putExtra("CommentNeedDto",new Gson().toJson(commentNeedDto));
		startActivity(intent);
	}

	private void collectClick() {
		if (isCollectShow){
			requestCancelCollect();
		}else {
			requestCollect();
		}
//		Toast.makeText(mContext,"collect click",Toast.LENGTH_SHORT).show();
	}

	private void requestCancelCollect() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid"
				, mPreference.getString(Preference.uid));
		params.put("tid",dto.isbn);
		params.put("method", "favorite.delete");
		showLoding();
		HttpRequest.loadWithMap(params)
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {

					}

					@Override
					public void onResponse(String json, int id) {
						Log.w("json", json);
						dismissLoading();
						if (CheckUtil.isNullTxt(json)) {
							return;
						}
						if (!CheckUtil.isNull(json)) {
							JSONObject mjson = null;
							try {
								mjson = new JSONObject(json);
								if (mjson.getInt("code") == 1) {
									Toast.makeText(mContext, "取消收藏成功", Toast.LENGTH_SHORT).show();
									isCollectShow = false;
									imgCollect.setBackgroundResource(R.drawable.collect_no);
									Bundle bundle = new Bundle();
									bundle.putBoolean("isDelete",true);
									EventBus.getDefault().post(new MessageEvent(bundle));
								} else {
									Toast.makeText(mContext, "取消收藏失败", Toast.LENGTH_SHORT).show();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						} else {
							showToast("未搜索到相关记录");
						}
					}
				});
	}

	private void requestCollect() {
		showLoding();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid"
				, mPreference.getString(Preference.uid));
		params.put("tid", dto.isbn);
		params.put("type",5);
		params.put("method", "favorite.save");
		showLoding();
		HttpRequest.loadWithMap(params)
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {

					}

					@Override
					public void onResponse(String json, int id) {
						Log.w("json", json);
						dismissLoading();
						if (CheckUtil.isNullTxt(json)) {
							return;
						}
						if (!CheckUtil.isNull(json)) {
							JSONObject mjson = null;
							try {
								mjson = new JSONObject(json);
								if (mjson.getInt("code") == 1) {
									Toast.makeText(mContext, "收藏成功", Toast.LENGTH_SHORT).show();
									isCollectShow = true;
									imgCollect.setBackgroundResource(R.drawable.collect);
									Bundle bundle = new Bundle();
									bundle.putBoolean("isDelete",true);
									EventBus.getDefault().post(new MessageEvent(bundle));
								} else {
									Toast.makeText(mContext, "收藏失败", Toast.LENGTH_SHORT).show();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}

						} else {
							showToast("未搜索到相关记录");
						}
					}
				});

	}

	private void loadData(String key) {
		showLoding();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("uid", Preference.instance(mContext)
				.getString(Preference.uid));
		if (isFromMainPage){
			params.put("method", "book.getBook");
			params.put("bid", IsbnUtils.obscure(key));
		}else {
			params.put("method", "book.get");
			params.put("lid", schoolCode);
			params.put("bid", key);
		}
		HttpRequest.loadWithMap(params)
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {

					}

					@Override
					public void onResponse(String json, int id) {
						dismissLoading();
						if (CheckUtil.isNullTxt(json)) {
							showToast("请求超时，请稍后再试");
							return;
						}
						if (!CheckUtil.isNull(json)) {
//							try {
							if (isFromCapture) {
								if (allBookString != null) {
									json = allBookString;
								}
							}
							BookDetailDto bookDetailDto = JsonUtil.fromJson(json,new TypeToken<BookDetailDto>(){}.getType());
							bookdto = bookDetailDto;
							isCollectShow = (bookDetailDto.favorite == 1);
							if (isCollectShow) {
								imgCollect.setBackgroundResource(R.drawable.collect);
							} else {
								imgCollect.setBackgroundResource(R.drawable.collect_no);
							}
							dto = bookDetailDto.book;
							if (dto != null) {
								// report.setText(dto.publisher);
								InfoFragment fragment = InfoFragment
										.Instance();
								fragment.loadInfo(dto);
								datas.add(fragment);
								if (!TextUtils.isEmpty(dto.downloadUrl)) {
									tryReadRadio.setVisibility(View.VISIBLE);
								} else {
									tryReadRadio.setVisibility(View.GONE);
								}
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
								label = bookDetailDto.label;
//									label = new LabelDto();
//									label.of(jsonObj.getJSONObject("label"));
								setLabel(label, dto);
							}
//							} catch (JSONException e) {
////								showToast("数据出错");
//								e.printStackTrace();
//							}
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
		showLoding();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("method", "label.add");
		params.put("bid", dto.id);
		params.put("mid", dto.mid);
		params.put("code", schoolCode);
		params.put("action", tag);
		params.put("value", stage);
		HttpRequest.loadWithMap(params)
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {

					}

					@Override
					public void onResponse(String jsonStr, int id) {
						dismissLoading();
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
//		if (!CheckUtil.isNull(detail.isbn)) {
//			postError.setVisibility(View.VISIBLE);
//		}
//		see.setText(detail.past + "");
//		want.setText(detail.wish + "");
//		favorate.setText(detail.gather + "");
//		up.setText(detail.like + "");
//		down.setText(detail.unlike + "");

//		if (dto.past == 1) {
//			seeLabel.setTextColor(getResources().getColor(R.color.main_color));
//		}
//
//		if (dto.wish == 1) {
//			wantLabel.setTextColor(getResources().getColor(R.color.main_color));
//		}
//
//		if (dto.gather == 1) {
//			favorateLabel.setTextColor(getResources().getColor(
//					R.color.main_color));
//		}
//
//		if (dto.like == 1) {
//			upLabel.setTextColor(getResources().getColor(R.color.main_color));
//		}
//
//		if (dto.unlike == 1) {
//			downLabel.setTextColor(getResources().getColor(R.color.main_color));
//		}
		if (!CheckUtil.isNull(detail.title)) {
			titleView.setText(detail.title);
		}
		if (!CheckUtil.isNull(detail.author)) {
			auther.setText(detail.author);
		}
		if (!CheckUtil.isNull(detail.publisher)) {
			report.setText(detail.publisher);
		}

//		level.setProgress((int) (1.0f * detail.score / detail.comment));
//		if (detail.mid == null) {
//			common.setVisibility(View.GONE);
//		} else {
//			common.setVisibility(View.VISIBLE);
//			commonValue = detail.comment;
//			common.setText(Html.fromHtml("(共" + detail.comment
//					+ "条<font color=\"#47b89d\">评论</font>)"));
//		}
	}


	public DetailDto getDto() {
		return dto;
	}

	public void show() {
		showLoding();
	}

	public void hide() {
		dismissLoading();
	}

	public String getKey() {
		return key;
	}

	public String getSchool() {
		return schoolCode;
	}


}
