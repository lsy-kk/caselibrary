package com.lsykk.caselibrary.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    //指定时间格式
    static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    //返回当前时间的指定字符串格式 yyyy/MM/dd HH:mm:ss
    public static String getTime(){
        String format = dateFormat.format(new Date());
        return format;
    }

    //返回指定时间的指定字符串格式 yyyy/MM/dd HH:mm:ss
    public static String getTime(Date date){
        String format = dateFormat.format(date);
        return format;
    }

}