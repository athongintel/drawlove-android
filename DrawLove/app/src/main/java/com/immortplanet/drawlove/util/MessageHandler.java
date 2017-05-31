package com.immortplanet.drawlove.util;

import android.os.Handler;

/**
 * Created by tom on 5/31/17.
 */

public class MessageHandler {
    Handler handler;
    String event;
    int loop;

    public MessageHandler(String event, Handler handler, int loop){
        this.event = event;
        this.handler = handler;
        this.loop = loop;
    }
}
