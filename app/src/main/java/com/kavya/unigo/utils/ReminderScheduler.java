package com.kavya.unigo.utils;

import android.content.Context;
import android.util.Log;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.kavya.unigo.ui.features.workers.AssignmentReminder;
import com.kavya.unigo.ui.features.workers.AttendacneReminder;
import com.kavya.unigo.ui.features.workers.AttendanceStatusWorker;
import com.kavya.unigo.ui.features.workers.QuotesWorker;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class ReminderScheduler {

    public static void scheduleAll(Context context) {
        scheduleAssignmentReminder(context);
        scheduleAttendacneReminder(context);
        scheduleMotivationWorkder(context);
        scheduleBunkReminder(context);
    }

    private static void scheduleAssignmentReminder(Context context) {

//        // worker will run every 12hr...

        Log.d("AssignmentWorker", "workerScheduled");
        PeriodicWorkRequest assignmentReminder = new PeriodicWorkRequest.Builder(AssignmentReminder.class, 12, TimeUnit.HOURS).build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork("assignment_reminder", ExistingPeriodicWorkPolicy.UPDATE, assignmentReminder);

//        OneTimeWorkRequest testWorker = new OneTimeWorkRequest.Builder(AssignmentReminder.class).build();
//        WorkManager.getInstance(context).enqueue(testWorker);

    }

    private static void scheduleAttendacneReminder(Context context) {
        Log.d("AttendanceScheduler", "attendanceworkder Scheduled");
        Calendar now = Calendar.getInstance();    // this gets current time...
        Calendar sixPm = Calendar.getInstance();    // this represents 6pm of every day...

        sixPm.set(Calendar.HOUR_OF_DAY, 18);
        sixPm.set(Calendar.MINUTE, 0);
        sixPm.set(Calendar.SECOND, 0);

        if (sixPm.before(now)) {      // this check when the user opens up the app if they opens after 6pm then move the sixPm to nextday..
            sixPm.add(Calendar.DAY_OF_MONTH, 1);
        }
        long delay = sixPm.getTimeInMillis() - now.getTimeInMillis();   // calculating the delay till next 6pm...

        // worker ....
        PeriodicWorkRequest attedanceReminder = new PeriodicWorkRequest.Builder(AttendacneReminder.class, 1, TimeUnit.DAYS).setInitialDelay(delay, TimeUnit.MILLISECONDS).build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork("attendance_reminder", ExistingPeriodicWorkPolicy.UPDATE, attedanceReminder);

    }

    private static void scheduleMotivationWorkder(Context context) {
        Log.d("Motivation Scheduler", "motivationWorker Scheduled");
        Calendar now = Calendar.getInstance();
        Calendar morning = Calendar.getInstance();

        morning.set(Calendar.HOUR_OF_DAY, 8);
        morning.set(Calendar.MINUTE, 0);
        morning.set(Calendar.SECOND, 0);

        if (morning.before(now)) {
            morning.add(Calendar.DAY_OF_MONTH, 1);
        }

        long delay = morning.getTimeInMillis() - now.getTimeInMillis();

        PeriodicWorkRequest quotesScheduler = new PeriodicWorkRequest.Builder(QuotesWorker.class, 1, TimeUnit.DAYS).setInitialDelay(delay, TimeUnit.MILLISECONDS).build();
        WorkManager.getInstance(context).enqueueUniquePeriodicWork("quotes_reminder", ExistingPeriodicWorkPolicy.UPDATE, quotesScheduler);

    }

    private static void scheduleBunkReminder(Context context) {
        Log.d("Bunk Scheduler", "BunkWorker Scheduled");
        Calendar now = Calendar.getInstance();
        Calendar eightAm = Calendar.getInstance();

        eightAm.set(Calendar.HOUR_OF_DAY, 8);
        eightAm.set(Calendar.MINUTE, 0);
        eightAm.set(Calendar.SECOND, 0);

        if (eightAm.before(now)) {
            eightAm.add(Calendar.DAY_OF_MONTH, 1);
        }

        long delay = eightAm.getTimeInMillis() - now.getTimeInMillis();

//        OneTimeWorkRequest testreq = new OneTimeWorkRequest.Builder(AttendanceStatusWorker.class).build();
//        WorkManager.getInstance(context).enqueue(testreq);

        PeriodicWorkRequest bunkScheduler = new PeriodicWorkRequest.Builder(AttendanceStatusWorker.class, 1, TimeUnit.DAYS).setInitialDelay(delay, TimeUnit.MILLISECONDS).build();
        WorkManager.getInstance(context).enqueueUniquePeriodicWork("bunk_reminder", ExistingPeriodicWorkPolicy.UPDATE, bunkScheduler);
    }

}
