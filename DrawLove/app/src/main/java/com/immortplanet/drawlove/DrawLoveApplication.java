package com.immortplanet.drawlove;

import android.app.Application;

import java.net.CookieHandler;
import java.net.CookieManager;

/**
 * Created by tom on 5/1/17.
 */

public class DrawLoveApplication extends Application {

//    public static String DOMAIN = "http://drawlove.immortplanet.com/api";
    public static String DOMAIN = "http://192.168.1.9:8083/api";


    @Override
    public void onCreate(){
        super.onCreate();
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
    }
}
