package com.btc.prosport.core.manager.auth;

import android.support.annotation.NonNull;

import rx.Observable;

public interface AuthManager {
    @NonNull
    Observable<AuthResult> logIn(@NonNull LogInArgs logInArgs);

    @NonNull
    Observable<AuthResult> reLogIn(@NonNull LogInArgs logInArgs);

    @NonNull
    Observable<AuthResult> signUp(@NonNull SignUpArgs signUpArgs);
}
