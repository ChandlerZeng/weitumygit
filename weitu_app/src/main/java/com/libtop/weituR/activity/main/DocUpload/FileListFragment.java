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

package com.libtop.weituR.activity.main.DocUpload;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.libtop.weitu.R;
import com.libtop.weituR.widget.dialog.AlertDialogSingle;

import java.io.File;
import java.util.List;

/**
 * Fragment that displays a list of Files in a given path.
 * 
 * @version 2013-12-11
 * @author paulburke (ipaulpro)
 */
public class FileListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<List<File>> {

    /**
     * Interface to listen for events.
     */
    public interface Callbacks {
        /**
         * Called when a file is selected from the list.
         *
         * @param file The file selected
         */
        public void onFileSelected(File file);
    }

    private static final int LOADER_ID = 0;
    private static final int QUERY_TIME = 100;

    private FileListAdapter mAdapter;
    private String mPath;
    ListView listView;
    TextView tvTitle;
    TextView tvEmpty;
    TextView tvCommit;
    TextView tvUpFolder;
    ImageView imgBack;

    private Callbacks mListener;
    private Handler handler = new Handler();
    private FileLoader fileLoader;
    private Runnable runnable;
    private AlertDialogSingle alertDialog;



    /**
     * Create a new instance with the given file path.
     *
     * @param path The absolute path of the file (directory) to display.
     * @return A new Fragment with the given file path.
     */
    public static FileListFragment newInstance(String path) {
        FileListFragment fragment = new FileListFragment();
        Bundle args = new Bundle();
        args.putString(FileChooserActivity.PATH, path);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mListener = (Callbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement FileListFragment.Callbacks");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAdapter = new FileListAdapter(getActivity());
        mPath = getArguments() != null ? getArguments().getString(
                FileChooserActivity.PATH) : Environment
                .getExternalStorageDirectory().getAbsolutePath();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOADER_ID, null, this);

        super.onActivityCreated(savedInstanceState);
    }

//    @Override
//    public void onListItemClick(ListView l, View v, int position, long id) {
//        FileListAdapter adapter = (FileListAdapter) l.getAdapter();
//        if (adapter != null) {
//            File file = (File) adapter.getItem(position);
//            mPath = file.getAbsolutePath();
//            mListener.onFileSelected(file);
//        }
//    }
      @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
          LinearLayout mLinearLayout = (LinearLayout) inflater.inflate(
                R.layout.activity_doc_file_list, container, false);
          listView = (ListView) mLinearLayout.findViewById(R.id.lv_doc);
          tvTitle = (TextView) mLinearLayout.findViewById(R.id.title);
          tvUpFolder = (TextView) mLinearLayout.findViewById(R.id.tv_up_folder);
          String[] splits = mPath.split("/");
          if (splits.length!=0){
              String last = splits[splits.length-1];
              tvUpFolder.setText("上一级 > "+last);
          }
          tvUpFolder.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  upFolder();
              }
          });
          tvEmpty = (TextView) mLinearLayout.findViewById(R.id.tv_empty);
          tvCommit = (TextView) mLinearLayout.findViewById(R.id.commit);
          tvTitle.setText("选择文档");
          tvCommit.setText("扫描");
          tvCommit.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  searchDoc();
              }
          });
          imgBack = (ImageView) mLinearLayout.findViewById(R.id.back_btn);
          listView.setAdapter(mAdapter);
          imgBack.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  getActivity().finish();
              }
          });
          listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
              @Override
              public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                  FileListAdapter adapter = (FileListAdapter) parent.getAdapter();
                if (adapter != null) {
                    File file = (File) adapter.getItem(position);
                    mPath = file.getAbsolutePath();
                    mListener.onFileSelected(file);
                }
              }
          });

          return mLinearLayout;
      }

    @Override
    public void onStop() {
        cancelSearch();
        super.onStop();
    }

    private void upFolder() {
        File file = new File(mPath);
        File parent = file.getParentFile();
        if (parent!=null){
            if (FileChooserActivity.EXTERNAL_BASE_PATH.contains(parent.getAbsolutePath())){
                mListener.onFileSelected(parent);
            }else {
                getActivity().onBackPressed();
            }
        }else {
            Toast.makeText(getActivity(),"没有上级目录",Toast.LENGTH_SHORT).show();
        }

    }

    private void searchDoc() {
        getLoaderManager().restartLoader(1,null,this);
        alertDialog = showDocDialog();
        alertDialog.setCancelable(false);


        runnable = new Runnable(){
            @Override
            public void run() {
                // TODO Auto-generated method stub
                // 在此处添加执行的代码
                if (fileLoader!=null) {
                    if (alertDialog.isShowing()&&fileLoader.getDirPath()!=null){
                        String dirPath = fileLoader.getDirPath().getAbsolutePath().replace(FileChooserActivity.EXTERNAL_BASE_PATH,"");
                        alertDialog.setContent(dirPath);
                    }
                }
                handler.postDelayed(this, QUERY_TIME);// 执行this，即runable
            }
        };
        handler.postDelayed(runnable, QUERY_TIME);// 打开定时器，50ms后执行runnable操作


    }

    private AlertDialogSingle showDocDialog() {
        AlertDialogSingle alertDialog =  new AlertDialogSingle(getActivity(),"");
        alertDialog.setCallBack(new AlertDialogSingle.CallBack() {
            @Override
            public void cancel() {
                cancelSearch();
            }
        });
        alertDialog.show();
        return alertDialog;
    }

    private void cancelSearch() {
        if (handler!=null){
            handler.removeCallbacks(runnable);
        }
        if (fileLoader!=null){
            fileLoader.cancelLoad();
        }
    }


