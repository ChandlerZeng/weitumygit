package com.libtop.weitu.activity.search;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.search.adapter.BookGridAdapter;
import com.libtop.weitu.activity.search.adapter.BookListAdapter;
import com.libtop.weitu.activity.search.dto.BookDto;
import com.libtop.weitu.base.impl.NotifyFragment;
import com.libtop.weitu.eventbus.MessageEvent;
import com.libtop.weitu.http.HttpRequest;
import com.libtop.weitu.tool.Preference;
import com.libtop.weitu.utils.CheckUtil;
import com.libtop.weitu.utils.CollectionUtils;
import com.libtop.weitu.utils.JsonUtil;
import com.libtop.weitu.widget.listview.ScrollRefListView;
import com.libtop.weitu.widget.stage.StaggeredGridView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import okhttp3.Call;

public class BooksFragment extends NotifyFragment{

	@Bind(R.id.grid_view)
	StaggeredGridView mGridview;
	@Bind(R.id.list)
	ScrollRefListView mListview;
	@Bind(R.id.null_txt)
	TextView mNullText;

	private BookGridAdapter mAdapter;
	private BookListAdapter mListAdapter;
	private List<BookDto> mData=new ArrayList<BookDto>();
//	private HistoryBo mHbo;
	private boolean isCreate = false, hasData = true;
	private int mCurPage = 1;
	private SearchActivity mActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);

		mActivity = (SearchActivity) mContext;
		mAdapter = new BookGridAdapter(mContext, mData);
		mListAdapter = new BookListAdapter(mContext, mData);
//		mHbo= new HistoryBo(mContext);
		mCurPage = 1;
	}

	@Override
	protected int getLayoutId() {
		return R.layout.fragment_result_layout;
	}

	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	@Override
	public void onCreation(View root) {
		initView();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onMessage(MessageEvent event) {
		Bundle bm = event.message;
		int pageIndex = bm.getInt("pageIndex");
		if (this.isVisible()&&pageIndex==ResultFragment.BOOK){
			Log.w("guanglog","getHere Book.....");
			mCurPage = 1;
			loadPage();
		}
	}

	@SuppressLint("NewApi")
	private void initView() {
		mGridview.setAdapter(mAdapter);
		mListview.setAdapter(mListAdapter);
		mListview.setPullLoadEnable(false);
		mGridview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
									int position, long id) {
				BookDto dto = mData.get(position);
//				// 保存点击记录
//				mHbo.saveUpdate(
//						dto.title,
//						dto.isbn,
//						dto.cover,
//						dto.author,
//						"",
//						Preference.instance(mContext).getString(
//								Preference.SchoolCode), dto.publisher);
//				ContantsUtil.UPDATE_HISTORY = true;
				Bundle bundle = new Bundle();
				bundle.putString("isbn", dto.isbn);
//				mContext.startActivity(bundle, BookDetailFragment.class);
				bundle.putString(ContentActivity.FRAG_CLS,BookDetailFragment.class.getName());
				mContext.startActivity(bundle,ContentActivity.class);
			}
		});
		mListview.setXListViewListener(new ScrollRefListView.IXListViewListener() {
			@Override
			public void onLoadMore() {
				if (hasData) {
					loadPage();
				}
			}
		});
		mListview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
									int position, long id) {
				BookDto dto = mData.get(position - 1);
//				// 保存点击记录
//				mHbo.saveUpdate(
//						dto.title,
//						dto.id,
//						dto.cover,
//						dto.author,
//						"",
//						Preference.instance(mContext).getString(
//								Preference.SchoolCode), dto.publisher);
//				ContantsUtil.UPDATE_HISTORY = false;
				Bundle bundle = new Bundle();
				bundle.putString("name", dto.title);
				bundle.putString("cover", dto.cover);
				bundle.putString("auth", dto.author);
				bundle.putString("isbn", dto.id);
				bundle.putString("publisher", dto.publisher);
				bundle.putString("school", Preference.instance(mContext)
						.getString(Preference.SchoolCode));
				bundle.putString(ContentActivity.FRAG_CLS,BookDetailFragment.class.getName());
				bundle.putBoolean(ContentActivity.FRAG_ISBACK,false);
				mContext.startActivity(bundle, ContentActivity.class);
//				mContext.startActivity(bundle, BookDetailFragment.class);
			}
		});

//		load();
	}

	@Override
	public void load(){
		if (!isCreate) {
			mCurPage = 1;
			mData.clear();
			mAdapter.notifyDataSetChanged();
			mListAdapter.notifyDataSetChanged();
			loadPage();
			isCreate = true;
			return;
		}
		if (CollectionUtils.isEmpty(mData)){
			mNullText.setText("未搜索到相关记录");
			mNullText.setVisibility(View.VISIBLE);
		}
	}

	private void loadPage() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("method", "book.query");
		params.put("key", mActivity.getKey());
		params.put("lid",
				Preference.instance(mActivity).getString(Preference.SchoolCode));
		params.put("page", mCurPage);
		if (mCurPage == 1) {
			showLoding();
		}
		mNullText.setVisibility(View.GONE);
		HttpRequest.loadWithMapSec(params, new HttpRequest.CallBackSec() {
			@Override
			public void onError(Call call, Exception e, int id) {

			}

			@Override
			public void onResponse(String json, int id) {
				if (mCurPage == 1) {
					dismissLoading();
				}
				Log.w("json", json);
				if (CheckUtil.isNullTxt(json)) {
					mNullText.setText("未搜索到相关记录");
					mNullText.setVisibility(View.VISIBLE);
//							showToast("请求超时，请稍后再试");
					return;
				}
				if (!CheckUtil.isNull(json)) {
//					try {
						if (mCurPage == 1) {
							mData.clear();
						}
						List<BookDto> bookdto = JsonUtil.fromJson(json,new TypeToken<List<BookDto>>(){});
//						JSONArray array = new JSONArray(json);
						if (bookdto==null)
							return;
						if (bookdto.size() < 10) {
							hasData = false;
							mListview.setPullLoadEnable(false);
						} else {
							hasData = true;
							mListview.setPullLoadEnable(true);
						}
						mData.addAll(bookdto);
//						for (int i = 0; i < array.length(); i++) {
//							BookDto dto = new BookDto();
//							dto.of(array.getJSONObject(i));
//							mData.add(dto);
//						}
						mAdapter.notifyDataSetChanged();
						mListAdapter.notifyDataSetChanged();
						if (mData.size() == 0 && mCurPage == 1) {
							mNullText.setText("未搜索到相关记录");
							mNullText.setVisibility(View.VISIBLE);
						} else {
							mNullText.setVisibility(View.GONE);
						}
						mCurPage++;
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
				} else {
					if (mCurPage == 1) {
						showToast("未搜索到相关记录");
					}
				}
			}
		});
	}

	@Override
	public void notify(String data) {
		hasData = true;
		mCurPage = 1;
		loadPage();
	}

	@Override
	public void reSet() {
		hasData=true;
		mCurPage=1;
		isCreate=false;
	}

	private void hideAndSeek(){
		if (mData.size() == 0 && mCurPage == 1) {
			mNullText.setText("未搜索到相关记录");
			mNullText.setVisibility(View.VISIBLE);
		} else {
			mNullText.setVisibility(View.GONE);
		}
	}

	public void setCreate() {
		isCreate = false;
	}


	@Override
	public void onResume() {
		super.onResume();
		load();
	}
}
