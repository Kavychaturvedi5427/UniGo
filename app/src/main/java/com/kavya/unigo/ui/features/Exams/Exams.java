package com.kavya.unigo.ui.features.Exams;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.kavya.unigo.R;
import com.kavya.unigo.databinding.ExamScheduleBtmBinding;
import com.google.mlkit.vision.common.InputImage;

public class Exams extends BottomSheetDialogFragment {


    private ExamScheduleBtmBinding binding;
    private ExamsViewModel viewModel;
    private ActivityResultLauncher<String> galleryLauncher;
    private AppCompatButton galleryBtn, send, cancel;
    private ProgressBar progressBar;
    private EditText title, desc;
    private Dialog dialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                showPreviewDialog(uri);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ExamScheduleBtmBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(ExamsViewModel.class);

        galleryBtn = binding.galleryBtn;
        galleryBtn.setOnClickListener(v -> {
            galleryLauncher.launch("image/*");
        });

        observeState();
        return binding.getRoot();
    }

    private void showPreviewDialog(Uri uri) {
        dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.dialog_preview);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        ImageView previewImg = dialog.findViewById(R.id.previewImage);


        send = dialog.findViewById(R.id.saveBtn);
        send.setText("Parse");
        cancel = dialog.findViewById(R.id.cancelBtn);
        progressBar = dialog.findViewById(R.id.Progress);

        title = dialog.findViewById(R.id.titleInput);
        desc = dialog.findViewById(R.id.descriptionInput);
        title.setVisibility(View.GONE);
        desc.setVisibility(View.GONE);

        cancel.setOnClickListener(unused -> {
            dialog.dismiss();
        });

        previewImg.setImageURI(uri);
        send.setOnClickListener(v -> {
//            Toast.makeText(requireContext(), "Will be implemented in the coming days", Toast.LENGTH_SHORT).show();
            // the reason we're providing its context because When an image is selected from the gallery, Android returns a content URI, not a file path.
            //To access the actual image data, Android uses the ContentResolver, which requires a Context.
            //The Context tells Android which ContentProvider should serve the image data.
            Snackbar.make(
                    previewImg,
                    "Exam schedule parsing will be available in the next update.",
                    Snackbar.LENGTH_LONG
            ).show();
        });
        dialog.show();
    }


    private void observeState() {
        viewModel.getParsedState().observe(getViewLifecycleOwner(), schedule -> {
            progressBar.setVisibility(View.GONE);
            dialog.dismiss();
            if (schedule == null || schedule.isEmpty()) {
                binding.noScheduleText.setVisibility(View.VISIBLE);
                binding.scheduleContainer.setVisibility(View.GONE);
            }

            binding.noScheduleText.setVisibility(View.GONE);
            binding.scheduleContainer.setVisibility(View.VISIBLE);

            binding.scheduleContainer.removeAllViews();

            for (String exam : schedule) {
                TextView tv = new TextView(requireContext());
                tv.setText(exam);
                tv.setTextSize(14);
                tv.setPadding(0, 10, 0, 10);

                binding.scheduleContainer.addView(tv);
            }
        });
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(d -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) d;
            FrameLayout bottomsheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);

            if (bottomsheet != null) {
                bottomsheet.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.custom_btm));
            }
            bottomsheet.setClipToOutline(true);
        });

        return dialog;
    }
}
