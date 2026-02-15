package com.kavya.unigo.ui.features;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kavya.unigo.databinding.AssignmentBinding;

import java.util.ArrayList;
import java.util.List;

public class Assignment extends AppCompatActivity {

    private AssignmentBinding binding;
    private FloatingActionButton assignmentFab;

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private AssignmentAdapter adapter;
    private List<AssignmentModel> assignmentList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = AssignmentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        assignmentFab = binding.addAssignmentFab;

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        //  Setup RecyclerView
        adapter = new AssignmentAdapter();
        binding.assignmentRecycler.setLayoutManager(new LinearLayoutManager(this));
        binding.assignmentRecycler.setAdapter(adapter);

        //  FAB click
        assignmentFab.setOnClickListener(view -> {
            AddAssignmentBTM btm = new AddAssignmentBTM();
            btm.show(getSupportFragmentManager(), "AddAssignmentBTM");
        });

        // Load assignments
        fetchAssignments();
    }

    private void fetchAssignments() {

        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        db.collection("users")
                .document(user.getUid())
                .collection("assignments")
                .orderBy("dueDate")
                .addSnapshotListener((value, error) -> {

                    if (error != null || value == null) {
                        Toast.makeText(this,
                                "Error fetching assignments",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    assignmentList.clear();

                    for (DocumentSnapshot doc : value.getDocuments()) {

                        AssignmentModel assignment =
                                doc.toObject(AssignmentModel.class);

                        if (assignment != null) {
                            assignmentList.add(assignment);
                        }
                    }

                    adapter.setData(assignmentList);
                });
    }
}
