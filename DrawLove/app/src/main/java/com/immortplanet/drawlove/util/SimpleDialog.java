package com.immortplanet.drawlove.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

/**
 * Created by tom on 5/1/17.
 */

public class SimpleDialog {
    AlertDialog alertDialog;

    public SimpleDialog(Context context, String title, String message, final DialogInterface.OnClickListener okCallback) {
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", okCallback);
    }
    public void show(){
        alertDialog.show();
    }
    public void dismiss(){
        alertDialog.dismiss();
    }
}
