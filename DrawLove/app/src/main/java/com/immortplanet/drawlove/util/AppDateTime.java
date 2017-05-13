package com.immortplanet.drawlove.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by tom on 5/5/17.
 */

public class AppDateTime {
    public static String getDateFromID(String _id){
        Calendar calendar = Calendar.getInstance();
        long milliSeconds = Long.parseLong(_id.substring(0, 8), 16)*1000;
        calendar.setTimeInMillis(milliSeconds);
        DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormatter.format(calendar.getTime());
    }
    public static String parseJSDate(String datetime){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        try {
            Date date = format.parse(datetime);
            DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
            return dateFormatter.format(date);
        } catch (ParseException e) {
            Date date = new Date();
            DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
            return dateFormatter.format(date);
        }
    }
}
