package com.kavya.unigo.data.model;

public abstract class SignUpRes {

    public static class SignUpSucc extends SignUpRes{}
    public static class SignUpError extends SignUpRes{
        public final String mess;
        public SignUpError(String message){
            this.mess = message;
        }
    }
}
