package com.kavya.unigo.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.kavya.unigo.ui.auth.ChooseAuth;
import com.kavya.unigo.R;

public class MainSplash extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.main_splash);

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
