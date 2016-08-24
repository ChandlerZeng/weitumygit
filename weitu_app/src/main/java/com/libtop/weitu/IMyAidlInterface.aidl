package com.libtop.weitu;
import com.libtop.weitu.uploadMessage;
// Declare any non-default types here with import statements

interface IMyAidlInterface {

   List<uploadMessage> getMes();

   void setBean(in uploadMessage um);

    List<String> stop();
}
