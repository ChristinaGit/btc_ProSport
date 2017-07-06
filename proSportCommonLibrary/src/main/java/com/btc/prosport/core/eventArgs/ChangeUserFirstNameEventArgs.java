package com.btc.prosport.core.eventArgs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.event.eventArgs.EventArgs;

@Accessors(prefix = "_")
public class ChangeUserFirstNameEventArgs extends EventArgs {

    public ChangeUserFirstNameEventArgs(
        @NonNull final String newFirstName, @Nullable final String oldFirstName) {
        _newFirstName = newFirstName;
        _oldFirstName = oldFirstName;
    }

    @NonNull
    @Getter
    private final String _newFirstName;

    @Nullable
    @Getter
    private final String _oldFirstName;
}
