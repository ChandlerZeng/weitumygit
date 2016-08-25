package com.libtop.weitu.utils.selector.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.libtop.weitu.R;
import com.libtop.weitu.utils.selector.bean.ListGridImage;
import com.libtop.weitu.utils.selector.view.ImageCoverActivity;
import com.libtop.weitu.utils.viewpagerbitmap.ImagePagerActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Title: ListGridAdtper.java
 * </p>
 * <p>
 * Descriptio
 * </p>
 * <p>
 * CreateTime：16/5/5
 * </p>
 *
 * @author 作者名
 * @version common v1.0
 */
public class ListGridAdapter extends BaseAdapter {
    private Map<String, List<ListGridImage>> mLinkedHashMap;
    private Context mcontext;
    private LayoutInflater mInflater;
    List<String> titleList = new ArrayList<String>();
    ArrayList<String> commentList = new ArrayList<String>();
    ArrayList<String> urlList = new ArrayList<String>();
    ArrayList<String> idList = new ArrayList<String>();
    List<List<ListGridImage>> imageList = new ArrayList<List<ListGridImage>>();
    List<ListGridImage> positionList = new ArrayList<ListGridImage>();
    boolean mAdministration = false;
    List<Integer> ischeckList = new ArrayList<Integer>();
    private int isCover = 1;

    public ListGridAdapter(Context context, Map<String, List<ListGridImage>> LinkedHashMap) {
        this.mcontext = context;
        this.mLinkedHashMap = LinkedHashMap;
        this.mInflater = LayoutInflater.from(context);
        setIntegerList();
        countPosition();
        getData();
    }

    public ListGridAdapter(Context context, Map<String, List<ListGridImage>> LinkedHashMap, boolean Administration, int isCover) {
        this.isCover = isCover;
        this.mcontext = context;
        this.mLinkedHashMap = LinkedHashMap;
        this.mInflater = LayoutInflater.from(context);
        this.mAdministration = Administration;
        setIntegerList();
        countPosition();
        getData();
    }

    private void setIntegerList() {
        for (int i = 0; i < mLinkedHashMap.size(); i++) {
            ischeckList.add(1);
        }
    }


    public void setData(Map<String, List<ListGridImage>> LinkedHashMap) {
        this.mLinkedHashMap = LinkedHashMap;
        getData();
        countPosition();
    }


    @Override
    public int getCount() {
        return mLinkedHashMap.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from
                    (this.mcontext).inflate(R.layout.list_grid, null, false);
            holder.imageView = (CheckBox) convertView.findViewById(R.id.is_check);
            holder.textView = (TextView) convertView.findViewById(R.id.title_text);
            holder.gridView = (GridView) convertView.findViewById(R.id.grid);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (titleList == null || titleList.size() == 0)
            return convertView;
        holder.textView.setText(titleList.get(position));
        if (!mAdministration)
            holder.imageView.setVisibility(View.GONE);

        final ImageGridAdapter2 imageGridAdapter2 = new ImageGridAdapter2(imageList.get(position), mcontext, mAdministration);
        holder.gridView.setAdapter(imageGridAdapter2);
        if (mAdministration) {
            holder.imageView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    setListChange(position, isChecked);
                }
            });
            holder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                    boolean good = imageList.get(position).get(i).ischeck();
                    if (good)
                        imageList.get(position).get(i).setIscheck(false);
                    else
                        imageList.get(position).get(i).setIscheck(true);
                    imageGridAdapter2.setData(imageList.get(position));
                    imageGridAdapter2.notifyDataSetChanged();
                }
            });
        } else {
            holder.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                    if (isCover == 1) {
                        ArrayList<String> list = new ArrayList<String>();
                        ArrayList<String> clist = new ArrayList<String>();
                        list.add(imageList.get(position).get(i).getImageUrl());
                        clist.add(imageList.get(position).get(i).getIntroduction());
                        Intent intent = new Intent(mcontext, ImagePagerActivity.class);
                        String str = imageList.get(position).get(i).getImageUrl();
                        int a = countP(imageList.get(position).get(i).getImageUrl());
                        intent.putExtra("position", a);
                        intent.putExtra("see_pic", 2);
                        intent.putExtra(ImagePagerActivity.DEFAULT_SELECTED_LIST, urlList);
                        intent.putExtra(ImagePagerActivity.COMMENT_LIST, commentList);
                        intent.putExtra(ImagePagerActivity.ID_LIST, idList);
                        mcontext.startActivity(intent);
                    } else {
                        Intent data = new Intent();
                        data.putExtra("id", imageList.get(position).get(i).id);
                        data.putExtra("cover", imageList.get(position).get(i).getImageUrl());
                        ((Activity) mcontext).setResult(ImageCoverActivity.DESCRIPTION_RETURNQ, data);
                        ((Activity) mcontext).finish();
                    }
                }
            });
        }
        return convertView;
    }

    private void setListChange(int position, boolean change) {
        List<ListGridImage> list = imageList.get(position);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setIscheck(change);
        }
        notifyDataSetChanged();
    }

    private void setState(ImageView imageView, int position) {
        if (ischeckList.get(position) == 1)
            imageView.setImageResource(R.drawable.btn_unselected);
        else
            imageView.setImageResource(R.drawable.btn_selected);
    }

    private void getData() {
        Iterator iter = mLinkedHashMap.entrySet().iterator();
        titleList.clear();
        imageList.clear();
        for (Map.Entry<String, List<ListGridImage>> entry : mLinkedHashMap.entrySet()) {
            titleList.add((String) entry.getKey());
            imageList.add((List<ListGridImage>) entry.getValue());
        }
    }


    class ViewHolder {
        CheckBox imageView;
        TextView textView;
        GridView gridView;
    }

    private void countPosition() {
        positionList.clear();

        List<List<ListGridImage>> imageList = new ArrayList<List<ListGridImage>>();
        for (Map.Entry<String, List<ListGridImage>> entry : mLinkedHashMap.entrySet()) {
            imageList.add((List<ListGridImage>) entry.getValue());
        }

        for (int i = 0; i < imageList.size(); i++) {
            List<ListGridImage> mlist = imageList.get(i);
            for (int j = 0; j < mlist.size(); j++) {
                positionList.add(mlist.get(j));
                commentList.add(mlist.get(j).getIntroduction());
                urlList.add(mlist.get(j).getImageUrl());
                idList.add(mlist.get(j).id);
            }
        }


    }

    private int countP(String key) {
        int po = 0;
        for (int i = 0; i < positionList.size(); i++) {
            String s = positionList.get(i).getImageUrl();
            if (key.equals(positionList.get(i).getImageUrl())) {
                po = i;
                break;
            }
        }
        return po;
    }

}
