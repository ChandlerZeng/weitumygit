//package com.libtop.weituR.http.old_del;
//
//import org.apache.http.HttpEntity;
//import org.apache.http.entity.HttpEntityWrapper;
//
//import java.io.FilterOutputStream;
//import java.io.IOException;
//import java.io.OutputStream;
//
///**
// * Created by Administrator on 2016/1/15 0015.
// */
//@Deprecated
//public class ProgressOutEntity extends HttpEntityWrapper {
//    private ProgressListener mListener;
//    public ProgressOutEntity(HttpEntity wrapped,ProgressListener listener) {
//        super(wrapped);
//        mListener=listener;
//    }
//
//    @Override
//    public void writeTo(OutputStream outstream) throws IOException {
//        this.wrappedEntity.writeTo(outstream instanceof CountingOutputStream ? outstream
//                : new CountingOutputStream(outstream, mListener));
//    }
//
//    public interface ProgressListener {
//        public void transferred(long transferedBytes);
//    }
//
//    public static class CountingOutputStream extends FilterOutputStream{
//
//        /**
//         * Constructs a new {@code FilterOutputStream} with {@code out} as its
//         * target stream.
//         *
//         * @param out the target stream that this stream writes to.
//         */
//        private ProgressListener listener;
//        private long current;
//
//        public CountingOutputStream(OutputStream out,ProgressListener listener) {
//            super(out);
//            this.listener=listener;
//            this.current=0;
//        }
//
//        @Override
//        public void write(int oneByte) throws IOException {
////            super.write(oneByte);
//            out.write(oneByte);
//            current++;
//            listener.transferred(current);
//        }
//
//
//        @Override
//        public void write(byte[] buffer, int offset, int length) throws IOException {
////            super.write(buffer, offset, length);
//            out.write(buffer, offset, length);
//            current+=length;
//            listener.transferred(current);
//        }
//    }
//}
