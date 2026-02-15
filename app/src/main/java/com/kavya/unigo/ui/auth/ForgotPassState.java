package com.kavya.unigo.ui.auth;

public class ForgotPassState {
    public static class Success extends ForgotPassState{}

    public static class Error extends ForgotPassState{
        public String mess;
        public Error(String message){
            this.mess = message;
        }
    }

    public static class Loading extends ForgotPassState{}

}
