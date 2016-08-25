package com.libtop.weitu.utils;

import android.content.Context;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 陆伟健 on 2016/4/13 0013.
 */
public class LargeMappedFiles {
    /**
     * 读取文件
     */
    RandomAccessFile randomAccessFile;
    /**
     * 读取文件二进制流
     */
    private byte[] mbyte;
    /**
     * 片段个数
     */
    public int flowChunkNumber = 0;

    /**
     * 单个片段值为3000KB
     */
    public int flowChunkSize = 1024 * 300;

    /**
     * 总片段数
     */
    private int flowCount = 0;
    /**
     * 文件总容量
     */
    private long fileCountSize = 0;

    private Context mContext;
    /**
     * 尾段片段大小
     */
    private long endFlowChunkSize;

    private String mUrl;

    public LargeMappedFiles(Context context, String url) {
        mContext = context;
        mUrl = url;
        try {
            randomAccessFile = new RandomAccessFile(url, "rw");
            try {
                fileCountSize = (long) randomAccessFile.length();
                mbyte = new byte[flowChunkSize];
                countflowChunkNumber();
                // flowChunkNumber = (int) (fileCountSize / (long) flowChunkSize);
//                mbyte = new byte[(int) fileCount];
//                randomAccessFile.seek(0);
//                randomAccessFile.readFully(mbyte);
//                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 计算片段数和尾段数
     */
    private void countflowChunkNumber() {
        //取余
        long a = fileCountSize % flowChunkSize;
        if (fileCountSize == flowChunkSize || fileCountSize < flowChunkSize) {
            flowCount = 1;
            flowChunkSize = (int) fileCountSize;
            endFlowChunkSize = flowChunkSize;
        } else {
            if (a == 0) {
                flowCount = (int) (fileCountSize / flowChunkSize) - 1;
                endFlowChunkSize = flowChunkSize * 2;
            } else {
                flowCount = (int) (fileCountSize / flowChunkSize);
                endFlowChunkSize = a + flowChunkSize;
            }
        }
    }


    /**
     * 返回当前片段
     *
     * @param count
     * @return
     */
    public byte[] readFile(int count) {
        byte[] ChunkByte;
        flowChunkNumber = count;

        try {
            randomAccessFile = new RandomAccessFile(mUrl, "rw");
            if (flowCount == 1) {
                ChunkByte = new byte[(int) endFlowChunkSize];
                randomAccessFile.seek(0);
                randomAccessFile.readFully(ChunkByte);
                randomAccessFile.close();
            } else {
                if (flowChunkNumber == flowCount) {
                    ChunkByte = new byte[(int) endFlowChunkSize];
                    long haveRead = (count - 1) * flowChunkSize;
                    randomAccessFile.seek(haveRead);
                    randomAccessFile.readFully(ChunkByte);
                    randomAccessFile.close();
                } else {
                    ChunkByte = new byte[flowChunkSize];
                    long haveRead = (count - 1) * flowChunkSize;
                    randomAccessFile.seek(haveRead);
                    randomAccessFile.readFully(ChunkByte);
                    randomAccessFile.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            ChunkByte = new byte[0];
        }
        return ChunkByte;
    }


//    /**
//     * 开始上传文件
//     */
//    public void start() {
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("method", "user.auth");
//        params.put("flowChunkSize", mflowChunkSize);
//        params.put("flowTotalSize", mflowTotalSize);
//        params.put("flowIdentifier", mflowIdentifier);
//        params.put("flowFilename", mflowFilename);
//        params.put("flowRelativePath", mflowRelativePath);
//        params.put("flowChunkNumber", mflowChunkNumber);
//        HttpServiceUtil.getFile(mContext, params
//                , new HttpServiceUtil.CallBack() {
//            @Override
//            public void callback(String jsonStr) {
//
//                postFile(mflowChunkSize, mflowTotalSize, mflowIdentifier, mflowFilename, mflowRelativePath, mflowChunkNumber);
//            }
//        });
//    }

//    /**
//     * 发送文件
//     *
//     * @param flowChunkSize
//     * @param flowTotalSize
//     * @param flowIdentifier
//     * @param flowFilename
//     * @param flowRelativePath
//     * @param flowChunkNumber
//     */
//    private void postFile(String flowChunkSize, String flowTotalSize, String flowIdentifier, String flowFilename, String flowRelativePath, String flowChunkNumber) {
//        readFile(flowCount);
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("method", "user.auth");
//        params.put("flowChunkSize", flowChunkSize);
//        params.put("flowTotalSize", flowTotalSize);
//        params.put("flowIdentifier", flowIdentifier);
//        params.put("flowFilename", flowFilename);
//        params.put("flowRelativePath", flowRelativePath);
//        params.put("flowChunkNumber", flowChunkNumber);
//        HttpServiceUtil.postFile(mContext, params, mbyte
//                , new HttpServiceUtil.CallBack() {
//            @Override
//            public void callback(String jsonStr) {
//                flowCount++;
//                start();
//            }
//        });
//    }

    public String numberFormat() {
        long percent = (flowChunkSize * flowCount) / fileCountSize;
        // 获取格式化对象
        NumberFormat nt = NumberFormat.getPercentInstance();
        // 设置百分数精确度2即保留两位小数
        nt.setMinimumFractionDigits(2);
        String xx = "" + nt.format(percent);
        return xx;
    }

    public byte[] getMbyte() {
        return mbyte;
    }

    public void setMbyte(byte[] mbyte) {
        this.mbyte = mbyte;
    }

    public int getFlowChunkNumber() {
        return flowCount;
    }

    public void setFlowChunkNumber(int flowChunkNumber) {
        this.flowChunkNumber = flowChunkNumber;
    }

    public int getFlowChunkSize() {
        return flowChunkSize;
    }

    public void setFlowChunkSize(int flowChunkSize) {
        this.flowChunkSize = flowChunkSize;
    }

    public int getFlowCount() {
        return flowCount;
    }

    public void setFlowCount(int flowCount) {
        this.flowCount = flowCount;
    }

    public long getFileCountSize() {
        return fileCountSize;
    }

    public void setFileCountSize(long fileCountSize) {
        this.fileCountSize = fileCountSize;
    }

    public long getEndFlowChunkSize() {
        return endFlowChunkSize;
    }

    public void setEndFlowChunkSize(long endFlowChunkSize) {
        this.endFlowChunkSize = endFlowChunkSize;
    }
}
