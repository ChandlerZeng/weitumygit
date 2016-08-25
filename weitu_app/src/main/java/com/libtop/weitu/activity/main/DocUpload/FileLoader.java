/*
 * Copyright (C) 2013 Paul Burke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.libtop.weitu.activity.main.DocUpload;

import android.content.Context;
import android.os.FileObserver;
import android.support.v4.content.AsyncTaskLoader;

import com.libtop.weitu.utils.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Loader that returns a list of Files in a given file path.
 * 
 * @version 2013-12-11
 * @author paulburke (ipaulpro)
 */
public class FileLoader extends AsyncTaskLoader<List<File>> {

	private static final int FILE_OBSERVER_MASK = FileObserver.CREATE
			| FileObserver.DELETE | FileObserver.DELETE_SELF
			| FileObserver.MOVED_FROM | FileObserver.MOVED_TO
			| FileObserver.MODIFY | FileObserver.MOVE_SELF;

	private FileObserver mFileObserver;

	private List<File> mData;
	private String mPath;
	private int mSearchInt;
	private File dirPath;
//	ArrayList<File> lists = new ArrayList<File>();



	public FileLoader(Context context, String path) {
		super(context);
		this.mPath = path;
	}

	public FileLoader(Context context,String path,int searchInt){
		super(context);
		this.mPath = path;
		this.mSearchInt = searchInt;
	}

	@Override
	public List<File> loadInBackground() {
		ArrayList<File> list = new ArrayList<File>();
		if (mSearchInt==1){
			final File pathDir = new File(FileChooserActivity.EXTERNAL_BASE_PATH);
			getDirs(pathDir,list);
		}else {
			//		final File pathDir = new File(mPath);
//		getDirs(pathDir);
			// Current directory File instance
			final File pathDir = new File(mPath);
//		final File[] dirs = pathDir.listFiles(FileUtils.sDirFilter);


			// List file in this directory with the directory filter
			final File[] dirs = pathDir.listFiles(FileUtils.sDirFilter);
			if (dirs != null) {
				// Sort the folders alphabetically
				Arrays.sort(dirs, FileUtils.sComparator);
				// Add each folder to the File list for the list adapter
				for (File dir : dirs)
					list.add(dir);
			}

//        // List file in this directory with the file filter
			final File[] files = pathDir.listFiles(fileTypeFilter);
			if (files != null) {
				// Sort the files alphabetically
				Arrays.sort(files, FileUtils.sComparator);
				// Add each file to the File list for the list adapter
				for (File file : files)
					list.add(file);
			}
		}
        return list;
	}

	private void getDirs(File path,ArrayList<File> list){
		final File[] dirs = path.listFiles(FileUtils.sDirFilter);
        if (dirs != null) {
            for (File dir : dirs){
				dirPath = dir;
				getDirs(dir,list);
			}
        }
		final File[] files = path.listFiles(fileTypeFilter);
        if (files != null) {
            // Add each file to the File list for the list adapter
            for (File file : files)
                list.add(file);
        }
	}



	private FileFilter fileTypeFilter = new FileFilter() {
		@Override
		public boolean accept(File file) {
			final String fileName = file.getName();
			// Return files only (not directories) and skip hidden files
			return file.isFile() && !fileName.startsWith(".") && (fileName.endsWith("doc") || fileName.endsWith("xls")
					|| fileName.endsWith("ppt") || fileName.endsWith("docx")
					|| fileName.endsWith("xlsx") || fileName.endsWith("pptx")
					|| fileName.endsWith("pdf") || fileName.endsWith("odt")
					|| fileName.endsWith("ods"));
		}
	};

	@Override
	public void deliverResult(List<File> data) {
		if (isReset()) {
			onReleaseResources(data);
			return;
		}

		List<File> oldData = mData;
		mData = data;

		if (isStarted())
			super.deliverResult(data);

		if (oldData != null && oldData != data)
			onReleaseResources(oldData);
	}

	@Override
	protected void onStartLoading() {
		if (mData != null)
			deliverResult(mData);

		if (mFileObserver == null) {
			mFileObserver = new FileObserver(mPath, FILE_OBSERVER_MASK) {
				@Override
				public void onEvent(int event, String path) {
					onContentChanged();
				}
			};
		}
		mFileObserver.startWatching();

		if (takeContentChanged() || mData == null)
			forceLoad();
	}

	@Override
	protected void onStopLoading() {
		cancelLoad();
	}

	@Override
	protected void onReset() {
		onStopLoading();

		if (mData != null) {
			onReleaseResources(mData);
			mData = null;
		}
	}

	@Override
	public void onCanceled(List<File> data) {
		super.onCanceled(data);

		onReleaseResources(data);
	}

	protected void onReleaseResources(List<File> data) {

		if (mFileObserver != null) {
			mFileObserver.stopWatching();
			mFileObserver = null;
		}
	}

	public File getDirPath() {
		return dirPath;
	}
}