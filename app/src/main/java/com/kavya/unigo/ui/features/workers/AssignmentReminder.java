package com.kavya.unigo.ui.features.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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

        if (auth.getCurrentUser() == null) {
            return Result.success();
        }

        uid = auth.getCurrentUser().getUid();
        db.collection("users").document(uid).collection("assignments").get().addOnSuccessListener(queryDocumentSnapshots -> {
            long currTime = System.currentTimeMillis();

            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                String title = doc.getString("title");
                Long dueDate = Long.parseLong(doc.getString("dueDate"));

                if(dueDate == null) continue;

                long diff = dueDate - currTime;
                long hours = diff / (1000 * 60 * 60);

                if(hours <= 24 && hours > 0){
                    NotificationHelper.showNotification(getApplicationContext(),NotificationHelper.CHANNEL_ASSIGNMENT,"Assignment Due",title + " assignment due in 24 hours");
                }
            }
        });

        return Result.success();
    }
}
