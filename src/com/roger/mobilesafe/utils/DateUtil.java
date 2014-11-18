package com.roger.mobilesafe.utils;

import java.util.Calendar;

/**
 * Created by Roger on 2014/11/15.
 * 时间工具
 */
public class DateUtil {
    /**
     * 获取零点的时间戳
     * @return
     */
    public static long getMorningTime(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    /**
     * 获取月初时间戳
     * @return
     */
    public static long getMonthTime(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.DAY_OF_MONTH,1);
        return cal.getTimeInMillis();
    }

    /**
     * 获取昨天零点时间戳
     * @return
     */
    public static long getYesterdayTime(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, -24);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
}
