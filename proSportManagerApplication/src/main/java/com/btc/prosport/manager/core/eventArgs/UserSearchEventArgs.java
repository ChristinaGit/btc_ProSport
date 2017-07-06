package com.btc.prosport.manager.core.eventArgs;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.event.eventArgs.EventArgs;

@Accessors(prefix = "_")
public class UserSearchEventArgs extends EventArgs {
    public UserSearchEventArgs(@Nullable final String phoneNumber) {
        _phoneNumber = phoneNumber;
    }

    @Getter
    @Nullable
    private final String _phoneNumber;
}
