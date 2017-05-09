package com.immortplanet.drawlove.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.immortplanet.drawlove.R;

/**
 * Created by tom on 5/3/17.
 */

public class InputDialog {
    String message;

    TextView lbName;
    EditText txtInput;

    AlertDialog alertDialog;

    public InputDialog(Context context, String message, String hint, final CallbackWithData okCallback, final CallbackWithData cancelCallback) {
        this.message = message;
        LayoutInflater li = LayoutInflater.from(context);
        View thisView = li.inflate(R.layout.input_dialog, null);
        lbName = (TextView) thisView.findViewById(R.id.lbName);
        txtInput = (EditText) thisView.findViewById(R.id.txtInput);
        lbName.setText(message);
        txtInput.setHint(hint);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(thisView);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                String text = txtInput.getText().toString();
                                txtInput.setText("");
                                dialog.dismiss();
                                okCallback.callback(text);
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                txtInput.setText("");
                                dialog.cancel();
                                cancelCallback.callback(null);
                            }
                        });

        alertDialog = alertDialogBuilder.create();
    }

    public void show() {
        alertDialog.show();
    }
    public void dismiss() {
        txtInput.setText("");
        alertDialog.dismiss();
    }

}
