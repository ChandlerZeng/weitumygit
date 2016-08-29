package com.libtop.weitu.activity.search;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.activity.ContentActivity;
import com.libtop.weitu.activity.search.adapter.Cd1Adapter;
import com.libtop.weitu.activity.search.adapter.CdAdapter;
import com.libtop.weitu.activity.search.dto.CdDto;
import com.libtop.weitu.activity.search.dto.MediaResult;
import com.libtop.weitu.activity.source.AudioPlayActivity2;
import com.libtop.weitu.activity.source.PdfActivity;
import com.libtop.weitu.activity.source.TxtActivity;
import com.libtop.weitu.activity.source.VideoPlayActivity;
import com.libtop.weitu.base.impl.NotifyFragment;
import com.libtop.weitu.utils.CheckUtil;
import com.libtop.weitu.utils.ContantsUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;

public class CdFragment extends NotifyFragment{

	@Bind(R.id.list)
	ListView listview;
	@Bind(R.id.top)
	TextView top;
	@Bind(R.id.cd)
	ListView cdView;

	private BookDetailFragment fragment;
	private List<CdDto> data;
	private Stack<Long> ids;
	private CdAdapter adapter;
	private Cd1Adapter adapter1;
	private List<Integer> cdList;

	private boolean isCreate = false;
	private long preId = 0;
	private int disc = 0;
	private String lib = "";
	private int curDisc = 0;
	private int curentPosition = 0;

	public static CdFragment Instance(int disc) {
		CdFragment fragment = new CdFragment();
		Bundle bundle = new Bundle();
		bundle.putInt("cd", disc);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (data == null) {
			data = new ArrayList<CdDto>();
		}
		cdList = new ArrayList<Integer>();
		ids = new Stack<Long>();
		adapter = new CdAdapter(mContext, data);
		adapter1 = new Cd1Adapter(mContext, cdList);
		fragment = (BookDetailFragment)(((ContentActivity) mContext).findFragment(BookDetailFragment.class.getName()));
		disc = getArguments().getInt("cd");
		lib = fragment.getSchool();
	}

	@Override
	protected int getLayoutId() {
		return R.layout.fragment_book_cd;
	}

	@Override
	public void onCreation(View root) {
		initView();
	}

