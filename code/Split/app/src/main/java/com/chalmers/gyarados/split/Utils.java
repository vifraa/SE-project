package com.chalmers.gyarados.split;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

class Utils {


    public static String formatDateTime(Date date) {

            if(date!=null){
                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                //format.setTimeZone(TimeZone.getDefault());
                //format.applyPattern("HH:mm");
                return format.format(date);
            }else{
                return  "";
            }
    }

}
