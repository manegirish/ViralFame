package com.technoindians.library;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author
 * Girish Mane <girishmane8692@gmail.com>
 * Created on 26/04/2016
 * Last modified 17/07/2016
 */

public class TimeConverter {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    private static final int MONTHS_MILLIS = 30 * DAY_MILLIS;

    @SuppressLint("SimpleDateFormat")
    protected static long getCurrentTimestamp() throws java.text.ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm a");
        Date now = new Date();
        String strDate = sdf.format(now);
        Date date = sdf.parse(strDate);
        long dateInMillis = date.getTime();
        return dateInMillis;
    }

    public static String getWallTime(long time) {
        if (time < 1000000000000L) {
            time *= 1000;
        }
        long now=0;
        try {
            now = getCurrentTimestamp();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        if (time > now || time <= 0) {
            return null;
        }
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "1 min";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " min";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "1 hr";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hr";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "1 d";
        } else {
            return getDayAgo(time);
        }
    }

    public static String getDayAgo(long time) {
        if (time < 1000000000000L) {
            time *= 1000;
        }
        long now = 0;
        try {
            now = getCurrentTimestamp();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        if (time > now || time <= 0) {
            return null;
        }
        final long diff = now - time;
        if ((diff / DAY_MILLIS)>8){
            return getDate(time);
        }else {
            return diff / DAY_MILLIS + " d";
        }
    }
    protected static String getDate(long time){
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy");
        Date result_date = new Date(time);
        return sdf.format(result_date);
    }
}
