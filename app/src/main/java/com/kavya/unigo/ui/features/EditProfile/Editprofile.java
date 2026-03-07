package com.kavya.unigo.ui.features.EditProfile;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kavya.unigo.R;
import com.kavya.unigo.databinding.EditUserProfileBinding;
import com.kavya.unigo.ui.landing.ProfileUpdatedListener;

import java.util.Map;

public class Editprofile extends BottomSheetDialogFragment {
    private ProfileUpdatedListener listener;

    public Editprofile(ProfileUpdatedListener listen){
        this.listener = listen;
    }

    private EditUserProfileBinding binding;
    private EditText name, phone, university, college, course;
    private AppCompatButton confirm, cancel;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    String uid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = EditUserProfileBinding.inflate(getLayoutInflater(), container, false);

        name = binding.NameTxt;
        phone = binding.phoneTxt;
        university = binding.uniTxt;
        college = binding.collegeTxt;
        course = binding.courseTxt;
        confirm = binding.confirmbtn;
        cancel = binding.cancelBtn;

        uid = auth.getCurrentUser().getUid();

        db.collection("users").document(uid).get().addOnSuccessListener(doc -> {
            name.setText(doc.getString("name"));
            phone.setText(doc.getString("phoneNumber"));
            university.setText(doc.getString("university"));
            college.setText(doc.getString("college"));
            course.setText(doc.getString("course"));
        });

        confirm.setOnClickListener(v -> {

            String newName = name.getText().toString().trim();
            String newPhone = phone.getText().toString().trim();
            String newUni = university.getText().toString().trim();
            String newColl = college.getText().toString().trim();
            String newCourse = course.getText().toString().trim();

            Map<String, Object> updates = new java.util.HashMap<>();

            if (!newName.isEmpty()) {
                updates.put("name", newName);
            }

            if (!newPhone.isEmpty()) {
                updates.put("phoneNumber", newPhone);
            }

            if (!newUni.isEmpty()) {
                updates.put("university", newUni);
            }

            if (!newColl.isEmpty()) {
                updates.put("college", newColl);
            }

            if (!newCourse.isEmpty()) {
                updates.put("course", newCourse);
            }

            if (updates.isEmpty()) {
                Toast.makeText(getContext(), "No changes made", Toast.LENGTH_SHORT).show();
                return;
            }

            binding.Progress.setVisibility(View.VISIBLE);

            db.collection("users")
                    .document(uid)
                    .update(updates)
                    .addOnSuccessListener(unused -> {
                        binding.Progress.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        if(listener != null){
                            listener.onProfileUpdated();
                        }
                        dismiss();
                    })
                    .addOnFailureListener(e -> {
                        binding.Progress.setVisibility(View.GONE);
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
        cancel.setOnClickListener(v->{
            dismiss();
        });
        return binding.getRoot();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(d -> {

            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) d;

            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);

            if (bottomSheet != null) {
                bottomSheet.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.custom_btm));
                bottomSheet.setClipToOutline(true);
            }
        });

        return dialog;
    }

}
