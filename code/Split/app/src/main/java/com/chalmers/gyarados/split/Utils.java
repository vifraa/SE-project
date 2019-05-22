package com.chalmers.gyarados.split;

import android.util.Log;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

class Utils {

    private static TimeZone timeZone = TimeZone.getDefault();

    public static String formatDateTime(Date date){
        if(date!=null) {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm", new Locale("se" , "SE"));
            format.setTimeZone(timeZone);

            //This solution below is simply a quick solution so that we can create value for our customers in our app Launch country (Sweden)
            //When we expand globally this class will be changed to accommodate additional timezones.
            date.setHours(date.getHours() + 2);
            return format.format(date);
        }
        else {
        }
      
        return "";
        }
    }
