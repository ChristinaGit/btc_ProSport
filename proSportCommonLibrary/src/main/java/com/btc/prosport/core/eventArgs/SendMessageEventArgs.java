package com.btc.prosport.core.eventArgs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.event.eventArgs.EventArgs;

@Accessors(prefix = "_")
public class SendMessageEventArgs extends EventArgs {

    public SendMessageEventArgs(
        @Nullable final String email, @Nullable final String name, @NonNull final String message) {
        _email = email;
        _message = message;
        _name = name;
    }

    @Nullable
    @Getter
    private final String _email;

    @Nullable
    @Getter
    private final String _message;

    @NonNull
    @Getter
    private final String _name;
}
