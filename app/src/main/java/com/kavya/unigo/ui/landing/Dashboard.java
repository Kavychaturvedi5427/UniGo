package com.kavya.unigo.ui.landing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kavya.unigo.R;
import com.kavya.unigo.databinding.DashboardBinding;
import com.kavya.unigo.ui.features.Assignment;

import java.util.Calendar;

public class Dashboard extends AppCompatActivity implements AttendanceUpdateListener {

    private TextView greetings, welcomeName, CardName, CardPhone, CardEmail, CardUni, pendingNumber,
            CardCollege, CardCourse, tagline, totallec, attendedlec, unattendedlect, attenPercent, SignIndi, warningtxt;
    private CardView AttendanceCard, userinfocard, assignmentCard;
    private LottieAnimationView attendindi, profileIndi;
    private ImageView logout;
//    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private String uid;

    // Dashboard binding...
    DashboardBinding binding;
    AttendanceUpdateListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        greetings = binding.greetingstxt;
        welcomeName = binding.name;
        warningtxt = binding.warningtext;

        CardName = binding.infoname;
        CardPhone = binding.infophone;
        CardEmail = binding.infoemail;
        CardUni = binding.infouni;
        CardCollege = binding.infocollege;
        CardCourse = binding.infocourse;
//        logout = findViewById(R.id.logout);
//        progressBar = binding.Progress;
        tagline = binding.tagline;
        attendindi = binding.attendindicator;
        profileIndi = binding.Profindi;

//      bind these to attendance card views
        totallec = binding.TotalLectures;
        attendedlec = binding.attended;
        unattendedlect = binding.Absentlecutre;
        attenPercent = binding.percentage;

        AttendanceCard = binding.attendanceCard;
        userinfocard = binding.usercard;
        assignmentCard = binding.AssignmentCard;
        pendingNumber = binding.pendingNumber;

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

        userinfocard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                db.collection("users").document(uid).get().addOnSuccessListener(doc -> {
                            Boolean pfComplete = doc.getBoolean("pfComplete");
                            if (pfComplete == null || !pfComplete) {
                                new ProfileSetupBtm()
                                        .show(getSupportFragmentManager(), "ProfileSetup");
                            }
                        })
                        .addOnFailureListener(e -> showMessage(e.getMessage()));
                return true;
            }
        });

        AttendanceCard.setOnClickListener(v -> {
            AttendanceBtm attendanceBtm = new AttendanceBtm();
            // pass reference of this activity to the btm to tell that this is the one who's updating the data...
            attendanceBtm.SetattendanceUpdateListerenr(this);
            attendanceBtm.show(getSupportFragmentManager(), "attendanceBtm");
        });

        assignmentCard.setOnClickListener(v -> {
            Intent intent = new Intent(this, Assignment.class);
            startActivity(intent);
        });

//        SignIndi.setText();

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
                        profileIndi.setAnimation(R.raw.yellowindi);
                        warningtxt.setVisibility(View.VISIBLE);
                        new ProfileSetupBtm()
                                .show(getSupportFragmentManager(), "ProfileSetup");
                    } else {
                        profileIndi.setAnimation(R.raw.complete);
                    }
                })
                .addOnFailureListener(e -> showMessage(e.getMessage()));

        db.collection("users").document(uid).collection("assignments")
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    int pendingAssignment = queryDocumentSnapshots.size();
                    if (pendingAssignment == 0) {
                        pendingNumber.setText("No assignment pending");
                    } else if (pendingAssignment == 1) {
                        pendingNumber.setText("1 assignment pending");
                    } else {
                        pendingNumber.setText(String.valueOf(pendingAssignment) + " assignments pending");
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

                });

    }

    @Override
    protected void onPause() {
        super.onPause();
        attendindi.pauseAnimation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getGreeting();
    }

    private void loadAttendance() {
        db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists() || !doc.contains("attendance")) {
                        // First-time user (no attendance yet)
                        return;
                    }

                    Object attendanceObj = doc.get("attendance");
                    if (!(attendanceObj instanceof java.util.Map)) return;

                    // type casting the attendanceObj into Map....
                    java.util.Map<String, Object> attendance =
                            (java.util.Map<String, Object>) attendanceObj;

                    // fetching the attendance value ...
                    long attended = attendance.get("attended") != null
                            ? (long) attendance.get("attended") : 0;

                    long total = attendance.get("total") != null
                            ? (long) attendance.get("total") : 0;

                    long unattended = total - attended;

                    int percent = total > 0
                            ? (int) ((attended * 100) / total)
                            : 0;


                    totallec.setText("Total Lectures: " + total);
                    attendedlec.setText("Attended Lectures: " + attended);
                    unattendedlect.setText("Unattended Lectures: " + unattended);
                    attenPercent.setText("Attendance Percentage: " + percent + "%");

                    // status + lottie logic here
                    if (percent >= 90) {
                        attendindi.setAnimation(R.raw.greenindi);
                    } else if (percent >= 75) {
                        attendindi.setAnimation(R.raw.yellowindi);
                    } else {
                        attendindi.setAnimation(R.raw.redindi);
                    }
                    attendindi.playAnimation();

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

    @Override
    public void onUpdate() {
        loadAttendance();
    }
}
