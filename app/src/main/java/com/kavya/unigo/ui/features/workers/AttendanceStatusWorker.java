package com.kavya.unigo.ui.features.workers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kavya.unigo.utils.AttendanceUtils;
import com.kavya.unigo.utils.NotificationHelper;

import java.util.Map;

public class AttendanceStatusWorker extends Worker {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    String uid;

    public AttendanceStatusWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("general_notify", Context.MODE_PRIVATE);
        boolean generalEnabled = prefs.getBoolean("general_notify", true);
        boolean attendenabled = prefs.getBoolean("attendance_notify", true);

        if (!generalEnabled || !attendenabled) {

            return Result.success();
        }
        if (auth.getCurrentUser() == null) return Result.success();
        try {
            uid = auth.getCurrentUser().getUid();

            // fetching the attendance from the db...
            DocumentSnapshot doc = Tasks.await(
                    db.collection("users").document(uid).get()
            );

            Map<String, String> attendance = (Map<String, String>) doc.get("attendance");
            if (attendance == null) return Result.success();

            String totalstr = attendance.get("total");
            String attendedstr = attendance.get("attended");

            if (totalstr == null || attendedstr == null) {
                return Result.success();
            }

            int total = Integer.parseInt(totalstr), attended = Integer.parseInt(attendedstr);

            String message = AttendanceUtils.getBunkMessage(total, attended);
            NotificationHelper.showNotification(getApplicationContext(), NotificationHelper.ATTENDANCE_CHANNEL, "Attendance Status", message);

        } catch (Exception e) {
            return Result.retry();
        }
        return Result.success();
    }
}
