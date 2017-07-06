package com.btc.prosport.api.request;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.google.gson.annotations.SerializedName;

@Accessors(prefix = "_")
public final class ChangeUserFirstNameParams {
    public ChangeUserFirstNameParams(@Nullable final String firstName) {
        _firstName = firstName;
    }

    @Getter
    @SerializedName("first_name")
    @Nullable
    private final String _firstName;
}
