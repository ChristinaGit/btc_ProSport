package com.btc.prosport.core.eventArgs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.event.eventArgs.EventArgs;

@Accessors(prefix = "_")
public class ChangeUserPhoneNumberEventArgs extends EventArgs {

    public ChangeUserPhoneNumberEventArgs(
        @NonNull final String newPhoneNumber, @Nullable final String oldPhoneNumber) {
        _newPhoneNumber = newPhoneNumber;
        _oldPhoneNumber = oldPhoneNumber;
    }

    @NonNull
    @Getter
    private final String _newPhoneNumber;

    @Nullable
    @Getter
    private final String _oldPhoneNumber;
}
