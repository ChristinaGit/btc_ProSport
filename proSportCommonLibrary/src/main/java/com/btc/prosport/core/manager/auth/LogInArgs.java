package com.btc.prosport.core.manager.auth;

import android.support.annotation.Nullable;

import lombok.experimental.Accessors;

@Accessors(prefix = "_")
public class LogInArgs extends AuthArgs {
    public LogInArgs(@Nullable final String phone, @Nullable final String password) {
        super(phone, password);
    }
}
