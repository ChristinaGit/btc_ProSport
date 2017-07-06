package com.btc.prosport.screen.activity.proSportVerification;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(prefix = "_")
public class ProSportLoginEventArgs extends ProSportVerificationEventArgs {
    /*package-private*/ ProSportLoginEventArgs(
        @Nullable final String phoneNumber, final boolean allowRegistration) {
        super(phoneNumber);

        _allowRegistration = allowRegistration;
    }

    @Getter
    private final boolean _allowRegistration;
}
