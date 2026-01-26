package com.kavya.unigo.ui.auth;

public class LoginState {
    public static class Loading extends LoginState {
    }

    public static class Error extends LoginState {
        public final String message;

        public Error(String mess) {
            this.message = mess;
        }
    }

    public static class Success extends LoginState {
    }


}
