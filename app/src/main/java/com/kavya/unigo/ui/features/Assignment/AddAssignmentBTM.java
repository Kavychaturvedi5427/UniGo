package com.kavya.unigo.ui.features.Assignment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.kavya.unigo.R;
import com.kavya.unigo.databinding.AssignmentBtmBinding;
import com.kavya.unigo.utils.NotificationHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddAssignmentBTM extends BottomSheetDialogFragment {

    private AssignmentBtmBinding binding;
    private long dueDateMillis = 0;
    private AddAssignmentViewModel viewModel;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = AssignmentBtmBinding.inflate(inflater, container, false);
        progressBar = binding.Progress;

        viewModel = new ViewModelProvider(this).get(AddAssignmentViewModel.class);

        binding.date.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            DatePickerDialog datePicker = new DatePickerDialog(
                    requireContext(),
                    (view, year, month, dayOfMonth) -> {

                        Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.set(year, month, dayOfMonth, 0, 0, 0);
                        selectedCalendar.set(Calendar.MILLISECOND, 0);

                        //  Save timestamp
                        dueDateMillis = selectedCalendar.getTimeInMillis();

                        // Format for display
                        SimpleDateFormat sdf =
                                new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

                        binding.date.setText(sdf.format(selectedCalendar.getTime()));
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            datePicker.show();
        });


        binding.saveAssignment.setOnClickListener(v -> {
            String title = binding.titletxt.getText().toString().trim();
            String subject = binding.subjtxt.getText().toString().trim();
            String desc = binding.desctxt.getText().toString().trim();

            // sending data to the Viewmodel for validation...
            viewModel.StoreAssignment(title, subject, desc, dueDateMillis);
            progressBar.setVisibility(View.VISIBLE);
        });

        observeState();

        return binding.getRoot();
    }

    private void observeState() {
        viewModel.getAssignmentState().observe(getViewLifecycleOwner(), state -> {
            if (state instanceof AssignmentState.AssignmentLoading) {
                progressBar.setVisibility(View.VISIBLE);
            }

            else if (state instanceof AssignmentState.AssignmentSuccess) {
                progressBar.setVisibility(View.GONE);
                NotificationHelper.showNotification(requireContext(),NotificationHelper.CHANNEL_ASSIGNMENT,"Assignment added","Assignment added successfully");
                Toast.makeText(getContext(),
                        "Assignment stored successfully.",
                        Toast.LENGTH_SHORT).show();
                dismiss();
            }
            else if (state instanceof AssignmentState.AssignmentError) {
                progressBar.setVisibility(View.GONE);
                String mes = ((AssignmentState.AssignmentError) state).message;
                Toast.makeText(requireContext(), mes, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(d -> {

            FrameLayout bottomSheet =
                    dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);

            if (bottomSheet != null) {
                bottomSheet.setBackground(
                        ContextCompat.getDrawable(requireContext(), R.drawable.custom_btm));
                bottomSheet.setClipToOutline(true);
            }
        });
        return dialog;
    }
}