	private void initView() {
		listview.setAdapter(adapter);
		cdView.setAdapter(adapter1);
		cdView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
									int position, long arg3) {
				cdView.setVisibility(View.GONE);
				listview.setVisibility(View.VISIBLE);
				loadData(cdList.get(position));
				curDisc = cdList.get(position);
				ids.push(-1L);
			}
		});
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
									int position, long id) {
				CdDto dto = data.get(position);
				if ("folder".equals(dto.type)) {
					ids.push(preId);
					loadData(dto.id);
				} else if ("image".equals(dto.type)) {
					Intent intent = new Intent();
					intent.putExtra("url", ContantsUtil.getFile(lib,
							fragment.getDto().mid, curDisc, dto.id));
					intent.setClass(mContext, ShowBigImage.class);
					mContext.startActivity(intent);
					mContext.overridePendingTransition(R.anim.zoomin,
							R.anim.alpha_outto);
				} else if ("pdf".equals(dto.type)) {
					Intent intent = new Intent();
					intent.putExtra("url", ContantsUtil.getFile(lib,
							fragment.getDto().mid, curDisc, dto.id));
					intent.setClass(mContext, PdfActivity.class);
					mContext.startActivity(intent);
					mContext.overridePendingTransition(R.anim.zoomin,
							R.anim.alpha_outto);
				} else if ("video".equals(dto.type)) {
					Bundle bundle = new Bundle();
					bundle.putString(VideoPlayActivity.MEDIA_NAME, dto.name);
					bundle.putString(VideoPlayActivity.MEDIA_PATH, ContantsUtil.getFile(lib,
							fragment.getDto().mid, curDisc, dto.id));
					mContext.startActivity(bundle, VideoPlayActivity.class);
				} else if ("audio".equals(dto.type)) {
					Bundle bundle = new Bundle();
//					bundle.putString(AudioPlayActivity.MEDIA_NAME, dto.name);
//					bundle.putString(AudioPlayActivity.MEDIA_PATH, ContantsUtil.getFile(lib,
//							activity.getDto().mid, curDisc, dto.id));
					bundle.putInt("media_list_position",position);
					bundle.putParcelableArrayList("media_list", (ArrayList<? extends Parcelable>) conver(data));
					mContext.startActivity(bundle, AudioPlayActivity2.class);
				} else if ("txt".equals(dto.type)) {
					Intent intent = new Intent();
					intent.putExtra("url", ContantsUtil.getFile(lib,
							fragment.getDto().mid, curDisc, dto.id));
					intent.setClass(mContext, TxtActivity.class);
					mContext.startActivity(intent);
					mContext.overridePendingTransition(R.anim.zoomin,
							R.anim.alpha_outto);
				}
			}
		});
	}

	private List<MediaResult> conver(List<CdDto> source){
		List<MediaResult> result=new ArrayList<MediaResult>();
		for (CdDto dto:source){
			MediaResult dat=new MediaResult();
			dat.title=dto.name;
			dat.url= ContantsUtil.getFile(lib,
					fragment.getDto().mid, curDisc, dto.id);
			result.add(dat);

		}
		return result;
	}

	private void loadData(final long sequence) {
		preId = sequence;
		showLoding();
//		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("lid", lib);
//		params.put("bid", fragment.getDto().mid);
//		params.put("sequnce", curDisc);
//		params.put("id", sequence);
//		http://disc.libtop.com/folder?lid=1&bid=8114277121190&sequnce=0&id=0
		OkHttpUtils
				.post()
				.url(ContantsUtil.CD_DETAIL+"?lid="+lib+"&bid="+fragment.getDto().mid+"&sequnce="+curDisc+"&id="+sequence)
//				.addParams("text", MapUtil.map2ParameterNoMethod(params))
				.build()
				.execute(new StringCallback() {
					@Override
					public void onError(Call call, Exception e, int id) {

					}

					@Override
					public void onResponse(String json, int id) {
						if (CheckUtil.isNullTxt(json)) {
							showToast("请求超时，请稍后再试");
							return;
						}
						if (!CheckUtil.isNull(json)) {
							JSONArray array;
							try {
								if (sequence == 0) {
									top.setVisibility(View.GONE);
								} else {
									top.setVisibility(View.VISIBLE);
								}
								data.clear();
								array = new JSONArray(json);
								for (int i = 0; i < array.length(); i++) {
									CdDto dto = new CdDto();
									dto.of(array.getJSONObject(i));
									data.add(dto);
								}
								adapter.notifyDataSetChanged();
							} catch (JSONException e) {
								e.printStackTrace();
								showToast("数据解析出错");
							}
						}
						dismissLoading();
					}
				});
	}

	@Nullable
	@OnClick({R.id.top})
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.top:
			if (!ids.isEmpty()) {
				Long id = ids.pop();
				if (id == -1) {
					cdView.setVisibility(View.VISIBLE);
					listview.setVisibility(View.GONE);
				} else {
					loadData(id);
				}
			}
			break;
		}
	}

	@Override
	public void notify(String data) {
		if (!isCreate) {
			if (disc > 1) {
				cdView.setVisibility(View.VISIBLE);
				listview.setVisibility(View.GONE);
				cdList.clear();
				for (int i = 0; i < disc; i++) {
					cdList.add(i + 1);
				}
				adapter1.notifyDataSetChanged();
				ids.push(-1L);
			} else {
				cdView.setVisibility(View.GONE);
				listview.setVisibility(View.VISIBLE);
				loadData(0);
			}
			isCreate = true;
		}
	}
}
