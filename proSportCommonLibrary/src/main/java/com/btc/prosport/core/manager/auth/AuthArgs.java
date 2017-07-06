package com.btc.prosport.core.manager.auth;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(prefix = "_")
/*package-private*/ class AuthArgs {
    /*package-private*/ AuthArgs(
        @Nullable final String phone, @Nullable final String password) {
        _phone = phone;
        _password = password;
    }

    @Getter
    @Nullable
    private final String _password;

    @Getter
    @Nullable
    private final String _phone;
}
