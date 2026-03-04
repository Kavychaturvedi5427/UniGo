package com.kavya.unigo.ui.features.Notes;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kavya.unigo.R;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private Context context;
    private List<NotesModel> noteList;

    public NotesAdapter(Context context, List<NotesModel> list){
        this.context = context;
        this.noteList = list;
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {

        ImageView noteImage;
        TextView noteTitle, noteDescription, noteTime;
        CheckBox completedCheck;
        CardView notesCard;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            noteImage = itemView.findViewById(R.id.noteImage);
            noteTitle = itemView.findViewById(R.id.noteTitle);
            noteDescription = itemView.findViewById(R.id.noteDescription);
            noteTime = itemView.findViewById(R.id.noteTime);
            completedCheck = itemView.findViewById(R.id.completedCheck);
            notesCard = itemView.findViewById(R.id.notesCard);
        }
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.notes_model, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {

        NotesModel notes = noteList.get(position);

        holder.noteTitle.setText(notes.getTitle());
        holder.noteDescription.setText(notes.getDescription());

        if (notes.getTimestamp() != null) {
            holder.noteTime.setText(
                    DateFormat.format(
                            "dd MMM yyyy • hh:mm a",
                            notes.getTimestamp().toDate()
                    )
            );
        } else {
            holder.noteTime.setText("");
        }

        if (notes.getImageUrl() != null && !notes.getImageUrl().isEmpty()) {
            holder.noteImage.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(notes.getImageUrl())
                    .centerCrop()
                    .into(holder.noteImage);
        } else {
            holder.noteImage.setVisibility(View.GONE);
        }

        // ✅ DELETE LOGIC (FIXED PATH)
        holder.completedCheck.setOnCheckedChangeListener(null);
        holder.completedCheck.setChecked(false);

        holder.completedCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {

                new AlertDialog.Builder(context)
                        .setTitle("Delete Note")
                        .setMessage("Are you sure you want to delete this note?")
                        .setPositiveButton("Delete", (dialog, which) -> {

                            int currentPosition = holder.getAdapterPosition();

                            if (currentPosition != RecyclerView.NO_POSITION) {

                                String uid = FirebaseAuth.getInstance().getUid();

                                FirebaseFirestore.getInstance()
                                        .collection("users")
                                        .document(uid)
                                        .collection("notes")
                                        .document(noteList.get(currentPosition).getId())
                                        .delete();

                                noteList.remove(currentPosition);
                                notifyItemRemoved(currentPosition);
                            }

                        })
                        .setNegativeButton("Cancel", (dialog, which) ->
                                holder.completedCheck.setChecked(false)
                        )
                        .show();
            }
        });

        holder.notesCard.setOnClickListener(v->{
            Intent intent = new Intent(context, FullImageActivity.class);
            intent.putExtra("imageUrl",notes.getImageUrl());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

}