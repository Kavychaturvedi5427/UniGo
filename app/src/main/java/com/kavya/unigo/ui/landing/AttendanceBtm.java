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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.kavya.unigo.R;

import java.util.HashMap;
import java.util.Map;

public class AttendanceBtm extends BottomSheetDialogFragment {

    TextInputEditText Attended, Total;
    AppCompatButton save, cancel;
    ProgressBar progressBar;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String uid;
    private AttendanceUpdateListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.attendance_btm, container, false);

        if (auth.getCurrentUser() != null) {
            uid = auth.getCurrentUser().getUid();
        }

        // binding view groups...
        Attended = view.findViewById(R.id.attendedtxt);
        Total = view.findViewById(R.id.totallectxt);
        save = view.findViewById(R.id.savebtn);
        cancel = view.findViewById(R.id.cancelbtn);
        progressBar = view.findViewById(R.id.Progress);

        save.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);

            // fetching the values from the view groups....
            String attendedStr = Attended.getText() != null
                    ? Attended.getText().toString().trim() : "";
            String totalStr = Total.getText() != null
                    ? Total.getText().toString().trim() : "";

            if (attendedStr.isEmpty() || totalStr.isEmpty()) {
                Attended.setError("Required");
                Total.setError("Required");
                return;
            }
            // typecasting to int...
            int attendedToday = Integer.parseInt(attendedStr);
            int totalToday = Integer.parseInt(totalStr);

            // validating
            if (attendedToday > totalToday || totalToday <= 0) {
                Total.setError("Invalid values");
                return;
            }

            // storing the attendance in the db...
            db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
                long oldAttended = 0, oldTotal = 0;

                // checking if the attendance field exit or not ....
                if (documentSnapshot.exists() && documentSnapshot.contains("attendance")) {
                    // if exist then get the value which is also a map so type cast it ....
                    Map<String, Object> attendance = (Map<String, Object>) documentSnapshot.get("attendance");
                    if (attendance != null) {
                        // within the map fetching the attended val and total val...
                        oldAttended = attendance.get("attended") != null ? (long) attendance.get("attended") : 0;
                        oldTotal = attendance.get("total") != null ? (long) attendance.get("total") : 0;
                    }
                }

                long newAttended = oldAttended + attendedToday;
                long newtotal = oldTotal + totalToday;

                // updating the old attendance with the new one ....
                Map<String, Object> attendanceMap = new HashMap<>();
                attendanceMap.put("attended", newAttended);
                attendanceMap.put("total", newtotal);
                attendanceMap.put("updatedAt", FieldValue.serverTimestamp());

                Map<String, Object> update = new HashMap<>();
                update.put("attendance", attendanceMap);

                db.collection("users").document(uid).set(update, SetOptions.merge()).addOnSuccessListener(unused -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Attendance stored.", Toast.LENGTH_SHORT).show();
                            if (listener != null) {
                                // now call the onUpadate() method which will load the data.....
                                listener.onUpdate();
                                dismiss();
                            }
                        })
                        .addOnFailureListener(e -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        });

            }).addOnFailureListener(e -> {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        });
        return view;
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

    // this will recieve the reference of the activity which is opening the btm.... and store it in...
    public void SetattendanceUpdateListerenr(AttendanceUpdateListener List) {
        this.listener = List;
    }

}
