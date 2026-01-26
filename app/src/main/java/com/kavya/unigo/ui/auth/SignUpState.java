package com.kavya.unigo.ui.auth;

public class SignUpState {
    public static class SingupSuccess extends SignUpState{}
    public static class SignUpError extends SignUpState{
        public String message;
        public SignUpError(String mess){
            this.message = mess;
        }
    }
    public static class SignUpLoading extends SignUpState{}
}
