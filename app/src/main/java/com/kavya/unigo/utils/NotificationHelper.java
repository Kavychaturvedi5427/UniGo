package com.kavya.unigo.utils;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.kavya.unigo.R;

public class NotificationHelper {
    public static final String CHANNEL_ASSIGNMENT = "assignment";
    public static final String CHANNEL_NOTES = "notes";
    public static final String GENERAL_CHANNEL = "general";
    public static final String ATTENDANCE_CHANNEL = "attendance";

    public static void createChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = context.getSystemService(NotificationManager.class);

            // creating channel for assignment feature....
            NotificationChannel assignmentChannel = new NotificationChannel(CHANNEL_ASSIGNMENT, "Assignment Notification", NotificationManager.IMPORTANCE_HIGH);
            NotificationChannel notesChannel = new NotificationChannel(CHANNEL_NOTES, "Notes Notification", NotificationManager.IMPORTANCE_HIGH);
            NotificationChannel generalChannel = new NotificationChannel(GENERAL_CHANNEL, "General Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationChannel attendanceCannel = new NotificationChannel(ATTENDANCE_CHANNEL, "Attendance Notification", NotificationManager.IMPORTANCE_HIGH);

            manager.createNotificationChannel(assignmentChannel);
            manager.createNotificationChannel(notesChannel);
            manager.createNotificationChannel(generalChannel);
            manager.createNotificationChannel(attendanceCannel);
        }
    }

    public static void showNotification(Context context, String channel, String title, String message) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channel)
                .setSmallIcon(R.drawable.notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);       // this makes the notification disappear when teh user taps on it....

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED) {

            return; // permission not granted, don't send notification
        }

        NotificationManagerCompat.from(context)
                .notify((int) System.currentTimeMillis(), builder.build());

    }


}
