package com.kavya.unigo.ui.auth;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.kavya.unigo.databinding.ResetDialogBinding;

public class ForgotPass extends DialogFragment {
    private ResetDialogBinding binding;
    private TextInputEditText email;
    private AppCompatButton cnf, cancel;
    private ResetViewModel viewModel;
    private ProgressBar progress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ResetDialogBinding.inflate(inflater, container, false);
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        email = binding.emailtxt;
        cnf = binding.confirmBtn;
        cancel = binding.cancelBtn;
        progress = binding.Progress;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(ResetViewModel.class);

        observeState();

        cnf.setOnClickListener(v -> {
            String em = email.getText().toString().trim();
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            startActivity(Intent.createChooser(intent, "Open email app"));
            viewModel.handleReset(em);
        });
        cancel.setOnClickListener(v -> {
            dismiss();
        });
    }

    private void observeState() {
        // getViewLifeCycleOwner() --> it is used when we're displaying any dialog or any fragment on top of any activity...
        // otherwise this keyword....
        viewModel.getForgotState().observe(getViewLifecycleOwner(), forgotPassState -> {
            if (forgotPassState instanceof ForgotPassState.Success) {
                progress.setVisibility(View.GONE);
                viewModel.clearState();
                dismiss();

            } else if (forgotPassState instanceof ForgotPassState.Error) {
                Toast.makeText(getContext(), ((ForgotPassState.Error) forgotPassState).mess, Toast.LENGTH_SHORT).show();
            } else if (forgotPassState instanceof ForgotPassState.Loading) {
                progress.setVisibility(View.VISIBLE);
            }
        });
    }

}
