package com.libtop.weituR.eventbus;

import android.os.Bundle;

/**
 * Created by LianTu on 2016/4/26.
 */
public class MessageEvent {
    public final Bundle message;

    public MessageEvent(Bundle message){
        this.message = message;
    }
}
