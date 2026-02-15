package com.kavya.unigo.data.model;

public abstract class LoginResult {

    // Firebase login succeeded
    public static class Success extends LoginResult {}

    // Firebase login failed
    public static class Error extends LoginResult {
        public String message;

        public Error(String message) {
            this.message = message;
        }
    }
}
