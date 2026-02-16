package com.kavya.unigo.ui.features;

import static java.util.Locale.*;

import android.app.AlertDialog;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kavya.unigo.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AssignmentAdapter
        extends RecyclerView.Adapter<AssignmentAdapter.ViewHolder> {

    private List<AssignmentModel> list = new ArrayList<>();

    public void setData(List<AssignmentModel> newList) {
        list = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.assignment_model, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        AssignmentModel assignment = list.get(position);

        holder.titleText.setText(assignment.getTitle());
        holder.subjectText.setText(assignment.getSubject());
        holder.descriptionText.setText(assignment.getDescription());
        holder.check.setOnCheckedChangeListener(null);
        holder.check.setChecked(false); // always start unchecked

        holder.check.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (!isChecked) return;

            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Complete Assignment")
                    .setMessage("Mark \"" + assignment.getTitle() + "\" as completed?\nIt will be removed from your assignments.")
                    .setPositiveButton("Yes delete", (dialog, which) -> {

                        FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .collection("assignments")
                                .document(assignment.getId())
                                .delete();
                    })
                    .setNegativeButton("Not now", (dialog, which) -> {
                        holder.check.setChecked(false);
                    })
                    .setCancelable(false)
                    .show();
        });



        Date date = new Date(assignment.getDueDate());
        SimpleDateFormat sdf =
                new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        holder.dateText.setText("Due: " + sdf.format(date));

        if (assignment.getDueDate() < System.currentTimeMillis()) {
            holder.dateText.setTextColor(Color.RED);
        } else {
            holder.dateText.setTextColor(Color.GRAY);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleText, subjectText, descriptionText, dateText;
        CheckBox check ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titleText = itemView.findViewById(R.id.titleTxt);
            subjectText = itemView.findViewById(R.id.subjectTxt);
            descriptionText = itemView.findViewById(R.id.descTxt);
            dateText = itemView.findViewById(R.id.dueDateTxt);
            check = itemView.findViewById(R.id.completedCheck);
        }
    }
}

