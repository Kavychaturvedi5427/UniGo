package com.kavya.unigo.data.model;

public class ForgotRes {
    public static class success extends ForgotRes {
    }

    public static class error extends ForgotRes {
        public String message;

        public error(String mess) {
            this.message = mess;
        }
    }
}
