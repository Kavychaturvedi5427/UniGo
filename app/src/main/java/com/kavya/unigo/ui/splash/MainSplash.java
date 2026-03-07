package com.kavya.unigo.ui.splash;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.kavya.unigo.ui.auth.ChooseAuth;
import com.kavya.unigo.R;
import com.kavya.unigo.ui.features.workers.AssignmentReminder;
import com.kavya.unigo.utils.NotificationHelper;

import java.util.concurrent.TimeUnit;

public class MainSplash extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.main_splash);

        NotificationHelper.createChannel(this); // this will create the channel once and eveytime app restart or opens up again android will check whether channel for this app exists or not
        // if no then it will create it otherwise not ...

        // asks for the user permission to send notification...
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {    // this check whether the android version of the device is 13+ or not

            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        101
                );
            }
        }

        AppCompatImageView logo = findViewById(R.id.logo);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade);
        logo.post(() -> logo.startAnimation(animation));

        ProgressBar progressBar = findViewById(R.id.Progress);
        progressBar.setVisibility(View.VISIBLE);
        Handler handler = new Handler();

        handler.postDelayed(() -> {
            Intent intent = new Intent(getApplicationContext(), ChooseAuth.class);
            startActivity(intent);

            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        }, 2000);


    }
}
