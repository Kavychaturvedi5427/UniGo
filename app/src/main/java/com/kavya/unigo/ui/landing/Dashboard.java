package com.kavya.unigo.ui.landing;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kavya.unigo.R;
import com.kavya.unigo.databinding.DashboardBinding;
import com.kavya.unigo.ui.about.AboutApp;
import com.kavya.unigo.ui.auth.ChooseAuth;
import com.kavya.unigo.ui.features.Assignment.Assignment;
import com.kavya.unigo.ui.features.Exams.Exams;
import com.kavya.unigo.ui.features.Notes.Notes;
import com.kavya.unigo.ui.features.Notes.NotesRecycler;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Dashboard extends AppCompatActivity implements AttendanceUpdateListener {

    private TextView greetings, welcomeName, CardName, CardPhone, CardEmail, CardUni, pendingNumber,
            CardCollege, CardCourse, totallec, attendedlec, unattendedlect, attenPercent, motivationText, warningtxt, nav_username, nav_useremail;
    private CardView AttendanceCard, userinfocard, assignmentCard, notesCard, examsCard;
    private LottieAnimationView attendindi, profileIndi;
    private ImageView hamberger;
    //    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private String uid;

    // Dashboard binding...
    private DashboardBinding binding;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

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

        motivationText = binding.motivationText;
        attendindi = binding.attendindicator;
        profileIndi = binding.Profindi;

        totallec = binding.TotalLectures;
        attendedlec = binding.attended;
        unattendedlect = binding.Absentlecutre;
        attenPercent = binding.percentage;

        AttendanceCard = binding.attendanceCard;
        userinfocard = binding.usercard;
        assignmentCard = binding.AssignmentCard;
        pendingNumber = binding.pendingNumber;
        notesCard = binding.notesCard;
        examsCard = binding.examCard;

        drawerLayout = binding.drawerLayout;
        navigationView = binding.navigationDrawer;
        hamberger = binding.hamburger;

        hamberger.setOnClickListener(v -> {
            binding.drawerLayout.openDrawer(GravityCompat.START);
        });

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

        String[] quotes = {"Small progress every day adds up to big results.",
                "Push yourself, because no one else will do it for you.",
                "Success is built on daily discipline.",
                "Your future is created by what you do today.",
                "Dream big. Work hard. Stay consistent."
        };
        Random random = new Random();
        binding.motivationText.setText(quotes[random.nextInt(quotes.length)]);

        View headerView = navigationView.getHeaderView(0);

        // fetching them using the db call made by the userinfo card...
        nav_username = headerView.findViewById(R.id.Username);
        nav_useremail = headerView.findViewById(R.id.UserEmail);

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawer(GravityCompat.START);  // this will close the drawer...
            } else if (id == R.id.nav_assignments) {
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(this, Assignment.class));
            } else if (id == R.id.nav_notes) {
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(this, NotesRecycler.class));
            } else if (id == R.id.nav_settings) {
                Snackbar.make(binding.getRoot(), "Settings will be available in the coming days", Snackbar.LENGTH_LONG).show();
            } else if (id == R.id.nav_feedback) {
                Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.feedback_dialog);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                EditText input = dialog.findViewById(R.id.feedbackinput);
                AppCompatButton send = dialog.findViewById(R.id.saveBtn), cancel = dialog.findViewById(R.id.cancelBtn);

                send.setOnClickListener(v -> {
                    String feedback = input.getText().toString().trim();
                    if (feedback.isEmpty()) {
                        input.setError("Please enter feedback");
                        return;
                    }
                    Map<String, Object> map = new HashMap<>();
                    map.put("feedback", feedback);
                    map.put("email", auth.getCurrentUser().getEmail());
                    map.put("uid", auth.getCurrentUser().getUid());
                    map.put("timestamp", FieldValue.serverTimestamp());

                    db.collection("feedback").add(map).addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Thanks for sharing your feedback.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }).addOnFailureListener(e -> {
                        Log.d("feedback error", e.getMessage());
                        Toast.makeText(this, "Failed to send feedback.", Toast.LENGTH_SHORT).show();
                    });
                });

                cancel.setOnClickListener(unused -> {
                    dialog.dismiss();
                });

                dialog.show();
            } else if (id == R.id.nav_about) {
                startActivity(new Intent(this, AboutApp.class));
            } else if (id == R.id.nav_logout) {
                drawerLayout.closeDrawer(GravityCompat.START);
                new AlertDialog.Builder(this)
                        .setTitle("Sign Out")
                        .setMessage("Are you sure you want to sign out?")
                        .setPositiveButton("yes", ((dialog1, which) -> {
                                    auth.signOut();
                                    Toast.makeText(getApplicationContext(), "Signed out successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                        )
                        .setNegativeButton("no", ((dialog1, which) -> {
                            dialog1.dismiss();
                        })).show();
            }
            return true;
        });


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

        notesCard.setOnClickListener(v -> {
            Notes note = new Notes();
            note.show(getSupportFragmentManager(), "AddNotes");
        });

        examsCard.setOnClickListener(v -> {
            new Exams().show(getSupportFragmentManager(), "examsBtm");
        });

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

                    //setting the name and email of the navigation drawer header...
                    nav_username.setText(fullName);
                    nav_useremail.setText(auth.getCurrentUser().getEmail());


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
