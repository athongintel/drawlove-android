package com.immortplanet.drawlove.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.immortplanet.drawlove.R;

/**
 * Created by tom on 5/29/17.
 */

public class SliderDialog extends Dialog {

    LayoutInflater inflater;
    int minValue;
    int currentValue;
    int maxValue;
    ObjectCallback listener;

    Button btOK;
    ImageView imgPreview;
    SeekBar seeker;

    Bitmap bitmap;
    Canvas canvas;
    Paint paint;

    public SliderDialog(Context context, String title, int minValue, int currentValue, int maxValue, ObjectCallback listener) {
        super(context);
        setTitle(title);
        this.minValue = minValue;
        this.currentValue = currentValue < minValue? minValue : currentValue > maxValue? maxValue : currentValue;
        this.maxValue = maxValue;
        this.listener = listener;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = inflater.inflate(R.layout.stroke_slider_dialog, null);
        setContentView(rootView);

        btOK = (Button) rootView.findViewById(R.id.btOK);
        imgPreview = (ImageView) rootView.findViewById(R.id.imgPreview);
        seeker = (SeekBar) rootView.findViewById(R.id.seekValue);

        seeker.setMax(maxValue - minValue);
        seeker.setProgress(currentValue - minValue);

        bitmap = Bitmap.createBitmap(40, 40, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        canvas.drawColor(Color.WHITE);
        canvas.drawCircle(20, 20, currentValue/2 - 1, paint);
        imgPreview.setImageBitmap(bitmap);

        seeker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //-- set imgPreview
                currentValue = progress + minValue;
                canvas.drawColor(Color.WHITE);
                canvas.drawCircle(20, 20, currentValue/2 - 1, paint);
                imgPreview.setImageBitmap(bitmap);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                listener.callback(currentValue);
            }
        });
    }
}
