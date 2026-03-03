package com.kavya.unigo.ui.features.Notes;

public abstract class NotesState {

    private NotesState() {}

    public static final class Loading extends NotesState { }

    public static final class Success extends NotesState {
        public final String downloadUrl;

        public Success(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }
    }

    public static final class Error extends NotesState {
        public final String message;

        public Error(String message) {
            this.message = message;
        }
    }
}