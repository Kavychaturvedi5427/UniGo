package com.kavya.unigo;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

public class SignUpBtm extends BottomSheetDialogFragment {

    private AppCompatButton Signup, Login;
    private TextInputEditText name, email, password, confirmpass;
    ProgressBar progressBar;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore db;

    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle SavedInstanceState) {
        View view = inflater.inflate(R.layout.signup_btm, container, false);

        // binding view groups....
        Signup = view.findViewById(R.id.signupbtn);
        Login = view.findViewById(R.id.Loginbtn);
        progressBar = view.findViewById(R.id.Progress);

        name = view.findViewById(R.id.nametxt);
        email = view.findViewById(R.id.emailtxt);
        password = view.findViewById(R.id.passtxt);
        confirmpass = view.findViewById(R.id.confirmpasstxt);

        // getting instance for cloud firestore db and auth ..
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSignUp();
            }
        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                LoginBtm login = new LoginBtm();
                login.show(getActivity().getSupportFragmentManager(), "LoginBtm");
            }
        });
        return view;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(d -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) d;
            // this fetches the actual dialog which holds the UI
            FrameLayout bottomSheet =
                    bottomSheetDialog.findViewById(
                            com.google.android.material.R.id.design_bottom_sheet
                    );

            // applying custom BTM to it
            if (bottomSheet != null) {
                bottomSheet.setBackground(
                        ContextCompat.getDrawable(
                                requireContext(),
                                R.drawable.custom_btm
                        )
                );
                bottomSheet.setClipToOutline(true);
            }
        });
        return dialog;
    }

    private void handleSignUp() {
        progressBar.setVisibility(View.VISIBLE);
        Signup.setEnabled(false);

        // getting input values from the textlayouts...
        String Name = name.getText() != null ? name.getText().toString().trim() : "";
        String Email = email.getText() != null ? email.getText().toString().trim().replaceAll("\\s+", "").replaceAll("[\\u200B-\\u200D\\uFEFF]", "").toLowerCase() : "";
        String Pass = password.getText() != null ? password.getText().toString().trim() : "";
        String CPass = confirmpass.getText() != null ? confirmpass.getText().toString().trim() : "";

        if (Name.isEmpty() || Email.isEmpty() || Pass.isEmpty() || CPass.isEmpty()) {
            showMessage("You can't leave any of the fields blank.");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {
            showMessage("That’s not even an email… unless Gmail suddenly lowered its standards.");
            return;
        }

        if (!Pass.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@#$%!]{6,}$")) {
            showMessage("That password’s so weak it probably skips leg day.");
            return;
        }

        if (!CPass.equals(Pass)) {
            showMessage("You just set the password and you already forgot it.");
            return;
        }

        auth.createUserWithEmailAndPassword(Email, CPass).addOnSuccessListener(authResult -> {
            user = authResult.getUser();
            if (user == null) {
                showMessage("There's some error, try again.");
                return;
            }
            // fetching the id of the user created....
            String uid = user.getUid();

            // storing the data in a map....
            Map<String, Object> userData = new HashMap<>();
            userData.put("name", Name);
            userData.put("email", Email);
            userData.put("createdAt", com.google.firebase.firestore.FieldValue.serverTimestamp());

            db.collection("users").document(uid).set(userData).addOnSuccessListener(unused -> {
                Toast.makeText(getActivity(), "Data Stored Safely. You can proceed to Login.", Toast.LENGTH_SHORT).show();
                showMessage("Please check you emails to get you email verified.");
                user.sendEmailVerification();
                clearFeild();
                resetProgress();
                dismiss();
            }).addOnFailureListener(e -> {
                showMessage("Account created, but failed to save profile. Try again.");
            });


        }).addOnFailureListener(e->{
            showMessage(e.getMessage() != null
                    ? e.getMessage()
                    : "Signup failed");
        });
    };

    private void resetProgress() {
        progressBar.setVisibility(View.GONE);
        Signup.setEnabled(true);
    }

    private void clearFeild() {
        name.setText("");
        email.setText("");
        password.setText("");
        confirmpass.setText("");
    }

    private void showMessage(String S) {
        Toast.makeText(getActivity(), S, Toast.LENGTH_SHORT).show();
        resetProgress();
    }


}
