package com.libtop.weituR.activity.user.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weituR.activity.search.utils.DocType;
import com.libtop.weituR.activity.user.dto.CollectionBean;
import com.libtop.weituR.utils.selector.MultiImageSelectorFragment;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.vov.vitamio.utils.Log;

/**
 * <p>
 * Title: CollectionAdapter.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * CreateTime：16/6/3
 * </p>
 *
 * @author 陆
 * @version common v1.0
 */
public class CollectionAdapter extends BaseAdapter {
    final int VIEW_TYPE = 0;
    final int TYPE_0 = 0;
    final int TYPE_1 = 1;
    final int TYPE_2 = 2;
    private LayoutInflater mInflater;
    private Context context;
    private List<CollectionBean> mlist;
    //0文档 1图片
    private int type = 0;
    private boolean visable = false;
    private boolean allCheck = false;
//    public DisplayImageOptions mOptions;

    public CollectionAdapter(Context context, List<CollectionBean> list) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mlist = list;
//        ImageLoader.getInstance().init(
//                ImageLoaderConfiguration.createDefault(context));
//        mOptions = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.default_image)
//                .showImageForEmptyUri(R.drawable.default_image)
//                .showImageOnFail(R.drawable.default_image)
//                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
//                .displayer(new RoundedBitmapDisplayer(8)).cacheInMemory(true)
//                .cacheOnDisc(true).build();
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void setData(List<CollectionBean> list) {
        this.mlist = list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder1 = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_list_result3, null);
            holder1 = new ViewHolder();
            holder1.view = (View) convertView.findViewById(R.id.check_layout);
            holder1.checkMark = (CheckBox) convertView.findViewById(R.id.checkmark);
            holder1.iconCover = (ImageView) convertView.findViewById(R.id.see);
            holder1.iconCover2 = (ImageView) convertView.findViewById(R.id.see2);
            holder1.titleText = (TextView) convertView.findViewById(R.id.doc_title);
            holder1.uploaderText = (TextView) convertView.findViewById(R.id.doc_author);
            holder1.timeText = (TextView) convertView.findViewById(R.id.doc_time);
            holder1.imageText = (TextView) convertView.findViewById(R.id.doc_size);
            holder1.fraOne = (View) convertView.findViewById(R.id.fra_one);
            holder1.fraTwo = (View) convertView.findViewById(R.id.fra_two);
            holder1.introductionText = (TextView) convertView.findViewById(R.id.doc_introduction);
            convertView.setTag(holder1);
        } else {
            holder1 = (ViewHolder) convertView.getTag();
        }

        String str = mlist.get(position).typedef;
        int typeF = mlist.get(position).type;

        if (str.equals("book")) {
            holder1.fraOne.setVisibility(View.GONE);
            holder1.fraTwo.setVisibility(View.VISIBLE);
            holder1.introductionText.setVisibility(View.VISIBLE);
        } else {
            holder1.fraOne.setVisibility(View.VISIBLE);
            holder1.fraTwo.setVisibility(View.GONE);
            holder1.introductionText.setVisibility(View.GONE);
        }

        holder1.titleText.setText(mlist.get(position).title);
        holder1.uploaderText.setText("上传者:" + mlist.get(position).uploadUsername);
        holder1.introductionText.setText(mlist.get(position).introduction);
        //  checkView(holder1.checkMark, mlist.get(position).ischecked);
        holder1.checkMark.setChecked(allCheck);
        if (mlist.get(position).typedef.equals("document")) {
            holder1.iconCover.setImageResource(DocType.getDocType(mlist.get(position).title));
        } else {
            String a = "http://cover1.bookday.cn/" + mlist.get(position).cover;
            Picasso.with(context).load(a).fit().into(holder1.iconCover);
//            ImageLoader.getInstance().displayImage(a, holder1.iconCover,
//                    mOptions, null);
        }
        if (visable)
            holder1.view.setVisibility(View.VISIBLE);
        else
            holder1.view.setVisibility(View.GONE);
        holder1.checkMark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mlist.get(position).ischecked = isChecked;
            }
        });
        return convertView;
    }

    protected class ViewHolder {
        ImageView iconCover, iconCover2;
        TextView titleText, uploaderText, timeText, imageText, introductionText;
        View view, fraOne, fraTwo;
        CheckBox checkMark;
    }

    private void getImage(ImageView image, String url) {
        String a = "http://cover1.bookday.cn/" + url;
        Picasso.with(context)
                .load(a)
                .placeholder(R.drawable.default_image)
                .tag(MultiImageSelectorFragment.TAG)
                .fit()
                .centerCrop()
                .into(image);
    }

    private void setTime(long beginDate, TextView textView) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String sd = sdf.format(new Date(beginDate));
        textView.setText("时间: " + sd);
    }

    public void setVisableView() {
        if (visable)
            visable = false;
        else
            visable = true;
        notifyDataSetChanged();
    }

    public void setNewData(List<CollectionBean> list) {
        this.mlist = list;
        notifyDataSetChanged();
    }

    public void setAllView() {
        if (allCheck)
            allCheck = false;
        else
            allCheck = true;
        for (int i = 0; i < mlist.size(); i++)
            mlist.get(i).ischecked = allCheck;
        notifyDataSetChanged();
    }

    public String[] cleanView() {
        List<CollectionBean> list = new ArrayList<CollectionBean>();
        for (int i = 0; i < mlist.size(); i++) {
            if (mlist.get(i).ischecked) {
                Log.e("" + mlist.get(i).ischecked);
                list.add(mlist.get(i));
            }
        }
        String[] str = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            str[i] = list.get(i).id;
        }
        allCheck = false;
        return str;
    }

}
