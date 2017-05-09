package com.immortplanet.drawlove.util;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by tom on 5/6/17.
 */

public class DrawLovePreferences {

    private static String PREF_NAME = "DrawLove-Preferences";

    private SharedPreferences sharedPrefs;

    public static SharedPreferences getInstance(Context context){
        return context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
    }
}
