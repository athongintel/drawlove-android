package com.immortplanet.drawlove.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by tom on 5/5/17.
 */

public class ConfirmDialog {
    AlertDialog dialog;

    public ConfirmDialog(Context context, String message, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        dialog = builder.setMessage(message).setPositiveButton("Yes", okListener)
                .setNegativeButton("No", cancelListener).create();
    }

    public void setCanceledOnTouchOutside(boolean cancel){
        dialog.setCanceledOnTouchOutside(cancel);
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener onCancelListener){
        dialog.setOnCancelListener(onCancelListener);
    }

    public void show(){
        dialog.show();
    }

    public void dismiss(){
        dialog.dismiss();
    }

}
