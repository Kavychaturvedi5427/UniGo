package com.kavya.unigo.ui.features.Notes;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.kavya.unigo.R;

import java.util.ArrayList;
import java.util.List;

public class NotesRecycler extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotesAdapter adapter;
    private List<NotesModel> noteList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String uid;
    private FloatingActionButton addnotesFab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notes_recycler_view);

        recyclerView = findViewById(R.id.notesRecycler);
        addnotesFab = findViewById(R.id.addNotesFab);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        noteList = new ArrayList<>();
        adapter = new NotesAdapter(this, noteList);
        recyclerView.setAdapter(adapter);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        loadNotes();

        addnotesFab.setOnClickListener(v->{
            Notes notes = new Notes();
            notes.show(getSupportFragmentManager(),"AddNotesBtm");
        });
    }

    private void loadNotes() {

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "User doesn't exist", Toast.LENGTH_SHORT).show();
            return;
        }

        uid = auth.getCurrentUser().getUid();

        // fetching the content of notes collection from Firebase...
        db.collection("users")
                .document(uid)
                .collection("notes")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {

                    if (error != null) {
                        Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value == null) return;

                    noteList.clear();

                    // Traversing each dc in the notes collection and storing in the list...
                    for (QueryDocumentSnapshot document : value) {
                        NotesModel note = document.toObject(NotesModel.class);
                        note.setId(document.getId());
                        noteList.add(note);
                    }

                    adapter.notifyDataSetChanged();

                    // incase no notes are present change the visibility of the message....
                    if (noteList.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        findViewById(R.id.state).setVisibility(View.VISIBLE);
                        findViewById(R.id.Lottie2).setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        findViewById(R.id.state).setVisibility(View.GONE);
                        findViewById(R.id.Lottie2).setVisibility(View.GONE);
                    }
                });
    }
}