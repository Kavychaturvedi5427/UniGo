package com.kavya.unigo;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginBtm extends BottomSheetDialogFragment {

    AppCompatButton login, signup;
    TextInputEditText email, pass;
    TextView forgot;
    FirebaseAuth auth;
    ProgressBar progressBar;

    @Override
    @Nullable
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle SavedInstanceState) {
        // this inflates my BTM inside the container which design_bottom_sheet which has its transparent background
        View view = inflater.inflate(R.layout.loginbtm, container, false);

        // binding View groups...
        email = view.findViewById(R.id.emailtxt);
        pass = view.findViewById(R.id.passtxt);
        progressBar = view.findViewById(R.id.Progress);
        forgot = view.findViewById(R.id.forgot);

        auth = FirebaseAuth.getInstance();

        login = view.findViewById(R.id.Loginbtn);
        signup = view.findViewById(R.id.signupbtn);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                SignUpBtm signup = new SignUpBtm();
                signup.show(getActivity().getSupportFragmentManager(), "SignUp");
            }
        });

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resethandler();
            }
        });
        return view;
    }

    private void resethandler() {
        Dialog resetDialog = new Dialog(requireContext());
        resetDialog.setContentView(R.layout.reset_dialog);
        resetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        resetDialog.show();

        TextInputEditText resetEmail = resetDialog.findViewById(R.id.emailtxt);
        AppCompatButton confirm = resetDialog.findViewById(R.id.confirmBtn);
        AppCompatButton cancel = resetDialog.findViewById(R.id.cancelBtn);

        confirm.setOnClickListener(v -> {
            String em = resetEmail.getText() != null
                    ? resetEmail.getText().toString().trim()
                    : "";

            if (em.isEmpty()) {
                Toast.makeText(getActivity(), "Email cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(em).matches()) {
                Toast.makeText(getActivity(), "Enter a valid registered email", Toast.LENGTH_SHORT).show();
                return;
            }

            confirm.setEnabled(false);

            auth.sendPasswordResetEmail(em)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(
                                getActivity(),
                                "Reset link sent. Check inbox or spam.",
                                Toast.LENGTH_LONG
                        ).show();
                        resetDialog.dismiss();
                    })
                    .addOnFailureListener(e -> {
                        confirm.setEnabled(true);
                        Toast.makeText(
                                getActivity(),
                                e.getMessage() != null
                                        ? e.getMessage()
                                        : "Failed to send reset email",
                                Toast.LENGTH_LONG
                        ).show();
                    });
        });

        cancel.setOnClickListener(v -> resetDialog.dismiss());
    }


    private void handleLogin() {
        progressBar.setVisibility(View.VISIBLE);
        login.setEnabled(false);


        String em = email.getText() != null ? email.getText().toString().trim() : "";
        String ps = pass.getText() != null ? pass.getText().toString().trim() : "";

        if (em.isEmpty() || ps.isEmpty()) {
            showMessage("You are supposed to enter complete credentials.");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(em).matches()) {
            showMessage("Enter valid email.");
            return;
        }

        auth.signInWithEmailAndPassword(em, ps).addOnSuccessListener(authResult -> {
            FirebaseUser user = authResult.getUser();
            if(user == null || !user.isEmailVerified()){
                showMessage("Please Verify you email.");
                auth.signOut();
                return;
            }
            showMessage("login Successful");
            Intent intent = new Intent(getActivity(),Dashboard.class);
            startActivity(intent);
            dismiss();
        }).addOnFailureListener(e -> showMessage(
                e.getMessage() != null ? e.getMessage() : "Login failed"
        ));
    }

    private void resetProgress() {
        progressBar.setVisibility(View.GONE);
        login.setEnabled(true);
    }

    private void showMessage(String s) {
        resetProgress();
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }


    // this gives access to the actual BTM container where mine is inflated...
    @NonNull
    @Override
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
}

