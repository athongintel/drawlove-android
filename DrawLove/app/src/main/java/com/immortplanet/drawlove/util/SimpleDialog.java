package com.immortplanet.drawlove.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

/**
 * Created by tom on 5/1/17.
 */

public class SimpleDialog extends AlertDialog{

    public SimpleDialog(Context context, String title, String message, final DialogInterface.OnClickListener okCallback){
        super(context);
        this.setTitle(title);
        this.setMessage(message);
        this.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", okCallback);
    }
}
