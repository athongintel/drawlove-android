package com.immortplanet.drawlove;

import android.app.Application;

import java.net.CookieHandler;
import java.net.CookieManager;

/**
 * Created by tom on 5/1/17.
 */

public class DrawLoveApplication extends Application {

    public static String DOMAIN = "http://drawlove.immortplanet.com";
//    public static String DOMAIN = "http://10.0.2.2:8083";


    @Override
    public void onCreate(){
        super.onCreate();
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
    }
}
