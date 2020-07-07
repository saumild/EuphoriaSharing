package com.example.twinkle.project.helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Twinkle on 3/13/2018.
 */
public class LogHelper {
    public static List<String> getDetectionLog() {
        return mDetectionLog;
    }

    // Add a new detection log item.
    public static void addDetectionLog(String log) {
        mDetectionLog.add(LogHelper.getLogHeader() + log);
    }

    // Clear all detection log items.
    public static void clearDetectionLog() {
        mDetectionLog.clear();
    }
    private static List<String> mDetectionLog = new ArrayList<>();

    private static String getLogHeader() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.US);
        return "[" + dateFormat.format(Calendar.getInstance().getTime()) + "] ";
    }
}

