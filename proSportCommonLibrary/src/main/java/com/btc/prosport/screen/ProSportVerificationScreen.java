package com.btc.prosport.screen;

import android.support.annotation.NonNull;

import com.btc.common.event.generic.Event;
import com.btc.common.mvp.screen.Screen;
import com.btc.prosport.screen.activity.proSportVerification.ProSportLoginEventArgs;
import com.btc.prosport.screen.activity.proSportVerification.ProSportVerificationEventArgs;

public interface ProSportVerificationScreen extends Screen {
    @NonNull
    Event<ProSportLoginEventArgs> getDisplayLogInScreenEvent();

    @NonNull
    Event<ProSportVerificationEventArgs> getDisplayReLogInScreenEvent();

    @NonNull
    Event<ProSportVerificationEventArgs> getDisplaySignUpScreenEvent();
}
