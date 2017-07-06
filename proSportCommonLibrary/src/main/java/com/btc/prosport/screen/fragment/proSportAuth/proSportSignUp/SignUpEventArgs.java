package com.btc.prosport.screen.fragment.proSportAuth.proSportSignUp;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.event.eventArgs.EventArgs;

@Accessors(prefix = "_")
public class SignUpEventArgs extends EventArgs {
    /*package-private*/ SignUpEventArgs(
        @Nullable final String phone,
        @Nullable final String password, @Nullable final String retryPassword) {
        _phone = phone;
        _password = password;
        _retryPassword = retryPassword;
    }

    @Getter
    @Nullable
    private final String _password;

    @Getter
    @Nullable
    private final String _phone;

    @Getter
    @Nullable
    private final String _retryPassword;
}
