package com.btc.prosport.screen;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.btc.common.event.generic.Event;
import com.btc.prosport.screen.fragment.proSportAuth.proSportLogIn.LogInEventArgs;
import com.btc.prosport.screen.fragment.proSportAuth.proSportLogIn.SignUpEventArgs;

public interface ProSportLogInScreen extends BaseProSportAuthScreen {
    void displayPhoneError(@StringRes int errorMessage);

    void displayPhoneError(@Nullable String errorMessage);

    @NonNull
    Event<LogInEventArgs> getLogInEvent();

    @NonNull
    Event<SignUpEventArgs> getSignUpEvent();
}
