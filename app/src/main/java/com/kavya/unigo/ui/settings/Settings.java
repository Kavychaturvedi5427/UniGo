package com.kavya.unigo.ui.settings;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.browser.customtabs.CustomTabsIntent;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kavya.unigo.R;
import com.kavya.unigo.databinding.SettingsDialogBinding;
import com.kavya.unigo.ui.about.AboutApp;
import com.kavya.unigo.ui.auth.ChooseAuth;
import com.kavya.unigo.ui.features.EditProfile.Editprofile;
import com.kavya.unigo.ui.landing.ProfileUpdatedListener;

public class Settings extends AppCompatActivity {
    private SettingsDialogBinding binding;
    private ImageView back;
    private SwitchMaterial generalNotify, AssignNotify, notesNotify, AttendanceNotify;
    private AppCompatButton editProf, ResetPass, DeleteAcc, AboutUni, contactDev, PriPol, termsOFuse, signout;
    private ProgressBar progressBar;
    private SharedPreferences prefs;
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SettingsDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        prefs = getSharedPreferences("notification_settings",MODE_PRIVATE);

        // binding viewGroups...
        back = binding.back;
        generalNotify = binding.switchGeneralNotification;
        AssignNotify = binding.switchAssignNotification;
        notesNotify = binding.switchNoteNotification;
        AttendanceNotify = binding.switchAttendNotification;
        editProf = binding.editProfileBtn;
        ResetPass = binding.resetPasswordBtn;
        DeleteAcc = binding.deleteAccountBtn;
        AboutUni = binding.aboutAppBtn;
        contactDev = binding.contactDevBtn;
        PriPol = binding.privacyPolicyBtn;
        termsOFuse = binding.termsBtn;
        signout = binding.signOutBtn;
        progressBar = binding.progress;

        // back function...
        back.setOnClickListener(v -> {
            finish();
        });

        // notification settings...
        // --> fetching the previous state of the switches...
        generalNotify.setChecked(prefs.getBoolean("general_notify",true));
        AssignNotify.setChecked(prefs.getBoolean("assign_notify",true));
        notesNotify.setChecked(prefs.getBoolean("notes_notify", true));
        AttendanceNotify.setChecked(prefs.getBoolean("attendance_notify", true));

        // saving changes when user check toggles a switch...
        generalNotify.setOnCheckedChangeListener((buttonView, isChecked) -> {

            prefs.edit().putBoolean("general_notify", isChecked).apply();

            if(!isChecked){

                // turn off all switches
                AssignNotify.setChecked(false);
                notesNotify.setChecked(false);
                AttendanceNotify.setChecked(false);

                // save preferences
                prefs.edit()
                        .putBoolean("assign_notify", false)
                        .putBoolean("notes_notify", false)
                        .putBoolean("attendance_notify", false)
                        .apply();

                // disable switches
                AssignNotify.setEnabled(false);
                notesNotify.setEnabled(false);
                AttendanceNotify.setEnabled(false);

            }else{

                // enable switches again
                AssignNotify.setEnabled(true);
                notesNotify.setEnabled(true);
                AttendanceNotify.setEnabled(true);
            }
        });

        AssignNotify.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            Log.d("WorkerTest","Assignment Notification disabled");
            prefs.edit().putBoolean("assign_notify",isChecked).apply();
        }));

        notesNotify.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            Log.d("WorkerTest","Notes Notification disabled");
            prefs.edit().putBoolean("notes_notify",isChecked).apply();
        }));

        AttendanceNotify.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            Log.d("WorkerTest","Attendance Notification disabled");
            prefs.edit().putBoolean("attendance_notify",isChecked).apply();
        }));


        // Account settings...
        editProf.setOnClickListener(v -> {
            Editprofile editprof = new Editprofile(new ProfileUpdatedListener() {
                @Override
                public void onProfileUpdated() {
                    Snackbar.make(binding.getRoot(), "Profile updated", Snackbar.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
            editprof.show(getSupportFragmentManager(), "edit_profile");
        });

        ResetPass.setOnClickListener(v -> {
            if (auth.getCurrentUser() != null) {
                String email = auth.getCurrentUser().getEmail();
                progressBar.setVisibility(View.VISIBLE);
                auth.sendPasswordResetEmail(email).addOnSuccessListener(unused -> {
                    Snackbar.make(binding.getRoot(), "Password reset link sent", Snackbar.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }).addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Snackbar.make(binding.getRoot(), e.getMessage(), Snackbar.LENGTH_SHORT).show();
                });
            }
        });

        DeleteAcc.setOnClickListener(v -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Delete Account").setMessage("Are you sure you want to delete your UniGo account?").setPositiveButton("yes", (dialog1, which) -> {
                progressBar.setVisibility(View.VISIBLE);
                if(auth.getCurrentUser() != null){
                    String uid = auth.getCurrentUser().getUid();

                    // deleting firebase data..
                    db.collection("users").document(uid).delete().addOnSuccessListener(unused -> {
                        // deleting auth account ...
                        auth.getCurrentUser().delete().addOnSuccessListener(unused1 -> {
                            progressBar.setVisibility(View.GONE);
                            Snackbar.make(binding.getRoot(), "Account deleted", Snackbar.LENGTH_SHORT).show();
                            Intent intent = new Intent(this, ChooseAuth.class);
                            // clearing activity stack...
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }).addOnFailureListener(e->{
                            progressBar.setVisibility(View.GONE);
                            Snackbar.make(binding.getRoot(), e.getMessage(), Snackbar.LENGTH_SHORT).show();
                        });

                    }).addOnFailureListener(e->{
                        progressBar.setVisibility(View.GONE);
                        Snackbar.make(binding.getRoot(), e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    });


                }
            }).setNegativeButton("no", (dialog1, which) -> {
                dialog1.dismiss();
            });
            alert.show();
        });

        AboutUni.setOnClickListener(v -> {
            startActivity(new Intent(this, AboutApp.class));
        });

        contactDev.setOnClickListener(v -> {
            openLink("https://kavychaturvedi5427.github.io/UniGo-Legal/contactDev.htm");
        });

        PriPol.setOnClickListener(v -> {
            openLink("https://kavychaturvedi5427.github.io/UniGo-Legal/privacy.htm");
        });

        termsOFuse.setOnClickListener(v -> {
            openLink("https://kavychaturvedi5427.github.io/UniGo-Legal/terms.htm");
        });

        signout.setOnClickListener(v -> {

            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Sign Out").setMessage("Are you sure you want to sign out?").setPositiveButton("yes", ((dialog1, which) -> {
                auth.signOut();

                Toast.makeText(this, "Signed out successfully", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(this, ChooseAuth.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            })).setNegativeButton("no", ((dialog1, which) -> {
                dialog1.dismiss();
            })).show();
        });
    }

    private void openLink(String url) {
        binding.progress.setVisibility(View.VISIBLE);

        // custom chrome tab intent...
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().build();

        customTabsIntent.launchUrl(this, Uri.parse(url));

        binding.progress.setVisibility(View.GONE);

    }
}
