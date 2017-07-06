package com.btc.prosport.api.request;

import lombok.Getter;
import lombok.experimental.Accessors;

import com.google.gson.annotations.SerializedName;

@Accessors(prefix = "_")
public final class ChangeUserCityParams {
    public ChangeUserCityParams(final String id) {
        _id = id;
    }

    @Getter
    @SerializedName("city")
    private final String _id;
}
