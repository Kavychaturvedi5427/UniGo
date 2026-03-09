package com.kavya.unigo.ui.features.workers;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.kavya.unigo.utils.NotificationHelper;
import com.kavya.unigo.utils.QuotesProvider;

public class QuotesWorker extends Worker {

    public QuotesWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("general_notify",MODE_PRIVATE);
        boolean enabled = prefs.getBoolean("general_notify",true);

        if(!enabled){
            return Result.success();
        }

        String quote = QuotesProvider.provideQuotes();
        NotificationHelper.showNotification(getApplicationContext(),NotificationHelper.GENERAL_CHANNEL,"Daily Motivation", quote);
        return Result.success();
    }
}
