package com.btc.prosport.api.request;

import android.support.annotation.NonNull;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.btc.common.contract.Contracts;
import com.google.gson.annotations.SerializedName;

@Accessors(prefix = "_")
public final class ChangeUserPhoneNumberParams {
    public ChangeUserPhoneNumberParams(@NonNull final String phone) {
        Contracts.requireNonNull(phone, "phone == null");

        _phone = phone;
    }

    @Getter
    @SerializedName("phone")
    @NonNull
    private final String _phone;
}
