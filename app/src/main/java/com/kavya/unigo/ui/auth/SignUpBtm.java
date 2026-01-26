package com.kavya.unigo.ui.auth;

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
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kavya.unigo.R;
import com.kavya.unigo.data.model.SignUpRes;
import com.kavya.unigo.databinding.SignupBtmBinding;

import java.util.*;

public class SignUpBtm extends BottomSheetDialogFragment {

    private AppCompatButton Signup, Login;
    private TextInputEditText name, email, password, confirmpass;
    ProgressBar progressBar;
    private SignupBtmBinding binding;
    private SignUpViewModel viewModel;

    @Override
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle SavedInstanceState) {
        binding = SignupBtmBinding.inflate(inflater, container, false);

        // binding view groups using ViewBinding....
        Signup = binding.signupbtn;
        Login = binding.Loginbtn;
        progressBar = binding.Progress;
        name = binding.nametxt;
        email = binding.emailtxt;
        password = binding.passtxt;
        confirmpass = binding.confirmpasstxt;

        // connecting Ui with the ViewModel for validation...
        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

        observeSignUpState();

        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.singup(name.getText().toString().trim(), email.getText().toString().trim(), password.getText().toString().trim(), confirmpass.getText().toString().trim());
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
        return binding.getRoot();
    }

    private void observeSignUpState() {
        viewModel.getSignUpState().observe(getViewLifecycleOwner(), signUpState -> {
            if (signUpState instanceof SignUpState.SignUpLoading) {
                progressBar.setVisibility(View.VISIBLE);
                Signup.setEnabled(false);
            } else if (signUpState instanceof SignUpState.SignUpError) {
                showMessage(((SignUpState.SignUpError) signUpState).message);
                Signup.setEnabled(true);
            } else if (signUpState instanceof SignUpState.SingupSuccess) {
                showMessage("\"Account created. Please verify your email before logging in.");
                dismiss();
                Signup.setEnabled(true);
                clearFeild();
            }
        });
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
