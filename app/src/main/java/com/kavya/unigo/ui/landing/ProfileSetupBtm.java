package com.kavya.unigo.ui.landing;

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
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.kavya.unigo.R;

import java.util.HashMap;
import java.util.Map;

public class ProfileSetupBtm extends BottomSheetDialogFragment {

    TextInputEditText phone, university, college, course;
    AppCompatButton confirm, cancel;
    ProgressBar progressBar;
    FirebaseAuth auth;
    FirebaseFirestore db;
    String uid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.profile_infobtm, container, false);

        // binding view groups...
        phone = view.findViewById(R.id.phoneTxt);
        university = view.findViewById(R.id.uniTxt);
        college = view.findViewById(R.id.collegeTxt);
        course = view.findViewById(R.id.courseTxt);
        progressBar = view.findViewById(R.id.Progress);
        confirm = view.findViewById(R.id.confirmbtn);
        cancel = view.findViewById(R.id.cancelBtn);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // get userid...
        if (auth.getCurrentUser() != null) {
            uid = auth.getCurrentUser().getUid();
        }

        confirm.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);

            if (uid == null) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
                return;
            }

            String ph = phone.getText() == null ? "" : phone.getText().toString().trim();
            String uni = university.getText() == null ? "" : university.getText().toString().trim();
            String coll = college.getText() == null ? "" : college.getText().toString().trim();
            String cour = course.getText() == null ? "" : course.getText().toString().trim();

            if (ph.isEmpty() || uni.isEmpty() || coll.isEmpty() || cour.isEmpty()) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            // creating a map that holds all the new info....
            Map<String, Object> userinfo = new HashMap<>();
            userinfo.put("phoneNumber", ph);
            userinfo.put("university", uni);
            userinfo.put("college", coll);
            userinfo.put("course", cour);
            userinfo.put("profileComplete", true);

            // merging the new info with the older one....
            db.collection("users")
                    .document(uid)
                    .set(userinfo, SetOptions.merge())
                    .addOnSuccessListener(unused -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Profile saved", Toast.LENGTH_SHORT).show();
                        dismiss();
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });

        cancel.setOnClickListener(v -> dismiss());

        return view;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(d -> {
            FrameLayout bottomSheet =
                    dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);

            if (bottomSheet != null) {
                bottomSheet.setBackground(
                        ContextCompat.getDrawable(requireContext(), R.drawable.custom_btm)
                );
                bottomSheet.setClipToOutline(true);
            }
        });
        return dialog;
    }
}
