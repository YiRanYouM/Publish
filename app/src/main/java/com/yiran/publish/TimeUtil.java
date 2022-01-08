package com.yiran.publish;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
    public static String getTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return simpleDateFormat.format(date);
    }
}
