package com.btc.prosport.api.request;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.google.gson.annotations.SerializedName;

@Accessors(prefix = "_")
public final class ChangeUserEmailParams {
    public ChangeUserEmailParams(@Nullable final String email) {
        _email = email;
    }

    @Getter
    @SerializedName("email")
    @Nullable
    private final String _email;
}