//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View layout = super.onCreateView(inflater, container,
//                savedInstanceState);
//        ListView lv = (ListView) layout.findViewById(android.R.id.list);
//        ViewGroup parent = (ViewGroup) lv.getParent();
//
//        // Remove ListView and add CustomView  in its place
//        int lvIndex = parent.indexOfChild(lv);
//        parent.removeViewAt(lvIndex);
//        LinearLayout mLinearLayout = (LinearLayout) inflater.inflate(
//                R.layout.activity_doc_file_list, container, false);
//        ImageView imageView = (ImageView) mLinearLayout.findViewById(R.id.back_btn);
//        TextView tvTitle = (TextView) mLinearLayout.findViewById(R.id.title);
//        tvTitle.setText("选择文档");
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getActivity().onBackPressed();
//            }
//        });
//        parent.addView(mLinearLayout, lvIndex, lv.getLayoutParams());
//        return layout;
//        View rootView = inflater.inflate(R.layout.activity_doc_file_list, container, false);
//        View superview = super.onCreateView(inflater, (ViewGroup) rootView, savedInstanceState);
//        FrameLayout listContainer = (FrameLayout) rootView.findViewById(R.id.listContainer1);
//        listContainer.addView(superview);
//        return rootView;
//        return inflater.inflate(R.layout.activity_doc_file_list,container,false);
//        return super.onCreateView(inflater,container,savedInstanceState);
//        View rootView = inflater.inflate(R.layout.activity_doc_file_list, container, false);
//        View superview = super.onCreateView(inflater, (ViewGroup) rootView, savedInstanceState);
//        return superview;
//    }


    @Override
    public Loader<List<File>> onCreateLoader(int id, Bundle args) {
        if (id==1){
            fileLoader = new FileLoader(getActivity(),mPath,1);
            return fileLoader;
        }else {
            return new FileLoader(getActivity(), mPath);
        }
    }


    @Override
    public void onLoadFinished(Loader<List<File>> loader, List<File> data) {
        if (loader.getId()==1){
            if (alertDialog.isShowing()){
                cancelSearch();
                alertDialog.cancel();
            }
            handler.removeCallbacks(runnable);
        }
        mAdapter.setListItems(data);
        if (data==null || data.size()==0){
            tvEmpty.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }else {
            tvEmpty.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
//        mListVew.setAdapter(mAdapter);
//        mListVew.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                FileListAdapter adapter = (FileListAdapter) parent.getAdapter();
//                if (adapter != null) {
//                    File file = (File) adapter.getItem(position);
//                    mPath = file.getAbsolutePath();
//                    mListener.onFileSelected(file);
//                }
//            }
//        });
//        if (isResumed())
//            setListShown(true);
//        else
//            setListShownNoAnimation(true);
    }

    @Override
    public void onLoaderReset(Loader<List<File>> loader) {
        mAdapter.clear();
    }
}
