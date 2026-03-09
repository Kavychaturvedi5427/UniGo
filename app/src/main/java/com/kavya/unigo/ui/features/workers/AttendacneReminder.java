package com.kavya.unigo.ui.features.workers;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.kavya.unigo.utils.NotificationHelper;

public class AttendacneReminder extends Worker {

    public AttendacneReminder(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("general_notify",MODE_PRIVATE);
        boolean attendeaceenabled = pref.getBoolean("attendance_notify",true);
        boolean generalEnabled = pref.getBoolean("general_notify", true);
        if(!generalEnabled && !attendeaceenabled){
            return Result.success();
        }

        NotificationHelper.showNotification(getApplicationContext(), NotificationHelper.ATTENDANCE_CHANNEL,"Attendance Reminder", "Don't forget to mark today's attendance");
        return Result.success();
    }
}
