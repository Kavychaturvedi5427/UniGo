package com.kavya.unigo.ui.features.Notes;

import com.google.firebase.Timestamp;

public class NotesModel {

    private String id;   // Firestore document ID
    private String title;
    private String description;
    private String imageUrl;
    private Timestamp timestamp;

    public NotesModel() {}

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public Timestamp getTimestamp() { return timestamp; }

    public void setId(String id) {
        this.id = id;
    }
}