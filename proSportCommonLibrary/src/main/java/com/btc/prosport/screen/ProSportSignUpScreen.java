package com.btc.prosport.screen;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.btc.common.event.generic.Event;
import com.btc.common.event.notice.NoticeEvent;
import com.btc.prosport.screen.fragment.proSportAuth.proSportSignUp.SignUpEventArgs;

public interface ProSportSignUpScreen extends BaseProSportAuthScreen {
    void displayPhoneError(@StringRes int errorMessage);

    void displayPhoneError(@Nullable String errorMessage);

    void displayRetryPasswordError(@StringRes int errorMessage);

    void displayRetryPasswordError(@Nullable String errorMessage);

    @NonNull
    NoticeEvent getAdditionalInfoEvent();

    @NonNull
    Event<SignUpEventArgs> getSignUpEvent();
}
