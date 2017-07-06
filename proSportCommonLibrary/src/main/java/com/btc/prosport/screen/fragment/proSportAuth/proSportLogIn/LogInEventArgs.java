package com.btc.prosport.screen.fragment.proSportAuth.proSportLogIn;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.event.eventArgs.EventArgs;

@Accessors(prefix = "_")
public class LogInEventArgs extends EventArgs {
    /*package-private*/ LogInEventArgs(
        @Nullable final String phone, @Nullable final String password) {
        _phone = phone;
        _password = password;
    }

    @Getter
    @Nullable
    private final String _password;

    @Getter
    @Nullable
    private final String _phone;
}
