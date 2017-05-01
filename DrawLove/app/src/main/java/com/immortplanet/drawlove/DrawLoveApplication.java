package com.immortplanet.drawlove;

import android.app.Application;

import java.net.CookieHandler;
import java.net.CookieManager;

/**
 * Created by tom on 5/1/17.
 */

public class DrawLoveApplication extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
    }
}
