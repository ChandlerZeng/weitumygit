package com.libtop.weituR.utils.selector.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.libtop.weitu.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * Title: TagsView.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * CreateTime：16/5/20
 * </p>
 *
 * @author 陆
 * @version common v1.0
 */
public class TagsView {
    private String tags;
    LinearLayout mAddPhotoLayout;
    private Context context;
    TextView textHint;

    public TagsView(String str, LinearLayout v, Context m, TextView t) {
        this.textHint = t;
        this.context = m;
        this.tags = str;
        this.mAddPhotoLayout = v;
        getTage();
    }

    public void setkong() {
        tags = "";
    }

    public String getTags() {
        String[] a = getViewText();
        if (a.length == 0)
            return "";
        if (a.length == 1) {
            tags = a[0];
        } else {
            for (int i = 0; i < a.length; i++) {
                if (i == 0)
                    tags = "" + a[i];
                else
                    tags = tags + "," + a[i];
            }
        }
        return tags;
    }

    private void getTage() {
        if (tags != null) {
            String[] a = tags.split(",");
            List<String> list = new ArrayList<String>();
            for (int i = 0; i < a.length; i++) {
                if (a[i].length() != 0)
                    list.add(a[i]);
            }
            setTags(list);
        }
    }

    private void setTags(List<String> text) {
        int count = mAddPhotoLayout.getChildCount();
        textHint.setVisibility(View.VISIBLE);
        for (int i = 0; i < text.size(); i++) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final TextView rb = (TextView) mInflater
                    .inflate(R.layout.tags_layout, null);
            rb.setText(text.get(i));
            rb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mAddPhotoLayout.removeView(rb);
                }
            });
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(10, 0, 0, 0);
            rb.setLayoutParams(lp);
            mAddPhotoLayout.addView(rb);
        }
    }

    public void setTags(String[] textAarry) {
        List<String> lists = Arrays.asList(textAarry);
        setTags(lists);
    }

    /**
     * 添加标签
     *
     * @param text
     */
    public void addTags(String text) {
        int count = mAddPhotoLayout.getChildCount();
        textHint.setVisibility(View.VISIBLE);
        if (count > 3) {
            Toast.makeText(context, "最多四项", Toast.LENGTH_SHORT).show();
            return;
        }
        int lenght = text.length();
        if (lenght > 8) {
            Toast.makeText(context, "8个字符以内", Toast.LENGTH_SHORT).show();
            return;
        }
        if (lenght == 0 || text.equals("\n")) {
            Toast.makeText(context, "不可输入空格", Toast.LENGTH_SHORT).show();
            return;
        }
        text = text.replaceAll("[\\n\\r]*", "");
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final TextView rb = (TextView) mInflater
                .inflate(R.layout.tags_layout, null);
        rb.setText(text);
        rb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddPhotoLayout.removeView(rb);
            }
        });
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10, 0, 0, 0);
        rb.setLayoutParams(lp);
        mAddPhotoLayout.addView(rb);
        tags = text + ",";
    }

    public String[] getViewText() {

        int count = mAddPhotoLayout.getChildCount();
        String[] Slist = new String[count];
        for (int i = 0; i < count; i++) {
            String a = ((TextView) mAddPhotoLayout.getChildAt(i)).getText().toString().trim();
            Slist[i] = a;
        }
        return Slist;
    }
}
