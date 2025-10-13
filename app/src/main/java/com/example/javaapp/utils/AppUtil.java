package com.example.javaapp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AppUtil {
    /**
     * Converts a string representing a timestamp (milliseconds since epoch) to a yyyy-MM-dd formatted string.
     * Always returns a valid date string (never null).
     */
    public static String stringToLocalDate(String timestampStr) {
        try {
            long millis = Long.parseLong(timestampStr);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            return sdf.format(new Date(millis));
        } catch (Exception e) {
            // Return current date if parsing fails
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            return sdf.format(new Date());
        }
    }
}
