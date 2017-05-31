package com.immortplanet.drawlove.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.immortplanet.drawlove.R;

/**
 * Created by tom on 5/31/17.
 */

public class ImagePopupDialog extends Dialog {
    LayoutInflater inflater;

    ImageView imgContent;

    Bitmap content;

    public ImagePopupDialog(@NonNull Context context, Bitmap content) {
        super(context);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.content = content;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = inflater.inflate(R.layout.dialog_image_popup, null);
        imgContent = (ImageView) rootView.findViewById(R.id.imgContent);
        imgContent.setImageBitmap(content);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(rootView);
    }
}
