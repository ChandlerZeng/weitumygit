package com.libtop.weituR.fileloader;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class TxtFileDto {

	public String path;
	public long length;
	public int page;
	private MappedByteBuffer m_mbBuf;

	public void of(File file) throws IOException {
		RandomAccessFile fileReader = new RandomAccessFile(file, "r");
		length = fileReader.length();
	    FileChannel channel = fileReader.getChannel();
	}

	public String read(int line) throws IOException {

		return "";
	}
}
