package com.kavya.unigo.ui.auth;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kavya.unigo.data.repository.AuthRepository;
import com.kavya.unigo.data.model.LoginResult;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<LoginState> loginState = new MutableLiveData<>();

    public LiveData<LoginState> getLoginState() {
        return loginState;
    }

    // data layer....
    private final AuthRepository authRepository;

    public LoginViewModel() {
        this.authRepository = new AuthRepository();
    }

    // validating the values....
    public void login(String em, String ps) {
        if (em.isEmpty() || ps.isEmpty()) {
            // set the ui state to error......
            loginState.setValue(new LoginState.Error("Can't leave the fields empty."));
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(em).matches()) {
            loginState.setValue(new LoginState.Error("Invalid Email."));
            return;
        }
        loginState.setValue(new LoginState.Loading());

        // proceed further for authentication with firebase...
        authRepository.login(em, ps, result -> {        // result is the callback lambda function.....
            if (result instanceof LoginResult.Success) {
                loginState.postValue(new LoginState.Success());
            } else {
                loginState.postValue(
                        new LoginState.Error(
                                ((LoginResult.Error) result).message
                        )
                );
            }

        });
    }
}


