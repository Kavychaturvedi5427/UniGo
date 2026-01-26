package com.kavya.unigo.ui.auth;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kavya.unigo.data.model.SignUpRes;
import com.kavya.unigo.data.repository.AuthRepository;

public class SignUpViewModel extends ViewModel {

    private final MutableLiveData<SignUpState> SignUpState = new MutableLiveData<>();
    public LiveData<SignUpState> getSignUpState(){
        return SignUpState;
    }
    private AuthRepository repo;
    public SignUpViewModel(){
        this.repo = new AuthRepository();   // linking ViewModel with the repository....
    }

    public void singup(String name, String em, String ps, String cps){
        if(name.isEmpty() || em.isEmpty() || ps.isEmpty() || cps.isEmpty()){
            SignUpState.setValue(new SignUpState.SignUpError("You can't leave any feild empty."));
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(em).matches()){
            SignUpState.setValue(new SignUpState.SignUpError("Enter Valid Email."));
            return;
        }
        if(!(ps.length() >= 5) || (!ps.equals(cps))){
            SignUpState.setValue(new SignUpState.SignUpError("Invalid Password, Make sure that password at least have 5 characters."));
            return;
        }

        SignUpState.setValue(new SignUpState.SignUpLoading());

        // handle the response from the repository and pass that response to the ViewModel state which will be observed by the UI...
        repo.signup(name,em,ps,result->{        // call back function
            if(result instanceof SignUpRes.SignUpSucc){
                SignUpState.postValue(new SignUpState.SingupSuccess());
            }
            else{
                SignUpState.postValue(new SignUpState.SignUpError(((SignUpRes.SignUpError) result).mess));
            }
        });


    }

}
