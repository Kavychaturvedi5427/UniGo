package com.kavya.unigo;

import static android.app.ProgressDialog.show;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class ChooseAuth extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.choose_auth);

        AppCompatButton login = findViewById(R.id.Loginbtn);
        AppCompatButton signup = findViewById(R.id.signupBtn);
        ImageView exit = findViewById(R.id.exit);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginBtm login = new LoginBtm();
                login.show(getSupportFragmentManager(),"LoginBtm");
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpBtm signup = new SignUpBtm();
                signup.show(getSupportFragmentManager(),"SignUpBtm");
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ChooseAuth.this, "App Closed", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
