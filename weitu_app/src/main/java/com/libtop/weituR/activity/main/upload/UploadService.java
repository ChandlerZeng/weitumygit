package com.libtop.weituR.activity.main.upload;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

import io.vov.vitamio.utils.Log;


/**
 * Created by wayne on 11/23/15.
 */
public class UploadService {
    private SocketChannel socketChannel;
    private String host;
    private int port;
    private int countTime = 0;
    private Context mcontext;
    private Handler updataHandler;
    private long mIndex;
    private ProgressBar progress;
    private int type = 1;
    public boolean isWorking = false;
    public int countY = 0;
    // Command command;


    public UploadService(String host, int port, Context context, Handler handler) {
        this.updataHandler = handler;
        this.mcontext = context;
        this.host = host;
        this.port = port;
    }

    public UploadService(String host, int port, Handler handler, int type) {
        this.type = type;
        this.updataHandler = handler;
        this.host = host;
        this.port = port;
    }

    public UploadService(String host, int port, Context context, Handler handler, ProgressBar progress, int type) {
        this.type = type;
        this.progress = progress;
        this.updataHandler = handler;
        this.mcontext = context;
        this.host = host;
        this.port = port;
    }

    public boolean upload(String uid, String fid, File file) {
        InetSocketAddress remote = new InetSocketAddress(host, port);
        try {
            socketChannel = SocketChannel.open();
            socketChannel.socket().setKeepAlive(true);
            socketChannel.socket().setTcpNoDelay(true);

            socketChannel.connect(remote);
            if (!socketChannel.socket().isConnected()) {
                return false;
            }
            ByteBuffer buffer = ByteBuffer.allocateDirect(33);
            buffer.put((byte) type);
            buffer.put(ByteUtils.hexToByte(uid));
            buffer.put(ByteUtils.hexToByte(fid));
            buffer.putLong(file.length());
            buffer.rewind();
            socketChannel.write(buffer);
            buffer.clear();
            buffer = ByteBuffer.allocateDirect(8);
            while (true) {
                long i = socketChannel.read(buffer);
                if (i == -1) {
                    close();
                    break;
                }
                if (i == 0 || buffer.position() < 8) {
                    continue;
                }
                buffer.rewind();
                long index = buffer.getLong();
                mIndex = index;
                buffer.clear();

                if (index == file.length()) {
                    System.out.println("文件已经传完了");
                    close();
                    return true;
                }

                RandomAccessFile randomAccessFile = new RandomAccessFile(file,
                        "r");
                randomAccessFile.seek(index);
                FileChannel fileChannel = randomAccessFile.getChannel();
                buffer = ByteBuffer.allocateDirect(1024 * 10);
                int size = 0;
                while ((size = fileChannel.read(buffer)) != -1) {
                    isWorking = true;
                    buffer.rewind();
                    buffer.limit(size);
                    count(file.length(), 10240);
                    socketChannel.write(buffer);
                    buffer.clear();
                }
                if (updataHandler != null) {
                    Message msg = updataHandler.obtainMessage();
                    msg.what = 4;
                    Bundle b = new Bundle();
                    b.putString("key", fid);
                    msg.setData(b);
                    updataHandler.sendMessage(msg);
                }

                randomAccessFile.close();
                isWorking = false;
                Log.e("上传完毕","");
            }

        } catch (IOException e) {
            isWorking = false;
            e.printStackTrace();
        }
        return false;
    }

    private void close() {
        try {
            socketChannel.close();
            System.out.println("关闭连接");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopSocket() {
        if (socketChannel == null)
            return;
        close();
    }

    /**
     * 计算百分比
     *
     * @param
     * @param length
     */
    private void count(long length, long buffer) {

        mIndex = mIndex + buffer;
        double one = mIndex * 1.0 / length;
        int cc = (int) (one * 100);
        countY = cc;
        if (progress != null)
            progress.setProgress(cc);

        //nt.format(count);
    }

    public static byte[] intToBytes(int value) {
        byte[] byte_src = new byte[4];
        byte_src[3] = (byte) ((value & 0xFF000000) >> 24);
        byte_src[2] = (byte) ((value & 0x00FF0000) >> 16);
        byte_src[1] = (byte) ((value & 0x0000FF00) >> 8);
        byte_src[0] = (byte) ((value & 0x000000FF));
        return byte_src;
    }

}
