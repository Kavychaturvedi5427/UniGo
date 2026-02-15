package com.kavya.unigo.ui.auth;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kavya.unigo.data.model.ForgotRes;
import com.kavya.unigo.data.repository.AuthRepository;

public class ResetViewModel extends ViewModel {
    private final AuthRepository authRepository;

    private MutableLiveData<ForgotPassState> forgotState = new MutableLiveData<>();
    public LiveData<ForgotPassState> getForgotState(){
        return forgotState;
    }

    public ResetViewModel(){
        this.authRepository = new AuthRepository();
    }

    public void handleReset(String em) {
        // validating the email..

        if(em.isEmpty()){
            forgotState.setValue(new ForgotPassState.Error("Email field can't be left empty."));
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(em).matches()){
            forgotState.setValue(new ForgotPassState.Error("Enter valid email."));
            return;
        }

        forgotState.setValue(new ForgotPassState.Loading());



        authRepository.passReset(em,result->{
            if(result instanceof ForgotRes.success){
                forgotState.setValue(new ForgotPassState.Success());
            }
            else{
                // the reason we're typecasting it to the ForgotRes.error type is because when firebase responds with ForgotRes result type....
                ForgotRes.error error = (ForgotRes.error)result;
                Log.e("Firebase Failed Error", error.message);
                forgotState.setValue(new ForgotPassState.Error("Unable to reset password"));
            }
        });

    }
    public void clearState() {
        forgotState.setValue(null);
    }


}
