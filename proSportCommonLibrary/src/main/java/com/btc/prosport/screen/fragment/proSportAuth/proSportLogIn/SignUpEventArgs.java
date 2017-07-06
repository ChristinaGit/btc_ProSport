package com.btc.prosport.screen.fragment.proSportAuth.proSportLogIn;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.event.eventArgs.EventArgs;

@Accessors(prefix = "_")
public class SignUpEventArgs extends EventArgs {
    /*package-private*/ SignUpEventArgs(
        @Nullable final String phoneNumber) {
        _phoneNumber = phoneNumber;
    }

    @Getter
    @Nullable
    private final String _phoneNumber;
}
