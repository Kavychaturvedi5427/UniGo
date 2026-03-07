package com.kavya.unigo.ui.features.workers;

import android.content.Context;

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
        NotificationHelper.showNotification(getApplicationContext(), NotificationHelper.ATTENDANCE_CHANNEL,"Attendance Reminder", "Don't forget to mark today's attendance");
        return Result.success();
    }
}
