package com.kavya.unigo.ui.features.workers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.kavya.unigo.utils.NotificationHelper;

public class AssignmentReminder extends Worker {
    public AssignmentReminder(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private String uid;

    @NonNull
    @Override
    public Result doWork() {

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("general_notify", Context.MODE_PRIVATE);
        boolean assignmentenabled = prefs.getBoolean("assign_notify", true);
        boolean generalEnabled = prefs.getBoolean("general_notify", true);

        if (!generalEnabled || !assignmentenabled) {
            return Result.success();
        }


        if (auth.getCurrentUser() == null) {
            return Result.success();
        }

        try {

            uid = auth.getCurrentUser().getUid();

            QuerySnapshot snapshot = Tasks.await(
                    db.collection("users")
                            .document(uid)
                            .collection("assignments")
                            .get()
            );

            long currTime = System.currentTimeMillis();

            for (DocumentSnapshot doc : snapshot.getDocuments()) {

                String title = doc.getString("title");
                String dueDateStr = doc.getString("dueDate");
                if (dueDateStr == null) continue;

                long dueDate = Long.parseLong(dueDateStr);
                long diff = dueDate - currTime;
                long hours = diff / (1000 * 60 * 60);

                Log.d("AssignmentWorker", "Hours left: " + hours);
                if (hours <= 24 && hours > 23) {

                    NotificationHelper.showNotification(
                            getApplicationContext(),
                            NotificationHelper.CHANNEL_ASSIGNMENT,
                            "Assignment Due",
                            title + " assignment due in 24 hours"
                    );
                }
            }

            return Result.success();

        } catch (Exception e) {
            return Result.retry();
        }
    }
}
