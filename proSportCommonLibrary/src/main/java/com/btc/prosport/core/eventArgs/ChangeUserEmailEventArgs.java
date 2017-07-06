package com.btc.prosport.core.eventArgs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.event.eventArgs.EventArgs;

@Accessors(prefix = "_")
public class ChangeUserEmailEventArgs extends EventArgs {

    public ChangeUserEmailEventArgs(
        @NonNull final String newEmail, @Nullable final String oldEmail) {
        _newEmail = newEmail;
        _oldEmail = oldEmail;
    }

    @NonNull
    @Getter
    private final String _newEmail;

    @Nullable
    @Getter
    private final String _oldEmail;
}
