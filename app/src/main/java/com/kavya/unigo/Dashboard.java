package com.kavya.unigo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class Dashboard extends AppCompatActivity {

    TextView greetings, welcomeName, CardName, CardPhone, CardEmail, CardUni, CardCollege, CardCourse, tagline;
    CardView AttendanceCard;
    ImageView logout;
    ProgressBar progressBar;
    FirebaseAuth auth;
    FirebaseFirestore db;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        greetings = findViewById(R.id.greetingstxt);
        welcomeName = findViewById(R.id.name);

        CardName = findViewById(R.id.infoname);
        CardPhone = findViewById(R.id.infophone);
        CardEmail = findViewById(R.id.infoemail);
        CardUni = findViewById(R.id.infouni);
        CardCollege = findViewById(R.id.infocollege);
        CardCourse = findViewById(R.id.infocourse);
//        logout = findViewById(R.id.logout);
        progressBar = findViewById(R.id.Progress);
        tagline = findViewById(R.id.tagline);

        AttendanceCard = findViewById(R.id.attendanceCard);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() == null) {
            showMessage("No authenticated user found");
            finish();
            return;
        }

        uid = auth.getCurrentUser().getUid();

        loadUserData();
        loadAttendance();

        AttendanceCard.setOnClickListener(v->{
            AttendanceBtm attendanceBtm = new AttendanceBtm();
            attendanceBtm.show(getSupportFragmentManager(),"attendanceBtm");
        });

//        logout.setOnClickListener(v->{
//            progressBar.setVisibility(View.VISIBLE);
//            auth.signOut();
////            startActivity(new Intent(this, ChooseAuth.class));
//            finish();
//
//        });
    }

    private void loadUserData() {
        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) {
                        showMessage("User profile not found");
                        return;
                    }

                    // Welcome name
                    String fullName = doc.getString("name");
                    if (fullName != null && !fullName.isEmpty()) {
                        welcomeName.setText(fullName);
                    } else {
                        welcomeName.setText("User");
                    }

                    // Profile card
                    CardName.setText("Name: " + valueOrNA(doc.getString("name")));
                    CardPhone.setText("Phone: " + valueOrNA(doc.getString("phoneNumber")));
                    CardEmail.setText("Email: " + valueOrNA(auth.getCurrentUser().getEmail()));
                    CardUni.setText("University: " + valueOrNA(doc.getString("university")));
                    CardCollege.setText("College: " + valueOrNA(doc.getString("college")));
                    CardCourse.setText("Course: " + valueOrNA(doc.getString("course")));

                    // Show profile setup only if incomplete
                    Boolean profileComplete = doc.getBoolean("profileComplete");
                    if (profileComplete == null || !profileComplete) {
                        new ProfileSetupBtm()
                                .show(getSupportFragmentManager(), "ProfileSetup");
                    }
                })
                .addOnFailureListener(e -> showMessage(e.getMessage()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        getGreeting();
        loadAttendance();
    }

    private void loadAttendance() {
        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists() || !doc.contains("attendance")) {
                        // First-time user (no attendance yet)
                        // You can show placeholders like "---"
                        return;
                    }

                    Object attendanceObj = doc.get("attendance");
                    if (!(attendanceObj instanceof java.util.Map)) return;

                    @SuppressWarnings("unchecked")
                    java.util.Map<String, Object> attendance =
                            (java.util.Map<String, Object>) attendanceObj;

                    long attended = attendance.get("attended") != null
                            ? (long) attendance.get("attended") : 0;

                    long total = attendance.get("total") != null
                            ? (long) attendance.get("total") : 0;

                    int percent = total > 0
                            ? (int) ((attended * 100) / total)
                            : 0;

                    // TODO: bind these to attendance card views
                    // Example:
                    // attendancePercent.setText(percent + "%");
                    // attendanceCount.setText(attended + " / " + total);

                    // TODO: status + lottie logic here

                })
                .addOnFailureListener(e -> showMessage(e.getMessage()));
    }

    private void getGreeting() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);

        if (hour >= 5 && hour < 12) {
            greetings.setText("Good Morning ☀️");
        } else if (hour >= 12 && hour < 17) {
            greetings.setText("Good Afternoon 🌤");
        } else {
            greetings.setText("Good Evening 🌆");
        }
    }

    private String valueOrNA(String value) {
        return value != null && !value.isEmpty() ? value : "Not set";
    }

    private void showMessage(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
