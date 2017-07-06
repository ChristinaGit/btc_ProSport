package com.btc.prosport.core.manager.auth;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(prefix = "_")
public class SignUpArgs extends AuthArgs {
    public SignUpArgs(
        @Nullable final String phone,
        @Nullable final String password,
        @Nullable final String retryPassword) {
        super(phone, password);

        _retryPassword = retryPassword;
    }

    @Getter
    @Nullable
    private final String _retryPassword;
}
