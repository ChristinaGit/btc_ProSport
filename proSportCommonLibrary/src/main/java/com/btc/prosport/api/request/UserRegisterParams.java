package com.btc.prosport.api.request;

import android.support.annotation.NonNull;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.contract.Contracts;
import com.google.gson.annotations.SerializedName;

@Accessors(prefix = "_")
public final class UserRegisterParams {
    public UserRegisterParams(@NonNull final String password, @NonNull final String phoneNumber) {
        Contracts.requireNonNull(password, "password == null");
        Contracts.requireNonNull(phoneNumber, "phoneNumber == null");

        _password = password;
        _phoneNumber = phoneNumber;
    }

    @Getter
    @SerializedName("password")
    @NonNull
    private final String _password;

    @Getter
    @SerializedName("phone")
    @NonNull
    private final String _phoneNumber;
}
