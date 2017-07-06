package com.btc.prosport.api.response;

import android.support.annotation.Nullable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import com.google.gson.annotations.SerializedName;

@Accessors(prefix = "_")
public final class AuthorizationResponse {
    @Getter
    @Setter
    @SerializedName("access_token")
    @Nullable
    private String _accessToken;

    @Getter
    @Setter
    @SerializedName("expires_in")
    @Nullable
    private String _expiresIn;

    @Getter
    @Setter
    @SerializedName("refresh_token")
    @Nullable
    private String _refreshToken;

    @Getter
    @Setter
    @SerializedName("scope")
    @Nullable
    private String _scope;
}
