package com.immortplanet.drawlove.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by tom on 5/5/17.
 */

public class NodeDateTime {
    public static String getDateFromID(String _id){
        Calendar calendar = Calendar.getInstance();
        long milliSeconds = Long.parseLong(_id.substring(0, 8), 16)*1000;
        calendar.setTimeInMillis(milliSeconds);
        DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormatter.format(calendar.getTime());
    }
}
