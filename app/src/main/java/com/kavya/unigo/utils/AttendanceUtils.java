package com.kavya.unigo.utils;

public class AttendanceUtils {
    public static String getBunkMessage(long total, long attended) {
        int required = (int) Math.ceil(total * 0.75);
        long safebunk = attended - required;

        String message;
        if (safebunk > 0) {
            message = "You can skip " + safebunk + "lectures today.";
        } else {
            message = "Don't skip any lectures today.";
        }
        return message;
    }
}
