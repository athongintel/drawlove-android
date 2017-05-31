package com.immortplanet.drawlove.util;

import android.os.Handler;
import android.os.Message;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.immortplanet.drawlove.DrawLoveApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by tom on 5/28/17.
 */

public class SocketSubscribe {

    public static Socket ioSocket = null;
    private static boolean running;
    private static LinkedList<MessageHandler> handlers;

    public static boolean init(){
        try {
            if (ioSocket != null){
                ioSocket.disconnect();
            }
            ioSocket = IO.socket(DrawLoveApplication.HTTP_DOMAIN);
            ioSocket.connect();
            handlers = new LinkedList<>();
            ioSocket.on(Socket.EVENT_MESSAGE, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    String event = (String) args[0];
                    JSONObject jsonObject = (JSONObject) args[1];

                    //-- notify event against subscriber
                    Iterator<MessageHandler> it = handlers.iterator();
                    while (it.hasNext()){
                        MessageHandler handler = it.next();
                        if ((handler != null) && (handler.event.equals(event))) {
                            Message message = new Message();
                            message.obj = jsonObject;
                            handler.handler.sendMessage(message);
                            if (handler.loop > 0){
                                handler.loop--;
                                if (handler.loop <= 0) {
                                    it.remove();
                                }
                            }
                        }
                    }
                }
            });
            running = true;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return running;
    }

    public static void emit(String event, JSONObject obj){
        //construct jsonObject from event and args
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("event", event);
            jsonObject.put("data", obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ioSocket.emit(Socket.EVENT_MESSAGE, jsonObject);
    }

    public static MessageHandler subscribe(String event, int loop, Handler callback){
        MessageHandler handler = new MessageHandler(event, callback, loop);
        handlers.add(handler);
        return handler;
    }

    public static void destroy(){
        running = false;
        ioSocket.disconnect();
    }
}
