package com.btc.prosport.api.request;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.google.gson.annotations.SerializedName;

@Accessors(prefix = "_")
public final class ChangeUserLastNameParams {
    public ChangeUserLastNameParams(@Nullable final String lastName) {
        _lastName = lastName;
    }

    @Getter
    @SerializedName("last_name")
    @Nullable
    private final String _lastName;
}
