package com.kavya.unigo.ui.auth;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
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
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.kavya.unigo.R;
import com.kavya.unigo.databinding.LoginbtmBinding;
import com.kavya.unigo.ui.landing.Dashboard;

public class LoginBtm extends BottomSheetDialogFragment {

    private AppCompatButton login, signup;
    private TextInputEditText email, pass;
    private TextView forgot;
    private ProgressBar progressBar;
    private LoginbtmBinding binding;  // for fetching the layout class
    private LoginViewModel viewModel;

    @Override
    @Nullable
    public View onCreateView(@Nullable LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = LoginbtmBinding.inflate(inflater, container, false);

        // Bind views using ViewBinding.....
        email = binding.emailtxt;
        pass = binding.passtxt;
        progressBar = binding.Progress;
        forgot = binding.forgot;
        login = binding.Loginbtn;
        signup = binding.signupbtn;

        // ViewModel
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        observeLoginState();

        login.setOnClickListener(v -> {
            String em = email.getText() != null ? email.getText().toString().trim() : "";
            String ps = pass.getText() != null ? pass.getText().toString().trim() : "";
            viewModel.login(em, ps);
        });

        signup.setOnClickListener(v -> {
            dismiss();
            new SignUpBtm().show(
                    requireActivity().getSupportFragmentManager(),
                    "SignUp"
            );
        });

        forgot.setOnClickListener(v -> {
            // keep this for now (can be refactored later)
            Toast.makeText(getContext(), "Reset handled separately", Toast.LENGTH_SHORT).show();
        });

        return binding.getRoot();
    }

    private void observeLoginState() {
        viewModel.getLoginState().observe(getViewLifecycleOwner(), state -> {

            // check whether the view model has validated the inputs and proceeds with the firebase call...
            if (state instanceof LoginState.Loading) {
                progressBar.setVisibility(View.VISIBLE);
                login.setEnabled(false);
            }
            // if the input is invalid then show error.....
            else if (state instanceof LoginState.Error) {
                progressBar.setVisibility(View.GONE);
                login.setEnabled(true);
                Toast.makeText(
                        getContext(),
                        ((LoginState.Error) state).message,
                        Toast.LENGTH_LONG
                ).show();
            }
            // if the response by the firebase to the viewmodel is success then viewmodel will also responds success to the ui... and it can move to the dashboard...
            else if (state instanceof LoginState.Success) {
                progressBar.setVisibility(View.GONE);
                login.setEnabled(true);

                Toast.makeText(getContext(), "Login successful", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), Dashboard.class));
                dismiss();
            }
        });
    }

    // BottomSheet styling (UI concern → allowed)
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(d -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) d;
            FrameLayout bottomSheet =
                    bottomSheetDialog.findViewById(
                            com.google.android.material.R.id.design_bottom_sheet
                    );

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
