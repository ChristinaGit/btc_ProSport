package com.btc.prosport.core.manager.auth;

import android.support.annotation.NonNull;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.btc.common.contract.Contracts;

@ToString
@Accessors(prefix = "_")
public class AuthResult {
    public AuthResult(
        @NonNull final String phoneNumber, @NonNull final String accountType) {
        Contracts.requireNonNull(phoneNumber, "phoneNumber == null");
        Contracts.requireNonNull(accountType, "accountType == null");

        _phoneNumber = phoneNumber;
        _accountType = accountType;
    }

    @Getter
    @NonNull
    private final String _accountType;

    @Getter
    @NonNull
    private final String _phoneNumber;
}
