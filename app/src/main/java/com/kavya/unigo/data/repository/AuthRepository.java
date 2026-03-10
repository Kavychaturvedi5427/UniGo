package com.kavya.unigo.data.repository;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kavya.unigo.data.model.ForgotRes;
import com.kavya.unigo.data.model.LoginResult;
import com.kavya.unigo.data.model.SignUpRes;

import java.util.HashMap;
import java.util.Map;

public class AuthRepository {
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface LoginCallback {
        void onResult(LoginResult result);    // this will hold the LoginResult from the Firebase....
    }

    public interface SignUpCallback {
        void onResult(SignUpRes result);
    }

    public interface resetCallback{
        void onResult(ForgotRes result);
    }

    public void login(String em, String pass, LoginCallback callback) {
        auth.signInWithEmailAndPassword(em, pass).addOnSuccessListener(authResult -> {
            FirebaseUser user = auth.getCurrentUser();
            if (user == null || !user.isEmailVerified()) {
                auth.signOut();
                callback.onResult(new LoginResult.Error("Please verify your email."));      // this will set the error state....
                return;
            }
            callback.onResult(new LoginResult.Success());
            ;
        }).addOnFailureListener(e -> {
            callback.onResult(new LoginResult.Error(e.getMessage() != null ? e.getMessage() : "Login Failed"));
        });
    }

    public void signup(String name, String em, String ps, SignUpCallback callback) {
        auth.createUserWithEmailAndPassword(em, ps).addOnSuccessListener(authResult -> {
            FirebaseUser user = auth.getCurrentUser();
            if (user == null) {
                callback.onResult(new SignUpRes.SignUpError("User can't be created."));
                return;
            }
            String uid = user.getUid();
            Map<String, Object> data = new HashMap<>();
            data.put("name", name);
            data.put("email", em);
            data.put("createdAt", com.google.firebase.firestore.FieldValue.serverTimestamp());
            data.put("isProfileComplete", false);

            db.collection("users").document(uid).set(data).addOnSuccessListener(unused -> {
                if (!user.isEmailVerified()) {
                    user.sendEmailVerification().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            auth.signOut();
                            callback.onResult(new SignUpRes.SignUpSucc());
                        } else {
                            callback.onResult(
                                    new SignUpRes.SignUpError("Failed to send verification email")
                            );
                        }
                    });
                } else {
                    auth.signOut();
                    callback.onResult(new SignUpRes.SignUpSucc());
                }
            }).addOnFailureListener(e -> {
                callback.onResult(new SignUpRes.SignUpError("Account created but failed to save profile."));
                return;
            });
        }).addOnFailureListener(e -> {
            callback.onResult(new SignUpRes.SignUpError(e.getMessage()));
        });
    }

    public void passReset(String em,resetCallback callback) {
        auth.sendPasswordResetEmail(em).addOnSuccessListener(unused -> {
            callback.onResult(new ForgotRes.success());
        }).addOnFailureListener(e->{
            callback.onResult(new ForgotRes.error(e.getMessage() != null ? e.getMessage() : " "));
        });
    }


}
