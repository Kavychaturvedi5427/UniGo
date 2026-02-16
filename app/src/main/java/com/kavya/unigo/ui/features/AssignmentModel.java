package com.kavya.unigo.ui.features;

public class AssignmentModel {

    private String id;
    private String title;
    private String subject;
    private String description;
    private long dueDate;
    private long createdAt;
    private boolean isComplete;

    public AssignmentModel() {
        // Required empty constructor for Firestore
    }

    public String getTitle() {
        return title;
    }

    public String getSubject() {
        return subject;
    }

    public String getDescription() {
        return description;
    }

    public long getDueDate() {
        return dueDate;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setId(String i) {
        id = i;
    }

    public String getId() {
        return id;
    }
}
