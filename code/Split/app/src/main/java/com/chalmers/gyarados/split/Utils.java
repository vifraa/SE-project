package com.chalmers.gyarados.split;

import java.text.SimpleDateFormat;
import java.util.Date;

class Utils {


    public static String formatDateTime(Date date) {

            if(date!=null){
                SimpleDateFormat format = new SimpleDateFormat();
                format.applyPattern("HH:mm");
                return format.format(date);
            }else{
                return  "";
            }
    }

}
