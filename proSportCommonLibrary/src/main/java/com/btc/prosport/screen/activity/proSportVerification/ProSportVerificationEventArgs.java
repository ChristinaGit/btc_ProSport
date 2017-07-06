package com.btc.prosport.screen.activity.proSportVerification;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.event.eventArgs.EventArgs;

@Accessors(prefix = "_")
public class ProSportVerificationEventArgs extends EventArgs {
    /*package-private*/ ProSportVerificationEventArgs(
        @Nullable final String phoneNumber) {
        _phoneNumber = phoneNumber;
    }

    @Getter
    @Nullable
    private final String _phoneNumber;
}
