package com.pumasi.surbay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Tools {
    public final long time_hour = 60 * 60 * 1000;
    public final long time_day = 60 * 60 * 24 * 1000;

    @SuppressLint("SimpleDateFormat")
    public String convertTimeZone(Context context, String time, String format) {
        String form = context.getResources().getString(R.string.date_format);
        SimpleDateFormat inputFormat = new SimpleDateFormat
                (form, Locale.KOREA);
        inputFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        SimpleDateFormat outputFormat = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = inputFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String outputText = "";
        try {
            outputText = outputFormat.format(date);
        } catch (Exception e) {
            Log.d("datedate", "convertTimeZone: " + date);
        }
        return outputText;
    }
    @SuppressLint("SimpleDateFormat")
    public String convertTimeZone(Context context, Date time, String format_string) {
        String form = context.getResources().getString(R.string.date_format);

        SimpleDateFormat input = new SimpleDateFormat(form);
        SimpleDateFormat fm = new SimpleDateFormat(form, Locale.KOREA);
        fm.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
        SimpleDateFormat output = new SimpleDateFormat(format_string);

        String time_string = input.format(time);

        Date date = null;
        try {
            date = fm.parse(time_string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String result = output.format(date);
        Log.d("convertime_zone", String.valueOf(date));

        return result;
    }
    public int dayCompare(long time2, long time) {
        SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd\\'T\\'kk:mm:ss.SSS");
        int day2 = (int) (time2 / time_day);
        int day = (int) (time / time_day);
        Log.d("TAG", "dayCompare: " + fm.format(time2) + ", " + fm.format(time));
        Log.d("TAG", "dayCompare: " + day2 + ", " + day);
        return day2 - day;
    }
    public long toUTC(long local) {
        return local - 9 * 60 * 60 * 1000;
    }
    public long toLocal(long UTC) {
        return UTC + 9 * 60 * 60 * 1000;
    }
}
