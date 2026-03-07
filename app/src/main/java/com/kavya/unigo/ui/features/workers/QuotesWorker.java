package com.kavya.unigo.ui.features.workers;

import android.content.Context;

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
        String quote = QuotesProvider.provideQuotes();
        NotificationHelper.showNotification(getApplicationContext(),NotificationHelper.GENERAL_CHANNEL,"Daily Motivation", quote);
        return Result.success();
    }
}
