package com.btc.prosport.screen.fragment.proSportAuth.proSportReLogIn;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.event.eventArgs.EventArgs;

@Accessors(prefix = "_")
public class ReLogInEventArgs extends EventArgs {
    /*package-private*/ ReLogInEventArgs(
        @NonNull final String phone, @Nullable final String password) {
        _phone = phone;
        _password = password;
    }

    @Getter
    @Nullable
    private final String _password;

    @Getter
    @NonNull
    private final String _phone;
}
