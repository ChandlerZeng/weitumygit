package com.libtop.weitu.activity.search.utils;

import android.widget.Switch;

import com.libtop.weitu.R;

/**
 * <p>
 * Title: DocType.java
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * CreateTime：16/5/31
 * </p>
 *
 * @author 陆
 * @version common v1.0
 */
public class DocType {

    public static int getDocType(String strs) {
        String[] stra = strs.split("\\.");
        int type = R.drawable.default_image;
        if (stra.length >= 2) {
            String str = stra[stra.length - 1];
            if (str.equals("doc")) {
                type = R.drawable.doc;
            } else if (str.equals("docx")) {
                type = R.drawable.docx;
            } else if (str.equals("ods")) {
                type = R.drawable.ods;
            } else if (str.equals("odt")) {
                type = R.drawable.odt;
            } else if (str.equals("pdf")) {
                type = R.drawable.pdf;
            } else if (str.equals("ppt")) {
                type = R.drawable.ppt;
            } else if (str.equals("pptx")) {
                type = R.drawable.pptx;
            } else if (str.equals("txt")) {
                type = R.drawable.txt;
            } else if (str.equals("xls")) {
                type = R.drawable.xls;
            } else if (str.equals("xlsx")) {
                type = R.drawable.xlsx;
            }
        }
        return type;
    }


}
