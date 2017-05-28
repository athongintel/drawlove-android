package com.immortplanet.drawlove;

import android.app.Application;

import java.net.CookieHandler;
import java.net.CookieManager;

/**
 * Created by tom on 5/1/17.
 */

public class DrawLoveApplication extends Application {

    //    public static String DOMAIN = "http://drawlove.immortplanet.com/api";
    public static String DOMAIN = "10.0.2.2:8083";
//    public static String DOMAIN = "192.168.19.107:8083";
    public static String HTTP_DOMAIN = "http://" + DOMAIN;

    public static String API_DOMAIN = HTTP_DOMAIN + "/api";


    @Override
    public void onCreate() {
        super.onCreate();
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
    }
}
