package com.btc.prosport.screen.fragment.proSportAuth.proSportAdditionalInfo;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.event.eventArgs.EventArgs;

@Accessors(prefix = "_")
public class AdditionalInfoArgs extends EventArgs {
    /*package-private*/ AdditionalInfoArgs(
        @Nullable final String firstName,
        @Nullable final String lastName,
        @Nullable final String email) {
        _email = email;
        _lastName = lastName;
        _firstName = firstName;
    }

    @Getter
    @Nullable
    private final String _email;

    @Getter
    @Nullable
    private final String _firstName;

    @Getter
    @Nullable
    private final String _lastName;
}
