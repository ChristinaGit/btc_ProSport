package com.btc.prosport.screen;

import android.support.annotation.NonNull;

import com.btc.common.event.generic.Event;
import com.btc.prosport.screen.fragment.proSportAuth.proSportReLogIn.ReLogInEventArgs;

public interface ProSportReLogInScreen extends BaseProSportAuthScreen {
    @NonNull
    Event<ReLogInEventArgs> getReLogInEvent();
}
