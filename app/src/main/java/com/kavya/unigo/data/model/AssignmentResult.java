package com.kavya.unigo.data.model;

public class AssignmentResult {
    public static class AssignSuccess extends AssignmentResult{}
    public static class AssignError extends AssignmentResult{
        public String message;
        public AssignError(String mess){
            this.message = mess;
        }
    }
}
