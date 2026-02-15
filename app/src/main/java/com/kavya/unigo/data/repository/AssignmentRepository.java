package com.kavya.unigo.data.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kavya.unigo.data.model.AssignmentResult;

import java.util.HashMap;
import java.util.Map;

public class AssignmentRepository {

    public interface AssignCallback {
        void onResult(AssignmentResult result);
    }

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void StoreAssign(String title, String subj, String desc, long duedateMillis, AssignCallback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            callback.onResult(new AssignmentResult.AssignError("User doesn't exist"));
            return;
        }
        String uid = user.getUid();
        Map<String, Object> assign = new HashMap<>();
        assign.put("title", title);
        assign.put("subject", subj);
        assign.put("description", desc);
        assign.put("dueDate", duedateMillis);
        assign.put("createdAt", System.currentTimeMillis());
        // when user exist then...
        db.collection("users").document(uid).collection("assignments").add(assign).addOnSuccessListener(unused -> {
            callback.onResult(new AssignmentResult.AssignSuccess());
        }).addOnFailureListener(e -> {
            callback.onResult(new AssignmentResult.AssignError(e.getMessage()));
        });

    }
}
