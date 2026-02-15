package com.kavya.unigo.ui.features;

public class AssignmentState {
    public static class AssignmentSuccess extends AssignmentState{}
    public static class AssignmentError extends AssignmentState{
        public String message;
        public AssignmentError(String mess){
            this.message = mess;
        }
    }
    public static class AssignmentLoading extends AssignmentState {}
}
