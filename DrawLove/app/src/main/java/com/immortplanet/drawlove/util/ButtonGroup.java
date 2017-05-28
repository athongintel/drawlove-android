package com.immortplanet.drawlove.util;

import android.graphics.Color;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

/**
 * Created by tom on 5/28/17.
 */

public class ButtonGroup {

    ArrayList<View> buttons;
    int selectedBackGroundColor;
    int deselectedBackGroundColor;
    View selectedButton;

    public ButtonGroup(){
        buttons = new ArrayList<>();
        selectedBackGroundColor = Color.GRAY;
        deselectedBackGroundColor = Color.TRANSPARENT;
    }

    public void setSelected(int index){
        for (View b : buttons) {
            b.setBackgroundColor(deselectedBackGroundColor);
        }
        selectedButton = buttons.get(index);
        selectedButton.setBackgroundColor(selectedBackGroundColor);
    }

    public void add(View button, final Button.OnClickListener listener){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //-- change background color
                if (v != selectedButton) {
                    selectedButton = v;
                    for (View b : buttons) {
                        b.setBackgroundColor(deselectedBackGroundColor);
                    }
                    v.setBackgroundColor(selectedBackGroundColor);
                    listener.onClick(v);
                }
            }
        });
        buttons.add(button);
    }
}
