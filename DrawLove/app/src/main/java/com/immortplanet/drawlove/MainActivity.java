package com.immortplanet.drawlove;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends Activity {

    public static int BACKGROUND_COUNT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //-- set fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        //-- count number of backgrounds
        Resources res = getResources(); //if you are in an activity
        AssetManager am = res.getAssets();
        String fileList[] = new String[0];
        try {
            fileList = am.list("images/backgrounds");
            BACKGROUND_COUNT = fileList.length;
        } catch (IOException e) {
            e.printStackTrace();
        }

        //-- get background object and set wallpaper
        View vwMain = (View) findViewById(R.id.vwMain);
        try {
            Drawable d = Drawable.createFromStream(getAssets().open("images/backgrounds/background_" + getRandom(BACKGROUND_COUNT) + ".jpg"), null);
            vwMain.setBackground(d);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //-- set underline for txtRegister
        TextView txtRegister = (TextView)findViewById(R.id.txtRegister);
        txtRegister.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        //-- set action for register
        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iRegister = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(iRegister);
            }
        });

        //-- set action for login button
        Button btLogin = (Button)findViewById(R.id.btLogin);
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iLogin = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(iLogin);
            }
        });



    }

    //-- return random integer number from 0 to n-1
    private int getRandom(int n) {
        return (int) (Math.random() * n);
    }
}
