package com.btc.prosport.core.eventArgs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.event.eventArgs.EventArgs;

@Accessors(prefix = "_")
public class ChangeUserLastNameEventArgs extends EventArgs {

    public ChangeUserLastNameEventArgs(
        @NonNull final String newLastName, @Nullable final String oldLastName) {
        _newLastName = newLastName;
        _oldLastName = oldLastName;
    }

    @NonNull
    @Getter
    private final String _newLastName;

    @Nullable
    @Getter
    private final String _oldLastName;
}
