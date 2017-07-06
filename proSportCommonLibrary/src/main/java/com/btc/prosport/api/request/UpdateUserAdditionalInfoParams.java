package com.btc.prosport.api.request;

import android.support.annotation.Nullable;

import lombok.Getter;

import com.google.gson.annotations.SerializedName;

public final class UpdateUserAdditionalInfoParams {
    public UpdateUserAdditionalInfoParams(
        @Nullable final String firstName,
        @Nullable final String lastName,
        @Nullable final String email) {
        _email = email;
        _firstName = firstName;
        _lastName = lastName;
    }

    @Getter
    @SerializedName("email")
    @Nullable
    private final String _email;

    @Getter
    @SerializedName("first_name")
    @Nullable
    private final String _firstName;

    @Getter
    @SerializedName("last_name")
    @Nullable
    private final String _lastName;
}
